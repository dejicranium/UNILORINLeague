package com.deji_cranium.unilorinleague.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.deji_cranium.unilorinleague.R;
import com.deji_cranium.unilorinleague.Utils.HtmlUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.R.attr.id;
import static android.R.attr.trimPathEnd;

/**
 * Created by cranium on 10/23/17.
 */

public class FixturesFragments extends Fragment {
    WebView webView;
    View parentView;
    ProgressBar mProgressBar;
    RelativeLayout mErrorLayout;
    private String mFixturesTable;
    private LoadFixturesTable loadFixturesTableTask;
    private Button mRecRefreshButton;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loadFixturesTableTask.cancel(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFixturesTableTask = new LoadFixturesTable();
        loadFixturesTableTask.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        loadFixturesTableTask.cancel(true);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        loadFixturesTableTask.cancel(true);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh){
            onRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onRefresh() {
        loadFixturesTableTask.cancel(true);
        loadFixturesTableTask = new LoadFixturesTable();
        loadFixturesTableTask.execute();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.table_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tables_fragment, container, false);
        parentView = view;



        mErrorLayout = (RelativeLayout)view.findViewById(R.id.error_layout);
        mRecRefreshButton = (Button)view.findViewById(R.id.rec_refresh_button);

        mRecRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefresh();
                mErrorLayout.setVisibility(View.GONE);
            }
        });

        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        webView = (WebView)view.findViewById(R.id.webView);





        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);



        return view;
    }

    public static FixturesFragments newInstance() {
        return new FixturesFragments();
    }


    public class LoadFixturesTable extends AsyncTask<Void, Void, String>{
        final String UNILORIN_LEAGUE_FIXTURES = "http://unilorinleague.org/league-features-3/";
        Elements tableDiv;
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder htmlContent = new StringBuilder();
            try{
                Document document = Jsoup.connect(UNILORIN_LEAGUE_FIXTURES).get();
                tableDiv = document.select("div.entry-content");




            } catch (IOException e) {
                e.printStackTrace();

                Snackbar.make(parentView, "Oops! Something went wrong while connecting to resource", Snackbar.LENGTH_LONG).show();
                return null;
            }

            if (tableDiv != null){
                return tableDiv.toString();
            }
            else {
                return null;
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mProgressBar.getVisibility() != View.VISIBLE){
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (webView.getVisibility() != View.INVISIBLE){
                webView.setVisibility(View.GONE);
            }
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String table) {
            super.onPostExecute(table);
            if (table != null){
                Document document = Jsoup.parse(table);
                mProgressBar.setVisibility(View.GONE);
                webView.loadData(HtmlUtils.changeTopScorersTable(document.toString()),
                        "text/html; charset=utf-8", null);
                webView.setVisibility(View.VISIBLE);

            }
            else {
                mProgressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(parentView, "Connection Error. Enable active network connection.", Snackbar.LENGTH_LONG).show();
                mErrorLayout.setVisibility(View.VISIBLE);
            }

        }


    }
}
