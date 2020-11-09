package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class ResultBean {

    @Id(autoincrement = true)
    public Long id;

    public long codeID;

    public String handlerAccout;

    public long timeStamp;

    public String workid;

    public String workid_extra;

    public String eventid;

    public String event;

    public String result;

    public double m1;

    public double m2;

    public double m3;

    public double m4;

    public String m1_group;

    public String m2_group;

    public String m3_group;

    public String m4_group;

    @Transient
    public boolean isSelect;

    @Generated(hash = 573637926)
    public ResultBean(Long id, long codeID, String handlerAccout, long timeStamp,
                      String workid, String workid_extra, String eventid, String event, String result,
                      double m1, double m2, double m3, double m4, String m1_group, String m2_group,
                      String m3_group, String m4_group) {
        this.id = id;
        this.codeID = codeID;
        this.handlerAccout = handlerAccout;
        this.timeStamp = timeStamp;
        this.workid = workid;
        this.workid_extra = workid_extra;
        this.eventid = eventid;
        this.event = event;
        this.result = result;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
        this.m4 = m4;
        this.m1_group = m1_group;
        this.m2_group = m2_group;
        this.m3_group = m3_group;
        this.m4_group = m4_group;
    }

    @Generated(hash = 2137771703)
    public ResultBean() {
    }

    public ResultBean(double pM1) {
        this.m1 = pM1;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHandlerAccout() {
        return this.handlerAccout;
    }

    public void setHandlerAccout(String handlerAccout) {
        this.handlerAccout = handlerAccout;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getWorkid() {
        return this.workid;
    }

    public void setWorkid(String workid) {
        this.workid = workid;
    }

    public String getWorkid_extra() {
        return this.workid_extra;
    }

    public void setWorkid_extra(String workid_extra) {
        this.workid_extra = workid_extra;
    }

    public String getEventid() {
        return this.eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
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

    public double getM1() {
        return this.m1;
    }

    public void setM1(double m1) {
        this.m1 = m1;
    }

    public double getM2() {
        return this.m2;
    }

    public void setM2(double m2) {
        this.m2 = m2;
    }

    public double getM3() {
        return this.m3;
    }

    public void setM3(double m3) {
        this.m3 = m3;
    }

    public double getM4() {
        return this.m4;
    }

    public void setM4(double m4) {
        this.m4 = m4;
    }

    public String getM1_group() {
        return this.m1_group;
    }

    public void setM1_group(String m1_group) {
        this.m1_group = m1_group;
    }

    public String getM2_group() {
        return this.m2_group;
    }

    public void setM2_group(String m2_group) {
        this.m2_group = m2_group;
    }

    public String getM3_group() {
        return this.m3_group;
    }

    public void setM3_group(String m3_group) {
        this.m3_group = m3_group;
    }

    public String getM4_group() {
        return this.m4_group;
    }

    public void setM4_group(String m4_group) {
        this.m4_group = m4_group;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "id=" + id +
                ", handlerAccout='" + handlerAccout + '\'' +
                ", timeStamp=" + timeStamp +
                ", workid='" + workid + '\'' +
                ", workid_extra='" + workid_extra + '\'' +
                ", eventid='" + eventid + '\'' +
                ", event='" + event + '\'' +
                ", result='" + result + '\'' +
                ", m1=" + m1 +
                ", m2=" + m2 +
                ", m3=" + m3 +
                ", m4=" + m4 +
                ", m1_group='" + m1_group + '\'' +
                ", m2_group='" + m2_group + '\'' +
                ", m3_group='" + m3_group + '\'' +
                ", m4_group='" + m4_group + '\'' +
                ", isSelect=" + isSelect +
                '}';
    }

    public long getCodeID() {
        return this.codeID;
    }

    public void setCodeID(long codeID) {
        this.codeID = codeID;
    }
}
