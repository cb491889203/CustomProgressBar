package com.handlecar.customprogressbar.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.handlecar.customprogressbar.R;

/**
 CustomProgressBar 自定义的进度条控件,修改了系统默认控件的样式
 Created by bob on 2016/12/28 11:29.
 */
public class CustomProgressBar extends ProgressBar {

	private static final String TAG = "CustomProgressBar";
	private static final int DEFAULT_REACH_COLOR = 0xFFA500;
	private static final int DEFAULT_UNREACH_COLOR = 0XCDB38B;
	private static final int DEFAULT_REACH_HEIGHT = 10;
	private static final int DEFAULT_UNREACH_HEIGHT = 5;
	private static final int DEFAULT_TEXT_SIZE = 10;
	private static final int DEFAULT_TEXT_OFFSET = 5;
	private static final int DEFAULT_TEXT_COLOR = 0xFFA500;
	private static final int DEFAULT_RADIUS = 20;
	private static final float DEFAULT_BEGIN_ANGLE = 0.0f;
	private static final int DEFUALT_STYLE = 0;
	/** 进度条样式, 0: 横向进度; 1:圆形进度 , 默认为0*/
	private int mStyle;
	private int mRadius;
	private float mBeginAngle;
	private int reachColor;
	private int unreachColor;
	private int reachHeight;
	private int unreachHeight;
	private int textSize;
	private int textOffset;
	private int textColor;
	private Paint mPaint;
	private float baseLineHeight;
	private int mRealWidth;

	public CustomProgressBar(Context context) {
		this(context, null);
	}

	public CustomProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomProgressBarStyle);
		mStyle = ta.getInt(R.styleable.CustomProgressBarStyle_progress_style, DEFUALT_STYLE);
		reachColor = ta.getColor(R.styleable.CustomProgressBarStyle_reach_color, DEFAULT_REACH_COLOR);
		unreachColor = ta.getColor(R.styleable.CustomProgressBarStyle_unreach_color, DEFAULT_UNREACH_COLOR);
		reachHeight = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_reach_height, dp2px(DEFAULT_REACH_HEIGHT));
		unreachHeight = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_unreach_height, dp2px(DEFAULT_UNREACH_HEIGHT));
		textSize = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_text_size, sp2px(DEFAULT_TEXT_SIZE));
		textOffset = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_text_offset, dp2px(DEFAULT_TEXT_OFFSET));
		textColor = ta.getColor(R.styleable.CustomProgressBarStyle_text_color, DEFAULT_TEXT_COLOR);
		mRadius = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_radius, dp2px(DEFAULT_RADIUS));
		mBeginAngle = ta.getFloat(R.styleable.CustomProgressBarStyle_begin_angle, DEFAULT_BEGIN_ANGLE) % 360;
		ta.recycle();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(textSize);

	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mStyle == 0) {
			measureHorizonalStyle(widthMeasureSpec, heightMeasureSpec);
		} else if (mStyle == 1) {
			measureCircleStyle(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/** 测量圆形的进度条
	 @param widthMeasureSpec
	 @param heightMeasureSpec
	 */
	private void measureCircleStyle(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int width;
		int height;
		//测量宽
		if (widthSpecMode == MeasureSpec.EXACTLY) {
			width = widthSpecSize;
		} else {
			width = getPaddingLeft() + getPaddingRight() + mRadius * 2 + Math.max(reachHeight, unreachHeight);
			if (widthSpecMode == MeasureSpec.AT_MOST) {
				width = Math.min(width, widthSpecSize);
			}
		}
		//测量高
		if (heightSpecMode == MeasureSpec.EXACTLY) {
			height = heightSpecSize;
		} else {
			height = getPaddingBottom() + getPaddingTop() + mRadius * 2 + Math.max(reachHeight, unreachHeight);
			if (heightSpecMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSpecSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	/** 测量横向的进度条
	 @param widthMeasureSpec
	 @param heightMeasureSpec
	 */
	private void measureHorizonalStyle(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int width = widthSpecSize;
		int height = 0;
		if (heightSpecMode == MeasureSpec.EXACTLY) {
			height = heightSpecSize;
		} else {
			int textHeight = (int) (mPaint.descent() - mPaint.ascent());
			height = (int) (getPaddingBottom() + getPaddingTop() + Math.max(Math.max(reachHeight, unreachHeight), textHeight));
			if (heightSpecMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSpecSize);
			}
		}
		mRealWidth = width - getPaddingLeft() - getPaddingRight();
		setMeasuredDimension(widthSpecSize, height);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		if (mStyle == 0) {
			drawHorizonalStyle(canvas);
		} else if (mStyle == 1) {
			drawCircleStyle(canvas);
		}
	}

	/** 绘制圆形的进度条
	 @param canvas
	 */
	private void drawCircleStyle(Canvas canvas) {
		canvas.save();
		//先将画布移动到中心位置
		canvas.translate(getPaddingLeft() + (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 2, getMeasuredHeight() / 2);
		int mProgress = getProgress();
		int mMaxProgress = getMax();
		int mRatio = (int) (((float) mProgress) / mMaxProgress * 100);
		Log.i(TAG, "onDraw: mProgress = " + mProgress + " ; mMaxProgress = " + mMaxProgress + " ; mRatio = " + mRatio);
		//进度字符串
		String mRatioStr = mRatio + "%";
		//文字的宽度
		float textWidth = mPaint.measureText(mRatioStr);
		//画进度百分比文字
		mPaint.setColor(textColor);
		mPaint.setStyle(Paint.Style.FILL);
		Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
		baseLineHeight = -(fontMetrics.top + fontMetrics.bottom)/2;
		canvas.drawText(mRatioStr, -textWidth / 2, baseLineHeight, mPaint);

		//画未完成的底色
		mPaint.setColor(unreachColor);
		mPaint.setStrokeWidth(unreachHeight);
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(0,0,mRadius,mPaint);

		//画已完成的进度条
		//圆弧的区域是中间圆圈的范围
		RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
		mPaint.setColor(reachColor);
		mPaint.setStrokeWidth(reachHeight);
		canvas.drawArc(rectF, mBeginAngle, mRatio * 360 / 100, false, mPaint);

		canvas.restore();
	}

	/**
	  绘制横向的进度条
	 @param canvas
	 */
	private void drawHorizonalStyle(Canvas canvas) {
		canvas.save();
		canvas.translate(getPaddingLeft(), getHeight() / 2);
		int mProgress = getProgress();
		int mMaxProgress = getMax();
		int mRatio = (int) (((float) mProgress) / mMaxProgress * 100);
		Log.i(TAG, "onDraw: mProgress = " + mProgress + " ; mMaxProgress = " + mMaxProgress + " ; mRatio = " + mRatio);
		/** 进度条总宽度 */
		int progressTotalWidth = mRealWidth;
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
			mReachedWidth = (int) (progressTotalWidth - textOffset * 2 - textWidth);
			canvas.drawLine(0, 0, mReachedWidth, 0, mPaint);
		} else {
			canvas.drawLine(0, 0, mReachedWidth, 0, mPaint);
		}

		//画进度百分比文字
		mPaint.setColor(textColor);
		Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
		baseLineHeight = -(fontMetrics.top + fontMetrics.bottom)/2;
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
