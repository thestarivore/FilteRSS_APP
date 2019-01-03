package com.company.rss.rss.restful_api.callbacks;

import com.google.gson.JsonObject;

public interface JsonArrayCallback {
    public void onLoad(JsonObject jsonArray);
    public void onFailure();
}
