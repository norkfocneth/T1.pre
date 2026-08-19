# Privacy Policy for T1 (Top 1%)

**Last Updated / Effective Date:** August 19, 2026  
**App Name:** T1 — Focus & Digital Behaviour Tracker (`com.focneth.t1`)  
**Developer / Organization:** Focneth (Focneth Innovations)  
**Contact Email:** privacy@focneth.com / support@focneth.com  
**Privacy Policy URL:** https://focneth.com/t1/privacy  
**Account & Data Deletion URL:** https://focneth.com/t1/delete-account  

---

## 1. Introduction & Overview

Welcome to **T1** ("we", "our", "us", or the "App"). T1 is a digital wellness, productivity enhancement, and focus tracking application developed by **Focneth**. Our mission is to empower individuals to master their digital habits, reclaim their attention, and elevate their productivity using an objective, algorithmically computed **Focus Score (0–100)**.

We believe your privacy is fundamental. T1 is architected from the ground up with a **Privacy-by-Design** and **Local-First Processing** philosophy:
- **Your raw application usage, screen time events, and digital activity are analyzed strictly ON YOUR DEVICE.**
- **We NEVER collect, read, or transmit your keystrokes, personal messages, chats, photos, videos, browsing history, or in-app content.**
- **We DO NOT sell, rent, or monetize your personal data with third-party advertisers or data brokers.**

This Privacy Policy explains what information we collect, how it is processed, why sensitive permissions are required, our cloud synchronization practices, and how you can exercise full control over your data (including **instant account and data deletion**).

---

## 2. Information We Collect and How We Use It

### A. Information You Provide Directly
When you create an account, complete onboarding, or customize your profile, you may provide:
1. **Account Credentials:** Email address, username, display name, and authentication tokens (managed securely via Supabase Auth).
2. **Onboarding & Goal Preferences:** Age range, gender (optional), occupation (optional), and your primary digital wellness goal (e.g., *Productivity*, *Social Media Reduction*, *Education*, *Entertainment Control*).
3. **Avatar / Profile Customization:** Profile avatar selection or custom URL.

### B. Information Analyzed Locally on Your Device (Usage Data)
To calculate your Focus Score, categorize screen time, and evaluate daily goal improvements, T1 inspects local Android system metrics:
1. **Application Foreground Time:** Duration spent inside individual apps on your device.
2. **App Launches & Device Unlocks:** Count of device unlocks and frequency of opening applications.
3. **Category Aggregations:** Time distributed across 6 functional categories (*Productivity*, *Education*, *Communication*, *Social Time*, *Entertainment*, *Utility*).
4. **7-Day Rolling Baseline:** Historical 7-day average usage used to compute realistic percentage and minute deltas (e.g., "+25 min more than yesterday").

> **CRITICAL PRIVACY GUARANTEE:**  
> Detailed per-second app usage sessions and timestamps remain in a private, encrypted local SQLite/Room database on your device. Only high-level daily aggregated scores and summary percentages are synchronized to our secure cloud.

### C. Focus Sessions & Gamification Data
1. **Focus Sessions:** Duration of completed deep work timer sessions.
2. **Streak & Consistency:** Daily streak counter and bonus multipliers.
3. **T1 Token Wallet:** Virtual token balances earned through daily logins, maintaining streaks, and hitting focus milestones.
4. **Leaderboard Ranks:** Your username, display name, avatar, and daily/weekly Focus Score for public leaderboard competition.

### D. Information We NEVER Collect
T1 strictly avoids collecting intrusive personal data:
- ❌ **NO Messages / Chats / Emails:** We cannot and do not read any messages or text conversations.
- ❌ **NO Notification Content:** We do not read the text or payload of incoming notifications from other apps.
- ❌ **NO Keystrokes or Input:** We do not track what you type.
- ❌ **NO Media, Photos, or Files:** We do not access your gallery, camera, microphone, or storage files.
- ❌ **NO GPS Location:** We do not track or store your real-time physical GPS coordinates.
- ❌ **NO Financial / Payment Data:** We do not collect credit card or banking details.

---

## 3. Android Permissions & Prominent Disclosures

To perform its core digital wellness functionality, T1 requests specific Android permissions. In compliance with **Google Play Developer Policies**, here is the transparent disclosure for each:

| Permission | Technical Name | Purpose & Justification | Data Storage Location |
| :--- | :--- | :--- | :--- |
| **Usage Access** | `android.permission.PACKAGE_USAGE_STATS` | **Core Feature:** Required to calculate your Focus Score, categorize app screen time, detect distraction patterns, and measure daily goal performance vs. 7-day baseline. | **Processed 100% on-device**; raw logs never uploaded. |
| **Query Installed Apps** | `android.permission.QUERY_ALL_PACKAGES` | **Core Feature:** Allows T1 to map installed application package names to human-readable app names and categorize them into Productivity, Social, Education, etc. | **Processed 100% on-device**. |
| **Notifications** | `android.permission.POST_NOTIFICATIONS` | Delivers daily focus score summaries, streak warnings, token reward alerts, and focus timer completion alerts. | On-device notification trigger. |
| **Boot Completed** | `android.permission.RECEIVE_BOOT_COMPLETED` | Reschedules local background worker tasks (WorkManager) when the device is restarted to ensure continuous daily calculation. | On-device execution. |
| **Network Access** | `android.permission.INTERNET` & `ACCESS_NETWORK_STATE` | Authenticates your account, syncs aggregated focus scores and token balances to Supabase, and updates public leaderboard rankings. | Transmitted securely over HTTPS/TLS. |
| **Vibration / Haptics** | `android.permission.VIBRATE` | Provides tactile haptic feedback during timer operations, button taps, and goal achievements. | On-device. |

---

## 4. How We Use Your Data

We use the collected information strictly for:
1. **Focus Score Engine & Habit Analytics:** Computing your daily Focus Score (0–100) and providing comparative daily deltas.
2. **AI Focus Coach:** Generating contextual, automated behavioral tips based on your aggregated productivity vs. distraction ratios.
3. **Leaderboard & Social Motivation:** Ranking users anonymously or by username to foster positive, competitive discipline.
4. **Gamification & Rewards:** Awarding T1 Tokens for maintaining focus habits and unlocking milestones.
5. **Account Management & Synchronization:** Allowing you to log in across devices and restore your focus history.

---

## 5. Data Sharing & Third-Party Service Providers

We **DO NOT** sell, trade, or monetize your personal data. We only share information with trusted infrastructure providers necessary to operate the application:

1. **Supabase (Backend-as-a-Service / PostgreSQL):**
   - **Purpose:** Secure user authentication, profile storage, encrypted score backups, token ledger, and leaderboard cache.
   - **Privacy Policy:** [https://supabase.com/privacy](https://supabase.com/privacy)
   - **Location & Security:** Hosted on secure enterprise cloud infrastructure with end-to-end encryption and Row-Level Security (RLS).
2. **Legal Requirements:** We may disclose your information only if required by applicable law, regulation, legal process, or governmental request.

**No Third-Party Advertising:** T1 contains **zero third-party advertisements**, no third-party ad SDKs (such as AdMob, Meta Audience Network, or Unity Ads), and no data broker tracking SDKs.

---

## 6. Data Security & Storage Practices

We implement industry-standard technical and organizational security measures:
- **Encryption in Transit:** All communications between the T1 app and cloud servers use **HTTPS with TLS 1.3 encryption**.
- **Encryption at Rest:** Cloud database tables are encrypted at rest with PostgreSQL Row-Level Security (RLS), restricting access strictly to authenticated user sessions.
- **Local Sandbox Isolation:** Local Android Room SQLite databases are stored within the application's private sandboxed directory, inaccessible to other apps.

---

## 7. Data Retention and Instant Account Deletion Policy

In compliance with the **Google Play User Data & Account Deletion Policy**, T1 gives users complete, instantaneous control over their data.

### A. Instant In-App Account & Data Deletion (Self-Serve)
You can permanently delete your entire account and all associated data at any time directly from the app:
1. Open **T1**.
2. Navigate to **User Profile / Settings (⚙️ icon)**.
3. Scroll down to the **Danger Zone** section.
4. Tap **"Delete Account"** and confirm.

### B. What Happens Immediately Upon Deletion:
- **Cloud Database (Supabase):** Your user profile, authentication record, historical focus scores, leaderboard entries, and token transaction logs are **PERMANENTLY AND IRREVOCABLY PURGED INSTANTLY (0 Days Retention / Immediate Purge)**.
- **Local Device Storage (Room / SQLite):** All cached daily usage statistics, category summaries, behavior scores, session records, and authentication tokens are **wiped completely from the device storage**.
- **Active Sessions:** All active tokens and sessions are immediately revoked.

### C. Web / Email Data Deletion Request (Alternative Method)
If you have uninstalled the app or cannot access your device, you can request full account deletion via our web portal or email:
- **Deletion Request Portal:** [https://focneth.com/t1/delete-account](https://focneth.com/t1/delete-account)
- **Email:** Send an email from your registered address to **privacy@focneth.com** with the subject line *"Account Deletion Request"*.
- **Processing Time:** Web/email requests are processed and confirmed within **24 to 48 hours**.

---

## 8. International Privacy Rights & Compliance

### A. GDPR (General Data Protection Regulation - EU / EEA Users)
If you reside in the European Economic Area (EEA) or UK, you have the following rights under GDPR:
- **Right to Access:** Request a copy of the personal data we hold about you.
- **Right to Rectification:** Update or correct inaccuracies in your personal profile.
- **Right to Erasure ("Right to be Forgotten"):** Delete your account and all associated records immediately via Settings.
- **Right to Restrict or Object to Processing:** Revoke usage access permissions at any time via Android System Settings.
- **Right to Data Portability:** Request an export of your historical focus score metrics.

### B. CCPA / CPRA (California Consumer Privacy Act)
If you are a California resident:
- **No Sale of Personal Information:** We **do not sell or share** your personal information as defined under CCPA/CPRA.
- **Right to Know and Delete:** You have the right to know what personal information is collected and request its permanent deletion without discrimination.

### C. Children’s Privacy (COPPA Compliance)
T1 is intended for individuals aged **13 and older** (or 16 and older in certain jurisdictions). We do not knowingly collect or solicit personal information from children under 13. If we discover that a child under 13 has provided personal data, we will immediately delete it from our servers.

---

## 9. Changes to This Privacy Policy

We may periodically update this Privacy Policy to reflect changes in our app features, underlying technology, or legal requirements. When changes are made:
- We will update the **"Effective Date"** at the top of this document.
- Significant changes will be prominently communicated via in-app banner notifications or email.
- Continued use of T1 after changes become effective constitutes acceptance of the revised policy.

---

## 10. Contact Information

If you have any questions, concerns, feedback, or privacy-related requests regarding this Privacy Policy or your personal data, please contact our Data Protection Officer / Support Team:

- **Entity:** Focneth (Focneth Innovations)
- **Email:** privacy@focneth.com / support@focneth.com
- **Website:** [https://focneth.com](https://focneth.com)
- **Privacy Center:** [https://focneth.com/t1/privacy](https://focneth.com/t1/privacy)
- **Data Deletion URL:** [https://focneth.com/t1/delete-account](https://focneth.com/t1/delete-account)
