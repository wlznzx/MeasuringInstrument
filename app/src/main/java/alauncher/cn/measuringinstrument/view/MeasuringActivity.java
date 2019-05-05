package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.widget.MValueView;
import butterknife.BindView;


public class MeasuringActivity extends BaseActivity {

    @BindView(R.id.m1_value)
    public MValueView m1;

    @BindView(R.id.m2_value)
    public MValueView m2;

    @BindView(R.id.m3_value)
    public MValueView m3;

    @BindView(R.id.m4_value)
    public MValueView m4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_measuring);
    }

    @Override
    protected void initView() {
        m1.setMValue(1.8);
        m2.setMValue(-2.8);
        m3.setMValue(0.8);
        m4.setMValue(-0.4);
    }
}
