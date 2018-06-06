package de.everybag.express.ui.mainscreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.support.media.ExifInterface
import de.everybag.express.BuildConfig
import de.everybag.express.di.ActivityScoped
import de.everybag.express.utils.toParcelable
import io.nyris.camera.*
import io.nyris.sdk.*
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 *
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
        if (!BuildConfig.DEBUG)
            return
        mView?.showMessage("onCameraClosed")
    }

    override fun onCameraOpened(cameraView: BaseCameraView) {
        if (!BuildConfig.DEBUG)
            return
        mView?.showMessage("onCameraOpened")
    }

    override fun onError(errorMessage: String) {
        mView?.onError(errorMessage)
    }

    override fun onPictureTaken(cameraView: BaseCameraView, resizedImage: ByteArray) {
        mOfferList.clear()
        mObjectProposalList.clear()
        mIsMatching = true

        match(resizedImage)
    }

    private fun match(resizedImage: ByteArray) {
        val resizedImageBtm = BitmapFactory.decodeByteArray(resizedImage, 0, resizedImage.size)
        val obs1 = matchingApi
                .exact(true)
                .similarity(false)
                .ocr(false)
                .match(resizedImage)

        val obs2 = objectProposalApi.extractObjects(resizedImage)

        val disposable = Single.zip(obs1, obs2, BiFunction<OfferResponseBody, List<ObjectProposal>, List<ObjectProposal>> { offerResponseBody: OfferResponseBody, objs: List<ObjectProposal> ->
            mOfferList.addAll(offerResponseBody.offers)
            objs
        })
                .subscribe({
                    normalizeAndAddObjects(resizedImageBtm.width, resizedImageBtm.height, it)
                    mView?.hideLoading()
                    mView?.showObjects(mObjectProposalList)
                    mIsMatching = false
                }, {
                    normalizeAndAddObjects(resizedImageBtm.width, resizedImageBtm.height, mutableListOf())
                    mView?.onError(it.message.toString())
                    mIsMatching = false
                })

        mCompositeDisposable.add(disposable)
    }

    private fun normalizeAndAddObjects(bitmapWidth: Int, bitmapHeight: Int, objs: List<ObjectProposal>) {
        val targetSize = mTargetSize!!
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

            mObjectProposalList.add(rectF)
        }

        if (!mObjectProposalList.isEmpty() && !mOfferList.isEmpty())
            return

        val width: Float
        val height: Float
        if (targetSize.height > targetSize.width) {
            height = targetSize.height.toFloat()
            width = targetSize.width.toFloat()
        } else {
            height = targetSize.width.toFloat()
            width = targetSize.height.toFloat()
        }

        val rectF = RectF()
        rectF.left = 0f
        rectF.top = height / 2 - width / 2
        rectF.right = width
        rectF.bottom = rectF.top + width

        mObjectProposalList.add(0, rectF)
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
            matrixTransform.mapRect(rectF)
        }

        if (rectF.left < 0)
            rectF.left = 0f
        if (rectF.top < 0)
            rectF.top = 0f
        if (rectF.bottom > bitmapForCropping.height)
            rectF.bottom = bitmapForCropping.height.toFloat()
        if (rectF.right > bitmapForCropping.width)
            rectF.right = bitmapForCropping.width.toFloat()

        val croppedBitmap = Bitmap.createBitmap(bitmapForCropping,
                rectF.left.toInt(),
                rectF.top.toInt(),
                rectF.width().toInt(),
                rectF.height().toInt())

        mView?.showOffersActivity(croppedBitmap, rectF, mOfferList.toParcelable())
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

    fun rotateImageFromUri(context: Context, imageUri: Uri): Bitmap? {
        val exif = ExifInterface(context.contentResolver.openInputStream(imageUri))
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        var captureBmp: Bitmap? = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        captureBmp = rotateBitmap(captureBmp, orientation)
        return captureBmp
    }

    fun rotateBitmap(bitmap: Bitmap?, @ExifOrientation orientation: Int): Bitmap? {
        if (bitmap == null)
            return null
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }

        return try {
            val bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    override fun onSharedImage(imageUri: Uri, targetWidth: Int, targetHeight: Int) {
        val bmp = rotateImageFromUri(mContext, imageUri)
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