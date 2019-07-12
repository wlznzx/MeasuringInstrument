package alauncher.cn.measuringinstrument.view;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBeanDao;
import alauncher.cn.measuringinstrument.utils.DateUtils;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;


public class StatisticalActivity extends BaseActivity {

    @BindView(R.id.statistical_chart)
    public LineChart chart;

    protected Typeface tfRegular;
    protected Typeface tfLight;

    private ResultBeanDao mResultBeanDao;

    private LineDataSet set1;

    private final String DAILY = "DAILY";

    private final String WEEK = "WEEK";

    private final String MONTH = "MONTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_statistical);
    }

    @Override
    protected void initView() {
        mResultBeanDao = App.getDaoSession().getResultBeanDao();
        initChart();
        setDatas(10, 100);
        // Log.d("statistcal", "" + "week = " + DateUtils.getDateD(DateUtils.getMondayOfThisWeek()));

        Log.d("statistcal", "" + "week = " + DateUtils.getDateD(DateUtils.getTimeOfMonthStart()));

        monthStatistics();

    }

    /*
     *
     * 数据转换为Data;
     *
     */
    private List<Entry> getDatas(float[] src) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int i = 0; i < src.length; i++) {
            values.add(new Entry(i + 1, src[i], getResources().getDrawable(R.drawable.star)));
        }
        return values;
    }

    private void updateChartDatas(float[] data) {
        set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
        List<Entry> values = getDatas(data);
        set1.setValues(values);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    @OnClick({R.id.daily_btn, R.id.monthly_btn, R.id.year_btn})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.daily_btn:
                new AnalyseTask().execute(DAILY);
                break;
            case R.id.monthly_btn:
                new AnalyseTask().execute(WEEK);
                break;
            case R.id.year_btn:
                new AnalyseTask().execute(MONTH);
                break;
        }
    }

    private List<ResultBean> selectResultByDate(long start_time, long end_time) {
        String query = "SELECT * FROM " + ResultBeanDao.TABLENAME + " where " + ResultBeanDao.Properties.TimeStamp.columnName + " between " + start_time + " and " + end_time;
        Cursor cursor = mResultBeanDao.getDatabase().rawQuery(query, null);
        int HandlerAccout = cursor.getColumnIndex(ResultBeanDao.Properties.HandlerAccout.columnName);
        int TimeStamp = cursor.getColumnIndex(ResultBeanDao.Properties.TimeStamp.columnName);
        int Workid = cursor.getColumnIndex(ResultBeanDao.Properties.Workid.columnName);
        int Event = cursor.getColumnIndex(ResultBeanDao.Properties.Event.columnName);
        int Result = cursor.getColumnIndex(ResultBeanDao.Properties.Result.columnName);
        int M1 = cursor.getColumnIndex(ResultBeanDao.Properties.M1.columnName);
        int M2 = cursor.getColumnIndex(ResultBeanDao.Properties.M2.columnName);
        int M3 = cursor.getColumnIndex(ResultBeanDao.Properties.M3.columnName);
        int M4 = cursor.getColumnIndex(ResultBeanDao.Properties.M4.columnName);
        int M1_Group = cursor.getColumnIndex(ResultBeanDao.Properties.M1_group.columnName);
        int M2_Group = cursor.getColumnIndex(ResultBeanDao.Properties.M2_group.columnName);
        int M3_Group = cursor.getColumnIndex(ResultBeanDao.Properties.M3_group.columnName);
        int M4_Group = cursor.getColumnIndex(ResultBeanDao.Properties.M4_group.columnName);

        List<ResultBean> _datas = new ArrayList<>();
        while (cursor.moveToNext()) {
            ResultBean rBean = new ResultBean();
            rBean.setHandlerAccout(cursor.getString(HandlerAccout));
            rBean.setWorkid(cursor.getString(Workid));
            rBean.setTimeStamp(cursor.getLong(TimeStamp));
            rBean.setEvent(cursor.getString(Event));
            rBean.setResult(cursor.getString(Result));
            rBean.setM1(cursor.getDouble(M1));
            rBean.setM2(cursor.getDouble(M2));
            rBean.setM3(cursor.getDouble(M3));
            rBean.setM4(cursor.getDouble(M4));
            rBean.setM1_group(cursor.getString(M1_Group));
            rBean.setM2_group(cursor.getString(M2_Group));
            rBean.setM3_group(cursor.getString(M3_Group));
            rBean.setM4_group(cursor.getString(M4_Group));
            _datas.add(rBean);
            Log.d("statistcal", "" + "p = " + rBean.toString());
        }
        if (cursor != null) cursor.close();
        return _datas;
    }


    private void initChart() {
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
                yAxis.setAxisMaximum(1f);
                yAxis.setAxisMinimum(0f);
                yAxis.setValueFormatter(new IndexAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return value + "%";
                    }
                });
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
                // yAxis.addLimitLine(ll1);
                // yAxis.addLimitLine(ll2);
                //xAxis.addLimitLine(llXAxis);
            }
        }
    }

    private void setDatas(int count, float range) {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
        }
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
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "%";
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


    /*
     *
     * 导出Excel Task.
     *
     * */
    public class AnalyseTask extends AsyncTask<String, Integer, float[]> {

        private ProgressDialog dialog;

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(StatisticalActivity.this);
            dialog.setTitle("统计报表");
            dialog.setMessage("报表统计中 , 请稍等.");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        //第二个执行方法,在onPreExecute()后执行，用于后台任务,不可在此方法内修改UI
        @Override
        protected float[] doInBackground(String... params) {
            if (params[0].equals(DAILY)) {
                return dailyStatistics();
            } else if (params[0].equals(WEEK)) {
                return weekStatistics();
            } else if (params[0].equals(MONTH)) {
                return monthStatistics();
            }
            return dailyStatistics();
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            //"loading..." + progresses[0] + "%"
        }

        @Override
        protected void onPostExecute(float[] result) {
            updateChartDatas(result);
            dialog.dismiss();
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            dialog.dismiss();
        }
    }

    private float[] dailyStatistics() {
        float[] percents = new float[7];
        int successTime;
        for (int i = 0; i > -7; i--) {
            successTime = 0;
            long start_time,end_time;
            if (i == 0) {
                // 获取本周一到当前的时间;
                start_time = DateUtils.getTimesMorning();
                end_time = System.currentTimeMillis();
            } else {
                start_time = DateUtils.getLastDateTimesMorning(i);
                end_time = DateUtils.getLastDateTimesMorning(i + 1);
            }
            Log.d("statistcal", "" + "start_time = " + DateUtils.getDateD(start_time) + " ----- end_time = " + DateUtils.getDateD(end_time));
            List<ResultBean> _beans = selectResultByDate(start_time, end_time);
            for (ResultBean bean : _beans) {
                if (bean.getResult().equals(getResources().getStringArray(R.array.result)[1])) {
                    successTime++;
                }
            }
            if (_beans.size() > 0) {
                percents[i + 6] = successTime / (float) _beans.size();
            } else {
                percents[i + 6] = 0;
            }
        }
        for (float p : percents) {
            Log.d("statistcal", "" + "p = " + p);
        }
        return percents;
    }

    private float[] weekStatistics() {
        float[] percents = new float[7];
        int successTime;
        for (int i = 0; i > -7; i--) {
            successTime = 0;
            long start_time, end_time;
            if (i == 0) {
                // 获取本周一到当前的时间;
                start_time = DateUtils.getMondayOfThisWeek();
                end_time = System.currentTimeMillis();
            } else {
                start_time = DateUtils.getLastWeekTimesMorning(i);
                end_time = DateUtils.getLastWeekTimesMorning(i + 1);
            }
            Log.d("statistcal", "" + "start_time = " + DateUtils.getDateD(start_time) + " ----- end_time = " + DateUtils.getDateD(end_time));
            List<ResultBean> _beans = selectResultByDate(start_time, end_time);
            for (ResultBean bean : _beans) {
                if (bean.getResult().equals(getResources().getStringArray(R.array.result)[1])) {
                    successTime++;
                }
            }
            if (_beans.size() > 0) {
                percents[i + 6] = successTime / (float) _beans.size();
            } else {
                percents[i + 6] = 0;
            }
        }
        for (float p : percents) {
            Log.d("statistcal", "" + "p = " + p);
        }
        return percents;
    }

    private float[] monthStatistics() {
        float[] percents = new float[7];
        int successTime;
        for (int i = 0; i > -7; i--) {
            successTime = 0;
            long start_time, end_time;
            if (i == 0) {
                // 获取本月初一到当前的时间;
                start_time = DateUtils.getTimeOfMonthStart();
                end_time = System.currentTimeMillis();
            } else {
                start_time = DateUtils.getLastMonthTimesMorning(i);
                end_time = DateUtils.getLastMonthTimesMorning(i + 1);
            }
            Log.d("statistcal", "" + "start_time = " + DateUtils.getDateD(start_time) + " ----- end_time = " + DateUtils.getDateD(end_time));
            List<ResultBean> _beans = selectResultByDate(start_time, end_time);
            for (ResultBean bean : _beans) {
                if (bean.getResult().equals(getResources().getStringArray(R.array.result)[1])) {
                    successTime++;
                }
                Log.d("statistcal", "" + bean.toString());
            }
            if (_beans.size() > 0) {
                percents[i + 6] = successTime / (float) _beans.size();
            } else {
                percents[i + 6] = 0;
            }


        }

        for (float p : percents) {
            Log.d("statistcal", "" + "p = " + p);
        }
        return percents;
    }
}
