package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.mvp.presenter.CalibrationPresenter;
import alauncher.cn.measuringinstrument.mvp.presenter.impl.CalibrationPresenterImpl;
import alauncher.cn.measuringinstrument.view.activity_view.CalibrationActivityView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.OnItemSelected;


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

    private double[] currentCHADValue = new double[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCalibrationPresenter = new CalibrationPresenterImpl(this);
        mCalibrationPresenter.updateUI();
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
    public void onUIUpdate(CalibrationBean bean) {
        if (bean != null) {
            // 校验模式
            calibrationTypeSP[0].setSelection(bean.getCh1CalibrationType());
            calibrationTypeSP[1].setSelection(bean.getCh2CalibrationType());
            calibrationTypeSP[2].setSelection(bean.getCh3CalibrationType());
            calibrationTypeSP[3].setSelection(bean.getCh4CalibrationType());
            // 小件标准
            smallPartEdt[0].setText(String.valueOf(bean.getCh1SmallPartStandard()));
            smallPartEdt[1].setText(String.valueOf(bean.getCh2SmallPartStandard()));
            smallPartEdt[2].setText(String.valueOf(bean.getCh3SmallPartStandard()));
            smallPartEdt[3].setText(String.valueOf(bean.getCh4SmallPartStandard()));
            // 大件标准
            bigPartCHEdt[0].setText(String.valueOf(bean.getCh1BigPartStandard()));
            bigPartCHEdt[1].setText(String.valueOf(bean.getCh2BigPartStandard()));
            bigPartCHEdt[2].setText(String.valueOf(bean.getCh3BigPartStandard()));
            bigPartCHEdt[3].setText(String.valueOf(bean.getCh4BigPartStandard()));
            // 倍率上限;
            upperLimitEdt[0].setText(String.valueOf(bean.getCh1UpperLimitRate()));
            upperLimitEdt[1].setText(String.valueOf(bean.getCh2UpperLimitRate()));
            upperLimitEdt[2].setText(String.valueOf(bean.getCh3UpperLimitRate()));
            upperLimitEdt[3].setText(String.valueOf(bean.getCh4UpperLimitRate()));
            // 倍率下限;
            lowerLimitEdt[0].setText(String.valueOf(bean.getCh1LowerLimitRate()));
            lowerLimitEdt[1].setText(String.valueOf(bean.getCh2LowerLimitRate()));
            lowerLimitEdt[2].setText(String.valueOf(bean.getCh3LowerLimitRate()));
            lowerLimitEdt[3].setText(String.valueOf(bean.getCh4LowerLimitRate()));
            // 倍率;
            kValueEdt[0].setText(String.valueOf(bean.getCh1KValue()));
            kValueEdt[1].setText(String.valueOf(bean.getCh2KValue()));
            kValueEdt[2].setText(String.valueOf(bean.getCh3KValue()));
            kValueEdt[3].setText(String.valueOf(bean.getCh4KValue()));
            // 偏差;
            compensationValueEdt[0].setText(String.valueOf(bean.getCh1CompensationValue()));
            compensationValueEdt[1].setText(String.valueOf(bean.getCh2CompensationValue()));
            compensationValueEdt[2].setText(String.valueOf(bean.getCh3CompensationValue()));
            compensationValueEdt[3].setText(String.valueOf(bean.getCh4CompensationValue()));

            for (int i = 0; i < calibrationTypeSP.length; i++) {
                kValueEdt[i].setEnabled(calibrationTypeSP[i].getSelectedItemId() == 0);
            }
        }
    }

    @OnClick({R.id.samll_part_btn, R.id.big_part_btn, R.id.calibration_save_btn, R.id.select_all_btn})
    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.samll_part_btn:
                for (int i = 0; i < smallPartADEdt.length; i++) {
                    int x = Integer.parseInt(measureADEdt[i].getText().toString().trim(), 16) - 100;
                    smallPartADEdt[i].setText("" + x);
                }
                break;
            case R.id.big_part_btn:
                for (int i = 0; i < smallPartADEdt.length; i++) {
                    int x = Integer.parseInt(measureADEdt[i].getText().toString().trim(), 16) + 100;
                    bigPartADEdt[i].setText("" + x);
                }
                break;
            case R.id.calibration_save_btn:
                doCalc();
                mCalibrationPresenter.saveCalibration(view2Bean());
                break;
            case R.id.select_all_btn:
                for (CheckBox _box : chRbs) {
                    _box.setChecked(true);
                }
                break;
        }
    }

    @OnItemSelected({R.id.calibration_method_sp_m1, R.id.calibration_method_sp_m2, R.id.calibration_method_sp_m3, R.id.calibration_method_sp_m4})
    public void onItemSelected(int position) {
        for (int i = 0; i < calibrationTypeSP.length; i++) {
            kValueEdt[i].setEnabled(calibrationTypeSP[i].getSelectedItemId() == 0);
        }
    }

    @Override
    public void onDataUpdate(double[] values) {
        android.util.Log.d("wlDebug", "1 = " + values[0]);
        currentCHADValue = values;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < measureADEdt.length; i++) {
                    measureADEdt[i].setText(String.valueOf(currentCHADValue[i]));
                }
            }
        });
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


    private CalibrationBean view2Bean() {
        CalibrationBean _bean = new CalibrationBean();
        _bean.setCode_id(1);
        // 校验模式
        _bean.setCh1CalibrationType((int) calibrationTypeSP[0].getSelectedItemId());
        _bean.setCh2CalibrationType((int) calibrationTypeSP[1].getSelectedItemId());
        _bean.setCh3CalibrationType((int) calibrationTypeSP[2].getSelectedItemId());
        _bean.setCh4CalibrationType((int) calibrationTypeSP[3].getSelectedItemId());
        // 小件标准
        _bean.setCh1SmallPartStandard(Double.valueOf(smallPartEdt[0].getText().toString().trim()));
        _bean.setCh2SmallPartStandard(Double.valueOf(smallPartEdt[1].getText().toString().trim()));
        _bean.setCh3SmallPartStandard(Double.valueOf(smallPartEdt[2].getText().toString().trim()));
        _bean.setCh4SmallPartStandard(Double.valueOf(smallPartEdt[3].getText().toString().trim()));
        // 大件标准
        _bean.setCh1BigPartStandard(Double.valueOf(bigPartCHEdt[0].getText().toString().trim()));
        _bean.setCh2BigPartStandard(Double.valueOf(bigPartCHEdt[1].getText().toString().trim()));
        _bean.setCh3BigPartStandard(Double.valueOf(bigPartCHEdt[2].getText().toString().trim()));
        _bean.setCh4BigPartStandard(Double.valueOf(bigPartCHEdt[3].getText().toString().trim()));
        // 倍率上限;
        _bean.setCh1UpperLimitRate(Double.valueOf(upperLimitEdt[0].getText().toString().trim()));
        _bean.setCh2UpperLimitRate(Double.valueOf(upperLimitEdt[1].getText().toString().trim()));
        _bean.setCh3UpperLimitRate(Double.valueOf(upperLimitEdt[2].getText().toString().trim()));
        _bean.setCh4UpperLimitRate(Double.valueOf(upperLimitEdt[3].getText().toString().trim()));
        // 倍率下限;
        _bean.setCh1LowerLimitRate(Double.valueOf(lowerLimitEdt[0].getText().toString().trim()));
        _bean.setCh2LowerLimitRate(Double.valueOf(lowerLimitEdt[1].getText().toString().trim()));
        _bean.setCh3LowerLimitRate(Double.valueOf(lowerLimitEdt[2].getText().toString().trim()));
        _bean.setCh4LowerLimitRate(Double.valueOf(lowerLimitEdt[3].getText().toString().trim()));
        // 倍率;
        _bean.setCh1KValue(Double.valueOf(kValueEdt[0].getText().toString().trim()));
        _bean.setCh2KValue(Double.valueOf(kValueEdt[1].getText().toString().trim()));
        _bean.setCh3KValue(Double.valueOf(kValueEdt[2].getText().toString().trim()));
        _bean.setCh4KValue(Double.valueOf(kValueEdt[3].getText().toString().trim()));
        // 偏差;
        _bean.setCh1CompensationValue(Double.valueOf(compensationValueEdt[0].getText().toString().trim()));
        _bean.setCh2CompensationValue(Double.valueOf(compensationValueEdt[1].getText().toString().trim()));
        _bean.setCh3CompensationValue(Double.valueOf(compensationValueEdt[2].getText().toString().trim()));
        _bean.setCh4CompensationValue(Double.valueOf(compensationValueEdt[3].getText().toString().trim()));
        return _bean;
    }


    private void doCalc() {
        for (int i = 0; i < chRbs.length; i++) {
            if (chRbs[i].isChecked()) {
                // android.util.Log.d("wlDebug", "doCalc smallPartEdt[i].getText() = " +);
                // 单件校准;
                if (calibrationTypeSP[i].getSelectedItemId() == 0) {
                    try {
                        if (!smallPartADEdt[i].getText().equals("")) {
                            double y = Double.valueOf(smallPartEdt[i].getText().toString().trim());
                            int x = Integer.parseInt(smallPartADEdt[i].getText().toString().trim(), 10);
                            double k = Double.valueOf(kValueEdt[i].getText().toString().trim());
                            double c = mCalibrationPresenter.calculationC(y, k, x);
                            android.util.Log.d("wlDebug", "y1 = " + y + " x = " + x + " k = " + k + " c = " + c);
                            compensationValueEdt[i].setText("" + c);
                        } else if (!bigPartADEdt[i].getText().equals("")) {
                            double y = Double.valueOf(bigPartCHEdt[i].getText().toString().trim());
                            int x = Integer.parseInt(bigPartADEdt[i].getText().toString().trim(), 10);
                            double k = Double.valueOf(kValueEdt[i].getText().toString().trim());
                            double c = mCalibrationPresenter.calculationC(y, k, x);
                            android.util.Log.d("wlDebug", "y2 = " + y + " x = " + x + " k = " + k + " c = " + c);
                            compensationValueEdt[i].setText("" + c);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "请先取得对应件的AD值", Toast.LENGTH_SHORT).show();
                    }
                }
                // 双键校准;
                else if (calibrationTypeSP[i].getSelectedItemId() == 1) {
                    try {
                        double y1 = Double.valueOf(smallPartEdt[i].getText().toString().trim());
                        double y2 = Double.valueOf(bigPartCHEdt[i].getText().toString().trim());

                        int x1 = Integer.parseInt(smallPartADEdt[i].getText().toString().trim(), 10);
                        int x2 = Integer.parseInt(bigPartADEdt[i].getText().toString().trim(), 10);

                        android.util.Log.d("wlDebug", "y1 = " + y1 + " y2 = " + y2 + " x1 = " + x1 + " x2 = " + x2);
                        double k = (y1 - y2) / (x1 - x2);
                        double c = mCalibrationPresenter.calculationC(y1, k, x1);
                        kValueEdt[i].setText("" + k);
                        compensationValueEdt[i].setText("" + c);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "请先取得对应件的AD值", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}
