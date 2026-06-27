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
    total_focus_sessions INTEGER DEFAULT 0
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
