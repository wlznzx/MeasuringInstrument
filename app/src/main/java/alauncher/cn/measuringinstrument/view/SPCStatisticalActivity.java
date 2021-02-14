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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBeanDao;
import alauncher.cn.measuringinstrument.utils.CommonUtil;
import alauncher.cn.measuringinstrument.utils.Constants;
import alauncher.cn.measuringinstrument.utils.DateUtils;

import alauncher.cn.measuringinstrument.utils.Format;

import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.OnClick;

import static alauncher.cn.measuringinstrument.App.getDaoSession;


public class SPCStatisticalActivity extends BaseOActivity {

    @BindView(R.id.statistical_chart)
    public LineChart chart;

    @BindView(R.id.r_chart)
    public LineChart rChart;

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

    @BindView(R.id.xucl_edt)
    public EditText xuclEdt;

    @BindView(R.id.xlcl_edt)
    public EditText xlclEdt;

    @BindView(R.id.rucl_edt)
    public EditText ruclEdt;

    @BindView(R.id.rlcl_edt)
    public EditText rlclEdt;

    @BindView(R.id.jzjct_btn)
    public View jzjctBtn;

    @BindView(R.id.ybyxt_btn)
    public View ybyxtBtn;

    @BindView(R.id.gcnlt_btn)
    public View gcnltBtn;

    @BindView(R.id.TableLayout)
    public View mTableLayout;

    @BindView(R.id.average_value_tv)
    public TextView averageValueTV;

    @BindView(R.id.max_value_tv)
    public TextView maxValueTV;

    @BindView(R.id.min_value_tv)
    public TextView minValueTV;

    @BindView(R.id.nominal_value_tv)
    public TextView nominalValueTV;

    @BindView(R.id.spc_usl_tv)
    public TextView gcUslTV;

    @BindView(R.id.spc_lsl_tv)
    public TextView gcLslTV;

    @BindView(R.id._3a_tv)
    public TextView _3aTV;

    @BindView(R.id.a_tv)
    public TextView _aTV;

    @BindView(R.id.cp_tv)
    public TextView cpTV;

    @BindView(R.id.cpk_tv)
    public TextView cpkTV;

    @BindView(R.id.cpl_tv)
    public TextView cplTV;

    @BindView(R.id.cpu_tv)
    public TextView cpuTV;

    @BindView(R.id.pp_tv)
    public TextView ppTV;

    @BindView(R.id.ppk_tv)
    public TextView ppkTV;

    @BindView(R.id.ppl_tv)
    public TextView pplTV;

    @BindView(R.id.ppu_tv)
    public TextView ppuTV;

    public ResultBeanDao mResultBeanDao;

    private long startTimeStamp = 0;

    private long stopTimeStamp = 0;

    // 均值极差图.
    private final int JZJCT_MODE = 1;

    // 样本运行图;
    private final int YBYXT_MODE = 2;

    // 过程能力图;
    private final int GCNLT_MODE = 3;

    private int spc_mode = JZJCT_MODE;

    // private LineDataSet set1;

    private FilterBean mFilterBean;

    ParameterBean mParameterBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SetupBean _bean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
        _bean.setXUpperLine(Double.valueOf(xuclEdt.getText().toString().trim()));
        _bean.setXLowerLine(Double.valueOf(xlclEdt.getText().toString().trim()));
        _bean.setRUpperLine(Double.valueOf(ruclEdt.getText().toString().trim()));
        _bean.setRLowerLine(Double.valueOf(rlclEdt.getText().toString().trim()));
        App.getDaoSession().getSetupBeanDao().insertOrReplace(_bean);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_spc_statistical);
    }

    @OnClick({R.id.start_time_btn, R.id.stop_time_btn, R.id.spc_statistical_btn, R.id.delete_spc_result, R.id.jzjct_btn})
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
//                startStatistical();
                new SPCTask().execute();
                break;
            case R.id.delete_spc_result:
                clearChart();
                break;

        }
    }

    @OnClick({R.id.jzjct_btn, R.id.ybyxt_btn, R.id.gcnlt_btn})
    public void onSPCClick(View v) {
        jzjctBtn.setBackgroundResource(R.drawable.btn_selector);
        ybyxtBtn.setBackgroundResource(R.drawable.btn_selector);
        gcnltBtn.setBackgroundResource(R.drawable.btn_selector);
        switch (v.getId()) {
            case R.id.jzjct_btn:
                if (spc_mode != JZJCT_MODE) {
                    clearChart();
                }
                spc_mode = JZJCT_MODE;
                chart.setVisibility(View.VISIBLE);
                rChart.setVisibility(View.VISIBLE);
                mTableLayout.setVisibility(View.GONE);
                break;
            case R.id.ybyxt_btn:
                if (spc_mode != YBYXT_MODE) {
                    clearChart();
                }
                spc_mode = YBYXT_MODE;
                chart.setVisibility(View.VISIBLE);
                rChart.setVisibility(View.INVISIBLE);
                mTableLayout.setVisibility(View.GONE);
                break;
            case R.id.gcnlt_btn:
                chart.setVisibility(View.GONE);
                rChart.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.VISIBLE);
                if (spc_mode != GCNLT_MODE) {
                    clearChart();
                }
                spc_mode = GCNLT_MODE;
                break;
        }
        v.setBackgroundResource(R.drawable.bg_tv_seleted);
    }

    private void startStatistical() {
        mFilterBean = new FilterBean();
        mFilterBean.codeID = codeSP.getSelectedItemId() + 1;
        mFilterBean.targetNum = (int) targetSP.getSelectedItemId();
        mFilterBean.groupSize = Integer.valueOf((String) groupSizeSP.getSelectedItem());
        mFilterBean.groupNum = Integer.valueOf((String) groupNumSP.getSelectedItem());
        mFilterBean.isTimeAuto = timeRG.getCheckedRadioButtonId() == R.id.auto_time_rb ? true : false;
        mFilterBean.isLineAuto = lineRG.getCheckedRadioButtonId() == R.id.auto_line_rb ? true : false;

        mFilterBean.setXucl(Double.valueOf(xuclEdt.getText().toString().trim()));
        mFilterBean.setXlcl(Double.valueOf(xlclEdt.getText().toString().trim()));
        mFilterBean.setRucl(Double.valueOf(ruclEdt.getText().toString().trim()));
        mFilterBean.setRlcl(Double.valueOf(rlclEdt.getText().toString().trim()));
        android.util.Log.d("wlDebug", "mFilterBean = " + mFilterBean.toString());
        dataFilterUpdate();
    }


    /*
     *
     * 导出Excel Task.
     *
     * */
    public class SPCTask extends AsyncTask<String, Integer, Object> {

        private ProgressDialog dialog;

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SPCStatisticalActivity.this);
            dialog.setTitle("SPC");
            dialog.setMessage("SPC分析中...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        //第二个执行方法,在onPreExecute()后执行，用于后台任务,不可在此方法内修改UI
        @Override
        protected Object doInBackground(String... params) {
            //处理耗时操作
            startStatistical();
            List<ResultBean> _datas = dataFilterUpdate();
            if (_datas == null) {
                return null;
            }
            if (spc_mode == JZJCT_MODE) {
                return jzjctDatas(_datas);
            } else if (spc_mode == YBYXT_MODE) {
                return ybyxtDatas(_datas);
            } else if (spc_mode == GCNLT_MODE) {
                return gcnltDatas(_datas);
            }
            return null;
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
        protected void onPostExecute(Object result) {
            dialog.dismiss();
            if (result != null) {
                if (spc_mode == JZJCT_MODE) {
                    JZJCTBean _bean = (JZJCTBean) result;
                    YAxis yAxis = chart.getAxisLeft();
                    yAxis.setAxisMaximum(_bean.maxXY);
                    yAxis.setAxisMinimum(_bean.minXY);
                    yAxis.removeAllLimitLines();
                    yAxis.addLimitLine(getLimitLine(_bean.xUCL, "上控制线"));
                    yAxis.addLimitLine(getLimitLine(_bean.xLCL, "下控制线"));
                    android.util.Log.d("wllDebug", "_bean.xUCL = " + _bean.xUCL);
                    android.util.Log.d("wllDebug", "_bean.xLCL = " + _bean.xLCL);
                    yAxis.addLimitLine(getLimitLine(_bean.upperValue, "上公差线"));
                    yAxis.addLimitLine(getLimitLine(_bean.lowerValue, "下公差线"));
                    android.util.Log.d("wllDebug", "_bean.upperValue = " + _bean.upperValue);
                    android.util.Log.d("wllDebug", "_bean.lowerValue = " + _bean.lowerValue);
                    updateChartDatas(_bean.xValues);
                    // 绘制R图;
                    YAxis rYAxis = rChart.getAxisLeft();
                    rYAxis.setAxisMaximum(_bean.maxRY);
                    rYAxis.setAxisMinimum(_bean.minRY);
                    rYAxis.removeAllLimitLines();
                    rYAxis.addLimitLine(getLimitLine(_bean.rUCL, "上控制线"));
                    rYAxis.addLimitLine(getLimitLine(_bean.rLCL, "下控制线"));
                    /**/
                    if (rChart.getData() == null) {
                        ArrayList<ILineDataSet> rDataSets = new ArrayList<>();
                        rDataSets.add(getLineDataSet(getDemoValues(), "R")); // add the data sets
                        // create a data object with the data sets
                        LineData rData = new LineData(rDataSets);
                        rChart.setData(rData);
                    }
                    LineDataSet _set1 = (LineDataSet) rChart.getData().getDataSetByIndex(0);
                    _set1.setValues(_bean.rValues);
                    rChart.getData().notifyDataChanged();
                    rChart.notifyDataSetChanged();
                    rChart.invalidate();
                } else if (spc_mode == YBYXT_MODE) {
                    YBYXTBean _bean = (YBYXTBean) result;
                    YAxis yAxis = chart.getAxisLeft();
                    yAxis.setAxisMaximum(_bean.maxY);
                    yAxis.setAxisMinimum(_bean.minY);
                    yAxis.removeAllLimitLines();
                    yAxis.addLimitLine(getLimitLine(_bean.ucl, "上公差线"));
                    yAxis.addLimitLine(getLimitLine(_bean.lcl, "下公差线"));
                    updateChartDatas(_bean.mValues);
                } else if (spc_mode == GCNLT_MODE) {
                    GCNLTBean _bean = (GCNLTBean) result;
                    android.util.Log.d("wlDebug", "bean = " + _bean.toString());
                    averageValueTV.setText("" + Format.m1(_bean.averageValue, 4));
                    maxValueTV.setText("" + Format.m1(_bean.maxValue, 4));
                    minValueTV.setText("" + Format.m1(_bean.minValue, 4));
                    nominalValueTV.setText("" + Format.m1(_bean.nominalValue, 4));
                    gcUslTV.setText("" + Format.m1(_bean.usl, 4));
                    gcLslTV.setText("" + Format.m1(_bean.lsl, 4));
                    _3aTV.setText("" + Format.m1((_bean.averageValue + _bean.a * -3), 4));
                    _aTV.setText("" + Format.m1((_bean.averageValue + _bean.a * 3), 4));
                    cpTV.setText("" + Format.m1(_bean.cp, 2));
                    cpkTV.setText("" + Format.m1(_bean.cpk, 2));
                    cplTV.setText("" + Format.m1(_bean.cpl, 2));
                    cpuTV.setText("" + Format.m1(_bean.cpu, 2));
                    ppTV.setText("" + Format.m1(_bean.pp, 2));
                    ppkTV.setText("" + Format.m1(_bean.ppk, 2));
                    pplTV.setText("" + Format.m1(_bean.ppl, 2));
                    ppuTV.setText("" + Format.m1(_bean.ppu, 2));
                }
            } else {
                Toast.makeText(SPCStatisticalActivity.this, "数据源数量不足以分析.", Toast.LENGTH_SHORT).show();
            }
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    public List<ResultBean> dataFilterUpdate() {

        int _limit = mFilterBean.getGroupNum() * mFilterBean.getGroupSize();

        if (!mFilterBean.isTimeAuto() && startTimeStamp >= stopTimeStamp) {
            return null;
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
            // Toast.makeText(SPCStatisticalActivity.this, "数据源数量不足以分析.", Toast.LENGTH_SHORT).show();
            return null;
        }

        return _datas;
    }

    /*
     *  均值极差图;
     * */
    private JZJCTBean jzjctDatas(List<ResultBean> _datas) {
        JZJCTBean _bean = new JZJCTBean();
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
        // 绘制XBar;
        float xbar = (float) (total / _datas.size());
        _bean.xbar = xbar;
        // 绘制折线图；
        ArrayList<Entry> values = new ArrayList<Entry>();
        ArrayList<Double> _mList = new ArrayList<>();
        ArrayList<Entry> rValues = new ArrayList<>();
        float rbar = 0;
        for (int i = 0; i < mFilterBean.getGroupNum(); i++) {
            double _groupM = 0;
            float _r = 0;
            _mList.clear();
            for (int j = 0; j < mFilterBean.getGroupSize(); j++) {
                int index = i * mFilterBean.getGroupSize() + j;
                _groupM += m[index];
                _mList.add(m[index]);
            }
            Collections.sort(_mList);
            _r = (float) Math.abs(_mList.get(0) - _mList.get(_mList.size() - 1));
            rbar = rbar + _r;
            _groupM = _groupM / mFilterBean.getGroupSize();
            float _roupM = (float) _groupM;
            android.util.Log.d("wlDebug", "__groupM = " + _roupM);
            values.add(new Entry(i + 1, _roupM, getResources().getDrawable(R.drawable.star)));
            rValues.add(new Entry(i + 1, _r, getResources().getDrawable(R.drawable.star)));
        }
        _bean.xValues = values;
        _bean.rValues = rValues;
        // 计算Rbar
        rbar = rbar / mFilterBean.getGroupNum();
        _bean.rbar = rbar;

        float xucl, xlcl, rucl, rlcl, maxX, minX, maxR, minR;
        if (mFilterBean.isLineAuto()) {
            xucl = (float) (xbar + Constants.A2[mFilterBean.groupSize - 2] * rbar);
            xlcl = (float) (xbar - Constants.A2[mFilterBean.groupSize - 2] * rbar);
            rucl = (float) (Constants.D4[mFilterBean.groupSize - 2] * rbar);
            rlcl = (float) (Constants.D3[mFilterBean.groupSize - 2] * rbar);
        } else {
            xucl = (float) mFilterBean.getXucl();
            xlcl = (float) mFilterBean.getXlcl();
            rucl = (float) mFilterBean.getRucl();
            rlcl = (float) mFilterBean.getRlcl();
        }

        maxX = (float) (xucl + Math.abs(xucl - xbar) * 0.2);
        minX = (float) (xlcl - Math.abs(xbar - xlcl) * 0.2);

        maxR = (float) (rucl + Math.abs(rucl - rbar) * 0.2);
        minR = (float) (rlcl - Math.abs(rlcl - rbar) * 0.2);

        _bean.xUCL = xucl;
        _bean.xLCL = xlcl;
        _bean.rUCL = rucl;
        _bean.rLCL = rlcl;
        _bean.maxXY = maxX;
        _bean.minXY = minX;
        _bean.maxRY = maxR;
        _bean.minRY = minR;

        ParameterBean _ParameterBean = App.getDaoSession().getParameterBeanDao().load(mFilterBean.codeID);
        if (_ParameterBean != null) {
            switch (mFilterBean.getTargetNum()) {
                case 0:
                    _bean.upperValue = (float) (_ParameterBean.getM1_nominal_value() + _ParameterBean.getM1_upper_tolerance_value());
                    _bean.lowerValue = (float) (_ParameterBean.getM1_nominal_value() + _ParameterBean.getM1_lower_tolerance_value());
                    break;
                case 1:
                    _bean.upperValue = (float) (_ParameterBean.getM2_nominal_value() + _ParameterBean.getM2_upper_tolerance_value());
                    _bean.lowerValue = (float) (_ParameterBean.getM2_nominal_value() + _ParameterBean.getM2_lower_tolerance_value());
                    break;
                case 2:
                    _bean.upperValue = (float) (_ParameterBean.getM3_nominal_value() + _ParameterBean.getM3_upper_tolerance_value());
                    _bean.lowerValue = (float) (_ParameterBean.getM3_nominal_value() + _ParameterBean.getM3_lower_tolerance_value());
                    break;
                case 3:
                    _bean.upperValue = (float) (_ParameterBean.getM4_nominal_value() + _ParameterBean.getM4_upper_tolerance_value());
                    _bean.lowerValue = (float) (_ParameterBean.getM4_nominal_value() + _ParameterBean.getM4_lower_tolerance_value());
                    break;
            }
        } else {
            _bean.upperValue = (float) (xbar + Constants.A2[mFilterBean.groupSize - 2] * 0);
            _bean.lowerValue = (float) (xbar - Constants.A2[mFilterBean.groupSize - 2] * 0);
        }

        android.util.Log.d("wlDebug", _bean.toString());
        return _bean;
    }

    private YBYXTBean ybyxtDatas(List<ResultBean> _datas) {
        YBYXTBean _bean = new YBYXTBean();
        ArrayList<Entry> values = new ArrayList<Entry>();
        double[] _values = new double[_datas.size()];
        for (int i = 0; i < _datas.size(); i++) {
            double m = 0;
            switch (mFilterBean.getTargetNum()) {
                case 0:
                    m = _datas.get(i).getM1();
                    break;
                case 1:
                    m = _datas.get(i).getM2();
                    break;
                case 2:
                    m = _datas.get(i).getM3();
                    break;
                case 3:
                    m = _datas.get(i).getM4();
                    break;
            }
            _values[i] = m;
            values.add(new Entry(i + 1, (float) m, getResources().getDrawable(R.drawable.star)));
        }

        double _r = new Max().evaluate(_values) - new Min().evaluate(_values);
        double xbar = new Mean().evaluate(_values);

        ParameterBean _ParameterBean = App.getDaoSession().getParameterBeanDao().load(mFilterBean.codeID);
        _bean.mValues = values;
        if (_ParameterBean != null) {
            switch (mFilterBean.getTargetNum()) {
                case 0:
                    _bean.ucl = (float) (_ParameterBean.getM1_nominal_value() + _ParameterBean.getM1_upper_tolerance_value());
                    _bean.lcl = (float) (_ParameterBean.getM1_nominal_value() + _ParameterBean.getM1_lower_tolerance_value());
                    break;
                case 1:
                    _bean.ucl = (float) (_ParameterBean.getM2_nominal_value() + _ParameterBean.getM2_upper_tolerance_value());
                    _bean.lcl = (float) (_ParameterBean.getM2_nominal_value() + _ParameterBean.getM2_lower_tolerance_value());
                    break;
                case 2:
                    _bean.ucl = (float) (_ParameterBean.getM3_nominal_value() + _ParameterBean.getM3_upper_tolerance_value());
                    _bean.lcl = (float) (_ParameterBean.getM3_nominal_value() + _ParameterBean.getM3_lower_tolerance_value());
                    break;
                case 3:
                    _bean.ucl = (float) (_ParameterBean.getM4_nominal_value() + _ParameterBean.getM4_upper_tolerance_value());
                    _bean.lcl = (float) (_ParameterBean.getM4_nominal_value() + _ParameterBean.getM4_lower_tolerance_value());
                    break;
            }
        } else {
            _bean.ucl = (float) (xbar + Constants.A2[mFilterBean.groupSize - 2] * _r);
            _bean.lcl = (float) (xbar - Constants.A2[mFilterBean.groupSize - 2] * _r);
        }

        _bean.maxY = (float) (_bean.ucl + Math.abs(_bean.ucl - xbar) * 0.5);
        _bean.minY = (float) (_bean.lcl - Math.abs(xbar - _bean.lcl) * 0.5);
        android.util.Log.d("wlDebug", " --- " + _bean.toString());
        return _bean;
    }

    /*
     *
     * 过程能力图;
     *
     * */
    private GCNLTBean gcnltDatas(List<ResultBean> _datas) {
        GCNLTBean _bean = new GCNLTBean();
        double[] values = new double[_datas.size()];
        for (int i = 0; i < _datas.size(); i++) {
            double m = 0;
            switch (mFilterBean.getTargetNum()) {
                case 0:
                    m = _datas.get(i).getM1();
                    break;
                case 1:
                    m = _datas.get(i).getM2();
                    break;
                case 2:
                    m = _datas.get(i).getM3();
                    break;
                case 3:
                    m = _datas.get(i).getM4();
                    break;
            }
            values[i] = m;
        }

        Min min = new Min();
        Max max = new Max();
        Mean mean = new Mean(); // 算术平均值
        _bean.minValue = min.evaluate(values);
        _bean.maxValue = max.evaluate(values);
        _bean.averageValue = mean.evaluate(values);

        double normalValue, upperValue, lowValue, T, U, deviation;
        ParameterBean _ParameterBean = App.getDaoSession().getParameterBeanDao().load(mFilterBean.codeID);
        if (_ParameterBean != null) {
            switch (mFilterBean.getTargetNum()) {
                case 0:
                    normalValue = _ParameterBean.getM1_nominal_value();
                    upperValue = normalValue + _ParameterBean.getM1_upper_tolerance_value();
                    lowValue = normalValue + _ParameterBean.getM1_lower_tolerance_value();
                    break;
                case 1:
                    normalValue = _ParameterBean.getM2_nominal_value();
                    upperValue = normalValue + _ParameterBean.getM2_upper_tolerance_value();
                    lowValue = normalValue + _ParameterBean.getM2_lower_tolerance_value();
                    break;
                case 2:
                    normalValue = _ParameterBean.getM3_nominal_value();
                    upperValue = normalValue + _ParameterBean.getM3_upper_tolerance_value();
                    lowValue = normalValue + _ParameterBean.getM3_lower_tolerance_value();
                    break;
                case 3:
                    normalValue = _ParameterBean.getM4_nominal_value();
                    upperValue = normalValue + _ParameterBean.getM4_upper_tolerance_value();
                    lowValue = normalValue + _ParameterBean.getM4_lower_tolerance_value();
                    break;
                default:
                    normalValue = 12;
                    upperValue = normalValue + 1;
                    lowValue = normalValue - 2;
                    break;
            }
        } else {
            normalValue = 12;
//            upperValue = 23.991796;
//            lowValue = 23.988764;
            upperValue = 24;
            lowValue = 23.99;
        }

        _bean.nominalValue = normalValue;
        _bean.usl = upperValue;
        _bean.lsl = lowValue;

        T = upperValue - lowValue;
        U = (upperValue + lowValue) / 2;
        StandardDeviation StandardDeviation = new StandardDeviation();//标准差
        // deviation = StandardDeviation.evaluate(values);
        double rbar = 0;
        ArrayList<Double> _mList = new ArrayList<>();
        double[] _rGroup = new double[mFilterBean.getGroupNum()];
        for (int i = 0; i < mFilterBean.getGroupNum(); i++) {
            double _r = 0;
            double[] rGroup = new double[mFilterBean.getGroupSize()];
            for (int j = 0; j < mFilterBean.getGroupSize(); j++) {
                int index = i * mFilterBean.getGroupSize() + j;
                rGroup[j] = values[index];
            }
            _r = max.evaluate(rGroup) - min.evaluate(rGroup);
            _rGroup[i] = _r;
        }
        rbar = mean.evaluate(_rGroup);
        double d2 = Constants.d2[mFilterBean.groupSize - 2];
        deviation = rbar / d2;
        double cp, ca, CPKu, CPKl, cpl, cpu, cpk;
        _bean.a = deviation;
        cp = T / (6 * deviation);
        ca = (_bean.averageValue - U) / (T / 2);
        CPKu = Math.abs(upperValue - _bean.averageValue) / (3 * deviation);
        CPKl = Math.abs(_bean.averageValue - lowValue) / (3 * deviation);
        cpl = (_bean.averageValue - lowValue) / (3 * deviation);
        cpu = (upperValue - _bean.averageValue) / (3 * deviation);
        cpk = Math.min(CPKu, CPKl);

        // _bean.cp = cp;
        _bean.cpl = cpl;
        _bean.cpu = cpu;
        _bean.cpk = cpk;

        double pp, pa, PPKu, PPKl, ppl, ppu, ppk, deviation2;

        deviation2 = StandardDeviation.evaluate(values);
        pp = T / (6 * deviation2);
        pa = (_bean.averageValue - U) / (T / 2);
        PPKu = Math.abs(upperValue - _bean.averageValue) / (3 * deviation2);
        PPKl = Math.abs(_bean.averageValue - lowValue) / (3 * deviation2);
        ppl = (_bean.averageValue - lowValue) / (3 * deviation2);
        ppu = (upperValue - _bean.averageValue) / (3 * deviation2);
        ppk = Math.min(PPKl, PPKu);

        _bean.cp = cp;
        _bean.pp = pp;
        _bean.ppl = ppl;
        _bean.ppu = ppu;
        _bean.ppk = ppk;

        return _bean;
    }

    private void updateChartDatas(List<Entry> values) {
        if (chart.getData() == null) {
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(getLineDataSet(getDemoValues(), "M")); // add the data sets
            // create a data object with the data sets
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
        LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
        set1.setValues(values);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    @Override
    protected void initView() {
        mParameterBean = App.getDaoSession().getParameterBeanDao().load((long) App.getSetupBean().getCodeID());
        timeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                android.util.Log.d("wlDebug", "timeRG check id = " + checkedId);
                if (timeRG.getCheckedRadioButtonId() == R.id.auto_time_rb) {
                    // 自动情况下，edt不可以写;
                    startTimeBtn.setEnabled(false);
                    stopTimeBtn.setEnabled(false);
                } else {
                    startTimeBtn.setEnabled(true);
                    stopTimeBtn.setEnabled(true);
                }
            }
        });

        SetupBean _bean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
        xuclEdt.setText(String.valueOf(_bean.getXUpperLine()));
        xlclEdt.setText(String.valueOf(_bean.getXLowerLine()));

        ruclEdt.setText(String.valueOf(_bean.getRUpperLine()));
        rlclEdt.setText(String.valueOf(_bean.getRLowerLine()));

        lineRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                android.util.Log.d("wlDebug", "lineRG check id = " + checkedId);
                if (lineRG.getCheckedRadioButtonId() == R.id.auto_line_rb) {
                    // 自动情况下，edt不可以写;
                    ruclEdt.setEnabled(false);
                    rlclEdt.setEnabled(false);
                    xuclEdt.setEnabled(false);
                    xlclEdt.setEnabled(false);
                } else {
                    ruclEdt.setEnabled(true);
                    rlclEdt.setEnabled(true);
                    xuclEdt.setEnabled(true);
                    xlclEdt.setEnabled(true);
                }
            }
        });

        startTimeBtn.setEnabled(false);
        stopTimeBtn.setEnabled(false);
        xuclEdt.setEnabled(false);
        xlclEdt.setEnabled(false);
        ruclEdt.setEnabled(false);
        rlclEdt.setEnabled(false);

        mResultBeanDao = getDaoSession().getResultBeanDao();
        ((RadioButton) lineRG.getChildAt(0)).setChecked(true);
        codeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                android.util.Log.d("wlDebug", "position = " + (position + 1));
                ParameterBean _bean = App.getDaoSession().getParameterBeanDao().load((long) (position + 1));
                if (_bean != null) {
                    List<String> data_list = new ArrayList<>();
                    if (!CommonUtil.isNull(_bean.getM1_describe())) {
                        data_list.add(_bean.getM1_describe());
                    } else {
                        data_list.add("M1");
                    }

                    if (!CommonUtil.isNull(_bean.getM2_describe())) {
                        data_list.add(_bean.getM2_describe());
                    } else {
                        data_list.add("M2");
                    }

                    if (!CommonUtil.isNull(_bean.getM3_describe())) {
                        data_list.add(_bean.getM3_describe());
                    } else {
                        data_list.add("M3");
                    }

                    if (!CommonUtil.isNull(_bean.getM4_describe())) {
                        data_list.add(_bean.getM4_describe());
                    } else {
                        data_list.add("M4");
                    }
                    targetSP.setAdapter(new ArrayAdapter<String>(SPCStatisticalActivity.this, android.R.layout.simple_spinner_dropdown_item, data_list));
                } else {
                    targetSP.setAdapter(new ArrayAdapter<String>(SPCStatisticalActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.m_values)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // background color
        chart.setBackgroundColor(Color.WHITE);
        rChart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);
        rChart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);
        rChart.setTouchEnabled(true);

        chart.setDoubleTapToZoomEnabled(false);
        rChart.setDoubleTapToZoomEnabled(false);

        // chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        rChart.setDrawGridBackground(false);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        rChart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        rChart.setScaleEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);
        rChart.setPinchZoom(true);

        chart.animateX(1500);

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            chart.getXAxis().setDrawGridLines(false);
            rChart.getXAxis().setDrawGridLines(false);

            // vertical grid lines
            // xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);
            rChart.getAxisRight().setEnabled(false);

            chart.getAxisLeft().setDrawGridLines(false);
            rChart.getAxisLeft().setDrawGridLines(false);

            // horizontal grid lines
            // yAxis.enableGridDashedLine(10f, 10f, 0f);
        }
        //

    }

    private LimitLine getLimitLine(float value, String str) {
        LimitLine ll1 = new LimitLine(value, str);
        ll1.setLineWidth(2f);
        ll1.setLineColor(R.color.baseColor);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tfRegular);
        return ll1;
    }

    private LineDataSet getLineDataSet(ArrayList<Entry> values, String str) {
        LineDataSet set1;
        // create a dataset and give it a type
        set1 = new LineDataSet(values, str);

        set1.setDrawIcons(false);

        // draw dashed line 设置虚线;
        // set1.enableDashedLine(10f, 5f, 0f);

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

        set1.setCubicIntensity(0.05f);

        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return super.getFormattedValue(value);
            }
        });

        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f);

        // set the filled area 设置填充颜色，明明很棒的效果，客户不要;
        set1.setDrawFilled(false);
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
        return set1;
    }

    private void clearChart() {
        /*
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(getLineDataSet(getDemoValues(), "M")); // add the data sets
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.getAxisLeft().removeAllLimitLines();
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();


        ArrayList<ILineDataSet> rDataSets = new ArrayList<>();
        rDataSets.add(getLineDataSet(getDemoValues(), "R")); // add the data sets
        LineData rData = new LineData(rDataSets);
        rChart.setData(rData);
        rChart.getAxisLeft().removeAllLimitLines();
        rChart.getData().notifyDataChanged();
        rChart.notifyDataSetChanged();
        rChart.invalidate();
        */
        chart.clear();
        rChart.clear();
        averageValueTV.setText("");
        maxValueTV.setText("");
        minValueTV.setText("");
        nominalValueTV.setText("");
        gcUslTV.setText("");
        gcLslTV.setText("");
        _3aTV.setText("");
        _aTV.setText("");
        cpTV.setText("");
        cpkTV.setText("");
        cplTV.setText("");
        cpuTV.setText("");
        ppTV.setText("");
        ppkTV.setText("");
        pplTV.setText("");
        ppuTV.setText("");
    }

    private ArrayList<Entry> getDemoValues() {
        ArrayList<Entry> values = new ArrayList<>();
        return values;
    }

    class FilterBean {

        private long codeID;

        private int targetNum;

        private int groupSize;

        private int groupNum;

        private double xucl;

        private double xlcl;

        private double rucl;

        private double rlcl;

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

        public double getXucl() {
            return xucl;
        }

        public void setXucl(double xucl) {
            this.xucl = xucl;
        }

        public double getXlcl() {
            return xlcl;
        }

        public void setXlcl(double xlcl) {
            this.xlcl = xlcl;
        }

        public double getRucl() {
            return rucl;
        }

        public void setRucl(double rucl) {
            this.rucl = rucl;
        }

        public double getRlcl() {
            return rlcl;
        }

        public void setRlcl(double rlcl) {
            this.rlcl = rlcl;
        }

        @Override
        public String toString() {
            return "FilterBean{" +
                    "codeID=" + codeID +
                    ", targetNum=" + targetNum +
                    ", groupSize=" + groupSize +
                    ", groupNum=" + groupNum +
                    ", xucl=" + xucl +
                    ", xlcl=" + xlcl +
                    ", rucl=" + rucl +
                    ", rlcl=" + rlcl +
                    ", isTimeAuto=" + isTimeAuto +
                    ", isLineAuto=" + isLineAuto +
                    '}';
        }
    }

    // 均值极差图;
    class JZJCTBean {
        float xbar;
        // X图的上控制线;
        float xUCL;
        // X图的下控制线;
        float xLCL;
        // X图的Y轴最大值;
        float maxXY;
        // X图的Y轴最小值:
        float minXY;
        // X图的数据；
        ArrayList<Entry> xValues;
        //
        float rbar;
        // R图的上控制线;
        float rUCL;
        // R图的下控制线;
        float rLCL;
        // R图的Y轴最大值;
        float maxRY;
        // R图的Y轴最小值;
        float minRY;
        // R图的数据；
        ArrayList<Entry> rValues;

        float upperValue;

        float lowerValue;

        @Override
        public String toString() {
            return "JZJCTBean{" +
                    "xbar=" + xbar +
                    ", xUCL=" + xUCL +
                    ", xLCL=" + xLCL +
                    ", maxXY=" + maxXY +
                    ", minXY=" + minXY +
                    ", rbar=" + rbar +
                    ", rUCL=" + rUCL +
                    ", rLCL=" + rLCL +
                    ", maxRY=" + maxRY +
                    ", minRY=" + minRY +
                    '}';
        }
    }

    class YBYXTBean {
        // M数据；
        ArrayList<Entry> mValues;
        // 上公差值;
        float ucl;
        // 下公差值;
        float lcl;
        //
        float maxY;
        //
        float minY;

        @Override
        public String toString() {
            return "YBYXTBean{" +
                    "mValues=" + mValues +
                    ", ucl=" + ucl +
                    ", lcl=" + lcl +
                    ", maxY=" + maxY +
                    ", minY=" + minY +
                    '}';
        }
    }

    class GCNLTBean {
        // 平均值;
        double averageValue;
        // 最大值;
        double maxValue;
        // 最小值;
        double minValue;
        // 名义值;
        double nominalValue;
        // USL;
        double usl;
        // LSL;
        double lsl;
        // 标准差;
        double a;
        // cp
        double cp;
        //
        double cpk;
        //
        double cpl;
        //
        double cpu;
        // pp
        double pp;
        //
        double ppk;
        //
        double ppl;
        //
        double ppu;

        @Override
        public String toString() {
            return "GCNLTBean{" +
                    "averageValue=" + averageValue +
                    ", maxValue=" + maxValue +
                    ", minValue=" + minValue +
                    ", nominalValue=" + nominalValue +
                    ", usl=" + usl +
                    ", lsl=" + lsl +
                    ", a=" + a +
                    ", cp=" + cp +
                    ", cpk=" + cpk +
                    ", cpl=" + cpl +
                    ", cpu=" + cpu +
                    ", pp=" + pp +
                    ", ppk=" + ppk +
                    ", ppl=" + ppl +
                    ", ppu=" + ppu +
                    '}';
        }
    }

}
