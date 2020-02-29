package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.StoreBean2;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.utils.Arith;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import alauncher.cn.measuringinstrument.widget.ParameterEditDialog;
import butterknife.BindView;
import butterknife.OnClick;


public class ParameterManagement2Activity extends BaseOActivity implements DataUpdateInterface {

    @BindView(R.id.rv)
    RecyclerView rv;

    private List<ParameterBean2> mDates;

    private ParameterAdapter mAdapter;

    private int enableMSize = 0;

    private DeviceInfoBean mDeviceInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_parameter_management2);
    }

    @Override
    protected void initView() {
        mDates = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
        enableMSize = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list().size();
        mDeviceInfoBean = App.getDeviceInfo();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ParameterManagement2Activity.this);
        rv.setLayoutManager(layoutManager);
        mAdapter = new ParameterAdapter();
        rv.setAdapter(mAdapter);
    }

    @Override
    public void dataUpdate() {
        mDates = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
        // 当实际测量参数个数有变的情况下，同步删除该程序条件，分步等信息;
        int _enableMSize = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list().size();

        if (_enableMSize != enableMSize) {
            enableMSize = _enableMSize;
            App.getDaoSession().getStepBean2Dao().queryBuilder()
                    .where(StepBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).buildDelete().executeDeleteWithoutDetachingEntities();
            App.getDaoSession().getTriggerConditionBeanDao().queryBuilder()
                    .where(TriggerConditionBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).buildDelete().executeDeleteWithoutDetachingEntities();
            StoreBean2 mStoreBean = App.getDaoSession().getStoreBean2Dao()
                    .queryBuilder().where(StoreBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).unique();
            mStoreBean.setStoreMode(0);
            App.getDaoSession().getStoreBean2Dao().insertOrReplace(mStoreBean);
            android.util.Log.d("wlDebug", "do clean ");
        }
        mAdapter.notifyDataSetChanged();
        syncToServer();
    }

    class ParameterAdapter extends RecyclerView.Adapter<ViewHolder> {

        public ParameterAdapter() {

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(ParameterManagement2Activity.this, parent, R.layout.parameter_list_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ParameterBean2 _bean = mDates.get(position);
            holder.setTextColor(R.id.is_enable_tv, _bean.getEnable() ?
                    getResources().getColor(android.R.color.holo_green_dark) : getResources().getColor(android.R.color.holo_red_dark));
            holder.setText(R.id.is_enable_tv, _bean.getEnable() ? "是" : "否");
            holder.setText(R.id.m_title_tv, "M" + (_bean.getSequenceNumber() + 1));
            holder.setText(R.id.describe_tv, _bean.getDescribe());
            holder.setText(R.id.nominal_value_tv, Arith.double2Str(_bean.getNominalValue()));
            holder.setText(R.id.upper_tolerance_value_tv, Arith.double2Str(_bean.getUpperToleranceValue()));
            holder.setText(R.id.lower_tolerance_value_tv, Arith.double2Str(_bean.getLowerToleranceValue()));
            holder.setText(R.id.deviation_tv, Arith.double2Str((_bean.getDeviation())));
            holder.setText(R.id.resolution_tv, getResources().getStringArray(R.array.resolution_values)[(int) _bean.getResolution()]);
            holder.setText(R.id.formula_tv, _bean.getCode());

            holder.setOnClickListener(R.id.data_layout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParameterEditDialog _dialog = new ParameterEditDialog(ParameterManagement2Activity.this, _bean);
                    _dialog.setDataUpdateInterface(ParameterManagement2Activity.this);
                    _dialog.show();
                }
            });

            holder.setOnLongClickListener(R.id.data_layout, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog builder = new AlertDialog.Builder(ParameterManagement2Activity.this)
                            .create();
                    builder.show();
                    if (builder.getWindow() == null) return false;
                    builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
                    TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
                    Button cancel = (Button) builder.findViewById(R.id.btn_cancle);
                    Button sure = (Button) builder.findViewById(R.id.btn_sure);
                    msg.setText("是否删除 " + _bean.getDescribe() + " ?");
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            App.getDaoSession().getGroupBean2Dao().queryBuilder().where(GroupBean2Dao.Properties.PID.eq(_bean.getId())).buildDelete().executeDeleteWithoutDetachingEntities();
                            App.getDaoSession().getParameterBean2Dao().delete(_bean);
                            for (ParameterBean2 _bean2 : mDates) {
                                if (_bean2.getSequenceNumber() > _bean.getSequenceNumber()) {
                                    _bean2.setSequenceNumber(_bean2.getSequenceNumber() - 1);
                                    App.getDaoSession().getParameterBean2Dao().insertOrReplace(_bean2);
                                }
                            }
                            builder.dismiss();
                            dataUpdate();
                        }
                    });
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDates.size();
        }
    }

    @OnClick(R.id.add_parameter_btn)
    public void addParameterBtn(View v) {
        // 目前参数数量限制为8个;
        int size = mDates.size();
        if (size >= 8) {
            showMsgDialog(this, "无法添加", "当前参数数量上限为8个。");
            return;
        }
        ParameterEditDialog _dialog = new ParameterEditDialog(this, null);
        _dialog.setDataUpdateInterface(ParameterManagement2Activity.this);
        _dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class DeleteTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ParameterManagement2Activity.this);
            dialog.setTitle("删除");
            dialog.setMessage("正在删除数据 , 请稍等.");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        //第二个执行方法,在onPreExecute()后执行，用于后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            return "";
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
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }


    public void showMsgDialog(Context context, String title, String msg) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle(title);
        normalDialog.setMessage(msg);
        normalDialog.setCancelable(true);
        normalDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        final Dialog _dialog = normalDialog.show();
        normalDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        _dialog.dismiss();
                    }
                });
    }

    private void syncToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int ret = JdbcUtil.deleteParam2s(mDeviceInfoBean.getFactoryCode(), mDeviceInfoBean.getDeviceCode());
                    android.util.Log.d("wlDebug", "delete ret = " + ret);
                    ret = JdbcUtil.addParam2Config(mDeviceInfoBean.getFactoryCode(), mDeviceInfoBean.getDeviceCode(), mDates);
                    android.util.Log.d("wlDebug", "add ret = " + ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
