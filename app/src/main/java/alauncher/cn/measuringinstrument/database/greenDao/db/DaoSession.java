package alauncher.cn.measuringinstrument.database.greenDao.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.ForceCalibrationBean;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.ResultData;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.bean.CodeBean;

import alauncher.cn.measuringinstrument.database.greenDao.db.CalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ForceCalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultDataDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.SetupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.CodeBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig calibrationBeanDaoConfig;
    private final DaoConfig forceCalibrationBeanDaoConfig;
    private final DaoConfig groupBeanDaoConfig;
    private final DaoConfig parameterBeanDaoConfig;
    private final DaoConfig resultBeanDaoConfig;
    private final DaoConfig resultDataDaoConfig;
    private final DaoConfig setupBeanDaoConfig;
    private final DaoConfig storeBeanDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig codeBeanDaoConfig;

    private final CalibrationBeanDao calibrationBeanDao;
    private final ForceCalibrationBeanDao forceCalibrationBeanDao;
    private final GroupBeanDao groupBeanDao;
    private final ParameterBeanDao parameterBeanDao;
    private final ResultBeanDao resultBeanDao;
    private final ResultDataDao resultDataDao;
    private final SetupBeanDao setupBeanDao;
    private final StoreBeanDao storeBeanDao;
    private final UserDao userDao;
    private final CodeBeanDao codeBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        calibrationBeanDaoConfig = daoConfigMap.get(CalibrationBeanDao.class).clone();
        calibrationBeanDaoConfig.initIdentityScope(type);

        forceCalibrationBeanDaoConfig = daoConfigMap.get(ForceCalibrationBeanDao.class).clone();
        forceCalibrationBeanDaoConfig.initIdentityScope(type);

        groupBeanDaoConfig = daoConfigMap.get(GroupBeanDao.class).clone();
        groupBeanDaoConfig.initIdentityScope(type);

        parameterBeanDaoConfig = daoConfigMap.get(ParameterBeanDao.class).clone();
        parameterBeanDaoConfig.initIdentityScope(type);

        resultBeanDaoConfig = daoConfigMap.get(ResultBeanDao.class).clone();
        resultBeanDaoConfig.initIdentityScope(type);

        resultDataDaoConfig = daoConfigMap.get(ResultDataDao.class).clone();
        resultDataDaoConfig.initIdentityScope(type);

        setupBeanDaoConfig = daoConfigMap.get(SetupBeanDao.class).clone();
        setupBeanDaoConfig.initIdentityScope(type);

        storeBeanDaoConfig = daoConfigMap.get(StoreBeanDao.class).clone();
        storeBeanDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        codeBeanDaoConfig = daoConfigMap.get(CodeBeanDao.class).clone();
        codeBeanDaoConfig.initIdentityScope(type);

        calibrationBeanDao = new CalibrationBeanDao(calibrationBeanDaoConfig, this);
        forceCalibrationBeanDao = new ForceCalibrationBeanDao(forceCalibrationBeanDaoConfig, this);
        groupBeanDao = new GroupBeanDao(groupBeanDaoConfig, this);
        parameterBeanDao = new ParameterBeanDao(parameterBeanDaoConfig, this);
        resultBeanDao = new ResultBeanDao(resultBeanDaoConfig, this);
        resultDataDao = new ResultDataDao(resultDataDaoConfig, this);
        setupBeanDao = new SetupBeanDao(setupBeanDaoConfig, this);
        storeBeanDao = new StoreBeanDao(storeBeanDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        codeBeanDao = new CodeBeanDao(codeBeanDaoConfig, this);

        registerDao(CalibrationBean.class, calibrationBeanDao);
        registerDao(ForceCalibrationBean.class, forceCalibrationBeanDao);
        registerDao(GroupBean.class, groupBeanDao);
        registerDao(ParameterBean.class, parameterBeanDao);
        registerDao(ResultBean.class, resultBeanDao);
        registerDao(ResultData.class, resultDataDao);
        registerDao(SetupBean.class, setupBeanDao);
        registerDao(StoreBean.class, storeBeanDao);
        registerDao(User.class, userDao);
        registerDao(CodeBean.class, codeBeanDao);
    }
    
    public void clear() {
        calibrationBeanDaoConfig.clearIdentityScope();
        forceCalibrationBeanDaoConfig.clearIdentityScope();
        groupBeanDaoConfig.clearIdentityScope();
        parameterBeanDaoConfig.clearIdentityScope();
        resultBeanDaoConfig.clearIdentityScope();
        resultDataDaoConfig.clearIdentityScope();
        setupBeanDaoConfig.clearIdentityScope();
        storeBeanDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
        codeBeanDaoConfig.clearIdentityScope();
    }

    public CalibrationBeanDao getCalibrationBeanDao() {
        return calibrationBeanDao;
    }

    public ForceCalibrationBeanDao getForceCalibrationBeanDao() {
        return forceCalibrationBeanDao;
    }

    public GroupBeanDao getGroupBeanDao() {
        return groupBeanDao;
    }

    public ParameterBeanDao getParameterBeanDao() {
        return parameterBeanDao;
    }

    public ResultBeanDao getResultBeanDao() {
        return resultBeanDao;
    }

    public ResultDataDao getResultDataDao() {
        return resultDataDao;
    }

    public SetupBeanDao getSetupBeanDao() {
        return setupBeanDao;
    }

    public StoreBeanDao getStoreBeanDao() {
        return storeBeanDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public CodeBeanDao getCodeBeanDao() {
        return codeBeanDao;
    }

}
