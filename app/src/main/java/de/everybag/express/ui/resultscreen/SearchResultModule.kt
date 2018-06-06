package de.everybag.express.ui.resultscreen

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
abstract class SearchResultModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun searchFragment(): SearchFragment

    @ActivityScoped
    @Binds
    abstract fun searchPresenter(presenter: SearchResultPresenter): SearchResultContract.Presenter
}