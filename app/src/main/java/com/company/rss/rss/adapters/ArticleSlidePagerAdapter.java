package com.company.rss.rss.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.company.rss.rss.fragments.ArticlesSlideFragment;
import com.company.rss.rss.models.ArticleContent;

import java.util.ArrayList;

public class ArticleSlidePagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_ARTICLES = 6;
    private ArrayList<ArticleContent.Article> mArticles;

    // https://stackoverflow.com/questions/7766630/changing-viewpager-to-enable-infinite-page-scrolling
    public ArticleSlidePagerAdapter(FragmentManager fm, ArrayList<ArticleContent.Article> articles) {
        super(fm);
        mArticles = articles;
    }

    @Override
    public Fragment getItem(int position) {
        if (mArticles != null && mArticles.size() > 0)
        {
            position = position % mArticles.size();
            return ArticlesSlideFragment.newInstance(mArticles.get(position));
        }
        else
        {
            return ArticlesSlideFragment.newInstance(null);
        }
    }

    @Override
    public int getCount() {
        return NUM_ARTICLES;
    }

}
