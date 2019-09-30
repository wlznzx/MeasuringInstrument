package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

/**
 * 日期：2019/8/5 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity(indexes = {@Index(value = "codeID DESC, stepID DESC", unique = true)})
public class StepBean {

    @Id(autoincrement = true)
    public Long id;

    public long codeID;

    public int stepID;
    // 0011;
    public int measured;

    @Generated(hash = 872925347)
    public StepBean(Long id, long codeID, int stepID, int measured) {
        this.id = id;
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

    @Override
    public String toString() {
        return "StepBean{" +
                "codeID=" + codeID +
                ", stepID=" + stepID +
                ", measured=" + measured +
                '}';
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }
}
