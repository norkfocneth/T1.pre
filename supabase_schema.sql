-- Create profiles table if it does not exist
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT UNIQUE NOT NULL,
    username_lower TEXT UNIQUE NOT NULL,
    display_name TEXT,
    focus_score INTEGER NOT NULL DEFAULT 0,
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    email TEXT,
    avatar_url TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()),
    onboarding_version INTEGER DEFAULT 1,
    performance_tier TEXT DEFAULT 'Bronze',
    behaviour_profile TEXT DEFAULT 'Balanced',
    initial_percentile REAL DEFAULT 50.0,
    current_streak INTEGER DEFAULT 0,
    best_streak INTEGER DEFAULT 0,
    time_saved BIGINT DEFAULT 0,
    total_focus_sessions INTEGER DEFAULT 0,
    last_active_date TEXT,
    behaviour_score INTEGER DEFAULT 0,
    social_ratio REAL DEFAULT 0.0,
    productivity_ratio REAL DEFAULT 0.0,
    age INTEGER,
    gender TEXT,
    occupation TEXT,
    primary_goal TEXT,
    focus_score_version INTEGER DEFAULT 1,
    onboarding_completed_v2 BOOLEAN DEFAULT FALSE
);

-- Enable Row Level Security
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Allow select to everyone (needed for username availability checks and leaderboard)
DROP POLICY IF EXISTS "Allow public read access" ON public.profiles;
CREATE POLICY "Allow public read access" ON public.profiles
    FOR SELECT
    USING (true);

-- Allow authenticated users to insert/update their own profile
DROP POLICY IF EXISTS "Allow users to manage their own profile" ON public.profiles;
CREATE POLICY "Allow users to manage their own profile" ON public.profiles
    FOR ALL
    TO authenticated
    USING (auth.uid() = id)
    WITH CHECK (auth.uid() = id);

-- =========================================================================
-- FOCUS FIGHTS TABLE (Multiplayer Real Focus Duel Arena)
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.focus_fights (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    challenger_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    opponent_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    duration_minutes INTEGER NOT NULL DEFAULT 25,
    wager_tokens INTEGER NOT NULL DEFAULT 25,
    status TEXT NOT NULL DEFAULT 'COMPLETED', -- 'PENDING', 'ACCEPTED', 'DECLINED', 'IN_PROGRESS', 'COMPLETED', 'SURRENDERED'
    winner_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    challenger_seconds INTEGER NOT NULL DEFAULT 0,
    opponent_seconds INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Enable Row Level Security
ALTER TABLE public.focus_fights ENABLE ROW LEVEL SECURITY;

-- Allow public read access to focus fights
DROP POLICY IF EXISTS "Allow public read focus_fights" ON public.focus_fights;
CREATE POLICY "Allow public read focus_fights" ON public.focus_fights
    FOR SELECT
    USING (true);

-- Allow authenticated participants to insert fights
DROP POLICY IF EXISTS "Allow authenticated users to insert fights" ON public.focus_fights;
CREATE POLICY "Allow authenticated users to insert fights" ON public.focus_fights
    FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = challenger_id OR auth.uid() = opponent_id);

-- Allow participants to update fights
DROP POLICY IF EXISTS "Allow participants to update fights" ON public.focus_fights;
CREATE POLICY "Allow participants to update fights" ON public.focus_fights
    FOR UPDATE
    TO authenticated
    USING (auth.uid() = challenger_id OR auth.uid() = opponent_id)
    WITH CHECK (auth.uid() = challenger_id OR auth.uid() = opponent_id);
