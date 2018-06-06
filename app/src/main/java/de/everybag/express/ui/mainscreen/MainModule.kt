package de.everybag.express.ui.mainscreen

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.everybag.express.di.ActivityScoped
import de.everybag.express.di.FragmentScoped

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
@Module
abstract class MainModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun mainFragment(): MainFragment

    @ActivityScoped
    @Binds
    abstract fun mainPresenter(presenter: MainPresenter): MainContract.Presenter
}