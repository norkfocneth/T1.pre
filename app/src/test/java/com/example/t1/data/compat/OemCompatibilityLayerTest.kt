package com.example.t1.data.compat

import org.junit.Assert.assertNotNull
import org.junit.Test

class OemCompatibilityLayerTest {

    private val oemCompatibilityLayer = OemCompatibilityLayer()

    @Test
    fun testGetOemProfile() {
        val profile = oemCompatibilityLayer.getOemProfile()
        assertNotNull(profile)
        assertNotNull(profile.manufacturer)
    }
}
