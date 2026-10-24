const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const CHROME = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const WORK_DIR = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\opal_style";
const OUT_DIR = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\top2_screenshots";

if (!fs.existsSync(OUT_DIR)) {
  fs.mkdirSync(OUT_DIR, { recursive: true });
}

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

// 2 Master Hero Screenshots (Zoomed-in & crystal clear for mobile screens)
const top2Slides = [
  // -------------------------------------------------------------
  // HERO 1: Cognitive Focus Score (0 to 100)
  // -------------------------------------------------------------
  {
    id: 1,
    fileName: "screenshot_1_focus_score.png",
    bgGradient: "radial-gradient(circle at 45% 10%, #e0f2fe 0%, #bae6fd 38%, #93c5fd 72%, #60a5fa 100%)",
    headerAlign: "left",
    headline: "Measure your focus.<br>Not just screen time.",
    subline: "Your daily cognitive benchmark. Calibrated against the top 1%.",
    phoneHtml: `
      <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; padding:32px 24px; text-align:left; gap:18px;">
        <!-- Top bar -->
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <span style="font-size:26px; font-weight:800; color:#fff; font-family:'Space Grotesk', monospace;">T1.</span>
          <div style="display:flex; align-items:center; gap:8px; background:#18181b; border:1px solid #27272a; padding:6px 14px; border-radius:20px;">
            <span style="color:#f59e0b; font-size:14px;">🪙</span>
            <span style="color:#fff; font-size:14px; font-weight:800;">220 T1</span>
          </div>
        </div>

        <div>
          <h2 style="font-size:26px; font-weight:800; color:#fff; letter-spacing:-0.5px;">Hello, foc.t1</h2>
          <p style="font-size:15px; color:#94a3b8; font-weight:600; margin-top:4px;">Stay locked in today.</p>
        </div>

        <!-- Hero Focus Ring Card (Zoomed & Ultra-Crisp) -->
        <div style="background:linear-gradient(160deg, #18181b 0%, #0c0f17 100%); border:1px solid rgba(56,189,248,0.25); border-radius:28px; padding:28px 20px; text-align:center; box-shadow:0 20px 45px rgba(0,0,0,0.6);">
          <span style="font-size:12px; font-weight:800; letter-spacing:2px; color:#38bdf8; text-transform:uppercase;">● AHEAD OF 94% OF USERS</span>
          
          <div style="position:relative; width:240px; height:240px; margin:16px auto; display:flex; align-items:center; justify-content:center;">
            <svg width="240" height="240" viewBox="0 0 240 240" style="transform: rotate(-90deg);">
              <circle cx="120" cy="120" r="100" stroke="#27272a" stroke-width="8" fill="none" />
              <circle cx="120" cy="120" r="100" stroke="#38bdf8" stroke-width="12" stroke-linecap="round" stroke-dasharray="628" stroke-dashoffset="125" fill="none" style="filter:drop-shadow(0 0 18px rgba(56,189,248,0.8));" />
            </svg>
            <div style="position:absolute; display:flex; flex-direction:column; align-items:center;">
              <span style="font-size:76px; font-weight:900; color:#fff; font-family:'Space Grotesk', sans-serif; letter-spacing:-3px; line-height:1;">85</span>
              <span style="font-size:12px; font-weight:800; color:#94a3b8; letter-spacing:2px; text-transform:uppercase; margin-top:6px;">FOCUS SCORE</span>
            </div>
          </div>

          <div style="display:inline-block; padding:7px 18px; border-radius:20px; background:rgba(56,189,248,0.15); border:1px solid rgba(56,189,248,0.3); color:#38bdf8; font-weight:800; font-size:13px; letter-spacing:1px; text-transform:uppercase;">
            TOP 6% PERFORMER
          </div>
        </div>

        <!-- Metrics Row -->
        <div style="display:grid; grid-template-columns: 1fr 1fr 1fr; gap:10px;">
          <div style="background:#18181b; border:1px solid #27272a; border-radius:18px; padding:12px 8px; text-align:center;">
            <span style="font-size:18px;">🔥</span>
            <div style="font-size:18px; font-weight:800; color:#fff; margin-top:2px;">5 Days</div>
            <div style="font-size:10px; font-weight:700; color:#71717a; text-transform:uppercase;">STREAK</div>
          </div>
          <div style="background:#18181b; border:1px solid #27272a; border-radius:18px; padding:12px 8px; text-align:center;">
            <span style="font-size:18px;">📱</span>
            <div style="font-size:18px; font-weight:800; color:#fff; margin-top:2px;">8</div>
            <div style="font-size:10px; font-weight:700; color:#71717a; text-transform:uppercase;">UNLOCKS</div>
          </div>
          <div style="background:#18181b; border:1px solid #27272a; border-radius:18px; padding:12px 8px; text-align:center;">
            <span style="font-size:18px;">⚡</span>
            <div style="font-size:18px; font-weight:800; color:#fff; margin-top:2px;">14</div>
            <div style="font-size:10px; font-weight:700; color:#71717a; text-transform:uppercase;">OPENS</div>
          </div>
        </div>

        <!-- Today's Flow Stat Card -->
        <div style="background:#18181b; border:1px solid #27272a; border-radius:20px; padding:16px 20px; display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="font-size:12px; font-weight:700; color:#71717a; text-transform:uppercase; letter-spacing:1px;">Today's Screen Time</div>
            <div style="font-size:22px; font-weight:800; color:#fff; margin-top:4px;">1h 42m <span style="font-size:13px; color:#22c55e; font-weight:700;">(-48% vs avg)</span></div>
          </div>
          <div style="width:44px; height:44px; border-radius:14px; background:rgba(56,189,248,0.15); border:1px solid rgba(56,189,248,0.3); display:flex; align-items:center; justify-content:center; font-size:20px;">
            📊
          </div>
        </div>

        <!-- Start Quick Session CTA -->
        <div style="background:linear-gradient(135deg, #38bdf8 0%, #2563eb 100%); border-radius:22px; padding:18px; text-align:center; color:#fff; font-weight:800; font-size:16px; box-shadow:0 12px 25px rgba(37,99,235,0.35); letter-spacing:0.5px;">
          ⚡ START DEEP WORK SESSION
        </div>
      </div>
    `,
    pill1: {
      icon: "⚡",
      iconBg: "rgba(56, 189, 248, 0.15)",
      iconBorder: "rgba(56, 189, 248, 0.4)",
      tag: "NAVAL",
      tagBg: "rgba(56, 189, 248, 0.2)",
      tagColor: "#38bdf8",
      title: "Naval Ravikant",
      body: 'Free education is abundant; desire is scarce.<br><strong style="color:#7dd3fc;">Outwork the distracted 99%.</strong>',
      top: "450px",
      left: "60px",
      rot: "-1.5deg",
      width: "520px"
    },
    pill2: {
      icon: "📈",
      iconBg: "rgba(168, 85, 247, 0.15)",
      iconBorder: "rgba(168, 85, 247, 0.4)",
      tag: "COGNITIVE RANK",
      tagBg: "rgba(168, 85, 247, 0.2)",
      tagColor: "#c084fc",
      title: "Score: 85/100",
      body: 'High-agency cognitive execution active.<br><strong style="color:#e9d5ff;">+42 min won back from dopamine loops.</strong>',
      top: "660px",
      left: "80px",
      rot: "2deg",
      width: "520px"
    },
    floatingRocks: [
      { top: "400px", right: "100px", size: 120, rot: 25, blur: 0, op: 1 },
      { top: "340px", left: "60px", size: 80, rot: -45, blur: 1, op: 0.85 },
      { bottom: "240px", left: "100px", size: 95, rot: 60, blur: 0, op: 0.95 }
    ],
    footer: ["THE WALL STREET JOURNAL", "TECHCRUNCH", "FORBES"]
  },

  // -------------------------------------------------------------
  // HERO 2: Deep Work Session Timer (25:00 Flow State)
  // -------------------------------------------------------------
  {
    id: 2,
    fileName: "screenshot_2_focus_timer.png",
    bgGradient: "radial-gradient(circle at 55% 10%, #f0fdf4 0%, #dcfce7 38%, #bbf7d0 72%, #4ade80 100%)",
    headerAlign: "left",
    headline: "Enter deep work<br>in 25 minutes flat.",
    subline: "Unbroken flow blocks. Built for intense coding, studying & building.",
    phoneHtml: `
      <div style="width:100%; height:100%; background:#09090b; display:flex; flex-direction:column; padding:32px 24px; text-align:center; gap:20px;">
        <!-- Top status -->
        <div style="width:100%; display:flex; justify-content:space-between; align-items:center;">
          <span style="font-size:12px; font-weight:800; letter-spacing:2px; color:#4ade80; text-transform:uppercase;">MONK MODE PROTOCOL</span>
          <div style="width:36px; height:36px; border-radius:50%; background:#18181b; border:1px solid #27272a; display:flex; align-items:center; justify-content:center; color:#94a3b8; font-size:16px;">✕</div>
        </div>
        
        <!-- Large Neon Green Timer Ring -->
        <div style="position:relative; width:260px; height:260px; margin:10px auto; display:flex; align-items:center; justify-content:center;">
          <svg width="260" height="260" viewBox="0 0 260 260" style="transform: rotate(-90deg);">
            <circle cx="130" cy="130" r="110" stroke="#27272a" stroke-width="8" fill="none" />
            <circle cx="130" cy="130" r="110" stroke="#22c55e" stroke-width="12" stroke-linecap="round" stroke-dasharray="691" stroke-dashoffset="190" fill="none" style="filter:drop-shadow(0 0 20px rgba(34,197,94,0.75));" />
          </svg>
          <div style="position:absolute; display:flex; flex-direction:column; align-items:center;">
            <span style="font-size:62px; font-weight:900; color:#fff; font-family:'Space Grotesk', monospace; letter-spacing:-2px; line-height:1;">25:00</span>
            <span style="font-size:12px; font-weight:800; color:#4ade80; letter-spacing:1.5px; text-transform:uppercase; margin-top:8px;">● FLOW STATE ACTIVE</span>
          </div>
        </div>

        <!-- Presets and CTA -->
        <div style="width:100%; display:flex; flex-direction:column; gap:16px;">
          <div style="display:flex; justify-content:center; gap:10px;">
            <div style="padding:10px 18px; border-radius:20px; background:#18181b; border:1px solid #27272a; color:#a1a1aa; font-weight:700; font-size:14px;">15 min</div>
            <div style="padding:10px 22px; border-radius:20px; background:#22c55e; color:#000; font-weight:800; font-size:14px; box-shadow:0 0 20px rgba(34,197,94,0.4);">25 min</div>
            <div style="padding:10px 18px; border-radius:20px; background:#18181b; border:1px solid #27272a; color:#a1a1aa; font-weight:700; font-size:14px;">45 min</div>
            <div style="padding:10px 18px; border-radius:20px; background:#18181b; border:1px solid #27272a; color:#a1a1aa; font-weight:700; font-size:14px;">60 min</div>
          </div>

          <!-- Bottom Card -->
          <div style="background:#18181b; border:1px solid #27272a; border-radius:20px; padding:16px 20px; display:flex; justify-content:space-between; align-items:center;">
            <div style="text-align:left;">
              <div style="font-size:14px; font-weight:800; color:#fff;">Session Mining Rate</div>
              <div style="font-size:12px; color:#4ade80; margin-top:2px;">1.5x Streak Multiplier Active</div>
            </div>
            <span style="font-size:18px; font-weight:800; color:#f59e0b;">+25 T1</span>
          </div>

          <div style="width:100%; height:58px; border-radius:29px; background:#fff; color:#000; font-weight:800; font-size:17px; display:flex; align-items:center; justify-content:center; gap:10px; box-shadow:0 15px 30px rgba(255,255,255,0.25);">
            <span>⚡</span> Stay In Session
          </div>
        </div>
      </div>
    `,
    pill1: {
      icon: "⏱️",
      iconBg: "rgba(34, 197, 94, 0.15)",
      iconBorder: "rgba(34, 197, 94, 0.4)",
      tag: "SAM ALTMAN",
      tagBg: "rgba(34, 197, 94, 0.2)",
      tagColor: "#4ade80",
      title: "Sam Altman",
      body: '"Extreme focus produces extraordinary leverage. Relentlessly execute."',
      top: "450px",
      left: "60px",
      rot: "-1.5deg",
      width: "520px"
    },
    pill2: {
      icon: "⚡",
      iconBg: "rgba(16, 185, 129, 0.15)",
      iconBorder: "rgba(16, 185, 129, 0.4)",
      tag: "FLOW STATE",
      tagBg: "rgba(16, 185, 129, 0.2)",
      tagColor: "#34d399",
      title: "25:00 Session Locked",
      body: 'Notifications silenced. Mind locked in.<br><strong style="color:#6ee7b7;">Mining T1 Focus Tokens at maximum rate.</strong>',
      top: "660px",
      left: "80px",
      rot: "2deg",
      width: "520px"
    },
    floatingRocks: [
      { top: "390px", right: "100px", size: 115, rot: 20, blur: 0, op: 1 },
      { top: "330px", left: "60px", size: 80, rot: -35, blur: 1, op: 0.85 },
      { bottom: "240px", left: "100px", size: 90, rot: 55, blur: 0, op: 0.95 }
    ],
    footer: ["15 MIN", "25 MIN", "45 MIN", "60 MIN PROTOCOLS"]
  }
];

function generateHtml(s) {
  return `
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<style>
  @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800;900&family=Space+Grotesk:wght@600;700;800;900&display=swap');

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
    position: absolute;
    top: 80px;
    left: 80px;
    max-width: 920px;
    text-align: left;
    z-index: 10;
  }

  .headline {
    font-size: 80px;
    font-weight: 800;
    line-height: 1.05;
    color: #09090b;
    letter-spacing: -3px;
  }

  .subline {
    font-size: 26px;
    font-weight: 600;
    color: #1e3a5f;
    margin-top: 16px;
    letter-spacing: -0.4px;
    line-height: 1.35;
  }

  /* Zoomed-in Phone Stage for crystal clarity */
  .stage {
    position: absolute;
    perspective: 1400px;
    bottom: -80px;
    right: 30px;
    width: 710px;
    height: 1440px;
    z-index: 5;
  }

  .phone-frame {
    width: 100%;
    height: 100%;
    background: #09090b;
    border-radius: 58px;
    padding: 13px;
    transform: rotateY(-14deg) rotateX(7deg) rotateZ(-3deg);
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
    border-radius: 46px;
    overflow: hidden;
    background: #09090b;
    position: relative;
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
        ${s.phoneHtml}
      </div>
    </div>
  </div>

  <!-- Pill 1 -->
  <div class="notification-pill" style="top: ${s.pill1.top}; left: ${s.pill1.left}; width: ${s.pill1.width}; transform: rotate(${s.pill1.rot});">
    <div class="pill-icon" style="background: ${s.pill1.iconBg}; border: 1.5px solid ${s.pill1.iconBorder};">${s.pill1.icon}</div>
    <div class="pill-content">
      <div class="pill-title">
        <span>${s.pill1.title}</span>
        <span class="pill-tag" style="background: ${s.pill1.tagBg}; color: ${s.pill1.tagColor}; border: 1px solid ${s.pill1.iconBorder};">${s.pill1.tag}</span>
      </div>
      <div class="pill-body">
        ${s.pill1.body}
      </div>
    </div>
  </div>

  <!-- Pill 2 -->
  <div class="notification-pill" style="top: ${s.pill2.top}; left: ${s.pill2.left}; width: ${s.pill2.width}; transform: rotate(${s.pill2.rot});">
    <div class="pill-icon" style="background: ${s.pill2.iconBg}; border: 1.5px solid ${s.pill2.iconBorder};">${s.pill2.icon}</div>
    <div class="pill-content">
      <div class="pill-title">
        <span>${s.pill2.title}</span>
        <span class="pill-tag" style="background: ${s.pill2.tagBg}; color: ${s.pill2.tagColor}; border: 1px solid ${s.pill2.iconBorder};">${s.pill2.tag}</span>
      </div>
      <div class="pill-body">
        ${s.pill2.body}
      </div>
    </div>
  </div>

  ${s.floatingRocks.map(r => `
    <div style="position: absolute; ${r.top ? `top: ${r.top};` : `bottom: ${r.bottom};`} ${r.left ? `left: ${r.left};` : `right: ${r.right};`} z-index: 18; pointer-events: none;">
      ${render3dRock(r.size, r.rot, r.blur, r.op)}
    </div>
  `).join('')}

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

for (const s of top2Slides) {
  const htmlFile = path.join(WORK_DIR, `slide_top2_${s.id}.html`);
  const pngFile = path.join(OUT_DIR, s.fileName);

  fs.writeFileSync(htmlFile, generateHtml(s), 'utf8');
  console.log(`Wrote ${htmlFile}`);

  try {
    execSync(`"${CHROME}" --headless --disable-gpu --screenshot="${pngFile}" --window-size=1080,1920 "${htmlFile}"`, { stdio: 'inherit' });
    console.log(`Rendered ${pngFile}`);
  } catch (err) {
    console.error(`Error rendering slide ${s.id}:`, err.message);
  }
}

console.log("TOP 2 SCREENSHOTS RENDERED SUCCESSFULLY!");
