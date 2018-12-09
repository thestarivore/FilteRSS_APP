package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.Category;

import java.util.List;

public interface CategoryCallback {
    public void onLoad(List<Category> categories);
    public void onFailure();
}
