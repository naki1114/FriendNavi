package com.example.friendnavi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Destination extends AppCompatActivity implements OnMapReadyCallback {

    String TAG = "목적지 페이지";

    private MapView mapView;
    private static NaverMap naverMap;

    LocationManager lm;
    Location currentLocation;

    ServiceAPI searchRoutes;

    String address;

    double latStart;
    double lngStart;

    double latGoal;
    double lngGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        setMapView(savedInstanceState);
        initRetrofit();
        initLoc();
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

        Log.v(TAG, "LatStart : " + latStart);
        Log.v(TAG, "LngStart : " + lngStart);
        Log.v(TAG, "LatGoal : " + latGoal);
        Log.v(TAG, "LngGoal : " + lngGoal);
        getSearchRoutes();
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

    public void initRetrofit() {
        searchRoutes = RetrofitClient.getClient().create(ServiceAPI.class);
    }

    public void initLoc() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        latStart = currentLocation.getLatitude();
        lngStart = currentLocation.getLongitude();

        Intent getDestination = getIntent();
        address = getDestination.getStringExtra("destination");

        latGoal = findGeoPoint(getApplicationContext(), address).getLatitude();
        lngGoal = findGeoPoint(getApplicationContext(), address).getLongitude();

    }

    public void setMapView(Bundle bundle) {
        //네이버 지도
        mapView = findViewById(R.id.naverMap);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        naverMap.setMapType(NaverMap.MapType.Navi);

        CameraPosition cameraPosition = new CameraPosition(new LatLng((latStart + latGoal) / 2, (lngStart + lngGoal) / 2),13);
        naverMap.setCameraPosition(cameraPosition);

        Marker marker = new Marker();
        marker.setPosition(new LatLng(latGoal, lngGoal));
        marker.setMap(naverMap);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(new LatLng(latStart, lngStart));
        locationOverlay.setSubIconWidth(50);
        locationOverlay.setSubIconHeight(50);
        locationOverlay.setSubAnchor(new PointF(0.5f, 1));

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

    private void getSearchRoutes() {
        String start = lngStart + "," + latStart;
        String goal = lngGoal + "," + latGoal;
        String option = "trafast:tracomfort:traoptimal";
        searchRoutes.getRoutes(start, goal, option).enqueue(new Callback<TrafficData>() {
            @Override
            public void onResponse(Call<TrafficData> call, Response<TrafficData> response) {
                TrafficData result = response.body();

                if(!result.equals(null)) {
                    Log.v(TAG, "성공 : " + result.getMessage());
                    Log.v(TAG, "코드 : " + result.getCode());
                    Log.v(TAG, "현시 : " + result.getCurrentDateTime());
                    Log.v(TAG, "거리 : " + result.getRoute().getTraoptimal().get(0).getPath()[0][1]);
                }
                else {
                    Log.v(TAG, "실패 : " + result.getMessage());
                }
            }

            @Override
            public void onFailure(Call<TrafficData> call, Throwable t) {
                Toast.makeText(Destination.this, "Sign up Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

}