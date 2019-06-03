package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoSession;
import alauncher.cn.measuringinstrument.widget.CalculateDialog;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;


public class ParameterManagementActivity extends BaseActivity implements CalculateDialog.CodeInterface {

    @BindViews({R.id.describe_m1, R.id.describe_m2, R.id.describe_m3, R.id.describe_m4})
    public List<EditText> describeEd;

    @BindViews({R.id.nominal_value_m1, R.id.nominal_value_m2, R.id.nominal_value_m3, R.id.nominal_value_m4})
    public List<EditText> nominalValueEd;

    @BindViews({R.id.upper_tolerance_m1, R.id.upper_tolerance_m2, R.id.upper_tolerance_m3, R.id.upper_tolerance_m4})
    public List<EditText> upperToleranceEd;

    @BindViews({R.id.lower_tolerance_m1, R.id.lower_tolerance_m2, R.id.lower_tolerance_m3, R.id.lower_tolerance_m4})
    public List<EditText> lowerToleranceEd;

    @BindViews({R.id.resolution_sp_m1, R.id.resolution_sp_m2, R.id.resolution_sp_m3, R.id.resolution_sp_m4})
    public List<Spinner> resolutionSp;

    @BindViews({R.id.deviation_m1, R.id.deviation_m2, R.id.deviation_m3, R.id.deviation_m4})
    public List<EditText> deviationEd;

    @BindViews({R.id.formula_m1, R.id.formula_m2, R.id.formula_m3, R.id.formula_m4})
    public List<Button> formulaEd;

    @BindViews({R.id.grouping_m1, R.id.grouping_m2, R.id.grouping_m3, R.id.grouping_m4})
    public List<Button> groupingBtn;

    @BindView(R.id.save_btn)
    public Button saveBtn;

    public DaoSession session;

    public String m1_code, m2_code, m3_code, m4_code;

    public ParameterBean mParameterBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_parameter_management);
    }

    @Override
    protected void initView() {
        session = App.getDaoSession();
        mParameterBean = session.getParameterBeanDao().load((long) 1);
        if (mParameterBean == null) {
            mParameterBean = new ParameterBean();
            mParameterBean.setCode_id(1);
        }
        updateUI();
    }

    @OnClick(R.id.save_btn)
    public void onSave(View v) {
        if (session.getParameterBeanDao().load((long) 1) == null) {
            session.getParameterBeanDao().insert(mParameterBean);
        } else {
            session.getParameterBeanDao().update(mParameterBean);
        }
    }

    @OnClick({R.id.formula_m1, R.id.formula_m2, R.id.formula_m3, R.id.formula_m4})
    public void onFormulaEdit(View v) {
        int m_num = -1;
        switch (v.getId()) {
            case R.id.formula_m1:
                m_num = 1;
                break;
            case R.id.formula_m2:
                m_num = 2;
                break;
            case R.id.formula_m3:
                m_num = 3;
                break;
            case R.id.formula_m4:
                m_num = 4;
                break;
            default:
                break;
        }
        // Toast.makeText(this, "go m = " + m_num, Toast.LENGTH_SHORT).show();
        // openActivty(MGroupActivity.class);
        CalculateDialog mCalculateDialog = new CalculateDialog(this);
        // mCalculateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCalculateDialog.setTitle("M" + m_num + "程序设置");
        mCalculateDialog.setM(m_num);
        mCalculateDialog.setCodeInterface(this);
        mCalculateDialog.show();
    }

    @Override
    public void onCodeSet(int m, String code) {
        android.util.Log.d("wlDebug", "M" + m + " = " + code);
        switch (m) {
            case 1:
                mParameterBean.setM1_code(code);
                break;
            case 2:
                mParameterBean.setM2_code(code);
                break;
            case 3:
                mParameterBean.setM3_code(code);
                break;
            case 4:
                mParameterBean.setM4_code(code);
                break;
        }
        updateUI();
    }

    public void updateUI() {
        if (mParameterBean != null) {
            formulaEd.get(0).setText(mParameterBean.getM1_code());
            formulaEd.get(1).setText(mParameterBean.getM2_code());
            formulaEd.get(2).setText(mParameterBean.getM3_code());
            formulaEd.get(3).setText(mParameterBean.getM4_code());
        }
    }
}
