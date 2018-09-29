package com.app.dlike.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.dlike.R;
import com.app.dlike.Tools;
import com.app.dlike.activities.NotificationActivity;
import com.app.dlike.models.FollowModel;
import com.app.dlike.models.MentionModel;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.util.ArrayList;

public class MentionsAdapter extends RecyclerView.Adapter<MentionsAdapter.ViewHolder>{

    String TAG = "Metions Adapter";
    ArrayList<MentionModel> mentions;
    Context context;
    NotificationActivity notificationActivity;
    public MentionsAdapter(Context context, ArrayList<MentionModel> temp) {
        this.mentions = temp;
        this.context = context;
        notificationActivity = (NotificationActivity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mentions_rv_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MentionModel model = mentions.get(position);
        model.setTitle(model.getTitle().replace("@" + Tools.getUsername(context), "<strong>@" + Tools.getUsername(context) + "</strong>"));
        model.setBody(model.getBody().replace("@" + Tools.getUsername(context), "<strong>@" + Tools.getUsername(context) + "</strong>"));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.title.setText(Html.fromHtml(model.getTitle(), Html.FROM_HTML_MODE_LEGACY));
            holder.body.setText(Html.fromHtml(model.getBody(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.title.setText(Html.fromHtml(model.getTitle()));
            holder.body.setText(Html.fromHtml(model.getBody()));
        }
        holder.author.setText("Author: " + model.getAuthor());
        holder.timestamp.setText(TimeAgo.using(Tools.convertDate(model.getCreated())));
        //holder.avatar.setImageDrawable(Tools.getDrawable(follow.getFollower()));
        Tools.getImage(model.getAuthor(), holder.avatar, model.getAuthor(), true);
    }

    @Override
    public int getItemCount() {
        return mentions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, timestamp,body, author;
        ImageView avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.timestamp);
            body = itemView.findViewById(R.id.body);
            author = itemView.findViewById(R.id.author);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}


