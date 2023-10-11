package com.example.friendnavi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    private ArrayList<FriendListData> friendList;

    public class FriendListViewHolder extends RecyclerView.ViewHolder {

        protected ImageView profileView;
        protected TextView nicknameView;

        String myNickname;
        String friendNickname;
        String roomNumber;

        public FriendListViewHolder(Context context, View view) {
            super(view);

            this.profileView = view.findViewById(R.id.profileView);
            this.nicknameView = view.findViewById(R.id.nicknameView);

            myNickname = getUserNickname(context);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendNickname = nicknameView.getText().toString();
                    addRoom(context);
                }
            });
        }

        public void toChatRoomActivity(Context context) {
            Intent toChatRoom = new Intent(context, ChatRoom.class);
            toChatRoom.putExtra("roomType", "friend");
            toChatRoom.putExtra("friend", friendNickname);
            toChatRoom.putExtra("roomNumber", roomNumber);
            context.startActivity(toChatRoom);
        }

        public void addRoom(Context context) {
            ServiceAPI getRoomNumber = RetrofitClient.getClient().create(ServiceAPI.class);

            getRoomNumber.addRoom(myNickname + "▒▒▒" + friendNickname).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    roomNumber = response.body();

                    toChatRoomActivity(context);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.v("FriendListAdapter", "실패");
                }
            });
        }

        public String getUserNickname(Context context) {
            SharedPreferences getNickname = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);

            String myNick = getNickname.getString("Nickname", "");

            return myNick;
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
