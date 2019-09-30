package alauncher.cn.measuringinstrument.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;

public class JdbcUtil {
    //url
    private static String url = null;
    //user
    private static String user = null;
    //password
    private static String password = null;
    //驱动程序类
    private static String driverClass = null;

    /**
     * 获取连接方法
     */
    public static Connection getConnection() {
        try {
            // Connection conn = DriverManager.getConnection(url, user, password);
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager
                    .getConnection("jdbc:postgresql://47.98.58.40:5432/NT_CLOUD",
                            "dfqtech", "dfqtech2016");
            return c;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 释放资源的方法
     */
    public static void close(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    public static int addResult(String factory_code, String machine_code, int prog_id, String serial_number,
                                String result, String ng_reason, String operator, String operate_time) throws Exception {
        Connection con = getConnection();
        // String sql = "insert into aistu values('" + name + "'," + id + ",'" + age + "','" + email + "','" + tel + "','" + salary + "','" + riqi + "')";
        String sql = "insert into ntqc_result (factory_code,machine_code,prog_id,serial_number,result,ng_reason,operator,operate_time) "
                + "values('" + factory_code + "','" + machine_code + "','" + prog_id + "','" + serial_number + "','" + result + "','" + ng_reason + "','" + operator + "','" + operate_time + "')";
        //        + "VALUES ('TEFA', 'Paul002', '32', '28','OK','Not','ADMIN','2019-08-16 15:50:57' );";
        Statement stmt = con.createStatement();//获取statement
        int re = stmt.executeUpdate(sql);
        android.util.Log.d("wlDebug", "result = " + re);
        close(stmt, con);
        return re;
    }

    public static int selectDevice(String machine_code) {
        int count = 0;
        Connection con = getConnection();
        String sql = "select count(*) from ntqc_equipment where machine_code = '" + machine_code + "'";
        try {

            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = null;
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;

    }

    public static int addDevice(String factory_code, String factory_name, String machine_code, String machine_name, String manufacturer, String rmk, String operator) {
        Connection con = getConnection();
        String sql = "insert into ntqc_equipment (factory_code,factory_name,machine_code,machine_name,manufacturer,manufacture_date,rmk,operator,operate_time) values (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, factory_code);
            pstmt.setString(2, factory_name);
            pstmt.setString(3, machine_code);
            pstmt.setString(4, machine_name);
            pstmt.setString(5, manufacturer);
            pstmt.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setString(7, rmk);
            pstmt.setString(8, operator);
            pstmt.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
            return pstmt.executeUpdate();//执行sql
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int updateDevice(String factory_code, String factory_name, String machine_code, String machine_name, String manufacturer, String rmk, String operator) {
        Connection conn = getConnection();
        String sql = "update ntqc_equipment set factory_code=?,factory_name=?,machine_name=?,manufacturer=?,rmk=?,operator=?,operate_time=? where machine_code=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, factory_code);
            pstmt.setString(2, factory_name);
            pstmt.setString(3, machine_name);
            pstmt.setString(4, manufacturer);
            pstmt.setString(5, rmk);
            pstmt.setString(6, operator);
            pstmt.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setString(8, machine_code);
            return pstmt.executeUpdate();//执行sql
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static int insertOrReplace(String factory_code, String factory_name, String machine_code, String machine_name, String manufacturer, String rmk, String operator) {
        if (selectDevice(machine_code) > 0) {
            updateDevice(factory_code, factory_name, machine_code, machine_name, manufacturer, rmk, operator);
        } else {
            addDevice(factory_code, factory_name, machine_code, machine_name, manufacturer, rmk, operator);
        }
        return 1;
    }


    public static int addResult2(String factory_code, String machine_code, int prog_id, String serial_number,
                                 final ResultBean _bean) throws Exception {
        Connection conn = getConnection();
        String sql = "insert into ntqc_result (factory_code,machine_code,prog_id,serial_number,result,ng_reason,operator,operate_time) VALUES (?,?,?,?,?,?,?,?);";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
        pstmt.setString(1, factory_code);
        pstmt.setString(2, machine_code);
        pstmt.setInt(3, prog_id);
        pstmt.setString(4, serial_number);
        pstmt.setString(5, _bean.getResult());
        pstmt.setString(6, "");
        pstmt.setString(7, _bean.getHandlerAccout());
        pstmt.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
        pstmt.executeUpdate();//执行sql                                                                             int autoInckey = -1;
        ResultSet rs = pstmt.getGeneratedKeys(); //获取结果
        int autoIncKey = 0;
        if (rs.next()) {
            autoIncKey = rs.getInt(1);//取得ID
            android.util.Log.d("wlDebug", "result = " + autoIncKey);
        } else {
            // throw an exception from here
        }

        String result_detail_sql = "insert into ntqc_result_detail (result_id,name,m_value,r_value,g_value,e_value) VALUES (?,?,?,?,?,?);";
        PreparedStatement m1pstmt = conn.prepareStatement(result_detail_sql, Statement.RETURN_GENERATED_KEYS);
        m1pstmt.setInt(1, autoIncKey);
        m1pstmt.setString(2, "1");
        m1pstmt.setFloat(3, (float) _bean.getM1());
        m1pstmt.setFloat(4, 0);
        m1pstmt.setString(5, _bean.getM1_group());
        m1pstmt.setString(6, _bean.getEvent());
        m1pstmt.executeUpdate();

        PreparedStatement m2pstmt = conn.prepareStatement(result_detail_sql, Statement.RETURN_GENERATED_KEYS);
        m2pstmt.setInt(1, autoIncKey);
        m2pstmt.setString(2, "2");
        m2pstmt.setFloat(3, (float) _bean.getM2());
        m2pstmt.setFloat(4, 0);
        m2pstmt.setString(5, _bean.getM2_group());
        m2pstmt.setString(6, _bean.getEvent());
        m2pstmt.executeUpdate();

        PreparedStatement m3pstmt = conn.prepareStatement(result_detail_sql, Statement.RETURN_GENERATED_KEYS);
        m3pstmt.setInt(1, autoIncKey);
        m3pstmt.setString(2, "3");
        m3pstmt.setFloat(3, (float) _bean.getM3());
        m3pstmt.setFloat(4, 0);
        m3pstmt.setString(5, _bean.getM3_group());
        m3pstmt.setString(6, _bean.getEvent());
        m3pstmt.executeUpdate();

        PreparedStatement m4pstmt = conn.prepareStatement(result_detail_sql, Statement.RETURN_GENERATED_KEYS);
        m4pstmt.setInt(1, autoIncKey);
        m4pstmt.setString(2, "4");
        m4pstmt.setFloat(3, (float) _bean.getM4());
        m4pstmt.setFloat(4, 0);
        m4pstmt.setString(5, _bean.getM4_group());
        m4pstmt.setString(6, _bean.getEvent());
        m4pstmt.executeUpdate();

        m2pstmt.close();
        m3pstmt.close();
        m4pstmt.close();
        close(pstmt, conn);
        return 1;
    }

    public static int addParamConfig(String factory_code, String machine_code, int prog_id, String prog_name, String param_key,
                                     String param_name, String type,
                                     float warning_up, float warning_low, String rmk, ParameterBean _bean) throws Exception {
        Connection conn = getConnection();
        String sql = "insert into ntqc_param_config (factory_code,machine_code,prog_id," +
                "prog_name,param_key,param_name,type,nominal_value,lower_tolerance,upper_tolerance,warning_up,warning_low,rmk) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";

//        String sql = "insert into ntqc_param_config (factory_code,machine_code,prog_id," +
//                "prog_name,param_key,param_name,type,nominal_value,lower_tolerance,upper_tolerance,warning_up,warning_low,rmk) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
        pstmt.setString(1, factory_code);
        pstmt.setString(2, machine_code);
        pstmt.setInt(3, App.getSetupBean().getCodeID());
        pstmt.setString(4, App.getCodeName());
        pstmt.setString(5, "M1");
        pstmt.setString(6, _bean.getM1_describe());
        pstmt.setString(7, type);
        pstmt.setFloat(8, Float.valueOf(String.valueOf(_bean.getM1_nominal_value())));
        pstmt.setFloat(9, (float) _bean.getM1_lower_tolerance_value());
        pstmt.setFloat(10, (float) _bean.getM1_upper_tolerance_value());
        pstmt.setFloat(11, warning_up);
        pstmt.setFloat(12, warning_low);
        pstmt.setString(13, rmk);
        pstmt.executeUpdate();//执行sql
//        ResultSet rs = pstmt.getGeneratedKeys(); //获取结果
//        int autoIncKey = 0;
//        if (rs.next()) {
//            autoIncKey = rs.getInt(1);//取得ID
//            android.util.Log.d("wlDebug", "result = " + autoIncKey);
//        } else {
//        }
        /*
        PreparedStatement pstmt2 = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
        pstmt2.setString(1, factory_code);
        pstmt2.setString(2, machine_code);
        pstmt2.setInt(3, prog_id);
        pstmt2.setString(4, prog_name);
        pstmt2.setString(5, param_key);
        pstmt2.setString(6, param_name);
        pstmt2.setString(7, type);
        pstmt2.setFloat(8, (float) _bean.getM2_nominal_value());
        pstmt2.setFloat(9, (float) _bean.getM2_lower_tolerance_value());
        pstmt2.setFloat(10, (float) _bean.getM2_upper_tolerance_value());
        pstmt2.setFloat(11, warning_up);
        pstmt2.setFloat(12, warning_low);
        pstmt2.setString(13, rmk);
        pstmt2.executeUpdate();//执行sql


        PreparedStatement pstmt3 = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
        pstmt3.setString(1, factory_code);
        pstmt3.setString(2, machine_code);
        pstmt3.setInt(3, prog_id);
        pstmt3.setString(4, prog_name);
        pstmt3.setString(5, param_key);
        pstmt3.setString(6, param_name);
        pstmt3.setString(7, type);
        pstmt3.setFloat(8, (float) _bean.getM3_nominal_value());
        pstmt3.setFloat(9, (float) _bean.getM3_lower_tolerance_value());
        pstmt3.setFloat(10, (float) _bean.getM3_upper_tolerance_value());
        pstmt3.setFloat(11, warning_up);
        pstmt3.setFloat(12, warning_low);
        pstmt3.setString(13, rmk);
        pstmt3.executeUpdate();//执行sql

        PreparedStatement pstmt4 = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
        pstmt4.setString(1, factory_code);
        pstmt4.setString(2, machine_code);
        pstmt4.setInt(3, prog_id);
        pstmt4.setString(4, prog_name);
        pstmt4.setString(5, param_key);
        pstmt4.setString(6, param_name);
        pstmt4.setString(7, type);
        pstmt4.setFloat(8, (float) _bean.getM4_nominal_value());
        pstmt4.setFloat(9, (float) _bean.getM4_lower_tolerance_value());
        pstmt4.setFloat(10, (float) _bean.getM4_upper_tolerance_value());
        pstmt4.setFloat(11, warning_up);
        pstmt4.setFloat(12, warning_low);
        pstmt4.setString(13, rmk);
        pstmt4.executeUpdate();//执行sql
        */
        pstmt.setString(5, "M2");
        pstmt.setString(6, _bean.getM2_describe());
        pstmt.setFloat(8, (float) _bean.getM2_nominal_value());
        pstmt.setFloat(9, (float) _bean.getM2_lower_tolerance_value());
        pstmt.setFloat(10, (float) _bean.getM2_upper_tolerance_value());
        pstmt.executeUpdate();//执行sql

        pstmt.setString(5, "M3");
        pstmt.setString(6, _bean.getM3_describe());
        pstmt.setFloat(8, (float) _bean.getM3_nominal_value());
        pstmt.setFloat(9, (float) _bean.getM3_lower_tolerance_value());
        pstmt.setFloat(10, (float) _bean.getM3_upper_tolerance_value());
        pstmt.executeUpdate();//执行sql

        pstmt.setString(5, "M4");
        pstmt.setString(6, _bean.getM4_describe());
        pstmt.setFloat(8, (float) _bean.getM4_nominal_value());
        pstmt.setFloat(9, (float) _bean.getM4_lower_tolerance_value());
        pstmt.setFloat(10, (float) _bean.getM4_upper_tolerance_value());
        pstmt.executeUpdate();//执行sql

        return 1;
    }
}
