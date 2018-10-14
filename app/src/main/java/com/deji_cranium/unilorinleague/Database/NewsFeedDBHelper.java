package com.deji_cranium.unilorinleague.Database;

import android.content.Context;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cranium on 10/23/17.
 */

public class NewsFeedDBHelper extends SQLiteOpenHelper {
    private static int SCHEMA_VERSION = 1;

    public static final String ID = "ID";
    public static final String DATABASE_NAME = "NewsFeeds";

    public static final String TABLE_UNILORIN_LEAGUE_NEWS = "unilorinleaguenews";
    public static final String TABLE_OTHER_SPORT_NEWS = "othersportsnews";
    public static final String TABLE_BOOKMARKS = "bookmarks";

    public static final String NEWS_TITLE = "title";
    public static final String NEWS_LINK = "link";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY = "category";
    public static final String TYPE = "type";
    public static final String READ = "read";
    public static final String PUB_DATE = "pubDate";
    public static final String AUTHOR = "author";


    private static final String CREATE_TABLE_UNILORIN_LEAGUE_NEWS = "CREATE TABLE IF NOT EXISTS " + TABLE_UNILORIN_LEAGUE_NEWS + "("+
            ID + " INTEGER PRIMARY KEY," +
            NEWS_TITLE + " TEXT,"+
            NEWS_LINK + " TEXT,"+
            DESCRIPTION + " TEXT,"+
            PUB_DATE + " TEXT,"+
            TYPE + " TEXT,"+
            READ + " TEXT,"+
            AUTHOR + " TEXT)";


    private static final String CREATE_TABLE_OTHER_SPORT_NEWS = "CREATE TABLE IF NOT EXISTS " + TABLE_OTHER_SPORT_NEWS + "("+
            ID + " INTEGER PRIMARY KEY," +
            NEWS_TITLE + " TEXT,"+
            TYPE + " TEXT,"+
            PUB_DATE + " TEXT,"+

            READ + " TEXT,"+
            NEWS_LINK + " TEXT,"+
            DESCRIPTION + " TEXT,"+
            AUTHOR + " TEXT)";



    private static final String CREATE_BOOKMARKS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKMARKS + "("+
            ID + " INTEGER PRIMARY KEY," +
            NEWS_TITLE + " TEXT,"+
            PUB_DATE + " TEXT,"+
            NEWS_LINK + " TEXT,"+
            READ + " TEXT,"+
            TYPE + " TEXT,"+
            DESCRIPTION + " TEXT,"+
            AUTHOR + " TEXT)";


    public NewsFeedDBHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_UNILORIN_LEAGUE_NEWS);
        sqLiteDatabase.execSQL(CREATE_TABLE_OTHER_SPORT_NEWS);
        sqLiteDatabase.execSQL(CREATE_BOOKMARKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
