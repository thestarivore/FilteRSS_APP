package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.Article;

import java.util.List;

public interface ArticleCallback {
    public void onLoad(List<Article> articles);
    public void onFailure();
}
