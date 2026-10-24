const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const chromePath = 'C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe';
const htmlSource = fs.readFileSync(path.join(__dirname, 'premium_minimalist_duel.html'), 'utf8');
const artifactsDir = 'C:\\Users\\FOCNETH\\.gemini\\antigravity-ide\\brain\\e0423f06-d957-44f9-adca-d7533627537a';

const screens = [
  { id: 1, name: 't1_minimalist_screen_1_search_found.png' },
  { id: 2, name: 't1_minimalist_screen_2_search_not_found.png' },
  { id: 3, name: 't1_minimalist_screen_3_incoming_challenge_modal.png' },
  { id: 4, name: 't1_minimalist_screen_4_live_split_arena.png' },
  { id: 5, name: 't1_minimalist_screen_5_victory_screen.png' }
];

screens.forEach(screen => {
  let modifiedHtml = htmlSource;
  
  // Strip active from all screens
  modifiedHtml = modifiedHtml.replace(/class="([^"]*)\bactive\b([^"]*)"/g, 'class="$1 $2"');
  
  // Add active to targeted screen
  const targetRegex = new RegExp(`id="screen-${screen.id}"\\s+class="([^"]*)"`);
  modifiedHtml = modifiedHtml.replace(targetRegex, `id="screen-${screen.id}" class="$1 active"`);
  
  const tempHtmlPath = path.join(__dirname, `_temp_screen_${screen.id}.html`);
  fs.writeFileSync(tempHtmlPath, modifiedHtml, 'utf8');

  const outPlayStore = path.join(__dirname, screen.name);
  const outArtifact = path.join(artifactsDir, screen.name);

  console.log(`Rendering Screen ${screen.id}: ${screen.name}...`);
  const cmd = `"${chromePath}" --headless --disable-gpu --virtual-time-budget=2000 --window-size=1080,2400 --hide-scrollbars --screenshot="${outPlayStore}" "file:///${tempHtmlPath.replace(/\\/g, '/')}"`;
  execSync(cmd, { stdio: 'inherit' });

  // Copy to artifacts dir
  fs.copyFileSync(outPlayStore, outArtifact);
  console.log(`Copied to artifact dir: ${outArtifact}`);

  // Cleanup temp html
  if (fs.existsSync(tempHtmlPath)) {
    fs.unlinkSync(tempHtmlPath);
  }
});

console.log('All 5 minimalist & premium screens successfully rendered!');
