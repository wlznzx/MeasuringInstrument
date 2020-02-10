package alauncher.cn.measuringinstrument.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import alauncher.cn.measuringinstrument.database.greenDao.db.DaoSession;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBean2Dao;

/**
 * 日期：2019/4/25 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class GroupBean2 implements Parcelable {

    @Id(autoincrement = true)
    public Long id;

    public String name;

    public double pID;

    public double upperLimit;

    public double lowerLimit;

    public String describe;
    

    protected GroupBean2(Parcel in) {
        this.name = in.readString();
        this.pID = in.readDouble();
        this.upperLimit = in.readDouble();
        this.lowerLimit = in.readDouble();
        this.describe = in.readString();
    }

    @Generated(hash = 858871815)
    public GroupBean2(Long id, String name, double pID, double upperLimit,
            double lowerLimit, String describe) {
        this.id = id;
        this.name = name;
        this.pID = pID;
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
        this.describe = describe;
    }

    @Generated(hash = 1972616)
    public GroupBean2() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(pID);
        parcel.writeDouble(upperLimit);
        parcel.writeDouble(lowerLimit);
        parcel.writeString(describe);
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

    public double getPID() {
        return this.pID;
    }

    public void setPID(double pID) {
        this.pID = pID;
    }

    public double getUpperLimit() {
        return this.upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public double getLowerLimit() {
        return this.lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getDescribe() {
        return this.describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public static final Creator<GroupBean2> CREATOR = new Creator<GroupBean2>() {
        @Override
        public GroupBean2 createFromParcel(Parcel in) {
            return new GroupBean2(in);
        }

        @Override
        public GroupBean2[] newArray(int size) {
            return new GroupBean2[size];
        }
    };

}
