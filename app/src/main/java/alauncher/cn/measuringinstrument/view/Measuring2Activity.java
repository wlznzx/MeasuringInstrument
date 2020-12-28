package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.MeasureConfigurationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StepBean2;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBean2Dao;
import alauncher.cn.measuringinstrument.mvp.presenter.MeasuringPresenter;
import alauncher.cn.measuringinstrument.mvp.presenter.impl.MeasuringPresenterImpl2;
import alauncher.cn.measuringinstrument.utils.Arith;
import alauncher.cn.measuringinstrument.utils.Constants;
import alauncher.cn.measuringinstrument.view.activity_view.MeasuringActivityView;
import alauncher.cn.measuringinstrument.widget.AdditionalDialog;
import alauncher.cn.measuringinstrument.widget.CustomMarkerView;
import alauncher.cn.measuringinstrument.widget.MValueViewLandscape;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;


public class Measuring2Activity extends BaseOActivity implements MeasuringActivityView, AdditionalDialog.AdditionDialogInterface {

    @BindView(R.id.value_btn)
    public TextView valueBtn;

    @BindViews({R.id.group_m1, R.id.group_m2, R.id.group_m3, R.id.group_m4})
    public TextView mGroupMs[];

    @BindView(R.id.measure_save_btn)
    public TextView saveTv;

    @BindView(R.id.value_btn_layout)
    public View valueBtnLayout;

    @BindView(R.id.measure_rv)
    public RecyclerView measureRV;

    @BindView(R.id.r_chart)
    public LineChart chart;

    @BindView(R.id.chartTitle)
    public TextView title;

    private double[] curMValues;

    private boolean inValue = false;

    private MeasuringPresenter mMeasuringPresenter;

    private AddInfoBean mAddInfoBean;

    private volatile boolean isUIDraw = false;

    // 测量界面加入逻辑;
    private StoreBean mStoreBean;

    private List<ParameterBean2> mDates;

    private MeasureConfigurationBean mMeasureConfigurationBean;

    private MeasuringAdapter mMeasuringAdapter;

    // 横显模式1的水平光柱;
    private MValueViewLandscape[] mMValueViewLandscapes;

    // 横显模式2，数值, 分组显示；
    private TextView[] mValueTV;
    private TextView[] mValueGroupTV;

    private int chartIndex = 0;

    // 列表标题;
    @BindView(R.id.measure_item_title)
    public View modeTitle;

    @BindView(R.id.measure_item_mode2_title)
    public View mode2Title;


    private final int HORIZONTAL_MODE_ONE = 0;

    private final int HORIZONTAL_MODE_TWO = 1;

    protected Typeface tfRegular;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopValue();
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_measuring2);
    }

    @Override
    protected void initView() {
        mMeasuringPresenter = new MeasuringPresenterImpl2(this);
        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        measureRV.setLayoutManager(layoutManager);
        if (App.getSetupBean().getIsAutoPopUp()) {
            showAddDialog();
        }
        chart.setBorderColor(Color.BLACK);
        chart.setBackgroundColor(Color.BLACK);
        chart.setDrawBorders(false);
        // disable description text
        chart.getDescription().setEnabled(false);
        // enable touch gestures
        chart.setTouchEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        // chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // force pinch zoom along both axis
        chart.setPinchZoom(true);
        chart.animateX(1500);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.WHITE);

        }
        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();
            // disable dual axis (only use LEFT axis)
            yAxis.setTextColor(Color.WHITE);
        }
        chart.clear();
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
        chart.setMarkerView(mv);
        initParameters();
        // 开始取值;
        startValue();
        updateSaveBtnMsg();
    }

    private void initParameters() {
        chartIndex = 0;
        mDates = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
        mMeasureConfigurationBean = App.getDaoSession().getMeasureConfigurationBeanDao().load((long) App.getSetupBean().getCodeID());
        mMValueViewLandscapes = new MValueViewLandscape[mDates.size()];
        mValueTV = new TextView[mDates.size()];
        mValueGroupTV = new TextView[mDates.size()];
        mMeasuringAdapter = new MeasuringAdapter();
        measureRV.setAdapter(mMeasuringAdapter);
        mMeasuringAdapter.notifyDataSetChanged();


        if (mMeasureConfigurationBean.getMeasureMode() == HORIZONTAL_MODE_ONE) {
            modeTitle.setVisibility(View.VISIBLE);
            mode2Title.setVisibility(View.GONE);
        } else if (mMeasureConfigurationBean.getMeasureMode() == HORIZONTAL_MODE_TWO) {
            modeTitle.setVisibility(View.GONE);
            mode2Title.setVisibility(View.VISIBLE);
        }
        if (mMeasureConfigurationBean.getIsShowChart()) {
            chart.setVisibility(View.VISIBLE);
        } else {
            chart.setVisibility(View.GONE);
        }
        if (mMeasureConfigurationBean.getIsShowChart()) new SPCTask().execute();
    }

    @OnClick({R.id.value_btn, R.id.additional_btn, R.id.measure_save_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.value_btn:
                if (!mMeasuringPresenter.getIsStartProcessValue()) {
                    // start 取值;
                    mMeasuringPresenter.startGetProcessValue();
                    valueBtn.setText(R.string.stop_get_value);
                } else {
                    // stop 取值;
                    mMeasuringPresenter.stopGetProcessValue();
                    valueBtn.setText(R.string.start_get_value);
                }
                break;
            case R.id.additional_btn:
                showAddDialog();
                break;
            case R.id.measure_save_btn:
                handlerSaveBtnClick();
                break;
        }
    }

    private void startValue() {
        // start 取值;
        if (inValue) return;
        inValue = true;
        mMeasuringPresenter.startMeasuring();
    }

    private void stopValue() {
        // stop 取值;
        if (!inValue) return;
        inValue = false;
        mMeasuringPresenter.stopMeasuring();
    }

    /*用来关闭上个提示框*/
    public void Toastxiaoxi(String xx) {
        if (toast != null) {
            toast.cancel();//注销之前显示的那条信息
            toast = null;//这里要注意上一步相当于隐藏了信息，mtoast并没有为空，我们强制是他为空
        }
        if (toast == null) {
            toast = Toast.makeText(this, xx, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private boolean doSave(boolean isManual) {

        // 判断是否时间校验模式，如果超时，不保存并且提示;
        /*
        ForceCalibrationBeanDao _dao = App.getDaoSession().getForceCalibrationBeanDao();
        ForceCalibrationBean _bean = _dao.load((long) App.getSetupBean().getCodeID());
        if (_bean != null && ((_bean.getForceMode() == 1 && _bean.getUsrNum() <= 0) || (_bean.getForceMode() == 2 && System.currentTimeMillis() > _bean.getRealForceTime()))) {
            ((MeasuringPresenterImpl2) mMeasuringPresenter).stopAutoStore();
            showForceDialog();
            return false;
        }
         */

        String _result = mMeasuringPresenter.saveResult(curMValues, mAddInfoBean, isManual);
        if (_result.equals("NoSave")) {
            // Toast.makeText(this, "测试结果不在自动保存区间内.", Toast.LENGTH_SHORT).show();
            //getResources().getString(R.string.step_tips)  取当前步骤

            return false;
        } else if (_result.equals("OK")) {

            Toastxiaoxi("测试结果保存成功");
            /*Toast.makeText(this, "测试结果保存成功." , Toast.LENGTH_SHORT).show();*/
            if (App.getSetupBean().getIsAutoPopUp()) {
                showAddDialog();
            }
        }
        /*
        _bean.setUsrNum(_bean.getUsrNum() - 1);
        _dao.update(_bean);
         */

        return true;
    }

    AlertDialog builder;

    public void showForceDialog() {
        if (builder == null) {
            builder = new AlertDialog.Builder(this)
                    .create();
        }
        if (builder.isShowing()) return;
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = builder.findViewById(R.id.tv_msg);
        Button cancel = builder.findViewById(R.id.btn_cancle);
        Button sure = builder.findViewById(R.id.btn_sure);
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
                finish();
                builder.dismiss();
            }
        });
    }

    private void showAddDialog() {
        AdditionalDialog mAdditionalDialog = new AdditionalDialog(this, R.style.Translucent_NoTitle);
        mAdditionalDialog.setDialogInterface(this);
        mAdditionalDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMeasuringDataUpdate(double[] values) {
        if (!isUIDraw) {
            isUIDraw = true;
            curMValues = values;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMValues(curMValues);
                    isUIDraw = false;
                }
            });
        } else {
        }
    }

    public void updateMValues(double[] values) {
        if (values == null) return;
        // 显示测量结果;
        mGroupMs[0].setText("结果: " + mMeasuringPresenter.getMResults(values));
        // 显示测量分组情况;
        String[] group = mMeasuringPresenter.getMGroupValues(values);
        if (group == null) return;
        mGroupMs[1].setText("M" + (mDates.get(0).getSequenceNumber() + 1) + "分组: " + group[0]);
        // 刷新柱状图;
        String[] results = mMeasuringPresenter.getResults(values);
        for (int i = 0; i < values.length; i++) {
            // if (mMeasuringPresenter.getGeted()[i]) continue;
            if (mMeasureConfigurationBean.getMeasureMode() == HORIZONTAL_MODE_ONE) {
                if (mMValueViewLandscapes[i] != null) {
                    mMValueViewLandscapes[i].setMValue(values[i]);
                }
            } else if (mMeasureConfigurationBean.getMeasureMode() == HORIZONTAL_MODE_TWO) {
                if (mValueTV[i] != null) {
                    mValueTV[i].setText(Arith.double2Str(values[i]));
                }
                if (mValueGroupTV[i] != null) {
                    mValueGroupTV[i].setText(results[i]);
                    mValueGroupTV[i].setBackgroundResource(results[i].equals("OK") ? R.drawable.measure_item_ok : R.drawable.measure_item_ng);
                }
            }
        }
    }

    @Override
    public void showUnSupportDialog(int index) {
        if (index < 0) {
            showNormalDialog("", getResources().getString(R.string.not_supported_same_msg));
        } else {
            showNormalDialog("", String.format(getResources().getString(R.string.not_supported_same_msg_by_step), index));
        }
    }

    @Override
    public void setValueBtnVisible(boolean isVisible) {
        valueBtnLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.swap_btn})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.swap_btn:
                for (int i = 0; i < 10; i++) {
                    CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) (i + 1));
                    if (_bean != null) {
                        province[i] = _bean.getName();
                    } else {
                        province[i] = "程序 " + (i + 1);
                    }
                }
                showGridDialog();
                break;
        }
    }

    private String[] province = new String[10];

    private void showGridDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.gridview_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(this, R.style.common_dialog);
        dialog.setContentView(view);
        dialog.show();
        GridView gridview = view.findViewById(R.id.gridview);
        final List<Map<String, Object>> item = getData();
        // SimpleAdapter对象，匹配ArrayList中的元素
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, item, R.layout.gridview_item, new String[]{"itemName"}, new int[]{R.id.grid_name});
        gridview.setAdapter(simpleAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mMeasuringPresenter.setPause(true);
                SetupBean _bean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
                _bean.setCodeID(arg2 + 1);
                App.getDaoSession().getSetupBeanDao().update(_bean);
                ((MeasuringPresenterImpl2) mMeasuringPresenter).initParameter();
                initParameters();
                CodeBean _CodeBean = App.getDaoSession().getCodeBeanDao().load((long) (arg2 + 1));
                if (_CodeBean != null) {
                    actionTips.setText(App.handlerAccout + " " + _CodeBean.getName());
                } else {
                    actionTips.setText(App.handlerAccout + " 程序" + App.getSetupBean().getCodeID());
                }
                updateSaveBtnMsg();
                dialog.dismiss();
                mMeasuringPresenter.setPause(false);
            }
        });
    }

    /**
     * 将数据ArrayList中
     *
     * @return
     */
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < province.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("itemName", province[i]);
            items.add(item);
        }
        return items;
    }

    @Override
    public void onAdditionSet(AddInfoBean pBean) {
        mAddInfoBean = pBean;
        // 设置是否自动弹出;
        App.setSetupPopUp(mAddInfoBean.isAutoShow());
    }

    private void showNormalDialog(String title, String Message) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(Measuring2Activity.this);
        normalDialog.setTitle(R.string.not_supported_same);
        normalDialog.setMessage(R.string.not_supported_same_msg);
        normalDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        /*
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }

                });

         */
        normalDialog.setCancelable(false);
        normalDialog.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_F1 && event.getAction() == KeyEvent.ACTION_UP) {
            handlerSaveBtnClick();
        }
        return super.dispatchKeyEvent(event);
    }

    private void handlerSaveBtnClick() {
        // 1. 如果当前步骤是过程值，且还没取值，显示开始取值;

        // 2. 如果当前步骤是过程值，且取值中，显示停止取值;

        // 3. 如果当前步骤是过程值，且已经取值完毕，显示保存；

        // 4. 如果当前是单步，那么就是单步；
        if (mMeasuringPresenter.isCurStepHaveProcess()) {


            if (mMeasuringPresenter.getMeasureState() == MeasuringPresenter.IN_PROCESS_VALUE_BEEN_TAKEN_MODE) {
                // android.util.Log.d("wlDebug", "do 1.");
                doSave(true);
            } else if (mMeasuringPresenter.getMeasureState() == MeasuringPresenter.IN_PROCESS_VALUE_MODE) {
                // android.util.Log.d("wlDebug", "do 2.");
                if (mMeasuringPresenter.getIsStartProcessValue()) {
                    // stop 取值;
                    mMeasuringPresenter.stopGetProcessValue();
                }
            } else if (mMeasuringPresenter.getMeasureState() == MeasuringPresenter.NORMAL_NODE) {
                // android.util.Log.d("wlDebug", "do 3.");
                if (!mMeasuringPresenter.getIsStartProcessValue()) {
                    // start 取值;
                    mMeasuringPresenter.startGetProcessValue();
                }
            }
        } else {

            Toastxiaoxi(saveTv.getText() + "成功！");
            /*Toast.makeText(this, ""+saveTv.getText()+"成功！" , Toast.LENGTH_SHORT).show();*/
            doSave(true);
        }
    }

    @Override
    public void updateSaveBtnMsg() {
        if (mMeasuringPresenter == null) return;
        // 1. 首先判断是否分步？
        if (mMeasuringPresenter.isSingleStep()) {
            if (mMeasuringPresenter.isCurStepHaveProcess()) {
                switch (mMeasuringPresenter.getMeasureState()) {
                    case 3:
                        saveTv.setText(R.string.save);
                        break;
                    case 2:
                        saveTv.setText(R.string.stop_get_value);
                        break;
                    case 1:
                        saveTv.setText(R.string.start_get_value);
                        break;
                }
            } else {
                saveTv.setText(R.string.save);
            }
        } else {
            StepBean2 _bean = ((MeasuringPresenterImpl2) mMeasuringPresenter).getStepBean();
            if (mMeasuringPresenter.isCurStepHaveProcess()) {
                switch (mMeasuringPresenter.getMeasureState()) {
                    case 3:
                        if (_bean != null) {
                            saveTv.setText(String.format(getResources().getString(R.string.step_tips), _bean.getSequenceNumber() + 1));
                        }
                        break;
                    case 2:
                        saveTv.setText(R.string.stop_get_value);
                        break;
                    case 1:
                        saveTv.setText(R.string.start_get_value);
                        break;
                }
            } else {
                if (_bean != null) {
                    saveTv.setText(String.format(getResources().getString(R.string.step_tips), _bean.getSequenceNumber() + 1));
                }
            }
        }
    }

    @Override
    public void toDoSave(boolean isManual) {
        doSave(isManual);
    }


    class MeasuringAdapter extends RecyclerView.Adapter<ViewHolder> {

        public MeasuringAdapter() {

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (mMeasureConfigurationBean.getMeasureMode() == HORIZONTAL_MODE_ONE) {
                return ViewHolder.createViewHolder(Measuring2Activity.this, parent, R.layout.measure_item);
            } else if (mMeasureConfigurationBean.getMeasureMode() == HORIZONTAL_MODE_TWO) {
                return ViewHolder.createViewHolder(Measuring2Activity.this, parent, R.layout.measure_item_mode2);
            }
            return ViewHolder.createViewHolder(Measuring2Activity.this, parent, R.layout.measure_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ParameterBean2 _bean = mDates.get(position);
            holder.setIsRecyclable(false);
            holder.setText(R.id.show_item_tv, "M" + (_bean.getSequenceNumber() + 1));
            holder.setText(R.id.describe_tv, _bean.getDescribe());
            /* title.setText("M" + (_bean.getSequenceNumber() + 1)+"趋势图");*/
            if (mMeasureConfigurationBean.getMeasureMode() == HORIZONTAL_MODE_ONE) {
                if (mMValueViewLandscapes[position] == null) {
                    mMValueViewLandscapes[position] = holder.getView(R.id.m_value_view);
                    mMValueViewLandscapes[position].init(_bean.getNominalValue(), _bean.getUpperToleranceValue()
                            , _bean.getLowerToleranceValue(), _bean.getResolution());
                }
            } else if (mMeasureConfigurationBean.getMeasureMode() == HORIZONTAL_MODE_TWO) {
                if (mValueTV[position] == null) {
                    mValueTV[position] = holder.getView(R.id.value_tv);
                }
                if (mValueGroupTV[position] == null) {
                    mValueGroupTV[position] = holder.getView(R.id.value_group_tv);
                }
            }

            holder.setOnClickListener(R.id.measure_item, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tasking) return;
                    chartIndex = position;
                    if (mMeasureConfigurationBean.getIsShowChart()) new SPCTask().execute();
                    // android.util.Log.d("wlDebug", "chartIndex = " + chartIndex);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDates.size();
        }
    }

    private boolean tasking = false;

    class SPCTask extends AsyncTask<String, Integer, Object> {

        private ProgressDialog dialog;

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            tasking = true;
            dialog = new ProgressDialog(Measuring2Activity.this);
            dialog.setTitle(R.string.loading);
            dialog.setMessage(getResources().getString(R.string.loading_chart));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

        }

        //第二个执行方法,在onPreExecute()后执行，用于后台任务,不可在此方法内修改UI
        @Override
        protected Object doInBackground(String... params) {
            //处理耗时操作
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<ResultBean2> _datas = getResultBean2s();
            /*  title.setText(ParameterBean2Dao.Properties.SequenceNumber+"");*/
            if (_datas == null) return null;
            return ybyxtDatas(_datas);
        }

        /*这个函数在doInBackground调用publishProgress(int i)时触发，虽然调用时只有一个参数
         但是这里取到的是一个数组,所以要用progesss[0]来取值
         第n个参数就用progress[n]来取值   */
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        /*上、下公差获取值*/
        private LimitLine getLimitLine(float value, String str) {
            LimitLine ll1 = new LimitLine(value, str);
            ll1.setLineWidth(2f);
            ll1.setLineColor(Color.RED);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
//            ll1.setTypeface(tfRegular);
            return ll1;
        }


        /*doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
        这里的result就是上面doInBackground执行后的返回值，所以这里是"后台任务执行完毕"  */
        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {

                chart.clear();
                YBYXTBean _bean = (YBYXTBean) result;
                YAxis yAxis = chart.getAxisLeft();
                yAxis.setAxisMaximum(_bean.maxY);
                yAxis.setAxisMinimum(_bean.minY);
                yAxis.removeAllLimitLines();
                yAxis.addLimitLine(getLimitLine(_bean.ucl, "上公差线"));
                yAxis.addLimitLine(getLimitLine(_bean.lcl, "下公差线"));
                XAxis xAxis = chart.getXAxis();
                xAxis.setAxisMinimum(0);
                if (_bean.mValues.size() < 10) xAxis.setAxisMaximum(10);
                if (10 <= _bean.mValues.size() && _bean.mValues.size() < 20)
                    xAxis.setAxisMaximum(30);
                if (_bean.mValues.size() > 20) xAxis.setAxisMaximum(30);
                if (_bean.mValues.size() > 0) {
                    updateChartDatas(_bean.mValues);
                } else {
                    chart.clear();
                }
            } else {
                chart.clear();
            }
            tasking = false;
            dialog.dismiss();
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            chart.clear();
            tasking = false;
        }
    }

    private YBYXTBean ybyxtDatas(List<ResultBean2> _datas) {
        YBYXTBean _bean = new YBYXTBean();
        ArrayList<Entry> values = new ArrayList<Entry>();
        Collections.reverse(_datas);
        double[] _values = new double[_datas.size()];
        double _max = -10000, _min = 10000;
        for (int i = 0; i < _datas.size(); i++) {
            _values[i] = getValuesFromResultBean2(_datas.get(i), chartIndex);
            if (_values[i] > _max) _max = _values[i];
            if (_values[i] < _min) _min = _values[i];
            values.add(new Entry(i + 1, (float) _values[i], getResources().getDrawable(R.drawable.star)));
        }
        _bean.mValues = values;
        double _r = new Max().evaluate(_values) - new Min().evaluate(_values);
        double xbar = new Mean().evaluate(_values);
        double _tolerance = 0.05;
        if (mDates.size() > 0) {
            ParameterBean2 _bean2 = mDates.get(chartIndex);
            _bean.ucl = (float) (_bean2.getNominalValue() + _bean2.getUpperToleranceValue());
            _bean.lcl = (float) (_bean2.getNominalValue() + _bean2.getLowerToleranceValue());
            _tolerance = Math.abs(_bean2.getUpperToleranceValue() - _bean2.getLowerToleranceValue());
            // android.util.Log.d("wlDebug", " --- " + _bean2.toString());
        } else {
            _bean.ucl = (float) (xbar + Constants.A2[0] * _r);
            _bean.lcl = (float) (xbar - Constants.A2[0] * _r);
        }

        // android.util.Log.d("wlDebug", " _bean.ucl = " + _bean.ucl);
        // android.util.Log.d("wlDebug", " _bean.lcl = " + _bean.lcl);
        _bean.maxY = (float) (_bean.ucl + Math.abs(_bean.ucl - xbar) * 0.5);
        _bean.minY = (float) (_bean.lcl - Math.abs(xbar - _bean.lcl) * 0.5);

        if (_max > _bean.maxY) {
            _bean.maxY = (float) (_max + (_tolerance / 10));
        }

        if (_min < _bean.minY) {
            _bean.minY = (float) (_min - (_tolerance / 10));
        }
        android.util.Log.d("wlDebug", " --- " + _bean.toString());
        return _bean;
    }

    private double getValuesFromResultBean2(ResultBean2 _bean, int index) {
        double result = 0;
        try {
            result = Double.parseDouble(_bean.getMeasurementValues().get(index));
            //测量界面趋势图标题
            index = index + 1;
            title.setText("M" + index + "趋势图");
        } catch (Exception e) {

        }
        return result;
    }

    private List<ResultBean2> getResultBean2s() {
        return App.getDaoSession().getResultBean2Dao().queryBuilder()
                .where(ResultBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderDesc(ResultBean2Dao.Properties.Id).limit(30).list();
    }

    private void updateChartDatas(List<Entry> values) {
        if (chart.getData() == null) {
            LineDataSet dataSet = new LineDataSet(values, "Label");
            initLineDataSet(dataSet);

            // create a data object with the data sets
            LineData data = new LineData(dataSet);
            chart.setData(data);
            //图上显示所有数据点的值
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setDrawValues(true);

        } else {
            LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
        }
//        chart.getData().notifyDataChanged();
//        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private ArrayList<Entry> getDemoValues() {
        ArrayList<Entry> values = new ArrayList<>();
        return values;
    }

    private void initLineDataSet(LineDataSet set1) {

        // create a dataset and give it a type
        set1.setDrawIcons(false);
        // draw dashed line 设置虚线;
        // set1.enableDashedLine(10f, 5f, 0f);

        set1.setDrawValues(true);
        //设置折线图每个数值显示

        // black lines and points
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);

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
            Drawable drawable = ContextCompat.getDrawable(Measuring2Activity.this, R.drawable.fade_red);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.BLACK);
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
}

