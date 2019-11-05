package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.CriticalBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBeanDao;
import butterknife.BindView;
import butterknife.OnClick;


public class StoreActivity extends BaseOActivity {

    @BindView(R.id.store_mode_rg)
    RadioGroup storeModeRG;

    @BindView(R.id.store_value_sp)
    Spinner storeValueSP;

    @BindView(R.id.store_upper_limit_edt)
    EditText storeUpperLimitEdt;

    @BindView(R.id.store_lower_limit_edt)
    EditText storeLowerLimitEdt;

    @BindView(R.id.store_time)
    EditText storeDelayTime;

    StoreBeanDao mStoreBeanDao;

    StoreBean mStoreBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_store);
    }

    @Override
    protected void initView() {
        mStoreBeanDao = App.getDaoSession().getStoreBeanDao();
        mStoreBean = mStoreBeanDao.load(App.SETTING_ID);

        storeModeRG.check(getModeID(mStoreBean.storeMode));
        storeUpperLimitEdt.setText("" + mStoreBean.getUpLimitValue().get(0));
        storeLowerLimitEdt.setText("" + mStoreBean.getLowLimitValue().get(0));
        storeDelayTime.setText("" + mStoreBean.getDelayTime());
        storeValueSP.setSelection(mStoreBean.getMValue());

        storeValueSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storeUpperLimitEdt.setText(mStoreBean.getUpLimitValue().get(position));
                storeLowerLimitEdt.setText(mStoreBean.getLowLimitValue().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private StoreBean view2Bean() {
        mStoreBean.setId(App.SETTING_ID);
        mStoreBean.setMValue(storeValueSP.getSelectedItemPosition());
        mStoreBean.setStoreMode(storeModeRG.getCheckedRadioButtonId() == R.id.store_auto ? 1 : 2);
        try {
//            _bean.setUpLimitValue(Double.valueOf(storeUpperLimitEdt.getText().toString().trim()));
//            _bean.setLowLimitValue(Double.valueOf(storeLowerLimitEdt.getText().toString().trim()));

            Double upLimitValue = Double.valueOf(storeUpperLimitEdt.getText().toString().trim());
            Double lowLimitValue = Double.valueOf(storeLowerLimitEdt.getText().toString().trim());
            mStoreBean.getUpLimitValue().set(storeValueSP.getSelectedItemPosition(), upLimitValue.toString());
            mStoreBean.getLowLimitValue().set(storeValueSP.getSelectedItemPosition(), lowLimitValue.toString());

            Log.d("wlDebug", mStoreBean.toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.input_fail, Toast.LENGTH_SHORT).show();
            return null;
        }

        mStoreBean.setDelayTime(Integer.valueOf(storeDelayTime.getText().toString().trim()));
        return mStoreBean;
    }

    @OnClick(R.id.save_btn)
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                mStoreBeanDao.insertOrReplace(view2Bean());
                Toast.makeText(this, "保存成功.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private int getModeID(int code) {
        switch (code) {
            case 1:
                return R.id.store_auto;
            case 2:
                return R.id.store_manual;
        }
        return R.id.store_auto;
    }


}
