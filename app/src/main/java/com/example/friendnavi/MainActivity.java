package com.example.friendnavi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    String TAG = "메인 페이지";

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private Map fragMap;
    private Friend fragFriend;
    private Chatting fragChatting;
    private Profile fragProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate 호출");

        initFragment();
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

    public void initFragment() {

        fragmentManager = getSupportFragmentManager();

        fragMap = new Map();
        fragFriend = new Friend();
        fragChatting = new Chatting();
        fragProfile = new Profile();

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentLayout, fragMap).commitAllowingStateLoss();

    }

    public void choiceFragment(View view)
    {
        transaction = fragmentManager.beginTransaction();

        switch(view.getId())
        {
            case R.id.btnHome:
                transaction.replace(R.id.fragmentLayout, fragMap).commitAllowingStateLoss();
                break;
            case R.id.btnFriend:
                transaction.replace(R.id.fragmentLayout, fragFriend).commitAllowingStateLoss();
                break;
            case R.id.btnChatting:
                transaction.replace(R.id.fragmentLayout, fragChatting).commitAllowingStateLoss();
                break;
            case R.id.btnProfile:
                transaction.replace(R.id.fragmentLayout, fragProfile).commitAllowingStateLoss();
                break;
        }
    }

}