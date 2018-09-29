package com.app.dlike.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.dlike.R;
import com.app.dlike.Tools;
import com.app.dlike.activities.NotificationActivity;
import com.app.dlike.models.Discussion;
import com.app.dlike.models.FollowModel;

import java.util.ArrayList;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    String TAG = "Service Adapter";
    ArrayList<FollowModel> follows = new ArrayList<>();
    Context context;
    NotificationActivity notificationActivity;
    boolean isFollowers;
    public FollowAdapter(Context context, boolean isFollowers) {
        this.context = context;
        notificationActivity = (NotificationActivity) context;
        this.isFollowers = isFollowers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.follow_rv_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowModel follow = follows.get(position);
        holder.name.setText(isFollowers ? follow.getFollower() : follow.getFollowing());
        String txt = isFollowers ? "Followed You" : "Is Followed By You";
        holder.ff.setText(txt);
        //holder.avatar.setImageDrawable(Tools.getDrawable(follow.getFollower()));
        Tools.getImage(follow.getFollower(), holder.avatar, follow.getFollower(), true);
    }

    @Override
    public int getItemCount() {
        return follows.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, ff;
        ImageView avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            avatar = itemView.findViewById(R.id.avatar);
            ff = itemView.findViewById(R.id.ff);

            itemView.setOnClickListener(v -> {
                Tools.showToast(context, getAdapterPosition() + "clicked");
            });
        }
    }

    public void addFollow(ArrayList<FollowModel> ff) {
        for (FollowModel followModel : ff) {
            if(!exists(followModel)){
                this.follows.add(followModel);
                notifyItemInserted(this.follows.size() - 1);
            }
        }
    }

    private boolean exists(FollowModel followModel){
        for(FollowModel d: follows){
            if(followModel.getFollower().equalsIgnoreCase(d.getFollower()) &&
                    followModel.getFollowing().equalsIgnoreCase(d.getFollowing())){
                return true;
            }
        }
        return false;
    }
}
