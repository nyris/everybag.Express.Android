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

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import de.everybag.express.R

/**
 * CircleView.kt - View class that draw a Circle view and animate this circle from position X to Y
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class CircleView : View {
    private val mStrokeContainerCircleDp = 17f
    private val mRadiusContainerCircleDp = 41.5f
    private val radiusCircleDp = 17.5f

    private var mContainerPaint: Paint
    private var mCirclePaint: Paint
    private var mStrokeContainerCirclePx: Float = 0.toFloat()
    private var mRadiusContainerCirclePX: Float = 0.toFloat()
    private var mRadiusCirclePx: Float = 0.toFloat()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val r = resources
        mStrokeContainerCirclePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mStrokeContainerCircleDp, r.displayMetrics)
        mRadiusContainerCirclePX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRadiusContainerCircleDp, r.displayMetrics)
        mRadiusCirclePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusCircleDp, r.displayMetrics)

        mContainerPaint = Paint()
        mContainerPaint.style = Paint.Style.STROKE
        mContainerPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        mContainerPaint.strokeWidth = mStrokeContainerCirclePx
        mContainerPaint.isAntiAlias = true
        mContainerPaint.isDither = true
        mContainerPaint.isFilterBitmap = true

        mCirclePaint = Paint()
        mCirclePaint.style = Paint.Style.FILL
        mCirclePaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        mCirclePaint.isAntiAlias = true
        mCirclePaint.isDither = true
        mCirclePaint.isFilterBitmap = true
    }

    fun startAnimation(vPosCam: View) {
        val r = resources
        mStrokeContainerCirclePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mStrokeContainerCircleDp, r.displayMetrics)
        mRadiusContainerCirclePX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRadiusContainerCircleDp, r.displayMetrics)
        mRadiusCirclePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusCircleDp, r.displayMetrics)
        postDelayed({
            scaleCircleAnimation()

            val valueAnimator = ValueAnimator.ofFloat(0F, (vPosCam.top - top).toFloat())
            valueAnimator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                translationY = value
            }
            valueAnimator.interpolator = DecelerateInterpolator(3f)
            valueAnimator.duration = 700

            valueAnimator.start()
        }, 500)
    }

    private fun scaleCircleAnimation() {
        val originalStroke = mStrokeContainerCirclePx
        val originalCircle = mRadiusCirclePx
        val valueAnimator = ValueAnimator.ofFloat(0F, 40F)
        valueAnimator.duration = 500
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            mStrokeContainerCirclePx = originalStroke - value
            mRadiusCirclePx = originalCircle + value
            mContainerPaint.strokeWidth = mStrokeContainerCirclePx
            invalidate()
            requestLayout()
        }
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val x = width
        canvas.drawCircle((x / 2).toFloat(), (width / 2).toFloat(), mRadiusContainerCirclePX, mContainerPaint)
        canvas.drawCircle((x / 2).toFloat(), (width / 2).toFloat(), mRadiusCirclePx, mCirclePaint)
    }
}