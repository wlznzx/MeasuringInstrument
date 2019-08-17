package alauncher.cn.measuringinstrument;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.greendao.database.Database;

import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
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

    public static String factory_code = "恩梯量仪Android版";

    public static String machine_code = "machine_code";

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
            _bean.setIsAutoPopUp(false);
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


        if (getDaoSession().getForceCalibrationBeanDao().load(SETTING_ID) == null) {
            ForceCalibrationBean _bean = new ForceCalibrationBean();
            _bean.set_id(SETTING_ID);
            _bean.setForceMode(0);
            _bean.setForceNum(50);
            _bean.setForceTime(60);
            getDaoSession().getForceCalibrationBeanDao().insert(_bean);
        }

        if (getDaoSession().getStoreBeanDao().load(SETTING_ID) == null) {
            StoreBean _bean = new StoreBean();
            _bean.setId(SETTING_ID);
            _bean.setStoreMode(2);
            _bean.setUpLimitValue(10);
            _bean.setLowLimitValue(-10);
            _bean.setMValue(0);
            _bean.setDelayTime(3);
            getDaoSession().getStoreBeanDao().insert(_bean);
        }

        Bugly.init(getApplicationContext(), "e4d9621d74", false);
        // CrashReport.initCrashReport(getApplicationContext(), "e4d9621d74", false);
        // initTestDatas();
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static SetupBean getSetupBean() {
        return getDaoSession().getSetupBeanDao().load(SETTING_ID);
    }

    public static void setSetupPopUp(boolean isPopUp) {
        SetupBean _bean = getSetupBean();
        _bean.setIsAutoPopUp(isPopUp);
        getDaoSession().getSetupBeanDao().update(_bean);
    }

    public void initTestDatas() {
        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9896, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9914, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.992, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.99, 24, 24, 24, "m1", "m2", "m3", "m4"));
        // 6
        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9914, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9896, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9906, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9896, 24, 24, 24, "m1", "m2", "m3", "m4"));

        // 11
        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9896, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9896, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9902, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9912, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9922, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9912, 24, 24, 24, "m1", "m2", "m3", "m4"));

        // 21
        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.99, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9912, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9914, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.99, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.99, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.991, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9902, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9902, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9896, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        // 31
        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.99, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.99, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9894, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9902, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9896, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9902, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9898, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9902, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 23.9912, 24, 24, 24, "m1", "m2", "m3", "m4"));
    }
}
