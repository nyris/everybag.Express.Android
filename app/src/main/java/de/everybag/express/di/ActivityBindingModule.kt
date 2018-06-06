package de.everybag.express.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.everybag.express.ui.mainscreen.MainActivity
import de.everybag.express.ui.mainscreen.MainModule
import de.everybag.express.ui.resultscreen.SearchResultActivity
import de.everybag.express.ui.resultscreen.SearchResultModule

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [(MainModule::class)])
    abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(SearchResultModule::class)])
    abstract fun resultActivity(): SearchResultActivity

    /* @ActivityScoped
    @ContributesAndroidInjector(modules = [(ClassificationModule::class)])
    abstract fun classificationActivity(): ClassificationActivity*/
}
