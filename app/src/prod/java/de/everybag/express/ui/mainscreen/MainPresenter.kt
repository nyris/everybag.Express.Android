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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import de.everybag.express.di.ActivityScoped
import de.everybag.express.utils.toParcelable
import io.nyris.camera.BaseCameraView
import io.nyris.camera.Callback
import io.nyris.camera.ImageUtils
import io.nyris.camera.Size
import io.nyris.sdk.*
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import java.io.*
import javax.inject.Inject


/**
 * MainPresenter.kt - Class that listen to user actions from the MainFragment, retrieves pictures from camera,
 * show offers, crop image and update UI.
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
@ActivityScoped
class MainPresenter @Inject constructor(private val matchingApi: IImageMatchingApi,
                                        private val objectProposalApi: IObjectProposalApi) : MainContract.Presenter, Callback {
    private var mView: MainContract.View? = null

    private var mBitmapForPreviewing: Bitmap? = null

    private var mBitmapForCropping: Bitmap? = null

    private var mTargetSize: Size? = null

    private var mObjectProposalList = mutableListOf<RectF>()

    private var mOfferList = mutableListOf<Offer>()

    private var mSearchTime = ""

    private var mIsMatching = false

    @Inject
    lateinit var mCompositeDisposable: CompositeDisposable

    @Inject
    lateinit var mContext: Context

    override fun onAttach(view: MainContract.View) {
        mView = view
        mView?.addCameraCallback(this)
    }

    override fun onDetach() {
        unsubscribe()
        mView?.removeCameraCallback(this)
        mView = null
    }

    override fun takePicture() {
        mView?.takePicture()
    }

    override fun onCameraClosed(cameraView: BaseCameraView) {
    }

    override fun onCameraOpened(cameraView: BaseCameraView) {
    }

    override fun onError(errorMessage: String) {
        mView?.onError(errorMessage)
    }

    override fun onPictureTaken(cameraView: BaseCameraView, resizedImage: ByteArray) {
        mIsMatching = true
        match(resizedImage)
    }

    private fun match(resizedImage: ByteArray) {
        mOfferList.clear()
        mObjectProposalList.clear()
        val resizedImageBtm = BitmapFactory.decodeByteArray(resizedImage, 0, resizedImage.size)
        val obs1 = matchingApi
                .exact(true)
                .similarity(false)
                .ocr(false)
                .match(resizedImage)

        val obs2 = objectProposalApi.extractObjects(resizedImage)

        val previous = SystemClock.elapsedRealtime()
        val disposable = Single.zip(obs1, obs2, BiFunction<OfferResponseBody, List<ObjectProposal>, List<ObjectProposal>> { offerResponseBody: OfferResponseBody, objs: List<ObjectProposal> ->
                mOfferList.addAll(offerResponseBody.offers)
                objs
            })
            .subscribe({
                val now = SystemClock.elapsedRealtime()
                normalizeAndAddObjects(resizedImageBtm.width, resizedImageBtm.height, it)
                mView?.hideLoading()
                mView?.showObjects(mObjectProposalList)
                mIsMatching = false
                mSearchTime ="${mOfferList.size} results (${now - previous}ms)"
            }, {
                normalizeAndAddObjects(resizedImageBtm.width, resizedImageBtm.height, mutableListOf())
                handleError(it)
                mIsMatching = false
            })

        mCompositeDisposable.add(disposable)
    }



    private fun handleError(throwable: Throwable){
        if(throwable is IOException){
            mView?.onError("Please check your internet connection.")
        }else mView?.onError(throwable.message.toString())
    }

    private fun normalizeAndAddObjects(bitmapWidth: Int, bitmapHeight: Int, objs: List<ObjectProposal>) {
        val targetSize = mTargetSize!!

        var width: Float
        val height: Float
        if (targetSize.height > targetSize.width) {
            height = targetSize.height.toFloat()
            width = targetSize.width.toFloat()
        } else {
            height = targetSize.width.toFloat()
            width = targetSize.height.toFloat()
        }

        val margin = 250
        val centerMargin = (margin / 2).toFloat()
        width -= margin

        val defaultRect = RectF()
        defaultRect.left = centerMargin
        defaultRect.top = height / 2 - width / 2
        defaultRect.right = width + centerMargin
        defaultRect.bottom = defaultRect.top + width

        mObjectProposalList.add(0, defaultRect)

        val matrixTransform = ImageUtils.getTransformationMatrix(
                bitmapWidth,
                bitmapHeight,
                targetSize.width,
                targetSize.height,
                0,
                true)

        for (objectProposal in objs) {
            val region = objectProposal.region!!
            val rectF = RectF(region.left, region.top, region.right, region.bottom)
            matrixTransform.mapRect(rectF)
            if (rectF.left < 0)
                rectF.left = 0F
            if (rectF.top < 0)
                rectF.top = 0F
            if (rectF.bottom > targetSize.height)
                rectF.bottom = targetSize.height.toFloat()
            if (rectF.right > targetSize.width)
                rectF.right = targetSize.width.toFloat()

            val distance = distance(defaultRect.centerX(), defaultRect.centerY(), rectF.centerX(), rectF.centerY())
            val thirdWidth = defaultRect.width()/3
            if(distance >0 && distance <= thirdWidth){
                mObjectProposalList.remove(defaultRect)
            }

            mObjectProposalList.add(rectF)
        }
    }

    private fun distance(x1 : Float, y1 : Float, x2 : Float, y2 : Float) : Float{
        val dx   = (x1 - x2).toDouble()
        val dy   = (y1 - y2).toDouble()
        return Math.sqrt( dx*dx + dy*dy ).toFloat()
    }

    override fun onPictureTakenOriginal(cameraView: BaseCameraView, original: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(original, 0, original.size)
        mBitmapForPreviewing = bitmap
        mBitmapForCropping = bitmap
        mTargetSize = Size(bitmap.width, bitmap.height)
        mView?.setImPreviewBitmap(bitmap)
    }

    override fun isStillKeepingObjects(): Boolean {
        return !mObjectProposalList.isEmpty()
    }

    override fun cropObjectImage(rectF: RectF) {
        val newRectF = RectF(rectF)
        val targetSize = mTargetSize!!
        val bitmapForCropping = mBitmapForCropping!!
        val bitmapForPreviewing = mBitmapForPreviewing!!

        if (bitmapForCropping != bitmapForPreviewing
                && (bitmapForCropping.width != bitmapForPreviewing.width ||
                        bitmapForCropping.height != bitmapForPreviewing.height)) {

            val matrixTransform = ImageUtils.getTransformationMatrix(
                    targetSize.width,
                    targetSize.height,
                    bitmapForCropping.width,
                    bitmapForCropping.height,
                    0,
                    true)
            matrixTransform.mapRect(newRectF)
        }

        if (newRectF.left < 0)
            newRectF.left = 0f
        if (newRectF.top < 0)
            newRectF.top = 0f
        if (newRectF.bottom > bitmapForCropping.height)
            newRectF.bottom = bitmapForCropping.height.toFloat()
        if (newRectF.right > bitmapForCropping.width)
            newRectF.right = bitmapForCropping.width.toFloat()

        val croppedBitmap = Bitmap.createBitmap(bitmapForCropping,
                newRectF.left.toInt(),
                newRectF.top.toInt(),
                newRectF.width().toInt(),
                newRectF.height().toInt())
        mView?.showOffersActivity(croppedBitmap, rectF, mOfferList.toParcelable(), mSearchTime)
    }

    override fun clear() {
        unsubscribe()
        mObjectProposalList.clear()
        mOfferList.clear()

        mView?.showLabelCapture()
        mView?.showCircleView()
        mView?.hideImageCameraPreview()
        mView?.hideLoading()
        mView?.hideValidateView()
        mView?.hideViewPinCropper()
    }

    override fun unsubscribe() {
        mIsMatching = false
        mCompositeDisposable.clear()
    }

    override fun isMatching(): Boolean {
        return mIsMatching
    }

    override fun onSharedImage(imageUri: Uri, targetWidth: Int, targetHeight: Int) {
        val bmp = ImageUtils.rotateImageFromUri(mContext, imageUri)
        if (bmp == null) {
            onError("Can't load bitmap from image path")
            return
        }
        if (bmp.width < 416 && bmp.height < 416) {
            onError("Please select a picture with minimum size 416x416")
            return
        }

        mBitmapForCropping = bmp
        mBitmapForPreviewing = ImageUtils.resize(mContext, mBitmapForCropping!!, targetWidth, targetHeight)
        mTargetSize = Size(mBitmapForPreviewing!!.width, mBitmapForPreviewing!!.height)
        mView?.setImPreviewBitmap(mBitmapForPreviewing!!)

        val out = ByteArrayOutputStream()
        mBitmapForCropping?.compress(Bitmap.CompressFormat.JPEG, 80, out)
        val byteArray = out.toByteArray()

        val resizedImageByte = ImageUtils.resize(mContext, byteArray, 512, 512)
        match(resizedImageByte)
    }
}