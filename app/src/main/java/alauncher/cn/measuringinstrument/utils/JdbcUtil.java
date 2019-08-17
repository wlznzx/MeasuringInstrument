package alauncher.cn.measuringinstrument.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
                                 String result, String ng_reason, String operator, String operate_time) throws Exception {
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
        m1pstmt.setString(2, "M1");
        m1pstmt.setFloat(3, (float) 99.6);
        m1pstmt.setFloat(4, (float) 99.6);
        m1pstmt.setString(5, "");
        m1pstmt.setString(6, "");
        m1pstmt.executeUpdate();
        close(pstmt, conn);
        return 1;
    }
}
