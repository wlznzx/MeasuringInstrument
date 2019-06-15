package alauncher.cn.measuringinstrument.database.greenDao.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.bean.ResultData;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.GroupBeam;

import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultDataDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.CalibrationBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBeamDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig resultDataDaoConfig;
    private final DaoConfig parameterBeanDaoConfig;
    private final DaoConfig calibrationBeanDaoConfig;
    private final DaoConfig groupBeamDaoConfig;

    private final UserDao userDao;
    private final ResultDataDao resultDataDao;
    private final ParameterBeanDao parameterBeanDao;
    private final CalibrationBeanDao calibrationBeanDao;
    private final GroupBeamDao groupBeamDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        resultDataDaoConfig = daoConfigMap.get(ResultDataDao.class).clone();
        resultDataDaoConfig.initIdentityScope(type);

        parameterBeanDaoConfig = daoConfigMap.get(ParameterBeanDao.class).clone();
        parameterBeanDaoConfig.initIdentityScope(type);

        calibrationBeanDaoConfig = daoConfigMap.get(CalibrationBeanDao.class).clone();
        calibrationBeanDaoConfig.initIdentityScope(type);

        groupBeamDaoConfig = daoConfigMap.get(GroupBeamDao.class).clone();
        groupBeamDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        resultDataDao = new ResultDataDao(resultDataDaoConfig, this);
        parameterBeanDao = new ParameterBeanDao(parameterBeanDaoConfig, this);
        calibrationBeanDao = new CalibrationBeanDao(calibrationBeanDaoConfig, this);
        groupBeamDao = new GroupBeamDao(groupBeamDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(ResultData.class, resultDataDao);
        registerDao(ParameterBean.class, parameterBeanDao);
        registerDao(CalibrationBean.class, calibrationBeanDao);
        registerDao(GroupBeam.class, groupBeamDao);
    }
    
    public void clear() {
        userDaoConfig.clearIdentityScope();
        resultDataDaoConfig.clearIdentityScope();
        parameterBeanDaoConfig.clearIdentityScope();
        calibrationBeanDaoConfig.clearIdentityScope();
        groupBeamDaoConfig.clearIdentityScope();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public ResultDataDao getResultDataDao() {
        return resultDataDao;
    }

    public ParameterBeanDao getParameterBeanDao() {
        return parameterBeanDao;
    }

    public CalibrationBeanDao getCalibrationBeanDao() {
        return calibrationBeanDao;
    }

    public GroupBeamDao getGroupBeamDao() {
        return groupBeamDao;
    }

}
