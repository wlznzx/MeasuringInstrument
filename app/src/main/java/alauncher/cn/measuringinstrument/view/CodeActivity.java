package alauncher.cn.measuringinstrument.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.User;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;


public class CodeActivity extends BaseOActivity {
    /**/
    @BindView(R.id.code_rg)
    RadioGroup mCodeRadioGroup;

    @BindViews({R.id.code_1_edt, R.id.code_2_edt, R.id.code_3_edt, R.id.code_4_edt, R.id.code_5_edt, R.id.code_6_edt, R.id.code_7_edt, R.id.code_8_edt, R.id.code_9_edt, R.id.code_10_edt})
    public EditText codeEdts[];

    int codeID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_code);
    }

    @Override
    protected void initView() {
        for (EditText edt : codeEdts) {
            edt.setEnabled(false);
        }
        codeID = App.getSetupBean().getCodeID();
        mCodeRadioGroup.check(getCodeID(codeID));
        mCodeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    //男
                    case R.id.code_1:
                        codeID = 1;
                        //男
                        break;
                    //女
                    case R.id.code_2:
                        codeID = 2;
                        //女
                        break;
                    case R.id.code_3:
                        codeID = 3;
                        //女
                        break;
                    case R.id.code_4:
                        codeID = 4;
                        //女
                        break;
                    case R.id.code_5:
                        codeID = 5;
                        //女
                        break;
                    case R.id.code_6:
                        codeID = 6;
                        //女
                        break;
                    case R.id.code_7:
                        codeID = 7;
                        //女
                        break;
                    case R.id.code_8:
                        codeID = 8;
                        //女
                        break;
                    case R.id.code_9:
                        codeID = 9;
                        //女
                        break;
                    case R.id.code_10:
                        codeID = 10;
                        //女
                        break;
                    case R.id.code_11:
                        codeID = 11;
                        //女
                        break;
                }
            }
        });

        for (int i = 0; i < codeEdts.length; i++) {
            CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) (i + 1));
            if (_bean != null) {
                codeEdts[i].setText(_bean.getName());
            }
        }

    }

    private int getCodeID(int code) {
        switch (code) {
            case 1:
                return R.id.code_1;
            case 2:
                return R.id.code_2;
            case 3:
                return R.id.code_3;
            case 4:
                return R.id.code_4;
            case 5:
                return R.id.code_5;
            case 6:
                return R.id.code_6;
            case 7:
                return R.id.code_7;
            case 8:
                return R.id.code_8;
            case 9:
                return R.id.code_9;
            case 10:
                return R.id.code_10;
            case 11:
                return R.id.code_11;
        }
        return R.id.code_1;
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        for (int i = 0; i < codeEdts.length; i++) {
            CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) (i + 1));
            if (_bean == null) {
                _bean = new CodeBean((i + 1), codeEdts[i].getText().toString().trim(), "", "");
            } else {
                _bean.setCodeID((i + 1));
                _bean.setName(codeEdts[i].getText().toString().trim());
            }
            App.getDaoSession().getCodeBeanDao().insertOrReplace(_bean);
        }
        */
    }

    @OnClick({R.id.set_btn, R.id.set_as_btn})
    public void onSetBtnClick(View v) {
        switch (v.getId()) {
            case R.id.set_btn:
                Intent intent = new Intent(this, CodeDetailActivity.class);
                intent.putExtra("Title", R.string.code_set);
                startActivity(intent);
                break;
            case R.id.set_as_btn:
                SetupBean _sbean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
                _sbean.setCodeID(codeID);
                App.getDaoSession().getSetupBeanDao().insertOrReplace(_sbean);
                CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) App.getSetupBean().getCodeID());
                User user = App.getDaoSession().getUserDao().load(App.handlerAccout);
                String _name = App.handlerAccout;
                if (user != null) {
                    _name = user.getName();
                }
                if (_bean != null) {
                    actionTips.setText(_name + " " + _bean.getName());
                } else {
                    actionTips.setText(_name + " 程序" + App.getSetupBean().getCodeID());
                }
                break;
        }
    }

}
