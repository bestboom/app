package com.app.dlike.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.dlike.R;
import com.app.dlike.Tools;
import com.app.dlike.activities.NotificationActivity;
import com.app.dlike.activities.ViewPostActivity;
import com.app.dlike.adapters.FollowAdapter;
import com.app.dlike.adapters.MentionsAdapter;
import com.app.dlike.adapters.RecyclerItemClickListener;
import com.app.dlike.http.RetrofitClient;
import com.app.dlike.models.MentionModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.function.ToLongBiFunction;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MentionNotificationFragment extends Fragment {


    public MentionNotificationFragment() {
        // Required empty public constructor
    }

    String TAG = "Mentions";

    private View v;
    private NotificationActivity notificationActivity;
    RetrofitClient retrofitClient;

    RecyclerView rv;
    SwipeRefreshLayout swipe;

    ArrayList<MentionModel> mentions;
    MentionsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationActivity = (NotificationActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mention_notification, container, false);
        init();
        return v;
    }

    private void init() {
        rv = v.findViewById(R.id.rv);
        swipe = v.findViewById(R.id.swipeRefreshLayout);
        swipe.setRefreshing(true);
        mentions = new ArrayList<>();
        getMentions();
    }


    void getMentions(){
        retrofitClient = new RetrofitClient(getContext(), Tools.STEEM_PLUS_BASE_URL + Tools.getUsername(getContext()) + "/");
        retrofitClient.getSteemService().getMentions()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<MentionModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe.setRefreshing(false);
                        Tools.showToast(getContext(), e.toString());
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<MentionModel> response) {
                        Tools.log(TAG, mentions.toString());
                        for (MentionModel mention: response) {
                            if(mention.getBody().contains("@" + Tools.getUsername(getContext()).trim())){
                                mentions.add(mention);
                            }
                        }
                        populateList();
                        swipe.setRefreshing(false);
                    }
                });

    }


    void populateList(){
        rv.setVisibility(View.VISIBLE);
        adapter = new MentionsAdapter(getContext(),  mentions);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),llm.getOrientation()));
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rv, new RecyclerItemClickListener.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Tools.showToast(getContext(), position + "clicked");
                Tools.log(TAG, mentions.get(position).toString());
                startActivity(new Intent(getContext(), ViewPostActivity.class).putExtra("mentions", new Gson().toJson(mentions.get(position))));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));



    }
}

