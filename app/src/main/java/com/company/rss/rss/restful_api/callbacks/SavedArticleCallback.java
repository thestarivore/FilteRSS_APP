package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.SavedArticle;

import java.util.List;

public interface SavedArticleCallback {
    public void onLoad(List<SavedArticle> savedArticles);
    public void onFailure();
}
