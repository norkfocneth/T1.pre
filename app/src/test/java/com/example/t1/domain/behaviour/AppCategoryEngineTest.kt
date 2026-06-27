package com.example.t1.domain.behaviour

import com.example.t1.domain.model.behaviour.AppCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class AppCategoryEngineTest {

    private val engine = AppCategoryEngine()

    @Test
    fun testKnownPackages() {
        assertEquals(AppCategory.SOCIAL, engine.getCategory("com.instagram.android"))
        assertEquals(AppCategory.SOCIAL, engine.getCategory("com.facebook.katana"))
        assertEquals(AppCategory.SOCIAL, engine.getCategory("co.threads.android"))
        assertEquals(AppCategory.ENTERTAINMENT, engine.getCategory("com.netflix.mediaclient"))
        assertEquals(AppCategory.PRODUCTIVITY, engine.getCategory("com.notion.org"))
        assertEquals(AppCategory.DEVELOPMENT, engine.getCategory("com.github.android"))
        assertEquals(AppCategory.COMMUNICATION, engine.getCategory("com.whatsapp"))
    }

    @Test
    fun testCaseInsensitivityAndTrimming() {
        assertEquals(AppCategory.SOCIAL, engine.getCategory("  com.INSTAGRAM.android  "))
        assertEquals(AppCategory.COMMUNICATION, engine.getCategory("com.WHATSAPP  "))
    }

    @Test
    fun testPrefixMatching() {
        assertEquals(AppCategory.PRODUCTIVITY, engine.getCategory("com.microsoft.office.word"))
        assertEquals(AppCategory.PRODUCTIVITY, engine.getCategory("com.google.android.apps.docs.editors.sheets"))
    }

    @Test
    fun testUnknownPackagesFallbackToOther() {
        assertEquals(AppCategory.OTHER, engine.getCategory("com.unknown.random.app"))
        assertEquals(AppCategory.OTHER, engine.getCategory(""))
    }
}
