package com.company.rss.rss;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.company.rss.rss.models.ArticleContent;

public class ArticlesListActivity extends AppCompatActivity implements ArticleFragment.OnListFragmentInteractionListener{

    // TODO: refactor this
    public static final String EXTRA_ARTICLE = "com.rss.rss.ARTICLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);

        setTitle("All news");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_articles_list, menu);
        return true;
    }

    @Override
    public void onListFragmentInteraction(ArticleContent.Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        startArticleActivity(article);
    }

    private void startArticleActivity(ArticleContent.Article article) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(EXTRA_ARTICLE, article);
        startActivity(intent);
    }


}
