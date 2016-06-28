package org.bangbang.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * draw grid for utilizing ui design & debugging.
 * 
 * @author bysong
 *
 */
public class Grid extends View {

	private GridDrawer mGridDrawer;

	public Grid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mGridDrawer = new GridDrawer(50, 50, 10, 10);
	}

	public Grid(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Grid(Context context) {
		this(context, null);
	}
	
	@SuppressLint("WrongCall")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mGridDrawer.onDraw(canvas, getWidth(), getHeight());
	}

	

}
/**
 * utility to aid drawing grid.
 * 
 * @author bysong
 * @see #onDraw(Canvas, int, int)
 */
class GridDrawer {
	
	private static final int X_GAP = 100;
	private static final int Y_GAP = 100;
	private static final boolean DRAW_UNIT = true;
	
	private Paint mPaint = new Paint();
	private Paint mMinorPaint = new Paint();
	private Paint mTextPaint = new TextPaint();
	
	private int mXgap;
	private int mYGap;
	private int mXMinorGap;
	private int mYMinorGap;
	
	public GridDrawer() {
		this(X_GAP, Y_GAP);
	}
	
	public GridDrawer(int xGap, int yGap) {
		this(xGap, yGap, 0, 0);
	}
	
	public GridDrawer(int xGap, int yGap, int xMinorGap, int yMinorGap) {
		mXgap= xGap;
		mYGap = yGap;
		mXMinorGap = xMinorGap;
		mYMinorGap = yMinorGap;
		
		mTextPaint.setTextSize(10);
		mTextPaint.setColor(Color.RED);
		
		mPaint.setStrokeWidth(2);
		mMinorPaint.setStrokeWidth(1);
	}

	public void onDraw(Canvas canvas, int DrawingWidth, int drawingHeight){
		for (int i = 0 ; i < DrawingWidth ; i += mXgap){
			canvas.drawLine(i, 0, i, drawingHeight, mPaint);
			if (DRAW_UNIT) {
				canvas.drawText(i + "px", i + 1, 10, mTextPaint);
			}
		}
		
		for (int j = 0 ; j < drawingHeight ; j += mYGap) {
			canvas.drawLine(0, j, DrawingWidth, j, mPaint);
			if (DRAW_UNIT) {
				canvas.drawText(j + "px", 0, j + 1, mTextPaint);
			}
		}
		
		// minor grid
		if (0 != mXMinorGap) {
			for (int i = 0 ; i < DrawingWidth ; i += mXMinorGap){
				canvas.drawLine(i, 0, i, drawingHeight, mMinorPaint);
			}
		}
		if (0 != mYMinorGap) {
			for (int j = 0 ; j < drawingHeight ; j += mYMinorGap) {
				canvas.drawLine(0, j, DrawingWidth, j, mMinorPaint);
			}
		}
	}
}
