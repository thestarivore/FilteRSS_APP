package com.company.rss.rss.service;


import android.content.Context;
import android.database.SQLException;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.SQLOperation;
import com.company.rss.rss.persistence.articles.ArticleCursor;
import com.company.rss.rss.persistence.articles.ArticleSQLiteRepository;
import com.company.rss.rss.restful_api.callbacks.ArticleCallback;
import com.company.rss.rss.restful_api.callbacks.SQLOperationCallback;
import com.company.rss.rss.restful_api.callbacks.SQLOperationListCallback;

import java.util.ArrayList;
import java.util.List;

public class SQLiteService {

    private static SQLiteService instance;
    private ArticleSQLiteRepository articleSQLiteRepository;

    private SQLiteService(Context context){
        articleSQLiteRepository = new ArticleSQLiteRepository(context);
    }

    public static synchronized SQLiteService getInstance(Context context){
        if (instance == null){
            instance = new SQLiteService(context);
        }
        return instance;
    }

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

    public synchronized void putArticles(final List<Article> articles, final SQLOperationCallback callback){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    for (Article article : articles) {
                        articleSQLiteRepository.add(article);
                    }
                    SQLOperation sqlOperation = new SQLOperation();
                    sqlOperation.setAffectedRows(articles.size());
                    sqlOperation.setMessage("Successfully added a list of Articles("+articles.size()+") on the database Article table!");
                    callback.onLoad(sqlOperation);
                }
                catch (SQLException e){
                    callback.onFailure();
                }
            }
        };
        thread.start();
    }

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
        return articles;
    }
}
