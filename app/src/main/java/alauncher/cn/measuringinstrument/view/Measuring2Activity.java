package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StepBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.mvp.presenter.MeasuringPresenter;
import alauncher.cn.measuringinstrument.mvp.presenter.impl.MeasuringPresenterImpl2;
import alauncher.cn.measuringinstrument.view.activity_view.MeasuringActivityView;
import alauncher.cn.measuringinstrument.widget.AdditionalDialog;
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

    private double[] curMValues = {1.8, -2.8, 0.8, -0.4};

    private boolean inValue = false;

    private MeasuringPresenter mMeasuringPresenter;

    private AddInfoBean mAddInfoBean;

    private volatile boolean isUIDraw = false;

    // 测量界面加入逻辑;
    private StoreBean mStoreBean;

    private List<ParameterBean2> mDates;

    private MValueViewLandscape[] mMValueViewLandscapes;

    private MeasuringAdapter mMeasuringAdapter;

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
        mMeasuringAdapter = new MeasuringAdapter();
        measureRV.setAdapter(mMeasuringAdapter);
        initParameters();
        if (App.getSetupBean().getIsAutoPopUp()) {
            showAddDialog();
        }
        // 开始取值;
        startValue();
        updateSaveBtnMsg();
    }

    private void initParameters() {
        mDates = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
        mMValueViewLandscapes = new MValueViewLandscape[mDates.size()];
        mMeasuringAdapter.notifyDataSetChanged();
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

    private boolean doSave(boolean isManual) {
        // 判断是否时间校验模式，如果超时，不保存并且提示;
        ForceCalibrationBeanDao _dao = App.getDaoSession().getForceCalibrationBeanDao();
        ForceCalibrationBean _bean = _dao.load(App.SETTING_ID);
        if ((_bean.getForceMode() == 1 && _bean.getUsrNum() <= 0) || (_bean.getForceMode() == 2 && System.currentTimeMillis() > _bean.getRealForceTime())) {
            showForceDialog();
            return false;
        }

        String _result = mMeasuringPresenter.saveResult(curMValues, mAddInfoBean, isManual);
        // updateGetValueTips();
        if (_result.equals("NoSave")) {
            // Toast.makeText(this, "测试结果不在自动保存区间内.", Toast.LENGTH_SHORT).show();
        } else if (_result.equals("OK")) {
            Toast.makeText(this, "测试结果保存成功.", Toast.LENGTH_SHORT).show();
        }
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
        // 刷新柱状图;
        for (int i = 0; i < values.length; i++) {
            if (mMValueViewLandscapes[i] != null) {
                mMValueViewLandscapes[i].setMValue(values[i]);
            }
        }
        // 显示测量结果;
        mGroupMs[0].setText("结果: " + mMeasuringPresenter.getMResults(values));
        // 显示测量分组情况;
        String[] group = mMeasuringPresenter.getMGroupValues(values);
        mGroupMs[1].setText("M" + (mDates.get(0).getSequenceNumber() + 1) + "分组: " + group[0]);
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
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        final List<Map<String, Object>> item = getData();
        // SimpleAdapter对象，匹配ArrayList中的元素
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, item, R.layout.gridview_item, new String[]{"itemName"}, new int[]{R.id.grid_name});
        gridview.setAdapter(simpleAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
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
            if (mMeasuringPresenter.getMeasureState() == 3) {
                if (doSave(true)) {
                    if (App.getSetupBean().getIsAutoPopUp()) {
                        showAddDialog();
                    }
                }
            } else if (mMeasuringPresenter.getMeasureState() == 2) {
                if (mMeasuringPresenter.getIsStartProcessValue()) {
                    // stop 取值;
                    mMeasuringPresenter.stopGetProcessValue();
                }
            } else if (mMeasuringPresenter.getMeasureState() == 1) {
                if (!mMeasuringPresenter.getIsStartProcessValue()) {
                    // start 取值;
                    mMeasuringPresenter.startGetProcessValue();
                }
            }
        } else {
            if (doSave(true)) {
                if (App.getSetupBean().getIsAutoPopUp()) {
                    showAddDialog();
                }
            }
        }
    }

    @Override
    public void updateSaveBtnMsg() {
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
            StepBean _bean = ((MeasuringPresenterImpl2) mMeasuringPresenter).getStepBean();
            if (mMeasuringPresenter.isCurStepHaveProcess()) {
                switch (mMeasuringPresenter.getMeasureState()) {
                    case 3:
                        if (_bean != null) {
                            saveTv.setText(String.format(getResources().getString(R.string.step_tips), _bean.getStepID()));
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
                    saveTv.setText(String.format(getResources().getString(R.string.step_tips), _bean.getStepID()));
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
            return ViewHolder.createViewHolder(Measuring2Activity.this, parent, R.layout.measure_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ParameterBean2 _bean = mDates.get(position);
            holder.setText(R.id.show_item_tv, "M" + (_bean.getSequenceNumber() + 1));
            holder.setText(R.id.describe_tv, _bean.getDescribe());
            if (mMValueViewLandscapes[position] == null) {
                mMValueViewLandscapes[position] = holder.getView(R.id.m_value_view);
                mMValueViewLandscapes[position].init(_bean.getNominalValue(), _bean.getUpperToleranceValue()
                        , _bean.getLowerToleranceValue(), _bean.getResolution());
            }
        }

        @Override
        public int getItemCount() {
            return mDates.size();
        }
    }
}
