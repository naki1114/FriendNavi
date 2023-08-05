package com.example.friendnavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class Search extends AppCompatActivity {

    String TAG = "검색 페이지";

    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart 호출");

        search.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume 호출");

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                double lat = findGeoPoint(getApplicationContext(), search.getText().toString()).getLatitude();
                double lng = findGeoPoint(getApplicationContext(), search.getText().toString()).getLongitude();

                if (lat == 0 && lng == 0) {
                    Toast.makeText(getApplicationContext(), "장소, 주소를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent toDestination = new Intent(getApplicationContext(), Destination.class);
                    toDestination.putExtra("destination", search.getText().toString());
                    startActivity(toDestination);
                }
                return true;
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

    public void initView() {
        search = findViewById(R.id.search);
    }

    public static Location findGeoPoint(Context context, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(context);
        List<Address> addr = null;

        try {
            addr = coder.getFromLocationName(address, 5);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude();
                double lon = lating.getLongitude();
                loc.setLatitude(lat);
                loc.setLongitude(lon);
            }
        }
        return loc;
    }

}