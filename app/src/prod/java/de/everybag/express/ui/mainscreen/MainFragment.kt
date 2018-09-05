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

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.SharedElementCallback
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import de.everybag.express.R
import de.everybag.express.base.BaseFragment
import de.everybag.express.di.ActivityScoped
import de.everybag.express.model.OfferParcelable
import de.everybag.express.ui.resultscreen.SearchResultActivity
import de.everybag.express.utils.KeysConst
import de.everybag.express.utils.ParamsUtils
import io.nyris.camera.Callback
import io.nyris.camera.ImageUtils
import kotlinx.android.synthetic.main.view_camera.*
import kotlinx.android.synthetic.prod.fragment_main.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * MainFragment.kt - Fragment that displays a camera. User can take picture to match offers, object selection or image cropping.
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */

@ActivityScoped
class MainFragment @Inject constructor() : BaseFragment<MainContract.Presenter>(), MainContract.View,
        EasyPermissions.PermissionCallbacks {
    companion object {
        const val RC_CAMERA_PERM = 123
    }

    @Inject
    lateinit var mPresenter: MainContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cvTakePic.startAnimation(vPosCam)
        hideLoading()
        cvTakePic.setOnClickListener {
            takePicture()
            hideCircleView()
            hideViewPinCropper()
            hideLabelCapture()
            showLoading()
        }

        viewPinCropper.addOnPinClickListener {
            viewPinCropper.initCropWindow(it)
        }

        imValidate.setOnClickListener {
            mPresenter.cropObjectImage(viewPinCropper.selectedObjectProposal)
        }

        ActivityCompat.setExitSharedElementCallback(context as Activity, object : SharedElementCallback() {
            override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?, sharedElementSnapshots: List<View>?) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                for (shared in sharedElements!!) {
                    shared.visibility = View.GONE
                }

                for (shared in sharedElementSnapshots!!) {
                    shared.visibility = View.GONE
                }

                imCropped.setImageBitmap(null)
            }
        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        for (permission in perms) {
            if (permission == android.Manifest.permission.CAMERA) {
                Toast.makeText(context, "Camera permission denied!", Toast.LENGTH_LONG).show()
            }

            if (permission == android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                Toast.makeText(context, "External Storage permission denied!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDetach()
    }

    override fun onResume() {
        super.onResume()
        imCropped.setImageBitmap(null)

        if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mPresenter.onAttach(this)
            if (mPresenter.isMatching())
                return

            val action = arguments?.getString(KeysConst.ACTION)
            if (action == null) {
                if (!mPresenter.isStillKeepingObjects()) {
                    startCamera()
                    mPresenter.clear()
                } else {
                    showCircleView()
                    showValidateView()
                    showViewPinCropper()
                }
            } else {
                val imageUri = arguments?.getParcelable<Uri>(KeysConst.SCREENSHOT_PATH)
                arguments?.putString(KeysConst.ACTION, null)

                if (imageUri == null) {
                    onError("Can't load image path")
                    return
                }

                hideLabelCapture()
                hideCircleView()
                showImageCameraPreview()
                showLoading()
                hideValidateView()
                hideViewPinCropper()

                if (camera.width == 0) {
                    val post = camera.post {
                        mPresenter.onSharedImage(imageUri, camera.width, camera.height)
                    }
                } else {
                    mPresenter.onSharedImage(imageUri, camera.width, camera.height)
                }
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "Permissions",
                    RC_CAMERA_PERM,
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onDetach()
        stopCamera()
    }

    override fun takePicture() {
        camera.takePicture()
        camera.stopPreview()
    }

    override fun setImPreviewBitmap(bitmap: Bitmap) {
        stopCamera()
        imPreview.setBitmap(null)
        showImageCameraPreview()
        imPreview.setBitmap(bitmap)
    }

    override fun showObjects(objects: List<RectF>) {
        showViewPinCropper()
        viewPinCropper.setExtractedObjects(objects)
        val firstObject = objects[0]
        viewPinCropper.initCropWindow(firstObject) {
            mPresenter.cropObjectImage(it)
        }
    }

    override fun showOffersActivity(croppedBitmap: Bitmap, rectF: RectF, offers: ArrayList<OfferParcelable>) {
        imCropped.setImageBitmap(croppedBitmap)
        val layoutParams = imCropped.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = rectF.width().toInt()
        layoutParams.height = rectF.height().toInt()
        layoutParams.leftMargin = (rectF.centerX() - rectF.width() / 2).toInt()
        layoutParams.topMargin = (rectF.centerY() - rectF.height() / 2).toInt()
        imCropped.layoutParams = layoutParams

        Handler().postDelayed({
            hideCircleView()
            hideViewPinCropper()
            hideValidateView()
            val intent = Intent(context, SearchResultActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context as Activity,
                    imCropped,
                    ViewCompat.getTransitionName(imCropped))
            val bundle = Bundle()
            if (offers.isNotEmpty()) {
                bundle.putParcelableArrayList("listOffers", offers)
            }

            val stream = ByteArrayOutputStream()
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            var byteArray = stream.toByteArray()
            byteArray = ImageUtils.resize(context!!, byteArray, 512, 512)
            bundle.putByteArray("image", byteArray)
            intent.putExtras(bundle)

            startActivity(intent, options.toBundle())
        }, 100)
    }

    override fun addCameraCallback(callback: Callback) {
        camera.addCallback(callback)
    }

    override fun removeCameraCallback(callback: Callback) {
        camera.removeCallback(callback)
    }

    override fun startCamera() {
        camera.start()
    }

    override fun stopCamera() {
        camera.stopPreview()
        camera.stop()
    }

    override fun showViewPinCropper() {
        viewPinCropper.visibility = View.VISIBLE
    }

    override fun hideViewPinCropper() {
        viewPinCropper.visibility = View.INVISIBLE
    }

    override fun showCircleView() {
        cvTakePic.visibility = View.VISIBLE
    }

    override fun hideCircleView() {
        cvTakePic.visibility = View.INVISIBLE
    }

    override fun showValidateView() {
        imValidate.visibility = View.VISIBLE
    }

    override fun hideValidateView() {
        imValidate.visibility = View.GONE
    }

    override fun showImageCameraPreview() {
        rlCameraPreview.visibility = View.VISIBLE
    }

    override fun hideImageCameraPreview() {
        rlCameraPreview.visibility = View.INVISIBLE
    }

    override fun showLabelCapture() {
        tvCaptureLabel.visibility = View.VISIBLE
    }

    override fun hideLabelCapture() {
        tvCaptureLabel.visibility = View.GONE
    }

    override fun showLoading() {
        vProgress.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        vProgress.visibility = View.GONE
    }

    fun isCanBack(): Boolean {
        return !mPresenter.isStillKeepingObjects() && !mPresenter.isMatching()
    }

    fun clearView() {
        mPresenter.clear()
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            ParamsUtils.saveParam(context!!, KeysConst.BUDDY_SEARCH, false.toString())
            showMessage("Buddy Search deactivated.")
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            ParamsUtils.saveParam(context!!, KeysConst.BUDDY_SEARCH, true.toString())
            showMessage("Buddy Search activated.")
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activity?.onBackPressed()
        }
        return true
    }
}