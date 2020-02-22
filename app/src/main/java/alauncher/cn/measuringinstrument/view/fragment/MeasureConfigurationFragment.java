package alauncher.cn.measuringinstrument.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.MeasureConfigurationBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MeasureConfigurationFragment extends Fragment {

    @BindView(R.id.status_sp)
    public Spinner showModeSpinner;

    @BindView(R.id.measure_chart_show_switch)
    public Switch chartShowSwitch;

    private Unbinder unbinder;

    private MeasureConfigurationBean mMeasureConfigurationBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.measure_configration_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void initView() {
        mMeasureConfigurationBean = App.getDaoSession().getMeasureConfigurationBeanDao().load((long) App.getSetupBean().getCodeID());
        chartShowSwitch.setChecked(mMeasureConfigurationBean.getIsShowChart());
        chartShowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mMeasureConfigurationBean.setIsShowChart(b);
                App.getDaoSession().getMeasureConfigurationBeanDao().insertOrReplace(mMeasureConfigurationBean);
            }
        });
        showModeSpinner.setSelection(mMeasureConfigurationBean.getMeasureMode());
        showModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMeasureConfigurationBean.setMeasureMode(i);
                App.getDaoSession().getMeasureConfigurationBeanDao().insertOrReplace(mMeasureConfigurationBean);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }
}
