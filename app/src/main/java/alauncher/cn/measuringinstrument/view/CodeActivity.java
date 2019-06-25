package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import butterknife.BindView;
import butterknife.OnCheckedChanged;


public class CodeActivity extends BaseActivity {

    @BindView(R.id.code_rg)
    RadioGroup mCodeRadioGroup;

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
                SetupBean _bean = App.getDaoSession().getSetupBeanDao().load(App.SETTING_ID);
                _bean.setCodeID(codeID);
                App.getDaoSession().getSetupBeanDao().update(_bean);
                actionTips.setText(App.handlerAccout + " 程序" + App.getSetupBean().getCodeID());
            }
        });
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

}
