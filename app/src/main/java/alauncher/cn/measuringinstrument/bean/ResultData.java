package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ResultData {
    @Id
    public long id;

    public String handler;

    public long time;

    public long workpieceId;

    public String event;

    public int result;

    public double m1;

    public double m2;

    public double m3;

    public double m4;

    @Generated(hash = 884313210)
    public ResultData(long id, String handler, long time, long workpieceId,
                      String event, int result, double m1, double m2, double m3, double m4) {
        this.id = id;
        this.handler = handler;
        this.time = time;
        this.workpieceId = workpieceId;
        this.event = event;
        this.result = result;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
        this.m4 = m4;
    }

    @Generated(hash = 1484394295)
    public ResultData() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHandler() {
        return this.handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getWorkpieceId() {
        return this.workpieceId;
    }

    public void setWorkpieceId(long workpieceId) {
        this.workpieceId = workpieceId;
    }

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
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
}
