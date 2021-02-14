package alauncher.cn.measuringinstrument.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import alauncher.cn.measuringinstrument.utils.Arith;

import androidx.annotation.Nullable;

public class MValueView extends View {

    private Paint paint = new Paint();
    ;
    private RectF rect = new RectF(0, 0, 0, 0);

//    private Canvas canvas;

    private double baseValue;
    private double mValue;

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
        mValue = 1.5;
        // 公差的单位是毫米，所以需要 * 1000;
        upper_tolerance_value = upper;
        lower_tolerance_value = lower;

        if (pResolution == 6) {
            double _x = upper_tolerance_value - lower_tolerance_value;
            android.util.Log.d("wlDebug", "_x = " + _x);
            if (_x < 0.01) {
                pResolution = 0.1;
            } else if (_x < 0.02) {
                pResolution = 0.2;
            } else if (_x < 0.05) {
                pResolution = 0.5;
            } else if (_x < 0.1) {
                pResolution = 1;
            } else if (_x < 0.2) {
                pResolution = 2;
            } else {
                pResolution = 5;
            }
        }

        resolution = pResolution / 1000;
        hw = 0.9 * upper_tolerance_value + 0.1 * lower_tolerance_value;
        lw = 0.1 * upper_tolerance_value + 0.9 * lower_tolerance_value;

        baseValue = Arith.div(((nominal + upper_tolerance_value) + (nominal + lower_tolerance_value)), 2);
        android.util.Log.d("wlDebug", "nominal_value = " + nominal_value + " upper_tolerance_value = " + upper_tolerance_value + " lower_tolerance_value = " + lower_tolerance_value + " resolution = " + resolution);
        invalidate();
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

    // 变量;
    private double _upper_step;
    private float _HLLineHeight;
    private double _lower_step;
    private float _LLLineHeight;
    private float _LWLineHeight;
    private Path HLPath = new Path();
    private Path LLPath = new Path();
    private Path HWPath = new Path();
    private Path LWPath = new Path();
    private Path NPath = new Path();
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private double _value;

    // LinearGradient;
    LinearGradient redLinearGradient = new LinearGradient(0, 0, 200, 0, 0xF0FF0000, Color.RED, Shader.TileMode.MIRROR);
    LinearGradient yellowLinearGradient = new LinearGradient(0, 0, 200, 0, 0xF0FFFF00, Color.YELLOW, Shader.TileMode.MIRROR);
    LinearGradient greenLinearGradient = new LinearGradient(0, 0, 200, 0, 0xF000FF00, Color.GREEN, Shader.TileMode.MIRROR);

    float _top, _bottom;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // HL刻度线 , 上公差线;

        _upper_step = nominal_value + upper_tolerance_value - baseValue;
        _HLLineHeight = (float) ((3 * offectHeight) - ((_upper_step / resolution) * stepHeight));

        paint.setShader(null);
        paint.setColor(Color.RED);
        // canvas.drawLine(stepWidth + border, _HLLineHeight + border, stepWidth * 3 + border, _HLLineHeight + border, paint);
        // canvas.drawText("HL " + upper_tolerance_value, stepWidth * 3 + border + textMargin, _HLLineHeight + border + textMargin, paint);

        HLPath.reset();
        HLPath.moveTo(stepWidth * 3 + border + textMargin, _HLLineHeight + border);
        HLPath.lineTo(stepWidth * 3 + border + textMargin + 10, _HLLineHeight + border - 6);
        HLPath.lineTo(stepWidth * 3 + border + textMargin + 10, _HLLineHeight + border + 6);
        HLPath.close();
        canvas.drawPath(HLPath, paint);

        // LL刻度线 , 下公差线;
        _lower_step = nominal_value + lower_tolerance_value - baseValue;
        _LLLineHeight = (float) ((getMeasuredHeight() / 2) + Math.abs((_lower_step / resolution) * stepHeight));
        // canvas.drawLine(stepWidth + border, _LLLineHeight + border, stepWidth * 3 + border, _LLLineHeight + border, paint);
        // canvas.drawText("LL " + lower_tolerance_value, stepWidth * 3 + border + textMargin, _LLLineHeight + border + textMargin, paint);
        // 不画上刻度线，画三角形;

        LLPath.reset();
        LLPath.moveTo(stepWidth * 3 + border + textMargin, _LLLineHeight + border);
        LLPath.lineTo(stepWidth * 3 + border + textMargin + 10, _LLLineHeight + border - 6);
        LLPath.lineTo(stepWidth * 3 + border + textMargin + 10, _LLLineHeight + border + 6);
        LLPath.close();
        canvas.drawPath(LLPath, paint);

        hw = 0.9 * _upper_step + 0.1 * _lower_step;
        lw = 0.1 * _upper_step + 0.9 * _lower_step;
        // HW线 , 上报警;
        paint.setColor(Color.YELLOW);
        float _HWLineHeight = (float) ((getMeasuredHeight() / 2) - ((hw / resolution) * stepHeight));
        // canvas.drawLine(stepWidth + border, _HWLineHeight + border, stepWidth * 3 + border, _HWLineHeight + border, paint);
        // canvas.drawText("HW " + hw, stepWidth * 3 + border + textMargin, _HWLineHeight + border + textMargin, paint);

        HWPath.reset();
        HWPath.moveTo(stepWidth * 3 + border + textMargin, _HWLineHeight + border);
        HWPath.lineTo(stepWidth * 3 + border + textMargin + 10, _HWLineHeight + border - 6);
        HWPath.lineTo(stepWidth * 3 + border + textMargin + 10, _HWLineHeight + border + 6);
        HWPath.close();
        canvas.drawPath(HWPath, paint);

        _LWLineHeight = (float) ((getMeasuredHeight() / 2) + Math.abs((lw / resolution) * stepHeight));
        //canvas.drawLine(stepWidth + border, _LWLineHeight + border, stepWidth * 3 + border, _LWLineHeight + border, paint);
        //canvas.drawText("LW " + lw, stepWidth * 3 + border + textMargin, _LWLineHeight + border + textMargin, paint);

        LWPath.reset();
        LWPath.moveTo(stepWidth * 3 + border + textMargin, _LWLineHeight + border);
        LWPath.lineTo(stepWidth * 3 + border + textMargin + 10, _LWLineHeight + border - 6);
        LWPath.lineTo(stepWidth * 3 + border + textMargin + 10, _LWLineHeight + border + 6);
        LWPath.close();
        canvas.drawPath(LWPath, paint);

        // N 名义值
        paint.setColor(Color.GREEN);
        // canvas.drawText("N " + baseValue, stepWidth * 3 + border + textMargin, offectHeight * 3 + border + textMargin, paint);
        NPath.reset();
        NPath.moveTo(stepWidth * 3 + border + textMargin, offectHeight * 3 + border);
        NPath.lineTo(stepWidth * 3 + border + textMargin + 10, offectHeight * 3 + border - 6);
        NPath.lineTo(stepWidth * 3 + border + textMargin + 10, offectHeight * 3 + border + 6);
        NPath.close();
        canvas.drawPath(NPath, paint);

        // 绘制Base矩形；
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(border * 2);
        // rect = new RectF(stepWidth, 2, stepWidth * 3, getBottom() - 14);
        rect.set(stepWidth, 2, stepWidth * 3, getBottom() - 14);
        canvas.setDrawFilter(mPaintFlagsDrawFilter);
        // canvas.drawRoundRect(rect, 4, 4, paint); // 不画线了.

        _value = mValue - baseValue;
        if (_value > 0) {
            _top = (float) ((3 * offectHeight) - _value / resolution * stepHeight);
            if (_top < 0) {
                _top = 0;
            }
            _bottom = getMeasuredHeight() / 2;
            // rect = new RectF(stepWidth + border, _top, stepWidth * 3 - border, _bottom);
            rect.set(stepWidth + border, _top, stepWidth * 3 - border, _bottom);
        } else {
            _value = Math.abs(_value);
            _top = getMeasuredHeight() / 2;
            if (_top < 0) {
                _top = 0;
            }
            _bottom = (float) (_top + _value / resolution * stepHeight);
//            rect = new RectF(stepWidth + border, _top, stepWidth * 3 - border, _bottom);
            rect.set(stepWidth + border, _top, stepWidth * 3 - border, _bottom);
        }

        if (_value > 0) {
            if (_value > _upper_step) {
                paint.setShader(redLinearGradient);
            } else if (_value > hw) {
                paint.setShader(yellowLinearGradient);
            } else {
                paint.setShader(greenLinearGradient);
            }
        } else {
            if (_value < _lower_step) {
                paint.setShader(redLinearGradient);
            } else if (_value < lw) {
                paint.setShader(yellowLinearGradient);
            } else {
                paint.setShader(greenLinearGradient);
            }
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, 4, 4, paint);
    }
}
