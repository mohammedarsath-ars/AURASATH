-- ============================================================================
-- Supabase Row Level Security (RLS) Policies
-- AURASATH App Security Migration
-- ============================================================================

--------------------------------------------------------------------------------
-- PART A: CREATE POLICY STATEMENTS
-- Defines anonymous (anon) role access required by client-side user features.
-- Run Part A first.
--------------------------------------------------------------------------------

-- 1. APP UPDATES
-- Needed by: SupabaseService.checkLatestUpdate()
-- Allows anonymous users to query the latest available app release.
CREATE POLICY "anon_select_app_updates"
ON public.app_updates
FOR SELECT
TO anon
USING (true);

-- 2. NEWS UPDATES
-- Needed by: SupabaseService.checkLatestNews()
-- Allows anonymous users to fetch recent announcement/news posts.
CREATE POLICY "anon_select_news_updates"
ON public.news_updates
FOR SELECT
TO anon
USING (true);

-- 3. SUGGESTIONS (INSERT)
-- Needed by: SupabaseService.submitSuggestion()
-- Allows anonymous users to submit feedback and suggestions.
-- Strict validation ensures users cannot auto-approve their own suggestions or inject admin comments.
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
--
-- SECURITY NOTE: This SELECT policy exposes all suggestion rows to anon users
-- (the app filters by ID client-side). In a future phase, this query should be
-- replaced with a SECURITY DEFINER RPC function to restrict access strictly to user-owned IDs.
CREATE POLICY "anon_select_suggestions"
ON public.suggestions
FOR SELECT
TO anon
USING (true);

-- 5. BUG REPORTS (INSERT)
-- Needed by: SupabaseService.submitBugReport()
-- Allows anonymous users to report bugs and issues.
-- Strict validation ensures initial status is pending and admin_comment is null.
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
--
-- SECURITY NOTE: This SELECT policy exposes all bug report rows to anon users
-- (the app filters by ID client-side). In a future phase, this query should be
-- replaced with a SECURITY DEFINER RPC function to restrict access strictly to user-owned IDs.
CREATE POLICY "anon_select_bug_reports"
ON public.bug_reports
FOR SELECT
TO anon
USING (true);


--------------------------------------------------------------------------------
-- PART B: ENABLE ROW LEVEL SECURITY
-- !!! RUN LAST, AFTER TESTING PART A POLICIES !!!
-- Enabling RLS immediately restricts non-service_role access to only those
-- operations explicitly allowed by the policies above.
--------------------------------------------------------------------------------

ALTER TABLE public.app_updates ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.news_updates ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.suggestions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bug_reports ENABLE ROW LEVEL SECURITY;

