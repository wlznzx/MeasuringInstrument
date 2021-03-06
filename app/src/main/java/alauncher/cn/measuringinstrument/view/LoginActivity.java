package alauncher.cn.measuringinstrument.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.MainActivity;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.RememberPasswordBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class LoginActivity extends BaseOActivity {

    @BindView(R.id.login_user_name_edt)
    public EditText loginUserNameEdt;

    @BindView(R.id.login_user_password_edt)
    public EditText loginUserPasswordEdt;

    @BindView(R.id.login_btn)
    public Button loginBtn;

    @BindView(R.id.is_remember_cb)
    public CheckBox isRemeberCB;

    private UserDao mUserDao;

    private List<User> users;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_login_new);
    }

    @Override
    protected void initView() {
        mUserDao = App.getDaoSession().getUserDao();
        users = mUserDao.loadAll();
//        for (User user : users) {
//            android.util.Log.d("wlDebug", user.toString());
//        }
        actionTips.setVisibility(View.INVISIBLE);

        RememberPasswordBean _bean = App.getDaoSession().getRememberPasswordBeanDao().load(App.SETTING_ID);
        if (_bean.getIsRemeber() && _bean.getLogined()) {
            loginUserNameEdt.setText(_bean.getAccount());
            loginUserPasswordEdt.setText(_bean.getPassowrd());
        }


        isRemeberCB.setChecked(_bean.getIsRemeber());

        /*
        DeviceInfoBean _dBean = App.getDeviceInfo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JdbcUtil.insertOrReplace(_dBean.getFactoryCode(), _dBean.getFactoryName(), _dBean.getDeviceCode(), _dBean.getDeviceName(), _dBean.getManufacturer(),
                        _dBean.getRmk(), App.handlerAccout);
                Log.d("wlDebug", "count = " + JdbcUtil.selectDevice("SXFA1011"));
            }
        }).start();
        */
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
            String factory_code = App.factory_code;
            int prog_id = App.getSetupBean().getCodeID();
            String param_key = "M1";
            String sql = "select * from ntqc_param_config where factory_code = " +
                    factory_code + " and machine_code = " + App.machine_code + " and param_key = " + param_key + " and prog_id = " + prog_id;
            android.util.Log.d("wlDebug", "sql = " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String result = rs.getString("result");
                android.util.Log.d("wlDebug", "id = " + id + " result = " + result);
            }

            long timg = System.currentTimeMillis();

//            String sql = "INSERT INTO ntqc_equipment (factory_code,factory_name,machine_code,machine_name," +
//                    "manufacturer,manufacture_date,rmk,operator,operate_time) VALUES (?,?,?,?,?,?,?,?,?);";
//            PreparedStatement pstmt = (PreparedStatement) c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
//            pstmt.setString(1, App.factory_code);
//            pstmt.setString(2, App.factory_code);
//            pstmt.setString(3, App.machine_code);
//            pstmt.setString(4, App.machine_code);
//            pstmt.setString(5, "恩梯量仪");
//            pstmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
//            pstmt.setString(7, "");
//            pstmt.setString(8, "吴工");
//            pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
//            pstmt.executeUpdate();//执行sql


//            String sql = "INSERT INTO ntqc_equipment_status (equipment_id,status,update_time) VALUES (?,?,?);";
//            PreparedStatement pstmt = (PreparedStatement) c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
//            pstmt.setInt(1, 31);
//            pstmt.setString(2, "0");
//            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
//            pstmt.executeUpdate();//执行sql
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void goSQL() {
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

        RememberPasswordBean _bean = App.getDaoSession().getRememberPasswordBeanDao().load(App.SETTING_ID);
        if (_bean.getIsRemeber()) {
            _bean.setLogined(true);
            _bean.setAccount(accoutStr);
            _bean.setPassowrd(passwordStr);
            App.getDaoSession().getRememberPasswordBeanDao().insertOrReplace(_bean);
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.quick_login_btn)
    public void onQuickBtn(View v) {
        App.handlerAccout = "operator";
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @OnCheckedChanged(R.id.is_remember_cb)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        RememberPasswordBean _bean = App.getDaoSession().getRememberPasswordBeanDao().load(App.SETTING_ID);
        _bean.setIsRemeber(isChecked);
        App.getDaoSession().getRememberPasswordBeanDao().insertOrReplace(_bean);
    }

    @Override
    public void onBackPressed() {
    }

}
