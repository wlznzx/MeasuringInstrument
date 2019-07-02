package alauncher.cn.measuringinstrument.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.MainActivity;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import butterknife.BindView;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_user_name_edt)
    public EditText loginUserNameEdt;

    @BindView(R.id.login_user_password_edt)
    public EditText loginUserPasswordEdt;

    @BindView(R.id.login_btn)
    public Button loginBtn;

    private UserDao mUserDao;

    private List<User> users;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initView() {
        mUserDao = App.getDaoSession().getUserDao();
        users = mUserDao.loadAll();
        for (User user : users) {
            android.util.Log.d("wlDebug", user.toString());
        }
        actionTips.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.login_btn)
    public void onLogin(View v) {
        String accoutStr = loginUserNameEdt.getText().toString().trim();
        if (accoutStr == null || accoutStr.equals("")) {
            Toast.makeText(this, R.string.username_notnull, Toast.LENGTH_SHORT).show();
            return;
        }
        String passwordStr = loginUserPasswordEdt.getText().toString().trim();
        if (passwordStr == null || passwordStr.equals("")) {
            Toast.makeText(this, R.string.password_notnull, Toast.LENGTH_SHORT).show();
            return;
        }

        User _user;
        _user = mUserDao.load(accoutStr);
        if (_user == null) {
            Toast.makeText(this, R.string.user_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!_user.getPassword().equals(passwordStr)) {
            Toast.makeText(this, R.string.password_error, Toast.LENGTH_SHORT).show();
            return;
        }
        App.handlerAccout = accoutStr;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
