package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import org.nfunk.jep.function.Str;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import butterknife.BindView;
import butterknife.OnClick;


public class ForceCalibrationActivity extends BaseActivity {

    @BindView(R.id.force_calibration_sp)
    public Spinner forceCalibrationSP;

    @BindView(R.id.force_calibration_time_edt)
    public EditText forceCalibrationTimeEdt;

    @BindView(R.id.force_calibration_number_edt)
    public EditText forceCalibrationNumberEdt;

    private ForceCalibrationBeanDao mForceCalibrationBeanDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_force_calibration);
    }

    @Override
    protected void initView() {
        mForceCalibrationBeanDao = App.getDaoSession().getForceCalibrationBeanDao();

        ForceCalibrationBean _bean = mForceCalibrationBeanDao.load(App.SETTING_ID);

        forceCalibrationSP.setSelection(_bean.getForceMode());

        forceCalibrationTimeEdt.setText(String.valueOf(_bean.getForceTime()));

        forceCalibrationNumberEdt.setText(String.valueOf(_bean.getForceNum()));
    }


    @OnClick(R.id.save_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                mForceCalibrationBeanDao.update(view2Bean());
                break;
        }
    }

    private ForceCalibrationBean view2Bean() {
        ForceCalibrationBean _bean = new ForceCalibrationBean();
        _bean.set_id(App.SETTING_ID);
        _bean.setForceMode(forceCalibrationSP.getSelectedItemPosition());
        _bean.setForceNum(Integer.valueOf(forceCalibrationNumberEdt.getText().toString().trim()));
        _bean.setUsrNum(Integer.valueOf(forceCalibrationNumberEdt.getText().toString().trim()));
        int _time = Integer.valueOf(forceCalibrationTimeEdt.getText().toString().trim());
        _bean.setForceTime(_time);
        _bean.setRealForceTime(System.currentTimeMillis() + _time * 60 * 1000);
        return _bean;
    }
}
