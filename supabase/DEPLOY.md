# Supabase Deployment & Security Migration Guide

This document outlines the step-by-step procedure to deploy the server-side admin Edge Function and enforce database Row Level Security (RLS) for the AURASATH music app.

> [!IMPORTANT]
> Follow this safe order of operations strictly. Do **NOT** skip testing steps between Part A and Part B.

---

## 1. Prerequisites

- Supabase CLI installed (`npm install -g supabase` or `brew install supabase/tap/supabase`).
- Supabase project linked (`supabase link --project-ref <your-project-ref>`).

---

## 2. Set Admin Password Secret

Generate a strong, random password of at least 20+ characters:

```bash
# Example generating 32 random characters:
# openssl rand -base64 24
supabase secrets set ADMIN_PASSWORD="your-strong-random-20+-char-password-here"
```

> [!NOTE]
> `SUPABASE_SERVICE_ROLE_KEY` and `SUPABASE_URL` are provided automatically to Edge Functions by the Supabase runtime environment. You do **not** need to set them manually.

---

## 3. Deploy the Edge Function

Deploy the `admin-api` Edge Function with the `--no-verify-jwt` flag so it handles request authentication internally using the SHA-256 hashed admin password and constant-time comparison:

```bash
supabase functions deploy admin-api --no-verify-jwt
```

---

## 4. Run RLS Policies (Part A)

1. Open your Supabase Project Dashboard -> **SQL Editor**.
2. Open [`supabase/policies.sql`](./policies.sql).
3. Copy and execute **PART A ONLY** (the `CREATE POLICY` statements).
4. Verify all 6 policies are created without syntax errors:
   - `anon_select_app_updates`
   - `anon_select_news_updates`
   - `anon_insert_suggestions`
   - `anon_select_suggestions`
   - `anon_insert_bug_reports`
   - `anon_select_bug_reports`

---

## 5. Test Operations (Staging Verification)

Before enabling RLS enforcement, verify both user and admin operations:

1. **User Flow Testing**:
   - Check app updates (app launch or manually via Settings -> Check Updates).
   - Check news updates (news dialog).
   - Submit a test suggestion. Verify the ID returns and displays in user's submission list.
   - Submit a test bug report. Verify the ID returns.
2. **Admin Flow Testing**:
   - In Settings -> Admin Panel, enter the configured `ADMIN_PASSWORD`.
   - Verify unlock succeeds.
   - Test viewing suggestions and bug reports.
   - Test publishing an update or news item.
   - Test updating a status or reply comment.

---

## 6. Enable Row Level Security (Part B)

Once all Part A operations function properly:

1. In the Supabase Dashboard SQL Editor, run **PART B** of [`supabase/policies.sql`](./policies.sql):
   ```sql
   ALTER TABLE public.app_updates ENABLE ROW LEVEL SECURITY;
   ALTER TABLE public.news_updates ENABLE ROW LEVEL SECURITY;
   ALTER TABLE public.suggestions ENABLE ROW LEVEL SECURITY;
   ALTER TABLE public.bug_reports ENABLE ROW LEVEL SECURITY;
   ```
2. Re-test all normal-user and admin operations to verify that RLS enforcement permits valid client operations while blocking unauthorized queries.

---

## 7. Rollback Procedure

If any issue arises during or after enabling RLS:

1. Open Supabase Dashboard -> **SQL Editor**.
2. Open [`supabase/rollback.sql`](./rollback.sql).
3. Execute the entire rollback script. This disables RLS across all affected tables and drops the applied policies.

