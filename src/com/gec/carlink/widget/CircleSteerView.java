package com.gec.carlink.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义圆形方向盘类
 * 
 * @author sig
 * @version 1.0
 */
public class CircleSteerView extends View {
	private Direction mDirection = Direction.DEFAULT;

	private int center = 0;
	private int innerRadius = 0;

	private float innerCircleRadius = 0;
	private float smallCircle = 10;
	// 圆环直径
	private float circleWidth = 100.0f;

	private int backgroundColor = Color.TRANSPARENT;
	private int innerCircleColor = Color.rgb(0, 205, 0);
	private int circleColor = Color.rgb(72, 118, 255);

	private Paint mPaint = new Paint();
	private OnDirectionChangedListener mListener = null;

	public CircleSteerView(Context context) {
		super(context);
	}

	public CircleSteerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleSteerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 方向改变接口
	 */
	public interface OnDirectionChangedListener {

		/**
		 * @param 方向(上、下、左、右、中央、默认)
		 */
		public void onDirectionChanged(Direction mDirection);
	}

	public void setOnDirectionChangedListener(OnDirectionChangedListener listener) {
		mListener = listener;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int measuredWidth = measureDimension(200, widthMeasureSpec);
		int measuredHeight = measureDimension(200, heightMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);

		center = getWidth() / 2;
		// 圆环
		innerRadius = (int) (center - circleWidth / 2 - 10);
		innerCircleRadius = center / 3;
		setOnTouchListener(mTouchListener);
	}

	private int measureDimension(int defaultSize, int measureSpec) {
		int result = 0;

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = defaultSize; // UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * 开始绘制
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		initBackGround(canvas);
		drawDirTriangle(canvas, mDirection);

	}

	/**
	 * 绘制方向小箭头
	 * 
	 * @param canvas
	 * @param mDirection
	 */
	private void drawDirTriangle(Canvas canvas, Direction mDirection) {
		mPaint.setColor(innerCircleColor);
		mPaint.setStrokeWidth(1.0f);
		mPaint.setStyle(Paint.Style.FILL);

		switch (mDirection) {
		case CENTER:
			invalidate();
			break;
		case LEFT_DIR:
			drawLeftTriangle(canvas);
			break;
		case UP_DIR:
			drawUpTriangle(canvas);
			break;
		case RIGHT_DIR:
			drawRightTriangle(canvas);
			break;
		case DOWN_DIR:
			drawDownTriangle(canvas);
			break;
		default:
			break;
		}

		mPaint.setColor(backgroundColor);
		canvas.drawCircle(center, center, smallCircle, mPaint);
	}

	/**
	 * 绘制向上的小箭头
	 * 
	 * @param canvas
	 *            画布
	 */
	private void drawUpTriangle(Canvas canvas) {
		Path path = new Path();
		path.moveTo(center, center);
		double sqrt = innerCircleRadius / Math.sqrt(2);
		double pow = innerCircleRadius * Math.sqrt(2);

		path.lineTo((float) (center - sqrt), (float) (center - sqrt));
		path.lineTo(center, (float) (center - pow));
		path.lineTo((float) (center + sqrt), (float) (center - sqrt));
		canvas.drawPath(path, mPaint);

		mPaint.setColor(backgroundColor);
		canvas.drawLine(center, center, center, center - innerCircleRadius, mPaint);

		drawOnClickColor(canvas, Direction.UP_DIR);
	}

	/**
	 * 绘制向下的小箭头
	 * 
	 * @param canvas
	 *            画布
	 */
	private void drawDownTriangle(Canvas canvas) {
		Path path = new Path();
		path.moveTo(center, center);
		double sqrt = innerCircleRadius / Math.sqrt(2);
		double pow = innerCircleRadius * Math.sqrt(2);
		path.lineTo((float) (center - sqrt), (float) (center + sqrt));
		path.lineTo(center, (float) (center + pow));
		path.lineTo((float) (center + sqrt), (float) (center + sqrt));
		canvas.drawPath(path, mPaint);

		mPaint.setColor(backgroundColor);
		canvas.drawLine(center, center, center, center + innerCircleRadius, mPaint);

		drawOnClickColor(canvas, Direction.DOWN_DIR);
	}

	/**
	 * 绘制向左的小箭头
	 * 
	 * @param canvas
	 *            画布
	 */
	private void drawLeftTriangle(Canvas canvas) {
		Path path = new Path();
		path.moveTo(center, center);
		double sqrt = innerCircleRadius / Math.sqrt(2);
		double pow = innerCircleRadius * Math.sqrt(2);
		path.lineTo((float) (center - sqrt), (float) (center - sqrt));
		path.lineTo((float) (center - pow), center);
		path.lineTo((float) (center - sqrt), (float) (center + sqrt));
		canvas.drawPath(path, mPaint);

		mPaint.setColor(backgroundColor);
		canvas.drawLine(center, center, center - innerCircleRadius, center, mPaint);

		drawOnClickColor(canvas, Direction.LEFT_DIR);

	}

	/**
	 * 绘制向右的小箭头
	 * 
	 * @param canvas
	 *            画布
	 */
	private void drawRightTriangle(Canvas canvas) {
		Path path = new Path();
		path.moveTo(center, center);
		double sqrt = innerCircleRadius / Math.sqrt(2);
		double pow = innerCircleRadius * Math.sqrt(2);
		path.lineTo((float) (center + sqrt), (float) (center - sqrt));
		path.lineTo((float) (center + pow), center);
		path.lineTo((float) (center + sqrt), (float) (center + sqrt));
		canvas.drawPath(path, mPaint);
		mPaint.setColor(backgroundColor);
		canvas.drawLine(center, center, center + innerCircleRadius, center, mPaint);

		drawOnClickColor(canvas, Direction.RIGHT_DIR);
	}

	/**
	 * 点击的时候绘制黑色的扇形
	 * 
	 * @param canvas
	 *            画布
	 * @param mDirection
	 *            方向
	 */
	private void drawOnClickColor(Canvas canvas, Direction mDirection) {
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(100.0f);

		switch (mDirection) {

		case LEFT_DIR:
			canvas.drawArc(
					new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center + innerRadius),
					135, 90, false, mPaint);
			break;

		case UP_DIR:
			canvas.drawArc(
					new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center + innerRadius),
					-135, 90, false, mPaint);
			break;

		case RIGHT_DIR:
			canvas.drawArc(
					new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center + innerRadius),
					-45, 90, false, mPaint);
			break;

		case DOWN_DIR:
			canvas.drawArc(
					new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center + innerRadius),
					45, 90, false, mPaint);
			break;

		default:
			break;
		}

		mPaint.setStyle(Paint.Style.FILL);
	}

	/**
	 * 绘制基本的背景， 这包括了三个步骤：1.清空画布 2.绘制外圈的圆 3.绘制内圈的圆
	 * 
	 * @param canvas
	 *            画布
	 */
	private void initBackGround(Canvas canvas) {
		clearCanvas(canvas);
		drawBackCircle(canvas);
		drawInnerCircle(canvas);

	}

	/**
	 * 绘制中心小圆
	 * 
	 * @param canvas
	 *            画布
	 */
	private void drawInnerCircle(Canvas canvas) {
		mPaint.setColor(innerCircleColor);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(1.0f);
		canvas.drawCircle(center, center, innerCircleRadius, mPaint);
	}

	/**
	 * 绘制背景的圆圈和隔线
	 * 
	 * @param canvas
	 *            画布
	 */
	private void drawBackCircle(Canvas canvas) {
		mPaint.setColor(circleColor);
		mPaint.setStrokeWidth(circleWidth);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		// 绘制圆圈
		canvas.drawCircle(center, center, innerRadius, mPaint);

		mPaint.setColor(backgroundColor);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(4.0f);
		canvas.drawLine(center, center, 0, 0, mPaint);
		canvas.drawLine(center, center, center * 2, 0, mPaint);
		canvas.drawLine(center, center, 0, center * 2, mPaint);
		canvas.drawLine(center, center, center * 2, center * 2, mPaint);
	}

	/**
	 * 清空画布
	 * 
	 * @param canvas
	 *            画布
	 */
	private void clearCanvas(Canvas canvas) {
		canvas.drawColor(backgroundColor);
	}

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			Direction dir = Direction.DEFAULT;
			if ((dir = checkDir(event.getX(), event.getY())) != Direction.DEFAULT) {
				mDirection = dir;
				invalidate();
			}
			return true;
		}

		/**
		 * 检测方向
		 * 
		 * @param x
		 *            x坐标
		 * @param y
		 *            y坐标
		 * @return 方向
		 */
		private Direction checkDir(float x, float y) {
			Direction mDirection = Direction.DEFAULT;

			// 判断在中心圆圈内
			if (Math.sqrt(Math.pow(y - center, 2) + Math.pow(x - center, 2)) < innerCircleRadius) {
				mDirection = Direction.CENTER;
				mListener.onDirectionChanged(mDirection);
			}

			else if (y < x && y + x < 2 * center) {
				mDirection = Direction.UP_DIR;
				mListener.onDirectionChanged(mDirection);
			}

			else if (y < x && y + x > 2 * center) {
				mDirection = Direction.RIGHT_DIR;
				mListener.onDirectionChanged(mDirection);
			}

			else if (y > x && y + x < 2 * center) {
				mDirection = Direction.LEFT_DIR;
				mListener.onDirectionChanged(mDirection);
			}

			else if (y > x && y + x > 2 * center) {
				mDirection = Direction.DOWN_DIR;
				mListener.onDirectionChanged(mDirection);
			}
			return mDirection;
		}
	};
}
