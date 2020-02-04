package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StepBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import alauncher.cn.measuringinstrument.mvp.presenter.MeasuringPresenter;
import alauncher.cn.measuringinstrument.mvp.presenter.impl.MeasuringPresenterImpl;
import alauncher.cn.measuringinstrument.utils.NumberUtils;
import alauncher.cn.measuringinstrument.view.activity_view.MeasuringActivityView;
import alauncher.cn.measuringinstrument.widget.AdditionalDialog;
import alauncher.cn.measuringinstrument.widget.MValueView;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class MeasuringActivity extends BaseOActivity implements MeasuringActivityView, AdditionalDialog.AdditionDialogInterface {

    @BindViews({R.id.m1_value, R.id.m2_value, R.id.m3_value, R.id.m4_value})
    public MValueView[] mMValueViews;

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

    @BindView(R.id.measure_save_btn)
    public TextView saveTv;

    @BindView(R.id.value_btn_layout)
    public View valueBtnLayout;

    private double[] curMValues = {1.8, -2.8, 0.8, -0.4};

    private boolean inValue = false;

    private MeasuringPresenter mMeasuringPresenter;

    private AddInfoBean mAddInfoBean;

    private ParameterBean mParameterBean;

    // 锁
    // private Lock lock = new Lock();
    private volatile boolean isUIDraw = false;

    // 测量界面加入逻辑;
    private StoreBean mStoreBean;

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
        setContentView(R.layout.activity_measuring);
    }

    @Override
    protected void initView() {
        mMeasuringPresenter = new MeasuringPresenterImpl(this);
        initParameters();
        if (App.getSetupBean().getIsAutoPopUp()) {
            showAddDialog();
        }
        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);
        // 开始取值;
        startValue();
        updateSaveBtnMsg();
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
        // updateGetValueTips();
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
        mMeasuringPresenter.startMeasuing();
    }

    private void stopValue() {
        // stop 取值;
        if (!inValue) return;
        inValue = false;
        mMeasuringPresenter.stopMeasuing();
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

    /*
    private void updateGetValueTips() {
        if (mMeasuringPresenter.getStep() == -1) {
            // saveTv.setText(R.string.save);
        } else {
            StepBean _bean = ((MeasuringPresenterImpl) mMeasuringPresenter).getStepBean();
            if (_bean != null && !mMeasuringPresenter.isCurStepHaveProcess()) {
                saveTv.setText(String.format(getResources().getString(R.string.step_tips), _bean.getStepID()));
            } else {
                updateSaveBtnMsg(mMeasuringPresenter.getMeasureState());
            }
        }
    }
    */

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMeasuringDataUpdate(double[] values) {
        if (!isUIDraw) {
            isUIDraw = true;
            // android.util.Log.d("wlDebug", "write MValues.");
            curMValues = values;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMValues(curMValues);
                    isUIDraw = false;
                }
            });
        } else {
            // android.util.Log.d("wlDebug", "ignore this values.");
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
                // showSingleChoiceButton();
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
                ((MeasuringPresenterImpl) mMeasuringPresenter).initParameter();
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


    private void updateMValues(double[] mValues) {

        long startTime = System.currentTimeMillis(); // 获取开始时间

        String result = ((MeasuringPresenterImpl) mMeasuringPresenter).getMResults(mValues);
        if (mMeasuringPresenter.getStep() == -1) {
            if (!((MeasuringPresenterImpl) mMeasuringPresenter).mGeted[0]) {
                mGroupMs[0].setText("结果: " + result);
                if (result.equals("NG")) {
                    mGroupMs[0].setBackgroundResource(R.drawable.red_shape);
                } else {
                    mGroupMs[0].setBackgroundResource(R.drawable.green_shape);
                }
            }
        } else {
            mGroupMs[0].setText("- -");
        }
        String[] group = mMeasuringPresenter.getMGroupValues(mValues);
        switch (curMode) {
            case 0:
                for (int i = 0; i < mTValues.length; i++) {
                    if (!((MeasuringPresenterImpl) mMeasuringPresenter).mGeted[i]) {
                        mTValues[i].setText(NumberUtils.get4bits(mValues[i]));
                        mMValueViews[i].setMValue(mValues[i]);
                    }
                    // mGroupMs[i].setText(group[i]);
                }
                if (!((MeasuringPresenterImpl) mMeasuringPresenter).mGeted[0])
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
        long endTime = System.currentTimeMillis(); // 获取结束时间
        long stepTime = (endTime - startTime);
        if (stepTime > 0) {
            Log.d("wlDebug", "UI绘制耗时： " + (endTime - startTime) + "ms");
        }
    }

    @Override
    public void onAdditionSet(AddInfoBean pBean) {
        mAddInfoBean = pBean;
        // 设置是否自动弹出;
        App.setSetupPopUp(mAddInfoBean.isAutoShow());
    }

    private void showNormalDialog(String title, String Message) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MeasuringActivity.this);
        normalDialog.setTitle(R.string.not_supported_same);
        normalDialog.setMessage(R.string.not_supported_same_msg);
        normalDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

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
            StepBean _bean = ((MeasuringPresenterImpl) mMeasuringPresenter).getStepBean();
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
}
