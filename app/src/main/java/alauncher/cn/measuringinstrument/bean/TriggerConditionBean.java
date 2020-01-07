package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

import alauncher.cn.measuringinstrument.utils.StringConverter;

/**
 * 日期：2019/4/25 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */

@Entity
public class TriggerConditionBean {

    @Id
    public Long id;

    public long codeID;

    public int mIndex;

    public double scale;

    public boolean isScale;

    public double upperLimit;

    public double lowerLimit;

    public double stableTime;

    public String conditionName;

    @Generated(hash = 152069947)
    public TriggerConditionBean(Long id, long codeID, int mIndex, double scale,
            boolean isScale, double upperLimit, double lowerLimit,
            double stableTime, String conditionName) {
        this.id = id;
        this.codeID = codeID;
        this.mIndex = mIndex;
        this.scale = scale;
        this.isScale = isScale;
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
        this.stableTime = stableTime;
        this.conditionName = conditionName;
    }

    @Generated(hash = 1875679245)
    public TriggerConditionBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCodeID() {
        return this.codeID;
    }

    public void setCodeID(long codeID) {
        this.codeID = codeID;
    }

    public int getMIndex() {
        return this.mIndex;
    }

    public void setMIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean getIsScale() {
        return this.isScale;
    }

    public void setIsScale(boolean isScale) {
        this.isScale = isScale;
    }

    public double getUpperLimit() {
        return this.upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public double getLowerLimit() {
        return this.lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getStableTime() {
        return this.stableTime;
    }

    public void setStableTime(double stableTime) {
        this.stableTime = stableTime;
    }

    public String getConditionName() {
        return this.conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }
    
}
