package de.everybag.express.ui.resultscreen

import de.everybag.express.di.ActivityScoped
import de.everybag.express.utils.toParcelable
import io.nyris.sdk.IImageMatchingApi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
@ActivityScoped
class SearchResultPresenter @Inject constructor(private val matchingApi: IImageMatchingApi) : SearchResultContract.Presenter {
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
        matchingApi
                .exact(false)
                .similarity(true)
                .ocr(false)
                .match(image)
                .subscribe({
                    mView?.hideProgress()
                    mView?.showOffers(it.offers.toParcelable())
                }, {
                    mView?.showError(it.message.toString())
                })
    }

    override fun openOfferLink(link: String) {
        mView?.showOfferWebSite(link)
    }

    override fun searchOffers(image: ByteArray) {
        mView?.showProgress()
        matchingApi
                .match(image)
                .subscribe({
                    mView?.hideProgress()
                    mView?.showOffers(it.offers.toParcelable())
                }, {
                    mView?.showError(it.message.toString())
                })
    }
}