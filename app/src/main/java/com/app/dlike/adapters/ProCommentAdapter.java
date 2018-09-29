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
import com.app.dlike.models.Comment;
import com.app.dlike.models.CommentModel;
import com.app.dlike.models.UpVoteModel;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ProCommentAdapter extends   RecyclerView.Adapter<ProCommentAdapter.ViewHolder> {
    ArrayList<CommentModel> comments = new ArrayList<>();
    Context context;
    public ProCommentAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comment_rv_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentModel model = comments.get(position);
        holder.author.setText(model.getAuthor());
        String str = "You commented on a post <b><i>" + model.getParent_permlink() + "</i></b>";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.word.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
            holder.body.setText(Html.fromHtml(model.getBody(),  Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.word.setText(Html.fromHtml(str));
            holder.body.setText(Html.fromHtml(model.getBody()));
        }

        holder.timestamp.setText(TimeAgo.using(Tools.convertDate(model.getCreated())));
        Tools.getImage(model.getAuthor(), holder.avatar, model.getAuthor(), true);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView author, word, body, timestamp;
        ImageView avatar;
        ViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            word = itemView.findViewById(R.id.word);
            body = itemView.findViewById(R.id.body);
            timestamp = itemView.findViewById(R.id.timestamp);
            avatar = itemView.findViewById(R.id.avatar);

        }
    }

    public ArrayList<CommentModel> addComments(ArrayList<CommentModel> ff) {
        //Collections.reverse(ff);
        if(comments.size() == 0){
            comments.addAll(ff);
            notifyDataSetChanged();
        }else{
            for (CommentModel model : ff) {
                if(!exists(model)){
                    this.comments.add(model);
                    notifyItemInserted(comments.size() - 1);
                }
            }
        }


        return this.comments;
    }

    private boolean exists(CommentModel model){
        for(CommentModel d: comments){
            if(model.getAuthor().equalsIgnoreCase(d.getAuthor()) &&
                    model.getPermlink().equalsIgnoreCase(d.getPermlink())){
                return true;
            }
        }
        return false;
    }
}
