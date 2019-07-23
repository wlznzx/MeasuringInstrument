package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBeanDao;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;


public class MGroupActivity extends BaseActivity {

    @BindViews({R.id.a_upper_limit_edt, R.id.b_upper_limit_edt, R.id.c_upper_limit_edt, R.id.d_upper_limit_edt})
    public EditText upperLimits[];

    @BindViews({R.id.a_lower_limit_edt, R.id.b_lower_limit_edt, R.id.c_lower_limit_edt, R.id.d_lower_limit_edt})
    public EditText lowerLimits[];

    @BindViews({R.id.a_describe, R.id.b_describe, R.id.c_describe, R.id.d_describe})
    public EditText describes[];

    @BindView(R.id.save_btn)
    public Button saveBtn;

    private GroupBeanDao mDao;

    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_m_group);
    }

    @Override
    protected void initView() {
        mIndex = getIntent().getIntExtra("M_INDEX", 0);

        android.util.Log.d("wlDebug", "mIndex = " + mIndex);

        mDao = App.getDaoSession().getGroupBeanDao();
        GroupBean _bean = mDao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(mIndex)).unique();
        if (_bean != null) {

            android.util.Log.d("wlDebug", _bean.toString());

            upperLimits[0].setText(_bean.getA_upper_limit() + "");
            upperLimits[1].setText(_bean.getB_upper_limit() + "");
            upperLimits[2].setText(_bean.getC_upper_limit() + "");
            upperLimits[3].setText(_bean.getD_upper_limit() + "");

            lowerLimits[0].setText(_bean.getA_lower_limit() + "");
            lowerLimits[1].setText(_bean.getB_lower_limit() + "");
            lowerLimits[2].setText(_bean.getC_lower_limit() + "");
            lowerLimits[3].setText(_bean.getD_lower_limit() + "");

            describes[0].setText(_bean.getA_describe());
            describes[1].setText(_bean.getB_describe());
            describes[2].setText(_bean.getC_describe());
            describes[3].setText(_bean.getD_describe());
        }
    }

    @OnClick(R.id.save_btn)
    public void onSave(View v) {
        GroupBean _bean = mDao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(mIndex)).unique();

        if (_bean == null) {
            App.getDaoSession().getGroupBeanDao().insert(view2Bean());
        } else {
            GroupBean saveBean = view2Bean();
            saveBean.setId(_bean.getId());
            App.getDaoSession().getGroupBeanDao().update(saveBean);
            // android.util.Log.d("wlDebug", "Save Bean " + _bean.toString());
        }
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    private GroupBean view2Bean() {
        GroupBean _bean = new GroupBean();
        _bean.setCode_id(App.getSetupBean().getCodeID());
        _bean.setM_index(mIndex);
        // 分组的上区间
        if (!upperLimits[0].getText().toString().equals(""))
            _bean.setA_upper_limit(Double.valueOf(upperLimits[0].getText().toString().trim()));
        if (!upperLimits[1].getText().toString().equals(""))
            _bean.setB_upper_limit(Double.valueOf(upperLimits[1].getText().toString().trim()));
        if (!upperLimits[2].getText().toString().equals(""))
            _bean.setC_upper_limit(Double.valueOf(upperLimits[2].getText().toString().trim()));
        if (!upperLimits[3].getText().toString().equals(""))
            _bean.setD_upper_limit(Double.valueOf(upperLimits[3].getText().toString().trim()));
        // 分组的下区间
        if (!lowerLimits[0].getText().toString().equals(""))
            _bean.setA_lower_limit(Double.valueOf(lowerLimits[0].getText().toString().trim()));
        if (!lowerLimits[1].getText().toString().equals(""))
            _bean.setB_lower_limit(Double.valueOf(lowerLimits[1].getText().toString().trim()));
        if (!lowerLimits[2].getText().toString().equals(""))
            _bean.setC_lower_limit(Double.valueOf(lowerLimits[2].getText().toString().trim()));
        if (!lowerLimits[3].getText().toString().equals(""))
            _bean.setD_lower_limit(Double.valueOf(lowerLimits[3].getText().toString().trim()));
        // 分组描述
        _bean.setA_describe(describes[0].getText().toString());
        _bean.setB_describe(describes[1].getText().toString());
        _bean.setC_describe(describes[2].getText().toString());
        _bean.setD_describe(describes[3].getText().toString());

        android.util.Log.d("wlDebug", _bean.toString());

        return _bean;
    }

}
