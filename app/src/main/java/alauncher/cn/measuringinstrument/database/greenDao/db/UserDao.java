package alauncher.cn.measuringinstrument.database.greenDao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import alauncher.cn.measuringinstrument.bean.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER".
*/
public class UserDao extends AbstractDao<User, String> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Accout = new Property(0, String.class, "accout", true, "ACCOUT");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Password = new Property(2, String.class, "password", false, "PASSWORD");
        public final static Property Status = new Property(3, int.class, "status", false, "STATUS");
        public final static Property Email = new Property(4, String.class, "email", false, "EMAIL");
        public final static Property Id = new Property(5, String.class, "id", false, "ID");
    }


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                "\"ACCOUT\" TEXT PRIMARY KEY NOT NULL ," + // 0: accout
                "\"NAME\" TEXT NOT NULL ," + // 1: name
                "\"PASSWORD\" TEXT NOT NULL ," + // 2: password
                "\"STATUS\" INTEGER NOT NULL ," + // 3: status
                "\"EMAIL\" TEXT," + // 4: email
                "\"ID\" TEXT);"); // 5: id
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, User entity) {
        stmt.clearBindings();
 
        String accout = entity.getAccout();
        if (accout != null) {
            stmt.bindString(1, accout);
        }
        stmt.bindString(2, entity.getName());
        stmt.bindString(3, entity.getPassword());
        stmt.bindLong(4, entity.getStatus());
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(5, email);
        }
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(6, id);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        String accout = entity.getAccout();
        if (accout != null) {
            stmt.bindString(1, accout);
        }
        stmt.bindString(2, entity.getName());
        stmt.bindString(3, entity.getPassword());
        stmt.bindLong(4, entity.getStatus());
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(5, email);
        }
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(6, id);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // accout
            cursor.getString(offset + 1), // name
            cursor.getString(offset + 2), // password
            cursor.getInt(offset + 3), // status
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // email
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // id
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setAccout(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setPassword(cursor.getString(offset + 2));
        entity.setStatus(cursor.getInt(offset + 3));
        entity.setEmail(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final String updateKeyAfterInsert(User entity, long rowId) {
        return entity.getAccout();
    }
    
    @Override
    public String getKey(User entity) {
        if(entity != null) {
            return entity.getAccout();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(User entity) {
        return entity.getAccout() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
