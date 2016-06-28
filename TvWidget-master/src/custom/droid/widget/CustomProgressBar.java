
package custom.droid.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import custom.droid.R;

public class CustomProgressBar extends View {

    private static final boolean DBUG = true;

    private static final String TAG = "CustomProgressBar";

    private int mProgress = 0;

    private int mMax;

    private Paint mPaint;

    private Matrix mMatrix;

    private Drawable mDrawable;

    private RectF mCircle;

    private int mGravity = Gravity.TOP | Gravity.CENTER;

    public static int POSITION_MIDDLE = 0;

    public static int POSITION_LEFT = 1;

    public static int POSITION_RIGHT = 2;

    private int mPosition = POSITION_MIDDLE;

    private boolean mBlurEnable = false;

    private int mTextSize;

    private ColorStateList mTextColor;

    private String mText;

    private Rect mRect;

    private int mRadius;

    private PaintFlagsDrawFilter mDrawFilter;

    BlurMaskFilter mMaskFilter;

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mCircle = new RectF();
        mMatrix = new Matrix();
        mRect = new Rect(810, 390, 1110, 690);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar);
        mGravity = a.getInt(R.styleable.CustomProgressBar_android_layout_gravity, mGravity);
        mPosition = a.getInt(R.styleable.CustomProgressBar_layout_position, mPosition);
        mBlurEnable = a.getBoolean(R.styleable.CustomProgressBar_blurenable, mBlurEnable);
        mTextSize = a.getDimensionPixelSize(R.styleable.CustomProgressBar_textsize, mTextSize);
        mTextColor = a.getColorStateList(R.styleable.CustomProgressBar_textcolor);
        Drawable drawable = a.getDrawable(R.styleable.CustomProgressBar_bitmap);
        if (drawable != null) {
            setDrawable(drawable);
        }
        mText = a.getString(R.styleable.CustomProgressBar_text);
        mRadius = a.getDimensionPixelSize(R.styleable.CustomProgressBar_radius, mRadius);
        if (mRadius <= 0) {
            mRadius = 150;
        }

        setMax(a.getInt(R.styleable.CustomProgressBar_max, mMax));
        setProgress(a.getInt(R.styleable.CustomProgressBar_progress, mProgress));
        if (DBUG) {
            Log.d(TAG, "process : " + mProgress + " max : " + mMax);
        }

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mMaskFilter = new BlurMaskFilter(10, Blur.SOLID);

        a.recycle();
    }

    public synchronized int getMax() {
        return mMax;
    }

    public synchronized void setMax(int max) {
        if (max < 0) {
            mMax = 0;
        }

        if (max != mMax) {
            if (mProgress > mMax) {
                mProgress = mMax;
            }
            this.mMax = max;
            postInvalidate();
        }
    }

    public synchronized int getProgress() {
        return mProgress;
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            mProgress = 0;
        }

        if (progress > mMax) {
            progress = mMax;
        }
        mProgress = progress;
        invalidate();
    }

    public synchronized void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public synchronized void setDrawable(int id) {
        if (id <= 0) {
            return;
        }
        setDrawable(getResources().getDrawable(id));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.reset();
        // 抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.setDrawFilter(mDrawFilter);
        // 空心样式
        mPaint.setStyle(Paint.Style.STROKE);
        // mPaint.setColor(Color.RED);
        // mPaint.setStrokeWidth(1);
        // // 圆形进度条内切该正方形
        // canvas.drawRect(mRect, mPaint);

        int color = Color.argb(255, 0, 236, 125);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(14.8f);
        mCircle.set(mRect);
        float angle = ((float) mProgress / mMax) * 360;
        if (DBUG) {
            Log.d(TAG, "mCircle : " + mCircle.toShortString());
            Log.d(TAG, "angle : " + angle + " mProgress : " + mProgress + " mMax : " + mMax);
        }
        if (mBlurEnable) {
            if (angle >= 0 && angle < 360) {
                // sdk version must under 14
                mPaint.setMaskFilter(mMaskFilter);
            }
        }

        // 画圆弧，第二个参数为：起始角度，第三个为跨的角度，第四个为true的时候是实心，false的时候为空心
        canvas.drawArc(mCircle, -90, angle, false, mPaint);

        // 计算字体的baseline，保证字体居中
        FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        // baseline
        int y = mRect.top + (mRect.bottom - mRect.top) / 2 - (fontMetrics.bottom - fontMetrics.top) / 2
                - fontMetrics.top;

        // draw percent
        mPaint.reset();
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(76);
        if (mProgress == mMax) {
            String text = "100%";
            canvas.drawText(text, mRect.centerX(), y, mPaint);

            mPaint.setStrokeWidth(15.1f);
            mPaint.setStyle(Paint.Style.STROKE);
            if (mBlurEnable) {
                // sdk version must under 14
                mPaint.setMaskFilter(mMaskFilter);
            }

            // draw white circle
            canvas.drawCircle(960, 540, mRadius, mPaint);
        } else {
            float tmp = (float) mProgress / (float) mMax;
            int percentInt = (int) (100 * tmp);

            String text = (percentInt + "%");
            canvas.drawText(text, mRect.centerX(), y, mPaint);
        }

        mPaint.reset();
        int textColor = mTextColor.getColorForState(getDrawableState(), 0);
        mPaint.setColor(textColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(3);
        // draw "clean process"
        mPaint.setTextSize(mTextSize == 0 ? 28 : mTextSize);
        // italic font
        Typeface type = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
        mPaint.setTypeface(type);
        // FIXME maybe load from string.xml
        canvas.drawText(mText == null ? "" : mText, mRect.centerX(), y * 11 / 10, mPaint);

        // bitmap must not null
        if (mDrawable == null) {
            return;
        }
        Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

        // draw bitmap following this
        mMatrix.reset();
        float cosValue = (float) Math.cos(Math.PI / (180 / angle));
        float sinValue = (float) Math.sin(Math.PI / (180 / angle));
        /**
         * 圆点坐标：(x0,y0) 半径：r 角度：a0 则圆上任一点为：（x1,y1） x1 = x0 + r * cos(ao * 3.14 /
         * 180) y1 = y0 + r * sin(ao * 3.14 / 180)
         **/
        float translateX = (float) (960 + (mRadius + mBitmap.getWidth() / 2) * Math.cos((angle - 90) * Math.PI / 180));
        float translateY = (float) (540 + (mRadius + mBitmap.getWidth() / 2) * Math.sin((angle - 90) * Math.PI / 180));
        float deltaX = mBitmap.getWidth() / 2;
        float deltaY = mBitmap.getHeight() / 2;
        // 绘图时x, y的偏移量，保证图片居中显示，默认是以图片的左上角绘图，所以出现图片不居中的现象
        translateX -= deltaX * Math.cos(angle * Math.PI / 180);
        translateY -= deltaY * Math.sin(angle * Math.PI / 180);
        if (DBUG) {
            // bitmap's w & h
            Log.d(TAG, "bitmap, w : " + mBitmap.getWidth() + " h : " + mBitmap.getHeight());
            Log.d(TAG, "deltaX : " + deltaX + " deltaY : " + deltaY + " translateX : " + translateX + " translateY : "
                    + translateY);
        }

        // 设置旋转矩阵值
        mMatrix.setValues(new float[] {
                cosValue, -sinValue, translateX, sinValue, cosValue, translateY, 0, 0, 1
        });

        if (angle >= 0 && angle < 360) {
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }
}
