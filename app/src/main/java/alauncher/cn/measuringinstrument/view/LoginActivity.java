package alauncher.cn.measuringinstrument.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.MainActivity;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import alauncher.cn.measuringinstrument.utils.Arith;
import alauncher.cn.measuringinstrument.utils.DateUtils;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import butterknife.BindView;
import butterknife.OnClick;

import java.sql.Connection;
import java.sql.DriverManager;

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

        double _b = Arith.getStandardDeviation(null);
        android.util.Log.d("wlDebug", "_b = " + _b);

    }

    private void tPostgreSQL() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://47.98.58.40:5432/NT_CLOUD",
                            "dfqtech", "dfqtech2016");
            android.util.Log.d("wlDebug", "connect success.");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("select * from ntqc_result limit 10");
            while (rs.next()) {
                int id = rs.getInt("id");
                String result = rs.getString("result");
                android.util.Log.d("wlDebug", "id = " + id + " result = " + result);
            }

            long timg = System.currentTimeMillis();

            String sql = "INSERT INTO ntqc_result (factory_code,machine_code,prog_id,serial_number,result,ng_reason,operator,operate_time) "
                    + "VALUES ('TEFA', 'Paul002', '32', '28','OK','Not','ADMIN','2019-08-16 15:50:57' );";
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    private void goSQL() {
        try {
            JdbcUtil.addResult2("恩梯", "Paul002", 1, "Mac",
                    "OK", "no reason", "ADMIN", DateUtils.getDate(System.currentTimeMillis()),
                    "M1", "M2", "M3", "M4",
                    8, 6, 4, 5,
                    "A", "B", "C", "D",
                    "换刀", "换刀", "换刀", "换刀"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.login_btn)
    public void onLogin(View v) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                goSQL();
            }
        }).start();

        // CrashReport.testJavaCrash();

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


    @Override
    public void onBackPressed() {
    }

}
