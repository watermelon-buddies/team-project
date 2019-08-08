package com.example.buckit.models;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationSender {

    /*
    * KEY:
    * 0 - New event invite
    * 1 - Event invite accepted
    * 2 - New friend request
    * 3 - Friend request accepted
    * */

    private static final String KEY_BODY = "body";
    private static final String KEY_FRIEND_NOTIFICATION = "friendNotification";
    private static final String KEY_EVENT_NOTIFICATION = "eventNotification";
    private static final String KEY_NUMTYPE = "numType";
    private static final String KEY_RELATEDUSER = "relatedUser";
    private static final String KEY_DATA = "data";
    private static final String KEY_REGISTRATION_IDS = "registration_ids";
    private static final String KEY_PRIORITY = "priority";
    private static final String MEDIA_TYPE = "application/json; charset=utf-8";
    private static final String URL = "https://fcm.googleapis.com/fcm/send";
    public static final String KEY_AUTHORIZATION = "Authorization";
    private static final String API_KEY = "key=AAAARGH08UQ:APA91bFv6okGY7RVsHIXT1gfhQ4Ag_dlqCqmPSmBuChSmye8kboxYt2eJg4I-P-JPZ0ULtXUP5kac_GV1sjSPaLw1ZoM45Wtr-_jOWiv4OR9HpnxU5EgL3ZosA0bTzFdvXckTczaiBea";
    private static final String EVENT_INVITE_MESSAGE = "You have a new invite from ";
    private static final String EVENT_ACCEPTED_MESSAGE = " accepted your invitation! Tap to add to calendar.";
    private static final String FRIEND_REQUEST_MESSAGE = " sent you a friend request!";
    private static final String FRIEND_ADDED_MESSAGE = " added you as a friend!";
    private JSONArray recipients;
    private Integer mNotificationType;
    private String mRelatedUser;



    public NotificationSender(String deviceId, Integer notificationType, String relatedUser){
        recipients = new JSONArray();
        recipients.put(deviceId);
        mNotificationType = notificationType;
        mRelatedUser = relatedUser;

    }

    private String makeMessage(){
        String message = "";
        switch(mNotificationType){
            case 0:
                message = EVENT_INVITE_MESSAGE + mRelatedUser;
                break;
            case 1:
                message = mRelatedUser + EVENT_ACCEPTED_MESSAGE;
                break;
            case 2:
                message = mRelatedUser + FRIEND_REQUEST_MESSAGE;
                break;
            case 3:
                message = mRelatedUser + FRIEND_ADDED_MESSAGE;
                break;
            default:
                message = "Error";
        }
        return message;
    }

    private String makeNotificationType(){
        if(mNotificationType < 2){
            return KEY_EVENT_NOTIFICATION;
        } else {
            return KEY_FRIEND_NOTIFICATION;
        }
    }



    @SuppressLint("StaticFieldLeak")
    public void sendNotification(){
        try {
            if (recipients.getString(0).length() > 0)
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            JSONObject root = new JSONObject();
                            JSONObject data = new JSONObject();
                            data.put(KEY_BODY, makeMessage());
                            data.put(KEY_NUMTYPE, makeNotificationType());
                            data.put(KEY_RELATEDUSER, mRelatedUser); /* TODO: add the related user name to message and that way we check if the notifications are relevant to the particular person */
                            root.put(KEY_DATA, data);
                            root.put(KEY_REGISTRATION_IDS, recipients);
                            root.put(KEY_PRIORITY, 10);
                            String result = postToFCM(root.toString());
                            Log.d("chat Activity", "Result: " + result);
                            return result;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(String result) {
                        try {
                            JSONObject resultJson = new JSONObject(result);
                            int success, failure;
                            success = resultJson.getInt("success");
                            failure = resultJson.getInt("failure");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String postToFCM(String bodyString) throws IOException {
        OkHttpClient mClient = new OkHttpClient();
        final MediaType JSON = MediaType.parse(MEDIA_TYPE);
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .addHeader(KEY_AUTHORIZATION, API_KEY)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}
