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

import de.everybag.express.base.MvpPresenter
import de.everybag.express.base.MvpView
import de.everybag.express.model.OfferParcelable

/**
 * SearchResultContract.kt - This specifies the contract between the result search view and the presenter.
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
interface SearchResultContract {
    interface View : MvpView<Presenter> {
        fun showProgress()
        fun hideProgress()
        fun showOffers(offers: ArrayList<OfferParcelable>, requestId: String?)
        fun showOfferWebSite(link: String)
        fun showSnackViewOnce()
        fun showError(message: String)
        fun markImageAsNotFound()
    }

    interface Presenter : MvpPresenter<View> {
        fun searchSimilarOffers(image: ByteArray)
        fun openOfferLink(link: String)
        fun searchOffers(image: ByteArray)
        fun unsubscribe()
        fun markImageNotFound(requestId : String)
    }
}