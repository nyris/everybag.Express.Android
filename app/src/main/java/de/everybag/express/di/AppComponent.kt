package de.everybag.express.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import de.everybag.express.EverybagApp
import io.nyris.sdk.INyris
import javax.inject.Singleton

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */

@Singleton
@Component(modules = [ApplicationModule::class,
    ActivityBindingModule::class,
    AndroidSupportInjectionModule::class])
internal interface AppComponent : AndroidInjector<EverybagApp> {
    fun getNyrisSdk(): INyris

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): AppComponent.Builder

        fun build(): AppComponent
    }
}
