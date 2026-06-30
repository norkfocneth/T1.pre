package com.example.t1.domain.focus

object AICoachEngine {
    fun generateInitialGreeting(
        focusScore: Int,
        behaviourScore: Int,
        confidence: Int,
        timeSavedMs: Long,
        screenTimeMs: Long,
        unlocks: Int,
        categoryTimes: Map<String, Long>
    ): String {
        val screenTimeMin = screenTimeMs / 60000
        val hr = screenTimeMin / 60
        val mn = screenTimeMin % 60
        val screenTimeStr = if (hr > 0) "${hr}h ${mn}m" else "${mn}m"

        val timeSavedMin = kotlin.math.abs(timeSavedMs) / 60000
        val thr = timeSavedMin / 60
        val tmn = timeSavedMin % 60
        val timeSavedStr = if (thr > 0) "${thr}h ${tmn}m" else "${tmn}m"
        val savedText = if (timeSavedMs > 0) {
            "You saved $timeSavedStr compared to yesterday! Excellent job."
        } else if (timeSavedMs < 0) {
            "You spent $timeSavedStr more than yesterday. Let's try to reduce screen time."
        } else {
            "Keep an eye on your usage stats today."
        }

        // Find dominant category
        val maxCat = categoryTimes.maxByOrNull { it.value }
        val dominantAdvice = if (maxCat != null && maxCat.value > 0) {
            val catMin = maxCat.value / 60000
            val chr = catMin / 60
            val cmn = catMin % 60
            val catStr = if (chr > 0) "${chr}h ${cmn}m" else "${cmn}m"
            when (maxCat.key) {
                "Entertainment" -> "Your dominant activity was Entertainment ($catStr). Try restricting video/streaming apps to improve focus."
                "Social" -> "Your dominant activity was Social ($catStr). Constant messaging app notifications are highly distracting."
                "Productivity" -> "Outstanding! Your dominant activity was Productivity ($catStr). You are in a flow state."
                "Education" -> "Great job! You spent $catStr on Education. Learning is key."
                else -> "Your main activity was ${maxCat.key} ($catStr)."
            }
        } else {
            "No app activity registered yet."
        }

        return """
            Hello! I am your T1 AI Coach. Here are your attention insights for today:

            • Focus Score: $focusScore/100 (Confidence: $confidence%)
            • Behaviour Score: $behaviourScore/100
            • Screen Time: $screenTimeStr ($unlocks unlocks)
            
            💡 $savedText
            📊 $dominantAdvice
            
            Ask me anything about your focus, screen time, or daily stats!
        """.trimIndent()
    }

    fun getResponse(
        query: String,
        focusScore: Int,
        behaviourScore: Int,
        screenTimeMs: Long,
        unlocks: Int,
        categoryTimes: Map<String, Long>
    ): String {
        val q = query.lowercase().trim()
        
        return when {
            q.contains("hello") || q.contains("hi") || q.contains("hey") -> {
                "Hello there! How can I help you improve your focus today?"
            }
            q.contains("how are you") || q.contains("status") -> {
                "I'm feeling highly focused! Ready to analyze your attention metrics."
            }
            q.contains("focus score") || q.contains("my score") -> {
                "Your current Focus Score is $focusScore/100. This is a blend of your Questionnaire score and your Behaviour score (currently $behaviourScore/100)."
            }
            q.contains("behaviour") || q.contains("behavior") -> {
                "Your Behaviour Score is $behaviourScore/100. It is computed based on screen time, unlocks, app opens, and active focus sessions."
            }
            q.contains("screen time") || q.contains("phone usage") || q.contains("usage") -> {
                val min = screenTimeMs / 60000
                val hr = min / 60
                val mn = min % 60
                val timeStr = if (hr > 0) "${hr}h ${mn}m" else "${mn}m"
                "You have spent $timeStr on screen today. Try to keep it under 4 hours to avoid a screen time penalty!"
            }
            q.contains("unlock") || q.contains("unlocks") -> {
                "You have unlocked your phone $unlocks times today. Frequent unlocks fragment your attention span. Try batching your notifications!"
            }
            q.contains("productivity") || q.contains("work") || q.contains("study") -> {
                val prodMin = ((categoryTimes["Productivity"] ?: 0L) / 60000).toInt()
                "You spent $prodMin minutes on Productivity apps today. Keep it up!"
            }
            q.contains("entertainment") || q.contains("distract") || q.contains("youtube") -> {
                val entMin = ((categoryTimes["Entertainment"] ?: 0L) / 60000).toInt()
                "You spent $entMin minutes on Entertainment today. Try replacing 15 minutes of entertainment with a focus session!"
            }
            else -> {
                "I am focused on analyzing your local attention and behaviour metrics. If you have questions about your focus stats, unlocks, screen time, or daily trend, feel free to ask!"
            }
        }
    }
}
