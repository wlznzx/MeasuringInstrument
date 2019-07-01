package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 日期：2019/4/25 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class GroupBean {

    public long code_id;

    @NotNull
    public int m_index;

    public double a_upper_limit;
    public double a_lower_limit;

    public double b_upper_limit;
    public double b_lower_limit;

    public double c_upper_limit;
    public double c_lower_limit;

    public double d_upper_limit;
    public double d_lower_limit;

    public String a_describe;
    public String b_describe;
    public String c_describe;
    public String d_describe;

    @Generated(hash = 1260527221)
    public GroupBean(long code_id, int m_index, double a_upper_limit,
                     double a_lower_limit, double b_upper_limit, double b_lower_limit,
                     double c_upper_limit, double c_lower_limit, double d_upper_limit,
                     double d_lower_limit, String a_describe, String b_describe,
                     String c_describe, String d_describe) {
        this.code_id = code_id;
        this.m_index = m_index;
        this.a_upper_limit = a_upper_limit;
        this.a_lower_limit = a_lower_limit;
        this.b_upper_limit = b_upper_limit;
        this.b_lower_limit = b_lower_limit;
        this.c_upper_limit = c_upper_limit;
        this.c_lower_limit = c_lower_limit;
        this.d_upper_limit = d_upper_limit;
        this.d_lower_limit = d_lower_limit;
        this.a_describe = a_describe;
        this.b_describe = b_describe;
        this.c_describe = c_describe;
        this.d_describe = d_describe;
    }

    @Generated(hash = 405578774)
    public GroupBean() {
    }

    public long getCode_id() {
        return this.code_id;
    }

    public void setCode_id(long code_id) {
        this.code_id = code_id;
    }

    public int getM_index() {
        return this.m_index;
    }

    public void setM_index(int m_index) {
        this.m_index = m_index;
    }

    public double getA_upper_limit() {
        return this.a_upper_limit;
    }

    public void setA_upper_limit(double a_upper_limit) {
        this.a_upper_limit = a_upper_limit;
    }

    public double getA_lower_limit() {
        return this.a_lower_limit;
    }

    public void setA_lower_limit(double a_lower_limit) {
        this.a_lower_limit = a_lower_limit;
    }

    public double getB_upper_limit() {
        return this.b_upper_limit;
    }

    public void setB_upper_limit(double b_upper_limit) {
        this.b_upper_limit = b_upper_limit;
    }

    public double getB_lower_limit() {
        return this.b_lower_limit;
    }

    public void setB_lower_limit(double b_lower_limit) {
        this.b_lower_limit = b_lower_limit;
    }

    public double getC_upper_limit() {
        return this.c_upper_limit;
    }

    public void setC_upper_limit(double c_upper_limit) {
        this.c_upper_limit = c_upper_limit;
    }

    public double getC_lower_limit() {
        return this.c_lower_limit;
    }

    public void setC_lower_limit(double c_lower_limit) {
        this.c_lower_limit = c_lower_limit;
    }

    public double getD_upper_limit() {
        return this.d_upper_limit;
    }

    public void setD_upper_limit(double d_upper_limit) {
        this.d_upper_limit = d_upper_limit;
    }

    public double getD_lower_limit() {
        return this.d_lower_limit;
    }

    public void setD_lower_limit(double d_lower_limit) {
        this.d_lower_limit = d_lower_limit;
    }

    public String getA_describe() {
        return this.a_describe;
    }

    public void setA_describe(String a_describe) {
        this.a_describe = a_describe;
    }

    public String getB_describe() {
        return this.b_describe;
    }

    public void setB_describe(String b_describe) {
        this.b_describe = b_describe;
    }

    public String getC_describe() {
        return this.c_describe;
    }

    public void setC_describe(String c_describe) {
        this.c_describe = c_describe;
    }

    public String getD_describe() {
        return this.d_describe;
    }

    public void setD_describe(String d_describe) {
        this.d_describe = d_describe;
    }


    @Override
    public String toString() {
        return "GroupBean{" +
                "code_id=" + code_id +
                ", m_index=" + m_index +
                ", a_upper_limit=" + a_upper_limit +
                ", a_lower_limit=" + a_lower_limit +
                ", b_upper_limit=" + b_upper_limit +
                ", b_lower_limit=" + b_lower_limit +
                ", c_upper_limit=" + c_upper_limit +
                ", c_lower_limit=" + c_lower_limit +
                ", d_upper_limit=" + d_upper_limit +
                ", d_lower_limit=" + d_lower_limit +
                ", a_describe='" + a_describe + '\'' +
                ", b_describe='" + b_describe + '\'' +
                ", c_describe='" + c_describe + '\'' +
                ", d_describe='" + d_describe + '\'' +
                '}';
    }
}
