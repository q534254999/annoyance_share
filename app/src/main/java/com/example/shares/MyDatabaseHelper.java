package com.example.shares;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Wenpzhou on 2017/4/15 0015.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static  final String CREATE_USER = "create table User (" +
            "id integer primary key autoincrement, " +
            "username text, " +
            "password text)";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
