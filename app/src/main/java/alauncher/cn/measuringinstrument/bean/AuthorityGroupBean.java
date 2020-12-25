package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class AuthorityGroupBean {

    @Id(autoincrement = true)
    public Long id;

    public String name;

    public String describe;

    public String remarks;

    public int limit;

    @Generated(hash = 1821103672)
    public AuthorityGroupBean(Long id, String name, String describe, String remarks,
            int limit) {
        this.id = id;
        this.name = name;
        this.describe = describe;
        this.remarks = remarks;
        this.limit = limit;
    }

    @Generated(hash = 788752761)
    public AuthorityGroupBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return this.describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    
}
