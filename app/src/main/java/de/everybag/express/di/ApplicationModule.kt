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

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import de.everybag.express.BuildConfig
import io.nyris.sdk.*
import io.reactivex.disposables.CompositeDisposable

/**
 * ApplicationModule.kt - Dagger Module to bind our Application class as a Context in the AppComponent
 * also to provide SDK APIs
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
@Module
abstract class ApplicationModule {
    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideNyrisSdk(): INyris {
            return Nyris.createInstance(BuildConfig.API_KEY, BuildConfig.DEBUG)
        }

        @Provides
        @JvmStatic
        fun provideMatchingApi(nyris: INyris): IImageMatchingApi {
            return nyris.imageMatching()
        }

        @Provides
        @JvmStatic
        fun provideNotFoundMatchingApi(nyris: INyris): INotFoundMatchingApi {
            return nyris.notFoundMatching()
        }

        @Provides
        @JvmStatic
        fun provideObjectProposalApi(nyris: INyris): IObjectProposalApi {
            return nyris.objectProposal()
        }

        @Provides
        @JvmStatic
        fun provideOfferManagerApi(nyris: INyris): IOfferManagerApi {
            return nyris.offerManager()
        }

        @Provides
        @JvmStatic
        fun provideSimilarityApi(nyris: INyris): ISimilarityApi {
            return nyris.similarity()
        }

        @Provides
        @JvmStatic
        fun provideTextSearchApi(nyris: INyris): ITextSearchApi {
            return nyris.textSearch()
        }

        @Provides
        @JvmStatic
        fun provideCompositeDisposable(): CompositeDisposable {
            return CompositeDisposable()
        }
    }


    @Binds
    abstract fun bindContext(application: Application): Context
}