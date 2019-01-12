package com.company.rss.rss.service;


import android.content.Context;
import android.database.SQLException;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.SQLOperation;
import com.company.rss.rss.persistence.articles.ArticleCursor;
import com.company.rss.rss.persistence.articles.ArticleSQLiteRepository;
import com.company.rss.rss.restful_api.callbacks.ArticleCallback;
import com.company.rss.rss.restful_api.callbacks.SQLOperationCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLiteService {

    private static SQLiteService instance;
    private ArticleSQLiteRepository articleSQLiteRepository;
    private final Lock _mutex = new ReentrantLock(true);

    private SQLiteService(Context context){
        articleSQLiteRepository = new ArticleSQLiteRepository(context);
    }

    public static synchronized SQLiteService getInstance(Context context){
        if (instance == null){
            instance = new SQLiteService(context);
        }
        return instance;
    }

    /**
     * Get/Retrieve the List of all the articles in the local SQLite Database (from the Article Table)
     * @param callback  ArticleCallback callback object with the onLoad() and onFailed() methods
     *                  that are used to notify the result of the execution. This callback returns
     *                  a List<Article> on the onLoad() method.
     */
    public synchronized void getAllArticles(final ArticleCallback callback){
        Thread thread = new Thread() {
            @Override
            public void run() {
                ArticleCursor cursor = articleSQLiteRepository.findAll();
                if (cursor != null) {
                    List<Article> articleList = getArticleListFromCursor(cursor);
                    callback.onLoad(articleList);
                } else
                    callback.onFailure();
            }
        };
        thread.start();
    }

    /**
     * Put/Store a list of articles in the local SQLite Database (into the Article Table)
     * @param articles  List<Article> object containing the list of articles passed as argument
     * @param callback  SQLOperationCallback callback object with the onLoad() and onFailed() methods
     *                  that are used to notify the result of the execution. This callback returns
     *                  a SQLOperation object on the onLoad() method, containing informations about
     *                  the query ran.
     */
    public synchronized void putArticles(final List<Article> articles, final SQLOperationCallback callback){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    //_mutex.lock();
                    for (Article article : articles) {
                        articleSQLiteRepository.add(article);
                    }
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setAffectedRows(articles.size());
                    sqlOperation.setMessage("Successfully added a list of Articles("+articles.size()+") on the database Article table!");
                    callback.onLoad(sqlOperation);
                    //_mutex.unlock();
                }
                catch (SQLException e){
                    callback.onFailure();
                }
                catch (Exception e){
                    callback.onFailure();
                }
            }
        };
        thread.start();
    }

    /**
     * Delete all the articles from the local SQLite Database (from the Article Table)
     * @param callback  SQLOperationCallback callback object with the onLoad() and onFailed() methods
     *                  that are used to notify the result of the execution. This callback returns
     *                  a SQLOperation object on the onLoad() method, containing informations about
     *                  the query ran.
     */
    public synchronized void deleteAllArticles(final SQLOperationCallback callback){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    articleSQLiteRepository.deleteAll();
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setMessage("Deleted all the rows in the sqlite database!");
                    callback.onLoad(sqlOperation);
                }
                catch (SQLException e){
                    callback.onFailure();
                }
            }
        };
        thread.start();
    }

    /**
     * Delete an articles from the local SQLite Database (from the Article Table)
     * @param article   Article object to be deleted from the database
     * @param callback  SQLOperationCallback callback object with the onLoad() and onFailed() methods
     *                  that are used to notify the result of the execution. This callback returns
     *                  a SQLOperation object on the onLoad() method, containing informations about
     *                  the query ran.
     */
    public synchronized void deleteArticle(final Article article, final SQLOperationCallback callback){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    articleSQLiteRepository.delete(article);
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setMessage("Deleted an Article("+article.getHashId()+") in the sqlite database!");
                    callback.onLoad(sqlOperation);
                }
                catch (SQLException e){
                    callback.onFailure();
                }
            }
        };
        thread.start();
    }

    /**
     * A Helper function, that given an ArticleCursor object, it retrieves and returns a list of articles(List<Article>)
     * @param   cursor      ArticleCursor object
     * @return  articles    List<Article> object
     */
    public List<Article> getArticleListFromCursor(ArticleCursor cursor){
        final List<Article> articles = new ArrayList<>();

        //Iterate with the cursor and fill the list of articles
        while (cursor.moveToNext()){
            Article newArticle = new Article();
            newArticle.setHashId(cursor.getHashId());
            newArticle.setTitle(cursor.getTitle());
            newArticle.setAuthor(cursor.getAuthor());
            newArticle.setDescription(cursor.getDescription());
            newArticle.setComment(cursor.getComment());
            newArticle.setLink(cursor.getLink());
            newArticle.setImgLink(cursor.getImageLink());
            newArticle.setPubDateFromString(cursor.getPubDate());
            newArticle.setUser(cursor.getUser());
            newArticle.setFeed(cursor.getFeed());
            newArticle.setScore(cursor.getScore());
            articles.add(newArticle);
        }
        /*
        while (cursor.moveToNext()){
            Article newArticle = new Article();
            newArticle.setHashId(cursor.getLong(0));
            newArticle.setTitle(cursor.getString(1));
            newArticle.setAuthor(cursor.getString(2));
            newArticle.setDescription(cursor.getString(3));
            newArticle.setComment(cursor.getString(4));
            newArticle.setLink(cursor.getString(5));
            newArticle.setImgLink(cursor.getString(6));
            newArticle.setPubDateFromString(cursor.getString(7));
            newArticle.setUser(cursor.getInt(8));
            newArticle.setFeed(cursor.getInt(9));
            newArticle.setScore(cursor.getFloat(10));
            articles.add(newArticle);
        }*/
        return articles;
    }
}
