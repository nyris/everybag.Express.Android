package de.everybag.express.ui.resultscreen

import de.everybag.express.base.MvpPresenter
import de.everybag.express.base.MvpView
import de.everybag.express.model.OfferParcelable

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
interface SearchResultContract {
    interface View : MvpView<Presenter> {
        fun showProgress()
        fun hideProgress()
        fun showOffers(offers: ArrayList<OfferParcelable>)
        fun showOfferWebSite(link: String)
        fun showSnackViewOnce()
        fun showError(message: String)
        fun showClassificationActivity()
    }

    interface Presenter : MvpPresenter<View> {
        fun searchSimilarOffers(image: ByteArray)
        fun openOfferLink(link: String)
        fun searchOffers(image: ByteArray)
        fun unsubscribe()
    }
}