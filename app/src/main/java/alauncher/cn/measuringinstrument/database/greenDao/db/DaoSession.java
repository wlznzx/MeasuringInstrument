package alauncher.cn.measuringinstrument.database.greenDao.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.bean.CriticalBean;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.GroupBean2;
import alauncher.cn.measuringinstrument.bean.MeasureConfigurationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.RememberPasswordBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.bean.ResultData;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StepBean;
import alauncher.cn.measuringinstrument.bean.StepBean2;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.bean.StoreBean2;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.bean.User;

import alauncher.cn.measuringinstrument.database.greenDao.db.CalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.CodeBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.CriticalBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.DeviceInfoBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.MeasureConfigurationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.RememberPasswordBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultDataDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.SetupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig calibrationBeanDaoConfig;
    private final DaoConfig codeBeanDaoConfig;
    private final DaoConfig criticalBeanDaoConfig;
    private final DaoConfig deviceInfoBeanDaoConfig;
    private final DaoConfig forceCalibrationBeanDaoConfig;
    private final DaoConfig groupBeanDaoConfig;
    private final DaoConfig groupBean2DaoConfig;
    private final DaoConfig measureConfigurationBeanDaoConfig;
    private final DaoConfig parameterBeanDaoConfig;
    private final DaoConfig parameterBean2DaoConfig;
    private final DaoConfig rememberPasswordBeanDaoConfig;
    private final DaoConfig resultBeanDaoConfig;
    private final DaoConfig resultBean2DaoConfig;
    private final DaoConfig resultDataDaoConfig;
    private final DaoConfig setupBeanDaoConfig;
    private final DaoConfig stepBeanDaoConfig;
    private final DaoConfig stepBean2DaoConfig;
    private final DaoConfig storeBeanDaoConfig;
    private final DaoConfig storeBean2DaoConfig;
    private final DaoConfig triggerConditionBeanDaoConfig;
    private final DaoConfig userDaoConfig;

    private final CalibrationBeanDao calibrationBeanDao;
    private final CodeBeanDao codeBeanDao;
    private final CriticalBeanDao criticalBeanDao;
    private final DeviceInfoBeanDao deviceInfoBeanDao;
    private final ForceCalibrationBeanDao forceCalibrationBeanDao;
    private final GroupBeanDao groupBeanDao;
    private final GroupBean2Dao groupBean2Dao;
    private final MeasureConfigurationBeanDao measureConfigurationBeanDao;
    private final ParameterBeanDao parameterBeanDao;
    private final ParameterBean2Dao parameterBean2Dao;
    private final RememberPasswordBeanDao rememberPasswordBeanDao;
    private final ResultBeanDao resultBeanDao;
    private final ResultBean2Dao resultBean2Dao;
    private final ResultDataDao resultDataDao;
    private final SetupBeanDao setupBeanDao;
    private final StepBeanDao stepBeanDao;
    private final StepBean2Dao stepBean2Dao;
    private final StoreBeanDao storeBeanDao;
    private final StoreBean2Dao storeBean2Dao;
    private final TriggerConditionBeanDao triggerConditionBeanDao;
    private final UserDao userDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        calibrationBeanDaoConfig = daoConfigMap.get(CalibrationBeanDao.class).clone();
        calibrationBeanDaoConfig.initIdentityScope(type);

        codeBeanDaoConfig = daoConfigMap.get(CodeBeanDao.class).clone();
        codeBeanDaoConfig.initIdentityScope(type);

        criticalBeanDaoConfig = daoConfigMap.get(CriticalBeanDao.class).clone();
        criticalBeanDaoConfig.initIdentityScope(type);

        deviceInfoBeanDaoConfig = daoConfigMap.get(DeviceInfoBeanDao.class).clone();
        deviceInfoBeanDaoConfig.initIdentityScope(type);

        forceCalibrationBeanDaoConfig = daoConfigMap.get(ForceCalibrationBeanDao.class).clone();
        forceCalibrationBeanDaoConfig.initIdentityScope(type);

        groupBeanDaoConfig = daoConfigMap.get(GroupBeanDao.class).clone();
        groupBeanDaoConfig.initIdentityScope(type);

        groupBean2DaoConfig = daoConfigMap.get(GroupBean2Dao.class).clone();
        groupBean2DaoConfig.initIdentityScope(type);

        measureConfigurationBeanDaoConfig = daoConfigMap.get(MeasureConfigurationBeanDao.class).clone();
        measureConfigurationBeanDaoConfig.initIdentityScope(type);

        parameterBeanDaoConfig = daoConfigMap.get(ParameterBeanDao.class).clone();
        parameterBeanDaoConfig.initIdentityScope(type);

        parameterBean2DaoConfig = daoConfigMap.get(ParameterBean2Dao.class).clone();
        parameterBean2DaoConfig.initIdentityScope(type);

        rememberPasswordBeanDaoConfig = daoConfigMap.get(RememberPasswordBeanDao.class).clone();
        rememberPasswordBeanDaoConfig.initIdentityScope(type);

        resultBeanDaoConfig = daoConfigMap.get(ResultBeanDao.class).clone();
        resultBeanDaoConfig.initIdentityScope(type);

        resultBean2DaoConfig = daoConfigMap.get(ResultBean2Dao.class).clone();
        resultBean2DaoConfig.initIdentityScope(type);

        resultDataDaoConfig = daoConfigMap.get(ResultDataDao.class).clone();
        resultDataDaoConfig.initIdentityScope(type);

        setupBeanDaoConfig = daoConfigMap.get(SetupBeanDao.class).clone();
        setupBeanDaoConfig.initIdentityScope(type);

        stepBeanDaoConfig = daoConfigMap.get(StepBeanDao.class).clone();
        stepBeanDaoConfig.initIdentityScope(type);

        stepBean2DaoConfig = daoConfigMap.get(StepBean2Dao.class).clone();
        stepBean2DaoConfig.initIdentityScope(type);

        storeBeanDaoConfig = daoConfigMap.get(StoreBeanDao.class).clone();
        storeBeanDaoConfig.initIdentityScope(type);

        storeBean2DaoConfig = daoConfigMap.get(StoreBean2Dao.class).clone();
        storeBean2DaoConfig.initIdentityScope(type);

        triggerConditionBeanDaoConfig = daoConfigMap.get(TriggerConditionBeanDao.class).clone();
        triggerConditionBeanDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        calibrationBeanDao = new CalibrationBeanDao(calibrationBeanDaoConfig, this);
        codeBeanDao = new CodeBeanDao(codeBeanDaoConfig, this);
        criticalBeanDao = new CriticalBeanDao(criticalBeanDaoConfig, this);
        deviceInfoBeanDao = new DeviceInfoBeanDao(deviceInfoBeanDaoConfig, this);
        forceCalibrationBeanDao = new ForceCalibrationBeanDao(forceCalibrationBeanDaoConfig, this);
        groupBeanDao = new GroupBeanDao(groupBeanDaoConfig, this);
        groupBean2Dao = new GroupBean2Dao(groupBean2DaoConfig, this);
        measureConfigurationBeanDao = new MeasureConfigurationBeanDao(measureConfigurationBeanDaoConfig, this);
        parameterBeanDao = new ParameterBeanDao(parameterBeanDaoConfig, this);
        parameterBean2Dao = new ParameterBean2Dao(parameterBean2DaoConfig, this);
        rememberPasswordBeanDao = new RememberPasswordBeanDao(rememberPasswordBeanDaoConfig, this);
        resultBeanDao = new ResultBeanDao(resultBeanDaoConfig, this);
        resultBean2Dao = new ResultBean2Dao(resultBean2DaoConfig, this);
        resultDataDao = new ResultDataDao(resultDataDaoConfig, this);
        setupBeanDao = new SetupBeanDao(setupBeanDaoConfig, this);
        stepBeanDao = new StepBeanDao(stepBeanDaoConfig, this);
        stepBean2Dao = new StepBean2Dao(stepBean2DaoConfig, this);
        storeBeanDao = new StoreBeanDao(storeBeanDaoConfig, this);
        storeBean2Dao = new StoreBean2Dao(storeBean2DaoConfig, this);
        triggerConditionBeanDao = new TriggerConditionBeanDao(triggerConditionBeanDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);

        registerDao(CalibrationBean.class, calibrationBeanDao);
        registerDao(CodeBean.class, codeBeanDao);
        registerDao(CriticalBean.class, criticalBeanDao);
        registerDao(DeviceInfoBean.class, deviceInfoBeanDao);
        registerDao(ForceCalibrationBean.class, forceCalibrationBeanDao);
        registerDao(GroupBean.class, groupBeanDao);
        registerDao(GroupBean2.class, groupBean2Dao);
        registerDao(MeasureConfigurationBean.class, measureConfigurationBeanDao);
        registerDao(ParameterBean.class, parameterBeanDao);
        registerDao(ParameterBean2.class, parameterBean2Dao);
        registerDao(RememberPasswordBean.class, rememberPasswordBeanDao);
        registerDao(ResultBean.class, resultBeanDao);
        registerDao(ResultBean2.class, resultBean2Dao);
        registerDao(ResultData.class, resultDataDao);
        registerDao(SetupBean.class, setupBeanDao);
        registerDao(StepBean.class, stepBeanDao);
        registerDao(StepBean2.class, stepBean2Dao);
        registerDao(StoreBean.class, storeBeanDao);
        registerDao(StoreBean2.class, storeBean2Dao);
        registerDao(TriggerConditionBean.class, triggerConditionBeanDao);
        registerDao(User.class, userDao);
    }
    
    public void clear() {
        calibrationBeanDaoConfig.clearIdentityScope();
        codeBeanDaoConfig.clearIdentityScope();
        criticalBeanDaoConfig.clearIdentityScope();
        deviceInfoBeanDaoConfig.clearIdentityScope();
        forceCalibrationBeanDaoConfig.clearIdentityScope();
        groupBeanDaoConfig.clearIdentityScope();
        groupBean2DaoConfig.clearIdentityScope();
        measureConfigurationBeanDaoConfig.clearIdentityScope();
        parameterBeanDaoConfig.clearIdentityScope();
        parameterBean2DaoConfig.clearIdentityScope();
        rememberPasswordBeanDaoConfig.clearIdentityScope();
        resultBeanDaoConfig.clearIdentityScope();
        resultBean2DaoConfig.clearIdentityScope();
        resultDataDaoConfig.clearIdentityScope();
        setupBeanDaoConfig.clearIdentityScope();
        stepBeanDaoConfig.clearIdentityScope();
        stepBean2DaoConfig.clearIdentityScope();
        storeBeanDaoConfig.clearIdentityScope();
        storeBean2DaoConfig.clearIdentityScope();
        triggerConditionBeanDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
    }

    public CalibrationBeanDao getCalibrationBeanDao() {
        return calibrationBeanDao;
    }

    public CodeBeanDao getCodeBeanDao() {
        return codeBeanDao;
    }

    public CriticalBeanDao getCriticalBeanDao() {
        return criticalBeanDao;
    }

    public DeviceInfoBeanDao getDeviceInfoBeanDao() {
        return deviceInfoBeanDao;
    }

    public ForceCalibrationBeanDao getForceCalibrationBeanDao() {
        return forceCalibrationBeanDao;
    }

    public GroupBeanDao getGroupBeanDao() {
        return groupBeanDao;
    }

    public GroupBean2Dao getGroupBean2Dao() {
        return groupBean2Dao;
    }

    public MeasureConfigurationBeanDao getMeasureConfigurationBeanDao() {
        return measureConfigurationBeanDao;
    }

    public ParameterBeanDao getParameterBeanDao() {
        return parameterBeanDao;
    }

    public ParameterBean2Dao getParameterBean2Dao() {
        return parameterBean2Dao;
    }

    public RememberPasswordBeanDao getRememberPasswordBeanDao() {
        return rememberPasswordBeanDao;
    }

    public ResultBeanDao getResultBeanDao() {
        return resultBeanDao;
    }

    public ResultBean2Dao getResultBean2Dao() {
        return resultBean2Dao;
    }

    public ResultDataDao getResultDataDao() {
        return resultDataDao;
    }

    public SetupBeanDao getSetupBeanDao() {
        return setupBeanDao;
    }

    public StepBeanDao getStepBeanDao() {
        return stepBeanDao;
    }

    public StepBean2Dao getStepBean2Dao() {
        return stepBean2Dao;
    }

    public StoreBeanDao getStoreBeanDao() {
        return storeBeanDao;
    }

    public StoreBean2Dao getStoreBean2Dao() {
        return storeBean2Dao;
    }

    public TriggerConditionBeanDao getTriggerConditionBeanDao() {
        return triggerConditionBeanDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

}
