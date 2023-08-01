package com.example.friendnavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    String TAG = "로그인 페이지";

    TextView inputID;
    TextView inputPW;

    Button btnLogin;
    Button btnJoin;

    ImageButton btnKakao;

    ServiceAPI transUserInfo;

    String getID;
    String getPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                updateKakaoLoginUi();
                toMain();
                return null;
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUserInfo();
                checkUser(getID, getPW);
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toJoin();
            }
        });

        btnKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(Login.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(Login.this, callback);
                }
                else {
                    UserApiClient.getInstance().loginWithKakaoAccount(Login.this, callback);
                }
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
        inputPW = findViewById(R.id.inputPW);

        btnLogin = findViewById(R.id.btnLogin);
        btnJoin = findViewById(R.id.btnJoin);

        btnKakao = findViewById(R.id.btnKakao);

        transUserInfo = RetrofitClient.getClient().create(ServiceAPI.class);
    }

    public void toJoin() {
        Intent toJoinActivity = new Intent(getApplicationContext(), Join.class);
        startActivity(toJoinActivity);
        finish();
    }

    public void toMain() {
        Intent toMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(toMainActivity);
        finish();
    }

    public void setUserInfo() {
        getID = inputID.getText().toString();
        getPW = inputPW.getText().toString();
    }

    private void checkUser(String id, String password) {
        transUserInfo.userLogin(id, password).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                if(result.equals("로그인 되었습니다.")) {
                    toMain();
                }
                else {
                    Toast.makeText(Login.this, result, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Login.this, "Sign up Error", Toast.LENGTH_SHORT).show();
                Log.e("Sign up Error", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    Log.d(TAG, "id : " + user.getId());
                    Log.d(TAG, "nickname : " + user.getKakaoAccount().getProfile().getNickname());
                    Log.d(TAG, "profile : " + user.getKakaoAccount().getProfile().getProfileImageUrl());
                }
                else {
                    Log.d(TAG, "로그인이 되어있지 않습니다.");
                }
                return null;
            }
        });
    }

}