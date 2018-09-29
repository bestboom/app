package com.app.dlike.fragments;

import android.content.Intent;
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
import com.app.dlike.adapters.ProCommentAdapter;
import com.app.dlike.adapters.RecyclerItemClickListener;
import com.app.dlike.adapters.TransferAdapter;
import com.app.dlike.http.RetrofitClient;
import com.app.dlike.models.CommentModel;
import com.app.dlike.models.TransferModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class TransferNotificationFragment extends Fragment {
    String TAG = "TRANSFER";

    private View v;
    RetrofitClient retrofitClient;

    RecyclerView rv;
    SwipeRefreshLayout swipe;

    ArrayList<TransferModel> transfers;
    TransferAdapter adapter;
    int fetch = 1000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_transfer_notification, container, false);
        init();
        return v;
    }

    private void init(){
        swipe = v.findViewById(R.id.swipeRefreshLayout);
        swipe.setRefreshing(true);
        swipe.setOnRefreshListener(this::populateTransfer);
        rv = v.findViewById(R.id.rv);
        retrofitClient = new RetrofitClient(getActivity(), Tools.STEEM_API_BASE_URL);
        transfers = new ArrayList<>();
        populateTransfer();
    }
    private void getTransfer() {
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
                        //Tools.log(TAG, jsonArray.toString());
                        fetch  = fetch + 1000;
                        ArrayList<TransferModel> temp = TransferModel.sorter(jsonArray);
                        if(jsonArray.size() == 0){
                            getTransfer();
                        }else{
                            transfers = adapter.addTransfer(temp);
                            swipe.setRefreshing(false);
                        }


                    }
                });
        //JsonArray temp = new Gson().fromJson(Tools.accountHistory, JsonArray.class);
        //upvotes = UpVoteModel.sorter(temp);
    }

    void populateTransfer(){
        adapter = new TransferAdapter(getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),llm.getOrientation()));
        rv.setAdapter(adapter);
        getTransfer();

//        rv.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rv, new RecyclerItemClickListener.ClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Tools.showToast(getContext(), position + "clicked");
//                Tools.log(TAG, comments.get(position).toString());
//                getDiscussion(comments.get(position));
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    getTransfer();
                }
            }
        });
    }

//
//    private void getDiscussion(CommentModel commentModel){
//        startActivity(new Intent(getContext(), ViewPostActivity.class).putExtra("comment", new Gson().toJson(commentModel)));
//    }
}
