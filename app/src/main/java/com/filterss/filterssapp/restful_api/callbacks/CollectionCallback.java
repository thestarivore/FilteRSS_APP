package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.Collection;

import java.util.List;

public interface CollectionCallback {
    public void onLoad(List<Collection> collections);
    public void onFailure();
}
