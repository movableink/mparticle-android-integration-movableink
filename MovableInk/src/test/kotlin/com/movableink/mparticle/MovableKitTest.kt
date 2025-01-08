package com.movableink.mparticle

import android.content.Context
import com.mparticle.MParticleOptions
import com.mparticle.kits.KitIntegrationFactory
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class MovableKitTest {

    private val kit = MovableKit()

    @Test
    @Throws(Exception::class)
    fun testGetName() {
        val name = kit.name
        Assert.assertTrue(name.isNotEmpty())
    }



    @Test
    @Throws(Exception::class)
    fun testClassName() {
        val options = Mockito.mock(MParticleOptions::class.java)
        val factory = KitIntegrationFactory(options)
        val integrations= factory.supportedKits
        val className = kit.javaClass.name

        if (!integrations.any { it::class.java.name == className }) {
            Assert.fail("$className not found as a known integration.")
        }
    }

    @Test
    @Throws(Exception::class)
    fun testOnKitCreate() {
        var e: Exception? = null
        try {
            val kit = kit
            val settings= HashMap<String, String>()
            settings["fake setting"] = "fake"
            kit.onKitCreate(settings, Mockito.mock(Context::class.java))
        } catch (ex: Exception) {
            e = ex
        }
        Assert.assertNotNull(e)
    }
}