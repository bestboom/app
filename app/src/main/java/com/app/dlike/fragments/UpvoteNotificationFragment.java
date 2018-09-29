package com.app.dlike.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.app.dlike.activities.ViewPostActivity;
import com.app.dlike.adapters.MentionsAdapter;
import com.app.dlike.adapters.RecyclerItemClickListener;
import com.app.dlike.adapters.UpvoteAdapter;
import com.app.dlike.http.RetrofitClient;
import com.app.dlike.models.UpVoteModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.lang.reflect.Array;
import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpvoteNotificationFragment extends Fragment {


    public UpvoteNotificationFragment() {
        // Required empty public constructor
    }

    View v;
    SwipeRefreshLayout swipe;
    RecyclerView rv;
    RetrofitClient retrofitClient;
    UpvoteAdapter adapter;
    ArrayList<UpVoteModel> upvotes;
    String TAG = "UPVOTES";
    Handler handler;
    int fetch = 1000;
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_upvote_notification, container, false);
        init();
        return v;
    }

    private void init(){
        swipe = v.findViewById(R.id.swipeRefreshLayout);
        swipe.setRefreshing(true);
        swipe.setOnRefreshListener(() -> {
            fetch = 500;
            getUpvotes();
        });
        rv = v.findViewById(R.id.rv);
        retrofitClient = new RetrofitClient(getActivity(), Tools.STEEM_API_BASE_URL);
        upvotes = new ArrayList<>();
        handler = new Handler();
        populateUpvotes();
    }


    private void getUpvotes(){
        swipe.setRefreshing(true);
        retrofitClient.getSteemService().getAccountHistory(Tools.getUsername(getContext()),-1, fetch)
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
                        fetch  = fetch + 1000;
                        Tools.log(TAG, jsonArray.toString());
                        ArrayList<UpVoteModel> temp = UpVoteModel.sorter(jsonArray, getContext());
                        handler.post(() -> {
                            if(upvotes.size() < 100)
                                getUpvotes();
                        });
                        upvotes = adapter.addUpvotes(temp);
                        swipe.setRefreshing(false);

                    }
                });
        //JsonArray temp = new Gson().fromJson(Tools.accountHistory, JsonArray.class);
        //upvotes = UpVoteModel.sorter(temp);
    }


    private void populateUpvotes(){
        adapter = new UpvoteAdapter(getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),llm.getOrientation()));
        rv.setAdapter(adapter);
        getUpvotes();

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rv, new RecyclerItemClickListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Tools.showToast(getContext(), position + "clicked");
                Tools.log(TAG, upvotes.get(position).toString());
                getDiscussion(upvotes.get(position));
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
                    getUpvotes();
                }
            }
        });
    }

    private void getDiscussion(UpVoteModel upVoteModel){
        startActivity(new Intent(getContext(), ViewPostActivity.class).putExtra("upvote", new Gson().toJson(upVoteModel)));
    }
}
