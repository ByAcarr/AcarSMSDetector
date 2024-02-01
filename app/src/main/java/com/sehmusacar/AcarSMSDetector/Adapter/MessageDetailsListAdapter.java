package com.sehmusacar.AcarSMSDetector.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sehmusacar.AcarSMSDetector.Utils.MsgData;
import com.example.smsspamdetector.R;

import java.util.ArrayList;

public class MessageDetailsListAdapter extends RecyclerView.Adapter <MessageDetailsListAdapter.ViewHolder>{


    Context context;
    ArrayList<MsgData> msgData;

    public MessageDetailsListAdapter(Context context, ArrayList<MsgData> msgData){

        this.context = context;
        this.msgData = msgData;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.single_details_msg_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.messageTime.setText(msgData.get(position).getMsgDate());
        holder.messageData.setText(msgData.get(position).getMsgBody());
        if (msgData.get(position).getSpam()){

            holder.messageData.setTextColor(Color.WHITE);
            ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
            holder.messageData.setBackgroundTintList(blueColor);

        }

    }

    @Override
    public int getItemCount() {
        return msgData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView messageTime,messageData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageData = itemView.findViewById(R.id.msg_details_data);
            messageTime = itemView.findViewById(R.id.msg_details_date);

        }
    }
}
