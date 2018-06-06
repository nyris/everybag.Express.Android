package de.everybag.express.ui.mainscreen

import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import de.everybag.express.base.MvpPresenter
import de.everybag.express.base.MvpView
import de.everybag.express.model.OfferParcelable
import io.nyris.camera.Callback

/**
 *
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
        fun showOffersActivity(croppedBitmap: Bitmap, rectF: RectF, offers: ArrayList<OfferParcelable>)
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
