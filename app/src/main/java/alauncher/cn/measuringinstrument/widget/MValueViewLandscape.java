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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import alauncher.cn.measuringinstrument.utils.Arith;

public class MValueViewLandscape extends View {

    private Paint paint = new Paint();
    ;
    private RectF rect = new RectF(0, 0, 0, 0);

    private double baseValue;

    private double mValue = 30.106;

    private float stepWidth;

    final private int border = 4;

    // 名义值;
    private double nominal_value = 31;
    // 上公差值;
    public double upper_tolerance_value = 0.04;
    // 下公差值;
    public double lower_tolerance_value = 0;
    // 分辨率;
    public double resolution = 0.1;
    // HW 上报警
    public double hw;
    // HL 下报警
    public double lw;
    // 高度的step;
    public float stepHeight;

    public float halfWidth;

    // 上下黄刻度线;
    private double upperTolerance, lowerTolerance;
    // 上下黄刻度线,横刻度值;
    private float upperToleranceLineWidth, lowerToleranceLineWidth;
    // 上下绿刻度线，横刻度值;
    private float upperGreenLineWidth, lowerGreenLineWidth;
    //
    float _top, _bottom, _right, _left;

    public MValueViewLandscape(Context context) {
        super(context);
    }

    public MValueViewLandscape(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        // init(30.1, 0.04, 0.0, 6);
    }

    public MValueViewLandscape(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(double nominal, double upper, double lower, double pResolution) {
        baseValue = nominal;
        nominal_value = nominal;
        // 公差的单位是毫米，所以需要 * 1000;
        upper_tolerance_value = upper;
        lower_tolerance_value = lower;

        if (pResolution == 6) {
            double _x = upper_tolerance_value - lower_tolerance_value;
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
        /*
        android.util.Log.d("wlDebug", "nominal_value = " + nominal_value + " upper_tolerance_value = " +
                upper_tolerance_value + " lower_tolerance_value = " + lower_tolerance_value + " resolution = " + resolution +
                "baseValue = " + baseValue);
         */
        invalidate();
    }

    public void setMValue(double pValue) {
        mValue = pValue;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        // 将整条测量柱分为100等份;
        stepWidth = getMeasuredWidth() / 100;
        // 将整个布局的高度分成4等份;
        stepHeight = getMeasuredHeight() / 4;

        halfWidth = getMeasuredWidth() / 2;

        // android.util.Log.d("wlDebug", "stepWidth = " + stepWidth + " stepHeight = " + stepHeight + " halfWidth = " + halfWidth);
    }

    private PaintFlagsDrawFilter mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private double _value;

    // LinearGradient;
    LinearGradient redLinearGradient = new LinearGradient(0, 0, 200, 0, 0xF0FF0000, Color.RED, Shader.TileMode.MIRROR);
    LinearGradient yellowLinearGradient = new LinearGradient(0, 0, 200, 0, 0xF0FFFF00, Color.YELLOW, Shader.TileMode.MIRROR);
    LinearGradient greenLinearGradient = new LinearGradient(0, 0, 200, 0, 0xF000FF00, Color.GREEN, Shader.TileMode.MIRROR);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 设置黄刻度线参数;
        paint.setShader(null);
        paint.setStrokeWidth(4.5f);
        paint.setColor(Color.YELLOW);
        // HL刻度线 , 上公差线;
        upperTolerance = nominal_value + upper_tolerance_value - baseValue;
        upperToleranceLineWidth = (float) (halfWidth + ((upperTolerance / resolution) * stepWidth));
        // LL刻度线 , 下公差线;
        lowerTolerance = nominal_value + lower_tolerance_value - baseValue;
        lowerToleranceLineWidth = (float) (halfWidth - Math.abs((lowerTolerance / resolution) * stepWidth));
        // 画线;
        canvas.drawLine(lowerToleranceLineWidth, border, lowerToleranceLineWidth, getMeasuredHeight() - border, paint);
        canvas.drawLine(upperToleranceLineWidth, border, upperToleranceLineWidth, getMeasuredHeight() - border, paint);

        // 设置绿刻度线参数;
        paint.setStrokeWidth(2.5f);
        paint.setColor(Color.GREEN);
        hw = 0.9 * upperTolerance + 0.1 * lowerTolerance;
        lw = 0.1 * upperTolerance + 0.9 * lowerTolerance;
        upperGreenLineWidth = (float) (halfWidth + ((hw / resolution) * stepWidth));
        lowerGreenLineWidth = (float) (halfWidth - Math.abs((lw / resolution) * stepWidth));
        // 画线;
        canvas.drawLine(upperGreenLineWidth, border, upperGreenLineWidth, getMeasuredHeight() - border, paint);
        canvas.drawLine(lowerGreenLineWidth, border, lowerGreenLineWidth, getMeasuredHeight() - border, paint);

        // 绘制Base矩形；
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(border * 2);

        _value = mValue - baseValue;
        if (_value > 0) {
            _right = (float) (halfWidth + _value / resolution * stepWidth);
            if (_right > getMeasuredWidth() - border) _right = getMeasuredWidth() - border;
            rect.set(halfWidth + border, border * 2, _right, getMeasuredHeight() - border * 2);
        } else {
            // _value = Math.abs(_value);
            _left = (float) (halfWidth + _value / resolution * stepWidth);
            if (_left < border) _left = border;
            rect.set(_left, border * 2, halfWidth, getMeasuredHeight() - border * 2);
        }
        /*
        android.util.Log.d("wlDebug", "upperTolerance = " + upperTolerance);
        android.util.Log.d("wlDebug", "lowerTolerance = " + lowerTolerance);
        android.util.Log.d("wlDebug", "hw = " + hw);
        android.util.Log.d("wlDebug", "lw = " + lw);
        android.util.Log.d("wlDebug", "_value = " + _value);
        */
        if (_value > 0) {
            if (_value > upperTolerance) {
                paint.setShader(redLinearGradient);
            } else if (_value > hw) {
                paint.setShader(yellowLinearGradient);
            } else {
                paint.setShader(greenLinearGradient);
            }
        } else {
            if (_value < lowerTolerance) {
                paint.setShader(redLinearGradient);
            } else if (_value < lw) {
                paint.setShader(yellowLinearGradient);
            } else {
                paint.setShader(greenLinearGradient);
            }
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, border, border, paint);

        paint.setColor(Color.WHITE);
        paint.setShader(null);
        paint.setTextSize(36);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.translate(getWidth() / 2, getHeight() / 2);
        // 文字宽
        float textWidth = paint.measureText(String.valueOf(mValue));
        float baseLineY = Math.abs(paint.ascent() + paint.descent()) / 2;
        canvas.drawText(String.valueOf(mValue), -textWidth / 2, baseLineY, paint);
    }
}
