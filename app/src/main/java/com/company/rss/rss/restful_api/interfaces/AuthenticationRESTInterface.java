package com.company.rss.rss.restful_api.interfaces;

import com.company.rss.rss.models.SQLOperation;
import com.company.rss.rss.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthenticationRESTInterface {
    @FormUrlEncoded
    @POST("/v1/auth/login")
    public Call<List<User>> getUserAuthentication(@Field("email") String email,
                                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("/v1/auth/registration")
    public Call<SQLOperation> registerNewUser(@Field("name") String name,
                                              @Field("surname") String surname,
                                              @Field("email") String email,
                                              @Field("password") String password);

    @FormUrlEncoded
    @POST("/v1/auth/password/change")
    public Call<SQLOperation> changeUsersPassword(@Field("email") String email,
                                                  @Field("password") String password);
}