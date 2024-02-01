package com.sehmusacar.AcarSMSDetector.Adapter;

import static com.sehmusacar.AcarSMSDetector.SmsBroadcastReceiver.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.sehmusacar.AcarSMSDetector.Utils.MsgData;
import com.sehmusacar.AcarSMSDetector.MsgDetailsActivity;
import com.example.smsspamdetector.R;

import java.util.ArrayList;

public class MsgListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private ArrayList<MsgData> msgData;

    public MsgListAdapter(ArrayList<MsgData> msgData, Activity activity){
        this.msgData = msgData;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new MsgViewHolder(layoutInflater.inflate(R.layout.single_msg_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MsgViewHolder)
            setMsgData((MsgViewHolder)holder,position);

    }

    private void setMsgData(MsgViewHolder holder, int position) {

        String name = getContactName(msgData.get(position).getServiceNum(),activity.getApplicationContext());

        if (name==null)
            name = msgData.get(position).getMsgTitle();

        holder.msgTitle.setText(name);

        TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(name.charAt(0)),Color.GRAY);

        holder.msgImage.setImageDrawable(drawable);

        holder.msgBody.setText(msgData.get(position).getMsgBody());
        holder.msgDate.setText(msgData.get(position).getMsgDate());

        Log.e(TAG, "setMsgData: "+msgData.get(position).getMsgDate());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(activity, MsgDetailsActivity.class);
                i.putExtra("title",msgData.get(position).getMsgTitle());
                i.putExtra("spam",msgData.get(position).getSpam());
                i.putExtra("id",msgData.get(position).getId());
                activity.startActivity(i);

            }
        });

    }

    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName=null;
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    @Override
    public int getItemCount() {
        return msgData.size();
    }

    public static class MsgViewHolder extends RecyclerView.ViewHolder{

        ImageView msgImage;
        TextView msgTitle,msgDate,msgBody;
        CardView layout;

        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);

            msgImage = itemView.findViewById(R.id.msg_image);
            msgTitle = itemView.findViewById(R.id.msg_title);
            msgBody = itemView.findViewById(R.id.msg_body);
            msgDate = itemView.findViewById(R.id.msg_date);
            layout = itemView.findViewById(R.id.msg_card_layout);

        }

    }
}
