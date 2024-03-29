package com.example.buckit;

import android.app.Application;

import com.example.buckit.models.Bucketlist;
import com.example.buckit.models.FriendInvite;
import com.example.buckit.models.User;
import com.example.buckit.models.UserInvite;
import com.example.buckit.models.UserNotification;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;



public class ParseApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);
        ParseObject.registerSubclass(Bucketlist.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(UserInvite.class);
        ParseObject.registerSubclass(UserNotification.class);
        ParseObject.registerSubclass(FriendInvite.class);
        ParseObject.registerSubclass(ParseUser.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu-buckit")
                .clientKey("buckist2019")
                .server("http://fbu-buckit.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }

}
