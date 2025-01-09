package com.movableink.mparticle

import android.content.Context
import com.mparticle.MParticleOptions
import com.mparticle.kits.KitIntegration
import com.mparticle.kits.KitIntegrationFactory
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.InvocationTargetException

class MovableKitTest {
    private val kit: KitIntegration
        get() = MovableKit()

    @Test
    @Throws(Exception::class)
    fun testGetName() {
        val name = kit.name
        Assert.assertTrue(!name.isNullOrEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testClassName() {
        val options = Mockito.mock(MParticleOptions::class.java)
        val factory = KitIntegrationFactory(options)
        val integrations = factory.supportedKits
        val className = kit.javaClass.name
        // TODO test after the kit is registered #setupKnownIntegrations() + kitID
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
            val settings = HashMap<String, String>()
            settings["fake setting"] = "fake"

            // access the protected method
            val method =
                kit::class.java.getDeclaredMethod(
                    "onKitCreate",
                    Map::class.java,
                    Context::class.java,
                )
            method.isAccessible = true
            method.invoke(kit, settings, Mockito.mock(Context::class.java))
        } catch (ex: Exception) {
            e = ex
            if (e is InvocationTargetException && e.targetException != null) {
                e = e.targetException as Exception
            }
        }
        Assert.assertNotNull(e)
    }
}
