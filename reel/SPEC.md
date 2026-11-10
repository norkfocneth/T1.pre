# SPEC: T1 Reel - "The 1v1 Boss Fight Against Distraction"

GOAL:    Viewer feels the intense rush of defeating doomscrolling like a video game boss, wanting to download T1 immediately to turn their focus into a fight they can win.
OUTPUT:  HyperFrames, 1080 x 1920, 30 fps
TIME:    22.0 seconds x 30 fps = 660 frames; 128 BPM (14 frames per beat)
LOOK:    Ground: deep arcade dark slate (#0d0b18) with a 24px isometric battle arena grid and 3% subtle digital scanlines;
         Ink: crisp pure white (#FFFFFF) for primary titles, deep purple (#1b1433) for UI panel plates;
         Accent: Electric Arcade Gold (#F59E0B) for combo counters and XP, Electric Violet (#A855F7) for T1 Shields and HP recovery;
         Display face: Impactful geometric sans (Space Grotesk / Outfit) at 68px (-1.5% tracking) for headers, 120px for KO / FIGHT titles;
         Body face: Clean technical mono (JetBrains Mono / Space Mono) at 32px for stats, timers, and combo meters;
         Medium: 16-bit arcade fighting game HUD layered over high-definition 3D character cutouts with crisp drop shadows (offset 0 16px, blur 24px, 40% opacity);
         Light & depth: rim light on character edges, 4% vignette at edges.
OBJECTS: 1. Cute 3D Astronaut Mascot: 520px width, placed bottom-left at 35% height, layered with battle HP bar;
         2. Doom-Scroll Boss: 580px width, floating top-right, made of animated notification spikes, red badge swarm (99+ alerts);
         3. Fighting Game HUD: Player 1 (Mascot) vs Boss HP bars at top (height 44px, border 3px solid #332358);
         4. T1 Focus Shield: 620px diameter hexagonal barrier with rotating perimeter runes and shockwave impact rings;
         5. Focus Streak Counter: 84px bold kinetic text counting `×2`, `×5`, `×10 COMBO`;
         6. End Card: 240px T1 brand mark, centered, with action badge `START YOUR FIRST FIGHT`.
MOTION:  Eases:
         - Enter: `cubic-bezier(0.16, 1, 0.3, 1)` (expo out, snappy 14 frames)
         - Impact/Attack: `cubic-bezier(0.87, 0, 0.13, 1)` (snappy punch, 8 frames)
         - Spring: damping ratio 0.55 for Mascot reactions, screen shake magnitude 12px decaying over 10 frames
         - Exit: `cubic-bezier(0.7, 0, 0.84, 0)` (accelerate out, 10 frames)
SHOTS:
         - S1 [0-90 (0.0s - 3.0s)]: "ROUND 1 · FIGHT!" announcer banner wipes across frame. Boss unleashes doomscroll barrage. Mascot HP drops from 100% to 18%. Mascot shakes with tired sweat drop. Sound: arcade gong + fast hit-sparks.
         - S2 [90-210 (3.0s - 7.0s)]: "FOCUS CRITICAL!" alarm rings with flashing red frame border. Boss charges `INFINITE REELS CANNON`. Sound: rising synthesizer siren.
         - S3 [210-360 (7.0s - 12.0s)]: HERO COUNTER! Giant button taps: "ACTIVATE T1 FOCUS LOCK". Mascot puts on headphones, glowing purple eyes open. Giant hexagonal shield deploys and deflects all boss attacks with bright impact shockwaves. Mascot HP restores to 100%. Sound: deep sub-bass drop + crystalline barrier activation.
         - S4 [360-510 (12.0s - 17.0s)]: COUNTER COMBO RUSH! Mascot unleashes focus strikes: "POMODORO STRIKE (+25m)", "DEEP WORK SLASH (+60m)", "STRICT LOCK FINISHER". Boss HP drops to 0. Sound: rapid arcade combo hits (14 frames per hit on the beat).
         - S5 [510-600 (17.0s - 20.0s)]: "K.O.!" explodes in giant 130px gold letters. Boss vanishes in digital sparks. Mascot leaps onto mountain rock with T1 victory flag. Sound: heavy bass explosion + triumphant retro victory fanfare.
         - S6 [600-660 (20.0s - 22.0s)]: Clean End Card: Mascot smiles holding trophy, T1 logo shines, CTA: "Download T1 · Stop Scrolling, Start Winning". Sound: soft chime sparkle settle.
BANNED:  A generic dark void with small unreadable cards; generic stock music; floating disconnected tech particles; static slideshow transitions; tiny text under 28px; unanchored logos.
QA:      f30: Verify "ROUND 1 · FIGHT" reads clearly across safe zone (between 168px and 1330px);
         f120: Verify Mascot low-HP stagger animation and alert clarity;
         f270: Verify T1 Hex Shield impact alignment with incoming notifications;
         f450: Verify Combo rush typography and numbers readability;
         f540: Verify K.O. blast and celebratory mascot pose;
         f630: Verify End Card branding, action badge, and store download text.
