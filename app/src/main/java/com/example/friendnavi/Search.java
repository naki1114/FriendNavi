package com.example.friendnavi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {

    String TAG = "검색 페이지";

    ServiceAPI naverSearch;

    EditText search;

    RecyclerView searchListView;
    private ArrayList<ItemSearch> searchList;
    private SearchAdapter searchAdapter;

    String[] searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.v(TAG, "onCreate 호출");

        initView();
        initRetrofit();
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

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getResult();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

    public void initRetrofit() {
        naverSearch = RetrofitClient.getClient().create(ServiceAPI.class);
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

    public void getResult() {

        String placeSearch = "";

        try {
            placeSearch = URLEncoder.encode(search.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        naverSearch.getSearchResult(placeSearch).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                searchResult = response.body().split("<br>");
                initSearchResultList();
                setSearchResult();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Search.this, "Sign up Error", Toast.LENGTH_SHORT).show();
                Log.e("Sign up Error", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void initSearchResultList() {
        searchListView = (RecyclerView) findViewById(R.id.searchListView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        searchListView.setLayoutManager(mLinearLayoutManager);

        searchList = new ArrayList<>();

        searchAdapter = new SearchAdapter(searchList);
        searchListView.setAdapter(searchAdapter);
    }

    public void setSearchResult() {
        for (int i = 0; i < searchResult.length - 2; i+=3) {
            String searchName = searchResult[i + 1].replaceAll("<b>", "").replaceAll("</b>", "");
            String searchAddress = searchResult[i + 2].replaceAll("<b>", "").replaceAll("</b>", "");

            if (searchName.length() >= 15) {
                searchName = searchName.substring(0, 15) + "...";
            }
            if (searchAddress.length() >= 30) {
                searchAddress = searchAddress.substring(0, 30) + "...";
            }

            ItemSearch itemSearch = new ItemSearch(searchName, searchAddress);
            searchList.add(itemSearch);
        }
        searchAdapter.notifyDataSetChanged();
    }

}