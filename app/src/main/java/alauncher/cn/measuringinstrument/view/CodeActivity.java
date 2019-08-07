package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import butterknife.BindView;
import butterknife.BindViews;


public class CodeActivity extends BaseActivity {

    @BindView(R.id.code_rg)
    RadioGroup mCodeRadioGroup;

    @BindViews({R.id.code_1_edt, R.id.code_2_edt, R.id.code_3_edt, R.id.code_4_edt, R.id.code_5_edt, R.id.code_6_edt, R.id.code_7_edt, R.id.code_8_edt, R.id.code_9_edt, R.id.code_10_edt})
    public EditText codeEdts[];

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
        mCodeRadioGroup.check(getCodeID(App.getSetupBean().getCodeID()));
        mCodeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int codeID = 1;
                switch (checkedId) {
                    //男
                    case R.id.code_1:
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
                        //女r
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
                SetupBean _bean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
                _bean.setCodeID(codeID);
                App.getDaoSession().getSetupBeanDao().update(_bean);
                CodeBean _CodeBean = App.getDaoSession().getCodeBeanDao().load((long) codeID);
                if (_CodeBean != null) {
                    actionTips.setText(App.handlerAccout + " " + _CodeBean.getName());
                } else {
                    actionTips.setText(App.handlerAccout + " 程序" + App.getSetupBean().getCodeID());
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
        for (int i = 0; i < codeEdts.length; i++) {
            if (App.getDaoSession().getCodeBeanDao().load((long) (i + 1)) == null) {
                App.getDaoSession().getCodeBeanDao().insert(new CodeBean((i + 1), codeEdts[i].getText().toString().trim()));
            } else {
                App.getDaoSession().getCodeBeanDao().update(new CodeBean((i + 1), codeEdts[i].getText().toString().trim()));
            }
        }
    }
}
