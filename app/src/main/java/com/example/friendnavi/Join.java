package com.example.friendnavi;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    TextView checkIDView;
    TextView checkNicknameView;
    TextView checkPasswordView;
    TextView checkPasswordReView;

    TextView duplicationCheckIDView;
    TextView duplicationCheckNicknameView;

    Button btnCheckID;
    Button btnCheckNickname;

    Button btnConfirm;
    Button btnCancel;

    ServiceAPI transUserInfo;

    String getID;
    String getNickname;
    String getPW;
    String getPWRe;
    String getPhone;

    int confirmID = 0;
    int confirmNickname = 0;

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

        inputID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                duplicationCheckIDView.setTextColor(Color.parseColor("#FF0000"));
                duplicationCheckIDView.setText("중복 검사를 해주세요.");

                if (checkID(inputID.getText().toString()) == true) {
                    checkIDView.setTextColor(Color.parseColor("#0000FF"));
                }
                else {
                    checkIDView.setTextColor(Color.parseColor("#FF0000"));
                }
                confirmID = 0;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                duplicationCheckNicknameView.setTextColor(Color.parseColor("#FF0000"));
                duplicationCheckNicknameView.setText("중복 검사를 해주세요.");

                if (checkNickname(inputNickname.getText().toString()) == true) {
                    checkNicknameView.setTextColor(Color.parseColor("#0000FF"));
                }
                else {
                    checkNicknameView.setTextColor(Color.parseColor("#FF0000"));
                }
                confirmNickname = 0;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputPWRe.getText().toString().equals(inputPW.getText().toString())) {
                    checkPasswordReView.setTextColor(Color.parseColor("#0000FF"));
                }
                else {
                    checkPasswordReView.setTextColor(Color.parseColor("#FF0000"));
                }

                if (checkPW(inputPW.getText().toString()) == true) {
                    checkPasswordView.setTextColor(Color.parseColor("#0000FF"));
                }
                else {
                    checkPasswordView.setTextColor(Color.parseColor("#FF0000"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputPWRe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputPWRe.getText().toString().equals(inputPW.getText().toString())) {
                    checkPasswordReView.setTextColor(Color.parseColor("#0000FF"));
                }
                else {
                    checkPasswordReView.setTextColor(Color.parseColor("#FF0000"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputPhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (inputPhone.getText().toString().length() == 3) {
                    inputPhone.setText(inputPhone.getText().toString() + "-");
                    inputPhone.setSelection(inputPhone.getText().length());
                }
                else if (inputPhone.getText().toString().length() == 8) {
                    inputPhone.setText(inputPhone.getText().toString() + "-");
                    inputPhone.setSelection(inputPhone.getText().length());
                }
            }

        });

        btnCheckID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJoinData();
                duplicationCheckID(getID);
            }
        });

        btnCheckNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJoinData();
                duplicationCheckNickname(getNickname);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmID == 1 && confirmNickname == 1) {
                    setJoinData();
                    startJoin(getID, getNickname, getPW, getPWRe, getPhone);
                }
                else {
                    Toast.makeText(Join.this, "중복 검사를 확인 해주세요.", Toast.LENGTH_SHORT).show();
                }
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

        checkIDView = findViewById(R.id.checkIDView);
        checkNicknameView = findViewById(R.id.checkNicknameView);
        checkPasswordView = findViewById(R.id.checkPasswordView);
        checkPasswordReView = findViewById(R.id.checkPasswordReView);

        duplicationCheckIDView = findViewById(R.id.duplicationCheckIDView);
        duplicationCheckNicknameView = findViewById(R.id.duplicationCheckNicknameView);

        btnCheckID = findViewById(R.id.btnCheckID);
        btnCheckNickname = findViewById(R.id.btnCheckNickname);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);

        transUserInfo = UserClient.getClient().create(ServiceAPI.class);
    }

    public void toLogin() {
        Intent toLoginActivity = new Intent(getApplicationContext(), Login.class);
        startActivity(toLoginActivity);
        finish();
    }

    private void duplicationCheckID(String id) {
        transUserInfo.checkID(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                duplicationCheckIDView.setText(result);
                Toast.makeText(Join.this, result, Toast.LENGTH_SHORT).show();
                if(result.equals("사용 가능한 아이디 입니다.")) {
                    duplicationCheckIDView.setTextColor(Color.parseColor("#0000FF"));
                    confirmID = 1;
                }
                else {
                    duplicationCheckIDView.setTextColor(Color.parseColor("#FF0000"));
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

    private void duplicationCheckNickname(String nickname) {
        transUserInfo.checkNickname(nickname).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                duplicationCheckNicknameView.setText(result);
                Toast.makeText(Join.this, result, Toast.LENGTH_SHORT).show();
                if(result.equals("사용 가능한 닉네임 입니다.")) {
                    duplicationCheckNicknameView.setTextColor(Color.parseColor("#0000FF"));
                    confirmNickname = 1;
                }
                else {
                    duplicationCheckNicknameView.setTextColor(Color.parseColor("#FF0000"));
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

    private static boolean checkID(String id){
        String pattern = "^[a-zA-Z0-9]{6,16}$";

        return Pattern.matches(pattern, id);
    }

    private static boolean checkNickname(String nickname){
        String pattern1 = "^[a-zA-Z]{2,10}$";
        String pattern2 = "^[a-zA-Z0-9]{2,10}$";
        String pattern3 = "^[가-힣]{2,10}$";
        String pattern4 = "^[가-힣0-9]{2,10}$";

        if (Pattern.matches(pattern1, nickname) == true || Pattern.matches(pattern2, nickname) == true || Pattern.matches(pattern3, nickname) == true || Pattern.matches(pattern4, nickname) == true) {
            return true;
        }
        else {
            return false;
        }
    }

    private static boolean checkPW(String pw){
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";

        return Pattern.matches(pattern, pw);
    }

    public void setJoinData() {
        getID = inputID.getText().toString();
        getNickname = inputNickname.getText().toString();
        getPW = inputPW.getText().toString();
        getPWRe = inputPWRe.getText().toString();
        getPhone = inputPhone.getText().toString();
    }

    private void startJoin(String id, String nickname, String password, String passwordRe, String phone) {
        transUserInfo.userJoin(id, nickname, password, passwordRe, phone).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
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