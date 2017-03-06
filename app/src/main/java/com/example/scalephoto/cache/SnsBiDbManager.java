package com.example.scalephoto.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.example.scalephoto.cache.db.SnsBiDbContent;
import com.example.scalephoto.cache.db.SnsBiDbHelper;
import com.example.scalephoto.cache.models.SnsBiLogData;
import com.example.scalephoto.cache.utils.SerializeUtil;

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

    private SnsBiDbHelper mSnsBiDbHelper = null;

    private SnsBiDbHelper getSnsBiDbHelper(Context context) {
        if (mSnsBiDbHelper == null) {
            mSnsBiDbHelper = new SnsBiDbHelper(context);
        }
        return mSnsBiDbHelper;
    }


    /**
     * 插入单条数据
     *
     * @param data
     * @return
     */
    public synchronized boolean insert(Context context, SnsBiLogData data) {
        SnsBiDbHelper mSnsBiDbHelper = getSnsBiDbHelper(context);
        return mSnsBiDbHelper.insert(SnsBiDbContent.SnsBiTable.TABLE_NAME, objectToMap(data));
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
        //-----------------
        ContentValues[] valuesArr = new ContentValues[data.size()];
        //
        for (int i = 0; i < data.size(); i++) {
            ContentValues value = objectToMap(data.get(i));
            valuesArr[i] = value;
        }
        //-------------
        SnsBiDbHelper mSnsBiDbHelper = getSnsBiDbHelper(context);
        return mSnsBiDbHelper.bulkInsert(SnsBiDbContent.SnsBiTable.TABLE_NAME, valuesArr) >= 0;
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
        //-------------
        SnsBiDbHelper mSnsBiDbHelper = getSnsBiDbHelper(context);
        boolean deleteRet = mSnsBiDbHelper.delete(SnsBiDbContent.SnsBiTable.TABLE_NAME, sql, null) >= 0;
        Log.i(TAG, "del flowers result:" + deleteRet);
        return deleteRet;
    }

    /**
     * 删除所有数据
     *
     * @return
     */
    public synchronized boolean batchDelete(Context context) {
        //-------------
        SnsBiDbHelper mSnsBiDbHelper = getSnsBiDbHelper(context);
        boolean deleteRet = mSnsBiDbHelper.delete(SnsBiDbContent.SnsBiTable.TABLE_NAME, null, null) >= 0;
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
        //-------------
        SnsBiDbHelper mSnsBiDbHelper = getSnsBiDbHelper(context);
        Cursor cursor = mSnsBiDbHelper.query(SnsBiDbContent.SnsBiTable.TABLE_NAME, null, null, null, null);
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
        //-------------
        SnsBiDbHelper mSnsBiDbHelper = getSnsBiDbHelper(context);
        Cursor cursor = mSnsBiDbHelper.query(SnsBiDbContent.SnsBiTable.TABLE_NAME, null, null, null, null);
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
        //-------------
        SnsBiDbHelper mSnsBiDbHelper = getSnsBiDbHelper(context);
        return mSnsBiDbHelper.update(SnsBiDbContent.SnsBiTable.TABLE_NAME, objectToMap(data), sql, null) > 0;
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


}
