package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.Category;

import java.util.List;

public interface CategoryCallback {
    public void onLoad(List<Category> categories);
    public void onFailure();
}
