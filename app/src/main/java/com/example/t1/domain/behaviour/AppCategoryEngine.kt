package com.example.t1.domain.behaviour

import com.example.t1.domain.model.behaviour.AppCategory
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCategoryEngine @Inject constructor() {

    private val categoryCache = ConcurrentHashMap<String, AppCategory>()

    // Direct exact matches database
    private val packageMap = mapOf(
        // Social (Aggressive)
        "com.instagram.android" to AppCategory.SOCIAL,
        "com.facebook.katana" to AppCategory.SOCIAL,
        "com.facebook.lite" to AppCategory.SOCIAL,
        "com.facebook.orca" to AppCategory.SOCIAL,
        "com.twitter.android" to AppCategory.SOCIAL,
        "co.threads.android" to AppCategory.SOCIAL,
        "com.reddit.frontpage" to AppCategory.SOCIAL,
        "com.linkedin.android" to AppCategory.SOCIAL,
        "com.snapchat.android" to AppCategory.SOCIAL,
        "com.tiktok.android" to AppCategory.SOCIAL,
        "com.zhiliaoapp.musically" to AppCategory.SOCIAL,
        "com.pinterest" to AppCategory.SOCIAL,
        "com.tumblr" to AppCategory.SOCIAL,
        "com.badoo.mobile" to AppCategory.SOCIAL,
        "com.tinder" to AppCategory.SOCIAL,
        "com.okcupid.tinder" to AppCategory.SOCIAL,
        "com.grandr.android" to AppCategory.SOCIAL,
        "com.bumble.app" to AppCategory.SOCIAL,
        "co.hinge.app" to AppCategory.SOCIAL,
        "org.telegram.messenger" to AppCategory.COMMUNICATION,
        "org.thunderdog.challegram" to AppCategory.COMMUNICATION,
        "com.whatsapp" to AppCategory.COMMUNICATION,
        "com.whatsapp.w4b" to AppCategory.COMMUNICATION,
        "com.discord" to AppCategory.COMMUNICATION,

        // Entertainment
        "com.google.android.youtube" to AppCategory.ENTERTAINMENT,
        "com.netflix.mediaclient" to AppCategory.ENTERTAINMENT,
        "com.amazon.avod.thirdpartyclient" to AppCategory.ENTERTAINMENT,
        "com.disney.disneyplus" to AppCategory.ENTERTAINMENT,
        "com.hbo.hbonow" to AppCategory.ENTERTAINMENT,
        "com.hbo.broadband" to AppCategory.ENTERTAINMENT,
        "com.hulu" to AppCategory.ENTERTAINMENT,
        "tv.twitch.android.app" to AppCategory.ENTERTAINMENT,
        "com.hotstar" to AppCategory.ENTERTAINMENT,
        "com.peacocktv.peacock" to AppCategory.ENTERTAINMENT,
        "com.paramountplus.app" to AppCategory.ENTERTAINMENT,
        "com.plexapp.android" to AppCategory.ENTERTAINMENT,
        "com.crunchyroll.crunchyroid" to AppCategory.ENTERTAINMENT,
        "com.plutotv" to AppCategory.ENTERTAINMENT,
        "com.tubitv" to AppCategory.ENTERTAINMENT,
        "com.spotify.music" to AppCategory.ENTERTAINMENT,
        "com.jio.media.ondemand" to AppCategory.ENTERTAINMENT,
        "com.mxtech.videoplayer.ad" to AppCategory.ENTERTAINMENT,
        "com.mxtech.videoplayer.pro" to AppCategory.ENTERTAINMENT,

        // Productivity
        "com.openai.chatgpt" to AppCategory.PRODUCTIVITY,
        "a.s.g.chatgpt" to AppCategory.PRODUCTIVITY,
        "com.anthropic.claude" to AppCategory.PRODUCTIVITY,
        "com.perplexity.perplexity" to AppCategory.PRODUCTIVITY,
        "com.microsoft.copilot" to AppCategory.PRODUCTIVITY,
        "com.poe.poe" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.bard" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.docs" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.docs.editors.sheets" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.docs.editors.slides" to AppCategory.PRODUCTIVITY,
        "com.notion.org" to AppCategory.PRODUCTIVITY,
        "com.evernote" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.officehubrow" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.word" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.excel" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.powerpoint" to AppCategory.PRODUCTIVITY,
        "com.todoist" to AppCategory.PRODUCTIVITY,
        "com.ticktick.task" to AppCategory.PRODUCTIVITY,
        "com.trello" to AppCategory.PRODUCTIVITY,
        "com.asana.app" to AppCategory.PRODUCTIVITY,
        "com.zoho.docs" to AppCategory.PRODUCTIVITY,
        "com.dropbox.android" to AppCategory.PRODUCTIVITY,
        "com.box.android" to AppCategory.PRODUCTIVITY,
        "com.slack" to AppCategory.PRODUCTIVITY,
        "com.microsoft.teams" to AppCategory.PRODUCTIVITY,
        "com.google.android.gm" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.outlook" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.meetings" to AppCategory.PRODUCTIVITY,
        "com.google.android.apps.tachyon" to AppCategory.PRODUCTIVITY,
        "com.atlassian.android.jira" to AppCategory.PRODUCTIVITY,
        "com.clickup.clickup" to AppCategory.PRODUCTIVITY,
        "com.linear.android" to AppCategory.PRODUCTIVITY,
        "md.obsidian" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office.onenote" to AppCategory.PRODUCTIVITY,
        "com.google.android.keep" to AppCategory.PRODUCTIVITY,

        // Education
        "com.duolingo" to AppCategory.EDUCATION,
        "org.coursera.android" to AppCategory.EDUCATION,
        "org.khanacademy.android" to AppCategory.EDUCATION,
        "com.edx.mobile" to AppCategory.EDUCATION,
        "org.edx.mobile" to AppCategory.EDUCATION,
        "com.udemy.android" to AppCategory.EDUCATION,
        "org.stepik.android" to AppCategory.EDUCATION,
        "com.sololearn" to AppCategory.EDUCATION,
        "com.memrise.android" to AppCategory.EDUCATION,
        "com.linkedin.learning" to AppCategory.EDUCATION,
        "com.brainly" to AppCategory.EDUCATION,
        "com.quizlet.quizletandroid" to AppCategory.EDUCATION,
        "com.physicswallah" to AppCategory.EDUCATION,
        "com.allen.allen" to AppCategory.EDUCATION,
        "com.unacademyapp" to AppCategory.EDUCATION,
        "com.byjus.thelearningapp" to AppCategory.EDUCATION,
        "com.leetcode" to AppCategory.EDUCATION,
        "com.codeforces" to AppCategory.EDUCATION,
        "com.geeksforgeeks.gfgapp" to AppCategory.EDUCATION,
        "com.udacity.android" to AppCategory.EDUCATION,
        "com.brilliant.android" to AppCategory.EDUCATION,
        "com.adobe.reader" to AppCategory.EDUCATION,
        "com.foxit.mobile.pdf.lite" to AppCategory.EDUCATION,
        "com.amazon.kindle" to AppCategory.EDUCATION,
        "com.flyersoft.moonreader" to AppCategory.EDUCATION,

        // Development (Productivity Sub)
        "com.github.android" to AppCategory.DEVELOPMENT,
        "com.gitlab.android" to AppCategory.DEVELOPMENT,
        "com.atlassian.bitbucket.server" to AppCategory.DEVELOPMENT,
        "com.android.studio" to AppCategory.DEVELOPMENT,
        "org.jetbrains.kotlin" to AppCategory.DEVELOPMENT,
        "com.termux" to AppCategory.DEVELOPMENT,
        "com.quoda" to AppCategory.DEVELOPMENT,
        "com.artsfox.acode" to AppCategory.DEVELOPMENT,
        "com.replit.replit" to AppCategory.DEVELOPMENT,
        "com.duy.cplusplus" to AppCategory.DEVELOPMENT,
        "com.duy.compiler.jvd" to AppCategory.DEVELOPMENT,

        // Media
        "com.pandora.android" to AppCategory.MEDIA,
        "com.shazam.android" to AppCategory.MEDIA,
        "com.soundcloud.android" to AppCategory.MEDIA,
        "com.amazon.mp3" to AppCategory.MEDIA,
        "com.deezer.android" to AppCategory.MEDIA,
        "com.apple.android.music" to AppCategory.MEDIA,
        "com.google.android.music" to AppCategory.MEDIA,

        // Photography
        "com.niksoftware.snapseed" to AppCategory.PHOTOGRAPHY,
        "com.adobe.lrmobile" to AppCategory.PHOTOGRAPHY,
        "com.adobe.psmobile" to AppCategory.PHOTOGRAPHY,
        "com.instagram.layout" to AppCategory.PHOTOGRAPHY,

        // Utilities / Neutral
        "com.android.chrome" to AppCategory.UTILITIES,
        "org.mozilla.firefox" to AppCategory.UTILITIES,
        "com.opera.browser" to AppCategory.UTILITIES,
        "com.sec.android.app.sbrowser" to AppCategory.UTILITIES,
        "com.microsoft.emmx" to AppCategory.UTILITIES,
        "com.android.settings" to AppCategory.UTILITIES,
        "com.google.android.calculator" to AppCategory.UTILITIES,
        "com.google.android.deskclock" to AppCategory.UTILITIES,
        "com.android.camera" to AppCategory.UTILITIES,
        "com.sec.android.app.camera" to AppCategory.UTILITIES,
        "com.google.android.GoogleCamera" to AppCategory.UTILITIES,
        "com.google.android.apps.photos" to AppCategory.UTILITIES,
        "com.sec.android.gallery3d" to AppCategory.UTILITIES,
        "in.swiggy.android" to AppCategory.UTILITIES,
        "com.application.zomato" to AppCategory.UTILITIES,
        "com.rapido.passenger" to AppCategory.UTILITIES,

        // Finance
        "com.paytm" to AppCategory.FINANCE,
        "com.phonepe.app" to AppCategory.FINANCE,
        "com.google.android.apps.nbu.paisa.user" to AppCategory.FINANCE,
        "com.paypal.android.p2pmobile" to AppCategory.FINANCE,
        "com.venmo" to AppCategory.FINANCE,
        "com.square.cash" to AppCategory.FINANCE,
        "com.revolut.revolut" to AppCategory.FINANCE,
        "com.chime" to AppCategory.FINANCE,
        "com.robinhood.android" to AppCategory.FINANCE,
        "web.com.tdameritrade.client" to AppCategory.FINANCE,
        "com.schwab.mobile" to AppCategory.FINANCE,
        "com.capitalone.mobile" to AppCategory.FINANCE,
        "com.bankofamerica.apps.ehf.hr.ba" to AppCategory.FINANCE,
        "com.wf.wellsfargomobile" to AppCategory.FINANCE,
        "com.chase.sig.android" to AppCategory.FINANCE,

        // Health
        "com.google.android.apps.fitness" to AppCategory.HEALTH,
        "com.fitbit.FitbitMobile" to AppCategory.HEALTH,
        "com.runtastic.android" to AppCategory.HEALTH,
        "com.strava" to AppCategory.HEALTH,
        "com.myfitnesspal.android" to AppCategory.HEALTH,
        "com.calm.android" to AppCategory.HEALTH,
        "org.headspace.android" to AppCategory.HEALTH,
        "com.sleepcycle.android" to AppCategory.HEALTH,
        "com.nike.plusgps" to AppCategory.HEALTH,
        "com.garmin.android.apps.connectmobile" to AppCategory.HEALTH,

        // Navigation
        "com.google.android.apps.maps" to AppCategory.NAVIGATION,
        "com.waze" to AppCategory.NAVIGATION,
        "org.openstreetmap.automotive" to AppCategory.NAVIGATION,
        "com.uber.user" to AppCategory.NAVIGATION,
        "com.lyft.android" to AppCategory.NAVIGATION,
        "com.grabtaxi.passenger" to AppCategory.NAVIGATION,
        "com.bolt.client" to AppCategory.NAVIGATION,
        "com.here.app.maps" to AppCategory.NAVIGATION,

        // Shopping
        "com.amazon.mShop.android.shopping" to AppCategory.SHOPPING,
        "com.ebay.mobile" to AppCategory.SHOPPING,
        "com.alibaba.aliexpress.ita" to AppCategory.SHOPPING,
        "com.walmart.android" to AppCategory.SHOPPING,
        "com.target.autos" to AppCategory.SHOPPING,
        "com.flipkart.android" to AppCategory.SHOPPING,

        // System
        "android" to AppCategory.SYSTEM,
        "com.android.systemui" to AppCategory.SYSTEM,
        "com.google.android.googlequicksearchbox" to AppCategory.SYSTEM,
        "com.google.android.gms" to AppCategory.SYSTEM,
        "com.google.android.inputmethod.latin" to AppCategory.SYSTEM,
        "com.sec.android.app.launcher" to AppCategory.SYSTEM
    )

    // Prefix rules to handle subpackages or regional package variations
    private val prefixRules = listOf(
        "com.google.android.apps.docs" to AppCategory.PRODUCTIVITY,
        "com.microsoft.office." to AppCategory.PRODUCTIVITY,
        "com.whatsapp" to AppCategory.COMMUNICATION,
        "org.telegram." to AppCategory.COMMUNICATION,
        "com.google.android.apps.maps" to AppCategory.NAVIGATION,
        "com.android.settings" to AppCategory.UTILITIES,
        "com.android.system" to AppCategory.SYSTEM,
        "com.sec.android.app" to AppCategory.SYSTEM
    )

    fun getCategory(packageName: String): AppCategory {
        val lowerPkg = packageName.lowercase().trim()
        if (lowerPkg.isEmpty()) return AppCategory.OTHER

        // Check cache first
        categoryCache[lowerPkg]?.let { return it }

        // Check exact map
        packageMap[lowerPkg]?.let {
            categoryCache[lowerPkg] = it
            return it
        }

        // Check prefix rules
        for ((prefix, category) in prefixRules) {
            if (lowerPkg.startsWith(prefix)) {
                categoryCache[lowerPkg] = category
                return category
            }
        }

        // Default fallback
        categoryCache[lowerPkg] = AppCategory.OTHER
        return AppCategory.OTHER
    }
}
