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
public class CriticalBean {

    @Id(autoincrement = true)
    public Long id;

    public int codeID;

    public int mIndex;

    public double upLimitValue;

    public double lowLimitValue;

    @Generated(hash = 266898359)
    public CriticalBean(Long id, int codeID, int mIndex, double upLimitValue,
            double lowLimitValue) {
        this.id = id;
        this.codeID = codeID;
        this.mIndex = mIndex;
        this.upLimitValue = upLimitValue;
        this.lowLimitValue = lowLimitValue;
    }

    @Generated(hash = 1024311873)
    public CriticalBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMIndex() {
        return this.mIndex;
    }

    public void setMIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public int getCodeID() {
        return this.codeID;
    }

    public void setCodeID(int codeID) {
        this.codeID = codeID;
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
}
