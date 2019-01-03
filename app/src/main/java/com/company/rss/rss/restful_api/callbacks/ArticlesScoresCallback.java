package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.ArticlesScores;

import java.util.List;

public interface ArticlesScoresCallback {
    public void onLoad(List<ArticlesScores> articlesScores);
    public void onFailure();
}
