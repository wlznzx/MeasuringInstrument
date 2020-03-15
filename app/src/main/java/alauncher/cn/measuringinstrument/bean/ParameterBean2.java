package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ParameterBean2 {

    @Id(autoincrement = true)
    public Long id;

    public long codeID;

    public int sequenceNumber;

    // 描述
    public String describe;

    // 名义值
    public double nominalValue;

    // 上公差
    public double upperToleranceValue;

    // 下公差
    public double lowerToleranceValue;

    // 偏移
    public double deviation;

    // 分辨率，精度
    public int resolution;

    // 
    public double scale;

    // 计算公式
    public String code;

    // 使能
    public boolean enable;

    @Generated(hash = 411981944)
    public ParameterBean2(Long id, long codeID, int sequenceNumber, String describe,
                          double nominalValue, double upperToleranceValue,
                          double lowerToleranceValue, double deviation, int resolution,
                          double scale, String code, boolean enable) {
        this.id = id;
        this.codeID = codeID;
        this.sequenceNumber = sequenceNumber;
        this.describe = describe;
        this.nominalValue = nominalValue;
        this.upperToleranceValue = upperToleranceValue;
        this.lowerToleranceValue = lowerToleranceValue;
        this.deviation = deviation;
        this.resolution = resolution;
        this.scale = scale;
        this.code = code;
        this.enable = enable;
    }

    @Generated(hash = 1020281904)
    public ParameterBean2() {
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

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getDescribe() {
        return this.describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public double getNominalValue() {
        return this.nominalValue;
    }

    public void setNominalValue(double nominalValue) {
        this.nominalValue = nominalValue;
    }

    public double getUpperToleranceValue() {
        return this.upperToleranceValue;
    }

    public void setUpperToleranceValue(double upperToleranceValue) {
        this.upperToleranceValue = upperToleranceValue;
    }

    public double getLowerToleranceValue() {
        return this.lowerToleranceValue;
    }

    public void setLowerToleranceValue(double lowerToleranceValue) {
        this.lowerToleranceValue = lowerToleranceValue;
    }

    public double getDeviation() {
        return this.deviation;
    }

    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }

    public int getResolution() {
        return this.resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean getEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "ParameterBean2{" +
                "id=" + id +
                ", codeID=" + codeID +
                ", sequenceNumber=" + sequenceNumber +
                ", describe='" + describe + '\'' +
                ", nominalValue=" + nominalValue +
                ", upperToleranceValue=" + upperToleranceValue +
                ", lowerToleranceValue=" + lowerToleranceValue +
                ", deviation=" + deviation +
                ", resolution=" + resolution +
                ", scale=" + scale +
                ", code='" + code + '\'' +
                ", enable=" + enable +
                '}';
    }
}
