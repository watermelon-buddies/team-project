package com.example.buckit;

import android.app.Application;

import com.example.buckit.models.Bucketlist;
import com.parse.Parse;
import com.parse.ParseObject;





public class ParseApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Bucketlist.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu-buckit")
                .clientKey("buckist2019")
                .server("http://fbu-buckit.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }

}
