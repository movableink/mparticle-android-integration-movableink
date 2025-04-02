package com.movableink.mparticle

import android.content.Context
import com.mparticle.kits.MPSideloadedKit
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.InvocationTargetException

@Suppress("ktlint:standard:no-consecutive-comments")
class MovableKitTest {
    private val kit: MPSideloadedKit
        get() = MovableKit()

    @Test
    @Throws(Exception::class)
    fun testOnKitCreate() {
        var e: Exception? = null
        try {
            val kit = kit
            val settings = HashMap<String, String>()
            settings["mock setting"] = "mock"

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
