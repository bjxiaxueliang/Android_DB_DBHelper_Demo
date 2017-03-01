package com.example.scalephoto.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.example.scalephoto.cache.utils.SerializeUtil;
import com.example.scalephoto.cache.db.SnsBiDbContent;
import com.example.scalephoto.cache.db.SnsBiDbHelper;
import com.example.scalephoto.cache.models.SnsBiLogData;

import java.util.ArrayList;
import java.util.List;


public class SnsBiDbManager {

    private static final String TAG = "SnsBiDbManager";


    //---------------单例begin----------------
    private volatile static SnsBiDbManager instance;

    private SnsBiDbManager() {
    }

    public static SnsBiDbManager getInstance() {
        if (instance == null) {
            synchronized (SnsBiDbManager.class) {
                if (instance == null) {
                    instance = new SnsBiDbManager();
                }
            }
        }
        return instance;
    }
    //---------------单例end----------------

    //#########################################SnsBiData的增删改查begin##########################################

    /**
     * 插入单条数据
     *
     * @param data
     * @return
     */
    public synchronized boolean insert(Context context, SnsBiLogData data) {
        return this.insert(context, SnsBiDbContent.SnsBiTable.TABLE_NAME, objectToMap(data));
    }

    /**
     * 插入多条数据
     *
     * @param data
     * @return
     */
    public synchronized boolean batchInsert(Context context, List<SnsBiLogData> data) {

        if (data == null || data.size() == 0) {
            return false;
        }
        ContentValues[] valuesArr = new ContentValues[data.size()];
        //
        for (int i = 0; i < data.size(); i++) {
            ContentValues value = objectToMap(data.get(i));
            valuesArr[i] = value;
        }
        return this.bulkInsert(context, SnsBiDbContent.SnsBiTable.TABLE_NAME, valuesArr) >= 0;
    }

    /**
     * 删除特定数据
     *
     * @return
     */
    public synchronized boolean delete(Context context, String title) {
        if (TextUtils.isEmpty(title)) {
            return false;
        }
        //
        StringBuilder deleteSql = new StringBuilder();
        deleteSql.append(SnsBiDbContent.SnsBiTable.CTIME);
        deleteSql.append(" ='");
        deleteSql.append(title);
        deleteSql.append("'");
        deleteSql.append(";");
        String sql = deleteSql.toString();
        //
        Log.i(TAG, "del sql:" + sql);
        boolean deleteRet = this.delete(context, SnsBiDbContent.SnsBiTable.TABLE_NAME, sql, null) >= 0;
        Log.i(TAG, "del flowers result:" + deleteRet);
        return deleteRet;
    }

    /**
     * 删除所有数据
     *
     * @return
     */
    public synchronized boolean batchDelete(Context context) {
        boolean deleteRet = this.delete(context, SnsBiDbContent.SnsBiTable.TABLE_NAME, null, null) >= 0;
        return deleteRet;
    }

    /**
     * 查询所有的数据
     *
     * @return
     */
    public synchronized List<SnsBiLogData> query(Context context) {
        List<SnsBiLogData> dataList = null;
        //
        Cursor cursor = this.query(context, SnsBiDbContent.SnsBiTable.TABLE_NAME, null, null, null, null);
        //
        try {
            if (cursor != null) {
                //
                dataList = new ArrayList<SnsBiLogData>();
                //
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    int ctime = cursor
                            .getColumnIndex(SnsBiDbContent.SnsBiTable.CTIME);
                    int page = cursor
                            .getColumnIndex(SnsBiDbContent.SnsBiTable.PAGE);
                    int event = cursor
                            .getColumnIndex(SnsBiDbContent.SnsBiTable.EVENT);
                    int ip = cursor
                            .getColumnIndex(SnsBiDbContent.SnsBiTable.IP);
                    int network_type = cursor
                            .getColumnIndex(SnsBiDbContent.SnsBiTable.NETWORK_TYPE);
                    int app_version = cursor
                            .getColumnIndex(SnsBiDbContent.SnsBiTable.APP_VERSION);
                    int operator = cursor
                            .getColumnIndex(SnsBiDbContent.SnsBiTable.OPERATOR);
                    int bi_data = cursor
                            .getColumnIndex(SnsBiDbContent.SnsBiTable.BI_DATA);
                    do {
                        SnsBiLogData data = new SnsBiLogData();
                        //
                        data.ctime = cursor.getString(ctime);
                        data.pos = cursor.getString(page);
                        data.event = cursor.getString(event);
                        data.ip = cursor.getString(ip);
                        data.network = cursor.getString(network_type);
                        data.app_version = cursor.getString(app_version);
                        data.operator = cursor.getString(operator);
                        data.data = SerializeUtil.deserializeObject(cursor.getBlob(bi_data));
                        //
                        dataList.add(data);

                    } while (cursor.moveToNext());
                }
            }
        } catch (RuntimeException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return dataList;

    }

    /**
     * 获取数据条目
     *
     * @return
     */
    public synchronized int getDataCount(Context context) {
        //
        Cursor cursor = this.query(context, SnsBiDbContent.SnsBiTable.TABLE_NAME, null, null, null, null);
        //
        try {
            if (cursor != null) {
                return cursor.getCount();
            }
        } catch (RuntimeException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return 0;
    }

    /**
     * 更新指定数据
     */
    public synchronized boolean update(Context context, SnsBiLogData data) {
        //

        StringBuilder updateSql = new StringBuilder();
        updateSql.append(SnsBiDbContent.SnsBiTable.CTIME);
        updateSql.append(" ='");
        updateSql.append(data.ctime);
        updateSql.append("'");
        updateSql.append(";");
        //
        String sql = updateSql.toString();
        //
        return this.update(context, SnsBiDbContent.SnsBiTable.TABLE_NAME, objectToMap(data), sql, null) > 0;
    }

    /**
     * 数据转为contentValues
     *
     * @param data
     * @return
     */
    private ContentValues objectToMap(SnsBiLogData data) {
        ContentValues value = new ContentValues();
        value.put(SnsBiDbContent.SnsBiTable.CTIME, data.ctime);
        value.put(SnsBiDbContent.SnsBiTable.PAGE, data.pos);
        value.put(SnsBiDbContent.SnsBiTable.EVENT, data.event);
        value.put(SnsBiDbContent.SnsBiTable.IP, data.ip);
        value.put(SnsBiDbContent.SnsBiTable.NETWORK_TYPE, data.network);
        value.put(SnsBiDbContent.SnsBiTable.APP_VERSION, data.app_version);
        value.put(SnsBiDbContent.SnsBiTable.OPERATOR, data.operator);
        value.put(SnsBiDbContent.SnsBiTable.BI_DATA, SerializeUtil.serializeObject(data.data));
        return value;
    }

    //##################################################################SnsBi 数据库的 增删改查 begin####################################################################

    /**
     * 插入一条数据
     *
     * @param table
     * @param values
     * @return
     */
    private boolean insert(Context context, String table, ContentValues values) {
        Log.i(TAG, "Insert data to flash DB. table:" + table);
        if (values == null || TextUtils.isEmpty(table)) {
            return false;
        }
        //
        SQLiteDatabase db = this.getSnsBiDb(context);
        if (db == null) {
            return false;
        }
        try {
            return db.insert(table, null, values) >= 0;
        } catch (Exception e) {
            Log.e(TAG, "Insert data to client fail!", e);
            return true;
        }
    }


    private Cursor query(Context context, String table, String[] projection,
                         String selection, String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "Query data from flash DB. table:" + table);
        if (TextUtils.isEmpty(table)) {
            return null;
        }
        //
        SQLiteDatabase db = this.getSnsBiDb(context);
        if (db == null) {
            return null;
        }
        Cursor queryResult = null;
        try {
            queryResult = db.query(table, projection, selection, selectionArgs,
                    null, null, sortOrder);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Query SQL error:" + e.getMessage(), e);
        } catch (SQLiteException e) {
            Log.e(TAG, "Query database error!", e);
        } catch (SQLException e) {
            Log.e(TAG, "Query fail!", e);
        }
        return queryResult;
    }


    /**
     * Batch insert new data.
     */
    private int bulkInsert(Context context, String table, ContentValues[] values) {
        Log.i(TAG, "Batch insert data to flash DB. table:" + table);
        if (values == null || TextUtils.isEmpty(table)) {
            return -1;
        }
        //
        SQLiteDatabase db = this.getSnsBiDb(context);
        if (db == null) {
            return -1;
        }
        int numValues = values.length;
        try {
            // start transaction
            db.beginTransaction();
            try {
                for (int i = 0; i < numValues; i++) {
                    if (values[i] != null) {
                        db.insert(table, null, values[i]);
                    }
                }
                // Marks the current transaction as successful
                db.setTransactionSuccessful();
            } finally {
                // Commit or rollback by flag of transaction
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.e(TAG, "BulkUpdate fail! table: " + table, e);
            return -1;
        }
        return numValues;
    }

    /**
     * 删除
     *
     * @param table
     * @param selection
     * @param selectionArgs
     * @return
     */
    private int delete(Context context, String table, String selection,
                       String[] selectionArgs) {
        Log.i(TAG, "Delete data on flash DB. table:" + table);
        if (TextUtils.isEmpty(table)) {
            return -1;
        }
        //
        SQLiteDatabase db = this.getSnsBiDb(context);
        if (db == null) {
            return -1;
        }
        try {
            return db.delete(table, selection, selectionArgs);
        } catch (SQLException e) {
            Log.e(this.getClass().toString(), "Delete fail!", e);
            return -1;
        }
    }

    /**
     * 更新
     *
     * @param table
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    private int update(Context context, String table, ContentValues values,
                       String selection, String[] selectionArgs) {
        int result = 0;
        Log.i(TAG, "Update data on flash DB. table:" + table);
        if (TextUtils.isEmpty(table)) {
            return -1;
        }
        //
        SQLiteDatabase db = this.getSnsBiDb(context);
        if (db == null) {
            return -1;
        }
        if (values == null && selection != null) {
            // execute a SQL
            db.execSQL(selection);
            return 1;
        } else if (values == null) {
            return -1;
        } else {
            result = db.update(table, values, selection, selectionArgs);
        }
        return result;
    }

    // db helper
    private SnsBiDbHelper mSnsBiDbHelper;

    /**
     * Get instance of SQLiteDatabase,deal with exception
     *
     * @return
     */
    private SQLiteDatabase getSnsBiDb(Context context) {
        SQLiteDatabase db = null;
        try {
            if (mSnsBiDbHelper == null) {
                mSnsBiDbHelper = new SnsBiDbHelper(context);
            }
            db = mSnsBiDbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Get db fail!", e);
        }
        return db;
    }

}
