package com.app.dlike.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.dlike.R;
import com.app.dlike.Tools;
import com.app.dlike.activities.NotificationActivity;
import com.app.dlike.activities.ViewPostActivity;
import com.app.dlike.adapters.CommentsAdapter;
import com.app.dlike.adapters.MentionsAdapter;
import com.app.dlike.adapters.ProCommentAdapter;
import com.app.dlike.adapters.RecyclerItemClickListener;
import com.app.dlike.adapters.UpvoteAdapter;
import com.app.dlike.http.RetrofitClient;
import com.app.dlike.models.Comment;
import com.app.dlike.models.CommentModel;
import com.app.dlike.models.MentionModel;
import com.app.dlike.models.UpVoteModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */
public class CommentNotificationFragment extends Fragment {

    // TODO: Rename and change types of parameters



    public CommentNotificationFragment() {
        // Required empty public constructor
    }

    String TAG = "Comments";

    private View v;
    private NotificationActivity notificationActivity;
    RetrofitClient retrofitClient;

    RecyclerView rv;
    SwipeRefreshLayout swipe;

    ArrayList<CommentModel> comments;
    ProCommentAdapter adapter;
    int fetch = 100;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_comment_notification, container, false);
        init();
        return v;
    }

    private void init(){
        swipe = v.findViewById(R.id.swipeRefreshLayout);
        swipe.setRefreshing(true);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                fetch = 100;
                getComment();
            }
        });
        rv = v.findViewById(R.id.rv);
        retrofitClient = new RetrofitClient(getActivity(), Tools.STEEM_API_BASE_URL);
        comments = new ArrayList<>();
        populateComments();
    }
    private void getComment() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("start_author", Tools.getUsername(getContext()));
        jsonObject.addProperty("start_permlink", "");
        jsonObject.addProperty("limit", fetch);

       // Tools.log(TAG, jsonObject.toString());
        //Tools.log(TAG, jsonObject.size() + "");
        retrofitClient.getSteemService().getAllComments(jsonObject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<CommentModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<CommentModel> jsonArray) {
                        //Tools.log(TAG, jsonArray.toString());
                        fetch  = fetch + 100;
                        //ArrayList<CommentModel> temp = CommentModel.sorter(jsonArray, getContext());
                        if(jsonArray.size() == 0){
                            getComment();
                        }else{
                            comments = adapter.addComments(jsonArray);
                            swipe.setRefreshing(false);
                        }


                    }
                });
        //JsonArray temp = new Gson().fromJson(Tools.accountHistory, JsonArray.class);
        //upvotes = UpVoteModel.sorter(temp);
    }

    void populateComments(){
        adapter = new ProCommentAdapter(getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),llm.getOrientation()));
        rv.setAdapter(adapter);
        getComment();

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rv, new RecyclerItemClickListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Tools.showToast(getContext(), position + "clicked");
                Tools.log(TAG, comments.get(position).toString());
                getDiscussion(comments.get(position));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    getComment();
                }
            }
        });
    }


    private void getDiscussion(CommentModel commentModel){
        startActivity(new Intent(getContext(), ViewPostActivity.class).putExtra("comment", new Gson().toJson(commentModel)));
    }
}
