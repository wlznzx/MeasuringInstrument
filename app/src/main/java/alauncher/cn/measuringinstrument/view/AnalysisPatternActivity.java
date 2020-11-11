package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.AnalysisPatternBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.AnalysisPatternBeanDao;
import alauncher.cn.measuringinstrument.utils.DialogUtils;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import butterknife.BindView;
import butterknife.OnClick;

public class AnalysisPatternActivity extends BaseOActivity implements DataUpdateInterface {

    @BindView(R.id.line_rg)
    public RadioGroup lineRG;

    @BindView(R.id.deviation_rg)
    public RadioGroup deviationRG;

    @BindView(R.id.ucl_x_edt)
    public EditText uclXEdt;

    @BindView(R.id.lcl_x_edt)
    public EditText lclXEdt;

    @BindView(R.id.ucl_r_edt)
    public EditText uclREdt;

    @BindView(R.id.lcl_r_edt)
    public EditText lclREdt;

    @BindView(R.id.a_edt)
    public EditText aEdt;

    @BindView(R.id._a_edt)
    public EditText _aEdt;

    private long pID;

    private AnalysisPatternBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_analysis_pattern);
    }

    @Override
    protected void initView() {
        pID = getIntent().getLongExtra("pID", -1);
        actionTitleTV.setText(R.string.analysis_pattern);
    }

    @OnClick(R.id.save_btn)
    public void doSave(View v) {
        DialogUtils.showDialog(this, getResources().getString(R.string.save), getResources().getString(R.string.save_success));
        bean.setIsAAuto(lineRG.getCheckedRadioButtonId() == R.id.deviation_auto_rb);
        bean.setIsLineAuto(lineRG.getCheckedRadioButtonId() == R.id.auto_line_rb);
        bean.setUclX(Double.valueOf(uclXEdt.getText().toString().trim()));
        bean.setLclX(Double.valueOf(lclXEdt.getText().toString().trim()));
        bean.setUclR(Double.valueOf(uclREdt.getText().toString().trim()));
        bean.setLclR(Double.valueOf(lclREdt.getText().toString().trim()));
        bean.setA3(Double.valueOf(aEdt.getText().toString().trim()));
        bean.set_a3(Double.valueOf(_aEdt.getText().toString().trim()));
        App.getDaoSession().getAnalysisPatternBeanDao().insertOrReplace(bean);
    }

    @OnClick(R.id.clear_btn)
    public void doClear(View v) {
        DialogUtils.showDialog(this, getResources().getString(R.string.clear), getResources().getString(R.string.clear_success));
        bean.setIsAAuto(true);
        bean.setIsLineAuto(true);
        bean.setUclX(0);
        bean.setLclX(0);
        bean.setUclR(0);
        bean.setLclR(0);
        bean.setA3(0);
        bean.set_a3(0);
//        App.getDaoSession().getAnalysisPatternBeanDao().insertOrReplace(bean);
        dataUpdate();
    }

    @Override
    public void dataUpdate() {
        initBean();
    }

    private void initBean() {
        lineRG.check(bean.getIsLineAuto() ? R.id.auto_line_rb : R.id.line_rb);
        deviationRG.check(bean.getIsAAuto() ? R.id.deviation_auto_rb : R.id.deviation_rb);
        uclXEdt.setText(String.valueOf(bean.getUclX()));
        lclXEdt.setText(String.valueOf(bean.getLclX()));
        uclREdt.setText(String.valueOf(bean.getUclR()));
        lclREdt.setText(String.valueOf(bean.getLclR()));
        aEdt.setText(String.valueOf(bean.getA3()));
        _aEdt.setText(String.valueOf(bean.get_a3()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getDaoSession().clear();
        bean = App.getDaoSession().getAnalysisPatternBeanDao().queryBuilder().where(AnalysisPatternBeanDao.Properties.PID.eq(pID)).unique();
        initBean();
        android.util.Log.d("alauncher","AnalysisPatternActivity onResume");
    }
}
