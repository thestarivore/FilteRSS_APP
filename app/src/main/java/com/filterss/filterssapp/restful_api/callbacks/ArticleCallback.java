package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.Article;

import java.util.List;

public interface ArticleCallback {
    public void onLoad(List<Article> articles);
    public void onFailure();
}
