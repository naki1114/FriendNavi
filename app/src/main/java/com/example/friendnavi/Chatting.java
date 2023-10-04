package com.example.friendnavi;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chatting extends Fragment {

    String TAG = "F_채팅 페이지";
    private final String CHECKING = "checking";

    Context context;

    ServiceAPI checkChatRoom;

    String roomNo;
    String nickname;

    RecyclerView roomListView;
    private ArrayList<RoomListData> roomList;
    private RoomListAdapter roomListAdapter;

    int count = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(TAG, "onAttach()");

        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        clickAddChatRoom(view);
        initRoomList(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "onViewCreated()");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach()");
    }

    // Custom Method

    public void initRoomList(View view) {
        roomListView = (RecyclerView) view.findViewById(R.id.roomListView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
        roomListView.setLayoutManager(mLinearLayoutManager);

        roomList = new ArrayList<>();

        roomListAdapter = new RoomListAdapter(roomList);
        roomListView.setAdapter(roomListAdapter);
    }

    // onClick Method

    public void clickAddChatRoom(View view) {
        ImageButton btnAddRoom;

        btnAddRoom = view.findViewById(R.id.btnAddRoom);

        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChatRoom();
            }
        });
    }

    public void addChatRoom() {
        nickname = "나킈";
        checkChatRoom = RetrofitClient.getClient().create(ServiceAPI.class);
        checkChatRoom.addRoom(CHECKING, nickname).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                roomNo = response.body();
                Log.v(TAG, roomNo);
                addRoomItem(roomNo, "room" + count, "내용", "시간");
                count++;
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v(TAG, "실패 : " + t.getMessage());
            }
        });
    }

    public void addRoomItem(String roomNumber, String roomName, String content, String time) {
        RoomListData roomData = new RoomListData(roomNumber, roomName, content, time);
        roomList.add(roomData);
        roomListAdapter.notifyDataSetChanged();
    }

}
