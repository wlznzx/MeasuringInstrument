package alauncher.cn.measuringinstrument.view.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.StepBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.utils.StepUtils;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CodeStepFragment extends Fragment {

    private Unbinder unbinder;

    @BindViews({R.id.step_1_m1_cb, R.id.step_1_m2_cb, R.id.step_1_m3_cb, R.id.step_1_m4_cb})
    CheckBox[] step1CheckBoxs;

    @BindViews({R.id.step_2_m1_cb, R.id.step_2_m2_cb, R.id.step_2_m3_cb, R.id.step_2_m4_cb})
    CheckBox[] step2CheckBoxs;

    @BindViews({R.id.step_3_m1_cb, R.id.step_3_m2_cb, R.id.step_3_m3_cb, R.id.step_3_m4_cb})
    CheckBox[] step3CheckBoxs;

    @BindViews({R.id.step_4_m1_cb, R.id.step_4_m2_cb, R.id.step_4_m3_cb, R.id.step_4_m4_cb})
    CheckBox[] step4CheckBoxs;

    @BindViews({R.id.step_1_m1_cb, R.id.step_2_m1_cb, R.id.step_3_m1_cb, R.id.step_4_m1_cb})
    CheckBox[] m1CheckBoxs;

    @BindViews({R.id.step_1_m2_cb, R.id.step_2_m2_cb, R.id.step_3_m2_cb, R.id.step_4_m2_cb})
    CheckBox[] m2CheckBoxs;

    @BindViews({R.id.step_1_m3_cb, R.id.step_2_m3_cb, R.id.step_3_m3_cb, R.id.step_4_m3_cb})
    CheckBox[] m3CheckBoxs;

    @BindViews({R.id.step_1_m4_cb, R.id.step_2_m4_cb, R.id.step_3_m4_cb, R.id.step_4_m4_cb})
    CheckBox[] m4CheckBoxs;


    @BindView(R.id.table1)
    public TableLayout table;

    @BindView(R.id.isAudo)
    public Switch isAutoSwitch;

    @BindViews({R.id.step_row_1, R.id.step_row_2, R.id.step_row_3, R.id.step_row_4})
    TableRow[] rows;

    @BindViews({R.id.step_1_m1_sp, R.id.step_1_m2_sp, R.id.step_1_m3_sp, R.id.step_1_m4_sp})
    public Spinner[] mSpinners;

    private int showRows = 4;

    private int CodeID;

    private StepBeanDao dao;

    private ParameterBean mParameterBean;

    public List<String> condications = new ArrayList();

    private List<TriggerConditionBean> mDatas;

    public StoreBean mStoreBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CodeID = App.getSetupBean().getCodeID();
        dao = App.getDaoSession().getStepBeanDao();
        mParameterBean = App.getDaoSession().getParameterBeanDao().load((long) App.getSetupBean().getCodeID());

        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        condications.clear();
        mDatas = App.getDaoSession().getTriggerConditionBeanDao().queryBuilder().where(TriggerConditionBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();
        condications.add(getResources().getString(R.string.press_save));
        for (TriggerConditionBean _bean : mDatas) {
            condications.add(_bean.getConditionName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, condications);

        for (int i = 0; i < mSpinners.length; i++) {
            mSpinners[i].setAdapter(adapter);
        }
        isAutoSwitch.setChecked(mStoreBean.getStoreMode() == 1);
        List<StepBean> list = dao.queryBuilder().where(StepBeanDao.Properties.CodeID.eq(CodeID)).list();
        for (StepBean _bean : list) {
            if (_bean.getStepID() == 1) {
                for (int i = 0; i < step1CheckBoxs.length; i++) {
                    if (StepUtils.getChannelByStep(i, _bean.measured))
                        step1CheckBoxs[i].setChecked(true);
                }
                for (int j = 0; j < mDatas.size(); j++) {
                    if (mDatas.get(j).getId() == _bean.getConditionID()) {
                        mSpinners[0].setSelection(j + 1);
                        break;
                    }
                }
            }
            if (_bean.getStepID() == 2) {
                for (int i = 0; i < step2CheckBoxs.length; i++) {
                    if (StepUtils.getChannelByStep(i, _bean.measured))
                        step2CheckBoxs[i].setChecked(true);
                }
                for (int j = 0; j < mDatas.size(); j++) {
                    if (mDatas.get(j).getId() == _bean.getConditionID()) {
                        mSpinners[1].setSelection(j + 1);
                        break;
                    }
                }
            }
            if (_bean.getStepID() == 3) {
                for (int i = 0; i < step3CheckBoxs.length; i++) {
                    if (StepUtils.getChannelByStep(i, _bean.measured))
                        step3CheckBoxs[i].setChecked(true);
                }
                for (int j = 0; j < mDatas.size(); j++) {
                    if (mDatas.get(j).getId() == _bean.getConditionID()) {
                        mSpinners[2].setSelection(j + 1);
                        break;
                    }
                }
            }
            if (_bean.getStepID() == 4) {
                for (int i = 0; i < step4CheckBoxs.length; i++) {
                    if (StepUtils.getChannelByStep(i, _bean.measured))
                        step4CheckBoxs[i].setChecked(true);
                }
                for (int j = 0; j < mDatas.size(); j++) {
                    if (mDatas.get(j).getId() == _bean.getConditionID()) {
                        mSpinners[3].setSelection(j + 1);
                        break;
                    }
                }
            }
        }

        if (!mParameterBean.getM1_enable()) {
            table.setColumnCollapsed(2, true);
            android.util.Log.d("wlDebug", "m1 disable.");
            showRows--;
        }
        if (!mParameterBean.getM2_enable()) {
            table.setColumnCollapsed(3, true);
            android.util.Log.d("wlDebug", "m2 disable.");
            showRows--;
        }
        if (!mParameterBean.getM3_enable()) {
            table.setColumnCollapsed(4, true);
            android.util.Log.d("wlDebug", "m3 disable.");
            showRows--;
        }
        if (!mParameterBean.getM4_enable()) {
            table.setColumnCollapsed(5, true);
            android.util.Log.d("wlDebug", "m4 disable.");
            showRows--;
        }

        for (int i = 0; i < showRows; i++) {
            rows[i].setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.save_btn, R.id.clear_btn})
    public void onSave(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                int[] step = new int[4];
                step[0] = getMeasuredByCheckoutBoxs(step1CheckBoxs);
                step[1] = getMeasuredByCheckoutBoxs(step2CheckBoxs);
                step[2] = getMeasuredByCheckoutBoxs(step3CheckBoxs);
                step[3] = getMeasuredByCheckoutBoxs(step4CheckBoxs);

                StepBeanDao dao = App.getDaoSession().getStepBeanDao();
                List<StepBean> stepBeans = dao.queryBuilder().where(StepBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();
                for (StepBean _bean : stepBeans) {
                    dao.delete(_bean);
                }
                for (int i = 0; i < showRows; i++) {
                    StepBean _bean = new StepBean();
                    _bean.setCodeID(CodeID);
                    _bean.setStepID(i + 1);
                    _bean.setMeasured(step[i]);

                    int index = mSpinners[i].getSelectedItemPosition();
                    if (index > 0) {
                        _bean.setConditionID(mDatas.get(index - 1).getId());
                    } else {
                        _bean.setConditionID(-1);
                    }
                    if (step[i] != 0) dao.insertOrReplace(_bean);
                }
                mStoreBean.setStoreMode(isAutoSwitch.isChecked() ? 1 : 0);
                App.getDaoSession().getStoreBeanDao().insertOrReplace(mStoreBean);
                Toast.makeText(getContext(), "保存成功.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear_btn:
                clearDialog();
                break;
        }
    }

    private void clearDialog() {
        final AlertDialog builder = new AlertDialog.Builder(getContext()).create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;
        msg.setText(R.string.sure_clear);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                StepBeanDao dao = App.getDaoSession().getStepBeanDao();
                List<StepBean> stepBeans = dao.queryBuilder().where(StepBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();
                for (StepBean _bean : stepBeans) {
                    dao.delete(_bean);
                }
                mStoreBean.setStoreMode(0);
                App.getDaoSession().getStoreBeanDao().insertOrReplace(mStoreBean);
                isAutoSwitch.setChecked(false);
                for (CheckBox _cb : step1CheckBoxs) {
                    _cb.setChecked(false);
                }
                for (CheckBox _cb : step2CheckBoxs) {
                    _cb.setChecked(false);
                }
                for (CheckBox _cb : step3CheckBoxs) {
                    _cb.setChecked(false);
                }
                for (CheckBox _cb : step4CheckBoxs) {
                    _cb.setChecked(false);
                }
                for (Spinner _sp : mSpinners) {
                    _sp.setSelection(0);
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            android.util.Log.d("wlDebug", "do hidden.");
            mDatas = App.getDaoSession().getTriggerConditionBeanDao().queryBuilder().where(TriggerConditionBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();
            condications.add("无");
            for (TriggerConditionBean _bean : mDatas) {
                condications.add(_bean.getConditionName());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void onTriggerConditionChanged() {
        initView();
    }

    @OnCheckedChanged({R.id.step_1_m1_cb, R.id.step_2_m1_cb, R.id.step_3_m1_cb, R.id.step_4_m1_cb})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            for (int i = 0; i < m1CheckBoxs.length; i++) {
                if (m1CheckBoxs[i].isChecked()) {
                    if (m1CheckBoxs[i].getId() != buttonView.getId()) {
                        buttonView.setChecked(false);
                    }
                }
            }
        }
    }

    @OnCheckedChanged({R.id.step_1_m2_cb, R.id.step_2_m2_cb, R.id.step_3_m2_cb, R.id.step_4_m2_cb})
    public void onCheckedChanged2(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            for (int i = 0; i < m2CheckBoxs.length; i++) {
                if (m2CheckBoxs[i].isChecked()) {
                    if (m2CheckBoxs[i].getId() != buttonView.getId()) {
                        m2CheckBoxs[i].setChecked(false);
                        buttonView.setChecked(false);
                    }
                }
            }
        }
    }

    @OnCheckedChanged({R.id.step_1_m3_cb, R.id.step_2_m3_cb, R.id.step_3_m3_cb, R.id.step_4_m3_cb})
    public void onCheckedChanged3(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            for (int i = 0; i < m3CheckBoxs.length; i++) {
                if (m3CheckBoxs[i].isChecked()) {
                    if (m3CheckBoxs[i].getId() != buttonView.getId()) {
//                        m3CheckBoxs[i].setChecked(false);
                        buttonView.setChecked(false);
                    }
                }
            }
        }
    }

    @OnCheckedChanged({R.id.step_1_m4_cb, R.id.step_2_m4_cb, R.id.step_3_m4_cb, R.id.step_4_m4_cb})
    public void onCheckedChanged4(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            for (int i = 0; i < m4CheckBoxs.length; i++) {
                if (m4CheckBoxs[i].isChecked()) {
                    if (m4CheckBoxs[i].getId() != buttonView.getId()) {
                        buttonView.setChecked(false);
                    }
                }
            }
        }
    }

    private int getMeasuredByCheckoutBoxs(CheckBox[] cbs) {
        int _value = 0;
        for (int i = 0; i < cbs.length; i++) {
            if (cbs[i].isChecked()) {
                _value = _value | (1 << i);
            }
        }
        return _value;
    }

}
