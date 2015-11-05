package com.example.qwerty.http;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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
        db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + UID + " TEXT, " + DEFAULT_ACCOUNT + " BOOLEAN);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public void setData(String uid, boolean defaultAccount) {
        ContentValues values = new ContentValues();
        values.put(UID, uid);
        values.put(DEFAULT_ACCOUNT, defaultAccount);
        db.insert(DATABASE_TABLE, null, values);
    }
    public Cursor getUser(String uid) {
        return db.rawQuery("SELECT * from " + DATABASE_TABLE + " WHERE " + UID + " = '" + uid + "'",null);
    }
    public Cursor getAllUsers() {
        return db.rawQuery("SELECT * from '"+DATABASE_TABLE+"'", null);
    }
}
