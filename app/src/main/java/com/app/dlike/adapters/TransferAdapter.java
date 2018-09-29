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
import com.app.dlike.models.CommentModel;
import com.app.dlike.models.TransferModel;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.util.ArrayList;
import java.util.Collections;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ViewHolder> {
    ArrayList<TransferModel> transfers = new ArrayList<>();
    Context context;
    public TransferAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.transer_rv_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransferModel model = transfers.get(position);
        holder.author.setText(model.getFrom());
        String str = "transferred <b>" + model.getAmount() + "</b> to you";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.word.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
            holder.body.setText(Html.fromHtml(model.getMemo(),  Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.word.setText(Html.fromHtml(str));
            holder.body.setText(Html.fromHtml(model.getMemo()));
        }
        holder.timestamp.setText(TimeAgo.using(Tools.convertDate(model.getTimestamp())));
        Tools.getImage(model.getFrom(), holder.avatar, model.getFrom(), true);
    }

    @Override
    public int getItemCount() {
        return transfers.size();
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

    public ArrayList<TransferModel> addTransfer(ArrayList<TransferModel> ff) {
        Collections.reverse(ff);
        if(transfers.size() == 0){
            transfers.addAll(ff);
            notifyDataSetChanged();
        }else{
            for (TransferModel model : ff) {
                if(!exists(model)){
                    this.transfers.add(model);
                    notifyItemInserted(transfers.size() - 1);
                }
            }
        }


        return this.transfers;
    }

    private boolean exists(TransferModel model){
        for(TransferModel d: transfers){
            if(model.getFrom().equalsIgnoreCase(d.getFrom()) &&
                    model.getTimestamp().equalsIgnoreCase(d.getTimestamp())){
                return true;
            }
        }
        return false;
    }
}
