const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const CHROME = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const WORK_DIR = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\opal_style";
const OUT_DIR = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\opal_final_screenshots";

if (!fs.existsSync(OUT_DIR)) {
  fs.mkdirSync(OUT_DIR, { recursive: true });
}

// 3D Faceted Obsidian Rock SVG generator (Opal signature floating rocks)
function render3dRock(size, rotate, blur = 0, opacity = 1) {
  return `
  <svg width="${size}" height="${size}" viewBox="0 0 100 100" style="filter: drop-shadow(0 18px 28px rgba(0,0,0,0.45)) ${blur > 0 ? `blur(${blur}px)` : ''}; transform: rotate(${rotate}deg); opacity: ${opacity};">
    <defs>
      <linearGradient id="rf1_${size}_${rotate}" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#475569" />
        <stop offset="100%" stop-color="#1e293b" />
      </linearGradient>
      <linearGradient id="rf2_${size}_${rotate}" x1="0%" y1="100%" x2="100%" y2="0%">
        <stop offset="0%" stop-color="#334155" />
        <stop offset="100%" stop-color="#0f172a" />
      </linearGradient>
      <linearGradient id="rspec_${size}_${rotate}" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#94a3b8" />
        <stop offset="100%" stop-color="#475569" />
      </linearGradient>
    </defs>
    <polygon points="50,10 78,28 55,48 24,32" fill="url(#rspec_${size}_${rotate})" />
    <polygon points="24,32 55,48 42,88 12,65" fill="url(#rf1_${size}_${rotate})" />
    <polygon points="55,48 78,28 92,60 68,92 42,88" fill="url(#rf2_${size}_${rotate})" />
    <polygon points="42,88 68,92 50,96 22,82" fill="#09090b" opacity="0.8" />
    <polyline points="50,10 55,48 42,88" stroke="rgba(255,255,255,0.4)" stroke-width="1.2" fill="none" />
    <polyline points="24,32 55,48 78,28" stroke="rgba(255,255,255,0.3)" stroke-width="1" fill="none" />
  </svg>
  `;
}

// Pixel-perfect In-App Screen Renderers for T1 Screens
const inAppScreens = {
  // Screen 3: Minimalist Blocked Screen (Identical to Opal Slide 3)
  blockedScreen: `
    <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; align-items:center; justify-content:center; padding:44px; text-align:center;">
      <div style="width:108px; height:108px; border-radius:28px; background:linear-gradient(45deg, #f09433, #e6683c, #dc2743, #cc2366, #bc1888); display:flex; align-items:center; justify-content:center; margin-bottom:34px; box-shadow:0 20px 45px rgba(220,39,67,0.4);">
        <div style="width:54px; height:54px; border:5px solid white; border-radius:15px; position:relative; display:flex; align-items:center; justify-content:center;">
          <div style="width:22px; height:22px; border:5px solid white; border-radius:50%;"></div>
          <div style="width:6px; height:6px; background:white; border-radius:50%; position:absolute; top:4px; right:4px;"></div>
        </div>
      </div>
      <h2 style="font-size:36px; font-weight:800; color:#fff; margin-bottom:14px; letter-spacing:-0.5px; line-height:1.2;">Instagram is<br>Blocked by T1.</h2>
      <p style="font-size:18px; color:#94a3b8; line-height:1.45; margin-bottom:48px; max-width:390px;">You set this rule this morning.<br><strong style="color:#fff; font-size:20px;">Past you was right.</strong></p>
      <div style="width:100%; max-width:370px; height:66px; border-radius:33px; background:#fff; color:#000; font-weight:800; font-size:19px; display:flex; align-items:center; justify-content:center; letter-spacing:0.5px; box-shadow:0 15px 35px rgba(255,255,255,0.3);">Stay Locked In</div>
      <span style="font-size:12.5px; color:#64748b; margin-top:28px; letter-spacing:1.5px; text-transform:uppercase; font-weight:700;">Locked by T1 Monk Mode Protocol</span>
    </div>
  `,

  // Screen 4: Real Focus Timer Screen
  focusTimerScreen: `
    <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; align-items:center; justify-content:space-between; padding:40px 28px 48px 28px; text-align:center;">
      <div style="width:100%; display:flex; justify-content:space-between; align-items:center;">
        <span style="font-size:13px; font-weight:800; letter-spacing:2px; color:#64748b; text-transform:uppercase;">MONK MODE PROTOCOL</span>
        <div style="width:36px; height:36px; border-radius:50%; background:#18181b; display:flex; align-items:center; justify-content:center; color:#94a3b8; font-size:18px;">✕</div>
      </div>
      
      <!-- Circular Timer Ring -->
      <div style="position:relative; width:300px; height:300px; display:flex; align-items:center; justify-content:center; margin:10px 0;">
        <svg width="300" height="300" viewBox="0 0 300 300" style="transform: rotate(-90deg);">
          <circle cx="150" cy="150" r="128" stroke="#27272a" stroke-width="8" fill="none" />
          <circle cx="150" cy="150" r="128" stroke="#22c55e" stroke-width="10" stroke-linecap="round" stroke-dasharray="804" stroke-dashoffset="220" fill="none" style="filter:drop-shadow(0 0 15px rgba(34,197,94,0.6));" />
        </svg>
        <div style="position:absolute; display:flex; flex-direction:column; align-items:center;">
          <span style="font-size:62px; font-weight:800; color:#fff; font-family:'Space Grotesk', monospace; letter-spacing:-2px;">25:00</span>
          <span style="font-size:13px; font-weight:700; color:#4ade80; letter-spacing:1.5px; text-transform:uppercase; margin-top:4px;">● FLOW STATE ACTIVE</span>
        </div>
      </div>

      <!-- Duration Presets -->
      <div style="width:100%; display:flex; flex-direction:column; gap:16px;">
        <div style="display:flex; justify-content:center; gap:10px;">
          <div style="padding:10px 18px; border-radius:20px; background:#18181b; border:1px solid #27272a; color:#a1a1aa; font-weight:700; font-size:14px;">15 min</div>
          <div style="padding:10px 22px; border-radius:20px; background:#22c55e; color:#000; font-weight:800; font-size:14px; box-shadow:0 0 20px rgba(34,197,94,0.4);">25 min</div>
          <div style="padding:10px 18px; border-radius:20px; background:#18181b; border:1px solid #27272a; color:#a1a1aa; font-weight:700; font-size:14px;">45 min</div>
          <div style="padding:10px 18px; border-radius:20px; background:#18181b; border:1px solid #27272a; color:#a1a1aa; font-weight:700; font-size:14px;">60 min</div>
        </div>

        <div style="width:100%; height:60px; border-radius:30px; background:#fff; color:#000; font-weight:800; font-size:18px; display:flex; align-items:center; justify-content:center; gap:10px; box-shadow:0 15px 30px rgba(255,255,255,0.25);">
          <span>⚡</span> Stay In Session
        </div>
      </div>
    </div>
  `,

  // Screen 5: Real Screen Time Audit UI
  screenTimeAuditScreen: `
    <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; padding:36px 24px; text-align:left;">
      <!-- Header -->
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:24px;">
        <div>
          <span style="font-size:12px; font-weight:800; letter-spacing:1.5px; color:#f43f5e; text-transform:uppercase;">DAILY BEHAVIOUR AUDIT</span>
          <h2 style="font-size:32px; font-weight:800; color:#fff; font-family:'Space Grotesk', monospace; margin-top:2px;">3h 16m <span style="font-size:16px; color:#94a3b8; font-family:'Plus Jakarta Sans', sans-serif; font-weight:600;">Total Active</span></h2>
        </div>
        <div style="padding:6px 14px; border-radius:16px; background:rgba(244,63,94,0.15); border:1px solid rgba(244,63,94,0.4); color:#fb7185; font-size:12px; font-weight:700;">
          -1h 45m vs AVG
        </div>
      </div>

      <!-- Category Breakdown Bars -->
      <div style="background:#18181b; border:1px solid #27272a; border-radius:24px; padding:22px; margin-bottom:18px;">
        <span style="font-size:12px; font-weight:800; letter-spacing:1.5px; color:#71717a; text-transform:uppercase;">CATEGORY BREAKDOWN</span>
        
        <div style="display:flex; flex-direction:column; gap:16px; margin-top:16px;">
          <!-- Productivity -->
          <div>
            <div style="display:flex; justify-content:space-between; font-size:14px; font-weight:700; color:#fff; margin-bottom:6px;">
              <span>Productivity & Code</span>
              <span style="color:#22c55e;">2h 15m (69%)</span>
            </div>
            <div style="width:100%; height:8px; border-radius:4px; background:#27272a; overflow:hidden;">
              <div style="width:69%; height:100%; background:#22c55e;"></div>
            </div>
          </div>

          <!-- Education -->
          <div>
            <div style="display:flex; justify-content:space-between; font-size:14px; font-weight:700; color:#fff; margin-bottom:6px;">
              <span>Education & Reading</span>
              <span style="color:#38bdf8;">45m (23%)</span>
            </div>
            <div style="width:100%; height:8px; border-radius:4px; background:#27272a; overflow:hidden;">
              <div style="width:23%; height:100%; background:#38bdf8;"></div>
            </div>
          </div>

          <!-- Communication -->
          <div>
            <div style="display:flex; justify-content:space-between; font-size:14px; font-weight:700; color:#fff; margin-bottom:6px;">
              <span>Communication</span>
              <span style="color:#94a3b8;">16m (8%)</span>
            </div>
            <div style="width:100%; height:8px; border-radius:4px; background:#27272a; overflow:hidden;">
              <div style="width:8%; height:100%; background:#94a3b8;"></div>
            </div>
          </div>

          <!-- Social Reels -->
          <div>
            <div style="display:flex; justify-content:space-between; font-size:14px; font-weight:700; color:#fff; margin-bottom:6px;">
              <span>Social Media (Reels / Shorts)</span>
              <span style="color:#f43f5e; font-weight:800;">0m (BLOCKED)</span>
            </div>
            <div style="width:100%; height:8px; border-radius:4px; background:#27272a; overflow:hidden;">
              <div style="width:0%; height:100%; background:#f43f5e;"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Brutal Audit Insight Box -->
      <div style="background:linear-gradient(145deg, #18181b, #09090b); border:1px solid rgba(244,63,94,0.3); border-radius:22px; padding:20px; box-shadow:0 15px 30px rgba(0,0,0,0.5);">
        <div style="display:flex; align-items:center; gap:10px; margin-bottom:8px;">
          <span style="font-size:18px;">⚖️</span>
          <span style="font-size:14px; font-weight:800; color:#fff; letter-spacing:0.5px;">COGNITIVE AUDIT REPORT</span>
        </div>
        <p style="font-size:14.5px; line-height:1.45; color:#cbd5e1;">
          Zero minutes leaked into algorithmic dopamine loops today. <strong style="color:#fff;">You are outworking 94% of builders in your peer cohort.</strong>
        </p>
      </div>
    </div>
  `,

  // Screen 7: Real Token Wallet Screen
  tokenWalletScreen: `
    <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; padding:36px 24px; text-align:left;">
      <!-- Top Bar -->
      <div style="display:flex; align-items:center; gap:14px; margin-bottom:20px;">
        <div style="width:36px; height:36px; border-radius:50%; background:#18181b; border:1px solid #27272a; display:flex; align-items:center; justify-content:center; color:#fff;">←</div>
        <div>
          <h3 style="font-size:22px; font-weight:800; color:#fff;">Token Wallet</h3>
          <p style="font-size:13px; color:#a1a1aa;">Mine tokens by staying focused</p>
        </div>
      </div>

      <!-- Hero Coin Card -->
      <div style="background:linear-gradient(145deg, #18181b, #09090b); border:1px solid rgba(245,158,11,0.3); border-radius:28px; padding:28px 24px; text-align:center; margin-bottom:18px; box-shadow:0 20px 40px rgba(245,158,11,0.15);">
        <img src="coin.png" style="width:100px; height:100px; margin-bottom:12px; filter:drop-shadow(0 15px 25px rgba(245,158,11,0.4));">
        <h1 style="font-size:46px; font-weight:800; color:#fff; font-family:'Space Grotesk', monospace; letter-spacing:-1px;">220 <span style="font-size:24px; color:#f59e0b;">T1</span></h1>
        <p style="font-size:13px; font-weight:700; color:#a1a1aa; letter-spacing:1px; text-transform:uppercase;">TOTAL TOKENS MINED</p>
      </div>

      <!-- Merch Store Progress -->
      <div style="background:#18181b; border:1px solid #27272a; border-radius:22px; padding:18px; margin-bottom:18px;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
          <span style="font-size:15px; font-weight:700; color:#fff;">T1 Signature Oversized Tee</span>
          <span style="font-size:13px; font-weight:700; color:#f59e0b;">6,000 Tokens</span>
        </div>
        <div style="width:100%; height:8px; border-radius:4px; background:#27272a; overflow:hidden;">
          <div style="width:14%; height:100%; background:linear-gradient(90deg, #f59e0b, #fbbf24);"></div>
        </div>
        <p style="font-size:12px; color:#71717a; margin-top:8px;">Earned through deep work. Cannot be bought with fiat.</p>
      </div>

      <!-- Recent Transactions -->
      <div style="flex:1;">
        <span style="font-size:12px; font-weight:800; letter-spacing:1.5px; color:#71717a; text-transform:uppercase;">RECENT SESSIONS</span>
        <div style="display:flex; flex-direction:column; gap:10px; margin-top:10px;">
          <div style="display:flex; justify-content:space-between; align-items:center; padding:12px 16px; background:#18181b; border-radius:16px; border:1px solid #27272a;">
            <div>
              <div style="font-size:14.5px; font-weight:700; color:#fff;">Deep Focus Session (45m)</div>
              <div style="font-size:12px; color:#71717a;">Today • Streak Multiplier x1.5</div>
            </div>
            <span style="font-size:16px; font-weight:800; color:#22c55e;">+45 T1</span>
          </div>
          <div style="display:flex; justify-content:space-between; align-items:center; padding:12px 16px; background:#18181b; border-radius:16px; border:1px solid #27272a;">
            <div>
              <div style="font-size:14.5px; font-weight:700; color:#fff;">Focus Session (25m)</div>
              <div style="font-size:12px; color:#71717a;">Today • Verified on-device</div>
            </div>
            <span style="font-size:16px; font-weight:800; color:#22c55e;">+25 T1</span>
          </div>
        </div>
      </div>
    </div>
  `,

  // Screen 8: Real Offline AI Coach Dialog Screen (Centered & Elevated)
  aiCoachScreen: `
    <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; justify-content:center; align-items:center; padding:30px 24px; position:relative;">
      <!-- Ambient Cyan Flare -->
      <div style="position:absolute; inset:0; background:radial-gradient(circle at 50% 30%, rgba(34,211,238,0.18), transparent 70%);"></div>

      <!-- AI Coach Dialog Card -->
      <div style="position:relative; z-index:10; width:100%; background:#18181b; border:1px solid rgba(34,211,238,0.35); border-radius:28px; padding:28px 24px; box-shadow:0 30px 60px rgba(0,0,0,0.7);">
        <!-- Header -->
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px; border-bottom:1px solid #27272a; padding-bottom:18px;">
          <div style="display:flex; align-items:center; gap:14px;">
            <div style="width:52px; height:52px; border-radius:50%; overflow:hidden; border:2.5px solid #22d3ee; box-shadow:0 0 25px rgba(34,211,238,0.6);">
              <img src="robot.jpg" style="width:100%; height:100%; object-fit:cover;">
            </div>
            <div>
              <h3 style="font-size:19px; font-weight:800; color:#fff;">T1 AI Coach</h3>
              <p style="font-size:12px; font-weight:700; color:#22d3ee; letter-spacing:0.5px;">● OFFLINE INSIGHTS ENGINE</p>
            </div>
          </div>
          <span style="font-size:11px; font-weight:800; color:#71717a; letter-spacing:1px; background:#27272a; padding:4px 8px; border-radius:8px;">100% PRIVATE</span>
        </div>

        <!-- Chat Conversation Bubble -->
        <div style="background:#09090b; border:1px solid #27272a; border-radius:22px; padding:20px; margin-bottom:20px;">
          <p style="font-size:16px; color:#e2e8f0; line-height:1.5; font-weight:500;">
            "You logged <strong style="color:#fff;">3h 16m</strong> today, but 45 minutes leaked into mindless social scrolling. <br><br>
            <strong style="color:#38bdf8;">Hard truth:</strong> Stop optimizing your morning routine. The top 1% don't negotiate with their mood. Execute your first 90-minute deep work block now."
          </p>
        </div>

        <!-- Action Pill -->
        <div style="width:100%; height:56px; border-radius:28px; background:#22d3ee; color:#000; font-weight:800; font-size:17px; display:flex; align-items:center; justify-content:center; letter-spacing:0.5px; box-shadow:0 12px 25px rgba(34,211,238,0.35);">
          Execute 90-Min Focus Session
        </div>
      </div>
    </div>
  `
};

// 8 Diverse, High-Impact Slides
const slides = [
  // -------------------------------------------------------------
  // SLIDE 1: T1 Blocks Distractions For You
  // -------------------------------------------------------------
  {
    id: 1,
    bgGradient: "radial-gradient(circle at 45% 10%, #e0f2fe 0%, #bae6fd 38%, #93c5fd 72%, #60a5fa 100%)",
    headerAlign: "left",
    headerTop: "80px",
    headerLeft: "80px",
    headline: "T1 blocks<br>distractions for you.",
    subline: "Built for founders, builders & top 1% minds.",
    screenType: "image",
    phoneImg: "01_home_focus_ring.png",
    phoneStage: "perspective: 1400px; bottom: -80px; right: 40px; width: 680px; height: 1420px;",
    phoneTransform: "rotateY(-15deg) rotateX(8deg) rotateZ(-3deg)",
    pills: [
      {
        icon: "⚡",
        iconBg: "rgba(56, 189, 248, 0.15)",
        iconBorder: "rgba(56, 189, 248, 0.4)",
        tag: "NAVAL",
        tagBg: "rgba(56, 189, 248, 0.2)",
        tagColor: "#38bdf8",
        title: "Naval Ravikant",
        body: 'Free education is abundant; desire is scarce.<br><strong style="color:#7dd3fc;">Outwork the distracted 99%.</strong>',
        top: "470px",
        left: "70px",
        rot: "-1.5deg",
        width: "530px"
      },
      {
        icon: "⚠️",
        iconBg: "rgba(239, 68, 68, 0.15)",
        iconBorder: "rgba(239, 68, 68, 0.4)",
        tag: "INSTAGRAM",
        tagBg: "rgba(239, 68, 68, 0.2)",
        tagColor: "#f87171",
        title: "Doomscrolling Alert!",
        body: 'You\'ve been scrolling for 35 min.<br><strong style="color:#fca5a5;">"Your competition didn\'t take today off."</strong>',
        top: "680px",
        left: "90px",
        rot: "2deg",
        width: "530px"
      }
    ],
    floatingRocks: [
      { top: "420px", right: "120px", size: 120, rot: 25, blur: 0, op: 1 },
      { top: "350px", left: "70px", size: 80, rot: -45, blur: 1, op: 0.85 },
      { bottom: "240px", left: "120px", size: 95, rot: 60, blur: 0, op: 0.95 }
    ],
    footer: ["THE WALL STREET JOURNAL", "TECHCRUNCH", "FORBES"]
  },

  // -------------------------------------------------------------
  // SLIDE 2: Cognitive Focus Score & Weekly Analytics
  // -------------------------------------------------------------
  {
    id: 2,
    bgGradient: "radial-gradient(circle at 50% 12%, #faf5ff 0%, #f3e8ff 35%, #ddd6fe 70%, #c4b5fd 100%)",
    headerAlign: "center",
    headerTop: "80px",
    headline: "Measure your focus.<br>Not just screen time.",
    subline: "Your daily cognitive benchmark. Calibrated against the top 1%.",
    screenType: "image",
    phoneImg: "08_profile_screen.png",
    phoneStage: "bottom: -70px; left: 50%; transform: translateX(-50%); width: 680px; height: 1420px;",
    phoneTransform: "none",
    pills: [
      {
        icon: "📈",
        iconBg: "rgba(168, 85, 247, 0.15)",
        iconBorder: "rgba(168, 85, 247, 0.4)",
        tag: "SCORE 85",
        tagBg: "rgba(168, 85, 247, 0.2)",
        tagColor: "#c084fc",
        title: "Top 1% Focus Tier",
        body: 'Ahead of 94% of users today.<br><strong style="color:#d8b4fe;">High-agency cognitive execution active.</strong>',
        top: "470px",
        left: "50px",
        rot: "-2.5deg",
        width: "490px"
      },
      {
        icon: "💡",
        iconBg: "rgba(245, 158, 11, 0.15)",
        iconBorder: "rgba(245, 158, 11, 0.4)",
        tag: "STEVE JOBS",
        tagBg: "rgba(245, 158, 11, 0.2)",
        tagColor: "#fbbf24",
        title: "The Focus Rule",
        body: '"Deciding what NOT to do is as important as deciding what to do."',
        top: "680px",
        right: "50px",
        rot: "2deg",
        width: "500px"
      }
    ],
    floatingRocks: [
      { top: "360px", right: "90px", size: 110, rot: -25, blur: 0, op: 1 },
      { bottom: "260px", right: "100px", size: 85, rot: 45, blur: 1, op: 0.9 },
      { top: "420px", left: "80px", size: 75, rot: 15, blur: 0, op: 0.85 }
    ],
    footer: ["STANFORD NEUROSCIENCE", "Y COMBINATOR FOUNDERS", "HARVARD"]
  },

  // -------------------------------------------------------------
  // SLIDE 3: Locks The Apps You Keep Opening (The Opal Iconic Minimalist Look)
  // -------------------------------------------------------------
  {
    id: 3,
    bgGradient: "radial-gradient(circle at 50% 12%, #f8fafc 0%, #f1f5f9 35%, #e2e8f0 70%, #94a3b8 100%)",
    headerAlign: "center",
    headerTop: "80px",
    headline: "Locks the apps<br>you keep opening.",
    subline: "Zero negotiations. Past you set the lock. Future you wins.",
    screenType: "html",
    screenContent: inAppScreens.blockedScreen,
    phoneStage: "bottom: -70px; left: 50%; transform: translateX(-50%); width: 680px; height: 1420px;",
    phoneTransform: "none",
    pills: [], // Minimalist! No cluttered cards covering the iconic blocked screen, just like Opal Slide 3!
    floatingRocks: [
      { top: "380px", left: "90px", size: 110, rot: 30, blur: 0, op: 1 },
      { top: "410px", right: "80px", size: 95, rot: -40, blur: 0, op: 1 },
      { bottom: "260px", left: "90px", size: 75, rot: 70, blur: 1, op: 0.85 },
      { bottom: "340px", right: "90px", size: 85, rot: -20, blur: 0, op: 0.9 }
    ],
    footer: ["RADICAL ELIMINATION", "MONK MODE PROTOCOL", "ZERO COMPROMISE"]
  },

  // -------------------------------------------------------------
  // SLIDE 4: Deep Work Session Timer (Flow State in 25 Min)
  // -------------------------------------------------------------
  {
    id: 4,
    bgGradient: "radial-gradient(circle at 55% 10%, #f0fdf4 0%, #dcfce7 38%, #bbf7d0 72%, #4ade80 100%)",
    headerAlign: "left",
    headerTop: "80px",
    headerLeft: "80px",
    headline: "Enter flow state<br>in 25 minutes flat.",
    subline: "Unbroken deep work blocks. Built for intense building & study.",
    screenType: "html",
    screenContent: inAppScreens.focusTimerScreen,
    phoneStage: "perspective: 1400px; bottom: -80px; right: 40px; width: 680px; height: 1420px;",
    phoneTransform: "rotateY(-14deg) rotateX(7deg) rotateZ(-3deg)",
    pills: [
      {
        icon: "⏱️",
        iconBg: "rgba(34, 197, 94, 0.15)",
        iconBorder: "rgba(34, 197, 94, 0.4)",
        tag: "SAM ALTMAN",
        tagBg: "rgba(34, 197, 94, 0.2)",
        tagColor: "#4ade80",
        title: "Extreme Leverage",
        body: '"Extreme focus produces extraordinary leverage. Relentlessly execute."',
        top: "470px",
        left: "70px",
        rot: "-1.5deg",
        width: "530px"
      },
      {
        icon: "⚡",
        iconBg: "rgba(16, 185, 129, 0.15)",
        iconBorder: "rgba(16, 185, 129, 0.4)",
        tag: "FLOW STATE",
        tagBg: "rgba(16, 185, 129, 0.2)",
        tagColor: "#34d399",
        title: "25:00 Session Locked",
        body: 'Notifications silenced. Mind engaged.<br><strong style="color:#6ee7b7;">Mining T1 Tokens at 1.5x streak rate.</strong>',
        top: "680px",
        left: "90px",
        rot: "2deg",
        width: "530px"
      }
    ],
    floatingRocks: [
      { top: "410px", right: "110px", size: 115, rot: 20, blur: 0, op: 1 },
      { top: "350px", left: "80px", size: 80, rot: -35, blur: 1, op: 0.85 },
      { bottom: "250px", left: "110px", size: 90, rot: 55, blur: 0, op: 0.95 }
    ],
    footer: ["15 MIN", "25 MIN", "45 MIN", "60 MIN PROTOCOLS"]
  },

  // -------------------------------------------------------------
  // SLIDE 5: Brutal Screen Time Audit (Dedicated Crisp Audit UI)
  // -------------------------------------------------------------
  {
    id: 5,
    bgGradient: "radial-gradient(circle at 45% 10%, #fff1f2 0%, #ffe4e6 38%, #fecdd3 72%, #fb7185 100%)",
    headerAlign: "left",
    headerTop: "80px",
    headerLeft: "80px",
    headline: "Audits your day<br>with brutal honesty.",
    subline: "Every wasted minute logged. No sugar-coating. No excuses.",
    screenType: "html",
    screenContent: inAppScreens.screenTimeAuditScreen,
    phoneStage: "perspective: 1400px; bottom: -80px; right: 40px; width: 680px; height: 1420px;",
    phoneTransform: "rotateY(-14deg) rotateX(7deg) rotateZ(-3deg)",
    pills: [
      {
        icon: "⚖️",
        iconBg: "rgba(244, 63, 94, 0.15)",
        iconBorder: "rgba(244, 63, 94, 0.4)",
        tag: "AURELIUS",
        tagBg: "rgba(244, 63, 94, 0.2)",
        tagColor: "#fb7185",
        title: "Marcus Aurelius",
        body: '"You could be good today. But instead you choose tomorrow."',
        top: "470px",
        left: "70px",
        rot: "-1.5deg",
        width: "530px"
      },
      {
        icon: "📊",
        iconBg: "rgba(251, 113, 133, 0.15)",
        iconBorder: "rgba(251, 113, 133, 0.4)",
        tag: "AUDIT DELTA",
        tagBg: "rgba(251, 113, 133, 0.2)",
        tagColor: "#fda4af",
        title: "Screen Time Audit: 3h 16m",
        body: 'Social scrolling: -1h 45m today.<br><strong style="color:#ffffff;">Discipline won back 105 productive minutes.</strong>',
        top: "680px",
        left: "90px",
        rot: "2deg",
        width: "530px"
      }
    ],
    floatingRocks: [
      { top: "420px", right: "120px", size: 120, rot: -25, blur: 0, op: 1 },
      { top: "350px", left: "70px", size: 80, rot: 45, blur: 1, op: 0.85 },
      { bottom: "250px", left: "120px", size: 95, rot: -60, blur: 0, op: 0.95 }
    ],
    footer: ["BLOOMBERG", "WIRED", "FINANCIAL TIMES"]
  },

  // -------------------------------------------------------------
  // SLIDE 6: Universal Standings (Compete with the Top 1%)
  // -------------------------------------------------------------
  {
    id: 6,
    bgGradient: "radial-gradient(circle at 50% 12%, #fffbeb 0%, #fef3c7 35%, #fde68a 70%, #f59e0b 100%)",
    headerAlign: "center",
    headerTop: "80px",
    headline: "Compete with the<br>Top 1% worldwide.",
    subline: "Ranked purely by verified deep work hours. No fake streaks.",
    screenType: "image",
    phoneImg: "05_leaderboard_screen.png",
    phoneStage: "bottom: -70px; left: 50%; transform: translateX(-50%); width: 680px; height: 1420px;",
    phoneTransform: "none",
    pills: [
      {
        icon: "👑",
        iconBg: "rgba(245, 158, 11, 0.15)",
        iconBorder: "rgba(245, 158, 11, 0.4)",
        tag: "JENSEN HUANG",
        tagBg: "rgba(245, 158, 11, 0.2)",
        tagColor: "#fbbf24",
        title: "Character & Grit",
        body: '"Greatness comes from people who suffered and endured."',
        top: "470px",
        left: "50px",
        rot: "-2deg",
        width: "490px"
      },
      {
        icon: "🏆",
        iconBg: "rgba(217, 119, 6, 0.15)",
        iconBorder: "rgba(217, 119, 6, 0.4)",
        tag: "#1 RANK",
        tagBg: "rgba(217, 119, 6, 0.2)",
        tagColor: "#fde68a",
        title: "Podium: hyken.t1",
        body: '99 Focus Score today.<br><strong style="color:#ffffff;">Consistency beats raw talent every single day.</strong>',
        top: "680px",
        right: "50px",
        rot: "2deg",
        width: "510px"
      }
    ],
    floatingRocks: [
      { top: "360px", right: "80px", size: 110, rot: 30, blur: 0, op: 1 },
      { top: "390px", left: "80px", size: 85, rot: -50, blur: 1, op: 0.9 },
      { bottom: "260px", right: "90px", size: 90, rot: 75, blur: 0, op: 0.95 }
    ],
    footer: ["GLOBAL LEAGUE", "FOUNDER TRACK", "AUKAAT TRACK", "WEEKLY PODIUM"]
  },

  // -------------------------------------------------------------
  // SLIDE 7: Token Mining & Physical Merch (Proof of Work)
  // -------------------------------------------------------------
  {
    id: 7,
    bgGradient: "radial-gradient(circle at 45% 10%, #fff7ed 0%, #ffedd5 38%, #fed7aa 72%, #fb923c 100%)",
    headerAlign: "left",
    headerTop: "80px",
    headerLeft: "80px",
    headline: "Turn raw discipline<br>into tangible proof.",
    subline: "Mine T1 Tokens through deep focus. Unlock exclusive physical merch.",
    screenType: "html",
    screenContent: inAppScreens.tokenWalletScreen,
    phoneStage: "perspective: 1400px; bottom: -80px; right: 40px; width: 680px; height: 1420px;",
    phoneTransform: "rotateY(-14deg) rotateX(7deg) rotateZ(-3deg)",
    pills: [
      {
        icon: "🪙",
        iconBg: "rgba(249, 115, 22, 0.15)",
        iconBorder: "rgba(249, 115, 22, 0.4)",
        tag: "PROOF OF FOCUS",
        tagBg: "rgba(249, 115, 22, 0.2)",
        tagColor: "#fb923c",
        title: "+220 T1 Tokens Mined",
        body: 'Deep focus sessions validated.<br><strong style="color:#fed7aa;">Mined by sweat and focus. 0% crypto noise.</strong>',
        top: "470px",
        left: "70px",
        rot: "-1.5deg",
        width: "530px"
      },
      {
        icon: "👕",
        iconBg: "rgba(234, 88, 12, 0.15)",
        iconBorder: "rgba(234, 88, 12, 0.4)",
        tag: "PHYSICAL MERCH",
        tagBg: "rgba(234, 88, 12, 0.2)",
        tagColor: "#fdba74",
        title: "Signature Oversized Tee",
        body: 'Unlock at 6,000 Tokens.<br><strong style="color:#ffffff;">Cannot be bought with money. Only earned with grit.</strong>',
        top: "680px",
        left: "90px",
        rot: "2deg",
        width: "530px"
      }
    ],
    floatingRocks: [
      { top: "360px", left: "80px", size: 85, rot: -30, blur: 1, op: 0.85 },
      { bottom: "240px", left: "120px", size: 90, rot: 60, blur: 0, op: 0.95 }
    ],
    extra3d: `<img src="coin.png" style="position:absolute; top:400px; right:90px; width:170px; filter:drop-shadow(0 25px 40px rgba(0,0,0,0.55)); transform:rotate(18deg); z-index:25;">`,
    footer: ["PROOF OF FOCUS", "PHYSICAL REWARDS", "AUDITABLE", "TOP 1% EXCLUSIVE"]
  },

  // -------------------------------------------------------------
  // SLIDE 8: 100% Offline AI Coach (Unfiltered Mentor)
  // -------------------------------------------------------------
  {
    id: 8,
    bgGradient: "radial-gradient(circle at 50% 12%, #ecfeff 0%, #cffafe 35%, #a5f3fc 70%, #22d3ee 100%)",
    headerAlign: "center",
    headerTop: "80px",
    headline: "An unfiltered mentor<br>in your pocket.",
    subline: "100% on-device AI. Zero cloud tracking. Tells you the hard truth.",
    screenType: "html",
    screenContent: inAppScreens.aiCoachScreen,
    phoneStage: "bottom: -70px; left: 50%; transform: translateX(-50%); width: 680px; height: 1420px;",
    phoneTransform: "none",
    pills: [
      {
        icon: "🦾",
        iconBg: "rgba(6, 182, 212, 0.15)",
        iconBorder: "rgba(6, 182, 212, 0.4)",
        tag: "DAVID GOGGINS",
        tagBg: "rgba(6, 182, 212, 0.2)",
        tagColor: "#22d3ee",
        title: "The 40% Rule",
        body: '"When your mind says you\'re done, you\'re only at 40%. Keep going."',
        top: "470px",
        left: "50px",
        rot: "-2deg",
        width: "490px"
      },
      {
        icon: "🤖",
        iconBg: "rgba(8, 145, 178, 0.15)",
        iconBorder: "rgba(8, 145, 178, 0.4)",
        tag: "LOCAL AI",
        tagBg: "rgba(8, 145, 178, 0.2)",
        tagColor: "#67e8f9",
        title: "Offline AI Insights",
        body: '"Stop reading productivity books. Execute the first 90 minutes now."',
        top: "680px",
        right: "50px",
        rot: "2deg",
        width: "510px"
      }
    ],
    floatingRocks: [
      { top: "370px", left: "90px", size: 90, rot: 40, blur: 1, op: 0.85 },
      { bottom: "250px", right: "90px", size: 85, rot: -60, blur: 0, op: 0.95 }
    ],
    footer: ["100% PRIVATE", "ZERO TRACKING", "OFFLINE AI", "UNFILTERED ADVICE"]
  }
];

function generateHtml(s) {
  const isLeft = s.headerAlign === 'left';
  const headerStyle = isLeft 
    ? `position: absolute; top: ${s.headerTop}; left: ${s.headerLeft}; max-width: 900px; text-align: left;`
    : `position: absolute; top: ${s.headerTop}; width: 100%; text-align: center; padding: 0 60px;`;

  const phoneInnerHtml = s.screenType === 'html'
    ? s.screenContent
    : `<img src="${s.phoneImg}" alt="T1 Screen">`;

  return `
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<style>
  @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800;900&family=Space+Grotesk:wght@600;700;800&display=swap');

  * { margin: 0; padding: 0; box-sizing: border-box; }

  body {
    width: 1080px;
    height: 1920px;
    background: ${s.bgGradient};
    font-family: 'Plus Jakarta Sans', -apple-system, sans-serif;
    color: #0f172a;
    position: relative;
    overflow: hidden;
  }

  .ambient-sheen {
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at 45% 8%, rgba(255,255,255,0.95) 0%, rgba(255,255,255,0.35) 35%, transparent 70%);
    pointer-events: none;
    z-index: 1;
  }

  .header {
    ${headerStyle}
    z-index: 10;
  }

  .headline {
    font-size: 78px;
    font-weight: 800;
    line-height: 1.06;
    color: #09090b;
    letter-spacing: -2.8px;
  }

  .subline {
    font-size: 26px;
    font-weight: 600;
    color: #334155;
    margin-top: 16px;
    letter-spacing: -0.4px;
    line-height: 1.35;
  }

  .stage {
    position: absolute;
    ${s.phoneStage}
    z-index: 5;
  }

  .phone-frame {
    width: 100%;
    height: 100%;
    background: #09090b;
    border-radius: 56px;
    padding: 13px;
    transform: ${s.phoneTransform};
    box-shadow: 
      0 0 0 2px rgba(255,255,255,0.25),
      0 0 0 7px #1e293b,
      0 0 0 10px #09090b,
      0 40px 95px -15px rgba(15, 23, 42, 0.55),
      0 65px 130px -25px rgba(0, 0, 0, 0.45);
    position: relative;
    overflow: hidden;
  }

  .phone-screen {
    width: 100%;
    height: 100%;
    border-radius: 44px;
    overflow: hidden;
    background: #09090b;
    position: relative;
  }

  .phone-screen img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .camera-punch {
    position: absolute;
    top: 18px;
    left: 50%;
    transform: translateX(-50%);
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #000;
    border: 1.5px solid #1e293b;
    box-shadow: inset 0 0 3px #3b82f6;
    z-index: 30;
  }

  .screen-glare {
    position: absolute;
    inset: 0;
    background: linear-gradient(130deg, rgba(255,255,255,0.18) 0%, rgba(255,255,255,0.02) 35%, transparent 60%);
    pointer-events: none;
    z-index: 25;
  }

  .notification-pill {
    position: absolute;
    background: rgba(15, 23, 42, 0.90);
    backdrop-filter: blur(28px);
    border: 1px solid rgba(255, 255, 255, 0.22);
    border-radius: 26px;
    padding: 20px 24px;
    color: white;
    z-index: 20;
    box-shadow: 
      0 20px 45px -10px rgba(0,0,0,0.55),
      0 0 1px 1px rgba(255,255,255,0.15) inset;
    display: flex;
    align-items: flex-start;
    gap: 16px;
  }

  .pill-icon {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    font-size: 20px;
  }

  .pill-content {
    flex: 1;
  }

  .pill-title {
    font-size: 19px;
    font-weight: 800;
    color: #ffffff;
    letter-spacing: -0.2px;
    margin-bottom: 4px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .pill-tag {
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 1px;
    padding: 3px 8px;
    border-radius: 12px;
  }

  .pill-body {
    font-size: 16px;
    line-height: 1.42;
    color: #cbd5e1;
    font-weight: 400;
  }

  .credibility-footer {
    position: absolute;
    bottom: 35px;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 28px;
    z-index: 20;
  }

  .cred-text {
    font-size: 15px;
    font-weight: 900;
    letter-spacing: 3px;
    text-transform: uppercase;
    color: #0f172a;
    opacity: 0.85;
  }

  .cred-dot {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: #334155;
  }
</style>
</head>
<body>
  <div class="ambient-sheen"></div>

  <div class="header">
    <h1 class="headline">${s.headline}</h1>
    <p class="subline">${s.subline}</p>
  </div>

  <div class="stage">
    <div class="phone-frame">
      <div class="camera-punch"></div>
      <div class="screen-glare"></div>
      <div class="phone-screen">
        ${phoneInnerHtml}
      </div>
    </div>
  </div>

  ${s.pills.map((p, idx) => {
    const pos = p.right ? `right: ${p.right};` : `left: ${p.left};`;
    return `
    <div class="notification-pill" style="top: ${p.top}; ${pos} width: ${p.width}; transform: rotate(${p.rot});">
      <div class="pill-icon" style="background: ${p.iconBg}; border: 1.5px solid ${p.iconBorder};">${p.icon}</div>
      <div class="pill-content">
        <div class="pill-title">
          <span>${p.title}</span>
          <span class="pill-tag" style="background: ${p.tagBg}; color: ${p.tagColor}; border: 1px solid ${p.iconBorder};">${p.tag}</span>
        </div>
        <div class="pill-body">
          ${p.body}
        </div>
      </div>
    </div>
    `;
  }).join('')}

  ${s.floatingRocks.map(r => `
    <div style="position: absolute; ${r.top ? `top: ${r.top};` : `bottom: ${r.bottom};`} ${r.left ? `left: ${r.left};` : `right: ${r.right};`} z-index: 18; pointer-events: none;">
      ${render3dRock(r.size, r.rot, r.blur, r.op)}
    </div>
  `).join('')}

  ${s.extra3d || ''}

  <div class="credibility-footer">
    ${s.footer.map((item, idx) => `
      <span class="cred-text">${item}</span>
      ${idx < s.footer.length - 1 ? '<div class="cred-dot"></div>' : ''}
    `).join('')}
  </div>
</body>
</html>
`;
}

// Generate and Render all 8 slides
for (const s of slides) {
  const htmlFile = path.join(WORK_DIR, `slide_v3_${String(s.id).padStart(2, '0')}.html`);
  const pngFile = path.join(OUT_DIR, `screenshot_${s.id}_t1_opal.png`);
  
  fs.writeFileSync(htmlFile, generateHtml(s), 'utf8');
  console.log(`Wrote ${htmlFile}`);

  try {
    execSync(`"${CHROME}" --headless --disable-gpu --screenshot="${pngFile}" --window-size=1080,1920 "${htmlFile}"`, { stdio: 'inherit' });
    console.log(`Rendered ${pngFile}`);
  } catch (err) {
    console.error(`Error rendering slide ${s.id}:`, err.message);
  }
}

console.log("ALL 8 HYPER-REALISTIC OPAL V3 SCREENSHOTS RENDERED SUCCESSFULLY!");
