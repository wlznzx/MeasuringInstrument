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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.AnalysisPatternBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.AnalysisPatternBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBean2Dao;
import alauncher.cn.measuringinstrument.utils.Constants;
import alauncher.cn.measuringinstrument.utils.DateUtils;
import alauncher.cn.measuringinstrument.utils.Format;
import alauncher.cn.measuringinstrument.utils.StringConverter;
import alauncher.cn.measuringinstrument.widget.CustomMarkerView;
import butterknife.BindView;
import butterknife.OnClick;

public class SPCStatistical2Activity extends BaseOActivity {

    @BindView(R.id.statistical_chart)
    public LineChart chart;

    @BindView(R.id.r_chart)
    public LineChart rChart;

    @BindView(R.id.combine_chart)
    public CombinedChart mCombinedChart;

    @BindView(R.id.combine_chart_layout)
    public View mCombinedChartLayout;

    protected Typeface tfRegular;

    @BindView(R.id.code_sp)
    public Spinner codeSP;

    @BindView(R.id.target_sp)
    public Spinner targetSP;

    @BindView(R.id.group_size_sp)
    public Spinner groupSizeSP;

    @BindView(R.id.group_num_sp)
    public Spinner groupNumSP;

    @BindView(R.id.user_sp)
    public Spinner userSP;

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

    @BindView(R.id.process_no_sp)
    public Spinner processNoSP;

    @BindView(R.id.machine_info_sp)
    public Spinner machineInfoSP;

    @BindView(R.id.m_type_sp)
    public Spinner mTypeSP;

    @BindView(R.id.time_mode_sw)
    public Switch timeSW;

    public List<User> users;

//    public ResultBeanDao mResultBeanDao;

    private long startTimeStamp = 0;

    private long stopTimeStamp = 0;

    // 均值极差图.
    private final int JZJCT_MODE = 1;

    // 样本运行图;
    private final int YBYXT_MODE = 2;

    // 过程能力图;
    private final int GCNLT_MODE = 3;

    private int spc_mode = JZJCT_MODE;

    private FilterBean mFilterBean;

    private List<ParameterBean2> mParameterBean2Lists;

    private List<CodeBean> mCodeBeanLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        SetupBean _bean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
        _bean.setXUpperLine(Double.valueOf(xuclEdt.getText().toString().trim()));
        _bean.setXLowerLine(Double.valueOf(xlclEdt.getText().toString().trim()));
        _bean.setRUpperLine(Double.valueOf(ruclEdt.getText().toString().trim()));
        _bean.setRLowerLine(Double.valueOf(rlclEdt.getText().toString().trim()));
        App.getDaoSession().getSetupBeanDao().insertOrReplace(_bean);
         */
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_spc_statistical);
    }

    @OnClick({R.id.start_time_btn, R.id.stop_time_btn,
            R.id.spc_statistical_btn,
            R.id.delete_spc_result, R.id.jzjct_btn,
            R.id.enlarge_combine_chart_btn, R.id.narrow_combine_chart_btn})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.start_time_btn:
                Calendar now = Calendar.getInstance();
                new DatePickerDialog(
                        SPCStatistical2Activity.this,
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
                android.util.Log.d("wlDebug", "stop_time_btn click");
                Calendar _now = Calendar.getInstance();
                new DatePickerDialog(
                        SPCStatistical2Activity.this,
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
//                mCombinedChart.setScaleX(2);
//                mCombinedChart.setScaleY(2);
//                mCombinedChart.getScrollX();
                break;
            case R.id.enlarge_combine_chart_btn:
                mCombinedChart.zoomIn();
                break;
            case R.id.narrow_combine_chart_btn:
                mCombinedChart.zoomOut();
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
                mCombinedChart.setVisibility(View.GONE);
                mCombinedChartLayout.setVisibility(View.GONE);
                break;
            case R.id.ybyxt_btn:
                if (spc_mode != YBYXT_MODE) {
                    clearChart();
                }
                spc_mode = YBYXT_MODE;
                chart.setVisibility(View.VISIBLE);
                rChart.setVisibility(View.INVISIBLE);
                mTableLayout.setVisibility(View.GONE);
                mCombinedChart.setVisibility(View.GONE);
                mCombinedChartLayout.setVisibility(View.GONE);
                break;
            case R.id.gcnlt_btn:
                chart.setVisibility(View.GONE);
                rChart.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.VISIBLE);
                mCombinedChart.setVisibility(View.VISIBLE);
                mCombinedChartLayout.setVisibility(View.VISIBLE);
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
        mFilterBean.accout = users.get(userSP.getSelectedItemPosition()).getAccout();
        mFilterBean.groupSize = Integer.valueOf((String) groupSizeSP.getSelectedItem());
        mFilterBean.groupNum = Integer.valueOf((String) groupNumSP.getSelectedItem());
        mFilterBean.mType = mTypeSP.getSelectedItem().toString().trim();
        mFilterBean.processNo = processNoSP.getSelectedItem().toString().trim();
        mFilterBean.machineInfo = machineInfoSP.getSelectedItem().toString().trim();
        // 测量模式；
        mParameterBean2Lists = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(mFilterBean.codeID), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
        ParameterBean2 _bean2 = mParameterBean2Lists.get(mFilterBean.getTargetNum());
        android.util.Log.d("wlDebug", "spc pid = " + _bean2.getId());
        AnalysisPatternBean bean = App.getDaoSession().getAnalysisPatternBeanDao().queryBuilder().where(AnalysisPatternBeanDao.Properties.PID.eq(_bean2.getId())).unique();
        mFilterBean.isTimeAuto = timeSW.isChecked();
        mFilterBean.isLineAuto = bean.isLineAuto;
        mFilterBean.isA3Auto = bean.isAAuto;
        mFilterBean.setXucl(bean.getUclX());
        mFilterBean.setXlcl(bean.getLclX());
        mFilterBean.setRucl(bean.getUclR());
        mFilterBean.setRlcl(bean.getLclR());
        mFilterBean.set_a3(bean.get_a3());
        mFilterBean.setA3(bean.getA3());
        android.util.Log.d("wlDebug", "mFilterBean = " + mFilterBean.toString());
        // dataFilterUpdate();
    }

    /*
     *
     * 导出Excel Task.
     *
     * */
    public class SPCTask extends AsyncTask<String, Integer, Object> {

        private ProgressDialog dialog;
        private String path = Environment.getExternalStorageDirectory() + "/NTGage/";

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SPCStatistical2Activity.this);
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
            List<ResultBean2> _datas = dataFilterUpdate();
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
//                    yAxis.addLimitLine(getLimitLine(_bean.xUCL, "上控制线"));
//                    yAxis.addLimitLine(getLimitLine(_bean.xLCL, "下控制线"));
                    if (mFilterBean.isLineAuto()) {
                        yAxis.addLimitLine(getLimitLine(_bean.rUCL, "上控制线"));
                        yAxis.addLimitLine(getLimitLine(_bean.rLCL, "下控制线"));
                    } else {
                        yAxis.addLimitLine(getLimitLine((float) mFilterBean.rucl, "上控制线"));
                        yAxis.addLimitLine(getLimitLine((float) mFilterBean.rlcl, "下控制线"));
                    }
                    yAxis.addLimitLine(getLimitLine(_bean.upperValue, "上公差线"));
                    yAxis.addLimitLine(getLimitLine(_bean.lowerValue, "下公差线"));
                    android.util.Log.d("wllDebug", "_bean.xUCL = " + _bean.xUCL);
                    android.util.Log.d("wllDebug", "_bean.xLCL = " + _bean.xLCL);
                    android.util.Log.d("wllDebug", "_bean.upperValue = " + _bean.upperValue);
                    android.util.Log.d("wllDebug", "_bean.lowerValue = " + _bean.lowerValue);
                    updateChartDatas(_bean.xValues);
                    // 绘制R图;
                    YAxis rYAxis = rChart.getAxisLeft();
                    rYAxis.setAxisMaximum(_bean.maxRY);
                    rYAxis.setAxisMinimum(_bean.minRY);
                    rYAxis.removeAllLimitLines();
                    if (mFilterBean.isLineAuto()) {
                        rYAxis.addLimitLine(getLimitLine(_bean.rUCL, "上控制线"));
                        rYAxis.addLimitLine(getLimitLine(_bean.rLCL, "下控制线"));
                    } else {
                        rYAxis.addLimitLine(getLimitLine((float) mFilterBean.rucl, "上控制线"));
                        rYAxis.addLimitLine(getLimitLine((float) mFilterBean.rlcl, "下控制线"));
                    }
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
                    if (mFilterBean.isLineAuto()) {
                        gcUslTV.setText("" + Format.m1(_bean.usl, 4));
                        gcLslTV.setText("" + Format.m1(_bean.lsl, 4));
                    } else {
                        gcUslTV.setText("" + Format.m1(mFilterBean.xucl, 4));
                        gcLslTV.setText("" + Format.m1(mFilterBean.xlcl, 4));
                    }
                    if (mFilterBean.isA3Auto()) {
                        _3aTV.setText("" + Format.m1((_bean.averageValue + _bean.a * -3), 4));
                        _aTV.setText("" + Format.m1((_bean.averageValue + _bean.a * 3), 4));
                    } else {
                        _3aTV.setText("" + Format.m1((mFilterBean._a3), 4));
                        _aTV.setText("" + Format.m1((mFilterBean.a3), 4));
                    }
                    cpTV.setText("" + Format.m1(_bean.cp, 2));
                    cpkTV.setText("" + Format.m1(_bean.cpk, 2));
                    cplTV.setText("" + Format.m1(_bean.cpl, 2));
                    cpuTV.setText("" + Format.m1(_bean.cpu, 2));
                    ppTV.setText("" + Format.m1(_bean.pp, 2));
                    ppkTV.setText("" + Format.m1(_bean.ppk, 2));
                    pplTV.setText("" + Format.m1(_bean.ppl, 2));
                    ppuTV.setText("" + Format.m1(_bean.ppu, 2));
                    // 过程能力图形;
                    //颜色集合
                    List<Integer> colors = new ArrayList<>();
                    colors.add(Color.BLUE);
                    colors.add(Color.RED);
                    colors.add(Color.YELLOW);
                    colors.add(Color.CYAN);
                    showCombinedChart(_bean.xData, _bean.barData, _bean.lineData,
                            getResources().getString(R.string.frequent), getResources()
                                    .getString(R.string.normal_curve), R.color.colorPrimary, colors.get(1));
                }
            } else {
                Toast.makeText(SPCStatistical2Activity.this, "数据源数量不足以分析.", Toast.LENGTH_SHORT).show();
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

    public List<ResultBean2> dataFilterUpdate() {

        int _limit = mFilterBean.getGroupNum() * mFilterBean.getGroupSize();

//        if (mFilterBean.isTimeAuto() && startTimeStamp >= stopTimeStamp) {
//            return null;
//        }

        String queryString = "";
        /*
        if (!mFilterBean.isTimeAuto()) {
            queryString = "SELECT * FROM " + ResultBean2Dao.TABLENAME + " where " + ResultBean2Dao.Properties.CodeID.columnName + " = " + mFilterBean.getCodeID() + " order by _id desc limit " + _limit;
        } else {
            queryString = "SELECT * FROM " + ResultBean2Dao.TABLENAME + " where " + ResultBean2Dao.Properties.TimeStamp.columnName + " between " + startTimeStamp + " and " + stopTimeStamp + " order by _id desc limit " + _limit;
        }
         */
        queryString = "SELECT * FROM " + ResultBean2Dao.TABLENAME + " where " + ResultBean2Dao.Properties.CodeID.columnName + " = " + mFilterBean.getCodeID();
        if (mFilterBean.isTimeAuto()) {
            queryString = queryString + " and " + ResultBean2Dao.Properties.TimeStamp.columnName + " between " + startTimeStamp + " and " + stopTimeStamp;
        }
        if (!mFilterBean.getmType().equals("所有")) {
            queryString = queryString + " and " + ResultBean2Dao.Properties.MType.columnName + " = " + "'" + mFilterBean.getmType() + "'";
        }
        if (!mFilterBean.getProcessNo().equals("所有")) {
            queryString = queryString + " and " + ResultBean2Dao.Properties.ProcessNo.columnName + " = " + "'" + mFilterBean.getProcessNo() + "'";
        }
        if (!mFilterBean.getMachineInfo().equals("所有")) {
            queryString = queryString + " and " + ResultBean2Dao.Properties.MachineInfo.columnName + " = " + "'" + mFilterBean.getMachineInfo() + "'";
        }
        if (!mFilterBean.getAccout().equals("所有")) {
            queryString = queryString + " and " + ResultBean2Dao.Properties.HandlerAccount.columnName + " = " + "'" + mFilterBean.getAccout() + "'";
        }
        queryString = queryString + " order by _id desc limit " + _limit;
        android.util.Log.d("wlDebug", "queryString = " + queryString);

        Cursor cursor = App.getDaoSession().getResultBean2Dao().getDatabase().rawQuery(queryString, null);

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
//        for (ResultBean2 _bean : _datas) {
//            android.util.Log.d("wlDebug", _bean.toString());
//        }
        if (_datas.size() < _limit) {
            // Toast.makeText(SPCStatisticalActivity.this, "数据源数量不足以分析.", Toast.LENGTH_SHORT).show();
            return null;
        }
        return _datas;
    }

    /*
     *  均值极差图;
     * */
    private JZJCTBean jzjctDatas(List<ResultBean2> _datas) {
        JZJCTBean _bean = new JZJCTBean();
        double[] m = new double[_datas.size()];
        double total = 0;
        for (int i = 0; i < _datas.size(); i++) {
            m[i] = getValuesFromResultBean2(_datas.get(i), mFilterBean.getTargetNum());
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

        mParameterBean2Lists = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(mFilterBean.codeID), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
        if (mParameterBean2Lists.size() > 0) {
            ParameterBean2 _bean2 = mParameterBean2Lists.get(mFilterBean.getTargetNum());
            _bean.upperValue = (float) (_bean2.getNominalValue() + _bean2.getUpperToleranceValue());
            _bean.lowerValue = (float) (_bean2.getNominalValue() + _bean2.getLowerToleranceValue());
        } else {
            _bean.upperValue = (float) (xbar + Constants.A2[mFilterBean.groupSize - 2] * 0);
            _bean.lowerValue = (float) (xbar - Constants.A2[mFilterBean.groupSize - 2] * 0);
        }
        android.util.Log.d("wlDebug", _bean.toString());
        return _bean;
    }

    private YBYXTBean ybyxtDatas(List<ResultBean2> _datas) {
        YBYXTBean _bean = new YBYXTBean();
        ArrayList<Entry> values = new ArrayList<Entry>();
        double[] _values = new double[_datas.size()];
        for (int i = 0; i < _datas.size(); i++) {
            _values[i] = getValuesFromResultBean2(_datas.get(i), mFilterBean.getTargetNum());
            values.add(new Entry(i + 1, (float) _values[i], getResources().getDrawable(R.drawable.star)));
        }
        _bean.mValues = values;
        double _r = new Max().evaluate(_values) - new Min().evaluate(_values);
        double xbar = new Mean().evaluate(_values);

        mParameterBean2Lists = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(mFilterBean.codeID), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
        if (mParameterBean2Lists.size() > 0) {
            ParameterBean2 _bean2 = mParameterBean2Lists.get(mFilterBean.getTargetNum());
            _bean.ucl = (float) (_bean2.getNominalValue() + _bean2.getUpperToleranceValue());
            _bean.lcl = (float) (_bean2.getNominalValue() + _bean2.getLowerToleranceValue());
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
    private GCNLTBean gcnltDatas(List<ResultBean2> _datas) {
        GCNLTBean _bean = new GCNLTBean();
        double[] values = new double[_datas.size()];
        for (int i = 0; i < _datas.size(); i++) {
            double m = getValuesFromResultBean2(_datas.get(i), mFilterBean.getTargetNum());
            values[i] = m;
        }
        Min min = new Min();
        Max max = new Max();
        Mean mean = new Mean(); // 算术平均值
        _bean.minValue = min.evaluate(values);
        _bean.maxValue = max.evaluate(values);
        _bean.averageValue = mean.evaluate(values);

        double normalValue, upperValue, lowValue, T, U, deviation;
        /**/
        if (mParameterBean2Lists.size() > 0) {
            ParameterBean2 _bean2 = mParameterBean2Lists.get(mFilterBean.getTargetNum());
            normalValue = _bean2.getNominalValue();
            upperValue = _bean2.getNominalValue() + _bean2.getUpperToleranceValue();
            lowValue = _bean2.getNominalValue() + _bean2.getLowerToleranceValue();
        } else {
            normalValue = 12;
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
        if (mFilterBean.isA3Auto()) {
            deviation = rbar / d2;
        } else {
            deviation = (mFilterBean.getA3() - mFilterBean.get_a3()) / 6;
        }
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
        if (mFilterBean.isA3Auto()) {
            deviation2 = StandardDeviation.evaluate(values);
        } else {
            deviation2 = (mFilterBean.getA3() - mFilterBean.get_a3()) / 6;
        }
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

        // 计算中心值
        // 计算标准差
        // 计算组数：数据个数 / 4
//        int group_size = _datas.size() / 4;
        int group_size = 25;
        // 组坐标上限;
        double group_upper_limit = _bean.averageValue + 4 * deviation2;
        // 组坐标上限;
        double group_lower_limit = _bean.averageValue - 4 * deviation2;
        // 组距;
        _bean.barData = new ArrayList<>();
        _bean.xData = new ArrayList<>();
        _bean.lineData = new ArrayList<>();
        final NumberFormat formatter = new DecimalFormat("0.0000");
        double group_space = (group_upper_limit - group_lower_limit) / (group_size - 1);
        for (int i = 0; i < group_size; i++) {
            float _num = 0;
            for (int j = 0; j < values.length; j++) {
                if (i == 0) {
                    if (values[j] < group_lower_limit + (i + 1) * group_space) {
                        _num++;
                    }
                } else if (i == group_size - 1) {
                    if (values[j] >= group_lower_limit + i * group_space) {
                        _num++;
                    }
                } else if (values[j] >= group_lower_limit + i * group_space && values[j] < group_lower_limit + (i + 1) * group_space) {
                    _num++;
                }
            }
            _bean.barData.add(_num);
            // 组坐标
            double x = group_lower_limit + i * group_space;
            _bean.xData.add(formatter.format(x));
            float l = (float) ((1 / Math.sqrt(2 * Math.PI) * deviation2) * Math.exp(-1 * ((x - _bean.averageValue) * (x - _bean.averageValue)) / (2 * deviation2 * deviation2)) * _bean.averageValue);
            _bean.lineData.add(l);
            android.util.Log.d("alauncher", "l = " + l);
        }
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
        timeSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //控制开关字体颜色
                if (b) {
                    startTimeBtn.setEnabled(true);
                    stopTimeBtn.setEnabled(true);
                } else {
                    startTimeBtn.setEnabled(false);
                    stopTimeBtn.setEnabled(false);
                }
            }
        });
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

        // 初始化筛选条件 用户
        int userDefaultPosition = 0;
        users = new ArrayList<>();
        User __user = new User();
        __user.setName("所有");
        __user.setAccout("所有");
        users.add(__user);
        users.addAll(App.getDaoSession().getUserDao().loadAll());
        List<String> userList = new ArrayList<>();
//        userList.add("所有");
        for (int i = 0; i < users.size(); i++) {
            User _user = users.get(i);
            userList.add(_user.getName());
            if (_user.getAccout().equals(App.handlerAccout)) {
                userDefaultPosition = i;
            }
        }
        ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(SPCStatistical2Activity.this,
                android.R.layout.simple_spinner_item,
                userList);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSP.setAdapter(userAdapter);
        userSP.setSelection(userDefaultPosition);

        // 程序;
        int codeDefaultPosition = 0;
        mCodeBeanLists = App.getDaoSession().getCodeBeanDao().loadAll();
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < mCodeBeanLists.size(); i++) {
            CodeBean _code = mCodeBeanLists.get(i);
            codeList.add(_code.getName());
            if (App.getSetupBean().getCodeID() == _code.getId()) {
                codeDefaultPosition = i;
            }
        }
        ArrayAdapter<String> codeAdapter = new ArrayAdapter<String>(SPCStatistical2Activity.this,
                android.R.layout.simple_spinner_item,
                codeList);
        codeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        codeSP.setAdapter(codeAdapter);
        codeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                android.util.Log.d("wlDebug", "position = " + (position + 1));
                // 获取参数列表;
                mParameterBean2Lists = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                        .where(ParameterBean2Dao.Properties.CodeID.eq((long) (position + 1)), ParameterBean2Dao.Properties.Enable.eq(true))
                        .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
                if (mParameterBean2Lists.size() > 0) {
                    List<String> targetData = new ArrayList<>();
                    for (ParameterBean2 _bean : mParameterBean2Lists) {
                        targetData.add("M" + (_bean.getSequenceNumber() + 1));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SPCStatistical2Activity.this,
                            android.R.layout.simple_spinner_item,
                            targetData);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    targetSP.setAdapter(adapter);
                } else {
                    targetSP.setAdapter(new ArrayAdapter<String>(SPCStatistical2Activity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.m_values)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        codeSP.setSelection(codeDefaultPosition);

        ((RadioButton) lineRG.getChildAt(0)).setChecked(true);
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
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
        chart.setMarkerView(mv);
        rChart.setMarkerView(mv);
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
            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            rChart.getXAxis().setDrawGridLines(false);
            rChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

            // vertical grid lines
            // xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);
            rChart.getAxisRight().setEnabled(false);
            chart.getAxisLeft().setDrawGridLines(false);
            rChart.getAxisLeft().setDrawGridLines(false);

            // horizontal grid lines
            // yAxis.enableGridDashedLine(10f, 10f, 0f);
            initChart();
        }


    }

    // 初始化过程能力图;
    private void initChart() {
        YAxis leftAxis;
        YAxis rightAxis;
        //不显示描述内容
        mCombinedChart.getDescription().setEnabled(false);
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
        mCombinedChart.setMarkerView(mv);
        mCombinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.LINE
        });

        mCombinedChart.setBackgroundColor(Color.WHITE);
        mCombinedChart.setDrawGridBackground(false);
        mCombinedChart.setDrawBarShadow(false);
        mCombinedChart.setHighlightFullBarEnabled(false);
        //显示边界
        mCombinedChart.setDrawBorders(true);
        mCombinedChart.getXAxis().setLabelRotationAngle(-30);
        //
        leftAxis = mCombinedChart.getAxisLeft();
        rightAxis = mCombinedChart.getAxisRight();
        //图例说明
        Legend legend = mCombinedChart.getLegend();
        legend.setWordWrapEnabled(true);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        //Y轴设置
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        mCombinedChart.animateX(1500); // 立即执行的动画,x轴
    }

    /**
     * 设置X轴坐标值
     *
     * @param xAxisValues x轴坐标集合
     */
    public void setXAxis(final List<String> xAxisValues) {

        //设置X轴在底部
        XAxis xAxis = mCombinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(xAxisValues.size() - 1, false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        // mCombinedChart.invalidate();
    }

    /**
     * 得到折线图(一条)
     *
     * @param lineChartY 折线Y轴值
     * @param lineName   折线图名字
     * @param lineColor  折线颜色
     * @return
     */
    private LineData getLineData(List<Float> lineChartY, String lineName, int lineColor) {
        LineData lineData = new LineData();

        ArrayList<Entry> yValue = new ArrayList<>();
        for (int i = 0; i < lineChartY.size(); i++) {
            yValue.add(new Entry(i, lineChartY.get(i)));
        }
        LineDataSet dataSet = new LineDataSet(yValue, lineName);

        dataSet.setColor(lineColor);
        dataSet.setCircleColor(lineColor);
        dataSet.setValueTextColor(lineColor);

        dataSet.setCircleSize(3);
        //显示值
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(0f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineData.addDataSet(dataSet);
        return lineData;
    }

    /**
     * 得到柱状图
     *
     * @param barChartY Y轴值
     * @param barName   柱状图名字
     * @param barColor  柱状图颜色
     * @return
     */

    private BarData getBarData(List<Float> barChartY, String barName, int barColor) {
        BarData barData = new BarData();
        ArrayList<BarEntry> yValues = new ArrayList<>();
        for (int i = 0; i < barChartY.size(); i++) {
            yValues.add(new BarEntry(i, barChartY.get(i)));
        }
        final NumberFormat formatter = new DecimalFormat("0");
        BarDataSet barDataSet = new BarDataSet(yValues, barName);
        barDataSet.setColor(barColor);
        barDataSet.setValueTextSize(10f);
        barDataSet.setValueTextColor(barColor);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatter.format(value);
            }
        });
        barData.addDataSet(barDataSet);

        //以下是为了解决 柱状图 左右两边只显示了一半的问题 根据实际情况 而定
        mCombinedChart.getXAxis().setAxisMinimum(-0.5f);
        mCombinedChart.getXAxis().setAxisMaximum((float) (barChartY.size() - 0.5));
        return barData;
    }

    /**
     * 显示混合图(柱状图+折线图)
     *
     * @param xAxisValues X轴坐标
     * @param barChartY   柱状图Y轴值
     * @param lineChartY  折线图Y轴值
     * @param barName     柱状图名字
     * @param lineName    折线图名字
     * @param barColor    柱状图颜色
     * @param lineColor   折线图颜色
     */

    public void showCombinedChart(
            List<String> xAxisValues, List<Float> barChartY, List<Float> lineChartY
            , String barName, String lineName, int barColor, int lineColor) {
        initChart();
        setXAxis(xAxisValues);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(getBarData(barChartY, barName, barColor));
        combinedData.setData(getLineData(lineChartY, lineName, lineColor));
        mCombinedChart.setData(combinedData);
        mCombinedChart.invalidate();
    }


    private double getValuesFromResultBean2(ResultBean2 _bean, int index) {
        double result = 0;
        try {
            result = Double.parseDouble(_bean.getMeasurementValues().get(index));
        } catch (Exception e) {

        }
        return result;
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
        mCombinedChart.clear();
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

        private String accout;

        private String mType;

        private String machineInfo;

        private String processNo;

        private double xucl;

        private double xlcl;

        private double rucl;

        private double rlcl;

        private double a3;

        private double _a3;

        private boolean isA3Auto;

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

        public String getAccout() {
            return accout;
        }

        public void setAccout(String accout) {
            this.accout = accout;
        }

        public String getmType() {
            return mType;
        }

        public void setmType(String mType) {
            this.mType = mType;
        }

        public String getMachineInfo() {
            return machineInfo;
        }

        public void setMachineInfo(String machineInfo) {
            this.machineInfo = machineInfo;
        }

        public String getProcessNo() {
            return processNo;
        }

        public void setProcessNo(String processNo) {
            this.processNo = processNo;
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

        public double getA3() {
            return a3;
        }

        public void setA3(double a3) {
            this.a3 = a3;
        }

        public double get_a3() {
            return _a3;
        }

        public void set_a3(double _a3) {
            this._a3 = _a3;
        }

        public boolean isA3Auto() {
            return isA3Auto;
        }

        public void setA3Auto(boolean a3Auto) {
            isA3Auto = a3Auto;
        }

        @Override
        public String toString() {
            return "FilterBean{" +
                    "codeID=" + codeID +
                    ", targetNum=" + targetNum +
                    ", groupSize=" + groupSize +
                    ", groupNum=" + groupNum +
                    ", accout='" + accout + '\'' +
                    ", mType='" + mType + '\'' +
                    ", machineInfo='" + machineInfo + '\'' +
                    ", processNo='" + processNo + '\'' +
                    ", xucl=" + xucl +
                    ", xlcl=" + xlcl +
                    ", rucl=" + rucl +
                    ", rlcl=" + rlcl +
                    ", a3=" + a3 +
                    ", _a3=" + _a3 +
                    ", isA3Auto=" + isA3Auto +
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
        //
        List<Float> barData;
        //
        List<Float> lineData;
        //
        List<String> xData;

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
