package alauncher.cn.measuringinstrument.view;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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


public class SPCStatisticalActivity extends BaseActivity {

    @BindView(R.id.statistical_chart)
    public LineChart chart;

    protected Typeface tfRegular;

    @BindView(R.id.code_sp)
    public Spinner codeSP;

    @BindView(R.id.target_sp)
    public Spinner targetSP;

    @BindView(R.id.group_size_sp)
    public Spinner groupSizeSP;

    @BindView(R.id.group_num_sp)
    public Spinner groupNumSP;

    @BindView(R.id.start_time_btn)
    public Button startTimeBtn;

    @BindView(R.id.stop_time_btn)
    public Button stopTimeBtn;

    @BindView(R.id.time_rg)
    public RadioGroup timeRG;

    @BindView(R.id.line_rg)
    public RadioGroup lineRG;

    public ResultBeanDao mResultBeanDao;

    private long startTimeStamp = 0;

    private long stopTimeStamp = 0;

    // 过程能力图.
    private final int GCNLT_MODE = 1;

    // 趋势质量图;
    private final int QSZLT_MODE = 2;

    private int spc_mode = GCNLT_MODE;


    private FilterBean mFilterBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_spc_statistical);
    }

    @OnClick({R.id.start_time_btn, R.id.stop_time_btn, R.id.spc_statistical_btn, R.id.delete_spc_result})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.start_time_btn:
                Calendar now = Calendar.getInstance();
                new android.app.DatePickerDialog(
                        SPCStatisticalActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, month);
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.MILLISECOND, 0);
                                startTimeStamp = cal.getTimeInMillis();
                                android.util.Log.d("wlDebug", "data = " + cal.getTimeInMillis());
                                startTimeBtn.setText(DateUtils.getDate(startTimeStamp));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
            case R.id.stop_time_btn:
                Calendar _now = Calendar.getInstance();
                new android.app.DatePickerDialog(
                        SPCStatisticalActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, month);
                                cal.set(Calendar.HOUR_OF_DAY, 23);
                                cal.set(Calendar.SECOND, 59);
                                cal.set(Calendar.MINUTE, 59);
                                stopTimeStamp = cal.getTimeInMillis();
                                android.util.Log.d("wlDebug", "data = " + cal.getTimeInMillis());
                                stopTimeBtn.setText(DateUtils.getDate(stopTimeStamp));
                            }
                        },
                        _now.get(Calendar.YEAR),
                        _now.get(Calendar.MONTH),
                        _now.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
            case R.id.spc_statistical_btn:
                startStatistical();
                break;
            case R.id.delete_spc_result:

                break;
        }
    }

    private void startStatistical() {
        mFilterBean = new FilterBean();
        mFilterBean.codeID = codeSP.getSelectedItemId() + 1;
        mFilterBean.targetNum = (int) targetSP.getSelectedItemId();
        mFilterBean.groupSize = Integer.valueOf((String) groupSizeSP.getSelectedItem());
        mFilterBean.groupNum = Integer.valueOf((String) groupNumSP.getSelectedItem());
        mFilterBean.isTimeAuto = timeRG.getCheckedRadioButtonId() == R.id.auto_time_rb ? true : false;
        mFilterBean.isLineAuto = lineRG.getCheckedRadioButtonId() == R.id.auto_line_rb ? true : false;

        android.util.Log.d("wlDebug", "mFilterBean = " + mFilterBean.toString());

        dataFilterUpdate();
    }

    /*
     *
     * 导出Excel Task.
     *
     * */
    public class SPCTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;
        private String path = Environment.getExternalStorageDirectory() + "/ETGate/";

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SPCStatisticalActivity.this);
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

            path = path + "datas_" + DateUtils.getFileDate(System.currentTimeMillis()) + ".xls";
            return "后台任务执行完毕";
        }

        /*这个函数在doInBackground调用publishProgress(int i)时触发，虽然调用时只有一个参数
         但是这里取到的是一个数组,所以要用progesss[0]来取值
         第n个参数就用progress[n]来取值   */
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        /*doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
        这里的result就是上面doInBackground执行后的返回值，所以这里是"后台任务执行完毕"  */
        @Override
        protected void onPostExecute(String result) {

        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }

    public void dataFilterUpdate() {

//        String query = "SELECT * FROM " + ResultBeanDao.TABLENAME + " where " + ResultBeanDao.Properties.TimeStamp.columnName + " between " + startTimeStamp + " and " + stopTimeStamp + " order by id desc limit 10";

        int _limit = mFilterBean.getGroupNum() * mFilterBean.getGroupSize();

        if (!mFilterBean.isTimeAuto() && startTimeStamp >= stopTimeStamp) {
            Toast.makeText(SPCStatisticalActivity.this, "起始时间不可大于结束时间，请确认输入.", Toast.LENGTH_SHORT).show();
            return;
        }

        String queryString = "";
        if (mFilterBean.isTimeAuto()) {
            queryString = "SELECT * FROM " + ResultBeanDao.TABLENAME + " where " + ResultBeanDao.Properties.CodeID.columnName + " = " + mFilterBean.getCodeID() + " order by _id desc limit " + _limit;
        } else {
            queryString = "SELECT * FROM " + ResultBeanDao.TABLENAME + " where " + ResultBeanDao.Properties.TimeStamp.columnName + " between " + startTimeStamp + " and " + stopTimeStamp + " order by _id desc limit " + _limit;
        }


        Cursor cursor = mResultBeanDao.getDatabase().rawQuery(queryString, null);
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
        }

        for (ResultBean _bean : _datas) {
            android.util.Log.d("wlDebug", _bean.toString());
        }

        if (_datas.size() < _limit) {
            Toast.makeText(SPCStatisticalActivity.this, "数据源数量不足以分析.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *  均值极差图;
     * */
    private void jzjctDrew(List<ResultBean> _datas) {
        double[] m = new double[_datas.size()];
        double total = 0;
        for (int i = 0; i < _datas.size(); i++) {
            switch (mFilterBean.getTargetNum()) {
                case 0:
                    m[i] = _datas.get(i).getM1();
                    break;
                case 1:
                    m[i] = _datas.get(i).getM2();
                    break;
                case 2:
                    m[i] = _datas.get(i).getM3();
                    break;
                case 3:
                    m[i] = _datas.get(i).getM4();
                    break;
            }
            total += m[i];
        }
        float xbar = (float) (total / _datas.size());

    }

    @Override
    protected void initView() {
        mResultBeanDao = App.getDaoSession().getResultBeanDao();
        ((RadioButton) lineRG.getChildAt(0)).setChecked(true);

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
        }
        // setDatas(10, 100);
    }

    private void setDatas(int count, float range) {
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


    class FilterBean {

        private long codeID;

        private int targetNum;

        private int groupSize;

        private int groupNum;

        private boolean isTimeAuto;

        private boolean isLineAuto;

        public boolean isLineAuto() {
            return isLineAuto;
        }

        public void setLineAuto(boolean lineAuto) {
            isLineAuto = lineAuto;
        }

        public boolean isTimeAuto() {

            return isTimeAuto;
        }

        public void setTimeAuto(boolean timeAuto) {
            isTimeAuto = timeAuto;
        }

        public long getCodeID() {
            return codeID;
        }

        public void setCodeID(long codeID) {
            this.codeID = codeID;
        }

        public int getTargetNum() {
            return targetNum;
        }

        public void setTargetNum(int targetNum) {
            this.targetNum = targetNum;
        }

        public int getGroupSize() {
            return groupSize;
        }

        public void setGroupSize(int groupSize) {
            this.groupSize = groupSize;
        }

        public int getGroupNum() {
            return groupNum;
        }

        public void setGroupNum(int groupNum) {
            this.groupNum = groupNum;
        }

        @Override
        public String toString() {
            return "FilterBean{" +
                    "codeID=" + codeID +
                    ", targetNum=" + targetNum +
                    ", groupSize=" + groupSize +
                    ", groupNum=" + groupNum +
                    ", isTimeAuto=" + isTimeAuto +
                    ", isLineAuto=" + isLineAuto +
                    '}';
        }
    }

}
