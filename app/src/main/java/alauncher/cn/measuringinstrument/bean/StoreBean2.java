package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 日期：2019/4/25 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class StoreBean2 {

    @Id(autoincrement = true)
    public Long id;

    public long codeID;

    public int storeMode;

    @Generated(hash = 1178841882)
    public StoreBean2(Long id, long codeID, int storeMode) {
        this.id = id;
        this.codeID = codeID;
        this.storeMode = storeMode;
    }

    @Generated(hash = 647544563)
    public StoreBean2() {
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

    public int getStoreMode() {
        return this.storeMode;
    }

    public void setStoreMode(int storeMode) {
        this.storeMode = storeMode;
    }

}
