package alauncher.cn.measuringinstrument.database.greenDao.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.bean.ResultData;
import alauncher.cn.measuringinstrument.bean.SetupBean;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;

import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultDataDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.SetupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.CalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig groupBeanDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig resultDataDaoConfig;
    private final DaoConfig setupBeanDaoConfig;
    private final DaoConfig calibrationBeanDaoConfig;
    private final DaoConfig resultBeanDaoConfig;
    private final DaoConfig parameterBeanDaoConfig;

    private final GroupBeanDao groupBeanDao;
    private final UserDao userDao;
    private final ResultDataDao resultDataDao;
    private final SetupBeanDao setupBeanDao;
    private final CalibrationBeanDao calibrationBeanDao;
    private final ResultBeanDao resultBeanDao;
    private final ParameterBeanDao parameterBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        groupBeanDaoConfig = daoConfigMap.get(GroupBeanDao.class).clone();
        groupBeanDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        resultDataDaoConfig = daoConfigMap.get(ResultDataDao.class).clone();
        resultDataDaoConfig.initIdentityScope(type);

        setupBeanDaoConfig = daoConfigMap.get(SetupBeanDao.class).clone();
        setupBeanDaoConfig.initIdentityScope(type);

        calibrationBeanDaoConfig = daoConfigMap.get(CalibrationBeanDao.class).clone();
        calibrationBeanDaoConfig.initIdentityScope(type);

        resultBeanDaoConfig = daoConfigMap.get(ResultBeanDao.class).clone();
        resultBeanDaoConfig.initIdentityScope(type);

        parameterBeanDaoConfig = daoConfigMap.get(ParameterBeanDao.class).clone();
        parameterBeanDaoConfig.initIdentityScope(type);

        groupBeanDao = new GroupBeanDao(groupBeanDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        resultDataDao = new ResultDataDao(resultDataDaoConfig, this);
        setupBeanDao = new SetupBeanDao(setupBeanDaoConfig, this);
        calibrationBeanDao = new CalibrationBeanDao(calibrationBeanDaoConfig, this);
        resultBeanDao = new ResultBeanDao(resultBeanDaoConfig, this);
        parameterBeanDao = new ParameterBeanDao(parameterBeanDaoConfig, this);

        registerDao(GroupBean.class, groupBeanDao);
        registerDao(User.class, userDao);
        registerDao(ResultData.class, resultDataDao);
        registerDao(SetupBean.class, setupBeanDao);
        registerDao(CalibrationBean.class, calibrationBeanDao);
        registerDao(ResultBean.class, resultBeanDao);
        registerDao(ParameterBean.class, parameterBeanDao);
    }
    
    public void clear() {
        groupBeanDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
        resultDataDaoConfig.clearIdentityScope();
        setupBeanDaoConfig.clearIdentityScope();
        calibrationBeanDaoConfig.clearIdentityScope();
        resultBeanDaoConfig.clearIdentityScope();
        parameterBeanDaoConfig.clearIdentityScope();
    }

    public GroupBeanDao getGroupBeanDao() {
        return groupBeanDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public ResultDataDao getResultDataDao() {
        return resultDataDao;
    }

    public SetupBeanDao getSetupBeanDao() {
        return setupBeanDao;
    }

    public CalibrationBeanDao getCalibrationBeanDao() {
        return calibrationBeanDao;
    }

    public ResultBeanDao getResultBeanDao() {
        return resultBeanDao;
    }

    public ParameterBeanDao getParameterBeanDao() {
        return parameterBeanDao;
    }

}
