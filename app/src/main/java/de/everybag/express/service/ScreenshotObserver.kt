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

package de.everybag.express.service

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import java.io.File

/**
 * ScreenshotObserver.kt - Content Observer
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */

class ScreenshotObserver(private val mContext: Context, private val mListener: IOnScreenshotTakenListener, handler: Handler) : ContentObserver(handler) {
    private var isFromEdit = false
    private var previousPath: String? = null
    private var tag = ScreenshotObserver::class.java.name

    override fun onChange(selfChange: Boolean, uri: Uri) {
        var cursor: Cursor? = null
        try {
            cursor = mContext.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA), null, null, null)
            if (cursor != null && cursor!!.moveToLast()) {
                val displayNameColumnIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val fileName = cursor.getString(displayNameColumnIndex)
                val path = cursor.getString(dataColumnIndex)
                if (File(path).lastModified() >= System.currentTimeMillis() - 10000) {
                    if (isScreenshot(path) && !isFromEdit && !(previousPath != null && previousPath == path)) {
                        if (!path.toLowerCase().contains("screenshot"))
                            return
                        Log.d(tag, "screen shot added $fileName $path")
                        val file = File(path)
                        mListener.onScreenshotTaken(Uri.fromFile(file))
                    }
                    previousPath = path
                    isFromEdit = false
                } else {
                    cursor.close()
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isFromEdit = true
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        super.onChange(selfChange, uri)
    }

    /**
     * Check if it's screenshot
     * @param path A variable of type String
     * @return Boolean value
     */
    private fun isScreenshot(path: String?): Boolean {
        return path != null && path.toLowerCase().contains("screenshot")
    }
}
