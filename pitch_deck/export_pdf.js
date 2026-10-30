const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const chrome = "C:\\Users\\FOCNETH\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe";
const inpHtml = path.join(__dirname, 'T1_PitchDeck_v5_Action_Visual_Story.html');
const outPdf = path.join(__dirname, 'T1_PitchDeck_v5_Action_Visual_Story.pdf');
const latestPdf = path.join(__dirname, 'T1_Latest_Pitch_Deck.pdf');
const indexHtml = path.join(__dirname, 'index.html');

const desktopPdfV5 = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1_PitchDeck_v5_Action_Visual_Story.pdf";
const desktopPdfLatest = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1_Latest_Pitch_Deck.pdf";

console.log("Generating Pitch Deck v5 (Action & Visual Story) from:", inpHtml);
try {
  execSync(`"${chrome}" --headless=new --disable-gpu --virtual-time-budget=6000 --run-all-compositor-stages-before-draw --no-pdf-header-footer --print-to-pdf="${outPdf}" "${inpHtml}"`, { stdio: 'inherit' });
  
  if (fs.existsSync(outPdf)) {
    const stats = fs.statSync(outPdf);
    console.log("PDF generated! Size:", stats.size, "bytes");

    // Copy to latest & index in pitch_deck
    fs.copyFileSync(outPdf, latestPdf);
    fs.copyFileSync(inpHtml, indexHtml);

    // Copy to desktop
    fs.copyFileSync(outPdf, desktopPdfV5);
    fs.copyFileSync(outPdf, desktopPdfLatest);
    console.log("Synced successfully to:", desktopPdfV5);
    console.log("Synced successfully to:", desktopPdfLatest);
  } else {
    console.error("PDF file not found after generation.");
  }
} catch (e) {
  console.error("PDF generation failed:", e.message);
}
