package com.handlecar.customprogressbar.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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
	private static final int DEFAULT_BEGIN_COLOR = 0xFF0000;
	private static final int DEFAULT_END_COLOR = 0x00FF00;
	/** 进度条样式, 0: 横向进度; 1:圆形进度 , 默认为0 */
	private int mStyle;
	private int mRadius;
	private float mBeginAngle;
	private int mReachColor;
	private int mUnreachColor;
	private int mReachHeight;
	private int mUnreachHeight;
	private int mTextSize;
	private int mTextOffset;
	private int mTextColor;
	private Paint mPaint;
	private float mBaseLineHeight;
	private int mRealWidth;
	private int mBeginColor;
	private int mEndColor;

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
		mReachColor = ta.getColor(R.styleable.CustomProgressBarStyle_reach_color, DEFAULT_REACH_COLOR);
		mUnreachColor = ta.getColor(R.styleable.CustomProgressBarStyle_unreach_color, DEFAULT_UNREACH_COLOR);
		mReachHeight = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_reach_height, dp2px(DEFAULT_REACH_HEIGHT));
		mUnreachHeight = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_unreach_height, dp2px(DEFAULT_UNREACH_HEIGHT));
		mTextSize = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_text_size, sp2px(DEFAULT_TEXT_SIZE));
		mTextOffset = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_text_offset, dp2px(DEFAULT_TEXT_OFFSET));
		mTextColor = ta.getColor(R.styleable.CustomProgressBarStyle_text_color, DEFAULT_TEXT_COLOR);
		mRadius = ta.getDimensionPixelSize(R.styleable.CustomProgressBarStyle_radius, dp2px(DEFAULT_RADIUS));
		mBeginAngle = ta.getFloat(R.styleable.CustomProgressBarStyle_begin_angle, DEFAULT_BEGIN_ANGLE) % 360;
		mBeginColor = ta.getColor(R.styleable.CustomProgressBarStyle_begin_color, 0);
		mEndColor = ta.getColor(R.styleable.CustomProgressBarStyle_end_color, 0);
		ta.recycle();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(mTextSize);

	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mStyle == 0) {
			measureHorizonalStyle(widthMeasureSpec, heightMeasureSpec);
		} else if (mStyle == 1) {
			measureCircleStyle(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 测量圆形的进度条

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
			width = getPaddingLeft() + getPaddingRight() + mRadius * 2 + Math.max(mReachHeight, mUnreachHeight);
			if (widthSpecMode == MeasureSpec.AT_MOST) {
				width = Math.min(width, widthSpecSize);
			}
		}
		//测量高
		if (heightSpecMode == MeasureSpec.EXACTLY) {
			height = heightSpecSize;
		} else {
			height = getPaddingBottom() + getPaddingTop() + mRadius * 2 + Math.max(mReachHeight, mUnreachHeight);
			if (heightSpecMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSpecSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	/**
	 测量横向的进度条

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
			height = (int) (getPaddingBottom() + getPaddingTop() + Math.max(Math.max(mReachHeight, mUnreachHeight), textHeight));
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

	/**
	 绘制圆形的进度条

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
		mPaint.setColor(mTextColor);
		mPaint.setStyle(Paint.Style.FILL);
		Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
		mBaseLineHeight = -(fontMetrics.top + fontMetrics.bottom) / 2;
		canvas.drawText(mRatioStr, -textWidth / 2, mBaseLineHeight, mPaint);

		//画未完成的底色
		mPaint.setColor(mUnreachColor);
		mPaint.setStrokeWidth(mUnreachHeight);
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(0, 0, mRadius, mPaint);

		//画已完成的进度条
		//圆弧的区域是中间圆圈的范围
		RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
		mPaint.setColor(mReachColor);
		mPaint.setStrokeWidth(mReachHeight);
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
		mPaint.setStrokeWidth(mReachHeight);
		if (mReachedWidth + mTextOffset * 2 + textWidth > progressTotalWidth) {
			//已经划到进度的最后了
			mReachedWidth = (int) (progressTotalWidth - mTextOffset * 2 - textWidth);
		}
		//设置了渐变色 ,就使用渐变色
		Log.i(TAG, "drawHorizonalStyle: mBeginColor = " + mBeginColor + " ; mEndColor = " + mEndColor);
		if (mBeginColor != 0 || mEndColor != 0) {
			Shader mShader = new LinearGradient(0, 0, mReachedWidth, 0, mBeginColor, getColor(mRatio), Shader.TileMode.MIRROR);
//			Shader mShader = new LinearGradient(0, 0, progressTotalWidth, 0, mBeginColor, mEndColor, Shader.TileMode.MIRROR);
			mPaint.setShader(mShader);
		} else {
			mPaint.setColor(mReachColor);
		}
		canvas.drawLine(0, 0, mReachedWidth, 0, mPaint);

		//画进度百分比文字
		if (mBeginColor > 0 || mEndColor > 0) { //设置了渐变色 ,就使用渐变色
			mPaint.setColor(getColor(mRatio));
		} else {
			mPaint.setColor(mTextColor);
		}
		Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
		mBaseLineHeight = -(fontMetrics.top + fontMetrics.bottom) / 2;
		canvas.drawText(mRatioStr, mReachedWidth + mTextOffset, mBaseLineHeight, mPaint);

		//画未完成的进度条
		mPaint.setColor(mUnreachColor);
		mPaint.setShader(null);
		mPaint.setStrokeWidth(mUnreachHeight);
		if (mReachedWidth + mTextOffset * 2 + textWidth < progressTotalWidth) {
			canvas.drawLine(mReachedWidth + mTextOffset * 2 + textWidth, 0, progressTotalWidth, 0, mPaint);
		}

		canvas.restore();
	}

	/**
	 * 设置渐变色的paint color或者添加shader
	 */
	public void setmPaintColor(int color) {
		if (mBeginColor > 0 || mEndColor > 0) { //设置了渐变色 ,就使用渐变色
		} else {
			mPaint.setColor(color);
		}
	}

	public void setStartColor(int startColor) {
		this.mBeginColor = startColor;
	}

	public void setEndColor(int endColor) {
		this.mEndColor = endColor;
	}

	public int getColor(float radio) {
		radio = (float) radio / 100;
		mBeginColor = mBeginColor == 0 ? DEFAULT_BEGIN_COLOR : mBeginColor;
		mEndColor = mEndColor == 0 ? DEFAULT_END_COLOR : mEndColor;
		int redStart = Color.red(mBeginColor);
		int blueStart = Color.blue(mBeginColor);
		int greenStart = Color.green(mBeginColor);
		int redEnd = Color.red(mEndColor);
		int blueEnd = Color.blue(mEndColor);
		int greenEnd = Color.green(mEndColor);

		int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
		int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
		int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
		return Color.argb(255, red, greed, blue);
	}

	private int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
	}

	private int sp2px(int spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
	}
}
