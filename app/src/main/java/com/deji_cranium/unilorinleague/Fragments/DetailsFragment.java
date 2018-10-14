package com.deji_cranium.unilorinleague.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.deji_cranium.unilorinleague.Article;
import com.deji_cranium.unilorinleague.Database.NewsFeedDBHelper;
import com.deji_cranium.unilorinleague.R;
import com.deji_cranium.unilorinleague.Utils.ArticleUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;

import java.text.ParseException;

/**
 * Created by cranium on 10/23/17.
 */

public class DetailsFragment extends Fragment {

    Bundle bundle;
    private TextView mArticleTitleView, mArticleDescription, mDateView, mAuthorView;
    String mArticleLink;
    private Article article;
    private String mArticleTitle;
    private NewsFeedDBHelper db;
    private SQLiteDatabase dbWrite, dbRead;

    private WebView mWebView;
    private Typeface mNeuton;
    private Button mShareBtn;
    private Button mVisitWebsiteBtn;


    public static DetailsFragment newInstance(){
        return new DetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mNeuton = Typeface.createFromAsset(getActivity().getAssets(), "Neuton-Bold.ttf");
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.details_fragment, container, false);
        mArticleTitleView = (TextView)view.findViewById(R.id.detailed_title);
        mAuthorView = (TextView)view.findViewById(R.id.author);

        mWebView = (WebView)view.findViewById(R.id.webView);
        mShareBtn = (Button)view.findViewById(R.id.share_btn);

        mArticleTitleView.setTypeface(mNeuton);
        mWebView.getSettings().setDefaultFontSize(15);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");

        mVisitWebsiteBtn = (Button)view.findViewById(R.id.visit_website_btn);

        bundle = getActivity().getIntent().getExtras();


        if (bundle.getParcelable("article")!= null) {
            article = bundle.getParcelable("article");


            mArticleLink = article.getLink();
            mArticleTitle = article.getTitle();


            String continueReadingArticle = "<a style='color:#3498db; text-decoration:none;' href = \"" + article.getLink() + "\">" +"" +
                    " Continue Reading "+ article.getTitle()+"</a>";

            mWebView.loadData("<p style='text-align:justify; line-height:50px'>"+
                    article.getDescription() + continueReadingArticle+ "" +
                    "</p>", "text/html; charset=utf-8", "UTF-8" );


            mArticleTitleView.setText(mArticleTitle);

            mAuthorView.setText("By " + article.getAuthor());




        }


        mVisitWebsiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.setData(Uri.parse("http://www.unilorinleague.org/"));
                startActivity(in);
            }
        });

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mArticleTitle + mArticleLink);
                sendIntent.setType("text/html");
                startActivity(Intent.createChooser(sendIntent, "Send To"));
            }
        });



        return view;

    }


    }


