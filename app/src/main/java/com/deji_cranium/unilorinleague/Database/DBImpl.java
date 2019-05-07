package com.deji_cranium.unilorinleague.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;

/**
 * Created by cranium on 12/1/17.
 */

public class DBImpl {
    private static Context mContext;
    private static NewsFeedDBHelper mDbHelper;
    private static SQLiteDatabase mSqliteDatabaseForReading = mDbHelper.getReadableDatabase();
    private static SQLiteDatabase mSqliteDatabaseForWriting = mDbHelper.getWritableDatabase();

    public static SQLiteDatabase getReadableDatabaseInstance(Context context){
        mContext = context;
        mDbHelper = new NewsFeedDBHelper(mContext);
        return mSqliteDatabaseForReading;
    }

    public static SQLiteDatabase getWritableDatabaseInstance(Context context){
        mContext = context;
        mDbHelper = new NewsFeedDBHelper(mContext);
        return mSqliteDatabaseForWriting;
    }
}
