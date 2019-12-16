package alauncher.cn.measuringinstrument;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;

import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.RememberPasswordBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoMaster;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoSession;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.utils.Constants;
import alauncher.cn.measuringinstrument.utils.DeviceUtils;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import alauncher.cn.measuringinstrument.utils.SPUtils;
import alauncher.cn.measuringinstrument.utils.SystemPropertiesProxy;
import alauncher.cn.measuringinstrument.view.CodeActivity;
import alauncher.cn.measuringinstrument.view.SystemManagementActivity;
import alauncher.cn.measuringinstrument.view.TActivity;
import alauncher.cn.measuringinstrument.view.UpgradeActivity;

/**
 * 日期：2019/4/25 0025 10:27
 * 包名：alauncher.cn.measuringinstrument
 * 作者： wlznzx
 * 描述：
 */
public class App extends MultiDexApplication {


    private static DaoSession mDaoSession;

    // public static int codeID = 1;

    public static long SETTING_ID = 1;

    public static String handlerAccout = "恩梯";

    public static String factory_code = "TEFA";

    public static String machine_code = "TEFA_A_001";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "mi.db", null);
        Database db = openHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        // 初始化默认的数值;
        new Thread(new Runnable() {
            @Override
            public void run() {
                initDefaultDate();
            }
        }).start();

//        Beta.autoCheckUpgrade = true;
        // Beta.canShowUpgradeActs.add(SystemManagementActivity.class);
        Beta.enableNotification = false;
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                if (strategy != null) {
                    Intent i = new Intent();
                    i.setClass(getApplicationContext(), UpgradeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    // Toast.makeText(App.this, R.string.no_more_version, Toast.LENGTH_LONG).show();
                }
            }
        };
        Bugly.init(getApplicationContext(), "e4d9621d74", true);

        // CrashReport.initCrashReport(getApplicationContext(), "e4d9621d74", false);
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

    public static String getCodeName() {
        String codeName = "";
        CodeBean _CodeBean = App.getDaoSession().getCodeBeanDao().load((long) getSetupBean().getCodeID());
        if (_CodeBean != null) {
            codeName = _CodeBean.getName();
        } else {
            codeName = "程序" + App.getSetupBean().getCodeID();
        }
        return codeName;
    }

    public static DeviceInfoBean getDeviceInfo() {
        return getDaoSession().getDeviceInfoBeanDao().load(SETTING_ID);
    }

    public void initDefaultDate() {

        JdbcUtil.IP = String.valueOf(SPUtils.get(this, Constants.IP_KEY, "47.98.58.40"));

        if (getDaoSession().getSetupBeanDao().load(SETTING_ID) == null) {
            SetupBean _bean = new SetupBean();
            _bean.setCodeID(1);
            _bean.setAccout("wl");
            _bean.setIsAutoPopUp(false);
            _bean.setXUpperLine(0.007);
            _bean.setXLowerLine(-0.007);
            getDaoSession().getSetupBeanDao().insert(_bean);
        }

        if (getDaoSession().getUserDao().loadAll().size() == 0) {
            User _user = new User();
            _user.setAccout("admin");
            _user.setPassword("123456");
            _user.setName("管理员");
            _user.setStatus(0);
            _user.setId("1");
            _user.setLimit(0);
            _user.setEmail("");
            getDaoSession().getUserDao().insert(_user);

            User _manager = new User();
            _manager.setAccout("manager");
            _manager.setPassword("123456");
            _manager.setName("经理");
            _manager.setStatus(0);
            _manager.setId("2");
            _manager.setLimit(0);
            _manager.setEmail("");
            getDaoSession().getUserDao().insert(_manager);

            User _monitor = new User();
            _monitor.setAccout("monitor");
            _monitor.setPassword("123456");
            _monitor.setName("班长");
            _monitor.setStatus(0);
            _monitor.setId("3");
            _monitor.setLimit(0);
            _monitor.setEmail("");
            getDaoSession().getUserDao().insert(_monitor);

            User _operator = new User();
            _operator.setAccout("operator");
            _operator.setPassword("123456");
            _operator.setName("测试员");
            _operator.setStatus(0);
            _operator.setId("4");
            _operator.setLimit(4);
            _operator.setEmail("");
            getDaoSession().getUserDao().insert(_operator);
        }


        if (getDaoSession().getRememberPasswordBeanDao().load(App.SETTING_ID) == null) {
            RememberPasswordBean _bean = new RememberPasswordBean();
            _bean.setId(SETTING_ID);
            _bean.setAccount("");
            _bean.setPassowrd("");
            _bean.setIsRemeber(false);
            _bean.setLogined(false);
            getDaoSession().getRememberPasswordBeanDao().insertOrReplace(_bean);
        }

        if (getDaoSession().getDeviceInfoBeanDao().load(App.SETTING_ID) == null) {
            DeviceInfoBean _bean = new DeviceInfoBean();
            _bean.setId(SETTING_ID);
            _bean.setFactoryCode(getResources().getString(R.string.default_factory_code));
            _bean.setFactoryName(getResources().getString(R.string.default_factory_name));
            _bean.setManufacturer(getResources().getString(R.string.manufacturer));
            _bean.setDeviceCode(SystemPropertiesProxy.getString(this, "ro.serialno"));
            _bean.setDeviceName(getResources().getString(R.string.default_device_name));
            _bean.setRmk("rmk");
            android.util.Log.d("wlDebug", "info = " + _bean.toString());
            getDaoSession().getDeviceInfoBeanDao().insertOrReplace(_bean);
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
            _bean.setUpLimitValue(new ArrayList<>());
            _bean.setLowLimitValue(new ArrayList<>());
            _bean.setStable(new ArrayList<>());
            _bean.setIsScale(new ArrayList<>());
            _bean.setScale(new ArrayList<>());
            for (int i = 0; i < 4; i++) {
                _bean.getUpLimitValue().add(String.valueOf(30.045 + i * 1));
                _bean.getLowLimitValue().add(String.valueOf(29.995 + i * 1));
                _bean.getStable().add(String.valueOf(0));
                _bean.getIsScale().add(String.valueOf(0));
                _bean.getScale().add(String.valueOf(0.9));
            }
            _bean.setMValue(0);
            _bean.setDelayTime(1);
            getDaoSession().getStoreBeanDao().insert(_bean);
        }

        //
        for (int i = 1; i <= 10; i++) {
            // 初始化自动保存上下限;

            if (getDaoSession().getCalibrationBeanDao().load((long) i) == null) {
                CalibrationBean _bean = new CalibrationBean();
                _bean.setCode_id(i);
                if (1 == i) {
                    _bean.setCh1BigPartStandard(30.059);
                    _bean.setCh1SmallPartStandard(30.0168);

                    _bean.setCh2BigPartStandard(30.059);
                    _bean.setCh2SmallPartStandard(30.0168);

                    _bean.setCh3BigPartStandard(30.059);
                    _bean.setCh3SmallPartStandard(30.0168);

                    _bean.setCh4BigPartStandard(30.059);
                    _bean.setCh4SmallPartStandard(30.0168);
                } else {
                    _bean.setCh1BigPartStandard(30.04);
                    _bean.setCh1SmallPartStandard(30);

                    _bean.setCh2BigPartStandard(30.04);
                    _bean.setCh2SmallPartStandard(30);

                    _bean.setCh3BigPartStandard(30.04);
                    _bean.setCh3SmallPartStandard(30);

                    _bean.setCh4BigPartStandard(30.04);
                    _bean.setCh4SmallPartStandard(30);
                }
                _bean.setCh1CalibrationType(1);
                _bean.setCh1CompensationValue(0.007);
                _bean.setCh1KValue(0.01);

                _bean.setCh2BigPartStandard(30.059);
                _bean.setCh2SmallPartStandard(30.0168);
                _bean.setCh2CalibrationType(1);
                _bean.setCh2CompensationValue(0.007);
                _bean.setCh2KValue(0.01);

                _bean.setCh3BigPartStandard(30.059);
                _bean.setCh3SmallPartStandard(30.0168);
                _bean.setCh3CalibrationType(1);
                _bean.setCh3CompensationValue(0.007);
                _bean.setCh3KValue(0.01);

                _bean.setCh4BigPartStandard(30.059);
                _bean.setCh4SmallPartStandard(30.0168);
                _bean.setCh4CalibrationType(1);
                _bean.setCh4CompensationValue(0.007);
                _bean.setCh4KValue(0.01);
                getDaoSession().getCalibrationBeanDao().insert(_bean);
            }

            /*
            if (getDaoSession().getTriggerConditionBeanDao().queryBuilder().where(TriggerConditionBeanDao.Properties.CodeID.eq(i)).list().size() == 0) {
                for (int j = 1; j <= 4; j++) {
                    TriggerConditionBean _bean = new TriggerConditionBean();
                    _bean.setMIndex(j);
                    _bean.setCodeID(i);
                    _bean.setConditionName("条件" + j);
                    _bean.setIsScale(false);
                    _bean.setScale(0.9);
                    _bean.setUpperLimit(100);
                    _bean.setLowerLimit(-100);
                    getDaoSession().getTriggerConditionBeanDao().insert(_bean);
                }
            }
            */

            // 初始化分组;
            if (getDaoSession().getParameterBeanDao().load((long) i) == null) {
                for (int j = 1; j <= 4; j++) {
                    GroupBean _bean = new GroupBean();
                    _bean.setCode_id(i);
                    _bean.setM_index(j);
                    _bean.setA_describe("恩");
                    _bean.setA_upper_limit(30.04);
                    _bean.setA_lower_limit(30.03);

                    _bean.setB_describe("梯");
                    _bean.setB_upper_limit(30.03);
                    _bean.setB_lower_limit(30.02);

                    _bean.setC_describe("科");
                    _bean.setC_upper_limit(30.02);
                    _bean.setC_lower_limit(30.01);

                    _bean.setD_describe("技");
                    _bean.setD_upper_limit(30.01);
                    _bean.setD_lower_limit(30);
                    getDaoSession().getGroupBeanDao().insert(_bean);
                }
            }

            // 初始化参数;
            if (getDaoSession().getParameterBeanDao().load((long) i) == null) {
                ParameterBean _bean = new ParameterBean();
                _bean.setCode_id(i);
                _bean.setM1_enable(true);
                _bean.setM1_describe("内径1");
                _bean.setM1_nominal_value(30.0);
                _bean.setM1_upper_tolerance_value(0.04);
                _bean.setM1_lower_tolerance_value(0.0);
                _bean.setM1_scale(0.1);
                _bean.setM1_offect(0.0);
                _bean.setM1_code("ch1");

                _bean.setM2_enable(true);
                _bean.setM2_describe("内径2");
                _bean.setM2_nominal_value(30.0);
                _bean.setM2_upper_tolerance_value(0.04);
                _bean.setM2_lower_tolerance_value(0.0);
                _bean.setM2_scale(0.1);
                _bean.setM2_offect(0.0);
                _bean.setM2_code("ch2");

                _bean.setM3_enable(true);
                _bean.setM3_describe("内径3");
                _bean.setM3_nominal_value(30.0);
                _bean.setM3_upper_tolerance_value(0.04);
                _bean.setM3_lower_tolerance_value(0.0);
                _bean.setM3_scale(0.1);
                _bean.setM3_offect(0.0);
                _bean.setM3_code("ch3");

                _bean.setM4_enable(true);
                _bean.setM4_describe("内径4");
                _bean.setM4_nominal_value(30.0);
                _bean.setM4_upper_tolerance_value(0.04);
                _bean.setM4_lower_tolerance_value(0.0);
                _bean.setM4_scale(0.1);
                _bean.setM4_offect(0.0);
                _bean.setM4_code("ch4");
                getDaoSession().getParameterBeanDao().insert(_bean);
            }

            if (getDaoSession().getCodeBeanDao().load((long) (i)) == null) {
                CodeBean _bean = new CodeBean();
                _bean.setCodeID(i);
                _bean.setName("程序" + i);
                _bean.setMachineTool(getResources().getString(R.string.machine_tool) + i);
                _bean.setParts(getResources().getString(R.string.spare_parts) + i);
                getDaoSession().getCodeBeanDao().insert(_bean);
            }
        }

        // initTestDatas();
        // initTestDatas2();
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


    public void initTestDatas2() {
        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0088005065918, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0117988586426, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0066986083984, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0082015991211, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0122985839844, 24, 24, 24, "m1", "m2", "m3", "m4"));
        // 6
        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0084991455078, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0127983093262, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0089988708496, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0083999633789, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0088996887207, 24, 24, 24, "m1", "m2", "m3", "m4"));

        // 11
        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0099983215332, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0084991455078, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0099983215332, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0124015808105, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0102996826172, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0136985778809, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.013801574707, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.013801574707, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0139007568359, 24, 24, 24, "m1", "m2", "m3", "m4"));

        App.getDaoSession().getResultBeanDao().insert(new ResultBean(null, 1, "吴工",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "合格", 36.0139007568359, 24, 24, 24, "m1", "m2", "m3", "m4"));
    }
}
