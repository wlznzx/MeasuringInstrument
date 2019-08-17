package alauncher.cn.measuringinstrument.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import alauncher.cn.measuringinstrument.bean.ParameterBean;

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
            android.util.Log.d("wlDebug", "connect success.");
            return c;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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


    public static int addResult2(String factory_code, String machine_code, int prog_id, String serial_number,
                                 String result, String ng_reason, String operator, String operate_time,
                                 String m1_name, String m2_name, String m3_name, String m4_name,
                                 float m1_value, float m2_value, float m3_value, float m4_value,
                                 String m1_group, String m2_group, String m3_group, String m4_group,
                                 String m1_event, String m2_event, String m3_event, String m4_event) throws Exception {
        Connection conn = getConnection();
        String sql = "insert into ntqc_result (factory_code,machine_code,prog_id,serial_number,result,ng_reason,operator,operate_time) VALUES (?,?,?,?,?,?,?,?);";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
        pstmt.setString(1, factory_code);
        pstmt.setString(2, machine_code);
        pstmt.setInt(3, prog_id);
        pstmt.setString(4, serial_number);
        pstmt.setString(5, result);
        pstmt.setString(6, ng_reason);
        pstmt.setString(7, operator);
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
        m1pstmt.setString(2, m1_name);
        m1pstmt.setFloat(3, m1_value);
        m1pstmt.setFloat(4, 0);
        m1pstmt.setString(5, m1_group);
        m1pstmt.setString(6, m1_event);
        m1pstmt.executeUpdate();

        PreparedStatement m2pstmt = conn.prepareStatement(result_detail_sql, Statement.RETURN_GENERATED_KEYS);
        m2pstmt.setInt(1, autoIncKey);
        m2pstmt.setString(2, m2_name);
        m2pstmt.setFloat(3, m2_value);
        m2pstmt.setFloat(4, 0);
        m2pstmt.setString(5, m2_group);
        m2pstmt.setString(6, m2_event);
        m2pstmt.executeUpdate();

        PreparedStatement m3pstmt = conn.prepareStatement(result_detail_sql, Statement.RETURN_GENERATED_KEYS);
        m3pstmt.setInt(1, autoIncKey);
        m3pstmt.setString(2, m3_name);
        m3pstmt.setFloat(3, m3_value);
        m3pstmt.setFloat(4, 0);
        m3pstmt.setString(5, m3_group);
        m3pstmt.setString(6, m3_event);
        m3pstmt.executeUpdate();

        PreparedStatement m4pstmt = conn.prepareStatement(result_detail_sql, Statement.RETURN_GENERATED_KEYS);
        m4pstmt.setInt(1, autoIncKey);
        m4pstmt.setString(2, m4_name);
        m4pstmt.setFloat(3, m4_value);
        m4pstmt.setFloat(4, 0);
        m4pstmt.setString(5, m4_group);
        m4pstmt.setString(6, m4_event);
        m4pstmt.executeUpdate();
        close(pstmt, conn);
        return 1;
    }


    public static int addParamConfig(String factory_code, String machine_code, int prog_id, String prog_name, String param_key,
                                     String param_name, String type, float nominal_value, float lower_tolerance, float upper_tolerance,
                                     float warning_up, float warning_low, String rmk, ParameterBean _bean) throws Exception {
        Connection conn = getConnection();
        String sql = "insert into ntqc_param_config (factory_code,machine_code,prog_id," +
                "prog_name,param_key,param_name,type,nominal_value,lower_tolerance,upper_tolerance,warning_up,warning_low,rmk) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
        pstmt.setString(1, factory_code);
        pstmt.setString(2, machine_code);
        pstmt.setInt(3, prog_id);
        pstmt.setString(4, prog_name);
        pstmt.setString(5, param_key);
        pstmt.setString(6, param_name);
        pstmt.setString(7, type);
        pstmt.setFloat(8, nominal_value);
        pstmt.setFloat(9, lower_tolerance);
        pstmt.setFloat(10, upper_tolerance);
        pstmt.setFloat(11, warning_up);
        pstmt.setFloat(12, warning_low);
        pstmt.setString(13, rmk);
        pstmt.executeUpdate();//执行sql
        ResultSet rs = pstmt.getGeneratedKeys(); //获取结果
        int autoIncKey = 0;
        if (rs.next()) {
            autoIncKey = rs.getInt(1);//取得ID
            android.util.Log.d("wlDebug", "result = " + autoIncKey);
        } else {
            // throw an exception from here
        }
        return autoIncKey;
    }
}
