package de.everybag.express.custom.pinViewCropper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.everybag.express.R;

/**
 * PinView.kt - View class that draw a Pin
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
public class PinView extends View {
    public static final float SIZE_VIEW_DP = 40f;
    final float STROKE_CONTAINER_CIRCLE_DP = 1;
    final float RADIUS_CONTAINER_CIRCLE_DP = 10.5f;
    final float RADIUS_CIRCLE_DP = 5.5f;
    Paint mContainerPaint;
    Paint mCirclePaint;
    float mStrokeContainerCirclePx, mRadiusContainerCirclePX, mRaidusCirclePx, mSizeView;
    private RectF region;
    private List<OnPinClickListener> onPinClickListeners;

    public PinView(final Context context, final RectF region) {
        super(context);
        this.region = region;
        onPinClickListeners = new ArrayList<>();
        Resources r = getResources();
        mStrokeContainerCirclePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_CONTAINER_CIRCLE_DP, r.getDisplayMetrics());
        mRadiusContainerCirclePX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RADIUS_CONTAINER_CIRCLE_DP, r.getDisplayMetrics());
        mRaidusCirclePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RADIUS_CIRCLE_DP, r.getDisplayMetrics());
        mSizeView = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SIZE_VIEW_DP, r.getDisplayMetrics());

        mContainerPaint = new Paint();
        mContainerPaint.setStyle(Paint.Style.STROKE);
        mContainerPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mContainerPaint.setStrokeWidth(mStrokeContainerCirclePx);
        mContainerPaint.setAntiAlias(true);
        mContainerPaint.setDither(true);
        mContainerPaint.setFilterBitmap(true);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setFilterBitmap(true);

        setClickable(true);
        setFocusable(true);

        setOnClickListener(view -> {
            for (OnPinClickListener listener : onPinClickListeners) {
                if (listener != null)
                    listener.pinClick(region);
            }
        });
    }

    public void setRecognition(RectF region) {
        this.region = region;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        setMeasuredDimension((int) mSizeView, (int) mSizeView);
    }

    @Override
    public void onDraw(final Canvas canvas) {
        if (region != null) {
            float center = mSizeView / 2;
            canvas.drawCircle(center, center, mRadiusContainerCirclePX, mContainerPaint);
            canvas.drawCircle(center, center, mRaidusCirclePx, mCirclePaint);
        }
    }

    public void addOnPinClickListner(OnPinClickListener onPinClickListener) {
        onPinClickListeners.add(onPinClickListener);
    }
}
