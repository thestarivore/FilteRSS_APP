package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.User;

import java.util.List;

public interface UserCallback {
    public void onLoad(List<User> users);
    public void onFailure();
}
