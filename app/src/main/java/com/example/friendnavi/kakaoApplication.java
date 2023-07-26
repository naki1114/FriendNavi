package com.example.friendnavi;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class kakaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "bb9f33eee95b151908e9f6bcc005c322");
    }
}
