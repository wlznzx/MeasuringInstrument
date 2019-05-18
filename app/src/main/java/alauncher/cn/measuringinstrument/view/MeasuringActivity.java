package alauncher.cn.measuringinstrument.view;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.view.activity_view.MeasuringActivityView;
import alauncher.cn.measuringinstrument.widget.MValueView;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class MeasuringActivity extends BaseActivity implements MeasuringActivityView {

    @BindViews({R.id.m1_value, R.id.m2_value, R.id.m3_value, R.id.m4_value})
    public MValueView[] mMValueViews;

    @BindView(R.id.m_chart)
    public LineChart chart;

    @BindViews({R.id.m1_text_value, R.id.m2_text_value, R.id.m3_text_value, R.id.m4_text_value})
    public TextView mTValues[];

    @BindViews({R.id.m1_title, R.id.m2_title, R.id.m3_title, R.id.m4_title})
    public TextView mTitle[];

    protected Typeface tfRegular;
    protected Typeface tfLight;

    private double[] curMValues = {1.8, -2.8, 0.8, -0.4};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_measuring);
    }

    @Override
    protected void initView() {

        {   // // Chart Style // //

            // background color
            chart.setBackgroundColor(Color.WHITE);

            // disable description text
            chart.getDescription().setEnabled(false);

            // enable touch gestures
            chart.setTouchEnabled(true);

            // set listeners
            // chart.setOnChartValueSelectedListener(this);
            chart.setDrawGridBackground(false);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            // chart.setScaleXEnabled(true);
            // chart.setScaleYEnabled(true);

            // force pinch zoom along both axis
            chart.setPinchZoom(true);

            XAxis xAxis;
            {   // // X-Axis Style // //
                xAxis = chart.getXAxis();

                // vertical grid lines
                xAxis.enableGridDashedLine(10f, 10f, 0f);
            }

            YAxis yAxis;
            {   // // Y-Axis Style // //
                yAxis = chart.getAxisLeft();

                // disable dual axis (only use LEFT axis)
                chart.getAxisRight().setEnabled(false);

                // horizontal grid lines
                yAxis.enableGridDashedLine(10f, 10f, 0f);
                
                // axis range
                yAxis.setAxisMaximum(200f);
                yAxis.setAxisMinimum(-50f);
            }


            {   // // Create Limit Lines // //
                LimitLine llXAxis = new LimitLine(9f, "Index 10");
                llXAxis.setLineWidth(4f);
                llXAxis.enableDashedLine(10f, 10f, 0f);
                llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                llXAxis.setTextSize(10f);
                llXAxis.setTypeface(tfRegular);

                LimitLine ll1 = new LimitLine(150f, "Upper Limit");
                ll1.setLineWidth(2f);
                ll1.setLineColor(R.color.baseColor);
                ll1.enableDashedLine(10f, 10f, 0f);
                ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                ll1.setTextSize(10f);
                ll1.setTypeface(tfRegular);

                LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
                ll2.setLineWidth(2f);
                ll2.setLineColor(R.color.baseColor);
                ll2.enableDashedLine(10f, 10f, 0f);
                ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                ll2.setTextSize(10f);
                ll2.setTypeface(tfRegular);

                // draw limit lines behind data instead of on top
                yAxis.setDrawLimitLinesBehindData(true);
                xAxis.setDrawLimitLinesBehindData(true);

                // add limit lines
                yAxis.addLimitLine(ll1);
                yAxis.addLimitLine(ll2);
                //xAxis.addLimitLine(llXAxis);
            }
        }
        setDatas(10, 100);

        onMeasuringDataUpdate(curMValues);
    }

    private void setDatas(int count, double range) {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
        }

        LineDataSet set1;
        // create a dataset and give it a type
        set1 = new LineDataSet(values, getResources().getString(R.string.qualified_number));

        set1.setDrawIcons(false);

        // draw dashed line
        set1.enableDashedLine(10f, 5f, 0f);

        // black lines and points
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);

        // line thickness and point size
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);

        // draw points as solid circles
        set1.setDrawCircleHole(false);

        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        // text size of values
        set1.setValueTextSize(9f);

        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f);

        // set the filled area
        set1.setDrawFilled(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });

        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.BLACK);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);
        chart.setData(data);
    }

    @Override
    public void onMeasuringDataUpdate(double[] values) {
        curMValues = values;
        updateMValues(curMValues);
    }


    public void setMTitle(int mode) {
        switch (mode) {
            case 0:
                mTitle[0].setText(R.string.m1);
                mTitle[1].setText(R.string.m2);
                mTitle[2].setText(R.string.m3);
                mTitle[3].setText(R.string.m4);
                break;
            case 1:
                mTitle[0].setText("");
                mTitle[1].setText("");
                mTitle[2].setText("");
                mTitle[3].setText(R.string.m1);
                break;
            case 2:
                mTitle[3].setText(R.string.m2);
                break;
            case 3:
                mTitle[3].setText(R.string.m3);
                break;
            case 4:
                mTitle[3].setText(R.string.m4);
                break;
            default:
                break;
        }
    }

    private void setChartShow(boolean show) {
        if (show) {
            mMValueViews[0].setVisibility(View.GONE);
            mMValueViews[1].setVisibility(View.GONE);
            mMValueViews[2].setVisibility(View.GONE);
            chart.setVisibility(View.VISIBLE);
        } else {
            mMValueViews[0].setVisibility(View.VISIBLE);
            mMValueViews[1].setVisibility(View.VISIBLE);
            mMValueViews[2].setVisibility(View.VISIBLE);
            chart.setVisibility(View.GONE);
        }
    }

    private void setMode(int mode) {
        switch (mode) {
            case 0:
                setChartShow(false);
                break;
            case 1:
                setChartShow(true);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }
        setMTitle(mode);
        updateMValues(curMValues);
    }


    private int curMode = 0;

    @OnClick({R.id.swap_btn})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.swap_btn:
                if (curMode < 4) curMode++;
                else curMode = 0;
                setMode(curMode);
                break;
        }
    }

    private void updateMValues(double[] mValues) {
        for (int i = 0; i < mTValues.length; i++) {
            mTValues[i].setText("");
        }
        switch (curMode) {
            case 0:
                for (int i = 0; i < mTValues.length; i++) {
                    mTValues[i].setText("" + mValues[i]);
                    mMValueViews[i].setMValue(mValues[i]);
                }
                break;
            case 1:
                mTValues[3].setText("" + mValues[0]);
                mMValueViews[3].setMValue(mValues[0]);
                break;
            case 2:
                mTValues[3].setText("" + mValues[1]);
                mMValueViews[3].setMValue(mValues[1]);
                break;
            case 3:
                mTValues[3].setText("" + mValues[2]);
                mMValueViews[3].setMValue(mValues[2]);
                break;
            case 4:
                mTValues[3].setText("" + mValues[3]);
                mMValueViews[3].setMValue(mValues[3]);
                break;
        }
    }

}
