package com.example.friendnavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Join extends AppCompatActivity {

    String TAG = "회원가입 페이지";

    EditText inputID;
    EditText inputNickname;
    EditText inputPW;
    EditText inputPWRe;
    EditText inputPhone;
    EditText inputAddHome;
    EditText inputAddComp;

    Button btnConfirm;
    Button btnCancel;

    UserInfo transUserInfo;

    String getID;
    String getNickname;
    String getPW;
    String getPWRe;
    String getName;
    String getBirth;
    String getPhone;
    String getAddHome;
    String getAddComp;
    String getEmail;

    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Log.v(TAG, "onCreate 호출");

        initializing();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart 호출");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume 호출");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJoinData();
                startJoin(getID, getNickname, getPW, getPWRe, getPhone, getAddHome, getAddComp);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLogin();
            }
        });
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy 호출");
    }

    // Custom Method

    public void initializing() {
        inputID = findViewById(R.id.inputID);
        inputNickname = findViewById(R.id.inputNickname);
        inputPW = findViewById(R.id.inputPW);
        inputPWRe = findViewById(R.id.inputPWRe);
        inputPhone = findViewById(R.id.inputPhone);
        inputAddHome = findViewById(R.id.inputAddressHome);
        inputAddComp = findViewById(R.id.inputAddressCompany);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);

        transUserInfo = RetrofitClient.getClient().create(UserInfo.class);
    }

    public void toLogin() {
        Intent toLoginActivity = new Intent(getApplicationContext(), Login.class);
        startActivity(toLoginActivity);
        finish();
    }

    public void setJoinData() {
        getID = inputID.getText().toString();
        getNickname = inputNickname.getText().toString();
        getPW = inputPW.getText().toString();
        getPWRe = inputPWRe.getText().toString();
        getPhone = inputPhone.getText().toString();
        getAddHome = inputAddHome.getText().toString();
        getAddComp = inputAddComp.getText().toString();
    }

    private void startJoin(String id, String nickname, String password, String passwordRe, String phone, String addHome, String addComp) {
        transUserInfo.userJoin(id, nickname, password, passwordRe, phone, addHome, addComp).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                Log.v("확인",result);
                Toast.makeText(Join.this, result, Toast.LENGTH_SHORT).show();
                if(result.equals("회원가입이 완료되었습니다.")) {
                    toLogin();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Join.this, "Sign up Error", Toast.LENGTH_SHORT).show();
                Log.e("Sign up Error", t.getMessage());
                t.printStackTrace();
            }
        });
    }

}