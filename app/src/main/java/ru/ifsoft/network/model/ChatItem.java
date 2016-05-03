package ru.ifsoft.network.model;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import ru.ifsoft.network.constants.Constants;

public class ChatItem extends Application implements Constants {

    private long fromUserId;
    private int id, fromUserState, createAt;
    private String fromUserUsername, fromUserFullname, fromUserPhotoUrl, message, imgUrl, timeAgo, date;

    public ChatItem() {

    }

    public ChatItem(JSONObject jsonData) {

        try {

            this.setId(jsonData.getInt("id"));
            this.setFromUserId(jsonData.getLong("fromUserId"));
            this.setFromUserState(jsonData.getInt("fromUserState"));
            this.setFromUserUsername(jsonData.getString("fromUserUsername"));
            this.setFromUserFullname(jsonData.getString("fromUserFullname"));
            this.setFromUserPhotoUrl(jsonData.getString("fromUserPhotoUrl"));
            this.setMessage(jsonData.getString("message"));
            this.setImgUrl(jsonData.getString("imgUrl"));
            this.setCreateAt(jsonData.getInt("createAt"));
            this.setDate(jsonData.getString("date"));
            this.setTimeAgo(jsonData.getString("timeAgo"));

        } catch (Throwable t) {

            Log.e("ChatItem", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("ChatItem", jsonData.toString());
        }
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getId() {

        return this.id;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getFromUserId() {

        return this.fromUserId;
    }

    public void setFromUserState(int fromUserState) {

        this.fromUserState = fromUserState;
    }

    public int getFromUserState() {

        return this.fromUserState;
    }

    public void setFromUserUsername(String fromUserUsername) {

        this.fromUserUsername = fromUserUsername;
    }

    public String getFromUserUsername() {

        return this.fromUserUsername;
    }

    public void setFromUserFullname(String fromUserFullname) {

        this.fromUserFullname = fromUserFullname;
    }

    public String getFromUserFullname() {

        return this.fromUserFullname;
    }

    public void setFromUserPhotoUrl(String fromUserPhotoUrl) {

        this.fromUserPhotoUrl = fromUserPhotoUrl;
    }

    public String getFromUserPhotoUrl() {

        return this.fromUserPhotoUrl;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getMessage() {

        return this.message;
    }

    public void setImgUrl(String imgUrl) {

        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {

        return this.imgUrl;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getDate() {

        return this.date;
    }

    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }

    public String getTimeAgo() {

        return this.timeAgo;
    }

    public void setCreateAt(int createAt) {

        this.createAt = createAt;
    }

    public int getCreateAt() {

        return this.createAt;
    }
}
