package alauncher.cn.measuringinstrument.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.utils.BuildUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class InfoFragment extends Fragment {

    private Unbinder unbinder;
    private boolean resumed = false;

    @BindView(R.id.software_version_edt)
    public EditText softwareVersionEdt;

    @BindView(R.id.kernel_version_edt)
    public EditText kernelVersionEdt;

    @BindView(R.id.device_code_edt)
    public EditText deviceCodeEdt;

    @BindView(R.id.base_version_edt)
    public EditText baseVersionEdt;

    @BindView(R.id.factory_code_edt)
    public EditText factoryCodeEdt;

    @BindView(R.id.factory_name_edt)
    public EditText factoryNameEdt;

    @BindView(R.id.manufacturer_edt)
    public EditText manufacturerEdt;

    @BindView(R.id.device_name_edt)
    public EditText deviceNameEdt;

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
        View view = inflater.inflate(R.layout.info_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @OnClick(R.id.save_btn)
    public void onSave(View v) {
        DeviceInfoBean _bean = App.getDaoSession().getDeviceInfoBeanDao().load(App.SETTING_ID);
        if (_bean == null) {

        } else {
            _bean.setFactoryCode(factoryCodeEdt.getText().toString().trim());
            _bean.setFactoryName(factoryNameEdt.getText().toString().trim());
            _bean.setManufacturer(manufacturerEdt.getText().toString().trim());
            _bean.setDeviceName(deviceNameEdt.getText().toString().trim());

        }
        App.getDaoSession().getDeviceInfoBeanDao().insertOrReplace(_bean);
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
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

    private void initView() {
        DeviceInfoBean _bean = App.getDaoSession().getDeviceInfoBeanDao().load(App.SETTING_ID);

        if (_bean != null) {
            factoryCodeEdt.setText(_bean.getFactoryCode());
            factoryNameEdt.setText(_bean.getFactoryName());
            manufacturerEdt.setText(_bean.getManufacturer());
            deviceNameEdt.setText(_bean.getDeviceName());
        } else {

        }
        softwareVersionEdt.setText(BuildUtils.packageName(getContext()));
        kernelVersionEdt.setText(BuildUtils.getLinuxCore_Ver());
        deviceCodeEdt.setText(BuildUtils.getLocalMacAddress(getContext()));
        baseVersionEdt.setText(BuildUtils.getInner_Ver());
    }

}
