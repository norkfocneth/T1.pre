const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const CHROME = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const WORK_DIR = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\opal_style";
const OUT_DIR = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\opal_final_screenshots";

if (!fs.existsSync(OUT_DIR)) {
  fs.mkdirSync(OUT_DIR, { recursive: true });
}

// 3D Faceted Obsidian Rock SVG generator
function render3dRock(size, rotate, blur = 0, opacity = 1) {
  return `
  <svg width="${size}" height="${size}" viewBox="0 0 100 100" style="filter: drop-shadow(0 18px 28px rgba(0,0,0,0.45)) ${blur > 0 ? `blur(${blur}px)` : ''}; transform: rotate(${rotate}deg); opacity: ${opacity};">
    <defs>
      <linearGradient id="rf1" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#475569" />
        <stop offset="100%" stop-color="#1e293b" />
      </linearGradient>
      <linearGradient id="rf2" x1="0%" y1="100%" x2="100%" y2="0%">
        <stop offset="0%" stop-color="#334155" />
        <stop offset="100%" stop-color="#0f172a" />
      </linearGradient>
      <linearGradient id="rspec" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#94a3b8" />
        <stop offset="100%" stop-color="#475569" />
      </linearGradient>
    </defs>
    <polygon points="50,10 78,28 55,48 24,32" fill="url(#rspec)" />
    <polygon points="24,32 55,48 42,88 12,65" fill="url(#rf1)" />
    <polygon points="55,48 78,28 92,60 68,92 42,88" fill="url(#rf2)" />
    <polygon points="42,88 68,92 50,96 22,82" fill="#09090b" opacity="0.8" />
    <polyline points="50,10 55,48 42,88" stroke="rgba(255,255,255,0.4)" stroke-width="1.2" fill="none" />
    <polyline points="24,32 55,48 78,28" stroke="rgba(255,255,255,0.3)" stroke-width="1" fill="none" />
  </svg>
  `;
}

// 8 Unique Opal-Inspired Slides
const slides = [
  // -------------------------------------------------------------
  // SLIDE 1: T1 Blocks Distractions For You
  // -------------------------------------------------------------
  {
    id: 1,
    bgGradient: "radial-gradient(circle at 45% 10%, #e0f2fe 0%, #bae6fd 38%, #93c5fd 72%, #60a5fa 100%)",
    headerAlign: "left",
    headerTop: "90px",
    headerLeft: "80px",
    headline: "T1 blocks<br>distractions for you.",
    subline: "Built for founders, builders & top 1% aspirants.",
    phoneImg: "01_home_focus_ring.png",
    phoneStage: "perspective: 1400px; bottom: -80px; right: 40px; width: 680px; height: 1420px;",
    phoneTransform: "rotateY(-14deg) rotateX(7deg) rotateZ(-3deg)",
    pill1: {
      icon: "⚠️",
      iconBg: "rgba(239, 68, 68, 0.15)",
      iconBorder: "rgba(239, 68, 68, 0.4)",
      tag: "INSTAGRAM",
      tagBg: "rgba(239, 68, 68, 0.2)",
      tagColor: "#f87171",
      title: "Doomscrolling Alert!",
      body: 'You\'ve been scrolling for 35 min.<br><strong style="color:#fca5a5;">"Your competition didn\'t take today off."</strong>',
      top: "490px",
      left: "70px",
      rot: "-1.5deg",
      width: "530px"
    },
    pill2: {
      icon: "⚡",
      iconBg: "rgba(56, 189, 248, 0.15)",
      iconBorder: "rgba(56, 189, 248, 0.4)",
      tag: "MINDSET",
      tagBg: "rgba(56, 189, 248, 0.2)",
      tagColor: "#38bdf8",
      title: "Naval Ravikant",
      body: 'Free education is abundant; desire is scarce.<br><strong style="color:#7dd3fc;">Outwork the distracted 99%.</strong>',
      top: "700px",
      left: "90px",
      rot: "2deg",
      width: "530px"
    },
    extraElements: `
      <div style="position:absolute; top:420px; right:120px; z-index:18;">${render3dRock(120, 25, 0, 1)}</div>
      <div style="position:absolute; top:360px; left:80px; z-index:18;">${render3dRock(80, -45, 1, 0.85)}</div>
      <div style="position:absolute; bottom:240px; left:120px; z-index:18;">${render3dRock(95, 60, 0, 0.95)}</div>
    `,
    footer: ["THE WALL STREET JOURNAL", "TECHCRUNCH", "FORBES"]
  },

  // -------------------------------------------------------------
  // SLIDE 2: Cognitive Focus Score (0 to 100)
  // -------------------------------------------------------------
  {
    id: 2,
    bgGradient: "radial-gradient(circle at 50% 12%, #faf5ff 0%, #f3e8ff 35%, #ddd6fe 70%, #c4b5fd 100%)",
    headerAlign: "center",
    headerTop: "90px",
    headline: "Measure your focus.<br>Not just screen time.",
    subline: "Your daily cognitive score. Calibrated against the top 1%.",
    phoneImg: "01_home_focus_ring.png",
    phoneStage: "bottom: -70px; left: 50%; transform: translateX(-50%); width: 680px; height: 1420px;",
    phoneTransform: "none",
    pill1: {
      icon: "📈",
      iconBg: "rgba(168, 85, 247, 0.15)",
      iconBorder: "rgba(168, 85, 247, 0.4)",
      tag: "SCORE 85",
      tagBg: "rgba(168, 85, 247, 0.2)",
      tagColor: "#c084fc",
      title: "Top 1% Focus Tier",
      body: 'Ahead of 94% of users today.<br><strong style="color:#d8b4fe;">High-agency cognitive execution active.</strong>',
      top: "470px",
      left: "60px",
      rot: "-2.5deg",
      width: "500px"
    },
    pill2: {
      icon: "💡",
      iconBg: "rgba(245, 158, 11, 0.15)",
      iconBorder: "rgba(245, 158, 11, 0.4)",
      tag: "STEVE JOBS",
      tagBg: "rgba(245, 158, 11, 0.2)",
      tagColor: "#fbbf24",
      title: "The Focus Rule",
      body: '"Deciding what NOT to do is as important as deciding what to do."',
      top: "680px",
      right: "60px",
      rot: "2deg",
      width: "510px"
    },
    extraElements: `
      <div style="position:absolute; top:360px; right:90px; z-index:18;">${render3dRock(110, -25, 0, 1)}</div>
      <div style="position:absolute; bottom:260px; right:100px; z-index:18;">${render3dRock(85, 45, 1, 0.9)}</div>
      <div style="position:absolute; top:420px; left:90px; z-index:18;">${render3dRock(75, 15, 0, 0.85)}</div>
    `,
    footer: ["STANFORD NEUROSCIENCE", "Y COMBINATOR FOUNDERS", "HARVARD"]
  },

  // -------------------------------------------------------------
  // SLIDE 3: Locks The Apps You Keep Opening (The Blocked Screen)
  // -------------------------------------------------------------
  {
    id: 3,
    bgGradient: "radial-gradient(circle at 50% 12%, #f8fafc 0%, #f1f5f9 35%, #e2e8f0 70%, #94a3b8 100%)",
    headerAlign: "center",
    headerTop: "90px",
    headline: "Locks the apps<br>you keep opening.",
    subline: "Zero negotiations. Past you set the lock. Future you wins.",
    isBlockedScreen: true,
    phoneStage: "bottom: -70px; left: 50%; transform: translateX(-50%); width: 680px; height: 1420px;",
    phoneTransform: "none",
    pill1: {
      icon: "🚫",
      iconBg: "rgba(239, 68, 68, 0.15)",
      iconBorder: "rgba(239, 68, 68, 0.4)",
      tag: "MONK MODE",
      tagBg: "rgba(239, 68, 68, 0.2)",
      tagColor: "#f87171",
      title: "Dopamine Loop Broken",
      body: 'Attempted to open Instagram.<br><strong style="color:#ffffff;">The barrier held. Flow state protected.</strong>',
      top: "470px",
      left: "60px",
      rot: "-2deg",
      width: "490px"
    },
    pill2: {
      icon: "🛡️",
      iconBg: "rgba(59, 130, 246, 0.15)",
      iconBorder: "rgba(59, 130, 246, 0.4)",
      tag: "DISCIPLINE",
      tagBg: "rgba(59, 130, 246, 0.2)",
      tagColor: "#60a5fa",
      title: "Hard Truth",
      body: '"The temptation to quit is greatest right before the breakthrough."',
      top: "680px",
      right: "60px",
      rot: "2deg",
      width: "500px"
    },
    extraElements: `
      <div style="position:absolute; top:360px; left:90px; z-index:18;">${render3dRock(100, 30, 0, 1)}</div>
      <div style="position:absolute; top:390px; right:90px; z-index:18;">${render3dRock(90, -40, 0, 1)}</div>
      <div style="position:absolute; bottom:250px; left:80px; z-index:18;">${render3dRock(70, 70, 1, 0.8)}</div>
    `,
    footer: ["RADICAL ELIMINATION", "MONK MODE PROTOCOL", "ZERO COMPROMISE"]
  },

  // -------------------------------------------------------------
  // SLIDE 4: Deep Work Session Timer (Flow State in 25 Min)
  // -------------------------------------------------------------
  {
    id: 4,
    bgGradient: "radial-gradient(circle at 55% 10%, #f0fdf4 0%, #dcfce7 38%, #bbf7d0 72%, #4ade80 100%)",
    headerAlign: "left",
    headerTop: "90px",
    headerLeft: "80px",
    headline: "Enter flow state<br>in 25 minutes flat.",
    subline: "Unbroken deep work blocks. Built for coding, studying & building.",
    phoneImg: "03_focus_timer_screen.png",
    phoneStage: "perspective: 1400px; bottom: -80px; right: 40px; width: 680px; height: 1420px;",
    phoneTransform: "rotateY(-12deg) rotateX(6deg) rotateZ(-2.5deg)",
    pill1: {
      icon: "⏱️",
      iconBg: "rgba(34, 197, 94, 0.15)",
      iconBorder: "rgba(34, 197, 94, 0.4)",
      tag: "SAM ALTMAN",
      tagBg: "rgba(34, 197, 94, 0.2)",
      tagColor: "#4ade80",
      title: "Extreme Leverage",
      body: '"Extreme focus produces extraordinary leverage. Relentlessly execute."',
      top: "490px",
      left: "70px",
      rot: "-1.5deg",
      width: "530px"
    },
    pill2: {
      icon: "⚡",
      iconBg: "rgba(16, 185, 129, 0.15)",
      iconBorder: "rgba(16, 185, 129, 0.4)",
      tag: "FLOW STATE",
      tagBg: "rgba(16, 185, 129, 0.2)",
      tagColor: "#34d399",
      title: "25:00 Session Locked",
      body: 'Notifications silenced. Mind engaged.<br><strong style="color:#6ee7b7;">Mining T1 Tokens at 1.5x streak rate.</strong>',
      top: "700px",
      left: "90px",
      rot: "2deg",
      width: "530px"
    },
    extraElements: `
      <div style="position:absolute; top:410px; right:110px; z-index:18;">${render3dRock(115, 20, 0, 1)}</div>
      <div style="position:absolute; top:350px; left:80px; z-index:18;">${render3dRock(80, -35, 1, 0.85)}</div>
      <div style="position:absolute; bottom:250px; left:110px; z-index:18;">${render3dRock(90, 55, 0, 0.95)}</div>
    `,
    footer: ["15 MIN", "25 MIN", "45 MIN", "60 MIN PROTOCOLS"]
  },

  // -------------------------------------------------------------
  // SLIDE 5: Brutal Screen Time Audit (Daily Reality Check)
  // -------------------------------------------------------------
  {
    id: 5,
    bgGradient: "radial-gradient(circle at 45% 10%, #fff1f2 0%, #ffe4e6 38%, #fecdd3 72%, #fb7185 100%)",
    headerAlign: "left",
    headerTop: "90px",
    headerLeft: "80px",
    headline: "Audits your day<br>with brutal honesty.",
    subline: "Every wasted minute logged. No sugar-coating. No excuses.",
    phoneImg: "02_home_screen_time_breakdown.png",
    phoneStage: "perspective: 1400px; bottom: -80px; right: 40px; width: 680px; height: 1420px;",
    phoneTransform: "rotateY(-13deg) rotateX(7deg) rotateZ(-3deg)",
    pill1: {
      icon: "⚖️",
      iconBg: "rgba(244, 63, 94, 0.15)",
      iconBorder: "rgba(244, 63, 94, 0.4)",
      tag: "AURELIUS",
      tagBg: "rgba(244, 63, 94, 0.2)",
      tagColor: "#fb7185",
      title: "Marcus Aurelius",
      body: '"You could be good today. But instead you choose tomorrow."',
      top: "490px",
      left: "70px",
      rot: "-1.5deg",
      width: "530px"
    },
    pill2: {
      icon: "📊",
      iconBg: "rgba(251, 113, 133, 0.15)",
      iconBorder: "rgba(251, 113, 133, 0.4)",
      tag: "REALITY CHECK",
      tagBg: "rgba(251, 113, 133, 0.2)",
      tagColor: "#fda4af",
      title: "Daily Audit: 3h 16m",
      body: 'Social scrolling: -1h 45m today.<br><strong style="color:#ffffff;">Discipline won back 105 productive minutes.</strong>',
      top: "700px",
      left: "90px",
      rot: "2deg",
      width: "530px"
    },
    extraElements: `
      <div style="position:absolute; top:420px; right:120px; z-index:18;">${render3dRock(120, -25, 0, 1)}</div>
      <div style="position:absolute; top:350px; left:70px; z-index:18;">${render3dRock(80, 45, 1, 0.85)}</div>
      <div style="position:absolute; bottom:250px; left:120px; z-index:18;">${render3dRock(95, -60, 0, 0.95)}</div>
    `,
    footer: ["BLOOMBERG", "WIRED", "FINANCIAL TIMES"]
  },

  // -------------------------------------------------------------
  // SLIDE 6: Universal Standings (Compete with the Top 1%)
  // -------------------------------------------------------------
  {
    id: 6,
    bgGradient: "radial-gradient(circle at 50% 12%, #fffbeb 0%, #fef3c7 35%, #fde68a 70%, #f59e0b 100%)",
    headerAlign: "center",
    headerTop: "90px",
    headline: "Compete with the<br>Top 1% worldwide.",
    subline: "Ranked purely by verified deep work hours. No fake streaks.",
    phoneImg: "05_leaderboard_screen.png",
    phoneStage: "bottom: -70px; left: 50%; transform: translateX(-50%); width: 680px; height: 1420px;",
    phoneTransform: "none",
    pill1: {
      icon: "👑",
      iconBg: "rgba(245, 158, 11, 0.15)",
      iconBorder: "rgba(245, 158, 11, 0.4)",
      tag: "JENSEN HUANG",
      tagBg: "rgba(245, 158, 11, 0.2)",
      tagColor: "#fbbf24",
      title: "Character & Grit",
      body: '"Greatness comes from people who suffered and endured."',
      top: "470px",
      left: "60px",
      rot: "-2deg",
      width: "490px"
    },
    pill2: {
      icon: "🏆",
      iconBg: "rgba(217, 119, 6, 0.15)",
      iconBorder: "rgba(217, 119, 6, 0.4)",
      tag: "#1 RANK",
      tagBg: "rgba(217, 119, 6, 0.2)",
      tagColor: "#fde68a",
      title: "Podium: hyken.t1",
      body: '99 Focus Score today.<br><strong style="color:#ffffff;">Consistency beats raw talent every single day.</strong>',
      top: "680px",
      right: "60px",
      rot: "2deg",
      width: "510px"
    },
    extraElements: `
      <div style="position:absolute; top:360px; right:80px; z-index:18;">${render3dRock(110, 30, 0, 1)}</div>
      <div style="position:absolute; top:390px; left:80px; z-index:18;">${render3dRock(85, -50, 1, 0.9)}</div>
      <div style="position:absolute; bottom:260px; right:90px; z-index:18;">${render3dRock(90, 75, 0, 0.95)}</div>
    `,
    footer: ["GLOBAL LEAGUE", "FOUNDER TRACK", "AUKAAT TRACK", "WEEKLY PODIUM"]
  },

  // -------------------------------------------------------------
  // SLIDE 7: Token Mining & Tangible Rewards (Proof of Work)
  // -------------------------------------------------------------
  {
    id: 7,
    bgGradient: "radial-gradient(circle at 45% 10%, #fff7ed 0%, #ffedd5 38%, #fed7aa 72%, #fb923c 100%)",
    headerAlign: "left",
    headerTop: "90px",
    headerLeft: "80px",
    headline: "Turn raw discipline<br>into tangible proof.",
    subline: "Mine T1 Tokens through deep focus. Unlock exclusive physical merch.",
    phoneImg: "06_token_wallet_screen.png",
    phoneStage: "perspective: 1400px; bottom: -80px; right: 40px; width: 680px; height: 1420px;",
    phoneTransform: "rotateY(-13deg) rotateX(7deg) rotateZ(-3deg)",
    pill1: {
      icon: "🪙",
      iconBg: "rgba(249, 115, 22, 0.15)",
      iconBorder: "rgba(249, 115, 22, 0.4)",
      tag: "PROOF OF FOCUS",
      tagBg: "rgba(249, 115, 22, 0.2)",
      tagColor: "#fb923c",
      title: "+220 T1 Tokens Mined",
      body: 'Deep focus sessions validated.<br><strong style="color:#fed7aa;">Mined by sweat and focus. 0% crypto noise.</strong>',
      top: "490px",
      left: "70px",
      rot: "-1.5deg",
      width: "530px"
    },
    pill2: {
      icon: "👕",
      iconBg: "rgba(234, 88, 12, 0.15)",
      iconBorder: "rgba(234, 88, 12, 0.4)",
      tag: "PHYSICAL MERCH",
      tagBg: "rgba(234, 88, 12, 0.2)",
      tagColor: "#fdba74",
      title: "Signature Oversized Tee",
      body: 'Unlock at 6,000 Tokens.<br><strong style="color:#ffffff;">Cannot be bought with money. Only earned with grit.</strong>',
      top: "700px",
      left: "90px",
      rot: "2deg",
      width: "530px"
    },
    extraElements: `
      <img src="coin.png" style="position:absolute; top:400px; right:90px; width:170px; filter:drop-shadow(0 25px 40px rgba(0,0,0,0.55)); transform:rotate(18deg); z-index:25;">
      <div style="position:absolute; top:360px; left:80px; z-index:18;">${render3dRock(80, -30, 1, 0.85)}</div>
      <div style="position:absolute; bottom:240px; left:120px; z-index:18;">${render3dRock(90, 60, 0, 0.95)}</div>
    `,
    footer: ["PROOF OF FOCUS", "PHYSICAL REWARDS", "AUDITABLE", "TOP 1% EXCLUSIVE"]
  },

  // -------------------------------------------------------------
  // SLIDE 8: 100% Offline AI Coach (Unfiltered Mentor)
  // -------------------------------------------------------------
  {
    id: 8,
    bgGradient: "radial-gradient(circle at 50% 12%, #ecfeff 0%, #cffafe 35%, #a5f3fc 70%, #22d3ee 100%)",
    headerAlign: "center",
    headerTop: "90px",
    headline: "An unfiltered mentor<br>in your pocket.",
    subline: "100% on-device AI. Zero cloud tracking. Tells you the hard truth.",
    phoneImg: "04_ai_coach_screen.png",
    phoneStage: "bottom: -70px; left: 50%; transform: translateX(-50%); width: 680px; height: 1420px;",
    phoneTransform: "none",
    pill1: {
      icon: "🦾",
      iconBg: "rgba(6, 182, 212, 0.15)",
      iconBorder: "rgba(6, 182, 212, 0.4)",
      tag: "DAVID GOGGINS",
      tagBg: "rgba(6, 182, 212, 0.2)",
      tagColor: "#22d3ee",
      title: "The 40% Rule",
      body: '"When your mind says you\'re done, you\'re only at 40%. Keep going."',
      top: "470px",
      left: "60px",
      rot: "-2deg",
      width: "490px"
    },
    pill2: {
      icon: "🤖",
      iconBg: "rgba(8, 145, 178, 0.15)",
      iconBorder: "rgba(8, 145, 178, 0.4)",
      tag: "LOCAL AI",
      tagBg: "rgba(8, 145, 178, 0.2)",
      tagColor: "#67e8f9",
      title: "Offline AI Insights",
      body: '"Stop reading productivity books. Execute the first 90 minutes now."',
      top: "680px",
      right: "60px",
      rot: "2deg",
      width: "510px"
    },
    extraElements: `
      <div style="position:absolute; top:360px; right:80px; width:120px; height:120px; border-radius:50%; overflow:hidden; border:3px solid #22d3ee; box-shadow:0 0 45px rgba(34,211,238,0.7); z-index:25;">
        <img src="robot.jpg" style="width:100%; height:100%; object-fit:cover;">
      </div>
      <div style="position:absolute; top:390px; left:80px; z-index:18;">${render3dRock(90, 40, 1, 0.85)}</div>
      <div style="position:absolute; bottom:250px; right:90px; z-index:18;">${render3dRock(85, -60, 0, 0.95)}</div>
    `,
    footer: ["100% PRIVATE", "ZERO TRACKING", "OFFLINE AI", "UNFILTERED ADVICE"]
  }
];

function generateHtml(s) {
  const isLeft = s.headerAlign === 'left';
  const headerStyle = isLeft 
    ? `position: absolute; top: ${s.headerTop}; left: ${s.headerLeft}; max-width: 900px; text-align: left;`
    : `position: absolute; top: ${s.headerTop}; width: 100%; text-align: center; padding: 0 60px;`;

  const pill1Pos = s.pill1.right ? `right: ${s.pill1.right};` : `left: ${s.pill1.left};`;
  const pill2Pos = s.pill2.right ? `right: ${s.pill2.right};` : `left: ${s.pill2.left};`;

  const phoneScreenContent = s.isBlockedScreen ? `
    <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; align-items:center; justify-content:center; padding:44px; text-align:center;">
      <div style="width:104px; height:104px; border-radius:28px; background:linear-gradient(45deg, #f09433, #e6683c, #dc2743, #cc2366, #bc1888); display:flex; align-items:center; justify-content:center; margin-bottom:34px; box-shadow:0 20px 45px rgba(220,39,67,0.4);">
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
  ` : `
    <img src="${s.phoneImg}" alt="T1 Screen">
  `;

  return `
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<style>
  @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800;900&display=swap');

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
    background: #000;
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
    background: rgba(15, 23, 42, 0.88);
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

  .pill-1 {
    top: ${s.pill1.top};
    ${pill1Pos}
    width: ${s.pill1.width};
    transform: rotate(${s.pill1.rot});
  }

  .pill-2 {
    top: ${s.pill2.top};
    ${pill2Pos}
    width: ${s.pill2.width};
    transform: rotate(${s.pill2.rot});
  }

  .pill-icon {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    background: ${s.pill1.iconBg};
    border: 1.5px solid ${s.pill1.iconBorder};
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    font-size: 20px;
  }

  .pill-2 .pill-icon {
    background: ${s.pill2.iconBg};
    border-color: ${s.pill2.iconBorder};
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
    background: ${s.pill1.tagBg};
    color: ${s.pill1.tagColor};
    border: 1px solid ${s.pill1.iconBorder};
  }

  .pill-2 .pill-tag {
    background: ${s.pill2.tagBg};
    color: ${s.pill2.tagColor};
    border-color: ${s.pill2.iconBorder};
  }

  .pill-body {
    font-size: 16px;
    line-height: 1.42;
    color: #cbd5e1;
    font-weight: 400;
  }

  .credibility-footer {
    position: absolute;
    bottom: 45px;
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
        ${phoneScreenContent}
      </div>
    </div>
  </div>

  <div class="notification-pill pill-1">
    <div class="pill-icon">${s.pill1.icon}</div>
    <div class="pill-content">
      <div class="pill-title">
        <span>${s.pill1.title}</span>
        <span class="pill-tag">${s.pill1.tag}</span>
      </div>
      <div class="pill-body">
        ${s.pill1.body}
      </div>
    </div>
  </div>

  <div class="notification-pill pill-2">
    <div class="pill-icon">${s.pill2.icon}</div>
    <div class="pill-content">
      <div class="pill-title">
        <span>${s.pill2.title}</span>
        <span class="pill-tag">${s.pill2.tag}</span>
      </div>
      <div class="pill-body">
        ${s.pill2.body}
      </div>
    </div>
  </div>

  ${s.extraElements}

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

// Generate all 8 HTML files and render them
for (const s of slides) {
  const htmlFile = path.join(WORK_DIR, `slide_v2_${String(s.id).padStart(2, '0')}.html`);
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

console.log("ALL 8 HYPER-REALISTIC OPAL V2 SCREENSHOTS RENDERED SUCCESSFULLY!");
