package alauncher.cn.measuringinstrument.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MValueView extends View {

    private Paint paint;
    private RectF rect;

    private Canvas canvas;

    private double baseValue;
    private double mValue;
    private double offect;

    private float offectHeight;

    private float stepWidth;

    final private int border = 1;

    final private int textMargin = 4;

    // 名义值;
    private double nominal_value = 0;
    // 上公差值;
    public double upper_tolerance_value = 0.7;
    // 下公差值;
    public double lower_tolerance_value = 0.7;
    // 偏移值;
    public double offect_value = 0.7;
    // 分辨率;
    public double resolution = 0.1;

    // 高度的step;
    public double stepHeight;

    public MValueView(Context context) {
        super(context);
    }

    public MValueView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * 外部调用的接口
     */
    public void setRect(int left, int top, int right, int bottom) {
        invalidate();//更新调用onDraw重新绘制
    }


    private void init() {
        paint = new Paint();
        baseValue = 1.5;
        offect = 1;
        mValue = 1.5;
    }

    public void setMValue(double pValue) {
        mValue = pValue;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        offectHeight = getMeasuredHeight() / 6;
        stepWidth = getMeasuredWidth() / 4;
        // 将整条测量柱分为100等份;
        stepHeight = getMeasuredHeight() / 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // HL刻度线

        // 绘制刻度线
        paint.setColor(Color.RED);
        canvas.drawLine(stepWidth + border, offectHeight + border, stepWidth * 3 + border, offectHeight + border, paint);
        canvas.drawText("HL", stepWidth * 3 + border + textMargin, offectHeight + border + textMargin, paint);

        canvas.drawLine(stepWidth + border, offectHeight * 5 + border, stepWidth * 3 + border, offectHeight * 5 + border, paint);
        canvas.drawText("LL", stepWidth * 3 + border + textMargin, offectHeight * 5 + border + textMargin, paint);

        paint.setColor(Color.YELLOW);
        canvas.drawLine(stepWidth + border, offectHeight * 2 + border, stepWidth * 3 + border, offectHeight * 2 + border, paint);
        canvas.drawText("HW", stepWidth * 3 + border + textMargin, offectHeight * 2 + border + textMargin, paint);

        canvas.drawLine(stepWidth + border, offectHeight * 4 + border, stepWidth * 3 + border, offectHeight * 4 + border, paint);
        canvas.drawText("LW", stepWidth * 3 + border + textMargin, offectHeight * 4 + border + textMargin, paint);

        paint.setColor(Color.GREEN);
        // canvas.drawLine(stepWidth + border, offectHeight * 3 + border, stepWidth * 3 + border, offectHeight * 3 + border, paint);
        canvas.drawText("N", stepWidth * 3 + border + textMargin, offectHeight * 3 + border + textMargin, paint);

        // 绘制Base矩形；
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(border * 2);
        rect = new RectF(stepWidth, 2, stepWidth * 3, getBottom() - 14);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawRoundRect(rect, 4, 4, paint);

        double _value = mValue - baseValue;
        if (_value > 0) {
            float _top = (float) ((3 * offectHeight) - _value / offect * offectHeight);
            float _bottom = getMeasuredHeight() / 2;
            rect = new RectF(stepWidth + border, 3 + _top, stepWidth * 3 - border, _bottom);
        } else {
            _value = Math.abs(_value);
            float _top = getMeasuredHeight() / 2;
            float _bottom = (float) (_top + _value / offect * offectHeight);
            rect = new RectF(stepWidth + border, 3 + _top, stepWidth * 3 - border, _bottom);
        }
        _value = Math.abs(_value);
        if (_value > 2 * offect) {
            paint.setColor(Color.RED);
        } else if (_value > offect) {
            paint.setColor(Color.YELLOW);
        } else {
            paint.setColor(Color.GREEN);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, 4, 4, paint);
    }
}
