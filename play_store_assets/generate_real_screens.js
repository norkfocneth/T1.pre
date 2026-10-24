const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const CHROME = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const ARTIFACT_DIR = "C:\\Users\\FOCNETH\\.gemini\\antigravity-ide\\brain\\e0423f06-d957-44f9-adca-d7533627537a";

const baseHtml = fs.readFileSync(path.join(__dirname, 'premium_duel_experience.html'), 'utf-8');

const screens = [
  { id: 'screen-1-found', filename: 'premium_screen_1_search_found.png' },
  { id: 'screen-2-not-found', filename: 'premium_screen_2_search_not_found.png' },
  { id: 'screen-3-modal', filename: 'premium_screen_3_incoming_challenge_modal.png' },
  { id: 'screen-4-live-arena', filename: 'premium_screen_4_live_split_arena.png' },
  { id: 'screen-5-victory', filename: 'premium_screen_5_victory_screen.png' }
];

for (const s of screens) {
  const targetRegex = new RegExp(`<div id="${s.id}"[\\s\\S]*?<\\/div>\\s*(?=<div id="screen-|<\\/body>)`);
  const divMatch = baseHtml.match(targetRegex);
  const singleScreenHtml = baseHtml.replace(/<div id="screen-1-found"[\s\S]*<\/body>/, (divMatch ? divMatch[0] : '') + '\n</body>');

  const tempHtmlPath = path.join(__dirname, `temp_${s.id}.html`);
  fs.writeFileSync(tempHtmlPath, singleScreenHtml, 'utf-8');

  const outPath = path.join(ARTIFACT_DIR, s.filename);
  console.log(`Rendering ${s.id} -> ${s.filename}...`);
  execSync(`"${CHROME}" --headless --disable-gpu --hide-scrollbars --screenshot="${outPath}" --window-size=1080,2400 "${tempHtmlPath}"`);
  console.log(`Rendered: ${outPath}`);

  if (fs.existsSync(tempHtmlPath)) fs.unlinkSync(tempHtmlPath);
}

console.log("All premium screens rendered successfully!");
