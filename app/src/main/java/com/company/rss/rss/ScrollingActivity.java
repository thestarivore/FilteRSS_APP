package com.company.rss.rss;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.company.rss.rss.models.ArticleContent;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity implements ArticleFragment.OnListFragmentInteractionListener,  ArticleSlideFragment.OnFragmentInteractionListener{


    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarArtlclesList);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


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
    public void onListFragmentInteraction(ArticleContent.Article item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
