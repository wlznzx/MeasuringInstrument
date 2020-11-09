package alauncher.cn.measuringinstrument.bean;

import android.widget.EditText;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.utils.StringConverter;
import butterknife.BindView;

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

    public boolean isSelect;

    public String mType;

    public String machineInfo;

    private String processNo;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> measurementValues;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> measurementGroup;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> mItems;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> mDescribe;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> rValues;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> gValues;

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> eValues;

    private boolean isUploaded;

    @Generated(hash = 1748277256)
    public ResultBean2(Long id, long codeID, String handlerAccount, long timeStamp,
                       String workID, String workIDExtra, String eventID, String event,
                       String result, boolean isSelect, String mType, String machineInfo,
                       String processNo, List<String> measurementValues,
                       List<String> measurementGroup, List<String> mItems,
                       List<String> mDescribe, List<String> rValues, List<String> gValues,
                       List<String> eValues, boolean isUploaded) {
        this.id = id;
        this.codeID = codeID;
        this.handlerAccount = handlerAccount;
        this.timeStamp = timeStamp;
        this.workID = workID;
        this.workIDExtra = workIDExtra;
        this.eventID = eventID;
        this.event = event;
        this.result = result;
        this.isSelect = isSelect;
        this.mType = mType;
        this.machineInfo = machineInfo;
        this.processNo = processNo;
        this.measurementValues = measurementValues;
        this.measurementGroup = measurementGroup;
        this.mItems = mItems;
        this.mDescribe = mDescribe;
        this.rValues = rValues;
        this.gValues = gValues;
        this.eValues = eValues;
        this.isUploaded = isUploaded;
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

    public boolean getIsSelect() {
        return this.isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getMType() {
        return this.mType;
    }

    public void setMType(String mType) {
        this.mType = mType;
    }

    public String getMachineInfo() {
        return this.machineInfo;
    }

    public void setMachineInfo(String machineInfo) {
        this.machineInfo = machineInfo;
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

    public List<String> getMDescribe() {
        return this.mDescribe;
    }

    public void setMDescribe(List<String> mDescribe) {
        this.mDescribe = mDescribe;
    }

    public List<String> getRValues() {
        return this.rValues;
    }

    public void setRValues(List<String> rValues) {
        this.rValues = rValues;
    }

    public List<String> getGValues() {
        return this.gValues;
    }

    public void setGValues(List<String> gValues) {
        this.gValues = gValues;
    }

    public List<String> getEValues() {
        return this.eValues;
    }

    public void setEValues(List<String> eValues) {
        this.eValues = eValues;
    }

    public boolean getIsUploaded() {
        return this.isUploaded;
    }

    public void setIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    public String getProcessNo() {
        return this.processNo;
    }

    public void setProcessNo(String processNo) {
        this.processNo = processNo;
    }

    @Override
    public String toString() {
        return "ResultBean2{" +
                "id=" + id +
                ", codeID=" + codeID +
                ", handlerAccount='" + handlerAccount + '\'' +
                ", timeStamp=" + timeStamp +
                ", workID='" + workID + '\'' +
                ", workIDExtra='" + workIDExtra + '\'' +
                ", eventID='" + eventID + '\'' +
                ", event='" + event + '\'' +
                ", result='" + result + '\'' +
                ", isSelect=" + isSelect +
                ", mType='" + mType + '\'' +
                ", machineInfo='" + machineInfo + '\'' +
                ", processNo='" + processNo + '\'' +
                ", measurementValues=" + measurementValues +
                ", measurementGroup=" + measurementGroup +
                ", mItems=" + mItems +
                ", mDescribe=" + mDescribe +
                ", rValues=" + rValues +
                ", gValues=" + gValues +
                ", eValues=" + eValues +
                ", isUploaded=" + isUploaded +
                '}';
    }
}
