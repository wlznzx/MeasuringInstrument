package alauncher.cn.measuringinstrument;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoMaster;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoSession;

/**
 * 日期：2019/4/25 0025 10:27
 * 包名：alauncher.cn.measuringinstrument
 * 作者： wlznzx
 * 描述：
 */
public class App extends Application {


    private static DaoSession mDaoSession;

    // public static int codeID = 1;

    public static long SETTING_ID = 1;

    public static String handlerAccout = "吴工";

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "mi.db", null);
        Database db = openHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();

        if (getDaoSession().getSetupBeanDao().load(SETTING_ID) == null) {
            SetupBean _bean = new SetupBean();
            _bean.setCodeID(1);
            _bean.setAccout("wl");
            getDaoSession().getSetupBeanDao().insert(_bean);
        }

        if (getDaoSession().getUserDao().loadAll().size() == 0) {
            User _user = new User();
            _user.setAccout("admin");
            _user.setPassword("123456");
            _user.setName("管理员");
            _user.setStatus(0);
            _user.setId("1");
            getDaoSession().getUserDao().insert(_user);
        }
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static SetupBean getSetupBean() {
        return getDaoSession().getSetupBeanDao().load(SETTING_ID);
    }
}
