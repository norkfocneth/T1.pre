const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const CHROME = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const WORK_DIR = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\opal_style";
const OUT_DIR = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\opal_final_screenshots";

if (!fs.existsSync(OUT_DIR)) {
  fs.mkdirSync(OUT_DIR, { recursive: true });
}

const slides = [
  {
    id: 1,
    bgGradient: "radial-gradient(circle at 50% 16%, #dbeafe 0%, #bfdbfe 35%, #93c5fd 65%, #60a5fa 100%)",
    headline: "Built for the Top 1%.<br>Not the masses.",
    subline: "The operating system for founders, builders & elite minds.",
    phoneImg: "01_home_focus_ring.png",
    phoneRotate: "-3deg",
    phoneOffsetY: "40px",
    card1: {
      icon: "⚡",
      iconColor: "#ef4444",
      iconBg: "rgba(239,68,68,0.2)",
      title: "Doomscrolling Alert!",
      body: "You've been on Instagram for 35 min.<br><strong>Your competition didn't take today off.</strong>",
      top: "460px",
      left: "60px",
      rot: "-2deg"
    },
    card2: {
      icon: "👑",
      iconColor: "#3b82f6",
      iconBg: "rgba(59,130,246,0.2)",
      title: "Top 1% Mindset",
      body: "+15 Focus Points away from #1 Rank.<br><strong>While they scroll reels, you build equity.</strong>",
      top: "660px",
      left: "100px",
      rot: "1.5deg"
    },
    floating3d: `<img src="coin.png" style="position:absolute; top:390px; right:70px; width:150px; filter:drop-shadow(0 25px 35px rgba(0,0,0,0.5)); transform:rotate(15deg); z-index:25;">`,
    footer: ["FOUNDERS", "UPSC & CA", "JEE & NEET", "BUILDERS"]
  },
  {
    id: 2,
    bgGradient: "radial-gradient(circle at 50% 16%, #ede9fe 0%, #ddd6fe 35%, #c4b5fd 65%, #a78bfa 100%)",
    headline: "Measure your focus.<br>Not just screen time.",
    subline: "Your real-time cognitive score from 0 to 100.",
    phoneImg: "01_home_focus_ring.png",
    phoneRotate: "0deg",
    phoneOffsetY: "50px",
    card1: {
      icon: "📈",
      iconColor: "#8b5cf6",
      iconBg: "rgba(139,92,246,0.2)",
      title: "Focus Score: 85/100",
      body: "Ahead of 85% of users today.<br><strong>High-agency cognitive execution active.</strong>",
      top: "470px",
      left: "70px",
      rot: "-2.5deg"
    },
    card2: {
      icon: "⚡",
      iconColor: "#10b981",
      iconBg: "rgba(16,185,129,0.2)",
      title: "Time Saved vs Yesterday",
      body: "+8h 16m won back from distraction.<br><strong>Discipline is the rarest commodity.</strong>",
      top: "670px",
      right: "70px",
      rot: "2deg"
    },
    floating3d: `<div style="position:absolute; top:360px; right:80px; width:130px; height:130px; border-radius:50%; border:4px solid #8b5cf6; box-shadow:0 0 40px #8b5cf6, inset 0 0 30px #8b5cf6; z-index:25; display:flex; align-items:center; justify-content:center; color:#fff; font-size:42px; font-weight:800; background:rgba(15,23,42,0.6); backdrop-filter:blur(10px);">85</div>`,
    footer: ["BEHAVIOUR SCORE", "CONFIDENCE", "DAILY TREND", "TOP PERFORMER"]
  },
  {
    id: 3,
    bgGradient: "radial-gradient(circle at 50% 16%, #f1f5f9 0%, #e2e8f0 35%, #cbd5e1 65%, #94a3b8 100%)",
    headline: "Locks the apps<br>you keep opening.",
    subline: "Past you set the rules. Future you wins.",
    isBlockedScreen: true,
    phoneRotate: "0deg",
    phoneOffsetY: "30px",
    card1: {
      icon: "🚫",
      iconColor: "#ef4444",
      iconBg: "rgba(239,68,68,0.2)",
      title: "Dopamine Loop Broken",
      body: "Attempted to open Instagram.<br><strong>No negotiations with your future self.</strong>",
      top: "480px",
      left: "60px",
      rot: "-1.5deg"
    },
    card2: {
      icon: "🛡️",
      iconColor: "#3b82f6",
      iconBg: "rgba(59,130,246,0.2)",
      title: "Monk Mode Shield",
      body: "Distractions eliminated until 18:00.<br><strong>Deep work flow protected.</strong>",
      top: "680px",
      right: "60px",
      rot: "2deg"
    },
    floating3d: `<img src="shield.png" style="position:absolute; top:370px; right:60px; width:160px; filter:drop-shadow(0 25px 35px rgba(0,0,0,0.5)); transform:rotate(12deg); z-index:25;">`,
    footer: ["RADICAL ELIMINATION", "MONK MODE", "NO CHEATS", "ZERO COMPROMISE"]
  },
  {
    id: 4,
    bgGradient: "radial-gradient(circle at 50% 16%, #ecfdf5 0%, #d1fae5 35%, #a7f3d0 65%, #6ee7b7 100%)",
    headline: "Enter deep work<br>in 25 minutes flat.",
    subline: "Unbroken flow states. Designed for intense study & building.",
    phoneImg: "03_focus_timer_screen.png",
    phoneRotate: "3deg",
    phoneOffsetY: "40px",
    card1: {
      icon: "⏱️",
      iconColor: "#10b981",
      iconBg: "rgba(16,185,129,0.2)",
      title: "Focus Session Active",
      body: "25:00 countdown engaged.<br><strong>Phone silenced. Mind locked in.</strong>",
      top: "470px",
      left: "70px",
      rot: "-2deg"
    },
    card2: {
      icon: "⚡",
      iconColor: "#059669",
      iconBg: "rgba(5,150,105,0.2)",
      title: "Streak Multiplier x1.5",
      body: "3 consecutive sessions today.<br><strong>Mining T1 Focus Tokens at maximum rate.</strong>",
      top: "670px",
      left: "110px",
      rot: "1deg"
    },
    floating3d: `<div style="position:absolute; top:390px; right:80px; width:120px; height:120px; border-radius:50%; background:#10b981; box-shadow:0 0 50px rgba(16,185,129,0.6); display:flex; align-items:center; justify-content:center; z-index:25;"><div style="width:0; height:0; border-top:20px solid transparent; border-bottom:20px solid transparent; border-left:32px solid #000; margin-left:8px;"></div></div>`,
    footer: ["15 MIN", "25 MIN", "45 MIN", "60 MIN PRESETS"]
  },
  {
    id: 5,
    bgGradient: "radial-gradient(circle at 50% 16%, #fce7f3 0%, #fbcfe8 35%, #f472b6 65%, #ec4899 100%)",
    headline: "Audits your day<br>with brutal honesty.",
    subline: "Track every second across Productivity, Social & Education.",
    phoneImg: "02_home_screen_time_breakdown.png",
    phoneRotate: "-2deg",
    phoneOffsetY: "40px",
    card1: {
      icon: "📊",
      iconColor: "#ec4899",
      iconBg: "rgba(236,72,153,0.2)",
      title: "Screen Time Audit: 3h 16m",
      body: "Instagram: -45 min less than yesterday.<br><strong>Deep Work: +2h 10m gain.</strong>",
      top: "470px",
      left: "60px",
      rot: "-1.5deg"
    },
    card2: {
      icon: "💡",
      iconColor: "#8b5cf6",
      iconBg: "rgba(139,92,246,0.2)",
      title: "Billionaire Truth",
      body: "You don't lack time.<br><strong>You lack radical focus.</strong>",
      top: "670px",
      right: "60px",
      rot: "2deg"
    },
    floating3d: `<div style="position:absolute; top:380px; right:80px; background:rgba(15,23,42,0.9); backdrop-filter:blur(20px); border:1px solid rgba(255,255,255,0.2); border-radius:20px; padding:16px 20px; color:#fff; z-index:25; box-shadow:0 20px 40px rgba(0,0,0,0.4);"><span style="color:#22c55e; font-weight:800; font-size:22px;">↓ 45 min</span><br><span style="font-size:14px; color:#94a3b8;">Instagram vs yesterday</span></div>`,
    footer: ["PRODUCTIVITY", "COMMUNICATION", "SOCIAL TIME", "UTILITY"]
  },
  {
    id: 6,
    bgGradient: "radial-gradient(circle at 50% 16%, #fef3c7 0%, #fde68a 35%, #fcd34d 65%, #f59e0b 100%)",
    headline: "Compete with the Top 1%<br>across the globe.",
    subline: "Founders, JEE, NEET, UPSC & CA leaders on one podium.",
    phoneImg: "05_leaderboard_screen.png",
    phoneRotate: "0deg",
    phoneOffsetY: "30px",
    card1: {
      icon: "👑",
      iconColor: "#f59e0b",
      iconBg: "rgba(245,158,11,0.2)",
      title: "Podium Rank #1",
      body: "hyken.t1 holds #1 with 99 Focus Score.<br><strong>Consistency beats raw talent every single day.</strong>",
      top: "470px",
      left: "60px",
      rot: "-2deg"
    },
    card2: {
      icon: "🏆",
      iconColor: "#3b82f6",
      iconBg: "rgba(59,130,246,0.2)",
      title: "Segmented Leaderboards",
      body: "Global • Founder • Aukaat Aspirants.<br><strong>Measure yourself against the apex grinders.</strong>",
      top: "670px",
      right: "60px",
      rot: "2deg"
    },
    floating3d: `<div style="position:absolute; top:360px; right:80px; width:130px; height:130px; border-radius:50%; background:linear-gradient(135deg, #f59e0b, #d97706); box-shadow:0 0 50px rgba(245,158,11,0.6); display:flex; flex-direction:column; align-items:center; justify-content:center; color:#fff; z-index:25;"><span style="font-size:36px;">👑</span><span style="font-weight:900; font-size:20px; letter-spacing:1px;">#1 RANK</span></div>`,
    footer: ["GLOBAL STANDINGS", "FOUNDER TRACK", "AUKAAT TRACK", "WEEKLY PODIUM"]
  },
  {
    id: 7,
    bgGradient: "radial-gradient(circle at 50% 16%, #ffedd5 0%, #fed7aa 35%, #fdba74 65%, #fb923c 100%)",
    headline: "Turn raw discipline<br>into tangible proof.",
    subline: "Mine T1 Tokens through deep focus. Unlock exclusive physical merch.",
    phoneImg: "06_token_wallet_screen.png",
    phoneRotate: "-3deg",
    phoneOffsetY: "40px",
    card1: {
      icon: "🪙",
      iconColor: "#f97316",
      iconBg: "rgba(249,115,22,0.2)",
      title: "+25 T1 Tokens Earned",
      body: "Deep focus session validated.<br><strong>Proof of work permanently recorded.</strong>",
      top: "460px",
      left: "60px",
      rot: "-2deg"
    },
    card2: {
      icon: "👕",
      iconColor: "#ea580c",
      iconBg: "rgba(234,88,12,0.2)",
      title: "T1 Signature Oversized Tee",
      body: "Unlock at 6,000 Tokens.<br><strong>Cannot be bought with money. Only earned with grit.</strong>",
      top: "660px",
      left: "100px",
      rot: "1.5deg"
    },
    floating3d: `<img src="coin.png" style="position:absolute; top:380px; right:60px; width:160px; filter:drop-shadow(0 25px 40px rgba(0,0,0,0.5)); transform:rotate(-15deg); z-index:25;">`,
    footer: ["PROOF OF FOCUS", "PHYSICAL REWARDS", "AUDITABLE", "0% CRYPTO NOISE"]
  },
  {
    id: 8,
    bgGradient: "radial-gradient(circle at 50% 16%, #cffafe 0%, #a5f3fc 35%, #67e8f9 65%, #22d3ee 100%)",
    headline: "An unfiltered mentor<br>in your pocket.",
    subline: "100% on-device AI that tells you the hard truth about your habits.",
    phoneImg: "04_ai_coach_screen.png",
    phoneRotate: "0deg",
    phoneOffsetY: "30px",
    card1: {
      icon: "🤖",
      iconColor: "#06b6d4",
      iconBg: "rgba(6,182,212,0.2)",
      title: "Offline AI Insights Engine",
      body: "\"Ready for a breakthrough? Stop planning. Start executing.\"<br><strong>Zero cloud tracking. 100% on-device privacy.</strong>",
      top: "470px",
      left: "60px",
      rot: "-2deg"
    },
    card2: {
      icon: "⚡",
      iconColor: "#0891b2",
      iconBg: "rgba(8,145,178,0.2)",
      title: "Tonight's Focus Forecast",
      body: "Avoid 20 min of social media tonight.<br><strong>Unlock Top 20% Rank by morning.</strong>",
      top: "670px",
      right: "60px",
      rot: "2deg"
    },
    floating3d: `<div style="position:absolute; top:360px; right:70px; width:120px; height:120px; border-radius:50%; overflow:hidden; border:3px solid #22d3ee; box-shadow:0 0 40px rgba(34,211,238,0.6); z-index:25;"><img src="robot.jpg" style="width:100%; height:100%; object-fit:cover;"></div>`,
    footer: ["100% PRIVATE", "ZERO TRACKING", "OFFLINE AI", "UNFILTERED ADVICE"]
  }
];

function generateHtml(s) {
  const card1Right = s.card1.right ? `right: ${s.card1.right};` : `left: ${s.card1.left};`;
  const card2Right = s.card2.right ? `right: ${s.card2.right};` : `left: ${s.card2.left};`;

  const phoneInnerHtml = s.isBlockedScreen ? `
    <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; align-items:center; justify-content:center; padding:40px; text-align:center;">
      <div style="width:100px; height:100px; border-radius:26px; background:linear-gradient(45deg, #f09433, #e6683c, #dc2743, #cc2366, #bc1888); display:flex; align-items:center; justify-content:center; margin-bottom:32px; box-shadow:0 20px 40px rgba(220,39,67,0.35);">
        <div style="width:52px; height:52px; border:4.5px solid white; border-radius:14px; position:relative; display:flex; align-items:center; justify-content:center;">
          <div style="width:20px; height:20px; border:4.5px solid white; border-radius:50%;"></div>
          <div style="width:6px; height:6px; background:white; border-radius:50%; position:absolute; top:4px; right:4px;"></div>
        </div>
      </div>
      <h2 style="font-size:34px; font-weight:800; color:#fff; margin-bottom:12px; letter-spacing:-0.5px;">Instagram is<br>Blocked by T1.</h2>
      <p style="font-size:18px; color:#94a3b8; line-height:1.45; margin-bottom:48px; max-width:380px;">You set this rule this morning.<br><strong style="color:#fff;">Past you was right.</strong></p>
      <div style="width:100%; max-width:360px; height:64px; border-radius:32px; background:#fff; color:#000; font-weight:800; font-size:18px; display:flex; align-items:center; justify-content:center; letter-spacing:0.5px; box-shadow:0 15px 30px rgba(255,255,255,0.25);">Dismiss (Stay Locked In)</div>
      <span style="font-size:12px; color:#64748b; margin-top:24px; letter-spacing:1px; text-transform:uppercase; font-weight:700;">Locked by T1 Monk Mode</span>
    </div>
  ` : `
    <img src="${s.phoneImg}" style="width:100%; height:100%; object-fit:cover;" alt="T1 Screen">
  `;

  return `
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<style>
  @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=Space+Grotesk:wght@500;700;800&display=swap');

  * { margin: 0; padding: 0; box-sizing: border-box; }

  body {
    width: 1080px;
    height: 1920px;
    background: ${s.bgGradient};
    font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif;
    color: #0f172a;
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    overflow: hidden;
  }

  /* Ambient light overlay */
  .bg-ambient {
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at 50% 5%, rgba(255,255,255,0.85) 0%, transparent 45%),
                linear-gradient(180deg, rgba(255,255,255,0.4) 0%, transparent 50%, rgba(0,0,0,0.15) 100%);
    z-index: 1;
    pointer-events: none;
  }

  /* Header Section (Opal Style) */
  .header {
    position: relative;
    z-index: 10;
    text-align: center;
    margin-top: 100px;
    padding: 0 60px;
  }

  .headline {
    font-size: 72px;
    font-weight: 800;
    line-height: 1.06;
    color: #09090b;
    letter-spacing: -2.5px;
  }

  .subline {
    font-size: 26px;
    font-weight: 500;
    color: #334155;
    margin-top: 16px;
    letter-spacing: -0.5px;
    line-height: 1.35;
  }

  /* Phone Stage */
  .stage {
    position: absolute;
    bottom: -60px;
    width: 100%;
    height: 1380px;
    display: flex;
    justify-content: center;
    z-index: 5;
  }

  /* 3D Smartphone Frame */
  .phone-frame {
    width: 670px;
    height: 1400px;
    background: #09090b;
    border-radius: 56px;
    padding: 14px;
    box-shadow: 
      0 0 0 3px #3f3f46,
      0 0 0 8px #09090b,
      0 40px 90px -15px rgba(15, 23, 42, 0.45),
      0 60px 120px -25px rgba(0, 0, 0, 0.35);
    position: relative;
    transform: rotate(${s.phoneRotate}) translateY(${s.phoneOffsetY});
  }

  .phone-screen {
    width: 100%;
    height: 100%;
    border-radius: 44px;
    overflow: hidden;
    background: #000;
    position: relative;
  }

  /* Floating Notification Cards (Opal signature look) */
  .floating-card {
    position: absolute;
    width: 480px;
    background: rgba(15, 23, 42, 0.90);
    backdrop-filter: blur(30px);
    border: 1px solid rgba(255, 255, 255, 0.18);
    border-radius: 26px;
    padding: 22px 24px;
    color: white;
    z-index: 20;
    box-shadow: 0 30px 60px -12px rgba(0,0,0,0.55);
  }

  .card-1 {
    top: ${s.card1.top};
    ${card1Right}
    transform: rotate(${s.card1.rot});
  }

  .card-2 {
    top: ${s.card2.top};
    ${card2Right}
    transform: rotate(${s.card2.rot});
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
  }

  .card-icon {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    background: ${s.card1.iconBg};
    border: 1.5px solid ${s.card1.iconColor};
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
  }

  .card-2 .card-icon {
    background: ${s.card2.iconBg};
    border-color: ${s.card2.iconColor};
  }

  .card-title {
    font-size: 19px;
    font-weight: 700;
    color: #f8fafc;
    letter-spacing: -0.3px;
  }

  .card-body {
    font-size: 16.5px;
    line-height: 1.4;
    color: #cbd5e1;
    font-weight: 400;
  }

  .card-body strong {
    color: #fff;
    font-weight: 700;
  }

  /* Floating 3D Shards (Opal signature stones) */
  .shard {
    position: absolute;
    background: linear-gradient(135deg, #475569 0%, #0f172a 100%);
    box-shadow: 0 15px 30px rgba(0,0,0,0.35);
    z-index: 15;
  }

  .shard-1 {
    width: 45px;
    height: 55px;
    top: 420px;
    left: 70px;
    clip-path: polygon(30% 0%, 90% 20%, 100% 70%, 60% 100%, 0% 70%);
    transform: rotate(25deg);
  }

  .shard-2 {
    width: 35px;
    height: 42px;
    top: 610px;
    right: 110px;
    clip-path: polygon(50% 0%, 100% 50%, 70% 100%, 0% 80%);
    transform: rotate(-35deg);
  }

  .shard-3 {
    width: 55px;
    height: 65px;
    bottom: 280px;
    right: 70px;
    clip-path: polygon(20% 0%, 100% 30%, 80% 100%, 10% 90%);
    transform: rotate(15deg);
  }

  /* Bottom Credibility / Press Bar */
  .footer-bar {
    position: absolute;
    bottom: 45px;
    z-index: 20;
    display: flex;
    align-items: center;
    gap: 24px;
    font-size: 15px;
    font-weight: 800;
    color: #1e293b;
    letter-spacing: 2.5px;
    text-transform: uppercase;
    opacity: 0.85;
  }

  .footer-divider {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: #475569;
  }
</style>
</head>
<body>
  <div class="bg-ambient"></div>

  <!-- Header -->
  <div class="header">
    <h1 class="headline">${s.headline}</h1>
    <p class="subline">${s.subline}</p>
  </div>

  <!-- Phone Stage -->
  <div class="stage">
    <div class="phone-frame">
      <div class="phone-screen">
        ${phoneInnerHtml}
      </div>
    </div>
  </div>

  <!-- Floating Cards (Opal signature style) -->
  <div class="floating-card card-1">
    <div class="card-header">
      <div class="card-icon">${s.card1.icon}</div>
      <div class="card-title">${s.card1.title}</div>
    </div>
    <div class="card-body">
      ${s.card1.body}
    </div>
  </div>

  <div class="floating-card card-2">
    <div class="card-header">
      <div class="card-icon">${s.card2.icon}</div>
      <div class="card-title">${s.card2.title}</div>
    </div>
    <div class="card-body">
      ${s.card2.body}
    </div>
  </div>

  <!-- Floating 3D Elements -->
  ${s.floating3d}

  <!-- Floating Shards -->
  <div class="shard shard-1"></div>
  <div class="shard shard-2"></div>
  <div class="shard shard-3"></div>

  <!-- Bottom Credibility Bar -->
  <div class="footer-bar">
    ${s.footer.map((item, idx) => `
      <span>${item}</span>
      ${idx < s.footer.length - 1 ? '<div class="footer-divider"></div>' : ''}
    `).join('')}
  </div>
</body>
</html>
`;
}

// Generate all 8 HTML files and render them
for (const s of slides) {
  const htmlFile = path.join(WORK_DIR, `slide_${String(s.id).padStart(2, '0')}.html`);
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

console.log("ALL 8 OPAL STYLE SCREENSHOTS GENERATED SUCCESSFULLY!");
