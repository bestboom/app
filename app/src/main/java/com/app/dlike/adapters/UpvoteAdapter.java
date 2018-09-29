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
import com.app.dlike.models.FollowModel;
import com.app.dlike.models.UpVoteModel;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.util.ArrayList;
import java.util.Collections;

public class UpvoteAdapter extends RecyclerView.Adapter<UpvoteAdapter.ViewHolder> {

    ArrayList<UpVoteModel> upvotes = new ArrayList<>();
    Context context;
    public UpvoteAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.upvotes_rv, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UpVoteModel model = upvotes.get(position);
        String str = " voted for your post <b><i>" + model.getPermlink() + "</i></b>";
        holder.intro.setText(model.getVoter());
        holder.timestamp.setText(TimeAgo.using(Tools.convertDate(model.getTimestamp())));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.word.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.word.setText(Html.fromHtml(str));
        }
        Tools.getImage(model.getVoter(), holder.avatar, model.getAuthor(), true);
    }

    @Override
    public int getItemCount() {
        return upvotes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView intro, timestamp, word;
        ImageView avatar;
        public ViewHolder(View itemView) {
            super(itemView);
            intro = itemView.findViewById(R.id.intro);
            word = itemView.findViewById(R.id.word);
            timestamp = itemView.findViewById(R.id.timestamp);
            avatar = itemView.findViewById(R.id.avatar);

        }
    }

    public ArrayList<UpVoteModel> addUpvotes(ArrayList<UpVoteModel> ff) {
        Collections.reverse(ff);
        if(upvotes.size() == 0){
            upvotes.addAll(ff);
            notifyDataSetChanged();
        }else{
            for (UpVoteModel model : ff) {
                if(!exists(model)){
                    this.upvotes.add(model);
                    notifyItemInserted(this.upvotes.size() - 1);
                }
            }
        }


        return this.upvotes;
    }

    private boolean exists(UpVoteModel model){
        for(UpVoteModel d: upvotes){
            if(model.getVoter().equalsIgnoreCase(d.getVoter()) &&
                    model.getPermlink().equalsIgnoreCase(d.getPermlink())){
                return true;
            }
        }
        return false;
    }
}
