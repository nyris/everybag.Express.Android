package de.everybag.express.service

import android.net.Uri

/**
 * IOnScreenshotTakenListener.java - An interface that contain signature of mListener when ScreenShot
 * is taken
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */

interface IOnScreenshotTakenListener {
    /**
     * On ScreenShot taken called when screenshot is made by a user
     * @param uri
     */
    fun onScreenshotTaken(uri: Uri)
}