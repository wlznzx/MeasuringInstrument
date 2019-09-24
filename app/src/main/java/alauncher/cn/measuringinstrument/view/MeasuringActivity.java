package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
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
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBeanDao;
import alauncher.cn.measuringinstrument.mvp.presenter.MeasuringPresenter;
import alauncher.cn.measuringinstrument.mvp.presenter.impl.MeasuringPresenterImpl;
import alauncher.cn.measuringinstrument.utils.NumberUtils;
import alauncher.cn.measuringinstrument.view.activity_view.MeasuringActivityView;
import alauncher.cn.measuringinstrument.widget.AdditionalDialog;
import alauncher.cn.measuringinstrument.widget.MValueView;

import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class MeasuringActivity extends BaseOActivity implements MeasuringActivityView, AdditionalDialog.AdditionDialogInterface {

    @BindViews({R.id.m1_value, R.id.m2_value, R.id.m3_value, R.id.m4_value})
    public MValueView[] mMValueViews;

    @BindView(R.id.m_chart)
    public LineChart chart;

    @BindView(R.id.value_btn)
    public TextView valueBtn;

    @BindViews({R.id.group_m1, R.id.group_m2, R.id.group_m3, R.id.group_m4})
    public TextView mGroupMs[];

    @BindViews({R.id.m1_text_value, R.id.m2_text_value, R.id.m3_text_value, R.id.m4_text_value})
    public TextView mTValues[];

    @BindViews({R.id.m1_title, R.id.m2_title, R.id.m3_title, R.id.m4_title})
    public TextView mTitle[];

    @BindViews({R.id.m1_describe, R.id.m2_describe, R.id.m3_describe, R.id.m4_describe})
    public TextView mDescribes[];

    protected Typeface tfRegular;
    protected Typeface tfLight;

    private double[] curMValues = {1.8, -2.8, 0.8, -0.4};

    private boolean inValue = false;

    private MeasuringPresenter mMeasuringPresenter;

    private AddInfoBean mAddInfoBean;

    private LineDataSet set1;

    private ParameterBean mParameterBean;

    // 测量界面加入逻辑;
    private StoreBean mStoreBean;

    private final int MSG_AUTO_STORE = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTO_STORE:
                    handler.removeMessages(MSG_AUTO_STORE);
                    doAutoStore();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
        stopAutoStore();
        if (inValue) mMeasuringPresenter.stopMeasuing();
        */
        stopValue();
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_measuring);
    }

    @Override
    protected void initView() {
        mMeasuringPresenter = new MeasuringPresenterImpl(this);
        initChart();
        onMeasuringDataUpdate(curMValues);
        initParameters();
        if (App.getSetupBean().getIsAutoPopUp()) {
            showAddDialog();
        }
        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);
        startValue();
    }

    private void initParameters() {
        mParameterBean = mMeasuringPresenter.getParameterBean();
        if (mParameterBean != null) {
            mMValueViews[0].init(mParameterBean.getM1_nominal_value(), mParameterBean.getM1_upper_tolerance_value(), mParameterBean.getM1_lower_tolerance_value(), mParameterBean.getM1_scale());
            mMValueViews[1].init(mParameterBean.getM2_nominal_value(), mParameterBean.getM2_upper_tolerance_value(), mParameterBean.getM2_lower_tolerance_value(), mParameterBean.getM2_scale());
            mMValueViews[2].init(mParameterBean.getM3_nominal_value(), mParameterBean.getM3_upper_tolerance_value(), mParameterBean.getM3_lower_tolerance_value(), mParameterBean.getM3_scale());
            mMValueViews[3].init(mParameterBean.getM4_nominal_value(), mParameterBean.getM4_upper_tolerance_value(), mParameterBean.getM4_lower_tolerance_value(), mParameterBean.getM4_scale());

            mDescribes[0].setText(mParameterBean.getM1_describe());
            mDescribes[1].setText(mParameterBean.getM2_describe());
            mDescribes[2].setText(mParameterBean.getM3_describe());
            mDescribes[3].setText(mParameterBean.getM4_describe());

            for (int i = 0; i < 4; i++) {
                boolean isEnable = true;
                if (0 == i) {
                    isEnable = mParameterBean.getM1_enable();
                } else if (1 == i) {
                    isEnable = mParameterBean.getM2_enable();
                } else if (2 == i) {
                    isEnable = mParameterBean.getM3_enable();
                } else if (3 == i) {
                    isEnable = mParameterBean.getM4_enable();
                }
                mTValues[i].setVisibility(isEnable ? View.VISIBLE : View.INVISIBLE);
                mMValueViews[i].setVisibility(isEnable ? View.VISIBLE : View.INVISIBLE);
                mTitle[i].setVisibility(isEnable ? View.VISIBLE : View.INVISIBLE);
                mDescribes[i].setVisibility(isEnable ? View.VISIBLE : View.INVISIBLE);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                mMValueViews[i].init(0, 7, -7, 6);
                mTValues[i].setVisibility(true ? View.VISIBLE : View.INVISIBLE);
                mMValueViews[i].setVisibility(true ? View.VISIBLE : View.INVISIBLE);
                mTitle[i].setVisibility(true ? View.VISIBLE : View.INVISIBLE);
                mDescribes[i].setVisibility(true ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    @OnClick({R.id.value_btn, R.id.additional_btn, R.id.measure_save_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.value_btn:
                if (!inValue) {
                    // start 取值;
                    inValue = true;
                    mMeasuringPresenter.startMeasuing();
                    valueBtn.setText(R.string.in_value);
                    startAutoStore();
                } else {
                    // stop 取值;
                    inValue = false;
                    mMeasuringPresenter.stopMeasuing();
                    valueBtn.setText(R.string.get_value);
                    stopAutoStore();
                }
                break;
            case R.id.additional_btn:
                showAddDialog();
                break;
            case R.id.measure_save_btn:
                if (doSave()) {
                    if (App.getSetupBean().getIsAutoPopUp()) {
                        showAddDialog();
                    }
                }
                break;
        }
    }

    private void startValue() {
        // start 取值;
        if (inValue) return;
        inValue = true;
        mMeasuringPresenter.startMeasuing();
        valueBtn.setText(R.string.in_value);
        startAutoStore();
    }

    private void stopValue() {
        // stop 取值;
        if (!inValue) return;
        inValue = false;
        mMeasuringPresenter.stopMeasuing();
        valueBtn.setText(R.string.get_value);
        stopAutoStore();
    }

    private boolean doSave() {
        // 判断是否时间校验模式，如果超时，不保存并且提示;
        ForceCalibrationBeanDao _dao = App.getDaoSession().getForceCalibrationBeanDao();
        ForceCalibrationBean _bean = _dao.load(App.SETTING_ID);
        if ((_bean.getForceMode() == 1 && _bean.getUsrNum() <= 0) || (_bean.getForceMode() == 2 && System.currentTimeMillis() > _bean.getRealForceTime())) {
            showForceDialog();
            return false;
        }
        mMeasuringPresenter.saveResult(curMValues, mAddInfoBean);
        Toast.makeText(this, "测试结果保存成功.", Toast.LENGTH_SHORT).show();
        updateChartDatas();
        _bean.setUsrNum(_bean.getUsrNum() - 1);
        _dao.update(_bean);
        return true;
    }

    private void showForceDialog() {
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancel = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancel == null || sure == null) return;
        msg.setText("请校验后继续测量.");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }

    private void showAddDialog() {
        AdditionalDialog mAdditionalDialog = new AdditionalDialog(this, R.style.Translucent_NoTitle);
        mAdditionalDialog.setDialogInterface(this);
        mAdditionalDialog.show();
    }


    private void setDatas(int count, double range) {
        ArrayList<Entry> values = new ArrayList<Entry>();

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

    private List<Entry> getDatas() {
        ResultBeanDao _dao = App.getDaoSession().getResultBeanDao();
        List<ResultBean> beans = _dao.queryBuilder().orderDesc(ResultBeanDao.Properties.Id).limit(30).list();

        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int i = 0; i < beans.size(); i++) {
            float _val = 0;
            if (curMode == 1) {
                _val = (float) beans.get(beans.size() - 1 - i).getM1();
            } else if (curMode == 2) {
                _val = (float) beans.get(beans.size() - 1 - i).getM2();
            } else if (curMode == 3) {
                _val = (float) beans.get(beans.size() - 1 - i).getM3();
            } else if (curMode == 4) {
                _val = (float) beans.get(beans.size() - 1 - i).getM4();
            }
            values.add(new Entry(i + 1, _val, getResources().getDrawable(R.drawable.star)));
        }
        return values;
    }

    private void updateChartDatas() {
        //set1.setValues(getDatas());
//        chart.setData(new LineData(set1));
        set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
        List<Entry> values = getDatas();
//        Log.d("wlDebug", values.toString());
        set1.setValues(values);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMeasuringDataUpdate(double[] values) {
        curMValues = values;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateMValues(curMValues);
            }
        });
    }


    public void setMTitle(int mode) {
        switch (mode) {
            case 0:
//                mTitle[0].setText(R.string.m1);
//                mTitle[1].setText(R.string.m2);
//                mTitle[2].setText(R.string.m3);
//                mTitle[3].setText(R.string.m4);
//                mDescribes[0].setVisibility(View.VISIBLE);
//                mDescribes[1].setVisibility(View.VISIBLE);
//                mDescribes[2].setVisibility(View.VISIBLE);
                mParameterBean = mMeasuringPresenter.getParameterBean();
                if (mParameterBean != null) {
                    mMValueViews[0].init(mParameterBean.getM1_nominal_value(), mParameterBean.getM1_upper_tolerance_value(), mParameterBean.getM1_lower_tolerance_value(), mParameterBean.getM1_scale());
                    mMValueViews[1].init(mParameterBean.getM2_nominal_value(), mParameterBean.getM2_upper_tolerance_value(), mParameterBean.getM2_lower_tolerance_value(), mParameterBean.getM2_scale());
                    mMValueViews[2].init(mParameterBean.getM3_nominal_value(), mParameterBean.getM3_upper_tolerance_value(), mParameterBean.getM3_lower_tolerance_value(), mParameterBean.getM3_scale());
                    mMValueViews[3].init(mParameterBean.getM4_nominal_value(), mParameterBean.getM4_upper_tolerance_value(), mParameterBean.getM4_lower_tolerance_value(), mParameterBean.getM4_scale());
                    mDescribes[0].setText(mParameterBean.getM1_describe());
//                    mDescribes[1].setText(mParameterBean.getM2_describe());
//                    mDescribes[2].setText(mParameterBean.getM3_describe());
//                    mDescribes[3].setText(mParameterBean.getM4_describe());
                } else {
                    mMValueViews[0].init(0, 7, -7, 6);
                    mDescribes[0].setText("M1");
                }
                break;
            case 1:
                mTitle[0].setText("");
                mTitle[1].setText("");
                mTitle[2].setText("");
                mDescribes[0].setVisibility(View.INVISIBLE);
                mDescribes[1].setVisibility(View.INVISIBLE);
                mDescribes[2].setVisibility(View.INVISIBLE);
                mTitle[3].setText(R.string.m1);
                if (mParameterBean != null) {
                    mMValueViews[3].init(mParameterBean.getM1_nominal_value(), mParameterBean.getM1_upper_tolerance_value(), mParameterBean.getM1_lower_tolerance_value(), mParameterBean.getM1_scale());
                    mDescribes[3].setText(mParameterBean.getM1_describe());
                }
                break;
            case 2:
                mTitle[3].setText(R.string.m2);
                if (mParameterBean != null) {
                    mMValueViews[3].init(mParameterBean.getM2_nominal_value(), mParameterBean.getM2_upper_tolerance_value(), mParameterBean.getM2_lower_tolerance_value(), mParameterBean.getM2_scale());
                    mDescribes[3].setText(mParameterBean.getM2_describe());
                }
                break;
            case 3:
                mTitle[3].setText(R.string.m3);
                if (mParameterBean != null) {
                    mMValueViews[3].init(mParameterBean.getM3_nominal_value(), mParameterBean.getM3_upper_tolerance_value(), mParameterBean.getM3_lower_tolerance_value(), mParameterBean.getM3_scale());
                    mDescribes[3].setText(mParameterBean.getM3_describe());
                }
                break;
            case 4:
                mTitle[3].setText(R.string.m4);
                if (mParameterBean != null) {
                    mMValueViews[3].init(mParameterBean.getM4_nominal_value(), mParameterBean.getM4_upper_tolerance_value(), mParameterBean.getM4_lower_tolerance_value(), mParameterBean.getM4_scale());
                    mDescribes[3].setText(mParameterBean.getM4_describe());
                }
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
        updateChartDatas();
    }


    private int curMode = 0;

    @OnClick({R.id.swap_btn})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.swap_btn:
                /*
                if (curMode < 4) curMode++;
                else curMode = 0;
                setMode(curMode);
                */
                // new ChooseCodeDialog(this).show();
                for (int i = 0; i < 10; i++) {
                    CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) (i + 1));
                    if (_bean != null) {
                        province[i] = _bean.getName();
                    } else {
                        province[i] = "程序 " + (i + 1);
                    }
                }
                showSingleChoiceButton();
                break;
        }
    }

    private String[] province = new String[10];
    private Button btnSingleChoiceList;

    private ButtonOnClick buttonOnClick = new ButtonOnClick(1);


    private void showSingleChoiceButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择程序");
        builder.setSingleChoiceItems(province, App.getSetupBean().getCodeID() - 1, buttonOnClick);
        builder.setPositiveButton("确定", buttonOnClick);
        builder.setNegativeButton("取消", buttonOnClick);
        builder.show();
    }

    private class ButtonOnClick implements DialogInterface.OnClickListener {

        private int index; // 表示选项的索引

        public ButtonOnClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // which表示单击的按钮索引，所有的选项索引都是大于0，按钮索引都是小于0的。
            if (which >= 0) {
                //如果单击的是列表项，将当前列表项的索引保存在index中。
                //如果想单击列表项后关闭对话框，可在此处调用dialog.cancel()
                //或是用dialog.dismiss()方法。
                index = which;
            } else {
                //用户单击的是【确定】按钮
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    //显示用户选择的是第几个列表项。
                    final AlertDialog ad = new AlertDialog.Builder(
                            MeasuringActivity.this).setMessage(
                            "你选择的是" + province[index]).show();
                    //五秒钟后自动关闭。
                    SetupBean _bean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
                    _bean.setCodeID(index + 1);
                    App.getDaoSession().getSetupBeanDao().update(_bean);
                    ((MeasuringPresenterImpl) mMeasuringPresenter).initParameter();
//                    setMode(0);
//                    setMTitle(0);
                    initParameters();
                    CodeBean _CodeBean = App.getDaoSession().getCodeBeanDao().load((long) (index + 1));
                    if (_CodeBean != null) {
                        actionTips.setText(App.handlerAccout + " " + _CodeBean.getName());
                    } else {
                        actionTips.setText(App.handlerAccout + " 程序" + App.getSetupBean().getCodeID());
                    }

                    ad.dismiss();
//                    Handler hander = new Handler();
//                    Runnable runnable = new Runnable() {
//
//                        @Override
//                        public void run() {
//                            ad.dismiss();
//                        }
//                    };
//                    hander.postDelayed(runnable, 5 * 200);
                }
                //用户单击的是【取消】按钮
                else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    //Toast.makeText(SingleChoiceItemsTest.this, "你没有选择任何东西",
                    //      Toast.LENGTH_LONG);
                }
            }
        }
    }

    private void updateMValues(double[] mValues) {
        for (int i = 0; i < mTValues.length; i++) {
            mTValues[i].setText("");
        }
        String result = ((MeasuringPresenterImpl) mMeasuringPresenter).getMResults(mValues);
        mGroupMs[0].setText("结果: " + result);
        String[] group = mMeasuringPresenter.getMGroupValues(mValues);
        switch (curMode) {
            case 0:
                for (int i = 0; i < mTValues.length; i++) {
                    mTValues[i].setText(NumberUtils.get4bits(mValues[i]));
                    mMValueViews[i].setMValue(mValues[i]);
                    // mGroupMs[i].setText(group[i]);
                }
                mGroupMs[1].setText("M1分组: " + group[0]); // 显示M1分组;
                break;
            case 1:
                mTValues[3].setText(NumberUtils.get4bits(mValues[0]));
                mMValueViews[3].setMValue(mValues[0]);
                mGroupMs[1].setText("M1分组: " + group[0]);
                break;
            case 2:
                mTValues[3].setText(NumberUtils.get4bits(mValues[1]));
                mMValueViews[3].setMValue(mValues[1]);
                mGroupMs[1].setText("M2分组: " + group[1]);
                break;
            case 3:
                mTValues[3].setText(NumberUtils.get4bits(mValues[2]));
                mMValueViews[3].setMValue(mValues[2]);
                mGroupMs[1].setText("M3分组: " + group[2]);
                break;
            case 4:
                mTValues[3].setText("" + mValues[3]);
                mMValueViews[3].setMValue(mValues[3]);
                mGroupMs[1].setText("M4分组: " + group[3]);
                break;
        }
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
                xAxis.setAxisMaximum(32f);
                xAxis.setAxisMinimum(0f);
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
    }

    @Override
    public void onAdditionSet(AddInfoBean pBean) {
        mAddInfoBean = pBean;
        // 设置是否自动弹出;
        App.setSetupPopUp(mAddInfoBean.isAutoShow());
    }

    private void startAutoStore() {
        android.util.Log.d("store", mStoreBean.toString());
        if (mStoreBean.getStoreMode() == 1) {
            handler.sendEmptyMessageDelayed(MSG_AUTO_STORE, mStoreBean.getDelayTime() * 1000);
        }
    }

    private boolean doAutoStoreEnable = true;

    private void doAutoStore() {
        double mValue = curMValues[mStoreBean.getMValue()];
        if (mValue > mStoreBean.getLowLimitValue() && mValue < mStoreBean.getUpLimitValue()) {
            // Do Save;
            if (doAutoStoreEnable) {
                doAutoStoreEnable = false;
                doSave();
            }
        } else {
            doAutoStoreEnable = true;
        }
        handler.sendEmptyMessageDelayed(MSG_AUTO_STORE, mStoreBean.getDelayTime() * 1000);
    }

    private void stopAutoStore() {
        handler.removeMessages(MSG_AUTO_STORE);
    }
}
