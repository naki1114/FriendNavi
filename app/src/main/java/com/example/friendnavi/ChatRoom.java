package com.example.friendnavi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoom extends AppCompatActivity {

    String TAG = "채팅방 페이지";

    Socket socket;
    InetAddress serverAddr;
    PrintWriter sendWriter;

    ServiceAPI chattingAPI;

    ImageButton btnBack;
    ImageButton btnOption;
    ImageButton btnLocate;
    ImageButton btnSend;
    ImageButton btnSend2;

    TextView roomNameView;

    RecyclerView chattingView;

    ArrayList<ChattingData> chattingList;
    ChattingAdapter chattingAdapter;

    EditText chat;

    private Handler chatHandler;

    private final int PORT = 8000;
    private final String IP = "13.125.189.5";

    String sendmsg;
    String read;

    String roomNumber;
    String roomName;
    String roomType;

    boolean type;

    String myNick;

    SharedPreferences getNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Log.v(TAG, "onCreate 호출");

        initView();
        getRoomData();
        initRetrofit();
        initChattingView();
        getUserNickname();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart 호출");

        setSocket();
        clickBtnSend();
        clickBtnBack();
        changeButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume 호출");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause 호출");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart 호출");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop 호출");

        closeSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy 호출");
    }

    // Custom Method

    public void initView() {
        btnBack = findViewById(R.id.btnBack);
        btnOption = findViewById(R.id.btnOption);
        btnLocate = findViewById(R.id.btnLocate);
        btnSend = findViewById(R.id.btnSend);
        btnSend2 = findViewById(R.id.btnSend2);

        roomNameView = findViewById(R.id.chatRoomTitle);

        chat = findViewById(R.id.chat);

        chatHandler = new Handler();
    }

    public void initChattingView() {
        chattingView = findViewById(R.id.chattingView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        chattingView.setLayoutManager(mLinearLayoutManager);

        chattingList = new ArrayList<>();

        chattingAdapter = new ChattingAdapter(chattingList);
        chattingView.setAdapter(chattingAdapter);

        chattingAPI.getChattingData(roomNumber).enqueue(new Callback<ArrayList<ChattingDataFromServer>>() {
            @Override
            public void onResponse(Call<ArrayList<ChattingDataFromServer>> call, Response<ArrayList<ChattingDataFromServer>> response) {
                ArrayList<ChattingDataFromServer> result = new ArrayList<>();
                result = response.body();
                if (result.size() > 0) {
                    for (int i = 0; i < result.size(); i++) {
                        String[] time = result.get(i).getSavedTime().split(" ");
                        String getTime = time[1] + time[2];
                        String getMsg = result.get(i).getSavedContent();
                        boolean getType;

                        if (result.get(i).getSendUser().equals(myNick)) {
                            getType = false;
                        }
                        else {
                            getType = true;
                        }

                        ChattingData chattingData = new ChattingData(getTime, getMsg, getType);
                        chattingList.add(chattingData);
                        chattingAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ChattingDataFromServer>> call, Throwable t) {
                Log.v(TAG, "naki : 실패");
            }
        });
    }

    public void initRetrofit() {
        chattingAPI = RetrofitClient.getClient().create(ServiceAPI.class);
    }

    public void getUserNickname() {
        getNickname = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        myNick = getNickname.getString("Nickname", "");
    }

    public void getRoomData() {
        Intent getData = getIntent();

        roomType = getData.getStringExtra("roomType");

        if (roomType.equals("chatting")) {
            roomName = getData.getStringExtra("roomName");
        }
        else if (roomType.equals("friend")) {
            roomName = getData.getStringExtra("friend");
        }
        roomNumber = getData.getStringExtra("roomNumber");

        roomNameView.setText(roomName);
    }

    public void setSocket() {
        new Thread() {
            public void run() {
                try {
                    serverAddr = InetAddress.getByName(IP);
                    socket = new Socket(serverAddr, PORT);
                    sendWriter = new PrintWriter(socket.getOutputStream());
//                    sendWriter.println(roomNumber);
                    sendWriter.println(roomName);
                    sendWriter.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true) {
                        read = input.readLine();

                        if (read != null) {
                            chatHandler.post(new MsgUpdate(read));
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void clickBtnSend() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmsg = chat.getText().toString();
                saveChatContent();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
//                            sendWriter.println(roomNumber + "!!!###@@@!!!" + sendmsg);
                            sendWriter.println(roomName + "!!!###@@@!!!" + myNick + "!!!###@@@!!!" + sendmsg);
                            sendWriter.flush();
                            chat.setText("");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                btnSend2.setVisibility(View.VISIBLE);
                btnSend.setVisibility(View.GONE);
            }
        });
    }

    public void closeSocket() {
        try {
            sendWriter.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MsgUpdate implements Runnable {
        private String msg;

        public MsgUpdate(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            setChatting(msg);
        }
    }

    public void clickBtnBack() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMain();
            }
        });
    }

    public void toMain() {
        Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(toMainActivity);
        finish();
    }

    public void saveChatContent() {
        SimpleDateFormat dataFormat = new SimpleDateFormat();

        String currentTime = dataFormat.format(System.currentTimeMillis());
        chattingAPI.saveChatting(roomNumber, currentTime, myNick, sendmsg).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.v(TAG, "성공 : " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v(TAG, "실패 : " + t.getMessage());
            }
        });
    }

    public void setChatting(String msg) {
        SimpleDateFormat dataFormat = new SimpleDateFormat();

        String[] time = dataFormat.format(System.currentTimeMillis()).split(" ");
        String currentTime = time[1] + " " + time[2];

        String[] content = msg.split("!!!###@@@!!!");

        if (content[0].equals(myNick)) {
            type = false;
        }
        else {
            type = true;
        }

        ChattingData chattingData = new ChattingData(currentTime, content[1], type);
        chattingList.add(chattingData);
        chattingAdapter.notifyDataSetChanged();
    }

    public void changeButton() {
        chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (chat.getText().toString().equals("")) {
                    btnSend.setVisibility(View.GONE);
                    btnSend2.setVisibility(View.VISIBLE);
                }
                else {
                    btnSend2.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}