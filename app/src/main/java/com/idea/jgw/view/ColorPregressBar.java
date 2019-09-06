package com.idea.jgw.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.idea.jgw.R;

public class ColorPregressBar extends View {
    private Paint mPaint;
    private RectF mRectF;
    //进度
    private int mProgress;

    //颜色以及宽度
    private int mFirstColor;
    private int mSecondColor;
    private int mCircleWidth;
    
    //是否到下一圈
    private boolean mChanged;
    
    //空间的宽度 以及 高度(两个值设为一样)
    private int mWidth;

    public ColorPregressBar(Context context) {
        this(context, null);
    }

    public ColorPregressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPregressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ColorProgressBar);
        mFirstColor = ta.getColor(R.styleable.ColorProgressBar_firstColor, 0xff909090);//Color.RED);
        mSecondColor = ta.getColor(R.styleable.ColorProgressBar_secondColor, 0xff575757);//Color.BLUE);
        mCircleWidth = ta.getDimensionPixelSize(R.styleable.ColorProgressBar_circleWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()));
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);

        mChanged = false;
    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		if (widthMode == MeasureSpec.AT_MOST) {
			mWidth = Math.min(widthSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 160, getResources().getDisplayMetrics()));
			setMeasuredDimension(mWidth, mWidth);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int center = getWidth() / 2;
		int radius = center - mCircleWidth / 2;
		mRectF = new RectF(center - radius, center - radius, center + radius, center + radius);
		if (!mChanged) {
			mPaint.setColor(mSecondColor);
			canvas.drawCircle(center, center, radius, mPaint);
			mPaint.setColor(mFirstColor);
			canvas.drawArc(mRectF, -90, mProgress, false, mPaint);
		} else {
			mPaint.setColor(mFirstColor);
			canvas.drawCircle(center, center, radius, mPaint);
			mPaint.setColor(mSecondColor);
			canvas.drawArc(mRectF, -90, mProgress, false, mPaint);
		}
		startProgress();
	}	
	
	private void startProgress() {
		if (isShown()) {
			postDelayed(new Runnable() {
				@Override
				public void run() {
					
					if (mProgress >= 360) {
						mProgress = 360;
						mChanged = !mChanged;
					}
					invalidate();
				}
			}, 10);
		}
	}	
	
	public void setProgress(int prg){
		mProgress = prg * 360 / 100 + 4;
	}
}

