package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.ReadArticle;

import java.util.List;

public interface ReadArticleCallback {
    public void onLoad(List<ReadArticle> readArticles);
    public void onFailure();
}
