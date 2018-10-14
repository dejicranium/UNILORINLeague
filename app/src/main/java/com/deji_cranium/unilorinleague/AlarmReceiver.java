package com.deji_cranium.unilorinleague;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.deji_cranium.unilorinleague.Database.NewsFeedDBHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cranium on 11/11/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private NewsFeedDBHelper mDb;
    private SQLiteDatabase mSqliteDatabaseForWriting, mSqliteDatabaseForReading;
    private List<Article>mUnilorinLeagueNews, mOtherSportNews;
    private List<Article>mListOfNewArticles;
    private int mNewArticles;

    private boolean articleExists(String tableName, Article article){
        String[] projection = {"*"};
        String selection = NewsFeedDBHelper.NEWS_TITLE + " = ?";
        String[] selectionArgs = {article.getTitle()};
        Cursor c = mSqliteDatabaseForReading.query(tableName, projection, selection, selectionArgs, null, null, null);
        boolean articleExists = c.getCount() >0; // article exists if the cursor count is more than 0 (meant to be 1);
        c.close();
        return articleExists;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        mDb = new NewsFeedDBHelper(context);
        mSqliteDatabaseForReading = mDb.getReadableDatabase();
        mSqliteDatabaseForWriting = mDb.getWritableDatabase();
        mListOfNewArticles = new ArrayList<>();

        new GetLeagueNews().execute();
        new GetOtherSportNews().execute();

        if (mListOfNewArticles != null) {
            Intent in = new Intent(context, Home.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Notification notification = new NotificationCompat.Builder(context).
                    setContentTitle("New Notification")
                    .setContentText(mListOfNewArticles.size() + " new article(s)")
                    .setSmallIcon(R.mipmap.ic_unilorin_logo)
                    .setContentIntent(pendingIntent)
                    .build();


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, notification);

        }
    }


        //search for latest unilorinleaguenews;





    private class GetLeagueNews extends AsyncTask<Void, Void, List<Article>> {

        @Override
        protected List<Article> doInBackground(Void... voids) {

            //since the intent starts only when the unilorinleaguenewstable is not empty,
            //we don't need to check for the codition.
            ArticleXmlParser pullParser = new ArticleXmlParser();
            URL url = null;
            try {
                url = new URL("http://leadership.ng/feed");
            } catch (MalformedURLException e) {
                e.printStackTrace();

                return null;
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            try {
                pullParser.parse(urlConnection.getInputStream());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }
            mUnilorinLeagueNews = pullParser.articlesList;
            return mUnilorinLeagueNews;
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);
            if (articles != null && !articleExists(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, articles.get(0))) {
                for (Article article : articles) {
                    ContentValues vals = new ContentValues();
                    if (!articleExists(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, article)) {
                        vals.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                        vals.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                        vals.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                        vals.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                        vals.put(NewsFeedDBHelper.TYPE, article.getType());
                        vals.put(NewsFeedDBHelper.READ, article.getType());
                        vals.put(NewsFeedDBHelper.PUB_DATE, article.getPubDate());


                        long newRowId = mSqliteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, null, vals);
                        //increment new UnilorinLeagueNewsBy 1;
                        mListOfNewArticles.add(article);
                    }
                    //else stop the loop;
                    else {
                        break;
                    }


                }
            }
        }
    }



    private class GetOtherSportNews extends AsyncTask<Void, Void, List<Article>>{
        @Override
        protected List<Article> doInBackground(Void... voids) {
            ArticleXmlParser pullParser = new ArticleXmlParser();
            URL url = null;
            try {
                url = new URL("http://leadership.ng/feed");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            try {
                pullParser.parse(urlConnection.getInputStream());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            mOtherSportNews = pullParser.articlesList;

            return mOtherSportNews;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);
            if (articles != null){
                Cursor cursor;
                cursor = mSqliteDatabaseForReading.rawQuery("SELECT count(*) from othersportsnews", null, null);
                cursor.moveToFirst();
                int cursorCount = cursor.getInt(0);
                cursor.close();

                if(cursorCount < 1){
                    ContentValues values = new ContentValues();
                    for (Article article : articles) {
                        values.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                        values.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                        values.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                        values.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                        values.put(NewsFeedDBHelper.TYPE, article.getType());
                        values.put(NewsFeedDBHelper.READ, article.getRead());
                        values.put(NewsFeedDBHelper.PUB_DATE, article.getPubDate());


                        long newRowId = mSqliteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, null, values);
                        mListOfNewArticles.add(article);
                    }
                    values.clear();

                }
                else if(!articleExists(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, articles.get(0))) {
                    for (Article article : articles) {
                        ContentValues vals = new ContentValues();
                        if (!articleExists(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, article)) {
                            vals.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                            vals.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                            vals.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                            vals.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                            vals.put(NewsFeedDBHelper.TYPE, article.getType());
                            vals.put(NewsFeedDBHelper.READ, article.getType());
                            vals.put(NewsFeedDBHelper.PUB_DATE, article.getPubDate());

                            long newRowId = mSqliteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, null, vals);
                            //increment new UnilorinLeagueNewsBy 1;
                            mListOfNewArticles.add(article);
                        }
                        //else stop the loop;
                        else {
                            break;
                        }


                    }

                }
    }
    }






   /** @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getOtherSportsNews() {
        List<Article>articles = new ArrayList<>();
        int newArticles = 0;
        try {
            ArticleXmlParser pullParser = new ArticleXmlParser();
            URL url = new URL("http://leadership.ng/feed");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            pullParser.parse(urlConnection.getInputStream());
            mOtherSportNews = pullParser.getOtherSportsNews();



            //check if article exists;
            if (mOtherSportNews != null){
                Cursor cursor;
                cursor = mSqliteDatabaseForReading.rawQuery("SELECT count(*) from othersportsnews", null, null);
                cursor.moveToFirst();
                int cursorCount = cursor.getInt(0);
                cursor.close();

                if(cursorCount < 1){
                    ContentValues values = new ContentValues();
                    for (Article article : mOtherSportNews) {
                        values.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                        values.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                        values.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                        values.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                        values.put(NewsFeedDBHelper.TYPE, article.getType());
                        values.put(NewsFeedDBHelper.READ, article.getRead());


                        long newRowId = mSqliteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, null, values);
                        mListOfNewArticles.add(article);
                    }
                    values.clear();

                }
                else if(!articleExists(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, mOtherSportNews.get(0))) {
                    for (Article article : mOtherSportNews) {
                        ContentValues vals = new ContentValues();
                        if (!articleExists(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, article)) {
                            vals.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                            vals.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                            vals.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                            vals.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                            vals.put(NewsFeedDBHelper.TYPE, article.getType());
                            vals.put(NewsFeedDBHelper.READ, article.getType());

                            long newRowId = mSqliteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_OTHER_SPORT_NEWS, null, vals);
                            //increment new UnilorinLeagueNewsBy 1;
                            mListOfNewArticles.add(article);
                        }
                        //else stop the loop;
                        else {
                            break;
                        }


                    }

                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }








    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getUnilorinLeagueNews(){
        List<Article>articles = new ArrayList<>();
        int newArticles =0;
        try {
            ArticleXmlParser pullParser = new ArticleXmlParser();
            URL url = new URL("http://leadership.ng/feed" +
                    "");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            pullParser.parse(urlConnection.getInputStream());
            mUnilorinLeagueNews = pullParser.getLeagueNews();



            //check if article exists;
            if (mUnilorinLeagueNews != null){
                Cursor cursor;
                cursor = mSqliteDatabaseForReading.rawQuery("SELECT count(*) from unilorinleaguenews", null, null);
                cursor.moveToFirst();
                int cursorCount = cursor.getInt(0);
                cursor.close();

                if(cursorCount < 1){
                    ContentValues values = new ContentValues();
                    for (Article article : mUnilorinLeagueNews) {
                        values.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                        values.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                        values.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                        values.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                        values.put(NewsFeedDBHelper.TYPE, article.getType());
                        values.put(NewsFeedDBHelper.READ, article.getRead());


                        long newRowId = mSqliteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, null, values);
                        mListOfNewArticles.add(article);
                    }
                    values.clear();

                }
                else if(!articleExists(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, mUnilorinLeagueNews.get(0))) {
                    for (Article article : mUnilorinLeagueNews) {
                        ContentValues vals = new ContentValues();
                        if (!articleExists(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, article)) {
                            vals.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                            vals.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                            vals.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                            vals.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                            vals.put(NewsFeedDBHelper.TYPE, article.getType());
                            vals.put(NewsFeedDBHelper.READ, article.getType());

                            long newRowId = mSqliteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, null, vals);
                            //increment new UnilorinLeagueNewsBy 1;
                            mListOfNewArticles.add(article);
                        }
                        //else stop the loop;
                        else {
                            break;
                        }


                    }

                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }





**/

    }
}
