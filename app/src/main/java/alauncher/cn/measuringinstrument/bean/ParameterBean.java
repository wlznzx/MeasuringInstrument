package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class ParameterBean {
    @Id
    public long code_id;

    public String m1_code;

    public String m2_code;

    public String m3_code;

    public String m4_code;

    @Generated(hash = 979927382)
    public ParameterBean(long code_id, String m1_code, String m2_code,
            String m3_code, String m4_code) {
        this.code_id = code_id;
        this.m1_code = m1_code;
        this.m2_code = m2_code;
        this.m3_code = m3_code;
        this.m4_code = m4_code;
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

    @Override
    public String toString() {
        return "ParameterBean{" +
                "code_id=" + code_id +
                ", m1_code='" + m1_code + '\'' +
                ", m2_code='" + m2_code + '\'' +
                ", m3_code='" + m3_code + '\'' +
                ", m4_code='" + m4_code + '\'' +
                '}';
    }
}
