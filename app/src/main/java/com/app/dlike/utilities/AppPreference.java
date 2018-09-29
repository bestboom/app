package com.app.dlike.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.dlike.models.FollowCount;
import com.app.dlike.models.FollowModel;
import com.google.gson.Gson;

public class AppPreference {
    SharedPreferences spref;
    Context context;
    Gson gson = new Gson();
    SharedPreferences.Editor editor;
    private String LATEST_FOLLOWER_COUNT = "latest_follower_count";
    private String LATEST_MENTION_PERMLINK = "latest_mention_permlink";
    private String LATEST_COMMENT_PERMLINK = "latest_comment_permlink";
    private String LATEST_UPVOTES_PERMLINK = "latest_upvotes_permlink";
    private String LATEST_TRANSFER_PERMLINK = "latest_transfer_permlink";

    public boolean getFollowersNotification() {
        return spref.getBoolean("followers_notification", true);
    }

    public void setFollowersNotification(boolean followersNotification) {
        editor.putBoolean("followers_notification", followersNotification).apply();
    }

    public boolean getMentionsNotification() {
        return spref.getBoolean("mentions_notification", true);
    }

    public void setMentionsNotification(boolean mentionsNotification) {
        editor.putBoolean("mentions_notification", mentionsNotification).apply();
    }

    public boolean getUpvotesNotification() {
        return spref.getBoolean("upvotes_notification", true);
    }

    public void setUpvotesNotification(boolean upvotesNotification) {
        editor.putBoolean("upvotes_notification", upvotesNotification).apply();
    }

    public boolean getRepliesNotification() {
        return spref.getBoolean("replies_notification", true);
    }

    public void setRepliesNotification(boolean repliesNotification) {
        editor.putBoolean("replies_notification", repliesNotification).apply();
    }

    public boolean getTransferNotification() {
        return spref.getBoolean("transfer_notification", true);
    }

    public void setTransferNotification(boolean transferNotification) {
        editor.putBoolean("transfer_notification", transferNotification).apply();
    }

    public AppPreference(Context context)
    {
        this.context = context;
        spref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        editor = spref.edit();
    }

    public void setFollow(FollowModel follow)
    {
        editor.putString("follow",gson.toJson(follow, FollowModel.class)).apply();
    }

    public String getFollow(){
        return spref.getString("follow","");
    }





    public void setLatestFollowCount(int followCount){
        editor.putInt(LATEST_FOLLOWER_COUNT,followCount).apply();
    }

    public int getLatestFollowCount(){
        return spref.getInt(LATEST_FOLLOWER_COUNT, 0);
    }

    public void setLatestMentionPermlink(String value){
        editor.putString(LATEST_MENTION_PERMLINK, value).apply();
    }

    public String getLatestMentionPermlink(){
        return spref.getString(LATEST_MENTION_PERMLINK, "");
    }


    public void setLatestCommentPermlink(String value){
        editor.putString(LATEST_COMMENT_PERMLINK, value).apply();
    }

    public String getLatestCommentPermlink(){
        //return "";
       return spref.getString(LATEST_COMMENT_PERMLINK, "");
    }

    public void setLatestUpvoteVoter(String value){
        editor.putString(LATEST_UPVOTES_PERMLINK, value).apply();
    }

    public String getLatestUpVoteVoter(){
        //return "";
        return spref.getString(LATEST_UPVOTES_PERMLINK, "");
    }



    public void setLatestTransferTimestamp(String value){
        editor.putString(LATEST_TRANSFER_PERMLINK, value).apply();
    }

    public String getLatestTransferTImeStamp(){
        //return "";
        return spref.getString(LATEST_TRANSFER_PERMLINK, "");
    }

}
