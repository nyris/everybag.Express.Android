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

import de.everybag.express.di.ActivityScoped
import de.everybag.express.utils.toParcelable
import io.nyris.sdk.IImageMatchingApi
import io.nyris.sdk.INotFoundMatchingApi
import io.nyris.sdk.OfferResponse
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException
import javax.inject.Inject

/**
 * SearchResultPresenter.kt - Class that match taken/cropped picture for offers or do similarity search
 * using nyris API.
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
@ActivityScoped
class SearchResultPresenter @Inject constructor(private val matchingApi: IImageMatchingApi,
                                                private val notFoundApiMatchingApi: INotFoundMatchingApi) : SearchResultContract.Presenter {
    private var mView: SearchResultContract.View? = null

    @Inject
    lateinit var mCompositeDisposable: CompositeDisposable

    override fun onAttach(view: SearchResultContract.View) {
        mView = view
    }

    override fun onDetach() {
        unsubscribe()
        mView = null
    }

    override fun unsubscribe() {
        mCompositeDisposable.clear()
    }

    override fun searchSimilarOffers(image: ByteArray) {
        mView?.showProgress()
        val disposable = matchingApi
                .exact(false)
                .similarity(true)
                .ocr(false)
                .categoryPrediction(true)
                .match(image)
                .subscribe({
                    mView?.hideProgress()
                    mView?.showOffers(it.offers.toParcelable(), null)
                    mView?.showPredicatedCategories(it.predictedCategories)
                }, {
                    handleError(it)
                })
        mCompositeDisposable.add(disposable)
    }

    override fun openOfferLink(link: String) {
        mView?.showOfferWebSite(link)
    }

    override fun searchOffers(image: ByteArray) {
        mView?.showProgress()
        matchingApi
                .categoryPrediction(true)
                .match(image, OfferResponse::class.java)
                .subscribe({
                    if(it.body == null){
                        mView?.showMessage("Something wrong happened !")
                        return@subscribe
                    }
                    val body = it.body!!
                    mView?.hideProgress()
                    mView?.showOffers(body.offers.toParcelable(), it.getRequestId())
                    mView?.showPredicatedCategories(body.predictedCategories)
                }, {
                    handleError(it)
                })
    }

    override fun markImageNotFound(requestId: String) {
        notFoundApiMatchingApi
                .markAsNotFound(requestId)
                .subscribe({
                    mView?.showMessage("Image marked as not found.")
                },{
                    handleError(it)
                })
    }

    private fun handleError(throwable: Throwable){
        mView?.hideProgress()
        if(throwable is IOException){
            mView?.showError("Please check your internet connection.")
        }else mView?.showError(throwable.message.toString())
    }
}