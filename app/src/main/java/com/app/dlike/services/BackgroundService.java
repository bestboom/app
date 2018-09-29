package com.app.dlike.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.app.dlike.Tools;
import com.app.dlike.http.RetrofitClient;
import com.app.dlike.models.CommentModel;
import com.app.dlike.models.FollowCount;
import com.app.dlike.models.MentionModel;
import com.app.dlike.models.TransferModel;
import com.app.dlike.models.UpVoteModel;
import com.app.dlike.utilities.AppPreference;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.app.dlike.Tools.*;

public class BackgroundService extends Service {

    private AppPreference app;
    private String USERNAME;
    private  String TAG = "BAckground Service";
    Handler handler;
    Runnable r;
    private int fetch = 100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Tools.log(TAG, "Service Started");
        app = new AppPreference(this);
        USERNAME = Tools.getUsername(this);
        if(USERNAME == null || USERNAME == ""){
            stopService(new Intent(this, BackgroundService.class));
        }else {
            handler = new Handler();
            r = () -> {
                if(app.getFollowersNotification())
                    checkForNewFollowers();
                if(app.getMentionsNotification())
                    checkMentions();
                if(app.getRepliesNotification())
                    checkReplies(fetch);
                if(app.getUpvotesNotification())
                    checkUpvotes(fetch);
                if(app.getTransferNotification())
                    checkTransfer(fetch);
                if(!app.getFollowersNotification() && !app.getMentionsNotification()
                        && !app.getTransferNotification() && !app.getUpvotesNotification()
                        && !app.getRepliesNotification())
                    onDestroy();
                handler.postDelayed(r, TimeUnit.MINUTES.toMillis(15));
            };

            handler.postDelayed(r, TimeUnit.SECONDS.toMillis(5));
                //Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show()
        }
    }


    private void checkForNewFollowers() {
        RetrofitClient retrofitClient = new RetrofitClient(this, STEEM_API_BASE_URL);
        retrofitClient.getSteemService().getFollowCount(USERNAME)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<FollowCount>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(FollowCount followCount) {
                        if(followCount.getFollowers() > app.getLatestFollowCount()){
                            showPush(BackgroundService.this, "You have " + (followCount.getFollowers() - app.getLatestFollowCount()) + " new followers", "Followers");
                            app.setLatestFollowCount(followCount.getFollowers());
                        }
                    }
                });
    }

    private void checkMentions(){
        ArrayList<MentionModel> mentions = new ArrayList<>();
       RetrofitClient retrofitClient = new RetrofitClient(this, Tools.STEEM_PLUS_BASE_URL + USERNAME + "/");
        retrofitClient.getSteemService().getMentions()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<MentionModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<MentionModel> response) {
                        for (MentionModel mention: response) {
                            if(mention.getBody().contains("@" + USERNAME.trim())){
                                mentions.add(mention);
                                break;
                            }
                        }
                        if(!mentions.get(0).getPermlink().equals(app.getLatestMentionPermlink())){
                            showPush(BackgroundService.this, "You were mentioned in " + mentions.get(0).getPermlink(), "Mentions");
                            app.setLatestMentionPermlink(mentions.get(0).getPermlink());
                        }
                    }
                });

    }
    private void checkReplies(int fetch) {

        RetrofitClient retrofitClient = new RetrofitClient(this, Tools.STEEM_API_BASE_URL);
        retrofitClient.getSteemService().getAccountHistory(USERNAME,-1, fetch)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonArray>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(JsonArray jsonArray) {
                        //Tools.log(TAG, jsonArray.toString());
                                                ArrayList<CommentModel> temp = CommentModel.sorter(jsonArray, BackgroundService.this);
                        if(temp.size() == 0){
                            checkReplies(fetch + 500);
                        }else{
                            Collections.reverse(temp);
                            if(!temp.get(0).getParent_permlink().equals(app.getLatestCommentPermlink())){
                                showPush(BackgroundService.this, temp.get(0).getAuthor() + " made a comment on your post - " + temp.get(0).getParent_permlink(), "Replies");
                                app.setLatestCommentPermlink(temp.get(0).getParent_permlink());
                            }
                        }


                    }
                });
        //JsonArray temp = new Gson().fromJson(Tools.accountHistory, JsonArray.class);
        //upvotes = UpVoteModel.sorter(temp);
    }


    private void checkUpvotes(int fetch){
        RetrofitClient retrofitClient = new RetrofitClient(this, STEEM_API_BASE_URL);
        retrofitClient.getSteemService().getAccountHistory(USERNAME,-1, fetch)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonArray>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(JsonArray jsonArray) {
                        ArrayList<UpVoteModel> temp = UpVoteModel.sorter(jsonArray, BackgroundService.this);
                        if(temp.size() == 0){
                            checkUpvotes(fetch + 100);
                            return;
                        }else{
                            Collections.reverse(temp);
                            UpVoteModel up = temp.get(0);
                            if(!(up.getVoter() + "+" + up.getTimestamp()).equals(app.getLatestUpVoteVoter())){
                                showPush(BackgroundService.this, temp.get(0).getVoter() + " voted for you post - " + temp.get(0).getPermlink(), "UpVotes");
                                app.setLatestUpvoteVoter(up.getVoter() + "+" + up.getTimestamp());
                            }
                        }


                    }
                });
    }


    private void checkTransfer(int fetch) {
        RetrofitClient retrofitClient = new RetrofitClient(this, STEEM_API_BASE_URL);
        retrofitClient.getSteemService().getAccountHistory(USERNAME,-1, fetch)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonArray>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(JsonArray jsonArray) {
                        //Tools.log(TAG, jsonArray.toString());

                        ArrayList<TransferModel> temp = TransferModel.sorter(jsonArray);
                        if(jsonArray.size() == 0 || temp.size() == 0){
                            checkTransfer(fetch + 500);
                        }else{
                            Collections.reverse(temp);
                            if(!temp.get(0).getTimestamp().equals(app.getLatestTransferTImeStamp())){
                                showPush(BackgroundService.this,  temp.get(0).getFrom() + " transferred " + temp.get(0).getAmount() + " to you","TRANSFER");
                                app.setLatestTransferTimestamp(temp.get(0).getTimestamp());
                            }
                        }


                    }
                });
        //JsonArray temp = new Gson().fromJson(Tools.accountHistory, JsonArray.class);
        //upvotes = UpVoteModel.sorter(temp);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.log(TAG, "Service Closed");
    }
}
