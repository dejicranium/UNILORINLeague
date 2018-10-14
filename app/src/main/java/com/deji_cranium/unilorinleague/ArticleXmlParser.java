package com.deji_cranium.unilorinleague;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by cranium on 10/20/17.
 */

public class ArticleXmlParser {
    public List<Article> articlesList;
    private Article article;
    private String text;

    {
        articlesList = new ArrayList<>();
    }




    public List<Article>getLeagueNews(){
        List<Article>getLeagueNews = new ArrayList<>();

        for (Article article : articlesList){
            if (!(article.getCategories().contains("Table Tennis")|| article.getCategories().contains("Table tennis")||
                    article.getCategories().contains("Basketball") || article.getCategories().contains("Scrabble") ||
                    article.getCategories().contains("Chess") || article.getCategories().contains("Badminton") ||
                    article.getCategories().contains("Athletics") || article.getCategories().contains("Handball")||
                    article.getCategories().contains("Volleyball") || article.getCategories().contains("Soccer")
                    || article.getCategories().contains("Football")))
            {
                //if article satisfies the above requirement, we'll set the type to othersport news
                //I need this because I want DetailsActivity to know the database to query so as to
                // mark an article as read.
                article.setType("none");
                article.setRead("none");
                getLeagueNews.add(article);
            }
        }
        return getLeagueNews;
    }




    public List<Article> getOtherSportsNews(){
        List<Article>otherSportNews = new ArrayList<>();
        for (Article article: articlesList){
            if(article.getCategories().contains("Table Tennis")|| article.getCategories().contains("Table tennis")||
                    article.getCategories().contains("Basketball") || article.getCategories().contains("Scrabble") ||
                    article.getCategories().contains("Chess") || article.getCategories().contains("Badminton") ||
                    article.getCategories().contains("Athletics") || article.getCategories().contains("Handball")||
                    article.getCategories().contains("Volleyball") || article.getCategories().contains("Soccer")
                    || article.getCategories().contains("Football"))
            {

                article.setType("none");
                article.setRead("none");
                otherSportNews.add(article);
            }

        }
        return otherSportNews;

    }



    public void parse(InputStream is) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            article = new Article();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            articlesList.add(article);
                        }
                        else if(tagName.equalsIgnoreCase("title")){
                            if (article != null){
                                article.setTitle(Jsoup.parse(text).text());
                            }
                        }
                        else if (tagName.equalsIgnoreCase("description")){
                            if (article != null){
                                article.setDescription(Jsoup.parse(text).text());
                            }
                        }
                        else if (tagName.equalsIgnoreCase("dc:creator")){
                            if(article !=  null){
                                article.setAuthor(text);
                            }
                        }
                        else if (tagName.equalsIgnoreCase("link")){
                            if(article != null){
                                article.setLink(text);
                            }
                        }
                        else if(tagName.equalsIgnoreCase("category")){
                            if(article!=null){
                                article.addCategory(text);
                            }
                        }
                        else if (tagName.equalsIgnoreCase("pubDate")){
                            if (article!=null){
                                String pubDate = text;
                                article.setPubDate(pubDate);
                            }
                        }

                        break;
                    default:
                        break;

                }
                eventType = parser.next();

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}


