package com.example.scalephoto.cache.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * BI DB helper
 */
public class SnsBiDbHelper extends SQLiteOpenHelper {


    public SnsBiDbHelper(Context context) {
        super(context, SnsBiDbContent.DATABASE_NAME, null, SnsBiDbContent.DATABASE_VERSION);
    }

    // 建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建表
        createSnsBiTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级后，删除旧表
        if (newVersion >= 2 && oldVersion < 2) {
            destorySnsBiTable(db);
        }
        // 升级后，重建表
        onCreate(db);
    }
    //#################################################################

    /**
     * 建立Sns BI 数据存储表
     *
     * @param db
     */
    private void createSnsBiTable(SQLiteDatabase db) {
        StringBuffer createCacheSql = new StringBuffer()
                .append("CREATE TABLE IF NOT EXISTS ")
                //
                .append(SnsBiDbContent.SnsBiTable.TABLE_NAME)
                //
                .append(" (")
                //
                .append("_id integer primary key autoincrement,")
                //
                .append(SnsBiDbContent.SnsBiTable.CTIME)
                .append(" varchar(255),")
                //
                .append(SnsBiDbContent.SnsBiTable.PAGE)
                .append(" varchar(255),")
                //
                .append(SnsBiDbContent.SnsBiTable.EVENT)
                .append(" varchar(255),")
                //
                .append(SnsBiDbContent.SnsBiTable.IP)
                .append(" varchar(255),")
                //
                .append(SnsBiDbContent.SnsBiTable.NETWORK_TYPE)
                .append(" varchar(255),")
                //
                .append(SnsBiDbContent.SnsBiTable.APP_VERSION)
                .append(" varchar(255),")
                //
                .append(SnsBiDbContent.SnsBiTable.OPERATOR)
                .append(" varchar(255),")
                //
                .append(SnsBiDbContent.SnsBiTable.BI_DATA)
                .append(" blob")
                //
                .append(");");
        db.execSQL(createCacheSql.toString());
    }

    /**
     * destory sns BI 数据存储表
     *
     * @param db
     */
    private void destorySnsBiTable(SQLiteDatabase db) {
        db.execSQL(new StringBuffer().append("DROP TABLE IF EXISTS ")
                .append(SnsBiDbContent.SnsBiTable.TABLE_NAME).append(";")
                .toString());
    }
}
