package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.Multifeed;

import java.util.List;

public interface MultifeedCallback {
    public void onLoad(List<Multifeed> multifeeds);
    public void onFailure();
}
