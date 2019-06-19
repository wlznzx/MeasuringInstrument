package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 日期：2019/4/25 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class SetupBean {

    public int codeID;

    public String accout;

    @Generated(hash = 1671504447)
    public SetupBean(int codeID, String accout) {
        this.codeID = codeID;
        this.accout = accout;
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

}
