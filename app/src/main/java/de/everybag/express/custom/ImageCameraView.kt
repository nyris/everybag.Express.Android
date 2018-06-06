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

    init{
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
