package alauncher.cn.measuringinstrument;

import android.content.Context;
import android.content.Intent;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Arrays;

import alauncher.cn.measuringinstrument.bean.AnalysisPatternBean;
import alauncher.cn.measuringinstrument.bean.AuthorityBean;
import alauncher.cn.measuringinstrument.bean.AuthorityGroupBean;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.GroupBean2;
import alauncher.cn.measuringinstrument.bean.MeasureConfigurationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.RememberPasswordBean;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.bean.StoreBean2;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoMaster;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoSession;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBean2Dao;
import alauncher.cn.measuringinstrument.utils.Constants;
import alauncher.cn.measuringinstrument.utils.DBOpenHelper;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import alauncher.cn.measuringinstrument.utils.SPUtils;
import alauncher.cn.measuringinstrument.utils.SystemPropertiesProxy;
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
        // MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化数据库;
        DaoMaster.DevOpenHelper openHelper = new DBOpenHelper(this, "mi.db");
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

        // 这一段，就是集成了Bugly;
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
        MultiDex.install(this);
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static SetupBean getSetupBean() {
        return getDaoSession().getSetupBeanDao().load(SETTING_ID);
    }

    public static AuthorityGroupBean getCurrentAuthorityGroupBean() {
        try {
            android.util.Log.d("wlDebug", "App.handlerAccout = " + App.handlerAccout);
            return getDaoSession().getAuthorityGroupBeanDao().load(getDaoSession().getUserDao().load(App.handlerAccout).getUseAuthorityGroupID());
        } catch (Exception e) {
            return null;
        }
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
        return getDaoSession().getDeviceInfoBeanDao().load(App.SETTING_ID);
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
            _user.setName("系统管理员");
            _user.setStatus(0);
            _user.setId("1");
            _user.setEmail("");
            _user.setUseAuthorityGroupID(0);
            getDaoSession().getUserDao().insert(_user);

            User _manager = new User();
            _manager.setAccout("manager");
            _manager.setPassword("123456");
            _manager.setName("经理");
            _manager.setStatus(0);
            _manager.setId("2");
//            _manager.setLimit(1);
            _manager.setEmail("");
            _manager.setUseAuthorityGroupID(1);
            getDaoSession().getUserDao().insert(_manager);

            User _monitor = new User();
            _monitor.setAccout("monitor");
            _monitor.setPassword("123456");
            _monitor.setName("班长");
            _monitor.setStatus(0);
            _monitor.setId("3");
//            _monitor.setLimit(2);
            _monitor.setEmail("");
            _monitor.setUseAuthorityGroupID(2);
            getDaoSession().getUserDao().insert(_monitor);

            User _operator = new User();
            _operator.setAccout("operator1");
            _operator.setPassword("123456");
            _operator.setName("测试员");
            _operator.setStatus(0);
            _operator.setId("4");
//            _operator.setLimit(5);
            _operator.setEmail("");
            _operator.setUseAuthorityGroupID(3);
            getDaoSession().getUserDao().insert(_operator);

            User _defOperator = new User();
            _defOperator.setAccout("defOperator");
            _defOperator.setPassword("123456");
            _defOperator.setName("默认用户");
            _defOperator.setStatus(0);
            _defOperator.setId("5");
            _defOperator.setEmail("");
            _defOperator.setUseAuthorityGroupID(3);
            getDaoSession().getUserDao().insert(_defOperator);
        }

        if (getDaoSession().getAuthorityGroupBeanDao().loadAll().size() == 0) {
            AuthorityGroupBean _systemManagerGroup = new AuthorityGroupBean();
            _systemManagerGroup.setName("系统管理员组");
            _systemManagerGroup.setId((long) 0);
            _systemManagerGroup.setLimit(0);
            getDaoSession().getAuthorityGroupBeanDao().insert(_systemManagerGroup);
            for (int i = 0; i < 11; i++) {
                AuthorityBean _bean = new AuthorityBean();
                _bean.setId(String.valueOf(i));
                _bean.setGroupID(_systemManagerGroup.getId());
                _bean.setAuthorized(true);
                getDaoSession().getAuthorityBeanDao().insert(_bean);
            }

            AuthorityGroupBean _managerGroup = new AuthorityGroupBean();
            _managerGroup.setName("管理员组");
            _managerGroup.setId((long) 1);
            _managerGroup.setLimit(1);
            getDaoSession().getAuthorityGroupBeanDao().insert(_managerGroup);
            for (int i = 0; i < 11; i++) {
                AuthorityBean _bean = new AuthorityBean();
                _bean.setId(String.valueOf(i));
                _bean.setGroupID(_managerGroup.getId());
                _bean.setAuthorized(true);
                getDaoSession().getAuthorityBeanDao().insert(_bean);
            }

            AuthorityGroupBean _monitorGroup = new AuthorityGroupBean();
            _monitorGroup.setName("班长组");
            _monitorGroup.setId((long) 2);
            _monitorGroup.setLimit(2);
            getDaoSession().getAuthorityGroupBeanDao().insert(_monitorGroup);
            for (int i = 0; i < 11; i++) {
                AuthorityBean _bean = new AuthorityBean();
                _bean.setId(String.valueOf(i));
                _bean.setGroupID(_monitorGroup.getId());
                if (i == 9 || i == 5) {
                    _bean.setAuthorized(false);
                } else {
                    _bean.setAuthorized(true);
                }
                getDaoSession().getAuthorityBeanDao().insert(_bean);
            }

            AuthorityGroupBean _operatorGroup = new AuthorityGroupBean();
            _operatorGroup.setName("操作员组");
            _operatorGroup.setId((long) 3);
            _operatorGroup.setLimit(3);
            getDaoSession().getAuthorityGroupBeanDao().insert(_operatorGroup);
            for (int i = 0; i < 11; i++) {
                AuthorityBean _bean = new AuthorityBean();
                _bean.setId(String.valueOf(i));
                _bean.setGroupID(_operatorGroup.getId());
                if (i == 0 || i == 1 || i == 3 || i == 4 || i == 10) {
                    _bean.setAuthorized(true);
                    if (i == 3) {
                        for (int j = 0; j < 10; j++) {
                            AuthorityBean _sBean = new AuthorityBean();
                            _sBean.setId(i + "_" + j);
                            _sBean.setGroupID(_operatorGroup.getId());
                            if (j == 0 || j == 6 || j == 7 || j == 8 || j == 9) {
                                _sBean.setAuthorized(true);
                            } else {
                                _sBean.setAuthorized(false);
                            }
                            getDaoSession().getAuthorityBeanDao().insert(_sBean);
                        }
                    }
                    if (i == 1) {
                        for (int j = 0; j < 1; j++) {
                            AuthorityBean _sBean = new AuthorityBean();
                            _sBean.setId(i + "_" + j);
                            _sBean.setGroupID(_operatorGroup.getId());
                            if (j == 1) {
                                _sBean.setAuthorized(true);
                            } else {
                                _sBean.setAuthorized(false);
                            }
                            getDaoSession().getAuthorityBeanDao().insert(_sBean);
                        }
                    }
                } else {
                    _bean.setAuthorized(false);
                }
                getDaoSession().getAuthorityBeanDao().insert(_bean);
            }

            // 默认组;
            AuthorityGroupBean _defaultGroup = new AuthorityGroupBean();
            _defaultGroup.setName("默认组");
            _defaultGroup.setId((long) 4);
            _defaultGroup.setLimit(100);
            getDaoSession().getAuthorityGroupBeanDao().insert(_defaultGroup);
            for (int i = 0; i < 11; i++) {
                AuthorityBean _bean = new AuthorityBean();
                _bean.setId(String.valueOf(i));
                _bean.setGroupID(_defaultGroup.getId());
                if (i == 0 || i == 1 || i == 3 || i == 4 || i == 8 || i == 10) {
                    _bean.setAuthorized(true);
                } else {
                    _bean.setAuthorized(false);
                }
                getDaoSession().getAuthorityBeanDao().insert(_bean);
            }
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
            _bean.setManufacturer(getResources().getString(R.string.manufacturer_name));
            _bean.setDeviceCode(SystemPropertiesProxy.getString(this, "ro.serialno"));
            _bean.setDeviceName(getResources().getString(R.string.default_device_name));
            _bean.setRmk("rmk");
            getDaoSession().getDeviceInfoBeanDao().insertOrReplace(_bean);
        }
        /*
        if (getDaoSession().getForceCalibrationBeanDao().load(SETTING_ID) == null) {
            ForceCalibrationBean _bean = new ForceCalibrationBean();
            _bean.set_id(SETTING_ID);
            _bean.setForceMode(0);
            _bean.setForceNum(50);
            _bean.setForceTime(60);
            getDaoSession().getForceCalibrationBeanDao().insert(_bean);
        }
         */

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

        // 默认10个程序;
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
                _bean.setCh1CompensationValue(0);
                _bean.setCh1KValue(0.01);

                _bean.setCh2BigPartStandard(30.059);
                _bean.setCh2SmallPartStandard(30.0168);
                _bean.setCh2CalibrationType(1);
                _bean.setCh2CompensationValue(0);
                _bean.setCh2KValue(0.01);

                _bean.setCh3BigPartStandard(30.059);
                _bean.setCh3SmallPartStandard(30.0168);
                _bean.setCh3CalibrationType(1);
                _bean.setCh3CompensationValue(0);
                _bean.setCh3KValue(0.01);

                _bean.setCh4BigPartStandard(30.059);
                _bean.setCh4SmallPartStandard(30.0168);
                _bean.setCh4CalibrationType(1);
                _bean.setCh4CompensationValue(0);
                _bean.setCh4KValue(0.01);
                getDaoSession().getCalibrationBeanDao().insert(_bean);
            }

            if (getDaoSession().getForceCalibrationBeanDao().load((long) i) == null) {
                ForceCalibrationBean _bean = new ForceCalibrationBean();
                _bean.set_id(i);
                _bean.setForceMode(0);
                _bean.setForceNum(50);
                _bean.setForceTime(60);
                getDaoSession().getForceCalibrationBeanDao().insert(_bean);
            }

            if (getDaoSession().getMeasureConfigurationBeanDao().load((long) i) == null) {
                MeasureConfigurationBean _bean = new MeasureConfigurationBean();
                _bean.setCode_id(i);
                _bean.setMeasureMode(0);
                _bean.setIsShowChart(true);
                getDaoSession().getMeasureConfigurationBeanDao().insert(_bean);
            }

            if (App.getDaoSession().getStoreBean2Dao()
                    .queryBuilder().where(StoreBean2Dao.Properties.CodeID.eq(i)).unique() == null) {
                StoreBean2 _bean = new StoreBean2();
                _bean.setCodeID(i);
                _bean.setStoreMode(0);
                getDaoSession().getStoreBean2Dao().insert(_bean);
            }

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

            if (getDaoSession().getCodeBeanDao().load((long) (i)) == null) {
                CodeBean _bean = new CodeBean();
                _bean.setId(Long.valueOf(i));
                _bean.setName("程序" + i);
                _bean.setMachineTool(getResources().getString(R.string.machine_tool) + i);
                _bean.setParts(getResources().getString(R.string.spare_parts) + i);
                getDaoSession().getCodeBeanDao().insert(_bean);
            }

            // 判断该程序内是否有参数设置，如没有，设置4个初始的M值;
            if (getDaoSession().getParameterBean2Dao().queryBuilder()
                    .where(ParameterBean2Dao.Properties.CodeID.eq((long) i)).list().size() <= 0) {
                for (int j = 0; j < 4; j++) {
                    ParameterBean2 _bean = new ParameterBean2();
                    _bean.setCodeID(i);
                    _bean.setSequenceNumber(j);
                    _bean.setCode("ch1");
                    _bean.setDescribe("内径" + (j + 1));
                    _bean.setNominalValue(30);
                    _bean.setUpperToleranceValue(0.04);
                    _bean.setLowerToleranceValue(0.0);
                    _bean.setResolution(6);
                    _bean.setDeviation(0);
                    _bean.setEnable(true);
                    Long pID = getDaoSession().getParameterBean2Dao().insertOrReplace(_bean);
                    for (int z = 0; z < 4; z++) {
                        // 给每个参数赋分组初值;
                        GroupBean2 groupBean2 = new GroupBean2();
                        groupBean2.setName(String.valueOf(z + 1));
                        groupBean2.setDescribe(String.valueOf(z));
                        groupBean2.setUpperLimit(30 + (z + 1) * 0.01);
                        groupBean2.setLowerLimit(30 + z * 0.01);
                        groupBean2.setPID(pID);
                        getDaoSession().getGroupBean2Dao().insertOrReplace(groupBean2);
                    }
                    // 初始化分析模式;
                    AnalysisPatternBean _analysisPatternBean = new AnalysisPatternBean();
                    _analysisPatternBean.setPID(pID);
                    _analysisPatternBean.setIsAAuto(true);
                    _analysisPatternBean.setIsLineAuto(true);
                    _analysisPatternBean.setUclX(0);
                    _analysisPatternBean.setLclX(0);
                    _analysisPatternBean.setUclR(0);
                    _analysisPatternBean.setLclR(0);
                    _analysisPatternBean.setA3(0);
                    _analysisPatternBean.set_a3(0);
                    App.getDaoSession().getAnalysisPatternBeanDao().insert(_analysisPatternBean);
                }
            }
        }
        // 初始化

        // initTestDatas();
//        initTestDatas2();
    }
    /*
    public void initTestDatas() {
        Arrays.asList("23.9896", "24", "24", "24");
        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9896", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9914", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.992", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.992", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.99", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));
        // 6
        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9898", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9914", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9896", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9906", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9896", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        // 11
        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9896", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9896", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9902", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9912", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9922", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9898", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9898", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9898", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9898", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9912", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        // 21
        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.99", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9912", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9914", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.99", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.99", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.991", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9902", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9902", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9896", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9898", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        // 31
        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.99", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9898", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.99", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9894", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9902", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9896", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9902", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9898", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9902", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false,"- -", "- -", "- -",Arrays.asList("23.9912", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));
    }
    */

    public void initTestDatas2() {
        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0088005065918", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0117988586426", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0066986083984", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0082015991211", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0122985839844", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));
        // 6
        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0084991455078", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0127983093262", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0089988708496", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0083999633789", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0088996887207", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        // 11
        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0099983215332", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0084991455078", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0099983215332", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0124015808105", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0102996826172", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0136985778809", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.013801574707", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.013801574707", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0139007568359", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));

        App.getDaoSession().getResultBean2Dao().insert(new ResultBean2(null, 1, "admin",
                System.currentTimeMillis(), "wkid", "wkex", "eventid", "ev",
                "OK", false, "- -", "- -", "- -", Arrays.asList("36.0139007568359", "24", "24", "24"), Arrays.asList("m1", "m2", "m3", "m4"), Arrays.asList("1", "2", "3", "4"),
                null, null, null, null, true));
    }

}
