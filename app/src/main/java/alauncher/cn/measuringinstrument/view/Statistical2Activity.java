package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBean2Dao;
import alauncher.cn.measuringinstrument.utils.DateUtils;
import alauncher.cn.measuringinstrument.utils.ExcelUtil;
import alauncher.cn.measuringinstrument.utils.StringConverter;
import butterknife.BindView;
import butterknife.OnClick;


public class Statistical2Activity extends BaseOActivity {

    @BindView(R.id.statistical_chart)
    public LineChart chart;

    protected Typeface tfRegular;

    private LineDataSet set1;

    private final String DAILY = "DAILY";

    private final String WEEK = "WEEK";

    private final String MONTH = "MONTH";

    private String[] title = {"时间", "良品率"};

    private int total, succ, fail = 0;

    @BindView(R.id.total_production_tv)
    TextView totalProductionTV;

    @BindView(R.id.qualified_number_tv)
    TextView qualifiedNumberTV;

    @BindView(R.id.defective_tv)
    TextView defeectiveTV;

    @BindView(R.id.rate_tv)
    TextView rateTV;

    @BindView(R.id.statistical_chart_title)
    TextView statChartTV;


    private List<StatistialData> excelData = new ArrayList<StatistialData>();

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
        initChart();
        setDatas(10, 100);
        float[] result = {0, 0, 0, 0, 0, 0};
        updateChartDatas(result);
        // Log.d("statistcal", "" + "week = " + DateUtils.getDateD(DateUtils.getMondayOfThisWeek()));
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

    @OnClick({R.id.daily_btn, R.id.monthly_btn, R.id.year_btn, R.id.delete_delete_result, R.id.delete_statistical_btn})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.daily_btn:
                statChartTV.setText(getResources().getString(R.string.daily_table));
                new AnalyseTask().execute(DAILY);
                break;
            case R.id.monthly_btn:
                statChartTV.setText(getResources().getString(R.string.monthly_table));
                new AnalyseTask().execute(WEEK);
                break;
            case R.id.year_btn:
                statChartTV.setText(getResources().getString(R.string.year_table));
                new AnalyseTask().execute(MONTH);
                break;
            case R.id.delete_statistical_btn:
                totalProductionTV.setText("");
                qualifiedNumberTV.setText("");
                defeectiveTV.setText("");
                rateTV.setText("");
                statChartTV.setText("");
                float[] result = {0, 0, 0, 0, 0, 0};
                updateChartDatas(result);
                excelData.clear();
                break;
            case R.id.delete_delete_result:
                excelDatas();
                break;
        }
    }

    private List<ResultBean2> selectResultByDate(long start_time, long end_time) {
        String query = "SELECT * FROM " + ResultBean2Dao.TABLENAME + " where " + ResultBean2Dao.Properties.TimeStamp.columnName + " between " + start_time + " and " + end_time;
        Cursor cursor = App.getDaoSession().getResultBean2Dao().getDatabase().rawQuery(query, null);
        int HandlerAccount = cursor.getColumnIndex(ResultBean2Dao.Properties.HandlerAccount.columnName);
        int TimeStamp = cursor.getColumnIndex(ResultBean2Dao.Properties.TimeStamp.columnName);
        int WorkID = cursor.getColumnIndex(ResultBean2Dao.Properties.WorkID.columnName);
        int Event = cursor.getColumnIndex(ResultBean2Dao.Properties.Event.columnName);
        int Result = cursor.getColumnIndex(ResultBean2Dao.Properties.Result.columnName);
        int measurementValuesIndex = cursor.getColumnIndex(ResultBean2Dao.Properties.MeasurementValues.columnName);
        int measurementGroupIndex = cursor.getColumnIndex(ResultBean2Dao.Properties.MeasurementGroup.columnName);
        int mItemsIndex = cursor.getColumnIndex(ResultBean2Dao.Properties.MItems.columnName);
        int mDescribeIndex = cursor.getColumnIndex(ResultBean2Dao.Properties.MDescribe.columnName);

        List<ResultBean2> _datas = new ArrayList<>();

        while (cursor.moveToNext()) {
            ResultBean2 rBean = new ResultBean2();
            rBean.setHandlerAccount(cursor.getString(HandlerAccount));
            rBean.setWorkID(cursor.getString(WorkID));
            rBean.setTimeStamp(cursor.getLong(TimeStamp));
            rBean.setEvent(cursor.getString(Event));
            rBean.setResult(cursor.getString(Result));
            rBean.setMeasurementValues(StringConverter.convertToEntityPropertyG(cursor.getString(measurementValuesIndex)));
            rBean.setMeasurementGroup(StringConverter.convertToEntityPropertyG(cursor.getString(measurementGroupIndex)));
            rBean.setMItems(StringConverter.convertToEntityPropertyG(cursor.getString(mItemsIndex)));
            rBean.setMDescribe(StringConverter.convertToEntityPropertyG(cursor.getString(mDescribeIndex)));
            _datas.add(rBean);
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
                yAxis.setAxisMaximum(1.02f);
                yAxis.setAxisMinimum(0f);
                yAxis.setValueFormatter(new IndexAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (int) (value * 100) + "%";
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

    @OnClick({R.id.delete_statistical_btn, R.id.delete_delete_result})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_delete_result:
                // 需要导出Excel

                break;
            case R.id.delete_statistical_btn:
                break;
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
                return (int) (value * 100) + "%";
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
            dialog = new ProgressDialog(Statistical2Activity.this);
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
            fail = total - succ;
            totalProductionTV.setText(String.valueOf(total));
            qualifiedNumberTV.setText(String.valueOf(succ));
            defeectiveTV.setText(String.valueOf(fail));
            if (total > 0) {
                int rate = (int) ((succ / (float) total) * 100);
                rateTV.setText(rate + "%");
            } else {
                rateTV.setText("0%");
            }
            dialog.dismiss();
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            dialog.dismiss();
        }
    }

    private float[] dailyStatistics() {
        excelData.clear();
        total = 0;
        succ = 0;
        fail = 0;
        float[] percents = new float[7];
        int successTime;
        for (int i = 0; i > -7; i--) {
            successTime = 0;
            long start_time, end_time;
            if (i == 0) {
                // 获取本周一到当前的时间;
                start_time = DateUtils.getTimesMorning();
                end_time = System.currentTimeMillis();
            } else {
                start_time = DateUtils.getLastDateTimesMorning(i);
                end_time = DateUtils.getLastDateTimesMorning(i + 1);
            }
            Log.d("statistcal", "" + "start_time = " + DateUtils.getDateD(start_time) + " ----- end_time = " + DateUtils.getDateD(end_time));
            List<ResultBean2> _beans = selectResultByDate(start_time, end_time);
            for (ResultBean2 bean : _beans) {
                if (bean.getResult().equals(getResources().getStringArray(R.array.result)[1])) {
                    successTime++;
                }
            }
            if (_beans.size() > 0) {
                percents[i + 6] = successTime / (float) _beans.size();
            } else {
                percents[i + 6] = 0;
            }
            Log.d("statistcal", "" + "_beans.size() = " + _beans.size());
            total = total + _beans.size();
            succ = succ + successTime;
            excelData.add(new StatistialData(DateUtils.getDateD(start_time) + " - " + DateUtils.getDateD(end_time), percents[i + 6]));
        }
        for (float p : percents) {
            Log.d("statistcal", "" + "p = " + p);
        }
        return percents;
    }

    private float[] weekStatistics() {
        excelData.clear();
        total = 0;
        succ = 0;
        fail = 0;
        float[] percents = new float[8];
        int successTime;
        for (int i = 0; i > -8; i--) {
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
            List<ResultBean2> _beans = selectResultByDate(start_time, end_time);
            for (ResultBean2 bean : _beans) {
                if (bean.getResult().equals(getResources().getStringArray(R.array.result)[1])) {
                    successTime++;
                }
            }
            if (_beans.size() > 0) {
                percents[i + 7] = successTime / (float) _beans.size();
            } else {
                percents[i + 7] = 0;
            }
            total = total + _beans.size();
            succ = succ + successTime;
            excelData.add(new StatistialData(DateUtils.getDateD(start_time) + " - " + DateUtils.getDateD(end_time), percents[i + 7]));
        }
        for (float p : percents) {
            Log.d("statistcal", "" + "p = " + p);
        }
        return percents;
    }

    private float[] monthStatistics() {
        excelData.clear();
        total = 0;
        succ = 0;
        fail = 0;
        float[] percents = new float[12];
        int successTime;
        for (int i = 0; i > -12; i--) {
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
            List<ResultBean2> _beans = selectResultByDate(start_time, end_time);
            for (ResultBean2 bean : _beans) {
                if (bean.getResult().equals(getResources().getStringArray(R.array.result)[1])) {
                    successTime++;
                }
                Log.d("statistcal", "" + bean.toString());
            }
            if (_beans.size() > 0) {
                percents[i + 11] = successTime / (float) _beans.size();
            } else {
                percents[i + 11] = 0;
            }
            total = total + _beans.size();
            succ = succ + successTime;
            excelData.add(new StatistialData(DateUtils.getDateD(start_time) + " - " + DateUtils.getDateD(end_time), percents[i + 11]));
        }

        for (float p : percents) {
            Log.d("statistcal", "" + "p = " + p);
        }
        return percents;
    }

    private void excelDatas() {
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;

        msg.setText("是否导出统计列表.");

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExcelTask().execute();
                builder.dismiss();
            }
        });
    }

    /*
     *
     * 导出Excel Task.
     *
     * */
    public class ExcelTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;
        private String path = Environment.getExternalStorageDirectory() + "/ETGate/";

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Statistical2Activity.this);
            dialog.setTitle("导出Excel");
            dialog.setMessage("正在将数据导出Excel中 , 请稍等.");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        //第二个执行方法,在onPreExecute()后执行，用于后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            //处理耗时操作

            File destDir = new File(path);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            if (excelData.size() > 0) {
                path = path + "datas_" + DateUtils.getFileDate(System.currentTimeMillis()) + ".xls";
                ExcelUtil.initExcel(path, "data", title);
                ExcelUtil.writeStatisticalToExcel(excelData, path, Statistical2Activity.this);
                return path;
            } else {
                return null;
            }
        }

        /* 这个函数在doInBackground调用publishProgress(int i)时触发，虽然调用时只有一个参数
         但是这里取到的是一个数组,所以要用progesss[0]来取值
         第n个参数就用progress[n]来取值 */
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            //"loading..." + progresses[0] + "%"
        }

        /*doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
        这里的result就是上面doInBackground执行后的返回值，所以这里是"后台任务执行完毕"  */
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result == null) {
                Toast.makeText(Statistical2Activity.this, "请先选择分析图表", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Statistical2Activity.this, "导出至 : " + result, Toast.LENGTH_LONG).show();
            }
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }

    public class StatistialData {
        private String dataRange;
        private float percent;

        public StatistialData(String dataRange, float percent) {
            this.dataRange = dataRange;
            this.percent = percent;
        }

        public String getDataRange() {
            return dataRange;
        }

        public void setDataRange(String dataRange) {
            this.dataRange = dataRange;
        }

        public float getPercent() {
            return percent;
        }

        public void setPercent(float percent) {
            this.percent = percent;
        }
    }

}
