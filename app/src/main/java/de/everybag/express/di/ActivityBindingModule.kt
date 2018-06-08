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

package de.everybag.express.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.everybag.express.ui.mainscreen.MainActivity
import de.everybag.express.ui.mainscreen.MainModule
import de.everybag.express.ui.resultscreen.SearchResultActivity
import de.everybag.express.ui.resultscreen.SearchResultModule

/**
 * ActivityBindingModule.kt - Dagger Module that used to bind defined ActivityModule with the corresponding activity
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
}
