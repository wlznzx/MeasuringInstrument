package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.mvp.presenter.CalibrationPresenter;
import alauncher.cn.measuringinstrument.mvp.presenter.impl.CalibrationPresenterImpl;
import alauncher.cn.measuringinstrument.view.activity_view.CalibrationActivityView;
import butterknife.BindViews;


public class CalibrationActivity extends BaseActivity implements CalibrationActivityView {

    private CalibrationPresenter mCalibrationPresenter;

    @BindViews({R.id.calibration_method_sp_m1, R.id.calibration_method_sp_m2, R.id.calibration_method_sp_m3, R.id.calibration_method_sp_m4})
    public Spinner calibrationTypeSP[];

    @BindViews({R.id.small_part_standard_m1, R.id.small_part_standard_m2, R.id.small_part_standard_m3, R.id.small_part_standard_m4})
    public EditText smallPartEdt[];

    @BindViews({R.id.big_part_standard_m1, R.id.big_part_standard_m2, R.id.big_part_standard_m3, R.id.big_part_standard_m4})
    public EditText bigPartCHEdt[];

    @BindViews({R.id.upper_limit_of_information_rate_m1, R.id.upper_limit_of_information_rate_m2, R.id.upper_limit_of_information_rate_m3, R.id.upper_limit_of_information_rate_m4})
    public EditText upperLimitEdt[];


    @BindViews({R.id.lower_confidence_limit_m1, R.id.lower_confidence_limit_m2, R.id.lower_confidence_limit_m3, R.id.lower_confidence_limit_m4})
    public EditText lowerLimitEdt[];

    @BindViews({R.id.compensation_value_m1, R.id.compensation_value_m2, R.id.compensation_value_m3, R.id.compensation_value_m4})
    public EditText compensationValueEdt[];

    @BindViews({R.id.ch1_rb, R.id.ch2_rb, R.id.ch3_rb, R.id.ch4_rb})
    public CheckBox chRbs[];

    @BindViews({R.id.small_piece_ad_m1, R.id.small_piece_ad_m2, R.id.small_piece_ad_m3, R.id.small_piece_ad_m4})
    public EditText smallPartADEdt[];

    @BindViews({R.id.big_piece_ad_m1, R.id.big_piece_ad_m2, R.id.big_piece_ad_m3, R.id.big_piece_ad_m4})
    public EditText bigPartADEdt[];

    @BindViews({R.id.measure_ad_m1, R.id.measure_ad_m2, R.id.measure_ad_m3, R.id.measure_ad_m4})
    public EditText measureADEdt[];

    @BindViews({R.id.measure_value_m1, R.id.measure_value_m2, R.id.measure_value_m3, R.id.measure_value_m4})
    public EditText measureValueEdt[];

    @BindViews({R.id.ch1_k_edt, R.id.ch2_k_edt, R.id.ch3_k_edt, R.id.ch4_k_edt})
    public EditText kValueEdt[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCalibrationPresenter = new CalibrationPresenterImpl(this);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_calibration);
    }

    @Override
    protected void initView() {
        for (EditText _edt : smallPartADEdt) {
            _edt.setEnabled(false);
        }
        for (EditText _edt : bigPartADEdt) {
            _edt.setEnabled(false);
        }
        for (EditText _edt : measureADEdt) {
            _edt.setEnabled(false);
        }
        for (EditText _edt : measureValueEdt) {
            _edt.setEnabled(false);
        }
    }

    @Override
    public void onDataUpdate(double[] values) {
        android.util.Log.d("wlDebug","1 = " + values[0]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCalibrationPresenter.stopValueing();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCalibrationPresenter.startValueing();
    }
}
