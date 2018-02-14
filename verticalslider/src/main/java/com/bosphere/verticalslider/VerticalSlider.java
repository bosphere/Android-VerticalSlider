package com.bosphere.verticalslider;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bo on 7/2/18.
 */

public class VerticalSlider extends View {

    private static final int THUMB_RADIUS_FG = 6;

    private static final int TRACK_HEIGHT_BG = 4;
    private static final int TRACK_HEIGHT_FG = 2;

    private static final int COLOR_BG = Color.parseColor("#dddfeb");
    private static final int COLOR_FG = Color.parseColor("#7da1ae");

    private int mThumbRadius;
    private int mTrackBgThickness, mTrackFgThickness;
    private Paint mThumbFgPaint;
    private Paint mTrackBgPaint, mTrackFgPaint;
    private RectF mTrackRect;
    private OnProgressChangeListener mListener;
    private float mProgress = 0.0f;

    public VerticalSlider(Context context) {
        super(context);
        init(null, 0);
    }

    public VerticalSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VerticalSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void setThumbColor(int color) {
        mThumbFgPaint.setColor(color);
        invalidate();
    }

    public void setTrackColor(int color) {
        mTrackFgPaint.setColor(color);
        invalidate();
    }

    public void setTrackBgColor(int color) {
        mTrackBgPaint.setColor(color);
        invalidate();
    }

    public void setThumbRadiusPx(int radiusPx) {
        mThumbRadius = radiusPx;
        invalidate();
    }

    public void setTrackFgThicknessPx(int heightPx) {
        mTrackFgThickness = heightPx;
        invalidate();
    }

    public void setTrackBgThicknessPx(int heightPx) {
        mTrackBgThickness = heightPx;
        invalidate();
    }

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    public void setProgress(float progress, boolean notifyListener) {
        onProgressChanged(progress, notifyListener);
    }

    public void setOnSliderProgressChangeListener(OnProgressChangeListener listener) {
        mListener = listener;
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        int colorDefaultBg = resolveAttrColor("colorControlNormal", COLOR_BG);
        int colorDefaultFg = resolveAttrColor("colorControlActivated", COLOR_FG);

        mThumbFgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbFgPaint.setStyle(Paint.Style.FILL);
        mThumbFgPaint.setColor(colorDefaultFg);

        mTrackBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackBgPaint.setStyle(Paint.Style.FILL);
        mTrackBgPaint.setColor(colorDefaultBg);

        mTrackFgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackFgPaint.setStyle(Paint.Style.FILL);
        mTrackFgPaint.setColor(colorDefaultFg);

        mTrackRect = new RectF();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mThumbRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, THUMB_RADIUS_FG, dm);
        mTrackBgThickness = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TRACK_HEIGHT_BG, dm);
        mTrackFgThickness = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TRACK_HEIGHT_FG, dm);

        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalSlider, defStyleAttr, 0);
            int thumbColor = arr.getColor(R.styleable.VerticalSlider_vs_thumb_color, mThumbFgPaint.getColor());
            mThumbFgPaint.setColor(thumbColor);

            int trackColor = arr.getColor(R.styleable.VerticalSlider_vs_track_fg_color, mTrackFgPaint.getColor());
            mTrackFgPaint.setColor(trackColor);

            int trackBgColor = arr.getColor(R.styleable.VerticalSlider_vs_track_bg_color, mTrackBgPaint.getColor());
            mTrackBgPaint.setColor(trackBgColor);

            mThumbRadius = arr.getDimensionPixelSize(R.styleable.VerticalSlider_vs_thumb_radius,
                    mThumbRadius);
            mTrackFgThickness = arr.getDimensionPixelSize(R.styleable.VerticalSlider_vs_track_fg_thickness,
                    mTrackFgThickness);
            mTrackBgThickness = arr.getDimensionPixelSize(R.styleable.VerticalSlider_vs_track_bg_thickness,
                    mTrackBgThickness);

            arr.recycle();
        }
    }

    private int resolveAttrColor(String attrName, int defaultColor) {
        String packageName = getContext().getPackageName();
        int attrRes = getResources().getIdentifier(attrName, "attr", packageName);
        if (attrRes <= 0) {
            return defaultColor;
        }
        TypedValue value = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(attrRes, value, true);
        return getResources().getColor(value.resourceId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        int contentWidth = getPaddingTop() + mThumbRadius * 2 + getPaddingBottom();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            width = Math.max(contentWidth, getSuggestedMinimumWidth());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        float y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int height = getHeight() - getPaddingTop() - getPaddingBottom() - 2 * mThumbRadius;
                onProgressChanged(1 - y / height, true);
                break;
        }
        return true;
    }

    private void onProgressChanged(float progress, boolean notifyChange) {
        mProgress = progress;
        if (mProgress < 0) {
            mProgress = 0;
        } else if (mProgress > 1f) {
            mProgress = 1;
        }
        invalidate();
        if (notifyChange && mListener != null) {
            mListener.onProgress(mProgress);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTrack(canvas, mThumbRadius, mTrackBgThickness, 0, mTrackBgPaint, 1f);
        int trackPadding =
                mTrackBgThickness > mTrackFgThickness ? (mTrackBgThickness - mTrackFgThickness) >> 1 : 0;
        drawTrack(canvas, mThumbRadius, mTrackFgThickness, trackPadding, mTrackFgPaint, mProgress);

        // draw bg thumb
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom() - 2 * mThumbRadius - 2 * trackPadding;
        int leftOffset = (width - mThumbRadius * 2) >> 1;
        canvas.drawCircle(getPaddingLeft() + leftOffset + mThumbRadius, getPaddingTop() + mThumbRadius + (1 - mProgress) * height + trackPadding,
                mThumbRadius, mThumbFgPaint);
    }

    private void drawTrack(Canvas canvas, int thumbRadius, int trackThickness, int trackPadding, Paint trackPaint, float progress) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom() - 2 * thumbRadius;

        int trackLeft = getPaddingLeft() + ((width - trackThickness) >> 1);
        int trackTop = (int) (getPaddingTop() + thumbRadius + (1 - progress) * height) + trackPadding;
        int trackRight = trackLeft + trackThickness;
        int trackBottom = getHeight() - getPaddingBottom() - thumbRadius - trackPadding;
        float trackRadius = trackThickness * 0.5f;
        mTrackRect.set(trackLeft, trackTop, trackRight, trackBottom);
        canvas.drawRoundRect(mTrackRect, trackRadius, trackRadius, trackPaint);
    }

    public interface OnProgressChangeListener {
        void onProgress(float progress);
    }
}
