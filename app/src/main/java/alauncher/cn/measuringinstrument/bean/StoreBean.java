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
public class StoreBean {

    @Id
    public Long id;

    public int storeMode;

    public int mValue;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> upLimitValue;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> lowLimitValue;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> isScale;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> scale;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> stable;

    public int delayTime;

    @Generated(hash = 14670207)
    public StoreBean(Long id, int storeMode, int mValue, List<String> upLimitValue,
            List<String> lowLimitValue, List<String> isScale, List<String> scale,
            List<String> stable, int delayTime) {
        this.id = id;
        this.storeMode = storeMode;
        this.mValue = mValue;
        this.upLimitValue = upLimitValue;
        this.lowLimitValue = lowLimitValue;
        this.isScale = isScale;
        this.scale = scale;
        this.stable = stable;
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

    public List<String> getUpLimitValue() {
        return this.upLimitValue;
    }

    public void setUpLimitValue(List<String> upLimitValue) {
        this.upLimitValue = upLimitValue;
    }

    public List<String> getLowLimitValue() {
        return this.lowLimitValue;
    }

    public void setLowLimitValue(List<String> lowLimitValue) {
        this.lowLimitValue = lowLimitValue;
    }

    public int getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public String toString() {
        return "StoreBean{" +
                "id=" + id +
                ", storeMode=" + storeMode +
                ", mValue=" + mValue +
                ", upLimitValue=" + upLimitValue +
                ", lowLimitValue=" + lowLimitValue +
                ", delayTime=" + delayTime +
                '}';
    }

    public List<String> getIsScale() {
        return this.isScale;
    }

    public void setIsScale(List<String> isScale) {
        this.isScale = isScale;
    }

    public List<String> getScale() {
        return this.scale;
    }

    public void setScale(List<String> scale) {
        this.scale = scale;
    }

    public List<String> getStable() {
        return this.stable;
    }

    public void setStable(List<String> stable) {
        this.stable = stable;
    }
}
