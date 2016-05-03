package ru.ifsoft.network.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import ru.ifsoft.network.constants.Constants;

public class Comment extends Application implements Constants, Parcelable {

    private long id, postId, fromUserId, replyToUserId;
    private int fromUserState, fromUserVerify, createAt;
    private String comment, fromUserUsername, fromUserFullname, replyToUserUsername, replyToUserFullname, fromUserPhotoUrl, timeAgo;

    public Comment() {

    }

    public Comment(JSONObject jsonData) {

        try {

            this.setId(jsonData.getLong("id"));
            this.setItemId(jsonData.getLong("postId"));
            this.setFromUserId(jsonData.getLong("fromUserId"));
            this.setFromUserState(jsonData.getInt("fromUserState"));
            this.setFromUserVerify(jsonData.getInt("fromUserVerify"));
            this.setFromUserUsername(jsonData.getString("fromUserUsername"));
            this.setFromUserFullname(jsonData.getString("fromUserFullname"));
            this.setFromUserPhotoUrl(jsonData.getString("fromUserPhotoUrl"));
            this.setReplyToUserId(jsonData.getLong("replyToUserId"));
            this.setReplyToUserUsername(jsonData.getString("replyToUserUsername"));
            this.setReplyToUserFullname(jsonData.getString("replyToFullname"));
            this.setText(jsonData.getString("comment"));
            this.setTimeAgo(jsonData.getString("timeAgo"));
            this.setCreateAt(jsonData.getInt("createAt"));

        } catch (Throwable t) {

            Log.e("Comment", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Comment", jsonData.toString());
        }
    }

    public void setId(long id) {

        this.id = id;
    }

    public long getId() {

        return this.id;
    }

    public void setItemId(long postId) {

        this.postId = postId;
    }

    public long getItemId() {

        return this.postId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getFromUserId() {

        return this.fromUserId;
    }

    public void setReplyToUserId(long replyToUserId) {

        this.replyToUserId = replyToUserId;
    }

    public long getReplyToUserId() {

        return this.replyToUserId;
    }

    public void setFromUserState(int fromUserState) {

        this.fromUserState = fromUserState;
    }

    public int getFromUserState() {

        return this.fromUserState;
    }

    public void setFromUserVerify(int fromUserVerify) {

        this.fromUserVerify = fromUserVerify;
    }

    public int getFromUserVerify() {

        return this.fromUserVerify;
    }

    public void setText(String comment) {

        this.comment = comment;
    }

    public String getText() {

        return this.comment;
    }

    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }

    public String getTimeAgo() {

        return this.timeAgo;
    }

    public void setFromUserUsername(String fromUserUsername) {

        this.fromUserUsername = fromUserUsername;
    }

    public String getFromUserUsername() {

        return this.fromUserUsername;
    }

    public void setReplyToUserUsername(String replyToUserUsername) {

        this.replyToUserUsername = replyToUserUsername;
    }

    public String getReplyToUserUsername() {

        return this.replyToUserUsername;
    }

    public void setFromUserFullname(String fromUserFullname) {

        this.fromUserFullname = fromUserFullname;
    }

    public String getFromUserFullname() {

        return this.fromUserFullname;
    }

    public void setReplyToUserFullname(String replyToUserFullname) {

        this.replyToUserFullname = replyToUserFullname;
    }

    public String getReplyToUserFullname() {

        return this.replyToUserFullname;
    }

    public void setFromUserPhotoUrl(String fromUserPhotoUrl) {

        this.fromUserPhotoUrl = fromUserPhotoUrl;
    }

    public String getFromUserPhotoUrl() {

        return this.fromUserPhotoUrl;
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
