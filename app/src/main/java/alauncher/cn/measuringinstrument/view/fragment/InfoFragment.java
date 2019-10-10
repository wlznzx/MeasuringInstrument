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
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.utils.BuildUtils;
import alauncher.cn.measuringinstrument.utils.Constants;
import alauncher.cn.measuringinstrument.utils.DeviceUtils;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import alauncher.cn.measuringinstrument.utils.SPUtils;
import alauncher.cn.measuringinstrument.utils.SystemPropertiesProxy;
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

    @BindView(R.id.ip_edt)
    public EditText ipEdt;

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
            _bean.setDeviceCode(deviceCodeEdt.getText().toString().trim());
        }
        App.getDaoSession().getDeviceInfoBeanDao().insertOrReplace(_bean);
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();

        SPUtils.put(getContext(), Constants.IP_KEY, ipEdt.getText().toString().trim());
        android.util.Log.d("wlDebug", "info = " + _bean.toString());
        JdbcUtil.IP = String.valueOf(SPUtils.get(getContext(), Constants.IP_KEY,"47.98.58.40"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                JdbcUtil.insertOrReplace(_bean.getFactoryCode(), _bean.getFactoryName(), _bean.getDeviceCode(), _bean.getDeviceName(), _bean.getManufacturer(),
                        "rmk3", App.handlerAccout);
            }
        }).start();
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
        ipEdt.setText(String.valueOf(SPUtils.get(getContext(), Constants.IP_KEY, "47.98.58.40")));
        softwareVersionEdt.setText(BuildUtils.packageName(getContext()));
        kernelVersionEdt.setText(BuildUtils.getLinuxCore_Ver());
        deviceCodeEdt.setText(SystemPropertiesProxy.getString(getContext(), "ro.serialno"));
        baseVersionEdt.setText(BuildUtils.getInner_Ver());
    }

}
