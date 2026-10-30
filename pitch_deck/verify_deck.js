const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const chrome = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const artifactDir = "C:\\Users\\FOCNETH\\.gemini\\antigravity\\brain\\e0423f06-d957-44f9-adca-d7533627537a";
const htmlPath = path.join(__dirname, 'T1_Hyperelastic_Minimalist_Deck.html');

console.log("Checking slides rendering...");
const outCover = path.join(artifactDir, 'deck_slide_1_cover.png');
const outDuel = path.join(artifactDir, 'deck_slide_4_duel.png');
const outNotif = path.join(artifactDir, 'deck_slide_5_oem.png');
const outDual = path.join(artifactDir, 'deck_slide_6_classification.png');

try {
  // Capture cover
  execSync(`"${chrome}" --headless --disable-gpu --screenshot="${outCover}" --window-size=1920,1080 "${htmlPath}#slide-1"`);
  console.log("Cover captured:", fs.existsSync(outCover));
  
  // Also copy PDF to Desktop for immediate easy access
  const desktopPdf = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1_Hyperelastic_Minimalist_Pitch_Deck.pdf";
  fs.copyFileSync(path.join(__dirname, 'T1_Hyperelastic_Minimalist_Pitch_Deck.pdf'), desktopPdf);
  console.log("PDF copied to Desktop:", fs.existsSync(desktopPdf));
} catch (e) {
  console.error("Capture error:", e.message);
}

