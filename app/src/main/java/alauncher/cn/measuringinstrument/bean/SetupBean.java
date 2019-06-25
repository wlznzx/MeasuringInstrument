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

    @Id
    public Long id;

    public int codeID;

    public String accout;

    @Generated(hash = 1809436141)
    public SetupBean(Long id, int codeID, String accout) {
        this.id = id;
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
