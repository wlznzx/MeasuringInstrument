package alauncher.cn.measuringinstrument.view.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
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
import alauncher.cn.measuringinstrument.utils.DialogUtils;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import alauncher.cn.measuringinstrument.utils.SPUtils;
import alauncher.cn.measuringinstrument.utils.SystemPropertiesProxy;
import alauncher.cn.measuringinstrument.view.ParameterManagement2Activity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class InfoFragment extends Fragment {

    private Unbinder unbinder;
    private boolean resumed = false;

    @BindView(R.id.software_version_edt)
    public EditText softwareVersionEdt;

    @BindView(R.id.android_version_edt)
    public EditText androidVersionEdt;

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

    public DeviceInfoBean _bean;

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
        _bean = App.getDaoSession().getDeviceInfoBeanDao().load(App.SETTING_ID);
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
        JdbcUtil.IP = String.valueOf(SPUtils.get(getContext(), Constants.IP_KEY, "47.98.58.40"));
        new SyncTask().execute();
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
        androidVersionEdt.setText(Build.VERSION.RELEASE);
        deviceCodeEdt.setText(SystemPropertiesProxy.getString(getContext(), "ro.serialno"));
        baseVersionEdt.setText(SystemPropertiesProxy.getString(getContext(), "ro.build.description"));
    }

    public class SyncTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getContext());
            dialog.setTitle(getResources().getString(R.string.sync_title));
            dialog.setMessage(getResources().getString(R.string.sync_msg));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        //第二个执行方法,在onPreExecute()后执行，用于后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            int result;
            try {
                result = JdbcUtil.insertOrReplace(_bean.getFactoryCode(), _bean.getFactoryName(), _bean.getDeviceCode(), _bean.getDeviceName(), _bean.getManufacturer(),
                        "rmk3", App.handlerAccout);
            } catch (Exception e) {
                e.printStackTrace();
                return "NG";
            }
            return result >= 0 ? "OK" : "NG";
        }

        /*这个函数在doInBackground调用publishProgress(int i)时触发，虽然调用时只有一个参数
         但是这里取到的是一个数组,所以要用progesss[0]来取值
         第n个参数就用progress[n]来取值   */
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            //"loading..." + progresses[0] + "%"
        }

        /*doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
        这里的result就是上面doInBackground执行后的返回值，所以这里是"后台任务执行完毕"  */
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if ("OK".equals(result)) {
                DialogUtils.showDialog(getContext(),
                        getResources().getString(R.string.sync_title), getResources().getString(R.string.sync_success));
            } else {
                DialogUtils.showDialog(getContext(),
                        getResources().getString(R.string.sync_title), getResources().getString(R.string.sync_fail));
            }
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            dialog.dismiss();
        }
    }

}
