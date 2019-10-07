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
public class SetupBean {

    @Id
    public Long id;

    public int codeID;

    public String accout;

    public boolean isAutoPopUp;

    public double xUpperLine;

    public double xLowerLine;

    public double rUpperLine;

    public double rLowerLine;

    @Generated(hash = 572711874)
    public SetupBean(Long id, int codeID, String accout, boolean isAutoPopUp,
            double xUpperLine, double xLowerLine, double rUpperLine,
            double rLowerLine) {
        this.id = id;
        this.codeID = codeID;
        this.accout = accout;
        this.isAutoPopUp = isAutoPopUp;
        this.xUpperLine = xUpperLine;
        this.xLowerLine = xLowerLine;
        this.rUpperLine = rUpperLine;
        this.rLowerLine = rLowerLine;
    }

    @Generated(hash = 4168623)
    public SetupBean() {
    }

    public int getCodeID() {
        return this.codeID;
    }

    public void setCodeID(int codeID) {
        this.codeID = codeID;
    }

    public String getAccout() {
        return this.accout;
    }

    public void setAccout(String accout) {
        this.accout = accout;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsAutoPopUp() {
        return this.isAutoPopUp;
    }

    public void setIsAutoPopUp(boolean isAutoPopUp) {
        this.isAutoPopUp = isAutoPopUp;
    }

    public double getXUpperLine() {
        return this.xUpperLine;
    }

    public void setXUpperLine(double xUpperLine) {
        this.xUpperLine = xUpperLine;
    }

    public double getXLowerLine() {
        return this.xLowerLine;
    }

    public void setXLowerLine(double xLowerLine) {
        this.xLowerLine = xLowerLine;
    }

    public double getRUpperLine() {
        return this.rUpperLine;
    }

    public void setRUpperLine(double rUpperLine) {
        this.rUpperLine = rUpperLine;
    }

    public double getRLowerLine() {
        return this.rLowerLine;
    }

    public void setRLowerLine(double rLowerLine) {
        this.rLowerLine = rLowerLine;
    }

}
