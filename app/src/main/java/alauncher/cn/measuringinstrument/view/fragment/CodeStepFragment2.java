package alauncher.cn.measuringinstrument.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.nfunk.jep.function.Str;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.StepBean2;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.utils.DialogUtils;
import alauncher.cn.measuringinstrument.view.MeasuringActivity;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import alauncher.cn.measuringinstrument.widget.StepEditDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CodeStepFragment2 extends Fragment implements DataUpdateInterface {

    @BindView(R.id.isAudo)
    public Switch isAutoSwitch;

    @BindView(R.id.rv)
    public RecyclerView stepRV;

    private Unbinder unbinder;

    private List<StepBean2> mStepBean2s;

    private List<ParameterBean2> mParameterBean2s;

    private StepAdapter mStepAdapter;

    public StoreBean mStoreBean;

    // 已被测量参数列表;
    private List<String> measuredItems = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);
        mStepBean2s = App.getDaoSession().getStepBean2Dao().queryBuilder()
                .where(StepBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(StepBean2Dao.Properties.SequenceNumber).list();
        mParameterBean2s = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .list();
        android.util.Log.d("wlDebug", "" + mStepBean2s.toString());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_step2, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        stepRV.setLayoutManager(layoutManager);
        isAutoSwitch.setChecked(mStoreBean.getStoreMode() == 1);
        mStepAdapter = new StepAdapter();
        stepRV.setAdapter(mStepAdapter);
        isAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    measuredItems.clear();
                    for (StepBean2 _bean : mStepBean2s) {
                        if (_bean.getMeasureItems() != null)
                            measuredItems.addAll(_bean.getMeasureItems());
                    }
                    // 所有参数被测量完毕;
                    if (measuredItems.size() == mParameterBean2s.size()) {
                        // do it;
                        mStoreBean.setStoreMode(1);
                        App.getDaoSession().getStoreBeanDao().insertOrReplace(mStoreBean);
                    } else {
                        isAutoSwitch.setChecked(false);
                        DialogUtils.showDialog(getContext(), getString(R.string.error), getString(R.string.must_measured_all_items));
                    }
                } else {
                    if (mStoreBean.getStoreMode() == 1) {
                        mStoreBean.setStoreMode(0);
                        App.getDaoSession().getStoreBeanDao().insertOrReplace(mStoreBean);
                    }
                }
            }
        });
    }

    @OnClick({R.id.add_step_btn, R.id.clear_btn})
    public void onSave(View v) {
        switch (v.getId()) {
            case R.id.add_step_btn:
                if (mStepBean2s.size() >= mParameterBean2s.size()) {
                    DialogUtils.showDialog(getContext(), getString(R.string.tips), getString(R.string.step_enough));
                    return;
                }
                mStoreBean.setStoreMode(isAutoSwitch.isChecked() ? 1 : 0);
                StepEditDialog _dialog = new StepEditDialog(getContext(), null);
                _dialog.setDataUpdateInterface(CodeStepFragment2.this);
                _dialog.show();
                break;
            case R.id.clear_btn:
                clearDialog();
                break;
        }
    }

    private void clearDialog() {
        final AlertDialog builder = new AlertDialog.Builder(getContext()).create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = builder.findViewById(R.id.tv_msg);
        Button cancel = builder.findViewById(R.id.btn_cancle);
        Button sure = builder.findViewById(R.id.btn_sure);
        if (msg == null || cancel == null || sure == null) return;
        msg.setText(R.string.sure_clear);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                mStoreBean.setStoreMode(0);
                App.getDaoSession().getStoreBeanDao().insertOrReplace(mStoreBean);
                App.getDaoSession().getStepBean2Dao().queryBuilder().where(StepBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).buildDelete().executeDeleteWithoutDetachingEntities();
                dataUpdate();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            android.util.Log.d("wlDebug", "do hidden.");
//            mTriggerConditionBeans = App.getDaoSession().getTriggerConditionBeanDao().queryBuilder().where(TriggerConditionBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();
//            condications.add("无");
//            for (TriggerConditionBean _bean : mTriggerConditionBeans) {
//                condications.add(_bean.getConditionName());
//            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void dataUpdate() {
        mStepBean2s = App.getDaoSession().getStepBean2Dao().queryBuilder()
                .where(StepBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(StepBean2Dao.Properties.SequenceNumber).list();
        mStepAdapter.notifyDataSetChanged();
        measuredItems.clear();
        for (StepBean2 _bean : mStepBean2s) {
            if (_bean.getMeasureItems() != null)
                measuredItems.addAll(_bean.getMeasureItems());
        }
        // 所有参数被测量完毕;
        if (measuredItems.size() == mParameterBean2s.size()) {

        } else {
            isAutoSwitch.setChecked(false);
            mStoreBean.setStoreMode(0);
            App.getDaoSession().getStoreBeanDao().insertOrReplace(mStoreBean);
        }
    }


    class StepAdapter extends RecyclerView.Adapter<ViewHolder> {

        public StepAdapter() {

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(getContext(), parent, R.layout.step_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            StepBean2 _bean = mStepBean2s.get(position);

            holder.setText(R.id.step_tv, String.valueOf(_bean.getSequenceNumber() + 1));

            TriggerConditionBean _tBean = App.getDaoSession().getTriggerConditionBeanDao().load(_bean.getConditionID());
            if (_tBean != null) {
                holder.setText(R.id.condition_tv, _tBean.getConditionName());
            } else {
                holder.setText(R.id.condition_tv, R.string.press_save);
            }

            if (_bean.getMeasureItems() != null) {
                String _str = "";
                for (String item : _bean.getMeasureItems()) {
                    _str += "M" + (Integer.parseInt(item) + 1) + ".";
                }
                holder.setText(R.id.measure_item_tv, _str);
            }

            /*
            holder.setOnClickListener(R.id.data_layout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StepEditDialog _dialog = new StepEditDialog(getContext(), null);
                    // _dialog.setDataUpdateInterface(ParameterManagement2Activity.this);
                    _dialog.show();
                }
            });
             */

            holder.setOnLongClickListener(R.id.data_layout, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog builder = new AlertDialog.Builder(getContext())
                            .create();
                    builder.show();
                    if (builder.getWindow() == null) return false;
                    builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
                    TextView msg = builder.findViewById(R.id.tv_msg);
                    Button cancel = builder.findViewById(R.id.btn_cancle);
                    Button sure = builder.findViewById(R.id.btn_sure);
                    msg.setText("是否删除 " + getResources().getString(R.string.step_sequence) + _bean.getSequenceNumber() + " ?");
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**/
                            App.getDaoSession().getStepBean2Dao().queryBuilder().where(StepBean2Dao.Properties.Id.eq(_bean.getId())).buildDelete().executeDeleteWithoutDetachingEntities();
                            for (StepBean2 _bean2 : mStepBean2s) {
                                if (_bean2.getSequenceNumber() > _bean.getSequenceNumber()) {
                                    _bean2.setSequenceNumber(_bean2.getSequenceNumber() - 1);
                                    App.getDaoSession().getStepBean2Dao().insertOrReplace(_bean2);
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
            return mStepBean2s.size();
        }
    }
}
