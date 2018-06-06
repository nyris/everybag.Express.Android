package de.everybag.express

import android.content.Intent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import de.everybag.express.di.DaggerAppComponent
import de.everybag.express.service.ScreenshotService
import io.nyris.sdk.INyris

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class EverybagApp : DaggerApplication() {
    lateinit var sdk: INyris

    override fun onCreate() {
        super.onCreate()
        val i = Intent(this, ScreenshotService::class.java)
        startService(i)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder().application(this).build()
        sdk = appComponent.getNyrisSdk()
        return appComponent
    }
}