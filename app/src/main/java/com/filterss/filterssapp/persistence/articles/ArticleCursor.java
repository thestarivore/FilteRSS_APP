package com.filterss.filterssapp.persistence.articles;

import android.database.Cursor;
import android.database.CursorWrapper;

public class ArticleCursor extends CursorWrapper {

    public ArticleCursor(Cursor cursor) {
        super(cursor);
    }

    public long getHashId(){
        return getLong(
                getColumnIndex(ArticleDBContract.ArticleEntry.HASH_ID_CLMN));
    }

    public String getTitle(){
        return getString(
                getColumnIndex(ArticleDBContract.ArticleEntry.TITLE_CLMN));
    }

    public String getAuthor(){
        return getString(
                getColumnIndex(ArticleDBContract.ArticleEntry.AUTHOR_CLMN));
    }

    public String getDescription(){
        return getString(
                getColumnIndex(ArticleDBContract.ArticleEntry.DESCRIPTION_CLMN));
    }

    public String getComment(){
        return getString(
                getColumnIndex(ArticleDBContract.ArticleEntry.COMMENT_CLMN));
    }

    public String getLink(){
        return getString(
                getColumnIndex(ArticleDBContract.ArticleEntry.LINK_CLMN));
    }

    public String getImageLink(){
        return getString(
                getColumnIndex(ArticleDBContract.ArticleEntry.IMG_LINK_CLMN));
    }

    public String getPubDate(){
        return getString(
                getColumnIndex(ArticleDBContract.ArticleEntry.PUB_DATE_CLMN));
    }

    public int getUser(){
        return getInt(
                getColumnIndex(ArticleDBContract.ArticleEntry.USER_CLMN));
    }

    public int getFeed(){
        return getInt(
                getColumnIndex(ArticleDBContract.ArticleEntry.FEED_CLMN));
    }

    public float getScore(){
        return getFloat(
                getColumnIndex(ArticleDBContract.ArticleEntry.SCORE_CLMN));
    }
}
