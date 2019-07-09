package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 日期：2019/4/25 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class ForceCalibrationBean {
    @Id
    public long _id;

    public int forceMode;

    public int forceTime;

    public int forceNum;

    public long realForceTime;

    public int usrNum;

    @Generated(hash = 1754465221)
    public ForceCalibrationBean(long _id, int forceMode, int forceTime,
            int forceNum, long realForceTime, int usrNum) {
        this._id = _id;
        this.forceMode = forceMode;
        this.forceTime = forceTime;
        this.forceNum = forceNum;
        this.realForceTime = realForceTime;
        this.usrNum = usrNum;
    }

    @Generated(hash = 288321405)
    public ForceCalibrationBean() {
    }

    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public int getForceMode() {
        return this.forceMode;
    }

    public void setForceMode(int forceMode) {
        this.forceMode = forceMode;
    }

    public int getForceTime() {
        return this.forceTime;
    }

    public void setForceTime(int forceTime) {
        this.forceTime = forceTime;
    }

    public int getForceNum() {
        return this.forceNum;
    }

    public void setForceNum(int forceNum) {
        this.forceNum = forceNum;
    }

    public long getRealForceTime() {
        return this.realForceTime;
    }

    public void setRealForceTime(long realForceTime) {
        this.realForceTime = realForceTime;
    }

    public int getUsrNum() {
        return this.usrNum;
    }

    public void setUsrNum(int usrNum) {
        this.usrNum = usrNum;
    }

}
