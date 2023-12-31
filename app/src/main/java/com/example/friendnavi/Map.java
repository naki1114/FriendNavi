package com.example.friendnavi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;

public class Map extends Fragment implements OnMapReadyCallback {

    String TAG = "F_맵 페이지";

    private MapView mapView;
    private static NaverMap naverMap;

    LocationManager lm;
    Location currentLocation;

    Context context;

    EditText search;

    double lat;
    double lng;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(TAG, "onAttach()");

        this.context = context;
        initLoc();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        setMapView(savedInstanceState, view);

        initView(view);
//        initLoc();

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

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(context, Search.class);
                    startActivity(intent);
                }
                return true;
            }
        });
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

    public void initView(View view) {
        search = view.findViewById(R.id.search);
    }

    public void initLoc() {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        currentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        lat = currentLocation.getLatitude();
        lng = currentLocation.getLongitude();

    }

    public void setMapView(Bundle bundle, View view) {
        //네이버 지도
        mapView = view.findViewById(R.id.naverMap);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        naverMap.setMapType(NaverMap.MapType.Navi);

        // 에이블디 위도(Latitude) : 37.27175, 경도(Longitude) : 127.01395
        CameraPosition cameraPosition = new CameraPosition(new LatLng(lat, lng),16);
        naverMap.setCameraPosition(cameraPosition);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(new LatLng(lat, lng));
        locationOverlay.setSubAnchor(new PointF(0.5f, 1));

    }

}
