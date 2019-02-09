package com.kuzasystems.zoner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by victor on 29-Sep-18.
 */

public class Config {
    private String dbName = "zoner";
    private int dbVersion = 2;
    public String getDbName(){
        return this.dbName;
    }
    public int getDbVersion(){
        return this.dbVersion;
    }
    public static String url = "https://zoner.iswitchafricagroup.com/";
    //public static String url = "http://192.168.10.39/";
// AddUser.php
public static int getMyUserId(Context context){
    int userId= 0;
    try {
        SQLiteDatabase sqlDb = new ZonerDB(context).getWritableDatabase();
        Cursor cursor = sqlDb.rawQuery("select * from users", null);
        while (cursor.moveToNext()) {
            userId = cursor.getInt(cursor.getColumnIndex("id")) ;
            //  password = cursor.getString(cursor.getColumnIndex("password")) ;

        }
        cursor.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return userId;
}
}
