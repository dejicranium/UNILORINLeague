package com.deji_cranium.unilorinleague.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.deji_cranium.unilorinleague.Article;
import com.deji_cranium.unilorinleague.ArticleAdapter;
import com.deji_cranium.unilorinleague.CLickListener;
import com.deji_cranium.unilorinleague.Database.NewsFeedDBHelper;
import com.deji_cranium.unilorinleague.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cranium on 11/6/17.
 */

public class BookmarksFragment extends Fragment {
    private NewsFeedDBHelper mDb;
    private SQLiteDatabase mSqliteDabaseForReading;
    private ArticleAdapter mAdapater;
    private RecyclerView recyclerView;
    private SortArticlesTask sortArticlesTask;
    List<Article>bookmarks;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = new NewsFeedDBHelper(getContext());
        mSqliteDabaseForReading = mDb.getReadableDatabase();
        bookmarks = new ArrayList<>();
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookmarks_layout, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.root_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new LoadBookmarkTask().execute();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bookmark_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.sort_by_title){
            if (sortArticlesTask != null){
                sortArticlesTask.cancel(true);

                sortArticlesTask = new SortArticlesTask();
                sortArticlesTask.execute();
            }
            else {
                sortArticlesTask = new SortArticlesTask();
                sortArticlesTask.execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static Fragment newInstance() {
        return new BookmarksFragment();
    }


    private class LoadBookmarkTask extends AsyncTask<Object, Object, List<Article>> {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected List<Article> doInBackground(Object... voids) {
            Cursor cursor = mSqliteDabaseForReading.rawQuery("Select * from bookmarks", null, null);
            while (cursor.moveToNext()){
                String articleTitle = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.NEWS_TITLE));
                String articleDescription = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.DESCRIPTION));
                String articleLink = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.NEWS_LINK));
                String articleAuthor = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.AUTHOR));
                String articleRead = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.READ));
                String articleType = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.TYPE));
                String articlePubDate = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.PUB_DATE));
                bookmarks.add(new Article(articleTitle, articleLink, articleDescription,articleAuthor, articlePubDate, articleType, articleRead));

            }
            cursor.close();

            return bookmarks;
        }

        @Override
        protected void onPostExecute(List<Article> bookmarks) {
            super.onPostExecute(bookmarks);

            if (!bookmarks.isEmpty()){
                mAdapater = new ArticleAdapter(getContext(), bookmarks, new CLickListener() {
                    @Override
                    public void onPositionClicked(int postion) {

                    }
                });
                recyclerView.setAdapter(mAdapater);
            }
            else {

                //needs to be changed;
                Toast.makeText(getContext(), "There are presently no bookmarks available", Toast.LENGTH_SHORT).show();
            }
        }
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
            mAdapater = articleAdapter;
            recyclerView.setAdapter(mAdapater);
        }
    }




    public ArticleAdapter sortedArticleAdapter(){
        List<Article>lArticles = new ArrayList<>();

        if (bookmarks != null && bookmarks.size() > 0){
            lArticles = bookmarks;
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





}


