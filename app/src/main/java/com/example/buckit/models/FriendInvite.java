package com.example.buckit.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("FriendInvite")
public class FriendInvite extends ParseObject {

    public static final String KEY_INVITER = "inviter";
    public static final String KEY_INVITED = "invited";
    public static final String KEY_REQUEST_STATUS = "requestStatus";

    public FriendInvite(){ }

    public ParseUser getInvited () {return getParseUser(KEY_INVITED); }

    public void setInvited (ParseUser user) {put(KEY_INVITED, user); }

    public ParseUser getInviter () {return getParseUser(KEY_INVITER); }

    public void setInviter (ParseUser user) {put(KEY_INVITER, user); }

    public int getStatus() {return getInt(KEY_REQUEST_STATUS); }

    public void setStatus (int status) { put(KEY_REQUEST_STATUS, status); }



    public static class Query extends ParseQuery<FriendInvite> {


        public Query() {
            super(FriendInvite.class);
        }

        public FriendInvite.Query getFriendRequests(ParseUser user){
            whereEqualTo(KEY_INVITED, user);
            return this;
        }

        public FriendInvite.Query getSentRequests(ParseUser user){
            whereEqualTo(KEY_INVITER, user.getObjectId());
            return this;
        }

        public FriendInvite.Query withInviter(){
            include(KEY_INVITER);
            return this;
        }

        public FriendInvite.Query getAccepted(){
            whereEqualTo(KEY_REQUEST_STATUS, 1);
            return this;
        }

        public FriendInvite.Query getUnansweredRequests () {
            whereEqualTo(KEY_REQUEST_STATUS, 2);
            return this;
        }
    }
}
