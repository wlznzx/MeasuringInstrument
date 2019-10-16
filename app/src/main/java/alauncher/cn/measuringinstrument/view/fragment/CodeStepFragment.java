package alauncher.cn.measuringinstrument.view.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.StepBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBeanDao;
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

    @BindViews({R.id.step_row_1, R.id.step_row_2, R.id.step_row_3, R.id.step_row_4})
    TableRow[] rows;

    private int showRows = 4;

    int CodeID;
    private StepBeanDao dao;

    private ParameterBean mParameterBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CodeID = App.getSetupBean().getCodeID();
        dao = App.getDaoSession().getStepBeanDao();
        mParameterBean = App.getDaoSession().getParameterBeanDao().load((long) App.getSetupBean().getCodeID());
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

        if (!mParameterBean.getM1_enable()) {
            table.setColumnCollapsed(1, true);
            showRows--;
        }
        if (!mParameterBean.getM2_enable()) {
            table.setColumnCollapsed(2, true);
            showRows--;
        }
        if (!mParameterBean.getM3_enable()) {
            table.setColumnCollapsed(3, true);
            showRows--;
        }
        if (!mParameterBean.getM4_enable()) {
            table.setColumnCollapsed(4, true);
            showRows--;
        }
        for (int i = 0; i < showRows; i++) {
            rows[i].setVisibility(View.VISIBLE);
        }
        return view;
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
                    if (step[i] != 0) dao.insertOrReplace(_bean);
                }
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
            }
        });
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
                        // m1CheckBoxs[i].setChecked(false);
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
//                        m4CheckBoxs[i].setChecked(false);
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
