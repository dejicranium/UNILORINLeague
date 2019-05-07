package com.deji_cranium.unilorinleague;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cranium on 10/20/17.
 */

public class Article implements Parcelable {

    private String mTitle;
    private String mDescription;
    private String mAuthor;
    private String mLink;
    private String mContent;
    private String mRead;
    private String mImage;
    private List<String> mCategories;
    private String mPubDate;
    private String mType;

    public Article() {
        mCategories = new ArrayList<>();
        mRead = "none";
        mType = "none";

    }

    public Article(String title, String link, String description, String author, String pubDate, String type, String read){
        mTitle = title;
        mDescription = description;
        mAuthor = author;
        mLink = link;
        mCategories = new ArrayList<>();
        mType = type;
        mRead = read;
        mPubDate = pubDate;
    }

    protected Article(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
        mAuthor = in.readString();
        mLink = in.readString();
        mPubDate = in.readString();
        mContent = in.readString();
        mImage = in.readString();
        mType = in.readString();
        mRead = in.readString();


    }
    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public void setAuthor(String author){
        mAuthor = author;
    }
    public void setTitle(String title){
        mTitle = title;
    }
    public void setLink(String link){mLink = link;}
    public void setDescription(String description){
        mDescription = description;
    }
    public void setContent(String content){
        mContent = content;
    }
    public void setPubDate(String pubDate){mPubDate = pubDate;}
    public void addCategory(String category){mCategories.add(category);}
    public void setType(String type){this.mType = type;}
    public void setRead(String read){this.mRead = read;}


    public String getTitle(){
        return mTitle;
    }
    public String getLink(){return mLink;}
    public String getDescription(){
        return mDescription;
    }
    public String getAuthor(){
        return mAuthor;
    }
    public String getmImage(){return mImage;}
    public String getPubDate(){return mPubDate; }
    public String getRead(){return mRead;}
    public String getType(){return this.mType;}


    public List<String> getCategories(){return mCategories;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
        parcel.writeString(mAuthor);
        parcel.writeString(mLink);
        parcel.writeString(mType);
        parcel.writeString(mPubDate);
        parcel.writeString(mRead);
        parcel.writeString(mImage);
    }
}
