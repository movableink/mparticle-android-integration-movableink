package com.mparticle.movablekit

import android.app.Application
import com.mparticle.MParticle
import com.mparticle.MParticleOptions

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val options = MParticleOptions.builder(this)
            .credentials("us1-f7a198dbdb381846a72d39e32e8d5f73", "zWBzzfObtuNVlWeekZmSdv3OhP0uUyaGVMTjGPY3EI9BHYs6xiLgk17AiNaLTFPJ")
            .environment(MParticle.Environment.Development)
            .logLevel(MParticle.LogLevel.VERBOSE)
            .build()
        MParticle.start(options)
    }

}