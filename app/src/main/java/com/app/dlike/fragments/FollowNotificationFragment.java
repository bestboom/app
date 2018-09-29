package com.app.dlike.fragments;


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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.dlike.R;
import com.app.dlike.Tools;
import com.app.dlike.activities.NotificationActivity;
import com.app.dlike.adapters.FollowAdapter;
import com.app.dlike.http.RetrofitClient;
import com.app.dlike.models.FollowCount;
import com.app.dlike.models.FollowModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowNotificationFragment extends Fragment {

    String TAG = "FollowFrag";
    NotificationActivity notificationActivity;
    View v;
    RecyclerView rv, rvv;
    ArrayList<FollowModel> followers, following;
    FollowAdapter adapter, adapterr;
    TextView followersTxt, followingsTxt;
    RetrofitClient retrofit;
    ProgressBar pb, pb_sub;
    LinearLayout main;
    Button switch_layout;
    String start;
    boolean isFollower = true;
    SwipeRefreshLayout swipe;
    public FollowNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationActivity = (NotificationActivity)getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_follow_notification, container, false);
        rv = v.findViewById(R.id.rv);
        rvv = v.findViewById(R.id.rvv);
        swipe = v.findViewById(R.id.swipeRefreshLayout);
        following = new ArrayList<>();
        followers = new ArrayList<>();
        populateFollowersList();
        populateFollowingList();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateFollowersList();
                populateFollowingList();
                getFollowersAndFollowingCount();
                getFollowers(Tools.DEFAULT_START);
                getFollowing(Tools.DEFAULT_START);
            }
        });
        swipe.setRefreshing(true);
        init();
        return v;
    }



    private void init(){
        retrofit = new RetrofitClient(getContext(), Tools.STEEM_API_BASE_URL);
        followersTxt = v.findViewById(R.id.followers);
        followingsTxt = v.findViewById(R.id.following);
        main = v.findViewById(R.id.main_layout);
        switch_layout = v.findViewById(R.id.switch_btn);

        listeners();
        Tools.showToast(getContext(), Tools.getUsername(getActivity()));
        getFollowersAndFollowingCount();
//        getFollowers();
//        getFollowing();
        getFollowing(Tools.DEFAULT_START);
        getFollowers( Tools.DEFAULT_START);
    }


    private void getFollowersAndFollowingCount() {
        retrofit.getSteemService().getFollowCount(Tools.getUsername(getContext()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FollowCount>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(FollowCount followCount) {
                        Tools.log(TAG, followCount.toString());
                        followersTxt.setText(String.valueOf(followCount.getFollowers()));
                        followingsTxt.setText(String.valueOf(followCount.getFollowing()));
                    }
                });
    }

    private void getFollowing(String start){
        swipe.setRefreshing(true);
        retrofit.getSteemService().getFollowing(Tools.getUsername(getContext()), start, Tools.BLOG, Tools.FETCH_LIMIT_FOLLOW)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FollowModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<FollowModel> response) {
                        Tools.log(TAG, response.toString());
                        following.addAll(response);
                        adapterr.addFollow(following);
                        swipe.setRefreshing(false);
                    }
                });
    }
    private void getFollowers(String start){
        swipe.setRefreshing(true);
        //main.setVisibility(View.GONE);
        retrofit.getSteemService().getFollowers(Tools.getUsername(getContext()), start, Tools.BLOG, Tools.FETCH_LIMIT_FOLLOW)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FollowModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Tools.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<FollowModel> response) {
                        Tools.log(TAG, response.toString());
                        //populateList();
                        followers.addAll(response);
                        adapter.addFollow(new ArrayList<>(response.subList(1, response.size())));
                        swipe.setRefreshing(false);

                    }
                });
    }
    private void populateFollowersList(){
        //swipe.setRefreshing(false);
       // main.setVisibility(View.VISIBLE);
        rv.setVisibility(View.VISIBLE);
        adapter = new FollowAdapter(getContext() , true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),llm.getOrientation()));
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    getFollowers(followers.get(followers.size() - 1).getFollower());
                }
            }
        });
    }

    private void populateFollowingList(){
        adapterr = new FollowAdapter(getContext(), false);
        LinearLayoutManager llmm = new LinearLayoutManager(getActivity());
        rvv.setLayoutManager(llmm);
        rvv.setItemAnimator(new DefaultItemAnimator());
        rvv.addItemDecoration(new DividerItemDecoration(rvv.getContext(),llmm.getOrientation()));
        rvv.setAdapter(adapterr);

        rvv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    getFollowing(following.get(following.size() - 1).getFollowing());
                }
            }
        });
    }

    private void listeners() {
        switch_layout.setOnClickListener(v -> {
            isFollower = !isFollower;
            if(isFollower){
                rv.setVisibility(View.VISIBLE);
                rvv.setVisibility(View.GONE);
                switch_layout.setText("Switch to Following");
            }else{
                rv.setVisibility(View.GONE);
                rvv.setVisibility(View.VISIBLE);
                switch_layout.setText("Switch to Followers");
            }
        });
    }

}
