package alauncher.cn.measuringinstrument.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import alauncher.cn.measuringinstrument.utils.Arith;
import androidx.annotation.Nullable;

public class MValueView extends View {

    private Paint paint = new Paint();
    ;
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
    private double nominal_value = 31;
    // 上公差值;
    public double upper_tolerance_value = 7;
    // 下公差值;
    public double lower_tolerance_value = -7;
    // 偏移值;
    public double offect_value = 0.7;
    // 分辨率;
    public double resolution = 0.4;

    // HW 上报警
    public double hw;
    // HL 下报警
    public double lw;

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
    }

    /**
     * 外部调用的接口
     */
    public void setRect(int left, int top, int right, int bottom) {
        invalidate();//更新调用onDraw重新绘制
    }


    public void init(double nominal, double upper, double lower, double pResolution) {
        baseValue = nominal;
        nominal_value = nominal;
        offect = 1;
        mValue = 1.5;
        // 公差的单位是毫米，所以需要 * 1000;
        upper_tolerance_value = upper;
        lower_tolerance_value = lower;
        resolution = pResolution / 1000;
        hw = 0.9 * upper_tolerance_value + 0.1 * lower_tolerance_value;
        lw = 0.1 * upper_tolerance_value + 0.9 * lower_tolerance_value;

        baseValue = Arith.div(((nominal + upper_tolerance_value) + (nominal + lower_tolerance_value)), 2);

        android.util.Log.d("wlDebug", "nominal_value = " + nominal_value + " upper_tolerance_value = " + upper_tolerance_value + " lower_tolerance_value = " + lower_tolerance_value + " resolution = " + resolution);
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
        // android.util.Log.d("wlDebug", "stepHeight = " + stepHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // HL刻度线 , 上公差线;

        double _upper_step = nominal_value + upper_tolerance_value - baseValue;
        float _HLLineHeight = (float) ((3 * offectHeight) - ((_upper_step / resolution) * stepHeight));

        paint.setColor(Color.RED);
        canvas.drawLine(stepWidth + border, _HLLineHeight + border, stepWidth * 3 + border, _HLLineHeight + border, paint);
        canvas.drawText("HL " + upper_tolerance_value, stepWidth * 3 + border + textMargin, _HLLineHeight + border + textMargin, paint);

        // LL刻度线 , 下公差线;
        double _lower_step = nominal_value + lower_tolerance_value - baseValue;
        float _LLLineHeight = (float) ((getMeasuredHeight() / 2) + Math.abs((_lower_step / resolution) * stepHeight));
        canvas.drawLine(stepWidth + border, _LLLineHeight + border, stepWidth * 3 + border, _LLLineHeight + border, paint);
        canvas.drawText("LL " + lower_tolerance_value, stepWidth * 3 + border + textMargin, _LLLineHeight + border + textMargin, paint);

        hw = 0.9 * _upper_step + 0.1 * _lower_step;
        lw = 0.1 * _upper_step + 0.9 * _lower_step;
        // HW线 , 上报警;
        paint.setColor(Color.YELLOW);
        float _HWLineHeight = (float) ((getMeasuredHeight() / 2) - ((hw / resolution) * stepHeight));
        canvas.drawLine(stepWidth + border, _HWLineHeight + border, stepWidth * 3 + border, _HWLineHeight + border, paint);
        canvas.drawText("HW " + hw, stepWidth * 3 + border + textMargin, _HWLineHeight + border + textMargin, paint);

        float _LWLineHeight = (float) ((getMeasuredHeight() / 2) + Math.abs((lw / resolution) * stepHeight));
        canvas.drawLine(stepWidth + border, _LWLineHeight + border, stepWidth * 3 + border, _LWLineHeight + border, paint);
        canvas.drawText("LW " + lw, stepWidth * 3 + border + textMargin, _LWLineHeight + border + textMargin, paint);

        paint.setColor(Color.GREEN);
        // canvas.drawLine(stepWidth + border, offectHeight * 3 + border, stepWidth * 3 + border, offectHeight * 3 + border, paint);
        canvas.drawText("N " + baseValue, stepWidth * 3 + border + textMargin, offectHeight * 3 + border + textMargin, paint);

        // 绘制Base矩形；
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(border * 2);
        rect = new RectF(stepWidth, 2, stepWidth * 3, getBottom() - 14);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawRoundRect(rect, 4, 4, paint);

        double _value = mValue - baseValue;
        if (_value > 0) {
            float _top = (float) ((3 * offectHeight) - _value / resolution * stepHeight);
            float _bottom = getMeasuredHeight() / 2;
            rect = new RectF(stepWidth + border, 3 + _top, stepWidth * 3 - border, _bottom);
        } else {

            _value = Math.abs(_value);
            float _top = getMeasuredHeight() / 2;
            float _bottom = (float) (_top + _value / resolution * stepHeight);
            rect = new RectF(stepWidth + border, 3 + _top, stepWidth * 3 - border, _bottom);
        }

        if (_value > 0) {
            if (_value > _upper_step) {
                paint.setColor(Color.RED);
            } else if (_value > hw) {
                paint.setColor(Color.YELLOW);
            } else {
                paint.setColor(Color.GREEN);
            }
        } else {
            if (_value < _lower_step) {
                paint.setColor(Color.RED);
            } else if (_value < lw) {
                paint.setColor(Color.YELLOW);
            } else {
                paint.setColor(Color.GREEN);
            }
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, 4, 4, paint);
    }
}
