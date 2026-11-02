// Supabase Edge Function: admin-api
// Runtime: Deno

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

interface RateLimitEntry {
  failures: number;
  blockedUntil: number;
}

const rateLimitMap = new Map<string, RateLimitEntry>();

function getClientIp(req: Request): string {
  const forwarded = req.headers.get("x-forwarded-for");
  if (forwarded) {
    return forwarded.split(",")[0].trim();
  }
  return req.headers.get("cf-connecting-ip") || "unknown";
}

function isIpBlocked(ip: string): boolean {
  if (ip === "unknown") return false;
  const entry = rateLimitMap.get(ip);
  if (!entry) return false;
  const now = Date.now();
  if (entry.blockedUntil > now) {
    return true;
  }
  if (entry.blockedUntil > 0 && entry.blockedUntil <= now) {
    rateLimitMap.delete(ip);
    return false;
  }
  return false;
}

function recordFailure(ip: string): void {
  if (ip === "unknown") return;
  const now = Date.now();
  const entry = rateLimitMap.get(ip) ?? { failures: 0, blockedUntil: 0 };
  entry.failures += 1;
  if (entry.failures >= 5) {
    entry.blockedUntil = now + 15 * 60 * 1000; // 15-minute block
  }
  rateLimitMap.set(ip, entry);
}

function recordSuccess(ip: string): void {
  if (ip !== "unknown") {
    rateLimitMap.delete(ip);
  }
}

async function sha256Bytes(data: string): Promise<Uint8Array> {
  const encoded = new TextEncoder().encode(data);
  const digest = await crypto.subtle.digest("SHA-256", encoded);
  return new Uint8Array(digest);
}

function constantTimeCompare(a: Uint8Array, b: Uint8Array): boolean {
  if (a.length !== b.length) {
    return false;
  }
  let diff = 0;
  for (let i = 0; i < a.length; i++) {
    diff |= a[i] ^ b[i];
  }
  return diff === 0;
}

function jsonResponse(data: unknown, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      ...corsHeaders,
      "Content-Type": "application/json",
    },
  });
}

serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  if (req.method !== "POST") {
    return jsonResponse({ error: "Method not allowed" }, 405);
  }

  const clientIp = getClientIp(req);
  if (isIpBlocked(clientIp)) {
    return jsonResponse({ error: "Too many failed attempts. Try again in 15 minutes." }, 429);
  }

  const ADMIN_PASSWORD = Deno.env.get("ADMIN_PASSWORD") ?? "";
  const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";
  const SUPABASE_URL = Deno.env.get("SUPABASE_URL") ?? "";

  if (!ADMIN_PASSWORD || !SUPABASE_SERVICE_ROLE_KEY || !SUPABASE_URL) {
    return jsonResponse({ error: "Server misconfiguration" }, 500);
  }

  let body: any;
  try {
    body = await req.json();
  } catch {
    return jsonResponse({ error: "Invalid JSON body" }, 400);
  }

  if (!body || typeof body !== "object") {
    return jsonResponse({ error: "Invalid request format" }, 400);
  }

  const { action, password, payload } = body;
  if (typeof action !== "string" || typeof password !== "string") {
    return jsonResponse({ error: "Missing required fields: action and password" }, 400);
  }

  const submittedHash = await sha256Bytes(password);
  const correctHash = await sha256Bytes(ADMIN_PASSWORD);

  if (!constantTimeCompare(submittedHash, correctHash)) {
    recordFailure(clientIp);
    return jsonResponse({ error: "Invalid password" }, 401);
  }

  recordSuccess(clientIp);

  const dbHeaders = {
    "apikey": SUPABASE_SERVICE_ROLE_KEY,
    "Authorization": `Bearer ${SUPABASE_SERVICE_ROLE_KEY}`,
    "Content-Type": "application/json",
  };

  try {
    switch (action) {
      case "verify": {
        return jsonResponse({ success: true }, 200);
      }

      case "publish_update": {
        if (
          !payload ||
          typeof payload.version !== "string" ||
          typeof payload.title !== "string" ||
          typeof payload.description !== "string" ||
          (payload.update_type !== "optional" && payload.update_type !== "force") ||
          (payload.apk_url !== undefined && payload.apk_url !== null && typeof payload.apk_url !== "string")
        ) {
          return jsonResponse({ error: "Invalid payload for publish_update" }, 400);
        }

        const insertRow = {
          version: payload.version,
          title: payload.title,
          description: payload.description,
          update_type: payload.update_type,
          apk_url: payload.apk_url ?? null,
        };

        const res = await fetch(`${SUPABASE_URL}/rest/v1/app_updates`, {
          method: "POST",
          headers: { ...dbHeaders, "Prefer": "return=minimal" },
          body: JSON.stringify(insertRow),
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        return jsonResponse({ success: true }, 200);
      }

      case "publish_news": {
        if (
          !payload ||
          typeof payload.title !== "string" ||
          typeof payload.content !== "string" ||
          (payload.image_url !== undefined && payload.image_url !== null && typeof payload.image_url !== "string")
        ) {
          return jsonResponse({ error: "Invalid payload for publish_news" }, 400);
        }

        const insertRow = {
          title: payload.title,
          content: payload.content,
          image_url: payload.image_url ?? null,
        };

        const res = await fetch(`${SUPABASE_URL}/rest/v1/news_updates`, {
          method: "POST",
          headers: { ...dbHeaders, "Prefer": "return=minimal" },
          body: JSON.stringify(insertRow),
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        return jsonResponse({ success: true }, 200);
      }

      case "fetch_suggestions": {
        const res = await fetch(`${SUPABASE_URL}/rest/v1/suggestions?order=created_at.desc`, {
          method: "GET",
          headers: dbHeaders,
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        const data = await res.json();
        return jsonResponse(data, 200);
      }

      case "update_suggestion_status": {
        if (!payload || typeof payload.id !== "number" || typeof payload.status !== "string") {
          return jsonResponse({ error: "Invalid payload for update_suggestion_status" }, 400);
        }

        const res = await fetch(`${SUPABASE_URL}/rest/v1/suggestions?id=eq.${payload.id}`, {
          method: "PATCH",
          headers: dbHeaders,
          body: JSON.stringify({ status: payload.status }),
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        return jsonResponse({ success: true }, 200);
      }

      case "update_suggestion_comment": {
        if (
          !payload ||
          typeof payload.id !== "number" ||
          (payload.admin_comment !== undefined && payload.admin_comment !== null && typeof payload.admin_comment !== "string")
        ) {
          return jsonResponse({ error: "Invalid payload for update_suggestion_comment" }, 400);
        }

        const res = await fetch(`${SUPABASE_URL}/rest/v1/suggestions?id=eq.${payload.id}`, {
          method: "PATCH",
          headers: dbHeaders,
          body: JSON.stringify({ admin_comment: payload.admin_comment ?? null }),
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        return jsonResponse({ success: true }, 200);
      }

      case "delete_suggestion": {
        if (!payload || typeof payload.id !== "number") {
          return jsonResponse({ error: "Invalid payload for delete_suggestion" }, 400);
        }

        const res = await fetch(`${SUPABASE_URL}/rest/v1/suggestions?id=eq.${payload.id}`, {
          method: "DELETE",
          headers: dbHeaders,
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        return jsonResponse({ success: true }, 200);
      }

      case "fetch_bug_reports": {
        const res = await fetch(`${SUPABASE_URL}/rest/v1/bug_reports?order=created_at.desc`, {
          method: "GET",
          headers: dbHeaders,
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        const data = await res.json();
        return jsonResponse(data, 200);
      }

      case "update_bug_report_status": {
        if (!payload || typeof payload.id !== "number" || typeof payload.status !== "string") {
          return jsonResponse({ error: "Invalid payload for update_bug_report_status" }, 400);
        }

        const res = await fetch(`${SUPABASE_URL}/rest/v1/bug_reports?id=eq.${payload.id}`, {
          method: "PATCH",
          headers: dbHeaders,
          body: JSON.stringify({ status: payload.status }),
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        return jsonResponse({ success: true }, 200);
      }

      case "update_bug_report_comment": {
        if (
          !payload ||
          typeof payload.id !== "number" ||
          (payload.admin_comment !== undefined && payload.admin_comment !== null && typeof payload.admin_comment !== "string")
        ) {
          return jsonResponse({ error: "Invalid payload for update_bug_report_comment" }, 400);
        }

        const res = await fetch(`${SUPABASE_URL}/rest/v1/bug_reports?id=eq.${payload.id}`, {
          method: "PATCH",
          headers: dbHeaders,
          body: JSON.stringify({ admin_comment: payload.admin_comment ?? null }),
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        return jsonResponse({ success: true }, 200);
      }

      case "delete_bug_report": {
        if (!payload || typeof payload.id !== "number") {
          return jsonResponse({ error: "Invalid payload for delete_bug_report" }, 400);
        }

        const res = await fetch(`${SUPABASE_URL}/rest/v1/bug_reports?id=eq.${payload.id}`, {
          method: "DELETE",
          headers: dbHeaders,
        });

        if (!res.ok) {
          const errText = await res.text();
          return jsonResponse({ error: `Database error: ${errText}` }, res.status);
        }
        return jsonResponse({ success: true }, 200);
      }

      default:
        return jsonResponse({ error: "Unknown action" }, 400);
    }
  } catch (err: any) {
    return jsonResponse({ error: "Internal server error" }, 500);
  }
});
