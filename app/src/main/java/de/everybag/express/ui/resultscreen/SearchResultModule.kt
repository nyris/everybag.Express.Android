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

package de.everybag.express.ui.resultscreen

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.everybag.express.di.ActivityScoped
import de.everybag.express.di.FragmentScoped

/**
 * SearchResultModule.kt - Dagger module used to pass the view dependencies to the SearchResultPresenter
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