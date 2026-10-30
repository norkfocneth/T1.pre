const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const chrome = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const out = path.join(__dirname, 'official_launcher_icon.png');
const inp = path.join(__dirname, 'icon_preview.html');

try {
  execSync(`"${chrome}" --headless --disable-gpu --screenshot="${out}" --window-size=192,192 "${inp}"`, { stdio: 'inherit' });
  console.log("Success! File exists:", fs.existsSync(out));
} catch (e) {
  console.error("Error:", e.message);
}

