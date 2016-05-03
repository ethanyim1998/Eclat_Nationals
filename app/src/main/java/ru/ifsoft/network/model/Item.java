package ru.ifsoft.network.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.ifsoft.network.constants.Constants;


public class Item extends Application implements Constants, Parcelable {

    private long id, fromUserId, rePostId;
    private int createAt, likesCount, commentsCount, rePostsCount, allowComments, accessMode, fromUserVerify;
    private String timeAgo, date, post, imgUrl, fromUserUsername, fromUserFullname, fromUserPhotoUrl, area, country, city;
    private Double lat = 0.000000, lng = 0.000000;
    private Boolean myLike, myRePost;

    private String rePostTimeAgo, rePostDate, rePostPost, rePostImgUrl, rePostFromUserUsername, rePostFromUserFullname, rePostFromUserPhotoUrl;
    private int fromRePostUserVerify, rePostRemoveAt;
    private long rePostFromUserId;

    public Item() {

    }

    public Item(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setRePostId(jsonData.getLong("rePostId"));
                this.setFromUserId(jsonData.getLong("fromUserId"));
                this.setAccessMode(jsonData.getInt("accessMode"));
                this.setFromUserVerify(jsonData.getInt("fromUserVerify"));
                this.setFromUserUsername(jsonData.getString("fromUserUsername"));
                this.setFromUserFullname(jsonData.getString("fromUserFullname"));
                this.setFromUserPhotoUrl(jsonData.getString("fromUserPhoto"));
                this.setPost(jsonData.getString("post"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setArea(jsonData.getString("area"));
                this.setCountry(jsonData.getString("country"));
                this.setCity(jsonData.getString("city"));
                this.setAllowComments(jsonData.getInt("allowComments"));
                this.setCommentsCount(jsonData.getInt("commentsCount"));
                this.setLikesCount(jsonData.getInt("likesCount"));
                this.setRePostsCount(jsonData.getInt("rePostsCount"));
                this.setMyLike(jsonData.getBoolean("myLike"));
                this.setMyRePost(jsonData.getBoolean("myRePost"));
                this.setLat(jsonData.getDouble("lat"));
                this.setLng(jsonData.getDouble("lng"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));

                if (jsonData.has("rePost")) {

                    JSONArray rePostArray = jsonData.getJSONArray("rePost");

                    if (rePostArray.length() > 0) {

                        JSONObject rePostObj = (JSONObject) rePostArray.get(0);

                        this.setRePostFromUserFullname(rePostObj.getString("fromUserFullname"));
                        this.setRePostFromUserUsername(rePostObj.getString("fromUserUsername"));
                        this.setRePostFromUserPhotoUrl(rePostObj.getString("fromUserPhoto"));
                        this.setRePostPost(rePostObj.getString("post"));
                        this.setRePostImgUrl(rePostObj.getString("imgUrl"));
                        this.setRePostTimeAgo(rePostObj.getString("timeAgo"));
                        this.setRePostFromUserVerify(rePostObj.getInt("fromUserVerify"));
                        this.setRePostFromUserId(rePostObj.getLong("fromUserId"));
                        this.setRePostRemoveAt(rePostObj.getInt("removeAt"));

                    }
                }
            }

        } catch (Throwable t) {

            Log.e("Item", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Item", jsonData.toString());
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRePostId() {

        return this.rePostId;
    }

    public void setRePostId(long rePostId) {

        this.rePostId = rePostId;
    }

    public long getFromUserId() {

        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getRePostFromUserId() {

        return rePostFromUserId;
    }

    public void setRePostFromUserId(long rePostFromUserId) {

        this.rePostFromUserId = rePostFromUserId;
    }

    public int getAccessMode() {

        return accessMode;
    }

    public void setAccessMode(int accessMode) {

        this.accessMode = accessMode;
    }

    public int getFromUserVerify() {

        return fromUserVerify;
    }

    public void setFromUserVerify(int fromUserVerify) {

        this.fromUserVerify = fromUserVerify;
    }

    public int getRePostFromUserVerify() {

        return fromRePostUserVerify;
    }

    public void setRePostFromUserVerify(int fromRePostUserVerify) {

        this.fromRePostUserVerify = fromRePostUserVerify;
    }

    public int getRePostRemoveAt() {

        return rePostRemoveAt;
    }

    public void setRePostRemoveAt(int rePostRemoveAt) {

        this.rePostRemoveAt = rePostRemoveAt;
    }

    public int getAllowComments() {

        return allowComments;
    }

    public void setAllowComments(int allowComments) {
        this.allowComments = allowComments;
    }

    public int getCommentsCount() {

        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {

        return this.likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getRePostsCount() {

        return this.rePostsCount;
    }

    public void setRePostsCount(int rePostsCount) {
        this.rePostsCount = rePostsCount;
    }

    public int getCreateAt() {

        return createAt;
    }

    public void setCreateAt(int createAt) {
        this.createAt = createAt;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setRePostTimeAgo(String rePostTimeAgo) {
        this.rePostTimeAgo = rePostTimeAgo;
    }

    public String getRePostTimeAgo() {
        return rePostTimeAgo;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getRePostPost() {
        return rePostPost;
    }

    public void setRePostPost(String rePostPost) {
        this.rePostPost = rePostPost;
    }


    public String getFromUserUsername() {
        return fromUserUsername;
    }

    public void setFromUserUsername(String fromUserUsername) {
        this.fromUserUsername = fromUserUsername;
    }

    public String getRePostFromUserUsername() {
        return rePostFromUserUsername;
    }

    public void setRePostFromUserUsername(String rePostFromUserUsername) {
        this.rePostFromUserUsername = rePostFromUserUsername;
    }

    public String getFromUserFullname() {
        return fromUserFullname;
    }

    public void setFromUserFullname(String fromUserFullname) {
        this.fromUserFullname = fromUserFullname;
    }

    public String getRePostFromUserFullname() {
        return rePostFromUserFullname;
    }

    public void setRePostFromUserFullname(String rePostFromUserFullname) {
        this.rePostFromUserFullname = rePostFromUserFullname;
    }

    public String getFromUserPhotoUrl() {

        if (fromUserPhotoUrl == null) {

            fromUserPhotoUrl = "";
        }

        return fromUserPhotoUrl;
    }

    public void setFromUserPhotoUrl(String fromUserPhotoUrl) {
        this.fromUserPhotoUrl = fromUserPhotoUrl;
    }

    public String getRePostFromUserPhotoUrl() {

        if (rePostFromUserPhotoUrl == null) {

            rePostFromUserPhotoUrl = "";
        }

        return rePostFromUserPhotoUrl;
    }

    public void setRePostFromUserPhotoUrl(String rePostFromUserPhotoUrl) {
        this.rePostFromUserPhotoUrl = rePostFromUserPhotoUrl;
    }


    public Boolean isMyLike() {
        return myLike;
    }

    public void setMyLike(Boolean myLike) {

        this.myLike = myLike;
    }

    public Boolean isMyRePost() {
        return myRePost;
    }

    public void setMyRePost(Boolean myRePost) {

        this.myRePost = myRePost;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRePostImgUrl() {
        return rePostImgUrl;
    }

    public void setRePostImgUrl(String rePostImgUrl) {
        this.rePostImgUrl = rePostImgUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArea() {

        if (this.area == null) {

            this.area = "";
        }

        return this.area;
    }

    public void setArea(String area) {

        this.area = area;
    }

    public String getCountry() {

        if (this.country == null) {

            this.country = "";
        }

        return this.country;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public String getCity() {

        if (this.city == null) {

            this.city = "";
        }

        return this.city;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public Double getLat() {

        return this.lat;
    }

    public void setLat(Double lat) {

        this.lat = lat;
    }

    public Double getLng() {

        return this.lng;
    }

    public void setLng(Double lng) {

        this.lng = lng;
    }

    public String getLink() {

        return WEB_SITE + this.getFromUserUsername() + "/post/" + Long.toString(this.getId());
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Item createFromParcel(Parcel in) {

            return new Item();
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
