package com.handlecar.customprogressbar.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.handlecar.customprogressbar.R;

/**
 CustomProgressBar 自定义的进度条控件,修改了系统默认控件的样式
 Created by bob on 2016/12/28 11:29.
 */
public class CustomProgressBar extends ProgressBar {

	private static final int DEFAULT_REACH_COLOR = 0xFFA500;
	private static final int DEFAULT_UNREACH_COLOR = 0XCDB38B;
//	private static final int DEFAULT_REACH_HEIGHT = dp2px(10);
//	private static final int DEFAULT_UNREACH_HEIGHT = dp2px(5);
//	private static final int DEFAULT_TEXT_SIZE = sp2px(10);
//	private static final int DEFAULT_TEXT_OFFSET = dp2px(5);
	private static final int DEFAULT_TEXT_COLOR = 0xFFA500;
	private int reachColor;
	private int unreachColor;
	private int reachHeight;
	private int unreachHeight;
	private int textSize;
	private int textOffset;
	private int textColor;
	private Paint mPaint;
	private float baseLineHeight;

	public CustomProgressBar(Context context) {
		this(context, null);
	}

	public CustomProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomProgressBarStyle);
		reachColor = ta.getColor(R.styleable.CustomProgressBarStyle_reach_color, DEFAULT_REACH_COLOR);
		unreachColor = ta.getColor(R.styleable.CustomProgressBarStyle_unreach_color, DEFAULT_UNREACH_COLOR);
		reachHeight = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_reach_height, dp2px(10));
		unreachHeight = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_unreach_height, dp2px(5));
		textSize = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_text_size, sp2px(10));
		textOffset = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_text_offset, dp2px(5));
		textColor = ta.getColor(R.styleable.CustomProgressBarStyle_text_color, DEFAULT_TEXT_COLOR);

		ta.recycle();
		mPaint = new Paint();
		mPaint.setTextSize(textSize);

	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int width = widthSpecSize;
		int height = 0;
		if (heightSpecMode == MeasureSpec.EXACTLY) {
			height = heightSpecSize;
		} else {
			baseLineHeight = -(mPaint.descent() + mPaint.ascent()) / 2;
			height = (int) (getPaddingBottom() + getPaddingTop() + Math.max(Math.max(reachHeight, unreachHeight), baseLineHeight));
		}
		setMeasuredDimension(widthSpecSize, height);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		canvas.save();
		canvas.translate(getPaddingLeft(), getHeight() / 2);
		int mProgress = getProgress();
		int mMaxProgress = getMax();
		int mRatio = (int) (((float) mProgress) / mMaxProgress * 100);
		/** 进度条总宽度 */
		int progressTotalWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int mReachedWidth = (int) (progressTotalWidth * mRatio / 100);
		//进度字符串
		String mRatioStr = mRatio + "%";
		//文字的宽度
		float textWidth = mPaint.measureText(mRatioStr);

		//画已经完成的进度
		mPaint.setStrokeWidth(reachHeight);
		mPaint.setColor(reachColor);
		if (mReachedWidth + textOffset * 2 + textWidth > progressTotalWidth) {
			//已经划到进度的最后了
			mReachedWidth = (int) (getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - textOffset - textWidth);
			canvas.drawLine(0, 0, mReachedWidth, 0, mPaint);
		} else {
			canvas.drawLine(0, 0, mReachedWidth, 0, mPaint);
		}

		//画进度百分比文字
		mPaint.setColor(textColor);
		canvas.drawText(mRatioStr, mReachedWidth + textOffset, baseLineHeight, mPaint);

		//画未完成的进度条
		mPaint.setColor(unreachColor);
		mPaint.setStrokeWidth(unreachHeight);
		if (mReachedWidth + textOffset * 2 + textWidth < progressTotalWidth) {
			canvas.drawLine(mReachedWidth + textOffset * 2 + textWidth, 0, progressTotalWidth, 0, mPaint);
		}

		canvas.restore();
	}

	private int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
	}

	private int sp2px(int spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
	}
}
