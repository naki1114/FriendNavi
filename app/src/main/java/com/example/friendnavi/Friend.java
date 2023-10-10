package com.example.friendnavi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Friend extends Fragment {

    String TAG = "F_친구 페이지";

    Context context;

    ServiceAPI checkingFriend;

    RecyclerView friendListView;

    ImageButton btnAddFriend;
    ImageButton btnAddChatting;

    ArrayList<FriendListData> friendList;

    FriendListAdapter friendListAdapter;

    FriendDialog friendDialog;

    String result;
    String myNickname;

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

        getUserInfo();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        initView(view);
        initRetrofit();
        initFriendList(view);
        setFriend();
        clickAddFriend(view);
        clickAddChatting(view);

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

    // init

    public void initView(View view) {
        btnAddFriend = view.findViewById(R.id.btnAddFriend);
        btnAddChatting = view.findViewById(R.id.btnAddChatting);
    }

    public void initFriendList(View view) {
        friendListView = (RecyclerView) view.findViewById(R.id.friendListView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
        friendListView.setLayoutManager(mLinearLayoutManager);

        friendList = new ArrayList<>();

        friendListAdapter = new FriendListAdapter(friendList);
        friendListView.setAdapter(friendListAdapter);
    }

    public void initRetrofit() {
        checkingFriend = RetrofitClient.getClient().create(ServiceAPI.class);
    }

    public void getUserInfo() {
        SharedPreferences getInfo = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);

        myNickname = getInfo.getString("Nickname", "");
    }

    // onClick

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checkFriend();
        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            friendDialog.dismiss();
        }
    };

    public void checkFriend() {
        String friend = friendDialog.searchNickname.getText().toString();

        checkingFriend.checkFriend(myNickname, friend).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                result = response.body();
                if (result.equals("검색 결과가 없습니다.")) {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                }
                else if (result.equals("실패")) {
                    Toast.makeText(context, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (result.equals("이미 등록된 유저입니다.")) {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                }
                else if (result.equals("본인은 등록할 수 없습니다.")) {
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                }
                else {
                    addFriend();
                    friendDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v(TAG, "실패");
                t.printStackTrace();
            }
        });
    }

    public void addFriend() {
        FriendListData friendData = new FriendListData(result);
        friendList.add(friendData);
        friendListAdapter.notifyDataSetChanged();
    }

    public void setFriend() {
        checkingFriend.getFriendData(myNickname).enqueue(new Callback<ArrayList<FriendData>>() {
            @Override
            public void onResponse(Call<ArrayList<FriendData>> call, Response<ArrayList<FriendData>> response) {
                ArrayList<FriendData> result = response.body();
                Log.v(TAG, "성공naki");

                if (result.size() != 0) {
                    for (int i = 0; i < result.size(); i++) {
                        FriendListData friendData = new FriendListData(result.get(i).getFriendNickname());
                        friendList.add(friendData);
                        friendListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FriendData>> call, Throwable t) {
                Log.v(TAG, "실패naki");
            }
        });
    }

    public void clickAddFriend(View view) {
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendDialog = new FriendDialog(context, addListener, cancelListener);
                friendDialog.show();
            }
        });
    }

    public void clickAddChatting(View view) {
        btnAddChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
    }

}
