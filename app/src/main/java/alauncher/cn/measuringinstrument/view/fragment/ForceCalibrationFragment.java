package alauncher.cn.measuringinstrument.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
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
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.view.AccoutManagementActivity;
import alauncher.cn.measuringinstrument.view.DataActivity;
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

    private StoreBean mStoreBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        super.onCreate(savedInstanceState);

        // mDatas = App.getDaoSession().getTriggerConditionBeanDao().queryBuilder().where(TriggerConditionBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();
        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        mAdapter = new TriggerConditionAdapter();
        rv.setAdapter(mAdapter);
        return view;
    }

    @OnClick({R.id.save_btn, R.id.add_tg_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                mForceCalibrationBeanDao.update(view2Bean());
                android.util.Log.d("wlDebug", mStoreBean.toString());
                App.getDaoSession().getStoreBeanDao().insertOrReplace(mStoreBean);
                Toast.makeText(getContext(), "校验保存成功.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_tg_btn:


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
            holder.setText(R.id.m_value_tv, "M" + (position + 1));
            Spinner isScale = holder.getConvertView().findViewById(R.id.is_scale_tv);
            isScale.setSelection(mStoreBean.getIsScale().equals("0") ? 0 : 1);
            holder.setText(R.id.scale_tv, String.valueOf(mStoreBean.getScale().get(position)));
            holder.setText(R.id.upper_limit_tv, String.valueOf(mStoreBean.getUpLimitValue().get(position)));
            holder.setText(R.id.lower_limit_tv, String.valueOf(mStoreBean.getLowLimitValue().get(position)));
            holder.setText(R.id.stable_time_tv, String.valueOf(mStoreBean.getStable().get(position)));
            // holder.setText(R.id.name_tv, String.valueOf(datas.get(position).conditionName));

            EditText scaleEdt = holder.getConvertView().findViewById(R.id.scale_tv);
            scaleEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mStoreBean.getScale().set(position, s.toString());
                }
            });
            EditText stableTimeEdt = holder.getConvertView().findViewById(R.id.stable_time_tv);
            stableTimeEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mStoreBean.getStable().set(position, s.toString());
                }
            });

            EditText upperLimitEdt = holder.getConvertView().findViewById(R.id.upper_limit_tv);
            upperLimitEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mStoreBean.getUpLimitValue().set(position, s.toString());
                }
            });
            EditText lowLimitEdt = holder.getConvertView().findViewById(R.id.lower_limit_tv);
            lowLimitEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mStoreBean.getLowLimitValue().set(position, s.toString());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mStoreBean.getScale().size();
        }
    }
}
