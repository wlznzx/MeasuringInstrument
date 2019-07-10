package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 日期：2019/4/25 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class StoreBean {

    @Id
    public Long id;

    public int storeMode;

    public int mValue;

    public double upLimitValue;

    public double lowLimitValue;

    public int delayTime;

    @Generated(hash = 1310177342)
    public StoreBean(Long id, int storeMode, int mValue, double upLimitValue,
            double lowLimitValue, int delayTime) {
        this.id = id;
        this.storeMode = storeMode;
        this.mValue = mValue;
        this.upLimitValue = upLimitValue;
        this.lowLimitValue = lowLimitValue;
        this.delayTime = delayTime;
    }

    @Generated(hash = 1177606397)
    public StoreBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStoreMode() {
        return this.storeMode;
    }

    public void setStoreMode(int storeMode) {
        this.storeMode = storeMode;
    }

    public int getMValue() {
        return this.mValue;
    }

    public void setMValue(int mValue) {
        this.mValue = mValue;
    }

    public double getUpLimitValue() {
        return this.upLimitValue;
    }

    public void setUpLimitValue(double upLimitValue) {
        this.upLimitValue = upLimitValue;
    }

    public double getLowLimitValue() {
        return this.lowLimitValue;
    }

    public void setLowLimitValue(double lowLimitValue) {
        this.lowLimitValue = lowLimitValue;
    }

    public int getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    
}
