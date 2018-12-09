package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.User;

import java.util.List;

public interface UserCallback {
    public void onLoad(List<User> users);
    public void onFailure();
}
