package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;


public class ParameterManagementActivity extends BaseActivity {

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
    }

    @OnClick(R.id.save_btn)
    public void onSave(View v) {

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
        Toast.makeText(this,"go m = " + m_num,Toast.LENGTH_SHORT).show();
        openActivty(MGroupActivity.class);
    }
}
