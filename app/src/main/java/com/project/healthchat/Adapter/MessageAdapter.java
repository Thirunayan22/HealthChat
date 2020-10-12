package com.project.healthchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.healthchat.R;
import com.project.healthchat.model.ResponseMessage;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.CustomViewHolder> {
    List<ResponseMessage> responseMessages;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public CustomViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.tv_bot_message);
        }
    }

    public  MessageAdapter(List<ResponseMessage> responseMessages,Context context){
        this.responseMessages =  responseMessages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(responseMessages.get(position).isMe()){
            return R.layout.message_sent_bubble;
        }
        return R.layout.message_bot_bubble;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.textView.setText(responseMessages.get(position).getText());

    }

    @Override
    public int getItemCount() {
        return responseMessages.size();
    }


}
