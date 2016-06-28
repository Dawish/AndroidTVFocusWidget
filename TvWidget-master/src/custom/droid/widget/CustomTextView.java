
package custom.droid.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CustomTextView extends View {

    private static final String TAG = "CustomTextView";

    private String mText;

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTextWithBaseline(canvas);
    }

    public synchronized void setText(String text) {
        mText = text;
        invalidate();
    }

    private void drawTextWithBaseline(Canvas canvas) {
        Paint paint = new Paint();
        // 抗锯齿
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.setDrawFilter(pfd);

        paint.setStrokeWidth(3);
        paint.setTextSize(80);
        FontMetricsInt fmi = paint.getFontMetricsInt();
        Log.d(TAG, "" + fmi.toString());
        Rect bounds1 = new Rect();
        paint.getTextBounds(mText.substring(0, 1), 0, 1, bounds1);
        Rect bounds2 = new Rect();
        paint.getTextBounds(mText.substring(0, 6), 0, 6, bounds2);
        // 随意设一个位置作为baseline
        int x = 50;
        int y = 400;
        // 把testString画在baseline上
        paint.setColor(Color.MAGENTA);
        canvas.drawText(mText, x, y, paint);

        // bounds1
        paint.setStyle(Style.STROKE); // 画空心矩形
        canvas.save();
        canvas.translate(x, y);
        paint.setColor(Color.GREEN);
        canvas.drawRect(bounds1, paint);
        canvas.restore();

        // bounds2
        canvas.save();
        paint.setColor(Color.MAGENTA);
        canvas.translate(x, y);
        canvas.drawRect(bounds2, paint);
        canvas.restore();

        // baseline
        paint.setColor(Color.RED);
        canvas.drawLine(x, y, 1024, y, paint);

        // ascent
        paint.setColor(Color.YELLOW);
        canvas.drawLine(x, y + fmi.ascent, 1024, y + fmi.ascent, paint);

        // descent
        paint.setColor(Color.BLUE);
        canvas.drawLine(x, y + fmi.descent, 1024, y + fmi.descent, paint);

        // top
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(x, y + fmi.top, 1024, y + fmi.top, paint);

        // bottom
        paint.setColor(Color.GREEN);
        canvas.drawLine(x, y + fmi.bottom, 1024, y + fmi.bottom, paint);

        Rect targetRect = new Rect(50, 50, 1000, 200);
        paint.setStrokeWidth(3);
        paint.setTextSize(80);
        paint.setColor(Color.CYAN);
        canvas.drawRect(targetRect, paint);
        paint.setColor(Color.RED);
        FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2
                - fontMetrics.top;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mText, targetRect.centerX(), baseline, paint);
        canvas.drawLine(50, baseline, 1000, baseline, paint);

        Rect rect = new Rect(50, 600, 1000, 800);
        paint.setStrokeWidth(3);
        paint.setTextSize(80);
        paint.setColor(Color.GRAY);
        canvas.drawRect(rect, paint);
        paint.setColor(Color.BLUE);

        Typeface type = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
        paint.setTypeface(type);

        FontMetricsInt metrics = paint.getFontMetricsInt();
        int center = rect.top + (rect.bottom - rect.top - metrics.bottom + metrics.top) / 2 - metrics.top;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mText, rect.centerX(), center, paint);
        canvas.drawLine(50, center, 1000, center, paint);
    }
}
