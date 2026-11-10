# SPEC: T1 Minimalist Discipline Evolution Reel (1080x1920 · 12s)

GOAL:    Viewer feels the undeniable mathematical truth that consistency turns struggle into momentum, immediately motivated to build daily discipline with T1.
OUTPUT:  HyperFrames, 1080 x 1920, 30 fps
TIME:    12.0 seconds x 30 fps = 360 frames
LOOK:    Ground: Pure black (#000000) on top half, cinematic forest photography on bottom half;
         Ink: Ethereal neon violet (#c084fc, #d8b4fe, #a855f7) with glowing SVG drop-shadows;
         Display face: Outfit / Inter at 42px for stage titles, 46px for center punchline;
         Body face: JetBrains Mono / Inter at 30px for metadata and T1 brand signature;
         Medium: Dual-tier vertical split: 
           - Top 54% (0 - 1040px): 4-tier minimalist glowing vector line animation;
           - Center: Elegant divider quote "Consistency is the key.";
           - Bottom 46% (1040 - 1920px): High-contrast cinematic athlete shadowboxing in misty forest.
OBJECTS: 1. Stage 1 ("Today"): Glowing stick figure leaning and pushing tilted square box (friction, high effort);
         2. Stage 2 ("In a week"): Figure pushing rounded beveled box (corners shaving down);
         3. Stage 3 ("In a month"): Figure effortlessly rolling perfect glowing wheel;
         4. Stage 4 ("In a year"): Figure comfortably riding on top of the glowing sphere with hypersonic speed streaks;
         5. Ground lines: 4 horizontal wavy glowing lavender lines anchoring each stage;
         6. Bottom scene: 35mm cinematic photograph of lone hooded athlete training in cold fog with slow Ken Burns pan.
MOTION:  Eases:
         - Fade-in & reveal: `power2.out` (15 frames)
         - Stagger wave: 2.5s intervals per stage
         - Speed trail pulse: continuous deterministic harmonic wave
         - Camera slow dolly: `scale: 1.0` to `1.08` across 12s
BANNED:  Generic corporate UI cards; noisy colored gradients; cartoon memes; cluttered text; recycled app screenshots.
QA:      f30: Verify "Today" and tilted square friction read clearly;
         f120: Verify "In a week" rounded bevel transition;
         f200: Verify "In a month" wheel rolling motion;
         f280: Verify "In a year" riding figure and speed trail particles;
         f340: Verify center quote "Consistency is the key." and T1 brand resolution.
