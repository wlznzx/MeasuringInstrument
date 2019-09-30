package alauncher.cn.measuringinstrument.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.StepBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBeanDao;
import alauncher.cn.measuringinstrument.utils.StepUtils;
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

    int CodeID;
    private StepBeanDao dao;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CodeID = App.getSetupBean().getCodeID();
        dao = App.getDaoSession().getStepBeanDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        unbinder = ButterKnife.bind(this, view);

        List<StepBean> list = dao.queryBuilder().where(StepBeanDao.Properties.CodeID.eq(CodeID)).list();
        for (StepBean _bean : list) {
            if (_bean.getStepID() == 1) {
                for (int i = 0; i < step1CheckBoxs.length; i++) {
                    if (StepUtils.getChannelByStep(i, _bean.measured))
                        step1CheckBoxs[i].setChecked(true);
                }
            }
            if (_bean.getStepID() == 2) {
                for (int i = 0; i < step2CheckBoxs.length; i++) {
                    if (StepUtils.getChannelByStep(i, _bean.measured))
                        step2CheckBoxs[i].setChecked(true);
                }
            }
            if (_bean.getStepID() == 3) {
                for (int i = 0; i < step3CheckBoxs.length; i++) {
                    if (StepUtils.getChannelByStep(i, _bean.measured))
                        step3CheckBoxs[i].setChecked(true);
                }
            }
            if (_bean.getStepID() == 4) {
                for (int i = 0; i < step4CheckBoxs.length; i++) {
                    if (StepUtils.getChannelByStep(i, _bean.measured))
                        step4CheckBoxs[i].setChecked(true);
                }
            }
        }
        return view;
    }

    @OnClick(R.id.save_btn)
    public void onSave(View v) {
        StepBeanDao dao = App.getDaoSession().getStepBeanDao();
        StepBean _bean = new StepBean();
        _bean.setCodeID(CodeID);
        _bean.setStepID(1);
        _bean.setMeasured(getMeasuredByCheckoutBoxs(step1CheckBoxs));
        dao.insertOrReplace(_bean);


        StepBean _bean2= new StepBean();
        _bean2.setCodeID(CodeID);
        _bean2.setStepID(2);
        _bean2.setMeasured(getMeasuredByCheckoutBoxs(step2CheckBoxs));
        dao.insertOrReplace(_bean2);

        StepBean _bean3 = new StepBean();
        _bean3.setCodeID(CodeID);
        _bean3.setStepID(3);
        _bean3.setMeasured(getMeasuredByCheckoutBoxs(step3CheckBoxs));
        dao.insertOrReplace(_bean3);

        StepBean _bean4 = new StepBean();
        _bean4.setCodeID(CodeID);
        _bean4.setStepID(4);
        _bean4.setMeasured(getMeasuredByCheckoutBoxs(step4CheckBoxs));
        dao.insertOrReplace(_bean4);
        Toast.makeText(getContext(), "保存成功.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnCheckedChanged({R.id.step_1_m1_cb, R.id.step_2_m1_cb, R.id.step_3_m1_cb, R.id.step_4_m1_cb})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            for (int i = 0; i < m1CheckBoxs.length; i++) {
                if (m1CheckBoxs[i].isChecked()) {
                    if (m1CheckBoxs[i].getId() != buttonView.getId()) {
                        m1CheckBoxs[i].setChecked(false);
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
                        m3CheckBoxs[i].setChecked(false);
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
                        m4CheckBoxs[i].setChecked(false);
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
