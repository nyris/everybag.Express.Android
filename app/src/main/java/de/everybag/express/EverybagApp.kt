/*
 * Copyright 2018 nyris GmbH. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.everybag.express

import android.content.Intent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import de.everybag.express.di.DaggerAppComponent
import de.everybag.express.service.ScreenshotService
import io.nyris.sdk.INyris

/**
 * EverybagApp.kt - Custom application class that extend DaggerApplication that override applicationInjector
 * which tells Dagger how to make ou @Singleton Component.
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