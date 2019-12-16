package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.nfunk.jep.function.Str;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.bean.User;
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

    ForceCalibrationFragment mFragment;

    TriggerConditionBean _bean;

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
            mIndexSP.setSelection(_bean.getMIndex() - 1);
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

        if (_bean == null) _bean = new TriggerConditionBean();
        try {
            _bean.setConditionName(conditionNameEdt.getText().toString().trim());
            _bean.setIsScale(isScaleSwitch.isChecked());
            if (!isScaleSwitch.isChecked())
                _bean.setUpperLimit(Double.valueOf(upperlimitEdt.getText().toString().trim()));
            if (!isScaleSwitch.isChecked())
                _bean.setLowerLimit(Double.valueOf(lowerlimitEdt.getText().toString().trim()));

            _bean.setStableTime(Integer.valueOf(scaleTimeEdt.getText().toString().trim()));
            _bean.setCodeID(App.getSetupBean().getCodeID());
            _bean.setMIndex(mIndexSP.getSelectedItemPosition() + 1);
            if (isScaleSwitch.isChecked())
                _bean.setScale(Double.valueOf(scaleEdt.getText().toString().trim()));
            App.getDaoSession().getTriggerConditionBeanDao().insertOrReplace(_bean);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, "输入条件有误，请检查。", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public interface UIInterface {
        void upDateUserUI();
    }
}
