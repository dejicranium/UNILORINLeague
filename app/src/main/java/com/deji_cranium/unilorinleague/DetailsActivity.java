package com.deji_cranium.unilorinleague;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.deji_cranium.unilorinleague.Database.NewsFeedDBHelper;
import com.deji_cranium.unilorinleague.Fragments.DetailsFragment;
public class DetailsActivity extends AppCompatActivity {
    Toolbar toolbar;

    private String mArticleTitle;
    private String mArticleDescription;
    private String mAuthor;
    public static NewsFeedDBHelper mDb;
    public static SQLiteDatabase mSqLiteDatabaseForWriting, mSqliteDatabaseForReading;

    private AddBookMarkTask task;
    private Article article;
    private String mRead;
    private String mType;
    private String mArticleLink;
    private String mArticlePubDate;


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        mDb = new NewsFeedDBHelper(getBaseContext());
        mSqliteDatabaseForReading = mDb.getReadableDatabase();
        mSqLiteDatabaseForWriting = mDb.getWritableDatabase();
        


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("News");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getIntent().getExtras();
        if (bundle.getParcelable("article") != null){
            article = bundle.getParcelable("article");
            mArticleTitle = article.getTitle();
            mArticleDescription = article.getDescription();
            mAuthor = article.getAuthor();
            mArticleLink = article.getLink();
            mArticlePubDate = article.getPubDate();
        }

        task = new AddBookMarkTask();
        new MarkAsReadTask().execute();

/**
        Bundle bundle = getIntent().getExtras();
        if (bundle.getParcelable("article") != null) {


            article = bundle.getParcelable("article");

            //the first thing I'm going to do is to mark the article as read

            mArticleTitle = article.getTitle();
            mArticleDescription = article.getDescription();
            mArticleLink = article.getLink();
            mRead = article.getRead();
            mAuthor = article.getAuthor();
            mType = article.getType();
            mRead = article.getRead();

        }
 **/


        getSupportFragmentManager().beginTransaction().add(R.id.root, DetailsFragment.newInstance()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        if (id == R.id.bookmark) {
            task = new AddBookMarkTask();
            task.execute();


        }


         return super.onOptionsItemSelected(item);
    }


    private class AddBookMarkTask extends AsyncTask<Void, Void, Void> {
        String status;

        @Override
        protected Void doInBackground(Void... voids) {


            if (!articleExists(NewsFeedDBHelper.TABLE_BOOKMARKS, article)) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(NewsFeedDBHelper.NEWS_TITLE, mArticleTitle);
                contentValues.put(NewsFeedDBHelper.DESCRIPTION, mArticleDescription);
                contentValues.put(NewsFeedDBHelper.AUTHOR, mAuthor);
                contentValues.put(NewsFeedDBHelper.NEWS_LINK, mArticleLink);
                contentValues.put(NewsFeedDBHelper.PUB_DATE, mArticlePubDate);
                contentValues.put(NewsFeedDBHelper.TYPE, "bookmark");


                long newRowId = mSqLiteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_BOOKMARKS, null, contentValues);
                status = "success";
            } else {
                status = "failure";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (status.equals("success")) {
                Toast.makeText(getBaseContext(), "Added to bookmarks!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Already in bookmarks.", Toast.LENGTH_SHORT).show();
            }
        }


        private boolean articleExists(String tableName, Article article) {
            String[] projection = {"*"};
            String selection = NewsFeedDBHelper.NEWS_TITLE + " = ?";
            String[] selectionArgs = {article.getTitle()};
            Cursor c = mSqliteDatabaseForReading.query(tableName

                    , projection, selection, selectionArgs, null, null, null);
            boolean articleExists = c.getCount() > 0; // article exists if the cursor count is more than 0 (meant to be 1);
            c.close();
            return articleExists;
        }
    }

    private class MarkAsReadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
                    if (articleExists(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, article)) {
                        ContentValues values = new ContentValues();
                        String selection = NewsFeedDBHelper.NEWS_TITLE + " LIKE ?";
                        String[] selectionArgs = {String.valueOf(mArticleTitle)};

                        try {
                            values.put(NewsFeedDBHelper.READ, "true");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        long newRowId = mSqLiteDatabaseForWriting.update(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, values, selection, selectionArgs);
                        values.clear();
                    }
                    else{
                        ContentValues values = new ContentValues();
                        String selection = NewsFeedDBHelper.NEWS_TITLE + " LIKE ?";
                        String[] selectionArgs = {article.getTitle()};

                        try {
                            values.put(NewsFeedDBHelper.READ, "true");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        long newRowId = mSqLiteDatabaseForWriting.update(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, values, selection, selectionArgs);

                        values.clear();
                    }
            return null;
        }
    }

    private boolean articleExists(String tableName, Article article) {
        String[] projection = {"*"};
        String selection = NewsFeedDBHelper.NEWS_TITLE + " = ?";
        String[] selectionArgs = {article.getTitle()};
        Cursor c = mSqliteDatabaseForReading.query(tableName

                , projection, selection, selectionArgs, null, null, null);
        boolean articleExists = c.getCount() > 0; // article exists if the cursor count is more than 0 (meant to be 1);
        c.close();
        return articleExists;
    }
    }


