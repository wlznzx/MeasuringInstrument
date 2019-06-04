package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class CalibrationBean {
    @Id
    public long code_id;
    // 校验方式;
    int ch1CalibrationType;
    int ch2CalibrationType;
    int ch3CalibrationType;
    int ch4CalibrationType;
    // 小件标准;
    double ch1SmallPartStandard;
    double ch2SmallPartStandard;
    double ch3SmallPartStandard;
    double ch4SmallPartStandard;
    // 大件标准;
    double ch1BigPartStandard;
    double ch2BigPartStandard;
    double ch3BigPartStandard;
    double ch4BigPartStandard;
    // 倍率上限;
    double ch1UpperLimitRate;
    double ch2UpperLimitRate;
    double ch3UpperLimitRate;
    double ch4UpperLimitRate;
    // 倍率下限;
    double ch1LowerLimitRate;
    double ch2LowerLimitRate;
    double ch3LowerLimitRate;
    double ch4LowerLimitRate;
    // 补偿值
    double ch1CompensationValue;
    double ch2CompensationValue;
    double ch3CompensationValue;
    double ch4CompensationValue;
    // 倍率
    double ch1KValue;
    double ch2KValue;
    double ch3KValue;
    double ch4KValue;

    @Generated(hash = 1093008177)
    public CalibrationBean(long code_id, int ch1CalibrationType,
                           int ch2CalibrationType, int ch3CalibrationType, int ch4CalibrationType,
                           double ch1SmallPartStandard, double ch2SmallPartStandard,
                           double ch3SmallPartStandard, double ch4SmallPartStandard,
                           double ch1BigPartStandard, double ch2BigPartStandard,
                           double ch3BigPartStandard, double ch4BigPartStandard,
                           double ch1UpperLimitRate, double ch2UpperLimitRate,
                           double ch3UpperLimitRate, double ch4UpperLimitRate,
                           double ch1LowerLimitRate, double ch2LowerLimitRate,
                           double ch3LowerLimitRate, double ch4LowerLimitRate,
                           double ch1CompensationValue, double ch2CompensationValue,
                           double ch3CompensationValue, double ch4CompensationValue,
                           double ch1KValue, double ch2KValue, double ch3KValue,
                           double ch4KValue) {
        this.code_id = code_id;
        this.ch1CalibrationType = ch1CalibrationType;
        this.ch2CalibrationType = ch2CalibrationType;
        this.ch3CalibrationType = ch3CalibrationType;
        this.ch4CalibrationType = ch4CalibrationType;
        this.ch1SmallPartStandard = ch1SmallPartStandard;
        this.ch2SmallPartStandard = ch2SmallPartStandard;
        this.ch3SmallPartStandard = ch3SmallPartStandard;
        this.ch4SmallPartStandard = ch4SmallPartStandard;
        this.ch1BigPartStandard = ch1BigPartStandard;
        this.ch2BigPartStandard = ch2BigPartStandard;
        this.ch3BigPartStandard = ch3BigPartStandard;
        this.ch4BigPartStandard = ch4BigPartStandard;
        this.ch1UpperLimitRate = ch1UpperLimitRate;
        this.ch2UpperLimitRate = ch2UpperLimitRate;
        this.ch3UpperLimitRate = ch3UpperLimitRate;
        this.ch4UpperLimitRate = ch4UpperLimitRate;
        this.ch1LowerLimitRate = ch1LowerLimitRate;
        this.ch2LowerLimitRate = ch2LowerLimitRate;
        this.ch3LowerLimitRate = ch3LowerLimitRate;
        this.ch4LowerLimitRate = ch4LowerLimitRate;
        this.ch1CompensationValue = ch1CompensationValue;
        this.ch2CompensationValue = ch2CompensationValue;
        this.ch3CompensationValue = ch3CompensationValue;
        this.ch4CompensationValue = ch4CompensationValue;
        this.ch1KValue = ch1KValue;
        this.ch2KValue = ch2KValue;
        this.ch3KValue = ch3KValue;
        this.ch4KValue = ch4KValue;
    }

    @Generated(hash = 1815816231)
    public CalibrationBean() {
    }

    public long getCode_id() {
        return this.code_id;
    }

    public void setCode_id(long code_id) {
        this.code_id = code_id;
    }

    public int getCh1CalibrationType() {
        return this.ch1CalibrationType;
    }

    public void setCh1CalibrationType(int ch1CalibrationType) {
        this.ch1CalibrationType = ch1CalibrationType;
    }

    public int getCh2CalibrationType() {
        return this.ch2CalibrationType;
    }

    public void setCh2CalibrationType(int ch2CalibrationType) {
        this.ch2CalibrationType = ch2CalibrationType;
    }

    public int getCh3CalibrationType() {
        return this.ch3CalibrationType;
    }

    public void setCh3CalibrationType(int ch3CalibrationType) {
        this.ch3CalibrationType = ch3CalibrationType;
    }

    public int getCh4CalibrationType() {
        return this.ch4CalibrationType;
    }

    public void setCh4CalibrationType(int ch4CalibrationType) {
        this.ch4CalibrationType = ch4CalibrationType;
    }

    public double getCh1SmallPartStandard() {
        return this.ch1SmallPartStandard;
    }

    public void setCh1SmallPartStandard(double ch1SmallPartStandard) {
        this.ch1SmallPartStandard = ch1SmallPartStandard;
    }

    public double getCh2SmallPartStandard() {
        return this.ch2SmallPartStandard;
    }

    public void setCh2SmallPartStandard(double ch2SmallPartStandard) {
        this.ch2SmallPartStandard = ch2SmallPartStandard;
    }

    public double getCh3SmallPartStandard() {
        return this.ch3SmallPartStandard;
    }

    public void setCh3SmallPartStandard(double ch3SmallPartStandard) {
        this.ch3SmallPartStandard = ch3SmallPartStandard;
    }

    public double getCh4SmallPartStandard() {
        return this.ch4SmallPartStandard;
    }

    public void setCh4SmallPartStandard(double ch4SmallPartStandard) {
        this.ch4SmallPartStandard = ch4SmallPartStandard;
    }

    public double getCh1BigPartStandard() {
        return this.ch1BigPartStandard;
    }

    public void setCh1BigPartStandard(double ch1BigPartStandard) {
        this.ch1BigPartStandard = ch1BigPartStandard;
    }

    public double getCh2BigPartStandard() {
        return this.ch2BigPartStandard;
    }

    public void setCh2BigPartStandard(double ch2BigPartStandard) {
        this.ch2BigPartStandard = ch2BigPartStandard;
    }

    public double getCh3BigPartStandard() {
        return this.ch3BigPartStandard;
    }

    public void setCh3BigPartStandard(double ch3BigPartStandard) {
        this.ch3BigPartStandard = ch3BigPartStandard;
    }

    public double getCh4BigPartStandard() {
        return this.ch4BigPartStandard;
    }

    public void setCh4BigPartStandard(double ch4BigPartStandard) {
        this.ch4BigPartStandard = ch4BigPartStandard;
    }

    public double getCh1UpperLimitRate() {
        return this.ch1UpperLimitRate;
    }

    public void setCh1UpperLimitRate(double ch1UpperLimitRate) {
        this.ch1UpperLimitRate = ch1UpperLimitRate;
    }

    public double getCh2UpperLimitRate() {
        return this.ch2UpperLimitRate;
    }

    public void setCh2UpperLimitRate(double ch2UpperLimitRate) {
        this.ch2UpperLimitRate = ch2UpperLimitRate;
    }

    public double getCh3UpperLimitRate() {
        return this.ch3UpperLimitRate;
    }

    public void setCh3UpperLimitRate(double ch3UpperLimitRate) {
        this.ch3UpperLimitRate = ch3UpperLimitRate;
    }

    public double getCh4UpperLimitRate() {
        return this.ch4UpperLimitRate;
    }

    public void setCh4UpperLimitRate(double ch4UpperLimitRate) {
        this.ch4UpperLimitRate = ch4UpperLimitRate;
    }

    public double getCh1LowerLimitRate() {
        return this.ch1LowerLimitRate;
    }

    public void setCh1LowerLimitRate(double ch1LowerLimitRate) {
        this.ch1LowerLimitRate = ch1LowerLimitRate;
    }

    public double getCh2LowerLimitRate() {
        return this.ch2LowerLimitRate;
    }

    public void setCh2LowerLimitRate(double ch2LowerLimitRate) {
        this.ch2LowerLimitRate = ch2LowerLimitRate;
    }

    public double getCh3LowerLimitRate() {
        return this.ch3LowerLimitRate;
    }

    public void setCh3LowerLimitRate(double ch3LowerLimitRate) {
        this.ch3LowerLimitRate = ch3LowerLimitRate;
    }

    public double getCh4LowerLimitRate() {
        return this.ch4LowerLimitRate;
    }

    public void setCh4LowerLimitRate(double ch4LowerLimitRate) {
        this.ch4LowerLimitRate = ch4LowerLimitRate;
    }

    public double getCh1CompensationValue() {
        return this.ch1CompensationValue;
    }

    public void setCh1CompensationValue(double ch1CompensationValue) {
        this.ch1CompensationValue = ch1CompensationValue;
    }

    public double getCh2CompensationValue() {
        return this.ch2CompensationValue;
    }

    public void setCh2CompensationValue(double ch2CompensationValue) {
        this.ch2CompensationValue = ch2CompensationValue;
    }

    public double getCh3CompensationValue() {
        return this.ch3CompensationValue;
    }

    public void setCh3CompensationValue(double ch3CompensationValue) {
        this.ch3CompensationValue = ch3CompensationValue;
    }

    public double getCh4CompensationValue() {
        return this.ch4CompensationValue;
    }

    public void setCh4CompensationValue(double ch4CompensationValue) {
        this.ch4CompensationValue = ch4CompensationValue;
    }

    public double getCh1KValue() {
        return this.ch1KValue;
    }

    public void setCh1KValue(double ch1KValue) {
        this.ch1KValue = ch1KValue;
    }

    public double getCh2KValue() {
        return this.ch2KValue;
    }

    public void setCh2KValue(double ch2KValue) {
        this.ch2KValue = ch2KValue;
    }

    public double getCh3KValue() {
        return this.ch3KValue;
    }

    public void setCh3KValue(double ch3KValue) {
        this.ch3KValue = ch3KValue;
    }

    public double getCh4KValue() {
        return this.ch4KValue;
    }

    public void setCh4KValue(double ch4KValue) {
        this.ch4KValue = ch4KValue;
    }

    @Override
    public String toString() {
        return "CalibrationBean{" +
                "code_id=" + code_id +
                ", ch1CalibrationType=" + ch1CalibrationType +
                ", ch2CalibrationType=" + ch2CalibrationType +
                ", ch3CalibrationType=" + ch3CalibrationType +
                ", ch4CalibrationType=" + ch4CalibrationType +
                ", ch1SmallPartStandard=" + ch1SmallPartStandard +
                ", ch2SmallPartStandard=" + ch2SmallPartStandard +
                ", ch3SmallPartStandard=" + ch3SmallPartStandard +
                ", ch4SmallPartStandard=" + ch4SmallPartStandard +
                ", ch1BigPartStandard=" + ch1BigPartStandard +
                ", ch2BigPartStandard=" + ch2BigPartStandard +
                ", ch3BigPartStandard=" + ch3BigPartStandard +
                ", ch4BigPartStandard=" + ch4BigPartStandard +
                ", ch1UpperLimitRate=" + ch1UpperLimitRate +
                ", ch2UpperLimitRate=" + ch2UpperLimitRate +
                ", ch3UpperLimitRate=" + ch3UpperLimitRate +
                ", ch4UpperLimitRate=" + ch4UpperLimitRate +
                ", ch1LowerLimitRate=" + ch1LowerLimitRate +
                ", ch2LowerLimitRate=" + ch2LowerLimitRate +
                ", ch3LowerLimitRate=" + ch3LowerLimitRate +
                ", ch4LowerLimitRate=" + ch4LowerLimitRate +
                ", ch1CompensationValue=" + ch1CompensationValue +
                ", ch2CompensationValue=" + ch2CompensationValue +
                ", ch3CompensationValue=" + ch3CompensationValue +
                ", ch4CompensationValue=" + ch4CompensationValue +
                ", ch1KValue=" + ch1KValue +
                ", ch2KValue=" + ch2KValue +
                ", ch3KValue=" + ch3KValue +
                ", ch4KValue=" + ch4KValue +
                '}';
    }
}
