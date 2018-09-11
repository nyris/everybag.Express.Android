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

package de.everybag.express.ui.mainscreen

import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import de.everybag.express.base.MvpPresenter
import de.everybag.express.base.MvpView
import de.everybag.express.model.OfferParcelable
import io.nyris.camera.Callback

/**
 * MainContract.kt - This specifies the contract between the main view and the presenter.
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
interface MainContract {
    interface View : MvpView<Presenter> {
        fun addCameraCallback(callback: Callback)
        fun removeCameraCallback(callback: Callback)
        fun startCamera()
        fun stopCamera()
        fun hideLabelCapture()
        fun showLabelCapture()
        fun showImageCameraPreview()
        fun hideImageCameraPreview()
        fun showViewPinCropper()
        fun hideViewPinCropper()
        fun showCircleView()
        fun hideCircleView()
        fun showValidateView()
        fun hideValidateView()
        fun showLoading()
        fun hideLoading()
        fun takePicture()
        fun setImPreviewBitmap(bitmap: Bitmap)
        fun showObjects(objects: List<RectF>)
        fun showOffersActivity(croppedBitmap: Bitmap, rectF: RectF, offers: ArrayList<OfferParcelable>, searchTime : String)
    }

    interface Presenter : MvpPresenter<View> {
        fun takePicture()
        fun isStillKeepingObjects(): Boolean
        fun isMatching(): Boolean
        fun cropObjectImage(rectF: RectF)
        fun unsubscribe()
        fun clear()
        fun onSharedImage(imageUri: Uri, targetWidth: Int, targetHeight: Int)
    }
}
