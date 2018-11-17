package com.company.rss.rss;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.company.rss.rss.adapters.ArticleSlidePagerAdapter;
import com.company.rss.rss.fragments.ArticlesListFragment;
import com.company.rss.rss.fragments.ArticlesSlideFragment;
import com.company.rss.rss.models.Article;

import java.util.List;


public class ArticlesListActivity extends AppCompatActivity implements ArticlesListFragment.OnListFragmentInteractionListener, ArticlesSlideFragment.OnFragmentInteractionListener {

    // TODO: refactor this
    public static final String EXTRA_ARTICLE = "com.rss.rss.ARTICLE";
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarArtlclesList);
        setSupportActionBar(toolbar);

        // TOP ARTICLES
        // Create mock articles for top articles
        List<Article> topArticles = Article.generateMockupArticle(6);

        mPager = (ViewPager) findViewById(R.id.pagerArticles);
        mPagerAdapter = new ArticleSlidePagerAdapter(getSupportFragmentManager(), topArticles);
        mPager.setAdapter(mPagerAdapter);
        // Set current item to middle
        mPager.setCurrentItem(mPagerAdapter.getCount() / 2, false);
        mPager.setClipToPadding(false);
        mPager.setPadding(60, 0, 60, 0);
        mPager.setPageMargin(0);

        // Show CollapsingToolbarLayout Title only when collapsed
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayoutArticlesList);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.AppBarLayoutArticlesList);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Title");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_articles_list, menu);
        return true;
    }

    private void startArticleActivity(Article article) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(EXTRA_ARTICLE, article);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSearchArticleList:
                Intent intent = new Intent(this, FeedsSearchActivity.class);
                startActivity(intent);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    /*
    Handles the interactions with the list, click
     */
    @Override
    public void onListFragmentInteractionClick(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        startArticleActivity(article);
    }

    /*
    Handles the interactions with the list, swipe
     */
    @Override
    public void onListFragmentInteractionSwipe(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        /* TODO: save article in the read it later collection if not already present
        otherwise remove it from read it later */
        boolean alreadyPresent = false;
        if (alreadyPresent) {
            Toast.makeText(this, R.string.unswipe_to_remove, Toast.LENGTH_LONG).show();
        }
    }

    /*
    Handles the interactions with the top slider
     */
    @Override
    public void onFragmentInteraction(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        startArticleActivity(article);
    }

}
