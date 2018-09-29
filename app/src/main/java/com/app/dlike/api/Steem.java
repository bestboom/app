package com.app.dlike.api;

import com.app.dlike.models.Comment;
import com.app.dlike.models.CommentModel;
import com.app.dlike.models.CommentOperation;
import com.app.dlike.models.Discussion;
import com.app.dlike.models.FollowCount;
import com.app.dlike.models.FollowModel;
import com.app.dlike.models.LoginRequest;
import com.app.dlike.models.LoginResponse;
import com.app.dlike.models.MentionModel;
import com.app.dlike.models.RefreshTokenRequest;
import com.app.dlike.models.VoteOperation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by moses on 8/18/18.
 */

public interface Steem {

    @POST("/api/oauth2/token")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/oauth2/token")
    Call<LoginResponse> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    @GET("/get_discussions_by_created")
    Call<List<Discussion>> getDiscussions(@Query("query") String body);

    @GET("/get_content_replies")
    Call<List<Comment>> getComments(@Query("author") String author, @Query("permlink") String permLink);

    @POST("broadcast")
    Call<ResponseBody> vote(@Body VoteOperation body);

    @POST("broadcast")
    Call<ResponseBody> comment(@Body CommentOperation body);

    @GET("/get_content")
    Call<Discussion> getDiscussion(@Query("author") String author, @Query("permlink") String permLink);

    @GET("/get_active_votes")
    Call<List<VoteOperation.Vote>> getActivevotes(@Query("author") String author, @Query("permlink") String permlink);

    @GET("get_follow_count")
    Observable<FollowCount> getFollowCount(@Query("account") String name);

    @GET("get_followers")
    Observable<ArrayList<FollowModel>> getFollowers(@Query("following") String name, @Query("startFollower") String f, @Query("followType") String type, @Query("limit") int limit);

    @GET("get_following")
    Observable<ArrayList<FollowModel>> getFollowing(@Query("follower") String name, @Query("startFollowing") String f, @Query("followType") String type, @Query("limit") int limit);


    @GET(".")
    Observable<ArrayList<MentionModel>> getMentions();


    @GET("get_account_history")
    Observable<JsonArray> getAccountHistory(@Query("account") String account, @Query("from") int from, @Query("limit") int limit);

    @GET("/get_discussions_by_comments")
    Observable<ArrayList<CommentModel>> getAllComments(@Query("query") JsonObject author);
}
