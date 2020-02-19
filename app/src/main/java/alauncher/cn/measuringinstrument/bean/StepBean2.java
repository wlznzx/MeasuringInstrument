package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.List;

import alauncher.cn.measuringinstrument.utils.StringConverter;

/**
 * 日期：2019/8/5 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class StepBean2 {

    @Id(autoincrement = true)
    public Long id;

    public long codeID;

    public int stepID;

    public long conditionID;

    public int sequenceNumber;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> measureItems;

    @Generated(hash = 1940990719)
    public StepBean2(Long id, long codeID, int stepID, long conditionID,
                     int sequenceNumber, List<String> measureItems) {
        this.id = id;
        this.codeID = codeID;
        this.stepID = stepID;
        this.conditionID = conditionID;
        this.sequenceNumber = sequenceNumber;
        this.measureItems = measureItems;
    }

    @Generated(hash = 1414945329)
    public StepBean2() {
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

    public int getStepID() {
        return this.stepID;
    }

    public void setStepID(int stepID) {
        this.stepID = stepID;
    }

    public long getConditionID() {
        return this.conditionID;
    }

    public void setConditionID(long conditionID) {
        this.conditionID = conditionID;
    }

    public List<String> getMeasureItems() {
        return this.measureItems;
    }

    public void setMeasureItems(List<String> measureItems) {
        this.measureItems = measureItems;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public String toString() {
        return "StepBean2{" +
                "id=" + id +
                ", codeID=" + codeID +
                ", stepID=" + stepID +
                ", conditionID=" + conditionID +
                ", sequenceNumber=" + sequenceNumber +
                ", measureItems=" + measureItems +
                '}';
    }
}
