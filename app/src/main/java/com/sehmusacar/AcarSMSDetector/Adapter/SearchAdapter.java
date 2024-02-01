package com.sehmusacar.AcarSMSDetector.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smsspamdetector.R;
import com.sehmusacar.AcarSMSDetector.Utils.SearchData;

import java.util.ArrayList;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SApdap";
    ArrayList<SearchData> searchData;
    String searchText;
    Context context;

    public SearchAdapter(Context context, ArrayList<SearchData> searchData, String searchText) {
        this.searchData = searchData;
        this.searchText = searchText;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.e(TAG, "onCreateViewHolder: "+viewType );

        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.single_search_message, parent, false);
            return new MessageType(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.single_search_header, parent, false);

            return new HeaderType(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HeaderType){

            setHeaderMessage((HeaderType)holder,position);

        } else if(holder instanceof MessageType){

            setMessageData((MessageType)holder,position);

        }

    }

    private void setMessageData(MessageType holder, int position) {

        Log.e(TAG, "setMessageData: "+searchData.get(position).getMessage());

        holder.title.setText(searchData.get(position).getMessage());
        holder.msg.setText("");

        String fullText = searchData.get(position).getMessage();
        if (searchText != null && !searchText.isEmpty()) {
            int startPos = fullText.toLowerCase(Locale.US).indexOf(searchText.toLowerCase(Locale.US));
            int endPos = startPos + searchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(fullText);
                ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.title.setText(spannable);
            } else {
                holder.title.setText(fullText);
            }
        } else {
            holder.title.setText(fullText);
        }

    }

    private void setHeaderMessage(HeaderType holder, int position) {

        holder.title.setText(searchData.get(position).getMessage());
        holder.msg.setText("");

        String fullText = searchData.get(position).getMessage();
        if (searchText != null && !searchText.isEmpty()) {
            int startPos = fullText.toLowerCase(Locale.US).indexOf(searchText.toLowerCase(Locale.US));
            int endPos = startPos + searchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(fullText);
                ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.title.setText(spannable);
            } else {
                holder.title.setText(fullText);
            }
        } else {
            holder.title.setText(fullText);
        }

    }

    

    @Override
    public int getItemCount() {
        return searchData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return searchData.get(position).getType();
    }

    private class HeaderType extends RecyclerView.ViewHolder {
        TextView title,msg;
        public HeaderType(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_search);
            msg = itemView.findViewById(R.id.total_message);

        }
    }

    private class MessageType extends RecyclerView.ViewHolder {
        TextView title, msg;
        public MessageType(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.search_title);
            msg = itemView.findViewById(R.id.search_msg);

        }
    }

    public void UpdateList(ArrayList<SearchData> searchData){
        this.searchData.clear();
        this.searchData.addAll(searchData);
        notifyDataSetChanged();
    }
}
