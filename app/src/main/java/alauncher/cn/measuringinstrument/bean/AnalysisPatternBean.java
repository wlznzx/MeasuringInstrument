package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class AnalysisPatternBean {

    @Id(autoincrement = true)
    public Long id;

    public long pID;

    public boolean isLineAuto;

    public boolean isAAuto;

    public double uclX;

    public double lclX;

    public double uclR;

    public double lclR;

    public double a3;

    public double _a3;

    @Generated(hash = 1759607798)
    public AnalysisPatternBean(Long id, long pID, boolean isLineAuto, boolean isAAuto, double uclX,
                               double lclX, double uclR, double lclR, double a3, double _a3) {
        this.id = id;
        this.pID = pID;
        this.isLineAuto = isLineAuto;
        this.isAAuto = isAAuto;
        this.uclX = uclX;
        this.lclX = lclX;
        this.uclR = uclR;
        this.lclR = lclR;
        this.a3 = a3;
        this._a3 = _a3;
    }

    @Generated(hash = 219787428)
    public AnalysisPatternBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPID() {
        return this.pID;
    }

    public void setPID(long pID) {
        this.pID = pID;
    }

    public boolean getIsLineAuto() {
        return this.isLineAuto;
    }

    public void setIsLineAuto(boolean isLineAuto) {
        this.isLineAuto = isLineAuto;
    }

    public boolean getIsAAuto() {
        return this.isAAuto;
    }

    public void setIsAAuto(boolean isAAuto) {
        this.isAAuto = isAAuto;
    }

    public double getUclX() {
        return this.uclX;
    }

    public void setUclX(double uclX) {
        this.uclX = uclX;
    }

    public double getLclX() {
        return this.lclX;
    }

    public void setLclX(double lclX) {
        this.lclX = lclX;
    }

    public double getUclR() {
        return this.uclR;
    }

    public void setUclR(double uclR) {
        this.uclR = uclR;
    }

    public double getA3() {
        return this.a3;
    }

    public void setA3(double a3) {
        this.a3 = a3;
    }

    public double get_a3() {
        return this._a3;
    }

    public void set_a3(double _a3) {
        this._a3 = _a3;
    }

    public double getLclR() {
        return this.lclR;
    }

    public void setLclR(double lclR) {
        this.lclR = lclR;
    }


}
