package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.greendao.DaoException;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBeanDao;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;


public class MGroupActivity extends BaseOActivity {

    @BindViews({R.id.a_upper_limit_edt, R.id.b_upper_limit_edt, R.id.c_upper_limit_edt, R.id.d_upper_limit_edt})
    public EditText upperLimits[];

    @BindViews({R.id.a_lower_limit_edt, R.id.b_lower_limit_edt, R.id.c_lower_limit_edt, R.id.d_lower_limit_edt})
    public EditText lowerLimits[];

    @BindViews({R.id.a_describe, R.id.b_describe, R.id.c_describe, R.id.d_describe})
    public EditText describes[];

    @BindView(R.id.save_btn)
    public TextView saveBtn;

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

        mDao = App.getDaoSession().getGroupBeanDao();
        GroupBean _bean;
        try {
            _bean = mDao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(mIndex)).unique();
        } catch (DaoException e) {
            _bean = mDao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(mIndex)).list().get(0);
        }
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
        // GroupBean _bean = mDao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(mIndex)).unique();
        GroupBean mGroupBean = view2Bean();
        if (mGroupBean.getA_upper_limit() < mGroupBean.getA_lower_limit()) {
            Toast.makeText(this, "分组A的上限必须大于下限，请检查输入.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mGroupBean.getB_upper_limit() < mGroupBean.getB_lower_limit()) {
            Toast.makeText(this, "分组B的上限必须大于下限，请检查输入.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mGroupBean.getC_upper_limit() < mGroupBean.getC_lower_limit()) {
            Toast.makeText(this, "分组C的上限必须大于下限，请检查输入.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mGroupBean.getD_upper_limit() < mGroupBean.getD_lower_limit()) {
            Toast.makeText(this, "分组D的上限必须大于下限，请检查输入.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<GroupBean> _beans = mDao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(mIndex)).list();

        if (_beans != null) {
            for (GroupBean _bean : mDao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(mIndex)).list()) {
                mDao.delete(_bean);
            }
        }
//        if (_bean == null) {
        App.getDaoSession().getGroupBeanDao().insert(view2Bean());
//        } else {
//            GroupBean saveBean = view2Bean();
//            saveBean.setId(_bean.getId());
//            App.getDaoSession().getGroupBeanDao().update(saveBean);
//        }
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.clear_btn)
    public void onClear(View v) {
        final AlertDialog builder = new AlertDialog.Builder(MGroupActivity.this)
                .create();
        builder.show();
        if (builder.getWindow() == null) {
            return;
        }
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancel = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancel == null || sure == null) {
            return;
        }
        msg.setText("确认清空该组数据？");
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
                GroupBean _bean = mDao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(mIndex)).unique();
                if (_bean != null) {
                    mDao.delete(_bean);
                }
                finish();
            }
        });
    }

    private GroupBean view2Bean() {
        GroupBean _bean = new GroupBean();
        _bean.setCode_id(App.getSetupBean().getCodeID());
        _bean.setM_index(mIndex);
        // 分组的上区间
        if (!upperLimits[0].getText().toString().equals("")) {
            _bean.setA_upper_limit(Double.valueOf(upperLimits[0].getText().toString().trim()));
        }
        if (!upperLimits[1].getText().toString().equals("")) {
            _bean.setB_upper_limit(Double.valueOf(upperLimits[1].getText().toString().trim()));
        }
        if (!upperLimits[2].getText().toString().equals("")) {
            _bean.setC_upper_limit(Double.valueOf(upperLimits[2].getText().toString().trim()));
        }
        if (!upperLimits[3].getText().toString().equals("")) {
            _bean.setD_upper_limit(Double.valueOf(upperLimits[3].getText().toString().trim()));
        }
        // 分组的下区间
        if (!lowerLimits[0].getText().toString().equals("")) {
            _bean.setA_lower_limit(Double.valueOf(lowerLimits[0].getText().toString().trim()));
        }
        if (!lowerLimits[1].getText().toString().equals("")) {
            _bean.setB_lower_limit(Double.valueOf(lowerLimits[1].getText().toString().trim()));
        }
        if (!lowerLimits[2].getText().toString().equals("")) {
            _bean.setC_lower_limit(Double.valueOf(lowerLimits[2].getText().toString().trim()));
        }
        if (!lowerLimits[3].getText().toString().equals("")) {
            _bean.setD_lower_limit(Double.valueOf(lowerLimits[3].getText().toString().trim()));
        }
        // 分组描述
        _bean.setA_describe(describes[0].getText().toString());
        _bean.setB_describe(describes[1].getText().toString());
        _bean.setC_describe(describes[2].getText().toString());
        _bean.setD_describe(describes[3].getText().toString());
        return _bean;
    }

}
