# Privacy Policy for T1: Top 1%

**Last Updated: August 29, 2026**  
**Effective Date: August 29, 2026**  
**Developer / Organization: Focneth**  
**Application Package: com.focneth.t1**  
**Contact Email: support.t1.help@gmail.com**  
**Official Website: https://pixel-perfect-replica-tawny-beta.vercel.app/home**  
**Web Privacy Policy: https://pixel-perfect-replica-tawny-beta.vercel.app/home/privacy**  

---

## 1. Introduction & Core Philosophy
Welcome to **T1: Top 1%** ("T1", "we", "our", or "us"), developed by **Focneth**. T1 is a digital discipline, focus optimization, and habit-tracking ecosystem designed to help ambitious students, founders, and professionals master their digital attention and reclaim deep work capacity.

We hold a fundamental belief: **Your digital behavior and focus data belong strictly to you.** 

Unlike traditional platforms that monetize your attention through ad tracking and data brokerage, T1 is built on a **Local-First, Privacy-by-Design architecture**. This Privacy Policy explains transparently what data we process on your device, what minimal information is synchronized with our secure cloud servers, and your complete rights to your personal data.

---

## 2. On-Device Local Processing (Data That Never Leaves Your Device)
To calculate your personal focus baselines, screen time differentials, and cognitive trends, T1 requires specific Android system permissions. **All raw application telemetry is processed strictly on-device in real time and is never transmitted to or stored on external servers:**

### A. Android Usage Statistics (`PACKAGE_USAGE_STATS`)
- **What is accessed**: Time spent in foreground applications, app category classification (e.g., Productivity, Social, Entertainment, Education), screen unlock counts, and session durations.
- **Why it is needed**: To calculate your 7-day relative behavioral baseline and intra-day focus score differentials (e.g., Time Saved vs Yesterday).
- **Privacy Guarantee**: Raw package names, specific app opening timestamps, and granular activity logs remain locked in your local encrypted device storage (Room Database). **We NEVER read or transmit your private messages, in-app content, photos, browsing history, keystrokes, or notifications.**

### B. Installed Applications Query (`QUERY_ALL_PACKAGES`)
- **What is accessed**: System package metadata to assign installed apps into standard behavioral categories (Productivity, Social, Entertainment, Education, Games, Utility).
- **Why it is needed**: To classify foreground usage accurately without requiring manual categorization for hundreds of apps.
- **Privacy Guarantee**: We do not store or transmit your list of installed apps.

### C. On-Device AI Coach Engine
- **What is processed**: Live telemetry, 7-day score trends, time-saved deltas, and user queries.
- **Why it is needed**: To deliver personalized "Tomorrow's Blueprint", diagnostic feedback, and neuroscience-backed habit protocols.
- **Privacy Guarantee**: All AI Coach reasoning and telemetry audits run strictly offline on your phone. No chat history or usage metrics are shared with external AI servers.

---

## 3. Information We Collect and Synchronize to Cloud
When you create an account and participate in global standings or token features, we synchronize the minimum necessary data to our cloud infrastructure (hosted via Supabase with AES-256 database encryption and TLS 1.3 in-transit security):

### A. Account & Profile Information
- **Google OAuth Identifier**: Unique user ID provided by Google Authentication (via OAuth 2.0 PKCE).
- **Email Address & Display Name**: Used for account identity, session restoration, and optional user profile name.
- **Profile Avatar**: Optional Google account profile photo URL.
- **Goal Track Selection**: Your chosen primary focus track (e.g., *Productivity / Founder*, *Education / Student / Aukaat*, *Social Control*).

### B. Aggregated Focus & Performance Metrics
- **Current Focus Score**: A single numerical integer ranging from 0 to 99 representing your daily focus relative to your personal history.
- **Focus Streak**: The number of consecutive days you have achieved your focus target.
- **T1 Token Balance**: Earned reward tokens from completed deep focus sessions.
- **Total Completed Focus Sessions & Duration**: Cumulative count and minutes of manual Pomodoro/Focus timer sessions.

### C. Public Standings & Stealth Privacy Mode
- If you participate in global leaderboard standings, your username, score, streak, and badge are visible to other users in your active league.
- **Stealth Privacy Mode (Ghost Mode)**: You can enable "Stealth Privacy Mode" at any time in Settings. When enabled, your identity is masked as *Anonymous / Shadow* on all public leaderboards.

---

## 4. How We Use Your Information
We use your data solely for the following explicit purposes:
1. **Core App Functionality**: Calculating authentic focus scores, tracking progress streaks, and awarding proof-of-focus tokens.
2. **Account Management**: Authenticating your identity, syncing your focus score across app reinstalls, and safeguarding account security.
3. **Multiplayer Leaderboards**: Powering real-time percentile distribution and category leagues (Founder Track, Aukaat Exam Track).
4. **Local AI Coaching**: Providing real-time, on-device attention insights and focus recommendations via the T1 AI Coach engine.

---

## 5. What We NEVER Do With Your Data
- **No Ads / Zero Ad-Trackers**: T1 does not integrate third-party advertising SDKs (no Google AdMob, Unity Ads, Meta Audience Network, or data aggregators).
- **No Data Selling**: We will never sell, rent, trade, or lease your personal data or behavioral telemetry to any third party, advertiser, data broker, or government entity.
- **No Private Content Monitoring**: We never capture keystrokes, screen recordings, camera/microphone streams, personal messages, or location coordinates.

---

## 6. Data Retention & Instant Account Deletion (Right to Erasure)
We retain your cloud profile and aggregated scores only for as long as your account remains active.

### Instant In-App Account Deletion (Google Play 2023 Mandate Compliant)
You have the permanent right to delete your entire account and associated data at any time:
1. Open **T1** $\rightarrow$ Go to **Profile** $\rightarrow$ Open **Settings** ($\text{⚙️}$).
2. Scroll to the **Account & Danger Zone** section $\rightarrow$ Tap **Delete Account**.
3. Upon confirmation, your user profile, authentication records, focus scores, tokens, and historical records are **permanently and irreversibly purged** from our cloud databases (Supabase) and local device cache within 0 seconds.

Alternatively, you can submit an account deletion request via email to `support.t1.help@gmail.com` with your registered email address.

---

## 7. Security Measures
We implement industry-standard cryptographic safeguards:
- **In-Transit Encryption**: All network communications utilize HTTPS / TLS 1.3 encryption.
- **At-Rest Encryption**: Cloud database storage is secured with AES-256 encryption.
- **Local Isolation**: On-device telemetry resides inside Android's private app sandbox, inaccessible to other installed applications.

---

## 8. Children's Privacy (COPPA & Global Standards)
T1 is designed for students, founders, and working professionals (ages 13 and older). We do not knowingly collect personal identifiable information from children under the age of 13. If we discover that a child under 13 has created an account without parental consent, we will promptly delete all associated data.

---

## 9. Your Legal Rights (GDPR / CCPA / DPDP Act)
Depending on your jurisdiction, you have the following privacy rights:
- **Right to Access**: Request a summary of the data we hold about you.
- **Right to Rectification**: Correct any inaccurate profile information.
- **Right to Erasure ("Right to Be Forgotten")**: Permanently delete your account and all data.
- **Right to Restrict Processing**: Enable Stealth Privacy Mode or disable notification reminders at any time.

---

## 10. Changes to This Privacy Policy
We may update this Privacy Policy periodically to reflect new features or regulatory requirements. Any modifications will be posted within the app and on our official web portal with an updated "Effective Date".

---

## 11. Contact Us
If you have any questions, concerns, or requests regarding this Privacy Policy or your personal data, please contact our team at:

- **Organization**: Focneth
- **Email**: support.t1.help@gmail.com
- **Website**: https://pixel-perfect-replica-tawny-beta.vercel.app/home
- **Privacy Policy**: https://pixel-perfect-replica-tawny-beta.vercel.app/home/privacy
