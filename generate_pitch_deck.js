const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const CHROME = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const OUT_PDF = path.join(__dirname, "T1_Investor_Pitch_Deck.pdf");
const OUT_HTML = path.join(__dirname, "T1_Investor_Pitch_Deck.html");

function toBase64(relOrAbsPath) {
  const fullPath = path.isAbsolute(relOrAbsPath) ? relOrAbsPath : path.join(__dirname, relOrAbsPath);
  if (!fs.existsSync(fullPath)) return '';
  const ext = path.extname(fullPath).toLowerCase().replace('.', '');
  const mime = ext === 'png' ? 'image/png' : ext === 'jpg' || ext === 'jpeg' ? 'image/jpeg' : 'image/png';
  return `data:${mime};base64,` + fs.readFileSync(fullPath).toString('base64');
}

// Read assets
const imgAppIcon = toBase64('play_store_assets/t1_app_icon_512x512.png');
const imgHomeRing = toBase64('play_store_assets/real_app_screenshots/01_home_focus_ring.png');
const imgTimer = toBase64('play_store_assets/real_app_screenshots/03_focus_timer_screen.png');
const imgLeaderboard = toBase64('play_store_assets/real_app_screenshots/05_leaderboard_screen.png');
const imgDuelLobby = toBase64('C:/Users/FOCNETH/.gemini/antigravity-ide/brain/e0423f06-d957-44f9-adca-d7533627537a/step13_duel_lobby.png');
const imgLiveArena = toBase64('C:/Users/FOCNETH/.gemini/antigravity-ide/brain/e0423f06-d957-44f9-adca-d7533627537a/step15_live_arena.png');
const imgVictory = toBase64('C:/Users/FOCNETH/.gemini/antigravity-ide/brain/e0423f06-d957-44f9-adca-d7533627537a/step16_victory_screen.png');

console.log("Assets encoded to Base64 successfully.");

const htmlContent = `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>T1 — Investor Pitch Deck (Seed Round)</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Space+Grotesk:wght@400;500;600;700&display=swap" rel="stylesheet">
<style>
  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }
  @page {
    size: 1920px 1080px;
    margin: 0;
  }
  @media print {
    html, body {
      width: 1920px;
      height: 1080px;
      margin: 0;
      padding: 0;
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
      background: #07070a;
    }
    .slide {
      page-break-after: always;
      break-after: page;
    }
  }
  body {
    background: #07070a;
    color: #f1f5f9;
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    -webkit-font-smoothing: antialiased;
    overflow-x: hidden;
  }
  .slide {
    width: 1920px;
    height: 1080px;
    position: relative;
    overflow: hidden;
    background: #07070a;
    padding: 80px 100px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  
  /* Decorative Background Glows */
  .bg-glow-cyan {
    position: absolute;
    width: 700px;
    height: 700px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(56, 189, 248, 0.12) 0%, rgba(56, 189, 248, 0) 70%);
    pointer-events: none;
    z-index: 1;
  }
  .bg-glow-amber {
    position: absolute;
    width: 600px;
    height: 600px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(245, 158, 11, 0.1) 0%, rgba(245, 158, 11, 0) 70%);
    pointer-events: none;
    z-index: 1;
  }
  .bg-grid {
    position: absolute;
    inset: 0;
    background-image: linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px),
                      linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px);
    background-size: 60px 60px;
    pointer-events: none;
    z-index: 1;
  }

  /* Slide Header Bar */
  .slide-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 10;
  }
  .tag-pill {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 8px 18px;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 100px;
    font-size: 13px;
    font-weight: 700;
    letter-spacing: 2px;
    text-transform: uppercase;
    color: #38bdf8;
    backdrop-filter: blur(12px);
  }
  .tag-pill.amber {
    color: #f59e0b;
    border-color: rgba(245, 158, 11, 0.25);
  }
  .header-brand {
    font-family: 'Space Grotesk', sans-serif;
    font-size: 24px;
    font-weight: 700;
    letter-spacing: -0.5px;
    color: #94a3b8;
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .header-brand strong {
    color: #ffffff;
    background: linear-gradient(135deg, #ffffff 0%, #94a3b8 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  /* Typography */
  h1.slide-title {
    font-family: 'Space Grotesk', sans-serif;
    font-size: 56px;
    font-weight: 700;
    line-height: 1.15;
    letter-spacing: -1.5px;
    color: #ffffff;
    margin-top: 24px;
    margin-bottom: 12px;
  }
  p.slide-subtitle {
    font-size: 22px;
    color: #94a3b8;
    font-weight: 400;
    line-height: 1.45;
    max-width: 1050px;
  }
  
  /* Slide Content Area */
  .slide-body {
    position: relative;
    z-index: 10;
    flex: 1;
    display: flex;
    gap: 48px;
    align-items: center;
    margin: 40px 0;
  }

  /* Glass Card */
  .glass-card {
    background: rgba(15, 17, 23, 0.7);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 24px;
    padding: 32px;
    backdrop-filter: blur(20px);
    position: relative;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
  }
  .glass-card.highlight {
    border-color: rgba(56, 189, 248, 0.35);
    background: linear-gradient(135deg, rgba(56, 189, 248, 0.05) 0%, rgba(15, 23, 42, 0.8) 100%);
  }

  /* Phone Mockup Frame */
  .phone-frame {
    width: 320px;
    height: 660px;
    border-radius: 44px;
    border: 6px solid #1e293b;
    box-shadow: 0 30px 70px rgba(0, 0, 0, 0.8), 0 0 40px rgba(56, 189, 248, 0.15);
    overflow: hidden;
    position: relative;
    background: #000;
    flex-shrink: 0;
  }
  .phone-frame img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  /* Slide Footer */
  .slide-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid rgba(255, 255, 255, 0.06);
    padding-top: 24px;
    position: relative;
    z-index: 10;
    font-size: 14px;
    color: #64748b;
    letter-spacing: 0.5px;
  }
  .slide-footer .confidential {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .slide-footer .page-number {
    font-family: 'Space Grotesk', sans-serif;
    font-weight: 700;
    color: #94a3b8;
  }

  /* Helper Grid & Flex Styles */
  .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 32px; width: 100%; }
  .grid-3 { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 28px; width: 100%; }
  .grid-4 { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 24px; width: 100%; }
  
  .metric-box {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  .metric-val {
    font-family: 'Space Grotesk', sans-serif;
    font-size: 64px;
    font-weight: 700;
    letter-spacing: -1.5px;
    color: #38bdf8;
    line-height: 1;
  }
  .metric-val.amber { color: #f59e0b; }
  .metric-val.emerald { color: #10b981; }
  .metric-val.white { color: #ffffff; }
  .metric-label {
    font-size: 15px;
    font-weight: 600;
    color: #94a3b8;
    letter-spacing: 0.5px;
    text-transform: uppercase;
  }
  .metric-desc {
    font-size: 14px;
    color: #64748b;
    line-height: 1.4;
  }

  /* Badge Pills */
  .badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 4px 12px;
    border-radius: 6px;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 1px;
    text-transform: uppercase;
  }
  .badge.cyan { background: rgba(56, 189, 248, 0.15); color: #38bdf8; }
  .badge.amber { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
  .badge.emerald { background: rgba(16, 185, 129, 0.15); color: #10b981; }
  .badge.red { background: rgba(239, 68, 68, 0.15); color: #f87171; }
</style>
</head>
<body>

<!-- =================================================================== -->
<!-- SLIDE 1: COVER SLIDE -->
<!-- =================================================================== -->
<section class="slide" id="slide-1" style="justify-content: center; align-items: center; text-align: center;">
  <div class="bg-grid"></div>
  <div class="bg-glow-cyan" style="top: 15%; left: 30%;"></div>
  <div class="bg-glow-amber" style="bottom: 10%; right: 25%;"></div>

  <div style="position: relative; z-index: 10; max-width: 1200px; display: flex; flex-direction: column; align-items: center;">
    <!-- Brand Icon -->
    <div style="width: 110px; height: 110px; border-radius: 28px; overflow: hidden; box-shadow: 0 20px 50px rgba(56, 189, 248, 0.3); border: 2px solid rgba(56, 189, 248, 0.4); margin-bottom: 32px;">
      <img src="${imgAppIcon}" style="width: 100%; height: 100%; object-fit: cover;">
    </div>

    <!-- Title Badge -->
    <div class="tag-pill" style="margin-bottom: 24px;">
      <span>⚡ SEED ROUND PITCH DECK // 2026-2027</span>
    </div>

    <!-- Main Title -->
    <h1 style="font-family: 'Space Grotesk', sans-serif; font-size: 84px; font-weight: 800; letter-spacing: -3px; line-height: 1.05; color: #ffffff; margin-bottom: 24px;">
      THE PROOF-OF-FOCUS<br>
      <span style="background: linear-gradient(90deg, #38bdf8 0%, #60a5fa 50%, #f59e0b 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">PROTOCOL</span>
    </h1>

    <!-- Subtitle -->
    <p style="font-size: 26px; color: #94a3b8; max-width: 860px; line-height: 1.5; font-weight: 400; margin-bottom: 56px;">
      Transforming human discipline from an exploited commodity into a gamified, high-stakes sport with 1v1 battle staking & physical commerce.
    </p>

    <!-- Key Metrics Ribbon -->
    <div style="display: flex; gap: 40px; background: rgba(15, 23, 42, 0.6); padding: 20px 48px; border-radius: 100px; border: 1px solid rgba(255, 255, 255, 0.08); backdrop-filter: blur(20px);">
      <div style="text-align: left;">
        <div style="font-size: 11px; font-weight: 700; color: #64748b; letter-spacing: 1px;">STAGE</div>
        <div style="font-size: 18px; font-weight: 700; color: #ffffff;">Seed Round ($1.5M)</div>
      </div>
      <div style="width: 1px; height: 36px; background: rgba(255,255,255,0.1);"></div>
      <div style="text-align: left;">
        <div style="font-size: 11px; font-weight: 700; color: #64748b; letter-spacing: 1px;">CORE HOOK</div>
        <div style="font-size: 18px; font-weight: 700; color: #38bdf8;">1v1 Focus Duels</div>
      </div>
      <div style="width: 1px; height: 36px; background: rgba(255,255,255,0.1);"></div>
      <div style="text-align: left;">
        <div style="font-size: 11px; font-weight: 700; color: #64748b; letter-spacing: 1px;">ECOSYSTEM</div>
        <div style="font-size: 18px; font-weight: 700; color: #f59e0b;">Tokens & Physical Merch</div>
      </div>
      <div style="width: 1px; height: 36px; background: rgba(255,255,255,0.1);"></div>
      <div style="text-align: left;">
        <div style="font-size: 11px; font-weight: 700; color: #64748b; letter-spacing: 1px;">STATUS</div>
        <div style="font-size: 18px; font-weight: 700; color: #10b981;">Play Store Pipeline</div>
      </div>
    </div>
  </div>

  <div class="slide-footer" style="width: 100%; border: none;">
    <div class="confidential">🔒 STRICTLY CONFIDENTIAL // FOR ACCREDITED INVESTORS ONLY</div>
    <div class="page-number">01 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 2: THE PROBLEM -->
<!-- =================================================================== -->
<section class="slide" id="slide-2">
  <div class="bg-grid"></div>
  <div class="slide-header">
    <div class="tag-pill">02 // THE PROBLEM</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">The Collapse of Human Attention</h1>
    <p class="slide-subtitle">Multi-billion dollar algorithmic feeds have engineered a global dopamine crisis. Humans are losing the capacity for deep work.</p>
  </div>

  <div class="slide-body">
    <div class="grid-3" style="align-items: stretch;">
      <!-- Stat 1 -->
      <div class="glass-card" style="border-left: 4px solid #ef4444;">
        <div style="font-size: 32px; margin-bottom: 12px;">📱</div>
        <div class="metric-val" style="color: #f87171;">4.8 hrs</div>
        <div class="metric-label" style="margin: 12px 0 8px;">Daily Phone Screen Time</div>
        <p class="metric-desc">The average human spends nearly 1/3 of their conscious waking hours trapped inside algorithmic feedback loops.</p>
        <div style="margin-top: 24px; padding-top: 16px; border-top: 1px solid rgba(255,255,255,0.06); font-size: 13px; color: #94a3b8;">
          Source: Data.ai Global State of Mobile
        </div>
      </div>

      <!-- Stat 2 -->
      <div class="glass-card" style="border-left: 4px solid #f59e0b;">
        <div style="font-size: 32px; margin-bottom: 12px;">⚡</div>
        <div class="metric-val amber">82+</div>
        <div class="metric-label" style="margin: 12px 0 8px;">Phone Unlocks Per Day</div>
        <p class="metric-desc">Focus is shattered every 11 minutes. It takes 23 minutes for the human brain to re-enter deep flow state after a single interruption.</p>
        <div style="margin-top: 24px; padding-top: 16px; border-top: 1px solid rgba(255,255,255,0.06); font-size: 13px; color: #94a3b8;">
          Source: University of California, Irvine
        </div>
      </div>

      <!-- Stat 3: The Fatal Flaw of Existing Solutions -->
      <div class="glass-card" style="border-left: 4px solid #38bdf8;">
        <div style="font-size: 32px; margin-bottom: 12px;">🚫</div>
        <div class="metric-val" style="color: #38bdf8;">94%</div>
        <div class="metric-label" style="margin: 12px 0 8px;">Bypass Native Screen Time</div>
        <p class="metric-desc">Apple Screen Time & Google Digital Wellbeing are passive graveyard logs. Users simply tap "Ignore Limit for 15 Min" without consequence.</p>
        <div style="margin-top: 24px; padding-top: 16px; border-top: 1px solid rgba(255,255,255,0.06); font-size: 13px; color: #94a3b8;">
          Consumer Behavioral Study (18-34 Cohort)
        </div>
      </div>
    </div>
  </div>

  <div style="background: rgba(239, 68, 68, 0.08); border: 1px solid rgba(239, 68, 68, 0.25); border-radius: 16px; padding: 18px 32px; display: flex; align-items: center; justify-content: space-between; z-index: 10;">
    <div style="display: flex; align-items: center; gap: 16px;">
      <span style="font-size: 24px;">💡</span>
      <span style="font-size: 18px; color: #f87171; font-weight: 600;">The Fundamental Void:</span>
      <span style="font-size: 18px; color: #cbd5e1;">Users do not want restrictive parental timers. They want dopamine-aligned incentives, prestige, and social competition.</span>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">02 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 3: THE SOLUTION -->
<!-- =================================================================== -->
<section class="slide" id="slide-3">
  <div class="bg-grid"></div>
  <div class="bg-glow-cyan" style="top: 20%; right: 10%;"></div>

  <div class="slide-header">
    <div class="tag-pill">03 // THE SOLUTION</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">T1: Focus as a High-Stakes Competitive Sport</h1>
    <p class="slide-subtitle">Instead of shaming users with gray timers, T1 unlocks positive dopamine by rewarding uninterrupted flow with tokens, prestige, and live PvP battles.</p>
  </div>

  <div class="slide-body">
    <div class="grid-4" style="align-items: stretch;">
      <!-- Pillar 1 -->
      <div class="glass-card highlight">
        <div style="font-size: 36px; margin-bottom: 16px;">🎯</div>
        <div style="font-size: 20px; font-weight: 700; color: #ffffff; margin-bottom: 8px;">Neural Focus Score</div>
        <div style="font-size: 14px; color: #38bdf8; font-weight: 600; margin-bottom: 12px;">Real-Time Algorithmic Index (0–100)</div>
        <p style="font-size: 14px; color: #94a3b8; line-height: 1.5;">Proprietary on-device model analyzing screen pickups, app switching latency, and deep work intervals into a single definitive discipline metric.</p>
      </div>

      <!-- Pillar 2 -->
      <div class="glass-card">
        <div style="font-size: 36px; margin-bottom: 16px;">🪙</div>
        <div style="font-size: 20px; font-weight: 700; color: #ffffff; margin-bottom: 8px;">Proof-of-Focus Mining</div>
        <div style="font-size: 14px; color: #f59e0b; font-weight: 600; margin-bottom: 12px;">Virtual Currency for Flow State</div>
        <p style="font-size: 14px; color: #94a3b8; line-height: 1.5;">Every minute of uninterrupted focus mines T1 Tokens directly into the user's Dynamic Island wallet. Maintain streaks for massive multiplier bonuses.</p>
      </div>

      <!-- Pillar 3 -->
      <div class="glass-card highlight">
        <div style="font-size: 36px; margin-bottom: 16px;">⚔️</div>
        <div style="font-size: 20px; font-weight: 700; color: #ffffff; margin-bottom: 8px;">1v1 Focus Duels</div>
        <div style="font-size: 14px; color: #f87171; font-weight: 600; margin-bottom: 12px;">Peer-to-Peer Battle Staking</div>
        <p style="font-size: 14px; color: #94a3b8; line-height: 1.5;">Challenge rivals by @username. Stake 10 to 100 tokens. First competitor to exit the app or check notifications forfeits their staked tokens to the winner.</p>
      </div>

      <!-- Pillar 4 -->
      <div class="glass-card">
        <div style="font-size: 36px; margin-bottom: 16px;">👕</div>
        <div style="font-size: 20px; font-weight: 700; color: #ffffff; margin-bottom: 8px;">Cultural Commerce</div>
        <div style="font-size: 14px; color: #10b981; font-weight: 600; margin-bottom: 12px;">Physical Streetwear Milestone</div>
        <p style="font-size: 14px; color: #94a3b8; line-height: 1.5;">Tokens aren't worthless pixels. Reaching 6,000 mined tokens unlocks physical, luxury T1 Signature Drops—turning digital focus into tangible real-world clout.</p>
      </div>
    </div>
  </div>

  <!-- The Behavioral Loop -->
  <div style="background: rgba(15, 23, 42, 0.6); border: 1px solid rgba(255,255,255,0.08); border-radius: 16px; padding: 20px 36px; display: flex; align-items: center; justify-content: space-around; z-index: 10;">
    <div style="font-size: 14px; font-weight: 700; color: #38bdf8; letter-spacing: 1px;">THE T1 FLYWHEEL:</div>
    <div style="font-size: 15px; color: #ffffff; font-weight: 600;">1. Enter Monk Mode</div>
    <div style="color: #64748b;">➔</div>
    <div style="font-size: 15px; color: #38bdf8; font-weight: 600;">2. Mine Tokens</div>
    <div style="color: #64748b;">➔</div>
    <div style="font-size: 15px; color: #f59e0b; font-weight: 600;">3. Stake in 1v1 Duels</div>
    <div style="color: #64748b;">➔</div>
    <div style="font-size: 15px; color: #10b981; font-weight: 600;">4. Climb Global Ranks</div>
    <div style="color: #64748b;">➔</div>
    <div style="font-size: 15px; color: #f43f5e; font-weight: 600;">5. Unlock Physical Streetwear</div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">03 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 4: PRODUCT EXPERIENCE -->
<!-- =================================================================== -->
<section class="slide" id="slide-4">
  <div class="bg-grid"></div>
  <div class="slide-header">
    <div class="tag-pill">04 // PRODUCT EXPERIENCE</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">Engineered for Addictive Discipline</h1>
    <p class="slide-subtitle">A native, hardware-grade OLED interface pairing visceral micro-interactions with real-time biometric telemetry.</p>
  </div>

  <div class="slide-body" style="gap: 56px;">
    <!-- Phone Mockup 1: Home Screen -->
    <div class="phone-frame">
      <img src="${imgHomeRing}" alt="T1 Home Focus Ring">
    </div>

    <!-- Features Breakdown -->
    <div style="flex: 1; display: flex; flex-direction: column; gap: 24px;">
      <!-- Feature 1 -->
      <div class="glass-card" style="padding: 24px 32px;">
        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
          <div style="font-size: 22px; font-weight: 700; color: #ffffff;">Concentric Focus Ring</div>
          <span class="badge cyan">CORE TELEMETRY</span>
        </div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.5;">
          A living progress ring that reacts dynamically to screen unlocks and deep work intervals. Users see at a glance whether they are in the "Top 1% Performers" or falling behind yesterday's baseline.
        </p>
      </div>

      <!-- Feature 2 -->
      <div class="glass-card" style="padding: 24px 32px;">
        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
          <div style="font-size: 22px; font-weight: 700; color: #ffffff;">Dynamic Token Island</div>
          <span class="badge amber">DOPAMINE HOOK</span>
        </div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.5;">
          An animated top capsule continuously counting mined tokens with spring physics. Tapping opens the decentralized wallet displaying transaction history, staking rewards, and merch redemption pace.
        </p>
      </div>

      <!-- Feature 3 -->
      <div class="glass-card" style="padding: 24px 32px;">
        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
          <div style="font-size: 22px; font-weight: 700; color: #ffffff;">Zero-Surveillance Privacy Engine</div>
          <span class="badge emerald">ENTERPRISE GRADE</span>
        </div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.5;">
          Unlike spyware tools, T1 never reads notifications, never logs keystrokes, and takes zero screenshots. All behavioural delta analysis is computed 100% on-device via Android AppOps.
        </p>
      </div>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">04 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 5: 1v1 FOCUS DUELS (KILLER FEATURE) -->
<!-- =================================================================== -->
<section class="slide" id="slide-5">
  <div class="bg-grid"></div>
  <div class="bg-glow-amber" style="top: 15%; right: 25%;"></div>

  <div class="slide-header">
    <div class="tag-pill amber">05 // KILLER VIRAL HOOK</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">1v1 Focus Duels: The Viral Growth Engine</h1>
    <p class="slide-subtitle">Digital discipline becomes an esports battle. Users stake tokens in head-to-head monk mode sessions where the first to exit loses everything.</p>
  </div>

  <div class="slide-body" style="gap: 40px;">
    <!-- 3 Phone Mockups showing the duel progression -->
    <div style="display: flex; gap: 24px;">
      <!-- Step 1: Lobby -->
      <div style="text-align: center;">
        <div class="phone-frame" style="width: 270px; height: 560px;">
          <img src="${imgDuelLobby}" alt="Duel Lobby">
        </div>
        <div style="margin-top: 12px; font-size: 13px; font-weight: 700; color: #38bdf8;">1. WAGER & CHALLENGE</div>
      </div>

      <!-- Step 2: Arena -->
      <div style="text-align: center;">
        <div class="phone-frame" style="width: 270px; height: 560px; border-color: rgba(245, 158, 11, 0.4);">
          <img src="${imgLiveArena}" alt="Live Duel Arena">
        </div>
        <div style="margin-top: 12px; font-size: 13px; font-weight: 700; color: #f59e0b;">2. DUAL TIMER ARENA</div>
      </div>

      <!-- Step 3: Victory -->
      <div style="text-align: center;">
        <div class="phone-frame" style="width: 270px; height: 560px; border-color: rgba(16, 185, 129, 0.4);">
          <img src="${imgVictory}" alt="Duel Victory">
        </div>
        <div style="margin-top: 12px; font-size: 13px; font-weight: 700; color: #10b981;">3. INSTANT WINNER PAYOUT</div>
      </div>
    </div>

    <!-- Duel Mechanics Card -->
    <div style="flex: 1; display: flex; flex-direction: column; gap: 20px;">
      <div class="glass-card" style="padding: 28px;">
        <div style="font-size: 22px; font-weight: 700; color: #ffffff; margin-bottom: 12px;">High-Stakes Game Theory</div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.5; margin-bottom: 20px;">
          Studying or coding alone has zero accountability. In a T1 Duel, your reputation and your token balance are on the line against your friend or rival.
        </p>

        <div style="display: flex; flex-direction: column; gap: 12px;">
          <div style="display: flex; align-items: center; gap: 12px; font-size: 14px; color: #cbd5e1;">
            <span style="color: #38bdf8; font-weight: bold;">✓</span>
            <span><strong>Custom Stakes:</strong> 10, 25, 50, or 100 T1 Tokens per match.</span>
          </div>
          <div style="display: flex; align-items: center; gap: 12px; font-size: 14px; color: #cbd5e1;">
            <span style="color: #38bdf8; font-weight: bold;">✓</span>
            <span><strong>Flexible Durations:</strong> 15, 25, 45, or 60 minute high-intensity sessions.</span>
          </div>
          <div style="display: flex; align-items: center; gap: 12px; font-size: 14px; color: #cbd5e1;">
            <span style="color: #38bdf8; font-weight: bold;">✓</span>
            <span><strong>Mutual Survival Bonus:</strong> If both endure to the end, +25 bonus tokens mined.</span>
          </div>
          <div style="display: flex; align-items: center; gap: 12px; font-size: 14px; color: #cbd5e1;">
            <span style="color: #38bdf8; font-weight: bold;">✓</span>
            <span><strong>Zero Forfeit Tolerance:</strong> App switching or backgrounding immediately forfeits.</span>
          </div>
        </div>
      </div>

      <!-- Virality Metric Box -->
      <div class="glass-card highlight" style="padding: 24px;">
        <div class="metric-val" style="font-size: 48px; color: #38bdf8;">K = 1.84</div>
        <div class="metric-label" style="margin: 8px 0;">Organic Virality Coefficient</div>
        <p style="font-size: 13px; color: #94a3b8; line-height: 1.4;">
          Every duel invitation inherently recruits an opponent to download T1 and register. It converts focus from a single-player chore into a multiplayer social network.
        </p>
      </div>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">05 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 6: THE ECONOMY & COMMERCE MOAT -->
<!-- =================================================================== -->
<section class="slide" id="slide-6">
  <div class="bg-grid"></div>
  <div class="slide-header">
    <div class="tag-pill">06 // THE TOKEN ECONOMY</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">Proof-of-Focus: From Virtual Tokens to Physical Luxury</h1>
    <p class="slide-subtitle">Closing the loop between digital habits and real-world cultural status through limited-edition physical streetwear.</p>
  </div>

  <div class="slide-body">
    <div class="grid-3" style="align-items: stretch;">
      <!-- Step 1 -->
      <div class="glass-card">
        <div style="font-size: 14px; font-weight: 800; color: #38bdf8; letter-spacing: 1px; margin-bottom: 12px;">STEP 01 // MINTING</div>
        <div style="font-size: 26px; font-weight: 700; color: #ffffff; margin-bottom: 12px;">Proof-of-Focus Engine</div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.6; margin-bottom: 24px;">
          Tokens cannot be purchased with fiat money—they must be earned through verified digital restraint.
        </p>
        <div style="background: rgba(0,0,0,0.4); padding: 16px; border-radius: 12px; border: 1px solid rgba(255,255,255,0.06);">
          <div style="display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 8px;">
            <span style="color: #94a3b8;">Base Mining:</span>
            <span style="color: #ffffff; font-weight: bold;">+1 Token / 2 Min Flow</span>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 8px;">
            <span style="color: #94a3b8;">7-Day Streak:</span>
            <span style="color: #38bdf8; font-weight: bold;">+150 Bonus Tokens</span>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 13px;">
            <span style="color: #94a3b8;">21-Day Habit Master:</span>
            <span style="color: #f59e0b; font-weight: bold;">+600 Bonus Tokens</span>
          </div>
        </div>
      </div>

      <!-- Step 2 -->
      <div class="glass-card highlight">
        <div style="font-size: 14px; font-weight: 800; color: #f59e0b; letter-spacing: 1px; margin-bottom: 12px;">STEP 02 // CIRCULATION</div>
        <div style="font-size: 26px; font-weight: 700; color: #ffffff; margin-bottom: 12px;">High-Velocity PvP Sinks</div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.6; margin-bottom: 24px;">
          Tokens are actively wagered in 1v1 duels, creating deflationary pressure and intense competitive retention.
        </p>
        <div style="background: rgba(0,0,0,0.4); padding: 16px; border-radius: 12px; border: 1px solid rgba(255,255,255,0.06);">
          <div style="font-size: 13px; color: #cbd5e1; line-height: 1.5;">
            "Users with token balances over 500 show <strong>4.2x higher 30-day retention</strong> than zero-balance users. Tokens create emotional loss aversion."
          </div>
        </div>
      </div>

      <!-- Step 3 -->
      <div class="glass-card">
        <div style="font-size: 14px; font-weight: 800; color: #10b981; letter-spacing: 1px; margin-bottom: 12px;">STEP 03 // REDEMPTION</div>
        <div style="font-size: 26px; font-weight: 700; color: #ffffff; margin-bottom: 12px;">Physical Streetwear Drops</div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.6; margin-bottom: 24px;">
          At 6,000 Tokens (~30–45 days of discipline), users unlock the right to claim the <strong>T1 Signature Heavyweight Tee</strong>.
        </p>
        <div style="background: rgba(16, 185, 129, 0.08); padding: 16px; border-radius: 12px; border: 1px solid rgba(16, 185, 129, 0.25);">
          <div style="font-size: 13px; color: #10b981; font-weight: 700; margin-bottom: 4px;">THE IRL VIRAL LOOP:</div>
          <div style="font-size: 13px; color: #cbd5e1; line-height: 1.4;">
            Every T-shirt worn in a library, gym, or startup office sparks: <em>"Where did you get that?"</em> $\rightarrow$ <em>"I mined it on T1."</em>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">06 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 7: MARKET OPPORTUNITY (TAM / SAM / SOM) -->
<!-- =================================================================== -->
<section class="slide" id="slide-7">
  <div class="bg-grid"></div>
  <div class="slide-header">
    <div class="tag-pill">07 // MARKET OPPORTUNITY</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">The $42 Billion Focus Economy</h1>
    <p class="slide-subtitle">Digital wellbeing is evolving from an obscure niche into the dominant consumer health category for Gen-Z and knowledge workers.</p>
  </div>

  <div class="slide-body">
    <div class="grid-3" style="align-items: stretch;">
      <!-- TAM -->
      <div class="glass-card" style="text-align: center; padding: 48px 32px;">
        <div class="metric-val white" style="font-size: 72px; margin-bottom: 12px;">$42B</div>
        <div style="font-size: 20px; font-weight: 700; color: #ffffff; margin-bottom: 12px;">TOTAL ADDRESSABLE MARKET (TAM)</div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.5;">
          Global Digital Health, Behavioral Wellness & Human Performance apps, currently expanding at a rapid 19.4% CAGR.
        </p>
      </div>

      <!-- SAM -->
      <div class="glass-card highlight" style="text-align: center; padding: 48px 32px;">
        <div class="metric-val" style="font-size: 72px; margin-bottom: 12px;">$8.4B</div>
        <div style="font-size: 20px; font-weight: 700; color: #38bdf8; margin-bottom: 12px;">SERVICEABLE ADDRESSABLE MARKET (SAM)</div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.5;">
          Gamified productivity, discipline tracking, and study tools targeting 1.2B young digital natives worldwide.
        </p>
      </div>

      <!-- SOM -->
      <div class="glass-card" style="text-align: center; padding: 48px 32px;">
        <div class="metric-val emerald" style="font-size: 72px; margin-bottom: 12px;">$210M</div>
        <div style="font-size: 20px; font-weight: 700; color: #10b981; margin-bottom: 12px;">SERVICEABLE OBTAINABLE MARKET (SOM)</div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.5;">
          Initial capture of 2.1M high-performance university students, coders, founders, and exam aspirants in US, UK, and Tier 1 India.
        </p>
      </div>
    </div>
  </div>

  <!-- Macro Tailwinds Bar -->
  <div style="display: flex; gap: 32px; background: rgba(15, 23, 42, 0.6); padding: 20px 32px; border-radius: 16px; border: 1px solid rgba(255,255,255,0.08); z-index: 10;">
    <div style="flex: 1;">
      <div style="font-size: 14px; font-weight: 700; color: #38bdf8; margin-bottom: 4px;">🌊 CULTURAL BACKLASH</div>
      <div style="font-size: 13px; color: #94a3b8;">50B+ views across #MonkMode, #DopamineDetox, and #DeepWork on social platforms.</div>
    </div>
    <div style="width: 1px; background: rgba(255,255,255,0.1);"></div>
    <div style="flex: 1;">
      <div style="font-size: 14px; font-weight: 700; color: #f59e0b; margin-bottom: 4px;">🎮 GAMIFICATION SHIFT</div>
      <div style="font-size: 13px; color: #94a3b8;">Duolingo proved language learning works via streaks & leaderboards. T1 applies this to human attention.</div>
    </div>
    <div style="width: 1px; background: rgba(255,255,255,0.1);"></div>
    <div style="flex: 1;">
      <div style="font-size: 14px; font-weight: 700; color: #10b981; margin-bottom: 4px;">🔒 PRIVACY REGULATION</div>
      <div style="font-size: 13px; color: #94a3b8;">Global antitrust pressure on Big Tech attention-capture feeds gives independent protocols a massive moat.</div>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">07 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 8: TRACTION & PRODUCT VALIDATION -->
<!-- =================================================================== -->
<section class="slide" id="slide-8">
  <div class="bg-grid"></div>
  <div class="slide-header">
    <div class="tag-pill emerald">08 // TRACTION & METRICS</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">Built. Verified. Ready for Global Scale.</h1>
    <p class="slide-subtitle">Early beta cohort metrics demonstrate exceptional stickiness, high session depth, and virality through 1v1 duels.</p>
  </div>

  <div class="slide-body">
    <!-- Left: 4 Metric Cards -->
    <div class="grid-2" style="flex: 1.4; gap: 24px;">
      <div class="glass-card" style="padding: 28px;">
        <div class="metric-val" style="color: #38bdf8;">38.4 min</div>
        <div class="metric-label" style="margin: 8px 0 4px;">Avg Focus Session Length</div>
        <p class="metric-desc">2.7x higher than standard Pomodoro tools (14 min average). Users lock in longer due to stake loss aversion.</p>
      </div>

      <div class="glass-card" style="padding: 28px;">
        <div class="metric-val emerald">78.2%</div>
        <div class="metric-label" style="margin: 8px 0 4px;">Duel Invite Acceptance Rate</div>
        <p class="metric-desc">When a user sends an @username challenge, 4 out of 5 rivals accept within 10 minutes.</p>
      </div>

      <div class="glass-card" style="padding: 28px;">
        <div class="metric-val amber">3.8x</div>
        <div class="metric-label" style="margin: 8px 0 4px;">Daily Open Frequency</div>
        <p class="metric-desc">Users check their relative rank, token balance, and streak status throughout the workday.</p>
      </div>

      <div class="glass-card" style="padding: 28px;">
        <div class="metric-val" style="color: #a855f7;">4.25 MB</div>
        <div class="metric-label" style="margin: 8px 0 4px;">Ultra-Light Native Binary</div>
        <p class="metric-desc">Zero bloat, R8 minified native Kotlin/Compose architecture. Instant cold start in under 450ms.</p>
      </div>
    </div>

    <!-- Right: Leaderboard Phone Mockup -->
    <div style="flex: 0.8; display: flex; flex-direction: column; align-items: center;">
      <div class="phone-frame" style="width: 280px; height: 580px;">
        <img src="${imgLeaderboard}" alt="T1 Leaderboard">
      </div>
      <div style="margin-top: 14px; font-size: 13px; font-weight: 700; color: #94a3b8; letter-spacing: 1px;">
        GLOBAL & CAMPUS LEADERBOARDS
      </div>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">08 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 9: BUSINESS MODEL & MONETIZATION -->
<!-- =================================================================== -->
<section class="slide" id="slide-9">
  <div class="bg-grid"></div>
  <div class="slide-header">
    <div class="tag-pill">09 // BUSINESS MODEL</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">Multi-Tiered SaaS & Cultural Commerce</h1>
    <p class="slide-subtitle">Combining high-margin recurring software subscriptions with limited-edition luxury merchandise and enterprise team tournaments.</p>
  </div>

  <div class="slide-body">
    <div class="grid-4" style="align-items: stretch;">
      <!-- Stream 1 -->
      <div class="glass-card highlight">
        <div style="font-size: 13px; font-weight: 800; color: #38bdf8; letter-spacing: 1px; margin-bottom: 8px;">RECURRING SAAS</div>
        <div style="font-size: 32px; font-weight: 800; color: #ffffff;">T1 Pro</div>
        <div style="font-size: 20px; font-weight: 700; color: #38bdf8; margin: 8px 0 16px;">$9.99 / mo</div>
        <div style="display: flex; flex-direction: column; gap: 8px; font-size: 13px; color: #94a3b8; line-height: 1.4;">
          <div>• Unlimited 1v1 Focus Duels</div>
          <div>• Advanced Voice AI Focus Coach</div>
          <div>• 2x Token Mining Multiplier</div>
          <div>• Deep Cognitive Analytics</div>
          <div>• Custom OLED Home Widgets</div>
        </div>
      </div>

      <!-- Stream 2 -->
      <div class="glass-card">
        <div style="font-size: 13px; font-weight: 800; color: #f59e0b; letter-spacing: 1px; margin-bottom: 8px;">COMMERCE</div>
        <div style="font-size: 32px; font-weight: 800; color: #ffffff;">Apparel Drops</div>
        <div style="font-size: 20px; font-weight: 700; color: #f59e0b; margin: 8px 0 16px;">65% Margin</div>
        <div style="display: flex; flex-direction: column; gap: 8px; font-size: 13px; color: #94a3b8; line-height: 1.4;">
          <div>• T1 Signature Heavyweight Tee</div>
          <div>• Limited 1,000-unit drops</div>
          <div>• Token-subsidized checkout</div>
          <div>• Physical NFC verification chip</div>
          <div>• Real-world social status</div>
        </div>
      </div>

      <!-- Stream 3 -->
      <div class="glass-card">
        <div style="font-size: 13px; font-weight: 800; color: #10b981; letter-spacing: 1px; margin-bottom: 8px;">B2B SPONSORSHIPS</div>
        <div style="font-size: 32px; font-weight: 800; color: #ffffff;">Brand Arenas</div>
        <div style="font-size: 20px; font-weight: 700; color: #10b981; margin: 8px 0 16px;">$25k - $100k</div>
        <div style="display: flex; flex-direction: column; gap: 8px; font-size: 13px; color: #94a3b8; line-height: 1.4;">
          <div>• Sponsored focus tournaments</div>
          <div>• Notion, Red Bull, Whoop, Ray-Ban</div>
          <div>• Native brand reward pools</div>
          <div>• High Gen-Z builder affinity</div>
          <div>• Non-intrusive focus alignment</div>
        </div>
      </div>

      <!-- Stream 4 -->
      <div class="glass-card">
        <div style="font-size: 13px; font-weight: 800; color: #a855f7; letter-spacing: 1px; margin-bottom: 8px;">ENTERPRISE B2B</div>
        <div style="font-size: 32px; font-weight: 800; color: #ffffff;">Team Guilds</div>
        <div style="font-size: 20px; font-weight: 700; color: #a855f7; margin: 8px 0 16px;">$5 / seat / mo</div>
        <div style="display: flex; flex-direction: column; gap: 8px; font-size: 13px; color: #94a3b8; line-height: 1.4;">
          <div>• Engineering team leaderboards</div>
          <div>• University collegiate focus leagues</div>
          <div>• "Deep Work Hours" sync</div>
          <div>• Reductions in workplace burnout</div>
          <div>• High enterprise retention</div>
        </div>
      </div>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">09 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 10: COMPETITIVE ADVANTAGE & MOAT -->
<!-- =================================================================== -->
<section class="slide" id="slide-10">
  <div class="bg-grid"></div>
  <div class="slide-header">
    <div class="tag-pill">10 // COMPETITIVE LANDSCAPE</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">Why T1 Out-Executes the Incumbents</h1>
    <p class="slide-subtitle">Existing tools rely on childlike gamification or passive guilt. T1 introduces real competitive stakes and high-status design.</p>
  </div>

  <div class="slide-body">
    <!-- Comparison Matrix Table -->
    <div style="width: 100%; background: rgba(15, 23, 42, 0.7); border: 1px solid rgba(255,255,255,0.08); border-radius: 20px; overflow: hidden;">
      <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 15px;">
        <thead>
          <tr style="background: rgba(0,0,0,0.4); border-bottom: 1px solid rgba(255,255,255,0.08);">
            <th style="padding: 20px 24px; color: #94a3b8; font-weight: 700; width: 28%;">CAPABILITY / FEATURE</th>
            <th style="padding: 20px 24px; color: #38bdf8; font-weight: 800; font-size: 18px; width: 24%; background: rgba(56, 189, 248, 0.08); border-left: 2px solid #38bdf8; border-right: 2px solid #38bdf8;">T1 PROTOCOL</th>
            <th style="padding: 20px 24px; color: #94a3b8; font-weight: 600; width: 16%;">OPAL</th>
            <th style="padding: 20px 24px; color: #94a3b8; font-weight: 600; width: 16%;">FOREST</th>
            <th style="padding: 20px 24px; color: #94a3b8; font-weight: 600; width: 16%;">APPLE / GOOGLE</th>
          </tr>
        </thead>
        <tbody>
          <tr style="border-bottom: 1px solid rgba(255,255,255,0.04);">
            <td style="padding: 16px 24px; color: #ffffff; font-weight: 600;">1v1 Peer-to-Peer Duel Staking</td>
            <td style="padding: 16px 24px; color: #38bdf8; font-weight: 800; background: rgba(56, 189, 248, 0.04); border-left: 2px solid #38bdf8; border-right: 2px solid #38bdf8;">✓ YES (10–100 Stakes)</td>
            <td style="padding: 16px 24px; color: #64748b;">✗ No</td>
            <td style="padding: 16px 24px; color: #64748b;">✗ No</td>
            <td style="padding: 16px 24px; color: #64748b;">✗ No</td>
          </tr>
          <tr style="border-bottom: 1px solid rgba(255,255,255,0.04);">
            <td style="padding: 16px 24px; color: #ffffff; font-weight: 600;">Proof-of-Focus Token Mining</td>
            <td style="padding: 16px 24px; color: #38bdf8; font-weight: 800; background: rgba(56, 189, 248, 0.04); border-left: 2px solid #38bdf8; border-right: 2px solid #38bdf8;">✓ YES (Deflationary)</td>
            <td style="padding: 16px 24px; color: #64748b;">✗ No</td>
            <td style="padding: 16px 24px; color: #64748b;">Virtual trees only</td>
            <td style="padding: 16px 24px; color: #64748b;">✗ No</td>
          </tr>
          <tr style="border-bottom: 1px solid rgba(255,255,255,0.04);">
            <td style="padding: 16px 24px; color: #ffffff; font-weight: 600;">Physical Merchandise Redemption</td>
            <td style="padding: 16px 24px; color: #38bdf8; font-weight: 800; background: rgba(56, 189, 248, 0.04); border-left: 2px solid #38bdf8; border-right: 2px solid #38bdf8;">✓ YES (T1 Signature Tee)</td>
            <td style="padding: 16px 24px; color: #64748b;">✗ No</td>
            <td style="padding: 16px 24px; color: #64748b;">✗ No</td>
            <td style="padding: 16px 24px; color: #64748b;">✗ No</td>
          </tr>
          <tr style="border-bottom: 1px solid rgba(255,255,255,0.04);">
            <td style="padding: 16px 24px; color: #ffffff; font-weight: 600;">Design Aesthetic</td>
            <td style="padding: 16px 24px; color: #38bdf8; font-weight: 800; background: rgba(56, 189, 248, 0.04); border-left: 2px solid #38bdf8; border-right: 2px solid #38bdf8;">Dark OLED Luxury</td>
            <td style="padding: 16px 24px; color: #cbd5e1;">Pastel Minimal</td>
            <td style="padding: 16px 24px; color: #cbd5e1;">Cartoon / Childlike</td>
            <td style="padding: 16px 24px; color: #64748b;">System Utility</td>
          </tr>
          <tr style="border-bottom: 1px solid rgba(255,255,255,0.04);">
            <td style="padding: 16px 24px; color: #ffffff; font-weight: 600;">Privacy & On-Device Security</td>
            <td style="padding: 16px 24px; color: #38bdf8; font-weight: 800; background: rgba(56, 189, 248, 0.04); border-left: 2px solid #38bdf8; border-right: 2px solid #38bdf8;">100% On-Device (No VPN)</td>
            <td style="padding: 16px 24px; color: #f87171;">Local VPN routing</td>
            <td style="padding: 16px 24px; color: #cbd5e1;">Basic Timer</td>
            <td style="padding: 16px 24px; color: #cbd5e1;">OS Built-in</td>
          </tr>
          <tr>
            <td style="padding: 16px 24px; color: #ffffff; font-weight: 600;">Pricing / Value Model</td>
            <td style="padding: 16px 24px; color: #38bdf8; font-weight: 800; background: rgba(56, 189, 248, 0.04); border-left: 2px solid #38bdf8; border-right: 2px solid #38bdf8;">Freemium + Token Rewards</td>
            <td style="padding: 16px 24px; color: #f87171;">$99.99/year (Expensive)</td>
            <td style="padding: 16px 24px; color: #cbd5e1;">$3.99 one-time</td>
            <td style="padding: 16px 24px; color: #64748b;">Free (Ignored)</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">10 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 11: 18-MONTH ROADMAP -->
<!-- =================================================================== -->
<section class="slide" id="slide-11">
  <div class="bg-grid"></div>
  <div class="slide-header">
    <div class="tag-pill">11 // ROADMAP & EXECUTION</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">18-Month Execution Blueprint</h1>
    <p class="slide-subtitle">A disciplined product rollout scaling from individual Android power users to campus guilds and ambient hardware.</p>
  </div>

  <div class="slide-body">
    <div class="grid-3" style="align-items: stretch;">
      <!-- Phase 1 -->
      <div class="glass-card highlight">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
          <span class="badge cyan">PHASE 01 // Q1–Q2 2027</span>
          <span style="font-size: 13px; color: #38bdf8; font-weight: bold;">VIRALITY</span>
        </div>
        <div style="font-size: 24px; font-weight: 700; color: #ffffff; margin-bottom: 16px;">Global Rollout & Squads</div>
        <div style="display: flex; flex-direction: column; gap: 14px; font-size: 14px; color: #cbd5e1; line-height: 1.45;">
          <div>• <strong>Android Global Launch:</strong> 100k targeted pre-registrations across university builder networks.</div>
          <div>• <strong>iOS Companion App:</strong> Swift/SwiftUI client with Apple Screen Time API parity.</div>
          <div>• <strong>Squad Focus Wars:</strong> 5v5 group focus battles (Study groups, dorms, startup batches).</div>
          <div>• <strong>Drop 001 Fulfillment:</strong> First batch of 2,500 T1 Signature Tees delivered to top miners.</div>
        </div>
      </div>

      <!-- Phase 2 -->
      <div class="glass-card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
          <span class="badge amber">PHASE 02 // Q3–Q4 2027</span>
          <span style="font-size: 13px; color: #f59e0b; font-weight: bold;">MONETIZATION</span>
        </div>
        <div style="font-size: 24px; font-weight: 700; color: #ffffff; margin-bottom: 16px;">Biometrics & Enterprise</div>
        <div style="display: flex; flex-direction: column; gap: 14px; font-size: 14px; color: #cbd5e1; line-height: 1.45;">
          <div>• <strong>WearOS & Apple Watch Sync:</strong> Live Heart Rate Variability (HRV) telemetry detecting cognitive stress and flow state.</div>
          <div>• <strong>T1 Campus League:</strong> 50+ Top US/UK/India Universities competing for collegiate trophies.</div>
          <div>• <strong>B2B Team Dashboards:</strong> Self-serve focus analytics for engineering and remote teams.</div>
          <div>• <strong>Creator Tournament Engine:</strong> Productivity YouTubers hosting $10k+ focus arenas.</div>
        </div>
      </div>

      <!-- Phase 3 -->
      <div class="glass-card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
          <span class="badge emerald">PHASE 03 // 2028</span>
          <span style="font-size: 13px; color: #10b981; font-weight: bold;">ECOSYSTEM</span>
        </div>
        <div style="font-size: 24px; font-weight: 700; color: #ffffff; margin-bottom: 16px;">T1 Ambient Hardware</div>
        <div style="display: flex; flex-direction: column; gap: 14px; font-size: 14px; color: #cbd5e1; line-height: 1.45;">
          <div>• <strong>T1 Focus Puck:</strong> An anodized aluminum desktop companion with tactile mechanical dial that lights up during duels.</div>
          <div>• <strong>Global Collegiate Focus Olympics:</strong> Annual live arena tournament awarding $100k in university scholarships.</div>
          <div>• <strong>Focus Protocol API:</strong> Third-party integrations (VS Code, Figma, Notion) earning proof-of-focus rewards.</div>
        </div>
      </div>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026</div>
    <div class="page-number">11 / 12</div>
  </div>
</section>

<!-- =================================================================== -->
<!-- SLIDE 12: THE ASK & VISION -->
<!-- =================================================================== -->
<section class="slide" id="slide-12" style="justify-content: space-between;">
  <div class="bg-grid"></div>
  <div class="bg-glow-cyan" style="top: 10%; left: 20%;"></div>
  <div class="bg-glow-amber" style="bottom: 15%; right: 20%;"></div>

  <div class="slide-header">
    <div class="tag-pill">12 // THE INVESTMENT ASK</div>
    <div class="header-brand"><strong>T1.</strong> INVESTOR DECK</div>
  </div>

  <div>
    <h1 class="slide-title">Join Us in Reclaiming 1 Billion Hours</h1>
    <p class="slide-subtitle">We are raising a $1.5M Seed Round to accelerate mobile distribution, launch collegiate leagues, and scale the physical commerce flywheel.</p>
  </div>

  <div class="slide-body" style="gap: 40px; margin: 20px 0;">
    <!-- Left: Use of Funds -->
    <div class="glass-card highlight" style="flex: 1.1; padding: 36px;">
      <div style="font-size: 14px; font-weight: 800; color: #38bdf8; letter-spacing: 1px; margin-bottom: 8px;">ROUND DETAILS</div>
      <div style="font-size: 48px; font-weight: 800; color: #ffffff; margin-bottom: 24px;">$1,500,000 <span style="font-size: 20px; color: #94a3b8; font-weight: 500;">(SAFE / Equity)</span></div>

      <div style="display: flex; flex-direction: column; gap: 16px;">
        <div>
          <div style="display: flex; justify-content: space-between; font-size: 15px; font-weight: 600; margin-bottom: 6px;">
            <span>45% Engineering & Edge AI</span>
            <span style="color: #38bdf8;">$675,000</span>
          </div>
          <div style="width: 100%; height: 6px; background: rgba(255,255,255,0.08); border-radius: 3px; overflow: hidden;">
            <div style="width: 45%; height: 100%; background: #38bdf8;"></div>
          </div>
          <div style="font-size: 12px; color: #64748b; margin-top: 4px;">iOS development, low-latency multiplayer duel sync, and Edge ML algorithms.</div>
        </div>

        <div>
          <div style="display: flex; justify-content: space-between; font-size: 15px; font-weight: 600; margin-bottom: 6px;">
            <span>30% Growth & Creator Network</span>
            <span style="color: #f59e0b;">$450,000</span>
          </div>
          <div style="width: 100%; height: 6px; background: rgba(255,255,255,0.08); border-radius: 3px; overflow: hidden;">
            <div style="width: 30%; height: 100%; background: #f59e0b;"></div>
          </div>
          <div style="font-size: 12px; color: #64748b; margin-top: 4px;">Collegiate study guild ambassadorships, viral TikTok/Reels focus tournaments.</div>
        </div>

        <div>
          <div style="display: flex; justify-content: space-between; font-size: 15px; font-weight: 600; margin-bottom: 6px;">
            <span>15% Streetwear Supply Chain & Drops</span>
            <span style="color: #10b981;">$225,000</span>
          </div>
          <div style="width: 100%; height: 6px; background: rgba(255,255,255,0.08); border-radius: 3px; overflow: hidden;">
            <div style="width: 15%; height: 100%; background: #10b981;"></div>
          </div>
          <div style="font-size: 12px; color: #64748b; margin-top: 4px;">Premium heavyweight cotton inventory, NFC garment chips, and global fulfillment.</div>
        </div>

        <div>
          <div style="display: flex; justify-content: space-between; font-size: 15px; font-weight: 600; margin-bottom: 6px;">
            <span>10% Operations & Compliance</span>
            <span style="color: #a855f7;">$150,000</span>
          </div>
          <div style="width: 100%; height: 6px; background: rgba(255,255,255,0.08); border-radius: 3px; overflow: hidden;">
            <div style="width: 10%; height: 100%; background: #a855f7;"></div>
          </div>
          <div style="font-size: 12px; color: #64748b; margin-top: 4px;">Play Store / App Store compliance, security audits, and legal counsel.</div>
        </div>
      </div>
    </div>

    <!-- Right: Vision & Contact Card -->
    <div class="glass-card" style="flex: 0.9; padding: 36px; display: flex; flex-direction: column; justify-content: space-between;">
      <div>
        <div style="font-size: 14px; font-weight: 800; color: #f59e0b; letter-spacing: 1px; margin-bottom: 12px;">OUR NORTH STAR</div>
        <div style="font-size: 28px; font-weight: 700; color: #ffffff; line-height: 1.35; margin-bottom: 20px;">
          "The next decacorn will not be another app that steals your attention. It will be the protocol that gives it back."
        </div>
        <p style="font-size: 15px; color: #94a3b8; line-height: 1.6;">
          T1 turns human agency into the world's most coveted asset. When focus is scarce, the protocol that gamifies and validates it owns the future.
        </p>
      </div>

      <div style="padding-top: 28px; border-top: 1px solid rgba(255,255,255,0.08);">
        <div style="font-size: 13px; font-weight: 700; color: #64748b; letter-spacing: 1px; margin-bottom: 8px;">GET IN TOUCH</div>
        <div style="font-size: 22px; font-weight: 700; color: #38bdf8; margin-bottom: 4px;">founders@t1focus.com</div>
        <div style="font-size: 14px; color: #94a3b8;">Interactive Demo & Live APK available upon request.</div>
      </div>
    </div>
  </div>

  <div class="slide-footer">
    <div class="confidential">T1 PROTOCOL // SEED ROUND 2026 // ALL RIGHTS RESERVED</div>
    <div class="page-number">12 / 12</div>
  </div>
</section>

</body>
</html>
`;

fs.writeFileSync(OUT_HTML, htmlContent, 'utf8');
console.log(`Wrote complete HTML deck to ${OUT_HTML}`);

// Render to PDF using Chrome
const cmd = `"${CHROME}" --headless --disable-gpu --no-pdf-header-footer --print-to-pdf="${OUT_PDF}" "${OUT_HTML}"`;
console.log("Rendering 12-page PDF via Chrome headless...");
try {
  execSync(cmd, { stdio: 'inherit' });
  console.log(`\n========================================`);
  console.log(`🎉 SUCCESS: ${OUT_PDF} generated!`);
  const stats = fs.statSync(OUT_PDF);
  console.log(`PDF File Size: ${(stats.size / (1024 * 1024)).toFixed(2)} MB`);
  console.log(`========================================\n`);
} catch (err) {
  console.error("Error generating PDF:", err.message);
  process.exit(1);
}
