package com.company.rss.rss;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.company.rss.rss.models.ArticleContent;

import java.util.ArrayList;

public class ArticlesListActivity extends AppCompatActivity implements ArticleFragment.OnListFragmentInteractionListener, ArticleSlideFragment.OnFragmentInteractionListener{

    // TODO: refactor this
    public static final String EXTRA_ARTICLE = "com.rss.rss.ARTICLE";

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);
        setTitle("All news");

        // Create mock articles for top articles
        ArrayList<ArticleContent.Article> topArticles = new ArrayList<ArticleContent.Article>();
        for(int i=0; i<6; i++){
            topArticles.add(ArticleContent.createMockArticle(i));
        }

        mPager = (ViewPager) findViewById(R.id.pagerArticles);
        mPagerAdapter = new ArticleSlidePagerAdapter(getSupportFragmentManager(), topArticles);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mPagerAdapter.getCount() / 2, false); // set current item in the adapter to middle
        mPager.setClipToPadding(false);
        mPager.setPadding(60,0,60,0);
        mPager.setPageMargin(0);
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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
