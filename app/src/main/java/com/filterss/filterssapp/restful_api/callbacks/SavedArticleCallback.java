package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.SavedArticle;

import java.util.List;

public interface SavedArticleCallback {
    public void onLoad(List<SavedArticle> savedArticles);
    public void onFailure();
}
