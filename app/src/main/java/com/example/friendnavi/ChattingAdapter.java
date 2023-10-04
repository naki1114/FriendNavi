package com.example.friendnavi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChattingData> chattingList;

    public class MyChattingViewHolder extends RecyclerView.ViewHolder {

        protected TextView timeStampView;
        protected TextView msgView;

        public MyChattingViewHolder(Context context, View view) {
            super(view);

            timeStampView = view.findViewById(R.id.myTimeStampView);
            msgView = view.findViewById(R.id.myMsgView);
        }
    }

    public class OtherChattingViewHolder extends RecyclerView.ViewHolder {

        protected TextView timeStampView;
        protected TextView msgView;

        public OtherChattingViewHolder(Context context, View view) {
            super(view);

            timeStampView = view.findViewById(R.id.otherTimeStampView);
            msgView = view.findViewById(R.id.otherMsgView);
        }
    }

    public ChattingAdapter(ArrayList<ChattingData> chattingList) {
        this.chattingList = chattingList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.item_mychatting, viewGroup, false);
            MyChattingViewHolder viewHolder = new MyChattingViewHolder(context, view);
            return viewHolder;
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.item_otherchatting, viewGroup, false);
            OtherChattingViewHolder viewHolder = new OtherChattingViewHolder(context, view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (chattingList.get(position).getType() == false) {
            MyChattingViewHolder chattingViewHolder = (MyChattingViewHolder) viewHolder;
            chattingViewHolder.msgView.setText(chattingList.get(position).getMsg());
            chattingViewHolder.timeStampView.setText(chattingList.get(position).getTime());
        }
        else {
            OtherChattingViewHolder chattingViewHolder = (OtherChattingViewHolder) viewHolder;
            chattingViewHolder.msgView.setText(chattingList.get(position).getMsg());
            chattingViewHolder.timeStampView.setText(chattingList.get(position).getTime());
        }

    }

    @Override
    public int getItemCount() {
        return (null != chattingList ? chattingList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        ChattingData chattingData = chattingList.get(position);

        if (chattingData.getType() == false) {
            return 0;
        }
        else {
            return 1;
        }
    }

}
