package com.example.qwerty.http;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userDB";
    private final String DATABASE_TABLE = "users";
    private final String UID = "uid";
    private final String DEFAULT_ACCOUNT = "defaultAccount";
    SQLiteDatabase db;


    public DBHelper(Context context) {
        // Context, database name, optional cursor factory, database version
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE_TABLE +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + UID + " TEXT UNIQUE, " + DEFAULT_ACCOUNT + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public void setData(String uid) {
        moveDefaultFlag(uid, true);
        ContentValues values = new ContentValues();
        values.put(UID, uid);
        values.put(DEFAULT_ACCOUNT, 1);
        db.insert(DATABASE_TABLE, null, values);
    }

    //relocate the "default login account" flag
    private void moveDefaultFlag(String uid, boolean newUser) {
        if(newUser)
            db.execSQL("UPDATE " + DATABASE_TABLE + " SET " +
                DEFAULT_ACCOUNT + " = 0 WHERE " + DEFAULT_ACCOUNT + " = 1");

        //this might actually be pretty retarded
        else
            db.execSQL("UPDATE " + DATABASE_TABLE + " SET " +
                DEFAULT_ACCOUNT + " =  CASE WHEN '" + DEFAULT_ACCOUNT + "' = '" + uid + "' THEN " +
                    DEFAULT_ACCOUNT + " = 1 ELSE " + DEFAULT_ACCOUNT + " = 0 END");
    }

    public void clearDatabase() {
        String clearDBQuery = "DELETE FROM " + DATABASE_TABLE;
        db.execSQL(clearDBQuery);
    }

    public Cursor getUser(String uid) {
        moveDefaultFlag(uid, false);
        return db.rawQuery("SELECT * from " + DATABASE_TABLE + " WHERE " + UID + " LIKE '" + uid + "'",null);
    }

    public Cursor getActiveUser() {
        return db.rawQuery("SELECT " + UID + " from " + DATABASE_TABLE + " WHERE " + DEFAULT_ACCOUNT + " LIKE 1",null);
    }

    public Cursor getAllUsers() {
        return db.rawQuery("SELECT * from '"+DATABASE_TABLE+"'", null);
    }
}
