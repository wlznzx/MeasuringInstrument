package alauncher.cn.measuringinstrument.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ForceCalibrationFragment extends Fragment {

    private Unbinder unbinder;
    private boolean resumed = false;

    @BindView(R.id.force_calibration_sp)
    public Spinner forceCalibrationSP;

    @BindView(R.id.force_calibration_time_edt)
    public EditText forceCalibrationTimeEdt;

    @BindView(R.id.force_calibration_number_edt)
    public EditText forceCalibrationNumberEdt;

    private ForceCalibrationBeanDao mForceCalibrationBeanDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_force_calibration, container, false);
        unbinder = ButterKnife.bind(this, view);
        mForceCalibrationBeanDao = App.getDaoSession().getForceCalibrationBeanDao();
        ForceCalibrationBean _bean = mForceCalibrationBeanDao.load(App.SETTING_ID);
        forceCalibrationSP.setSelection(_bean.getForceMode());
        forceCalibrationTimeEdt.setText(String.valueOf(_bean.getForceTime()));
        forceCalibrationNumberEdt.setText(String.valueOf(_bean.getForceNum()));
        return view;
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

    @Override
    public void onResume() {
        super.onResume();
        if (resumed) {
            resumed = false;
        }
    }

    @Override
    public void onPause() {
        resumed = true;
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @SuppressLint("DefaultLocale")
    private void upSelectCount() {
    }

}
