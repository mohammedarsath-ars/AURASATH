-- ============================================================================
-- Supabase Row Level Security (RLS) Rollback Script
-- AURASATH App Security Migration
-- Use this script to restore tables to their unrestricted state if issues arise.
-- ============================================================================

-- 1. Disable Row Level Security on all affected tables
ALTER TABLE public.app_updates DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.news_updates DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.suggestions DISABLE ROW LEVEL SECURITY;
ALTER TABLE public.bug_reports DISABLE ROW LEVEL SECURITY;

-- 2. Drop applied policies
DROP POLICY IF EXISTS "anon_select_app_updates" ON public.app_updates;
DROP POLICY IF EXISTS "anon_select_news_updates" ON public.news_updates;
DROP POLICY IF EXISTS "anon_insert_suggestions" ON public.suggestions;
DROP POLICY IF EXISTS "anon_select_suggestions" ON public.suggestions;
DROP POLICY IF EXISTS "anon_insert_bug_reports" ON public.bug_reports;
DROP POLICY IF EXISTS "anon_select_bug_reports" ON public.bug_reports;

