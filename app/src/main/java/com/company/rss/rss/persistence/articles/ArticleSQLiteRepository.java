package com.company.rss.rss.persistence.articles;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.persistence.articles.ArticleDBContract.ArticleEntry;
import static com.company.rss.rss.persistence.articles.ArticleDBContract.getWritableDatabase;


public class ArticleSQLiteRepository {

    private SQLiteDatabase db;

    public ArticleSQLiteRepository(Context context){
        db = getWritableDatabase(context);
    }

    public void add(Article article) throws SQLException {
        db.execSQL("INSERT OR REPLACE INTO "+ArticleEntry.TABLE_NAME
                +" ("   +ArticleEntry.HASH_ID_CLMN+","
                        +ArticleEntry.TITLE_CLMN+","
                        +ArticleEntry.AUTHOR_CLMN+","
                        +ArticleEntry.DESCRIPTION_CLMN+","
                        +ArticleEntry.COMMENT_CLMN+","
                        +ArticleEntry.LINK_CLMN+","
                        +ArticleEntry.IMG_LINK_CLMN+","
                        +ArticleEntry.PUB_DATE_CLMN+","
                        +ArticleEntry.USER_CLMN+","
                        +ArticleEntry.FEED_CLMN+","
                        +ArticleEntry.SCORE_CLMN+") VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{
                        article.getHashId(),
                        article.getTitle(),
                        article.getAuthor(),
                        article.getDescription(),
                        article.getComment(),
                        article.getLink(),
                        article.getImgLink(),
                        article.getPubDate(),
                        article.getUser(),
                        article.getFeed(),
                        article.getScore()});
    }

    public void delete(Article article) throws SQLException {
        db.execSQL("DELETE FROM "+ArticleEntry.TABLE_NAME+ " WHERE " +
                        ArticleEntry.HASH_ID_CLMN + " = ?",
                new Object[]{article.getHashId()});
    }

    public void deleteAll() throws SQLException {
        db.execSQL("DELETE FROM "+ArticleEntry.TABLE_NAME+ " WHERE 1");
    }

    public ArticleCursor findAll() {
        Cursor res = db.rawQuery("SELECT * FROM "+
                ArticleEntry.TABLE_NAME, null);
        return new ArticleCursor(res);
    }

    public ArticleCursor findById(long id) {
        Cursor res = db.rawQuery("SELECT * FROM "+
                ArticleEntry.TABLE_NAME+" WHERE " +
                ArticleEntry.HASH_ID_CLMN+ " = ?", new String[]{String.valueOf(id)});
        return new ArticleCursor(res);
    }
}
