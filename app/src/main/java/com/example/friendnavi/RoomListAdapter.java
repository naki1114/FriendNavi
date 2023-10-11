package com.example.friendnavi;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder> {

    private ArrayList<RoomListData> roomList;

    public class RoomListViewHolder extends RecyclerView.ViewHolder {

        protected ImageView profileView;
        protected TextView roomNameView;
        protected TextView contentView;
        protected TextView timeStampView;
        protected String roomNumber;
        protected String roomName;

        public RoomListViewHolder(Context context, View view) {
            super(view);

            this.profileView = view.findViewById(R.id.profileView);
            this.roomNameView = view.findViewById(R.id.roomNameView);
            this.contentView = view.findViewById(R.id.contentView);
            this.timeStampView = view.findViewById(R.id.timeStampView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toChatRoom = new Intent(context, ChatRoom.class);
                    toChatRoom.putExtra("roomNumber", roomNumber);
                    toChatRoom.putExtra("roomName", roomName);
                    toChatRoom.putExtra("roomType", "chatting");
                    context.startActivity(toChatRoom);
                }
            });
        }
    }

    public RoomListAdapter(ArrayList<RoomListData> roomList) {
        this.roomList = roomList;
    }

    @Override
    public RoomListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_room, viewGroup, false);
        RoomListViewHolder viewHolder = new RoomListViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListViewHolder viewHolder, int position) {

        viewHolder.profileView.setImageResource(R.drawable.profile);
        viewHolder.roomNameView.setText(roomList.get(position).getRoomName());
        viewHolder.contentView.setText(roomList.get(position).getContent());
        viewHolder.timeStampView.setText(roomList.get(position).getTime());
        viewHolder.roomNumber = roomList.get(position).getRoomNumber();
        viewHolder.roomName = roomList.get(position).getRoomName();

    }

    @Override
    public int getItemCount() {
        return (null != roomList ? roomList.size() : 0);
    }

}
