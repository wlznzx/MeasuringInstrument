package alauncher.cn.measuringinstrument.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.HashMap;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.CodeBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.utils.DialogUtils;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import alauncher.cn.measuringinstrument.view.adapter.CodeListAdapter;
import alauncher.cn.measuringinstrument.widget.CodeEditDialog;
import butterknife.BindView;
import butterknife.OnClick;


public class Code2Activity extends BaseOActivity implements DataUpdateInterface {

    @BindView(R.id.lv_text_view)
    public ListView listView;

    @BindView(R.id.search_view)
    public SearchView mSearchView;

    public int codeID = 1;

    public CodeListAdapter adapter;

    public List<CodeBean> allCodeBeans;

    private DeviceInfoBean mDeviceInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_code2);
    }

    @Override
    protected void initView() {
        listView.setDividerHeight(0);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                android.util.Log.d("wlDebug", "onQueryTextSubmit s = " + s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    adapter.setFilterText(newText);
                } else {
                    adapter.setFilterText("-");
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDeviceInfoBean = App.getDaoSession().getDeviceInfoBeanDao().load(App.SETTING_ID);
        HashMap<String, Boolean> states = new HashMap<String, Boolean>();
//        allCodeBeans = App.getDaoSession().getCodeBeanDao().queryBuilder().orderAsc(CodeBeanDao.Properties.Id).list();
        allCodeBeans = App.getDaoSession().getCodeBeanDao().loadAll();
//        for (CodeBean bean : allCodeBeans) {
//            android.util.Log.d("wlDebug", "bean = " + bean.toString());
//        }
        codeID = App.getSetupBean().getCodeID();
        states.put(String.valueOf(codeID - 1), true);
        if (adapter == null) {
            adapter = new CodeListAdapter(allCodeBeans, states, this, this);
            listView.setAdapter(adapter);
        }
        adapter.setList(allCodeBeans);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick({R.id.set_btn, R.id.set_as_btn, R.id.add_code_btn, R.id.upload_server_btn})
    public void onSetBtnClick(View v) {
        switch (v.getId()) {
            case R.id.set_btn:
                Intent intent = new Intent(this, CodeDetailActivity.class);
                intent.putExtra("Title", R.string.code_set);
                intent.putExtra("CODE_ID", adapter.currentCodeID);
                android.util.Log.d("wlDebug", "adapter.currentCodeID = " + adapter.currentCodeID);
                startActivity(intent);
                break;
            case R.id.set_as_btn:
                SetupBean _sbean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
                _sbean.setCodeID(adapter.currentCodeID);
                App.getDaoSession().getSetupBeanDao().insertOrReplace(_sbean);
                CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) App.getSetupBean().getCodeID());
                User user = App.getDaoSession().getUserDao().load(App.handlerAccout);
                String _name = App.handlerAccout;
                if (user != null) {
                    _name = user.getName();
                }
                if (_bean != null) {
                    actionTips.setText(_name);
                    actionBarCodeTips.setText( _bean.getName());
                } else {
                    actionTips.setText(_name);
                    actionBarCodeTips.setText("程序" + App.getSetupBean().getCodeID());
                }

                // InputActivity.datas.clear();
                // InputActivity.updates.clear();
                DialogUtils.showDialog(this, "设为当前", "设置成功.");
                break;
            case R.id.add_code_btn:
                CodeEditDialog codeEditDialog = new CodeEditDialog(Code2Activity.this, null);
                codeEditDialog.setDataUpdateInterface(this);
                codeEditDialog.show();
                break;
            case R.id.upload_server_btn:
                new uploadTask().execute();
                break;
        }
    }

    @Override
    public void dataUpdate() {
        allCodeBeans = App.getDaoSession().getCodeBeanDao().loadAll();
        adapter.setList(allCodeBeans);
        adapter.notifyDataSetChanged();
    }

    public class uploadTask extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog dialog;
        private List<ParameterBean2> mDates;
        private List<CodeBean> mCodeBeans;

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            mCodeBeans = App.getDaoSession().getCodeBeanDao().loadAll();
            dialog = new ProgressDialog(Code2Activity.this);
            dialog.setTitle("程序同步");
            dialog.setMessage("共有" + mCodeBeans.size() + "个程序正在同步服务器，请稍等.");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // 删除该设备所有的Code;
                // int ret = JdbcUtil.deleteAllCodes(mDeviceInfoBean.getFactoryCode(), mDeviceInfoBean.getDeviceCode());
                /**/
                for (int i = 0; i < mCodeBeans.size(); i++) {
                    mDates = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                            .where(ParameterBean2Dao.Properties.CodeID.eq(mCodeBeans.get(i).getId())).list();
                    JdbcUtil.addParam2Config(mDeviceInfoBean.getFactoryCode(), mDeviceInfoBean.getDeviceCode(), mDates);
                    publishProgress(i + 1);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            dialog.setMessage("共有" + mCodeBeans.size() + "个程序正在同步服务器," + "已上传" + progresses[0] + "个程序.");
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            dialog.dismiss();
            if (isSuccess) {
                DialogUtils.showDialog(Code2Activity.this, "上传成功", "上传服务器成功.");
            } else {
                DialogUtils.showDialog(Code2Activity.this, "上传失败", "上传服务器失败，请配置和网络.");
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
