package com.example.friendnavi;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class FriendDialog extends Dialog {

    Button btnAdd;
    Button btnCancel;

    EditText searchNickname;

    View.OnClickListener btnAddListener;
    View.OnClickListener btnCancelListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_add_friend);

        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);

        searchNickname = findViewById(R.id.searchNickname);

        btnAdd.setOnClickListener(btnAddListener);
        btnCancel.setOnClickListener(btnCancelListener);
    }

    public FriendDialog(@NonNull Context context, View.OnClickListener btnAddListener, View.OnClickListener btnCancelListener) {
        super(context);

        this.btnAddListener = btnAddListener;
        this.btnCancelListener = btnCancelListener;
    }
}
