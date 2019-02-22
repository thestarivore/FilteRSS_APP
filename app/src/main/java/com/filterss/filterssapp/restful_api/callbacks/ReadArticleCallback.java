package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.ReadArticle;

import java.util.List;

public interface ReadArticleCallback {
    public void onLoad(List<ReadArticle> readArticles);
    public void onFailure();
}
