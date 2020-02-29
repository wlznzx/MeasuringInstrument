package alauncher.cn.measuringinstrument.database.greenDao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 33): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 33;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(Database db, boolean ifNotExists) {
        CalibrationBeanDao.createTable(db, ifNotExists);
        CodeBeanDao.createTable(db, ifNotExists);
        CriticalBeanDao.createTable(db, ifNotExists);
        DeviceInfoBeanDao.createTable(db, ifNotExists);
        ForceCalibrationBeanDao.createTable(db, ifNotExists);
        GroupBeanDao.createTable(db, ifNotExists);
        GroupBean2Dao.createTable(db, ifNotExists);
        MeasureConfigurationBeanDao.createTable(db, ifNotExists);
        ParameterBeanDao.createTable(db, ifNotExists);
        ParameterBean2Dao.createTable(db, ifNotExists);
        RememberPasswordBeanDao.createTable(db, ifNotExists);
        ResultBeanDao.createTable(db, ifNotExists);
        ResultBean2Dao.createTable(db, ifNotExists);
        ResultDataDao.createTable(db, ifNotExists);
        SetupBeanDao.createTable(db, ifNotExists);
        StepBeanDao.createTable(db, ifNotExists);
        StepBean2Dao.createTable(db, ifNotExists);
        StoreBeanDao.createTable(db, ifNotExists);
        TriggerConditionBeanDao.createTable(db, ifNotExists);
        UserDao.createTable(db, ifNotExists);
        StoreBean2Dao.createTable(db, ifNotExists);
    }

    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(Database db, boolean ifExists) {
        CalibrationBeanDao.dropTable(db, ifExists);
        CodeBeanDao.dropTable(db, ifExists);
        CriticalBeanDao.dropTable(db, ifExists);
        DeviceInfoBeanDao.dropTable(db, ifExists);
        ForceCalibrationBeanDao.dropTable(db, ifExists);
        GroupBeanDao.dropTable(db, ifExists);
        GroupBean2Dao.dropTable(db, ifExists);
        MeasureConfigurationBeanDao.dropTable(db, ifExists);
        ParameterBeanDao.dropTable(db, ifExists);
        ParameterBean2Dao.dropTable(db, ifExists);
        RememberPasswordBeanDao.dropTable(db, ifExists);
        ResultBeanDao.dropTable(db, ifExists);
        ResultBean2Dao.dropTable(db, ifExists);
        ResultDataDao.dropTable(db, ifExists);
        SetupBeanDao.dropTable(db, ifExists);
        StepBeanDao.dropTable(db, ifExists);
        StepBean2Dao.dropTable(db, ifExists);
        StoreBeanDao.dropTable(db, ifExists);
        TriggerConditionBeanDao.dropTable(db, ifExists);
        UserDao.dropTable(db, ifExists);
        StoreBean2Dao.dropTable(db, ifExists);
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     * Convenience method using a {@link DevOpenHelper}.
     */
    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(CalibrationBeanDao.class);
        registerDaoClass(CodeBeanDao.class);
        registerDaoClass(CriticalBeanDao.class);
        registerDaoClass(DeviceInfoBeanDao.class);
        registerDaoClass(ForceCalibrationBeanDao.class);
        registerDaoClass(GroupBeanDao.class);
        registerDaoClass(GroupBean2Dao.class);
        registerDaoClass(MeasureConfigurationBeanDao.class);
        registerDaoClass(ParameterBeanDao.class);
        registerDaoClass(ParameterBean2Dao.class);
        registerDaoClass(RememberPasswordBeanDao.class);
        registerDaoClass(ResultBeanDao.class);
        registerDaoClass(ResultBean2Dao.class);
        registerDaoClass(ResultDataDao.class);
        registerDaoClass(SetupBeanDao.class);
        registerDaoClass(StepBeanDao.class);
        registerDaoClass(StepBean2Dao.class);
        registerDaoClass(StoreBeanDao.class);
        registerDaoClass(TriggerConditionBeanDao.class);
        registerDaoClass(UserDao.class);
        registerDaoClass(StoreBean2Dao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
