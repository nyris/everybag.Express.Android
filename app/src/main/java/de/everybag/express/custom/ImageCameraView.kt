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

package de.everybag.express.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * ImageCameraView.kt - View class that show a full bitmap without using aspect ratio
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */

class ImageCameraView : View {
    private var mPaint: Paint = Paint()
    private var mBitmap: Bitmap? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        mPaint.isAntiAlias = true
        mPaint.isFilterBitmap = true
        mPaint.isDither = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mBitmap == null)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        else
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(mBitmap!!.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(mBitmap!!.height, View.MeasureSpec.EXACTLY))
    }

    fun setBitmap(bitmap: Bitmap?) {
        mBitmap = bitmap
        requestLayout()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
    }
}
