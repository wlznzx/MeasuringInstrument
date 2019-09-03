package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class ParameterBean {
    @Id
    public long code_id;

    public String m1_describe;

    public String m2_describe;

    public String m3_describe;

    public String m4_describe;

    public double m1_nominal_value;

    public double m2_nominal_value;

    public double m3_nominal_value;

    public double m4_nominal_value;

    public double m1_upper_tolerance_value;

    public double m2_upper_tolerance_value;

    public double m3_upper_tolerance_value;

    public double m4_upper_tolerance_value;

    public double m1_lower_tolerance_value;

    public double m2_lower_tolerance_value;

    public double m3_lower_tolerance_value;

    public double m4_lower_tolerance_value;

    public double m1_offect;

    public double m2_offect;

    public double m3_offect;

    public double m4_offect;

    public int m1_resolution;

    public int m2_resolution;

    public int m3_resolution;

    public int m4_resolution;

    public double m1_scale;

    public double m2_scale;

    public double m3_scale;

    public double m4_scale;

    public String m1_code;

    public String m2_code;

    public String m3_code;

    public String m4_code;

    public boolean m1_enable;

    public boolean m2_enable;

    public boolean m3_enable;

    public boolean m4_enable;

    @Generated(hash = 1353469702)
    public ParameterBean(long code_id, String m1_describe, String m2_describe, String m3_describe,
            String m4_describe, double m1_nominal_value, double m2_nominal_value,
            double m3_nominal_value, double m4_nominal_value, double m1_upper_tolerance_value,
            double m2_upper_tolerance_value, double m3_upper_tolerance_value,
            double m4_upper_tolerance_value, double m1_lower_tolerance_value,
            double m2_lower_tolerance_value, double m3_lower_tolerance_value,
            double m4_lower_tolerance_value, double m1_offect, double m2_offect, double m3_offect,
            double m4_offect, int m1_resolution, int m2_resolution, int m3_resolution,
            int m4_resolution, double m1_scale, double m2_scale, double m3_scale, double m4_scale,
            String m1_code, String m2_code, String m3_code, String m4_code, boolean m1_enable,
            boolean m2_enable, boolean m3_enable, boolean m4_enable) {
        this.code_id = code_id;
        this.m1_describe = m1_describe;
        this.m2_describe = m2_describe;
        this.m3_describe = m3_describe;
        this.m4_describe = m4_describe;
        this.m1_nominal_value = m1_nominal_value;
        this.m2_nominal_value = m2_nominal_value;
        this.m3_nominal_value = m3_nominal_value;
        this.m4_nominal_value = m4_nominal_value;
        this.m1_upper_tolerance_value = m1_upper_tolerance_value;
        this.m2_upper_tolerance_value = m2_upper_tolerance_value;
        this.m3_upper_tolerance_value = m3_upper_tolerance_value;
        this.m4_upper_tolerance_value = m4_upper_tolerance_value;
        this.m1_lower_tolerance_value = m1_lower_tolerance_value;
        this.m2_lower_tolerance_value = m2_lower_tolerance_value;
        this.m3_lower_tolerance_value = m3_lower_tolerance_value;
        this.m4_lower_tolerance_value = m4_lower_tolerance_value;
        this.m1_offect = m1_offect;
        this.m2_offect = m2_offect;
        this.m3_offect = m3_offect;
        this.m4_offect = m4_offect;
        this.m1_resolution = m1_resolution;
        this.m2_resolution = m2_resolution;
        this.m3_resolution = m3_resolution;
        this.m4_resolution = m4_resolution;
        this.m1_scale = m1_scale;
        this.m2_scale = m2_scale;
        this.m3_scale = m3_scale;
        this.m4_scale = m4_scale;
        this.m1_code = m1_code;
        this.m2_code = m2_code;
        this.m3_code = m3_code;
        this.m4_code = m4_code;
        this.m1_enable = m1_enable;
        this.m2_enable = m2_enable;
        this.m3_enable = m3_enable;
        this.m4_enable = m4_enable;
    }

    @Generated(hash = 530727603)
    public ParameterBean() {
    }

    public long getCode_id() {
        return this.code_id;
    }

    public void setCode_id(long code_id) {
        this.code_id = code_id;
    }

    public String getM1_code() {
        return this.m1_code;
    }

    public void setM1_code(String m1_code) {
        this.m1_code = m1_code;
    }

    public String getM2_code() {
        return this.m2_code;
    }

    public void setM2_code(String m2_code) {
        this.m2_code = m2_code;
    }

    public String getM3_code() {
        return this.m3_code;
    }

    public void setM3_code(String m3_code) {
        this.m3_code = m3_code;
    }

    public String getM4_code() {
        return this.m4_code;
    }

    public void setM4_code(String m4_code) {
        this.m4_code = m4_code;
    }

    public double getM1_nominal_value() {
        return this.m1_nominal_value;
    }

    public void setM1_nominal_value(double m1_nominal_value) {
        this.m1_nominal_value = m1_nominal_value;
    }

    public double getM2_nominal_value() {
        return this.m2_nominal_value;
    }

    public void setM2_nominal_value(double m2_nominal_value) {
        this.m2_nominal_value = m2_nominal_value;
    }

    public double getM3_nominal_value() {
        return this.m3_nominal_value;
    }

    public void setM3_nominal_value(double m3_nominal_value) {
        this.m3_nominal_value = m3_nominal_value;
    }

    public double getM4_nominal_value() {
        return this.m4_nominal_value;
    }

    public void setM4_nominal_value(double m4_nominal_value) {
        this.m4_nominal_value = m4_nominal_value;
    }

    public double getM1_upper_tolerance_value() {
        return this.m1_upper_tolerance_value;
    }

    public void setM1_upper_tolerance_value(double m1_upper_tolerance_value) {
        this.m1_upper_tolerance_value = m1_upper_tolerance_value;
    }

    public double getM2_upper_tolerance_value() {
        return this.m2_upper_tolerance_value;
    }

    public void setM2_upper_tolerance_value(double m2_upper_tolerance_value) {
        this.m2_upper_tolerance_value = m2_upper_tolerance_value;
    }

    public double getM3_upper_tolerance_value() {
        return this.m3_upper_tolerance_value;
    }

    public void setM3_upper_tolerance_value(double m3_upper_tolerance_value) {
        this.m3_upper_tolerance_value = m3_upper_tolerance_value;
    }

    public double getM4_upper_tolerance_value() {
        return this.m4_upper_tolerance_value;
    }

    public void setM4_upper_tolerance_value(double m4_upper_tolerance_value) {
        this.m4_upper_tolerance_value = m4_upper_tolerance_value;
    }

    public double getM1_lower_tolerance_value() {
        return this.m1_lower_tolerance_value;
    }

    public void setM1_lower_tolerance_value(double m1_lower_tolerance_value) {
        this.m1_lower_tolerance_value = m1_lower_tolerance_value;
    }

    public double getM2_lower_tolerance_value() {
        return this.m2_lower_tolerance_value;
    }

    public void setM2_lower_tolerance_value(double m2_lower_tolerance_value) {
        this.m2_lower_tolerance_value = m2_lower_tolerance_value;
    }

    public double getM3_lower_tolerance_value() {
        return this.m3_lower_tolerance_value;
    }

    public void setM3_lower_tolerance_value(double m3_lower_tolerance_value) {
        this.m3_lower_tolerance_value = m3_lower_tolerance_value;
    }

    public double getM4_lower_tolerance_value() {
        return this.m4_lower_tolerance_value;
    }

    public void setM4_lower_tolerance_value(double m4_lower_tolerance_value) {
        this.m4_lower_tolerance_value = m4_lower_tolerance_value;
    }

    public double getM1_offect() {
        return this.m1_offect;
    }

    public void setM1_offect(double m1_offect) {
        this.m1_offect = m1_offect;
    }

    public double getM2_offect() {
        return this.m2_offect;
    }

    public void setM2_offect(double m2_offect) {
        this.m2_offect = m2_offect;
    }

    public double getM3_offect() {
        return this.m3_offect;
    }

    public void setM3_offect(double m3_offect) {
        this.m3_offect = m3_offect;
    }

    public double getM4_offect() {
        return this.m4_offect;
    }

    public void setM4_offect(double m4_offect) {
        this.m4_offect = m4_offect;
    }

    public String getM1_describe() {
        return this.m1_describe;
    }

    public void setM1_describe(String m1_describe) {
        this.m1_describe = m1_describe;
    }

    public String getM2_describe() {
        return this.m2_describe;
    }

    public void setM2_describe(String m2_describe) {
        this.m2_describe = m2_describe;
    }

    public String getM3_describe() {
        return this.m3_describe;
    }

    public void setM3_describe(String m3_describe) {
        this.m3_describe = m3_describe;
    }

    public String getM4_describe() {
        return this.m4_describe;
    }

    public void setM4_describe(String m4_describe) {
        this.m4_describe = m4_describe;
    }

    public double getM1_scale() {
        return this.m1_scale;
    }

    public void setM1_scale(double m1_scale) {
        this.m1_scale = m1_scale;
    }

    public double getM2_scale() {
        return this.m2_scale;
    }

    public void setM2_scale(double m2_scale) {
        this.m2_scale = m2_scale;
    }

    public double getM3_scale() {
        return this.m3_scale;
    }

    public void setM3_scale(double m3_scale) {
        this.m3_scale = m3_scale;
    }

    public double getM4_scale() {
        return this.m4_scale;
    }

    public void setM4_scale(double m4_scale) {
        this.m4_scale = m4_scale;
    }

    public int getM1_resolution() {
        return this.m1_resolution;
    }

    public void setM1_resolution(int m1_resolution) {
        this.m1_resolution = m1_resolution;
    }

    public int getM2_resolution() {
        return this.m2_resolution;
    }

    public void setM2_resolution(int m2_resolution) {
        this.m2_resolution = m2_resolution;
    }

    public int getM3_resolution() {
        return this.m3_resolution;
    }

    public void setM3_resolution(int m3_resolution) {
        this.m3_resolution = m3_resolution;
    }

    public int getM4_resolution() {
        return this.m4_resolution;
    }

    public void setM4_resolution(int m4_resolution) {
        this.m4_resolution = m4_resolution;
    }

    @Override
    public String toString() {
        return "ParameterBean{" +
                "code_id=" + code_id +
                ", m1_describe='" + m1_describe + '\'' +
                ", m2_describe='" + m2_describe + '\'' +
                ", m3_describe='" + m3_describe + '\'' +
                ", m4_describe='" + m4_describe + '\'' +
                ", m1_nominal_value=" + m1_nominal_value +
                ", m2_nominal_value=" + m2_nominal_value +
                ", m3_nominal_value=" + m3_nominal_value +
                ", m4_nominal_value=" + m4_nominal_value +
                ", m1_upper_tolerance_value=" + m1_upper_tolerance_value +
                ", m2_upper_tolerance_value=" + m2_upper_tolerance_value +
                ", m3_upper_tolerance_value=" + m3_upper_tolerance_value +
                ", m4_upper_tolerance_value=" + m4_upper_tolerance_value +
                ", m1_lower_tolerance_value=" + m1_lower_tolerance_value +
                ", m2_lower_tolerance_value=" + m2_lower_tolerance_value +
                ", m3_lower_tolerance_value=" + m3_lower_tolerance_value +
                ", m4_lower_tolerance_value=" + m4_lower_tolerance_value +
                ", m1_offect=" + m1_offect +
                ", m2_offect=" + m2_offect +
                ", m3_offect=" + m3_offect +
                ", m4_offect=" + m4_offect +
                ", m1_resolution=" + m1_resolution +
                ", m2_resolution=" + m2_resolution +
                ", m3_resolution=" + m3_resolution +
                ", m4_resolution=" + m4_resolution +
                ", m1_scale=" + m1_scale +
                ", m2_scale=" + m2_scale +
                ", m3_scale=" + m3_scale +
                ", m4_scale=" + m4_scale +
                ", m1_code='" + m1_code + '\'' +
                ", m2_code='" + m2_code + '\'' +
                ", m3_code='" + m3_code + '\'' +
                ", m4_code='" + m4_code + '\'' +
                '}';
    }

    public boolean getM1_enable() {
        return this.m1_enable;
    }

    public void setM1_enable(boolean m1_enable) {
        this.m1_enable = m1_enable;
    }

    public boolean getM2_enable() {
        return this.m2_enable;
    }

    public void setM2_enable(boolean m2_enable) {
        this.m2_enable = m2_enable;
    }

    public boolean getM3_enable() {
        return this.m3_enable;
    }

    public void setM3_enable(boolean m3_enable) {
        this.m3_enable = m3_enable;
    }

    public boolean getM4_enable() {
        return this.m4_enable;
    }

    public void setM4_enable(boolean m4_enable) {
        this.m4_enable = m4_enable;
    }
}
