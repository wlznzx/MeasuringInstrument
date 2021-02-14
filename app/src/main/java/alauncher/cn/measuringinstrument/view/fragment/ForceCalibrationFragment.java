package alauncher.cn.measuringinstrument.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.widget.ConditionDialog;
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

    @BindView(R.id.rv)
    public RecyclerView rv;

    private ForceCalibrationBeanDao mForceCalibrationBeanDao;

    private TriggerConditionAdapter mAdapter;

    private List<TriggerConditionBean> mDatas;

    private RefreshInterface mRefreshInterface;

    private List<ParameterBean2> mParameterBean2s;

    // private StoreBean mStoreBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        super.onCreate(savedInstanceState);

        mDatas = App.getDaoSession().getTriggerConditionBeanDao().queryBuilder().where(TriggerConditionBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_force_calibration, container, false);
        unbinder = ButterKnife.bind(this, view);
        mForceCalibrationBeanDao = App.getDaoSession().getForceCalibrationBeanDao();
        ForceCalibrationBean _bean = mForceCalibrationBeanDao.load((long) App.getSetupBean().getCodeID());
        forceCalibrationSP.setSelection(_bean.getForceMode());
        forceCalibrationTimeEdt.setText(String.valueOf(_bean.getForceTime()));
        forceCalibrationNumberEdt.setText(String.valueOf(_bean.getForceNum()));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        mAdapter = new TriggerConditionAdapter();
        rv.setAdapter(mAdapter);
        return view;
    }

    public void setRefreshInterface(RefreshInterface refreshInterface) {
        mRefreshInterface = refreshInterface;
    }

    @OnClick({R.id.save_btn, R.id.add_tg_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                mForceCalibrationBeanDao.update(view2Bean());
                // android.util.Log.d("wlDebug", mStoreBean.toString());
                // App.getDaoSession().getStoreBeanDao().insertOrReplace(mStoreBean);
                Toast.makeText(getContext(), "校验保存成功.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_tg_btn:

                break;
        }
    }

    private ForceCalibrationBean view2Bean() {
        ForceCalibrationBean _bean = new ForceCalibrationBean();
        _bean.set_id(App.getSetupBean().getCodeID());
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

    public void conditionUpdate() {
        mDatas = App.getDaoSession().getTriggerConditionBeanDao().queryBuilder().where(TriggerConditionBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();
        mAdapter.notifyDataSetChanged();
        if (mRefreshInterface != null) {
            mRefreshInterface.onTriggerConditionChanged();
        }
    }

    @SuppressLint("DefaultLocale")
    private void upSelectCount() {
    }

    class TriggerConditionAdapter extends RecyclerView.Adapter<ViewHolder> {

        public TriggerConditionAdapter() {
        }

        public void setDatas() {

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(getContext(), parent, R.layout.triggercondition_layout_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            TriggerConditionBean _bean = mDatas.get(position);

//            holder.setText(R.id.m_value_tv, "M" + (position + 1));
            holder.setText(R.id.m_value_tv, "M" + (_bean.getMIndex() + 1));
//            Spinner isScale = holder.getConvertView().findViewById(R.id.is_scale_tv);
//            isScale.setSelection(_bean.getIsScale() ? 0 : 1);
            holder.setText(R.id.is_scale_tv, _bean.getIsScale() ? "是" : "否");
            holder.setText(R.id.scale_tv, String.valueOf(_bean.getScale()));
            holder.setText(R.id.upper_limit_tv, String.valueOf(_bean.getUpperLimit()));
            holder.setText(R.id.lower_limit_tv, String.valueOf(_bean.getLowerLimit()));
            holder.setText(R.id.stable_time_tv, String.valueOf(_bean.getStableTime()));
            holder.setText(R.id.name_tv, _bean.getConditionName());

            holder.setOnClickListener(R.id.data_layout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConditionDialog _dialog = new ConditionDialog(getContext(), ForceCalibrationFragment.this, _bean);
                    _dialog.show();
                }
            });

            holder.setOnLongClickListener(R.id.data_layout, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    final AlertDialog builder = new AlertDialog.Builder(getContext())
                            .create();
                    builder.show();
                    if (builder.getWindow() == null) {
                        return false;
                    }
                    builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
                    TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
                    Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
                    Button sure = (Button) builder.findViewById(R.id.btn_sure);
                    msg.setText("是否删除 " + _bean.getConditionName() + " ?");
                    cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            App.getDaoSession().getTriggerConditionBeanDao().delete(_bean);
                            conditionUpdate();
                            builder.dismiss();
                        }
                    });
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

    }

    @OnClick(R.id.add_tg_btn)
    public void addForceCF(View v) {
        ConditionDialog _dialog = new ConditionDialog(getContext(), this);
        _dialog.show();
    }


    public interface RefreshInterface {
        public void onTriggerConditionChanged();
    }

}
