package com.example.buckit;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.google.common.io.Resources;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class GoogleClient extends OAuthBaseClient {

    Context context;


    public static final BaseApi REST_API_INSTANCE = GoogleApi20.instance(); // Change this
    public static final String REST_URL = "https://accounts.google.com/"; // Change this, base API URL
    //public static final String REST_CONSUMER_KEY = "4KxocRp2Wh8RZ9cy1KJEjxGVy";       // Change this
    //public static final String REST_CONSUMER_SECRET = "EeyJ4vEZN3al7c0C13bMwAY3pGc2RASrampYtvJvnX1kLDHKJf"; // Change this
    public final static String REST_CONSUMER_KEY = "";
    public final static String REST_CONSUMER_SECRET = "";

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public GoogleClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }
    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
 /*   public void getHomeTimeline(AsyncHttpResponseHandler handler, long max_id) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        if (max_id != 0) {
            params.put("max_id", max_id);
        }
        params.put("count", 25);
        client.get(apiUrl, params, handler);
    }

    public void sendTweet(String message, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("status", message);
        client.post(apiUrl, params, handler);
    }*/
    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
}