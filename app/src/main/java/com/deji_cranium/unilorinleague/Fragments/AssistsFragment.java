package com.deji_cranium.unilorinleague.Fragments;

import android.app.ProgressDialog;
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

/**
 * Created by cranium on 11/4/17.
 */

public class AssistsFragment extends Fragment {
    private WebView mWebView;
    private String mAssistsTable;
    private View parentView;
    private RelativeLayout mErrorLayout;
    private ProgressBar mProgressBar;
    LoadAssistsTable loadAssistsTableTask;
    private Button mRecRefreshButton;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAssistsTableTask = new LoadAssistsTable();
        loadAssistsTableTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh){
            onRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loadAssistsTableTask.cancel(true);
        mProgressBar.invalidate();
    }

    @Override
    public void onStop() {
        super.onStop();
        loadAssistsTableTask.cancel(true);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        loadAssistsTableTask.cancel(true);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.table_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void onRefresh(){
        loadAssistsTableTask.cancel(true);
        loadAssistsTableTask = new LoadAssistsTable();
        loadAssistsTableTask.execute();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAssistsTable = new String();
        setHasOptionsMenu(true);
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
        mWebView = (WebView)view.findViewById(R.id.webView);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        return view;
    }

    public static AssistsFragment newInstance() {
        return new AssistsFragment();
    }

    private class LoadAssistsTable  extends AsyncTask<Void, Void, String>{

        String assistsUrl = "http://www.unilorinleague.org/list/goals-assist/";
        Elements tables;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(assistsUrl).get();
                tables =  doc.getElementsByTag("table");


            } catch (IOException e) {
                Snackbar.make(parentView, "Something went wrong. Try again!", Snackbar.LENGTH_LONG).show();
                return null;
            }
            if (tables != null){
                return tables.toString();
            }
            else {
                return null;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mWebView.getVisibility() != View.INVISIBLE){
                mWebView.setVisibility(View.GONE);
            }
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String table) {
            super.onPostExecute(table);
            if (table != null){
                mAssistsTable = table;
                mWebView.loadData(HtmlUtils.changeTopScorersTable(mAssistsTable), "text/html; charset=utf-8", null);
                mProgressBar.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
            }
            else {
                mProgressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(parentView, "Connection Error. Enable active network connection.", Snackbar.LENGTH_LONG).show();
                mErrorLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
