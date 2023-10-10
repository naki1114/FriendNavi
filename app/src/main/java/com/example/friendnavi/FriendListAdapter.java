package com.example.friendnavi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    private ArrayList<FriendListData> friendList;

    public class FriendListViewHolder extends RecyclerView.ViewHolder {

        protected ImageView profileView;
        protected TextView nicknameView;

        public FriendListViewHolder(Context context, View view) {
            super(view);

            this.profileView = view.findViewById(R.id.profileView);
            this.nicknameView = view.findViewById(R.id.nicknameView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //
                }
            });
        }
    }

    public FriendListAdapter(ArrayList<FriendListData> friendList) {
        this.friendList = friendList;
    }

    @Override
    public FriendListAdapter.FriendListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend, viewGroup, false);
        FriendListAdapter.FriendListViewHolder viewHolder = new FriendListAdapter.FriendListViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.FriendListViewHolder viewHolder, int position) {

        viewHolder.profileView.setImageResource(R.drawable.profile);
        viewHolder.nicknameView.setText(friendList.get(position).getNickname());

    }

    @Override
    public int getItemCount() {
        return (null != friendList ? friendList.size() : 0);
    }

}
