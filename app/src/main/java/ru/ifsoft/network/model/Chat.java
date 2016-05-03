package ru.ifsoft.network.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.ifsoft.network.constants.Constants;

public class Chat extends Application implements Constants, Parcelable {

    private long withUserId;
    private int id, withUserState, newMessagesCount, createAt, withUserVerify;
    private String withUserUsername, withUserFullname, withUserPhotoUrl, timeAgo, date, lastMessage, lastMessageAgo;

    public Chat() {

    }

    public Chat(JSONObject jsonData) {

        try {

            this.setId(jsonData.getInt("id"));
            this.setWithUserId(jsonData.getLong("withUserId"));
            this.setWithUserVerify(jsonData.getInt("withUserVerify"));
            this.setWithUserState(jsonData.getInt("withUserState"));
            this.setWithUserUsername(jsonData.getString("withUserUsername"));
            this.setWithUserFullname(jsonData.getString("withUserFullname"));
            this.setWithUserPhotoUrl(jsonData.getString("withUserPhotoUrl"));
            this.setLastMessage(jsonData.getString("lastMessage"));
            this.setLastMessageAgo(jsonData.getString("lastMessageAgo"));
            this.setNewMessagesCount(jsonData.getInt("newMessagesCount"));
            this.setDate(jsonData.getString("date"));
            this.setCreateAt(jsonData.getInt("createAt"));
            this.setTimeAgo(jsonData.getString("timeAgo"));

        } catch (Throwable t) {

            Log.e("Chat", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Chat", jsonData.toString());
        }
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getId() {

        return this.id;
    }

    public void setWithUserId(long withUserId) {

        this.withUserId = withUserId;
    }

    public long getWithUserId() {

        return this.withUserId;
    }

    public void setWithUserState(int withUserState) {

        this.withUserState = withUserState;
    }

    public long getWithUserVerify() {

        return this.withUserVerify;
    }

    public void setWithUserVerify(int withUserVerify) {

        this.withUserVerify = withUserVerify;
    }

    public int getWithUserState() {

        return this.withUserState;
    }

    public void setWithUserUsername(String withUserUsername) {

        this.withUserUsername = withUserUsername;
    }

    public String getWithUserUsername() {

        return this.withUserUsername;
    }

    public void setWithUserFullname(String withUserFullname) {

        this.withUserFullname = withUserFullname;
    }

    public String getWithUserFullname() {

        return this.withUserFullname;
    }

    public void setWithUserPhotoUrl(String withUserPhotoUrl) {

        this.withUserPhotoUrl = withUserPhotoUrl;
    }

    public String getWithUserPhotoUrl() {

        return this.withUserPhotoUrl;
    }

    public void setLastMessage(String lastMessage) {

        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {

        return this.lastMessage;
    }

    public void setLastMessageAgo(String lastMessageAgo) {

        this.lastMessageAgo = lastMessageAgo;
    }

    public String getLastMessageAgo() {

        return this.lastMessageAgo;
    }

    public void setNewMessagesCount(int newMessagesCount) {

        this.newMessagesCount = newMessagesCount;
    }

    public int getNewMessagesCount() {

        return this.newMessagesCount;
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

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Comment createFromParcel(Parcel in) {

            return new Comment();
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
