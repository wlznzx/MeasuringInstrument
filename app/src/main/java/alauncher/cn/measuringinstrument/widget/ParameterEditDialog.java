package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.GroupBean2;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.view.MGroup2Activity;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParameterEditDialog extends Dialog implements CalculateDialog.CodeInterface {

    private Context mContext;

    @BindView(R.id.parameter_dialog_title_tv)
    public TextView parameterDialogTitleTV;

    @BindView(R.id.describe_edt)
    public EditText describeEdt;

    @BindView(R.id.nominal_value_edt)
    public EditText nominalValueEdt;

    @BindView(R.id.upper_tolerance_value_edt)
    public EditText upperToleranceValueEdt;

    @BindView(R.id.lower_tolerance_value_edt)
    public EditText lowerToleranceValueEdt;

    @BindView(R.id.deviation_edt)
    public EditText deviationEdt;

    @BindView(R.id.formula_btn)
    public Button formulaBtn;

    @BindView(R.id.group_btn)
    public Button groupBtn;

    @BindView(R.id.is_enable_sw)
    public Switch isEnableSwitch;

    @BindView(R.id.resolution_sp)
    public Spinner resolutionSP;

    @BindView(R.id.show_item_sp)
    public Spinner showItemSP;

    ParameterBean2 _bean;

    DataUpdateInterface dataUpdateInterface;

    public List<ParameterBean2> mDatas;

    private boolean isAdd = true;

    public ParameterEditDialog(Context context, ParameterBean2 pParameterBean2) {
        super(context, R.style.Dialog);
        mContext = context;
        if (pParameterBean2 != null) {
            isAdd = false;
            _bean = pParameterBean2;
        }
        mDatas = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
    }

    public void setDataUpdateInterface(DataUpdateInterface pDataUpdateInterface) {
        dataUpdateInterface = pDataUpdateInterface;
    }

    @OnClick({R.id.no, R.id.yes})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no:
                dismiss();
                break;
            case R.id.yes:
                if (doConditionAdd()) {
                    if (dataUpdateInterface != null) dataUpdateInterface.dataUpdate();
                    dismiss();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_dialog_layout);
        ButterKnife.bind(this);
        if (!isAdd) parameterDialogTitleTV.setText(R.string.edit_parameter);
        formulaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalculateDialog mCalculateDialog = new CalculateDialog(getContext());
                mCalculateDialog.setCodeInterface(ParameterEditDialog.this);
                if (_bean != null) mCalculateDialog.setCode(_bean.getCode());
                mCalculateDialog.show();
            }
        });

        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MGroup2Activity.class);
                intent.putExtra("pID", _bean != null ? _bean.getId() : -1);
                getContext().startActivity(intent);
            }
        });

        resolutionSP.setSelection(6, true);
        showItemSP.setSelection(mDatas.size() >= 8 ? 7 : mDatas.size());
        if (_bean != null) {
            showItemSP.setSelection(_bean.getSequenceNumber());
            resolutionSP.setSelection(_bean.getResolution());
            describeEdt.setText(_bean.getDescribe());
            nominalValueEdt.setText(String.valueOf(_bean.getNominalValue()));
            upperToleranceValueEdt.setText(String.valueOf(_bean.getUpperToleranceValue()));
            lowerToleranceValueEdt.setText(String.valueOf(_bean.getLowerToleranceValue()));
            deviationEdt.setText(String.valueOf(_bean.getDeviation()));
            formulaBtn.setText(_bean.getCode());
            groupBtn.setText(R.string.grouping);
            isEnableSwitch.setChecked(_bean.getEnable());
        }
        // 每次进入参数设置,删除添加的数组;
        App.getDaoSession().getGroupBean2Dao().queryBuilder().where(GroupBean2Dao.Properties.PID.eq(-1)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void onCodeSet(int m, String code) {
        formulaBtn.setText(code);
    }


    public boolean doConditionAdd() {
        if (_bean == null) _bean = new ParameterBean2();
        try {
            int sequenceNumber = (int) showItemSP.getSelectedItemId();
            _bean.setResolution((int) resolutionSP.getSelectedItemId());
            _bean.setNominalValue(Double.valueOf(nominalValueEdt.getText().toString().trim()));
            _bean.setDescribe(describeEdt.getText().toString().trim());
            _bean.setUpperToleranceValue(Double.valueOf(upperToleranceValueEdt.getText().toString().trim()));
            _bean.setLowerToleranceValue(Double.valueOf(lowerToleranceValueEdt.getText().toString().trim()));
            _bean.setDeviation(Double.valueOf(deviationEdt.getText().toString().trim()));
            _bean.setEnable(isEnableSwitch.isChecked());
            _bean.setCode(formulaBtn.getText().toString());

            // 如果是新加数据;
            if (isAdd) {
                _bean.setCodeID(App.getSetupBean().getCodeID());
                if (sequenceNumber >= mDatas.size()) {
                    // 将序号设置为最后的序号;
                    _bean.setSequenceNumber(mDatas.size());
                } else {
                    // 如果插入新添加的数据;
                    for (ParameterBean2 _bean2 : mDatas) {
                        if (_bean2.getSequenceNumber() >= sequenceNumber) {
                            _bean2.setSequenceNumber(_bean2.getSequenceNumber() + 1);
                            App.getDaoSession().getParameterBean2Dao().insertOrReplace(_bean2);
                        }
                    }
                    _bean.setSequenceNumber(sequenceNumber);
                }
            } else {
                for (ParameterBean2 _bean2 : mDatas) {
                    if (_bean2.getSequenceNumber() > _bean.getSequenceNumber()) {
                        _bean2.setSequenceNumber(_bean2.getSequenceNumber() - 1);
                        App.getDaoSession().getParameterBean2Dao().insertOrReplace(_bean2);
                    }
                }
                if (sequenceNumber >= mDatas.size()) {
                    // 将序号设置为最后的序号;
                    _bean.setSequenceNumber(mDatas.size() - 1);
                } else {
                    // 如果插入新添加的数据;
                    for (ParameterBean2 _bean2 : mDatas) {
                        if (_bean2.getSequenceNumber() >= sequenceNumber) {
                            _bean2.setSequenceNumber(_bean2.getSequenceNumber() + 1);
                            App.getDaoSession().getParameterBean2Dao().insertOrReplace(_bean2);
                        }
                    }
                    _bean.setSequenceNumber(sequenceNumber);
                }
            }
            Long pID = App.getDaoSession().getParameterBean2Dao().insertOrReplace(_bean);
            if (isAdd) {
                List<GroupBean2> _bean2List = App.getDaoSession().getGroupBean2Dao().queryBuilder().where(GroupBean2Dao.Properties.PID.eq(-1)).list();
                for (GroupBean2 _bean2 : _bean2List) {
                    _bean2.setPID(pID);
                }
                App.getDaoSession().getGroupBean2Dao().updateInTx(_bean2List);
            }
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, "输入条件有误，请检查。", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

}
