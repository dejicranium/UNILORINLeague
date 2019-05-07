package com.deji_cranium.unilorinleague.Fragments;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.deji_cranium.unilorinleague.AlarmReceiver;
import com.deji_cranium.unilorinleague.Article;
import com.deji_cranium.unilorinleague.ArticleAdapter;
import com.deji_cranium.unilorinleague.ArticleXmlParser;
import com.deji_cranium.unilorinleague.CLickListener;
import com.deji_cranium.unilorinleague.Database.DBImpl;
import com.deji_cranium.unilorinleague.Database.NewsFeedDBHelper;
import com.deji_cranium.unilorinleague.Home;
import com.deji_cranium.unilorinleague.R;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Deji Atoyebi (Cranium) on 10/20/17.
 */

public class GeneralNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = GeneralNewsFragment.class.getSimpleName();

    private NewsFeedDBHelper db;
    public SQLiteDatabase sqLiteDatabaseForWriting;
    public SQLiteDatabase sqLiteDatabaseForReading;
    private Cursor cursor;

    private Menu menu;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Article> articlesList;
    private List<Article> articleListFromDb;

    private LoadFeedTask task;

    private View parentView;
    private CLickListener listener;
    private ActionBar mActionBar;
    private  boolean isReadHidden = false;

    Typeface neuton;
    private boolean mIsReadHidden;
    private boolean mIsSortByTitle;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_fragment_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MenuItem hideReadItem = menu.findItem(R.id.hide_all_read);
        MenuItem sortItem = menu.findItem(R.id.sort);

        if (id == R.id.refresh) {
            onRefresh();
        }

        if (id == R.id.exit) {
            ((Home) getActivity()).finish();
        }


        if (id == R.id.hide_all_read) {
            if (!mIsReadHidden) {
                mIsReadHidden = true;
                new HideAllReadTask().execute();
                hideReadItem.setTitle("Show Read");
            } else {
                mIsReadHidden = false;
                if (articleListFromDb.size() > 0) {
                    //it means if news from Activity was loaded from database.
                    //
                    // True after the first time of using the app.

                    adapter = new ArticleAdapter(getContext(), articleListFromDb, new CLickListener() {
                        @Override
                        public void onPositionClicked(int postion) {

                        }
                    });

                    hideReadItem.setTitle("Hide Read");
                    recyclerView.setAdapter(adapter);
                }
                else{
                    adapter = new ArticleAdapter(getContext(), articlesList, new CLickListener() {
                        @Override
                        public void onPositionClicked(int postion) {

                        }
                    });
                    mIsReadHidden = false;
                    hideReadItem.setTitle("Hide Read");
                    recyclerView.setAdapter(adapter);
                }
            }

        }

        if (id == R.id.sort){
            if (mIsSortByTitle == false) {
                mIsSortByTitle = true;
                new SortArticlesTask().execute();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        neuton = Typeface.createFromAsset(getActivity().getAssets(), "Neuton-Bold.ttf");
        mActionBar = ((Home) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = new NewsFeedDBHelper(getContext());
        sqLiteDatabaseForWriting = db.getWritableDatabase();
        sqLiteDatabaseForReading = db.getReadableDatabase();
        mActionBar.setTitle("League News");
        mIsReadHidden = false;
        mIsSortByTitle = false;
    }

    public static GeneralNewsFragment newInstance() {
        return new GeneralNewsFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        parentView = view;
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.root_recycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);


        /**
         * Checks if database is empty.
         * If it is, call the async task to fetch new data for the first time and insert records
         * into the database
         */
        articleListFromDb = new ArrayList<>();
        cursor = sqLiteDatabaseForReading.rawQuery("SELECT count(*) from unilorinleaguenews", null, null);
        cursor.moveToFirst();
        int cursorCount = cursor.getInt(0);
        cursor.close();

        if (cursorCount < 1) {
            /**
             * if the above condition is true, it means it's the user's first time of
             * using the app.
             *
             * thus, we need to execute the async task, after which we'd store in a sharedpreferences
             * instance the current number of articles, so that we can know when subsequent
             * async tasks arrive with new articles.
             *
             **/
            task = new LoadFeedTask();
            task.execute();


        }
        /**
         * On the other note, if the database is not empty, populate a list by getting article attributes
         * from the database and serving the list to the adapter.
         */
        else {
            new LoadFeedFromDBTask().execute();
        }

            /**try {
                cursor = sqLiteDatabaseForReading.rawQuery("SELECT * from unilorinleaguenews", null, null);

                while (cursor.moveToNext()) {
                    String articleTitle = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.NEWS_TITLE));
                    String articleDescription = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.DESCRIPTION));
                    String articleLink = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.NEWS_LINK));
                    String articleAuthor = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.AUTHOR));
                    String articleRead = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.READ));

                    //doing these because I need to know the article type because it's needed in the detailsactivity
                    //to decide which db to input data in
                    String articleType = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.TYPE));
                    //we need to ensure that our article adapter knows that the article is already read.
                    String articlePubDate = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.PUB_DATE));

                    articleListFromDb.add(new Article(articleTitle, articleLink, articleDescription, articleAuthor, articlePubDate, articleType, articleRead));


                }
                adapter = new ArticleAdapter(getContext(), articleListFromDb, new CLickListener() {
                    @Override
                    public void onPositionClicked(int postion) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();


            }


            recyclerView.setAdapter(adapter);
            cursor.close();
            swipeRefreshLayout.setRefreshing(false);

             **/


        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
            swipeRefreshLayout.setRefreshing(false);
        }
        db.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
            swipeRefreshLayout.setRefreshing(false);

        }

        //db.close();
        //sqLiteDatabaseForReading.close();
        //sqLiteDatabaseForWriting.close();

    }

    @Override
    public void onRefresh() {
        task = new LoadFeedTask();
        task.execute();
    }

    private class LoadFeedTask extends AsyncTask<Void, Void, List<Article>> {
        Cursor c = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<Article> doInBackground(Void... voids) {
            try {
                URL url = new URL("http://unilorinleague.org/feed/");
                ArticleXmlParser pullParser = new ArticleXmlParser();
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(15000);
                urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                pullParser.parse(urlConnection.getInputStream());
                articlesList = pullParser.getLeagueNews();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Snackbar.make(parentView, "Connection timeout. Enable an active internet Connection", Snackbar.LENGTH_LONG).show();
                return null;
            } catch (XmlPullParserException e) {
                e.printStackTrace();

            }
            return articlesList;

        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(List<Article> articles) {
            if (articles != null) {
                adapter = new ArticleAdapter(getActivity(), articles, new CLickListener() {
                    @Override
                    public void onPositionClicked(int postion) {

                    }
                });


                /**
                 * There are three things that need to be determined to proceed.
                 *
                 * @First: we must make suree that there's no data in the database already.
                 * Then, we add every article gotten by the AsyncTask
                 *
                 * @Second: we need to know whether the task arrived with new data from xml feed
                 * If that is the case, we'll add the new data to the database and make it adapt to
                 * the recyclerview
                 *
                 * @Third: we need to know whether the task arrived with no data.
                 * We know this by comparingg the size of the list of articles to the size already
                 * stored in the shared preferences.


                 */
                //db = new NewsFeedDBHelper(getContext());
                //sqLiteDatabaseForReading = db.getReadableDatabase();
                //sqLiteDatabaseForWriting = db.getWritableDatabase();
                cursor = sqLiteDatabaseForReading.rawQuery("SELECT count(*) from unilorinleaguenews", null, null);
                cursor.moveToFirst();
                int cursorCount = cursor.getInt(0);
                cursor.close();


                //@First:
                if (cursorCount < 1) {
                    Log.d(TAG, "I am adding articles for the first time");
                    try {
                        ContentValues values = new ContentValues();
                        for (Article article : articles) {
                            values.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                            values.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                            values.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                            values.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                            values.put(NewsFeedDBHelper.TYPE, article.getType());
                            values.put(NewsFeedDBHelper.READ, article.getRead());
                            values.put(NewsFeedDBHelper.PUB_DATE, article.getPubDate());

                            long newRowId = sqLiteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, null, values);
                        }
                        values.clear();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.setAdapter(adapter);
                }


                /**
                 * The condition means that:
                 *  if the first article doesn't exist in the database;
                 *
                 * if the first article isn't in the database, it means a new article or the new articles has/have been added to the site
                 */
                else if (!articleExists(articles.get(0))) {
                    Log.d(TAG, "article doesn't exist");

                    /**
                     * If the first article doesn;t exist, we'll start by adding it to our database.
                     *
                     * The loop will break only if an article exists. Breaking the loop at this point is safe enough
                     * because an XML feed is arranged chronologically and when an
                     */
                    try {
                        ContentValues vals = new ContentValues();
                        //should be tested

                        for (Article article : articles) {
                            if (!articleExists(article)) {
                                vals.put(NewsFeedDBHelper.NEWS_TITLE, article.getTitle());
                                vals.put(NewsFeedDBHelper.NEWS_LINK, article.getLink());
                                vals.put(NewsFeedDBHelper.DESCRIPTION, article.getDescription());
                                vals.put(NewsFeedDBHelper.AUTHOR, article.getAuthor());
                                vals.put(NewsFeedDBHelper.TYPE, article.getType());
                                vals.put(NewsFeedDBHelper.READ, article.getType());
                                vals.put(NewsFeedDBHelper.PUB_DATE, article.getPubDate());

                                long newRowId = sqLiteDatabaseForWriting.insert(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, null, vals);
                            }
                            //else stop the loop;
                            else {
                                break;
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.setAdapter(adapter);

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "No new articles", Toast.LENGTH_LONG).show();

                }


            } else {

                swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(parentView, "Connect to the internet to get updates.", Snackbar.LENGTH_LONG).show();
            }


        }


    }


    private boolean articleExists(Article article) {
        String[] projection = {"*"};
        String selection = NewsFeedDBHelper.NEWS_TITLE + " = ?";
        String[] selectionArgs = {article.getTitle()};
        Cursor c = sqLiteDatabaseForReading.query(NewsFeedDBHelper.TABLE_UNILORIN_LEAGUE_NEWS, projection, selection, selectionArgs, null, null, null);
        boolean articleExists = c.getCount() > 0; // article exists if the cursor count is more than 0 (meant to be 1);
        c.close();
        return articleExists;
    }




    private class HideAllReadTask extends AsyncTask<Void, Void, ArticleAdapter>{

        @Override
        protected ArticleAdapter doInBackground(Void... voids) {
            ArticleAdapter lAdapter = getUnreadArticleAdapter();

            return lAdapter;
        }

        @Override
        protected void onPostExecute(ArticleAdapter articleAdapter) {
            super.onPostExecute(articleAdapter);
            adapter = articleAdapter;
            recyclerView.setAdapter(adapter);
        }
    }




    private ArticleAdapter getUnreadArticleAdapter() {
        List<Article> unreadArticles = new ArrayList<>();
        ArticleAdapter lAdapter;
        Cursor cursor = sqLiteDatabaseForReading.rawQuery("Select * from unilorinleaguenews WHERE read = 'none'", null);
        while (cursor.moveToNext()) {
            String articleTitle = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.NEWS_TITLE));
            String articleDescription = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.DESCRIPTION));
            String articleLink = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.NEWS_LINK));
            String articleAuthor = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.AUTHOR));
            String articleRead = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.READ));
            String articlePubDate = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.PUB_DATE));
            //doing these because I need to know the article type because it's needed in the detailsactivity
            //to decide which db to input data in
            String articleType = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.TYPE));
            //we need to ensure that our article adapter knows that the article is already read.

            unreadArticles.add(new Article(articleTitle, articleLink, articleDescription, articleAuthor, articlePubDate, articleType, articleRead));


        }
        cursor.close();
        lAdapter = new ArticleAdapter(getContext(), unreadArticles, new CLickListener() {
            @Override
            public void onPositionClicked(int postion) {

            }
        });

        return lAdapter;

    }




    private class SortArticlesTask extends AsyncTask<Void, Void, ArticleAdapter>{

        @Override
        protected ArticleAdapter doInBackground(Void... voids) {
            ArticleAdapter lAdapter = sortedArticleAdapter();

            return lAdapter;
        }

        @Override
        protected void onPostExecute(ArticleAdapter articleAdapter) {
            super.onPostExecute(articleAdapter);
            adapter = articleAdapter;
            recyclerView.setAdapter(adapter);
        }
    }






    public ArticleAdapter sortedArticleAdapter(){
        List<Article>lArticles = new ArrayList<>();

         if (articlesList != null && articlesList.size() > 0){
            lArticles = articlesList;
            Collections.sort(lArticles, new Comparator<Article>() {
                @Override
                public int compare(Article article, Article t1) {
                    return article.getTitle().compareTo(t1.getTitle());
                }
            });
        }
        else if (articleListFromDb != null && articleListFromDb.size() > 0){
            lArticles = articleListFromDb;
            Collections.sort(lArticles, new Comparator<Article>() {
                @Override
                public int compare(Article article, Article t1) {
                    return article.getTitle().compareTo(t1.getTitle());
                }
            });
        }


        return new ArticleAdapter(getContext(), lArticles, new CLickListener() {
            @Override
            public void onPositionClicked(int postion) {

            }
        });
    }


        private class LoadFeedFromDBTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                swipeRefreshLayout.setRefreshing(false);
                adapter = new ArticleAdapter(getContext(), articleListFromDb, new CLickListener() {
                    @Override
                    public void onPositionClicked(int postion) {

                    }
                });

                recyclerView.setAdapter(adapter);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            protected Void doInBackground(Void... voids) {
                cursor = sqLiteDatabaseForReading.rawQuery("SELECT * from unilorinleaguenews", null, null);

                while (cursor.moveToNext()) {
                    String articleTitle = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.NEWS_TITLE));
                    String articleDescription = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.DESCRIPTION));
                    String articleLink = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.NEWS_LINK));
                    String articleAuthor = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.AUTHOR));
                    String articleRead = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.READ));

                    //doing these because I need to know the article type because it's needed in the detailsactivity
                    //to decide which db to input data in
                    String articleType = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.TYPE));
                    //we need to ensure that our article adapter knows that the article is already read.
                    String articlePubDate = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.PUB_DATE));

                    articleListFromDb.add(new Article(articleTitle, articleLink, articleDescription, articleAuthor, articlePubDate, articleType, articleRead));


                }
                cursor.close();
                return null;
            }


        }
}
