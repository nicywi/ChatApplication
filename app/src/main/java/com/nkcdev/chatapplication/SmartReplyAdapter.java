package com.nkcdev.chatapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SmartReplyAdapter extends RecyclerView.Adapter<SmartReplyAdapter.SmartReplyViewHolder> {

    private List<String> smartReplies;
    private OnSmartReplyClickListener listener;

    public SmartReplyAdapter(List<String> smartReplies, OnSmartReplyClickListener listener) {
        this.smartReplies = smartReplies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SmartReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_smart_reply, parent, false);
        return new SmartReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SmartReplyViewHolder holder, int position) {
        String reply = smartReplies.get(position);
        holder.buttonReply.setText(reply);
        holder.buttonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSmartReplyClicked(reply);
            }
        });
    }

    @Override
    public int getItemCount() {
        return smartReplies.size();
    }

    public void updateReplies(List<String> newReplies) {
        smartReplies.clear();
        smartReplies.addAll(newReplies);
        notifyDataSetChanged();
    }

    public static class SmartReplyViewHolder extends RecyclerView.ViewHolder {
        Button buttonReply;

        public SmartReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonReply = itemView.findViewById(R.id.buttonReply);
        }
    }

    public interface OnSmartReplyClickListener {
        void onSmartReplyClicked(String reply);
    }
}
