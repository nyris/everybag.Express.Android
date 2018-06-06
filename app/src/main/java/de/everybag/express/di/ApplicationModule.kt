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
 *
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