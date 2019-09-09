package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 日期：2019/8/5 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class StepBean {

    @Id
    public long codeID;

    public int stepID;

    // 0011;
    public int measured;


    @Generated(hash = 1869239251)
    public StepBean(long codeID, int stepID, int measured) {
        this.codeID = codeID;
        this.stepID = stepID;
        this.measured = measured;
    }


    @Generated(hash = 781306117)
    public StepBean() {
    }


    public long getCodeID() {
        return this.codeID;
    }


    public void setCodeID(long codeID) {
        this.codeID = codeID;
    }


    public int getStepID() {
        return this.stepID;
    }


    public void setStepID(int stepID) {
        this.stepID = stepID;
    }


    public int getMeasured() {
        return this.measured;
    }


    public void setMeasured(int measured) {
        this.measured = measured;
    }

}
