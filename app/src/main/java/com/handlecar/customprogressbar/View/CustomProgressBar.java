package com.handlecar.customprogressbar.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
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
	/** 普通画笔,绘制同一种颜色 */
	private Paint mPaint;
	/** 渐变颜色画笔,,绘制进度条渐变 */
	private Paint mShaderPaint;
	/** 进度条样式, 0: 横向进度; 1:圆形进度 , 默认为0 */
	private int mStyle;
	/** 圆形进度条的半径 */
	private int mRadius;
	/** 圆形进度条开始的角度 */
	private float mBeginAngle;
	/** 已完成进度的颜色 */
	private int mReachColor;
	/** 未完成进度的颜色 */
	private int mUnreachColor;
	/** 已完成进度的高度 */
	private int mReachHeight;
	/** 未完成进度的高度 */
	private int mUnreachHeight;
	/** 字体大小 */
	private int mTextSize;
	/** 字体与进度条之间的空隙 */
	private int mTextOffset;
	/** 字体颜色 */
	private int mTextColor;
	/** 绘制字体时的基准线高度 */
	private float mBaseLineHeight;
	/** 进度条真实的宽度,只在横向进度条时使用 */
	private int mRealWidth;
	/** 进度条开始的颜色,只在圆形进度条使用 */
	private int mBeginColor;
	/** 进度条结束的颜色,只在圆形进度条使用 */
	private int mEndColor;
	/** 是否为渐变色风格 */
	private boolean isShade;

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
		setColors(); //设置是否为渐变色.并设置颜色
		initPaint();
		initPaint();
	}

	private void initPaint() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(mTextSize);
		mPaint.setStrokeCap(Paint.Cap.ROUND);//设置为圆角
		mShaderPaint = new Paint();
		mShaderPaint.setAntiAlias(true);
		mShaderPaint.setStyle(Paint.Style.STROKE);
		mShaderPaint.setStrokeCap(Paint.Cap.ROUND);
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

		//画未完成的底色
		mPaint.setColor(mUnreachColor);
		mPaint.setStrokeWidth(mUnreachHeight);
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(0, 0, mRadius, mPaint);

		//画已完成的进度条
		//保存canvas,旋转起始角度
		canvas.save();
		canvas.rotate(mBeginAngle);
		//设置渐变色或固定色
		if (isShade) {
			Shader mShader = new SweepGradient(0, 0, new int[]{mBeginColor, mEndColor}, null);
			mShaderPaint.setShader(mShader);
			mShaderPaint.setStrokeWidth(mReachHeight);
		} else {
			mPaint.setColor(mReachColor);
			mPaint.setStrokeWidth(mReachHeight);
		}
		//圆弧的区域是中间圆圈的范围
		RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
//		canvas.drawArc(rectF, mBeginAngle, mRatio * 360 / 100, false, mPaint);
		if (isShade) {
			canvas.drawArc(rectF, (float) Math.sqrt(mReachHeight), mRatio * 360 / 100 + (float) Math.sqrt(mReachHeight), false, mShaderPaint);
		} else {
			canvas.drawArc(rectF, 0, mRatio * 360 / 100 , false, mPaint);
		}
		canvas.restore();

		//画进度百分比文字
		if (isShade) {
			mPaint.setColor(getColor(mRatio)); //动态获取渐变色
		} else {
			mPaint.setColor(mTextColor); //没有渐变色,设置为固定色
		}
		mPaint.setStyle(Paint.Style.FILL);
		Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
		mBaseLineHeight = -(fontMetrics.top + fontMetrics.bottom) / 2;
		canvas.drawText(mRatioStr, -textWidth / 2, mBaseLineHeight, mPaint);
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
		if (isShade) {
			// 这种设置是动态设置当前的位置,再根据当前位置取获取渐变色的结束颜色.
			// Shader mShader = new LinearGradient(0, 0, mReachedWidth, 0, mBeginColor, getColor(mRatio), Shader.TileMode.MIRROR);
			//这种设置是直接设置起始点到终点的颜色渐变路径,然后直接画就行了.
			Shader mShader = new LinearGradient(0, 0, progressTotalWidth, 0, mBeginColor, mEndColor, Shader.TileMode.MIRROR);
			mPaint.setShader(mShader);
		}
		mPaint.setColor(mReachColor);
		canvas.drawLine(0, 0, mReachedWidth, 0, mPaint);

		//画进度百分比文字
		if (isShade) { //设置了渐变色 ,就使用渐变色
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
	 设置渐变色的起始和终止颜色,如果有一个没有设置,则使用默认颜色, 如果都没有设置,则表示不使用渐变色风格.
	 */
	public void setColors() {
		if (mBeginColor != 0 || mEndColor != 0) {
			isShade = true;
			mBeginColor = mBeginColor == 0 ? DEFAULT_BEGIN_COLOR : mBeginColor;
			mEndColor = mEndColor == 0 ? DEFAULT_END_COLOR : mEndColor;
		} else {
			isShade = false;
		}
	}

	/**
	 根据当前的进度, 动态获取渐变色上面的当前颜色

	 @param radio 当前进度, 数值百分比,在0-100之间.
	 @return 当前进度上的颜色
	 */
	public int getColor(float radio) {
		radio = (float) radio / 100;
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
