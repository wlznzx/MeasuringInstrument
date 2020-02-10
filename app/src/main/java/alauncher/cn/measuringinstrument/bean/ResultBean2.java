package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

import alauncher.cn.measuringinstrument.utils.StringConverter;

@Entity
public class ResultBean2 {

    @Id(autoincrement = true)
    public Long id;

    public long codeID;

    public String handlerAccount;

    public long timeStamp;

    public String workID;

    public String workIDExtra;

    public String eventID;

    public String event;

    public String result;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> measurementValues;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> measurementGroup;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> mItems;

    @Transient
    public boolean isSelect;

    @Generated(hash = 292594885)
    public ResultBean2(Long id, long codeID, String handlerAccount, long timeStamp,
            String workID, String workIDExtra, String eventID, String event, String result,
            List<String> measurementValues, List<String> measurementGroup,
            List<String> mItems) {
        this.id = id;
        this.codeID = codeID;
        this.handlerAccount = handlerAccount;
        this.timeStamp = timeStamp;
        this.workID = workID;
        this.workIDExtra = workIDExtra;
        this.eventID = eventID;
        this.event = event;
        this.result = result;
        this.measurementValues = measurementValues;
        this.measurementGroup = measurementGroup;
        this.mItems = mItems;
    }

    @Generated(hash = 1498620417)
    public ResultBean2() {

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

    public String getHandlerAccount() {
        return this.handlerAccount;
    }

    public void setHandlerAccount(String handlerAccount) {
        this.handlerAccount = handlerAccount;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getWorkID() {
        return this.workID;
    }

    public void setWorkID(String workID) {
        this.workID = workID;
    }

    public String getWorkIDExtra() {
        return this.workIDExtra;
    }

    public void setWorkIDExtra(String workIDExtra) {
        this.workIDExtra = workIDExtra;
    }

    public String getEventID() {
        return this.eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<String> getMeasurementValues() {
        return this.measurementValues;
    }

    public void setMeasurementValues(List<String> measurementValues) {
        this.measurementValues = measurementValues;
    }

    public List<String> getMeasurementGroup() {
        return this.measurementGroup;
    }

    public void setMeasurementGroup(List<String> measurementGroup) {
        this.measurementGroup = measurementGroup;
    }

    public List<String> getMItems() {
        return this.mItems;
    }

    public void setMItems(List<String> mItems) {
        this.mItems = mItems;
    }

}
