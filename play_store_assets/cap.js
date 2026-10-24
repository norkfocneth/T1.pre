const { execSync } = require('child_process');
const fs = require('path');
const path = require('path');
const ADB = `${process.env.LOCALAPPDATA}\\Android\\Sdk\\platform-tools\\adb.exe`;

// Dump current UI hierarchy or take screenshot
const outPng = "C:\\Users\\FOCNETH\\OneDrive\\Desktop\\T1.pre\\play_store_assets\\current_emu.png";
execSync(`"${ADB}" -s emulator-5554 shell screencap /sdcard/screen.png`);
execSync(`"${ADB}" -s emulator-5554 pull /sdcard/screen.png "${outPng}"`);
console.log("Captured:", outPng);
