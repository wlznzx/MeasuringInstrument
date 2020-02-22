package alauncher.cn.measuringinstrument.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wenld.greendaoupgradehelper.DBMigrationHelper;

import alauncher.cn.measuringinstrument.bean.GroupBean2;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoMaster;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBean2Dao;

public class DBOpenHelper extends DaoMaster.DevOpenHelper {

    public DBOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.d("wlDebug", "oldVersion = " + oldVersion + " newVersion = " + newVersion);
        try {
            DBMigrationHelper migratorHelper = new DBMigrationHelper();
            //判断版本， 设置需要修改得表  我这边设置一个 FileInfo
            if (oldVersion == 27 && newVersion == 28) {
                // migratorHelper.onUpgrade(db, ParameterBean2Dao.class);
                ParameterBean2Dao.createTable(this.wrap(db), true);
            } else if (oldVersion == 28 && newVersion == 29) {
                ResultBean2Dao.createTable(this.wrap(db), true);
            } else if (oldVersion == 29 && newVersion == 30) {
                GroupBean2Dao.createTable(this.wrap(db), true);
            } else if (oldVersion == 31 && newVersion == 32) {
                StoreBean2Dao.createTable(this.wrap(db), true);
            }else {
                super.onUpgrade(db, oldVersion, newVersion);
            }
        } catch (ClassCastException e) {
        }
    }
}
