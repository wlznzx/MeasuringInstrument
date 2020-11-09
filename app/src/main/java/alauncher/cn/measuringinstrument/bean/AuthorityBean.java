package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;


@Entity(indexes = {@Index(value = "groupID,  id", unique = true)})
public class AuthorityBean {

    public Long groupID;

    public String id;

    public boolean authorized;

    @Generated(hash = 804904526)
    public AuthorityBean(Long groupID, String id, boolean authorized) {
        this.groupID = groupID;
        this.id = id;
        this.authorized = authorized;
    }

    @Generated(hash = 1226205281)
    public AuthorityBean() {
    }

    public Long getGroupID() {
        return this.groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getAuthorized() {
        return this.authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    @Override
    public String toString() {
        return "AuthorityBean{" +
                "groupID=" + groupID +
                ", id='" + id + '\'' +
                ", authorized=" + authorized +
                '}';
    }
}
