-- ============================================================================
-- Supabase Row Level Security (RLS) Policies
-- AURASATH App Security Migration
-- ============================================================================

--------------------------------------------------------------------------------
-- PART A: DROP EXISTING & RECREATE POLICIES
-- Defines anonymous (anon) role access required by client-side user features.
--------------------------------------------------------------------------------

-- Grant schema and table access to anon role
GRANT USAGE ON SCHEMA public TO anon, authenticated;
GRANT SELECT, INSERT ON public.app_updates TO anon, authenticated;
GRANT SELECT, INSERT ON public.news_updates TO anon, authenticated;
GRANT SELECT, INSERT ON public.suggestions TO anon, authenticated;
GRANT SELECT, INSERT ON public.bug_reports TO anon, authenticated;

-- 1. APP UPDATES
DROP POLICY IF EXISTS "anon_select_app_updates" ON public.app_updates;
CREATE POLICY "anon_select_app_updates"
ON public.app_updates
FOR SELECT
TO anon
USING (true);

-- 2. NEWS UPDATES
DROP POLICY IF EXISTS "anon_select_news_updates" ON public.news_updates;
CREATE POLICY "anon_select_news_updates"
ON public.news_updates
FOR SELECT
TO anon
USING (true);

-- 3. SUGGESTIONS (INSERT)
-- Needed by: SupabaseService.submitSuggestion()
-- Allows anonymous users to submit feedback and suggestions.
-- Strict validation ensures users cannot auto-approve their own suggestions or inject admin comments.
DROP POLICY IF EXISTS "anon_insert_suggestions" ON public.suggestions;
CREATE POLICY "anon_insert_suggestions"
ON public.suggestions
FOR INSERT
TO anon
WITH CHECK (
    status = 'pending'
    AND admin_comment IS NULL
);

-- 4. SUGGESTIONS (SELECT)
-- Needed by: SupabaseService.fetchSuggestionsByIds()
-- Also needed by: SupabaseService.submitSuggestion() because it uses 'Prefer: return=representation'
-- to read back the generated suggestion ID after insert.
DROP POLICY IF EXISTS "anon_select_suggestions" ON public.suggestions;
CREATE POLICY "anon_select_suggestions"
ON public.suggestions
FOR SELECT
TO anon
USING (true);

-- 5. BUG REPORTS (INSERT)
-- Needed by: SupabaseService.submitBugReport()
-- Allows anonymous users to report bugs and issues.
-- Strict validation ensures initial status is pending and admin_comment is null.
DROP POLICY IF EXISTS "anon_insert_bug_reports" ON public.bug_reports;
CREATE POLICY "anon_insert_bug_reports"
ON public.bug_reports
FOR INSERT
TO anon
WITH CHECK (
    status = 'pending'
    AND admin_comment IS NULL
);

-- 6. BUG REPORTS (SELECT)
-- Needed by: SupabaseService.fetchBugReportsByIds()
-- Also needed by: SupabaseService.submitBugReport() because it uses 'Prefer: return=representation'
-- to read back the generated bug report ID after insert.
DROP POLICY IF EXISTS "anon_select_bug_reports" ON public.bug_reports;
CREATE POLICY "anon_select_bug_reports"
ON public.bug_reports
FOR SELECT
TO anon
USING (true);


--------------------------------------------------------------------------------
-- PART B: ENABLE ROW LEVEL SECURITY
--------------------------------------------------------------------------------

ALTER TABLE public.app_updates ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.news_updates ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.suggestions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bug_reports ENABLE ROW LEVEL SECURITY;
