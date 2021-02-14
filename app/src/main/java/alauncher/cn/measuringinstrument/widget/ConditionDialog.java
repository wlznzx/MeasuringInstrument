package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.view.fragment.ForceCalibrationFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConditionDialog extends Dialog {

    private Context mContext;

    /**/
    @BindView(R.id.condition_name_edt)
    public EditText conditionNameEdt;

    @BindView(R.id.upper_limit_edt)
    public EditText upperlimitEdt;

    @BindView(R.id.lower_limit_edt)
    public EditText lowerlimitEdt;

    @BindView(R.id.scale_edt)
    public EditText scaleEdt;

    @BindView(R.id.stable_time_edt)
    public EditText scaleTimeEdt;

    @BindView(R.id.is_scale_sw)
    public Switch isScaleSwitch;

    @BindView(R.id.lower_limit_layout)
    public View lowerLimitLayout;

    @BindView(R.id.upper_limit_layout)
    public View upperLimitLayout;

    @BindView(R.id.scale_layout)
    public View scaleLayout;

    @BindView(R.id.m_index_sp)
    public Spinner mIndexSP;

    @BindView(R.id.condition_dialog_title_tv)
    public TextView titleTv;

    private ForceCalibrationFragment mFragment;

    private TriggerConditionBean _bean;

    private List<ParameterBean2> mParameterBean2s;

    public List<String> steps = new ArrayList();

    public ConditionDialog(Context context, ForceCalibrationFragment pFragment) {
        super(context, R.style.Dialog);
        mContext = context;
        mFragment = pFragment;
    }

    public ConditionDialog(Context context, ForceCalibrationFragment pFragment, TriggerConditionBean pTriggerConditionBean) {
        super(context, R.style.Dialog);
        mContext = context;
        mFragment = pFragment;
        _bean = pTriggerConditionBean;
    }

    @OnClick({R.id.no, R.id.yes})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no:
                dismiss();
                break;
            case R.id.yes:
                if (doConditionAdd()) {
                    dismiss();
                    mFragment.conditionUpdate();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.condition_dialog_layout);
        ButterKnife.bind(this);

        mParameterBean2s = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .list();

        for (int i = 0; i < mParameterBean2s.size(); i++) {
            steps.add("M" + String.valueOf(mParameterBean2s.get(i).getSequenceNumber() + 1));
        }

        ArrayAdapter<String> stepAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, steps);
        mIndexSP.setAdapter(stepAdapter);

        isScaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    upperLimitLayout.setVisibility(View.GONE);
                    lowerLimitLayout.setVisibility(View.GONE);
                    scaleLayout.setVisibility(View.VISIBLE);
                } else {
                    upperLimitLayout.setVisibility(View.VISIBLE);
                    lowerLimitLayout.setVisibility(View.VISIBLE);
                    scaleLayout.setVisibility(View.GONE);
                }
            }
        });
        int _index = 1;
        List<TriggerConditionBean> _list = App.getDaoSession().getTriggerConditionBeanDao().queryBuilder().orderDesc(TriggerConditionBeanDao.Properties.Id).list();
        if (_list.size() > 0) {
            android.util.Log.d("wlDebug", "max id = " + _list.get(0).getId());
            _index = (int) (_list.get(0).getId() + 1);
        }
        conditionNameEdt.setText("条件" + _index);

        if (_bean != null) {
            conditionNameEdt.setText(_bean.getConditionName());
            upperlimitEdt.setText(String.valueOf(_bean.getUpperLimit()));
            lowerlimitEdt.setText(String.valueOf(_bean.getLowerLimit()));
            scaleEdt.setText(String.valueOf(_bean.getScale()));
            scaleTimeEdt.setText(String.valueOf(_bean.getStableTime()));
            isScaleSwitch.setChecked(_bean.getIsScale());
            // mIndexSP.setSelection(_bean.getMIndex() - 1);
            titleTv.setText(R.string.edit_condition);
        }
    }

    public interface AdditionDialogInterface {
        void onAdditionSet(AddInfoBean pBean);
    }

    /**/
    public boolean doConditionAdd() {
        String accoutStr = conditionNameEdt.getText().toString().trim();
        if (accoutStr == null || accoutStr.equals("")) {
            Toast.makeText(mContext, "条件名不能为空.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (_bean == null) {
            _bean = new TriggerConditionBean();
        }
        try {
            _bean.setConditionName(conditionNameEdt.getText().toString().trim());
            _bean.setIsScale(isScaleSwitch.isChecked());
            if (!isScaleSwitch.isChecked()) {
                _bean.setUpperLimit(Double.valueOf(upperlimitEdt.getText().toString().trim()));
            }
            if (!isScaleSwitch.isChecked()) {
                _bean.setLowerLimit(Double.valueOf(lowerlimitEdt.getText().toString().trim()));
            }
            _bean.setStableTime(Double.valueOf(scaleTimeEdt.getText().toString().trim()));
            _bean.setCodeID(App.getSetupBean().getCodeID());
            _bean.setMIndex(mParameterBean2s.get(mIndexSP.getSelectedItemPosition()).getSequenceNumber());
            if (isScaleSwitch.isChecked()) {
                _bean.setScale(Double.valueOf(scaleEdt.getText().toString().trim()));
            }
            App.getDaoSession().getTriggerConditionBeanDao().insertOrReplace(_bean);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, "输入条件有误，请检查。", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    public interface UIInterface {
        void upDateUserUI();
    }

    @Override
    public void dismiss() {
        //避免闪屏 提高用户体验
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ConditionDialog.super.dismiss();
            }
        }, 500);

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(conditionNameEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(upperlimitEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(lowerlimitEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(scaleEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(scaleTimeEdt.getWindowToken(), 0);
    }
}
