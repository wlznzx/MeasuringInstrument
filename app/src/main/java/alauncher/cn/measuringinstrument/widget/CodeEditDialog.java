package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.AnalysisPatternBean;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.GroupBean2;
import alauncher.cn.measuringinstrument.bean.MeasureConfigurationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.StoreBean2;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBean2Dao;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class CodeEditDialog extends Dialog {

    private Context mContext;

    private DataUpdateInterface dataUpdateInterface;

    private CodeBean mCodeBean;

    @BindView(R.id.code_name_edt)
    public EditText codeNameEdt;

    @BindView(R.id.add_code_tips_tv)
    public View addCodeTipsTV;

    public CodeEditDialog(Context context, CodeBean pCodeBean) {
        super(context, R.style.Dialog);
        mContext = context;
        mCodeBean = pCodeBean;
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
                if (doCodeAdd()) {
                    if (dataUpdateInterface != null) {
                        dataUpdateInterface.dataUpdate();
                    }
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
        setContentView(R.layout.code_edit_dialog_layout);
        ButterKnife.bind(this);
    }

    public boolean doCodeAdd() {
        if (codeNameEdt.getText().toString().trim().equals("")) {
            addCodeTipsTV.setVisibility(View.VISIBLE);
            return false;
        }
        boolean isAdd = false;
        if (mCodeBean == null) {
            String _name = codeNameEdt.getText().toString().trim();
            mCodeBean = new CodeBean();
            mCodeBean.setMachineTool(getContext().getResources().getString(R.string.machine_tool));
            mCodeBean.setParts(getContext().getResources().getString(R.string.spare_parts));
            mCodeBean.setName(_name);
            isAdd = true;
        }
        long id = -1;
        // 创建新程序，需要初始化;
        if (isAdd) {
            id = App.getDaoSession().getCodeBeanDao().insert(mCodeBean);
            addCodeExtra((int) id);
        } else {
            id = App.getDaoSession().getCodeBeanDao().insertOrReplace(mCodeBean);
        }
        android.util.Log.d("alauncher", "doCodeAdd = " + isAdd + " id = " + id);
        return true;
    }


    private void addCodeExtra(int i) {
        // 初始化自动保存上下限;
        if (App.getDaoSession().getCalibrationBeanDao().load((long) i) == null) {
            CalibrationBean _bean = new CalibrationBean();
            _bean.setCode_id(i);
            if (1 == i) {
                _bean.setCh1BigPartStandard(30.059);
                _bean.setCh1SmallPartStandard(30.0168);
                _bean.setCh2BigPartStandard(30.059);
                _bean.setCh2SmallPartStandard(30.0168);
                _bean.setCh3BigPartStandard(30.059);
                _bean.setCh3SmallPartStandard(30.0168);
                _bean.setCh4BigPartStandard(30.059);
                _bean.setCh4SmallPartStandard(30.0168);
            } else {
                _bean.setCh1BigPartStandard(30.04);
                _bean.setCh1SmallPartStandard(30);
                _bean.setCh2BigPartStandard(30.04);
                _bean.setCh2SmallPartStandard(30);
                _bean.setCh3BigPartStandard(30.04);
                _bean.setCh3SmallPartStandard(30);
                _bean.setCh4BigPartStandard(30.04);
                _bean.setCh4SmallPartStandard(30);
            }
            _bean.setCh1CalibrationType(1);
            _bean.setCh1CompensationValue(0.007);
            _bean.setCh1KValue(0.01);

            _bean.setCh2BigPartStandard(30.059);
            _bean.setCh2SmallPartStandard(30.0168);
            _bean.setCh2CalibrationType(1);
            _bean.setCh2CompensationValue(0.007);
            _bean.setCh2KValue(0.01);

            _bean.setCh3BigPartStandard(30.059);
            _bean.setCh3SmallPartStandard(30.0168);
            _bean.setCh3CalibrationType(1);
            _bean.setCh3CompensationValue(0.007);
            _bean.setCh3KValue(0.01);

            _bean.setCh4BigPartStandard(30.059);
            _bean.setCh4SmallPartStandard(30.0168);
            _bean.setCh4CalibrationType(1);
            _bean.setCh4CompensationValue(0.007);
            _bean.setCh4KValue(0.01);
            App.getDaoSession().getCalibrationBeanDao().insert(_bean);
        }

        if (App.getDaoSession().getForceCalibrationBeanDao().load((long) i) == null) {
            ForceCalibrationBean _bean = new ForceCalibrationBean();
            _bean.set_id(i);
            _bean.setForceMode(0);
            _bean.setForceNum(50);
            _bean.setForceTime(60);
            App.getDaoSession().getForceCalibrationBeanDao().insert(_bean);
        }

        if (App.getDaoSession().getMeasureConfigurationBeanDao().load((long) i) == null) {
            MeasureConfigurationBean _bean = new MeasureConfigurationBean();
            _bean.setCode_id(i);
            _bean.setMeasureMode(0);
            _bean.setIsShowChart(true);
            App.getDaoSession().getMeasureConfigurationBeanDao().insert(_bean);
        }

        if (App.getDaoSession().getStoreBean2Dao()
                .queryBuilder().where(StoreBean2Dao.Properties.CodeID.eq(i)).unique() == null) {
            StoreBean2 _bean = new StoreBean2();
            _bean.setCodeID(i);
            _bean.setStoreMode(0);
            App.getDaoSession().getStoreBean2Dao().insert(_bean);
        }

            /*
            if (getDaoSession().getTriggerConditionBeanDao().queryBuilder().where(TriggerConditionBeanDao.Properties.CodeID.eq(i)).list().size() == 0) {
                for (int j = 1; j <= 4; j++) {
                    TriggerConditionBean _bean = new TriggerConditionBean();
                    _bean.setMIndex(j);
                    _bean.setCodeID(i);
                    _bean.setConditionName("条件" + j);
                    _bean.setIsScale(false);
                    _bean.setScale(0.9);
                    _bean.setUpperLimit(100);
                    _bean.setLowerLimit(-100);
                    getDaoSession().getTriggerConditionBeanDao().insert(_bean);
                }
            }
            */

        // 初始化分组;
        if (App.getDaoSession().getParameterBeanDao().load((long) i) == null) {
            for (int j = 1; j <= 4; j++) {
                GroupBean _bean = new GroupBean();
                _bean.setCode_id(i);
                _bean.setM_index(j);
                _bean.setA_describe("恩");
                _bean.setA_upper_limit(30.04);
                _bean.setA_lower_limit(30.03);

                _bean.setB_describe("梯");
                _bean.setB_upper_limit(30.03);
                _bean.setB_lower_limit(30.02);

                _bean.setC_describe("科");
                _bean.setC_upper_limit(30.02);
                _bean.setC_lower_limit(30.01);

                _bean.setD_describe("技");
                _bean.setD_upper_limit(30.01);
                _bean.setD_lower_limit(30);
                App.getDaoSession().getGroupBeanDao().insert(_bean);
            }
        }

        if (App.getDaoSession().getCodeBeanDao().load((long) (i)) == null) {
            CodeBean _bean = new CodeBean();
            _bean.setId(Long.valueOf(i));
            _bean.setName("程序" + i);
            _bean.setMachineTool(mContext.getResources().getString(R.string.machine_tool) + i);
            _bean.setParts(mContext.getResources().getString(R.string.spare_parts) + i);
            App.getDaoSession().getCodeBeanDao().insert(_bean);
        }

        // 判断该程序内是否有参数设置，如没有，设置4个初始的M值;
        if (App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq((long) i)).list().size() <= 0) {
            for (int j = 0; j < 4; j++) {
                ParameterBean2 _bean = new ParameterBean2();
                _bean.setCodeID(i);
                _bean.setSequenceNumber(j);
                _bean.setCode("ch1");
                _bean.setDescribe("内径" + (j + 1));
                _bean.setNominalValue(30);
                _bean.setUpperToleranceValue(0.04);
                _bean.setLowerToleranceValue(0.0);
                _bean.setResolution(6);
                _bean.setDeviation(0);
                _bean.setEnable(true);
                Long pID = App.getDaoSession().getParameterBean2Dao().insertOrReplace(_bean);
                for (int z = 0; z < 4; z++) {
                    GroupBean2 groupBean2 = new GroupBean2();
                    groupBean2.setName(String.valueOf(z + 1));
                    groupBean2.setDescribe(String.valueOf(z));
                    groupBean2.setUpperLimit(30 + (z + 1) * 0.01);
                    groupBean2.setLowerLimit(30 + z * 0.01);
                    groupBean2.setPID(pID);
                    App.getDaoSession().getGroupBean2Dao().insertOrReplace(groupBean2);
                }

                // 创建默认分析参数;
                AnalysisPatternBean _analysisPatternBean = new AnalysisPatternBean();
                _analysisPatternBean.setPID(pID);
                _analysisPatternBean.setIsAAuto(true);
                _analysisPatternBean.setIsLineAuto(true);
                _analysisPatternBean.setUclX(0);
                _analysisPatternBean.setLclX(0);
                _analysisPatternBean.setUclR(0);
                _analysisPatternBean.setLclR(0);
                _analysisPatternBean.setA3(0);
                _analysisPatternBean.set_a3(0);
                App.getDaoSession().getAnalysisPatternBeanDao().insert(_analysisPatternBean);
            }
        }
    }
}
