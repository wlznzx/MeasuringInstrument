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
public class RememberPasswordBean {

    @Id(autoincrement = true)
    public Long id;

    public boolean isRemeber;

    public String account;

    public String passowrd;

    public boolean logined;

    @Generated(hash = 785140256)
    public RememberPasswordBean(Long id, boolean isRemeber, String account,
            String passowrd, boolean logined) {
        this.id = id;
        this.isRemeber = isRemeber;
        this.account = account;
        this.passowrd = passowrd;
        this.logined = logined;
    }

    @Generated(hash = 1187930347)
    public RememberPasswordBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsRemeber() {
        return this.isRemeber;
    }

    public void setIsRemeber(boolean isRemeber) {
        this.isRemeber = isRemeber;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassowrd() {
        return this.passowrd;
    }

    public void setPassowrd(String passowrd) {
        this.passowrd = passowrd;
    }

    public boolean getLogined() {
        return this.logined;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    
}
