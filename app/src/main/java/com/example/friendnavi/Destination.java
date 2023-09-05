package com.example.friendnavi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Destination extends AppCompatActivity implements OnMapReadyCallback {

    String TAG = "목적지 페이지";

    private MapView mapView;
    private static NaverMap naverMap;

    Button btnGuide;

    LocationManager lm;
    Location currentLocation;

    ServiceAPI searchRoutes;

    String address;
    String trafficOption = "trafast";

    double latStart;
    double lngStart;

    double latGoal;
    double lngGoal;

    TrafficData getTrafficData;

    PathOverlay path;

    RecyclerView optionView;
    TrafficOptionAdapter optionAdapter;
    TrafficOption traOption;

    ArrayList<TrafficOption> optionList;

    int firstCheck = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        setMapView(savedInstanceState);
        initRetrofit();
        initLoc();
        initSearchInfoView();
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

        getSearchRoutes();

        optionAdapter.setOnItemClickListener(new TrafficOptionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position == 0) {
                    drawPathTracomfort(false);
                    drawPathTraoptimal(false);
                    drawPathTrafast(true);
                    trafficOption = "trafast";
                }
                else if (position == 1) {
                    drawPathTrafast(false);
                    drawPathTraoptimal(false);
                    drawPathTracomfort(true);
                    trafficOption = "tracomfort";
                }
                else {
                    drawPathTrafast(false);
                    drawPathTracomfort(false);
                    drawPathTraoptimal(true);
                    trafficOption = "traoptimal";
                }
                firstCheck = 1;
            }
        });

        btnGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toNavigation();
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

    public void initSearchInfoView() {
        optionView = findViewById(R.id.optionView);
        btnGuide = findViewById(R.id.btnGuide);

        optionView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, 0);
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.divider_item_option));
        optionView.addItemDecoration(dividerItemDecoration);

        optionList = new ArrayList<>();
        optionAdapter = new TrafficOptionAdapter(optionList);
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
                getTrafficData = response.body();
                setResult();

                if(!getTrafficData.equals(null)) {
                    Log.v(TAG, "성공 : " + getTrafficData.getMessage());
                    drawPathTracomfort(false);
                    drawPathTraoptimal(false);
                    drawPathTrafast(true);
                }
                else {
                    Log.v(TAG, "실패 : " + getTrafficData.getMessage());
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

    public void drawPathTrafast(boolean check) {
        double[][] pathList = getTrafficData.getRoute().getTrafast().get(0).getPath();
        ArrayList<TrafficData.Guide> guideList = getTrafficData.getRoute().getTrafast().get(0).getGuide();
        int pathCount = pathList.length;
        int guideCount = guideList.size();
        path = new PathOverlay();
        path.setMap(null);

        for (int i = pathCount - 1; i > 0; i--) {
            path = new PathOverlay();
            path.setCoords(Arrays.asList(
                    new LatLng(pathList[i][1], pathList[i][0]),
                    new LatLng(pathList[i - 1][1], pathList[i - 1][0])));
            path.setWidth(20);
            path.setOutlineWidth(0);

            if (check == true) {
                if (speedPath(guideList.get(guideCount - 1).getDistance(), guideList.get(guideCount - 1).getDuration()) >= 70) {
                    path.setColor(Color.GREEN);
                }
                else if (speedPath(guideList.get(guideCount - 1).getDistance(), guideList.get(guideCount - 1).getDuration()) >= 40) {
                    path.setColor(Color.YELLOW);
                }
                else {
                    path.setColor(Color.RED);
                }
            }
            else {
                path.setColor(Color.GRAY);
            }

            if (i == guideList.get(guideCount - 1).getPointIndex() && guideCount > 1) {
                guideCount--;
            }
            path.setMap(naverMap);
        }
    }

    public void drawPathTracomfort(boolean check) {
        double[][] pathList = getTrafficData.getRoute().getTracomfort().get(0).getPath();
        ArrayList<TrafficData.Guide> guideList = getTrafficData.getRoute().getTracomfort().get(0).getGuide();
        int pathCount = pathList.length;
        int guideCount = guideList.size();
        path = new PathOverlay();
        path.setMap(null);

        for (int i = pathCount - 1; i > 0; i--) {
            path = new PathOverlay();
            path.setCoords(Arrays.asList(
                    new LatLng(pathList[i][1], pathList[i][0]),
                    new LatLng(pathList[i - 1][1], pathList[i - 1][0])));
            path.setWidth(20);
            path.setOutlineWidth(0);

            if (check == true) {
                if (speedPath(guideList.get(guideCount - 1).getDistance(), guideList.get(guideCount - 1).getDuration()) >= 70) {
                    path.setColor(Color.GREEN);
                }
                else if (speedPath(guideList.get(guideCount - 1).getDistance(), guideList.get(guideCount - 1).getDuration()) >= 40) {
                    path.setColor(Color.YELLOW);
                }
                else {
                    path.setColor(Color.RED);
                }
            }
            else {
                path.setColor(Color.GRAY);
            }

            if (i == guideList.get(guideCount - 1).getPointIndex() && guideCount > 1) {
                guideCount--;
            }
            path.setMap(naverMap);
        }
    }

    public void drawPathTraoptimal(boolean check) {
        double[][] pathList = getTrafficData.getRoute().getTraoptimal().get(0).getPath();
        ArrayList<TrafficData.Guide> guideList = getTrafficData.getRoute().getTraoptimal().get(0).getGuide();
        int pathCount = pathList.length;
        int guideCount = guideList.size();
        path = new PathOverlay();
        path.setMap(null);

        for (int i = pathCount - 1; i > 0; i--) {
            path = new PathOverlay();
            path.setCoords(Arrays.asList(
                    new LatLng(pathList[i][1], pathList[i][0]),
                    new LatLng(pathList[i - 1][1], pathList[i - 1][0])));
            path.setWidth(20);
            path.setOutlineWidth(0);

            if (check == true) {
                if (speedPath(guideList.get(guideCount - 1).getDistance(), guideList.get(guideCount - 1).getDuration()) >= 70) {
                    path.setColor(Color.GREEN);
                }
                else if (speedPath(guideList.get(guideCount - 1).getDistance(), guideList.get(guideCount - 1).getDuration()) >= 40) {
                    path.setColor(Color.YELLOW);
                }
                else {
                    path.setColor(Color.RED);
                }
            }
            else {
                path.setColor(Color.GRAY);
            }

            if (i == guideList.get(guideCount - 1).getPointIndex() && guideCount > 1) {
                guideCount--;
            }
            path.setMap(naverMap);
        }
    }

    public double speedPath(int distance, int duration) {
        double dis = distance / 1000;           // m -> km
        double dur = duration / 1000 / 60 / 60; // ms -> h

        double speed = dis / dur;

        return speed;
    }

    public void setResult() {
        SimpleDateFormat dateFormat = new SimpleDateFormat();

        int duration = getTrafficData.getRoute().getTrafast().get(0).getSummary().getDuration() / 1000 / 60;
        int distance = getTrafficData.getRoute().getTrafast().get(0).getSummary().getDistance() / 1000;
        String[] timeArrive = dateFormat.format(System.currentTimeMillis() + getTrafficData.getRoute().getTrafast().get(0).getSummary().getDuration()).split(" ");
        int tollFare = getTrafficData.getRoute().getTrafast().get(0).getSummary().getTollFare();

        if (duration / 60 == 0) {
            traOption = new TrafficOption("최소 시간", timeArrive[1] + " " + timeArrive[2], duration + " 분", distance + " km", tollFare + " 원");
        }
        else {
            traOption = new TrafficOption("최소 시간", timeArrive[1] + " " + timeArrive[2], duration / 60 + " 시간 " + duration % 60 + " 분", distance + " km", tollFare + " 원");
        }
        optionAdapter.addData(traOption);
        optionView.setAdapter(optionAdapter);

        duration = getTrafficData.getRoute().getTracomfort().get(0).getSummary().getDuration() / 1000 / 60;
        distance = getTrafficData.getRoute().getTracomfort().get(0).getSummary().getDistance() / 1000;
        timeArrive = dateFormat.format(System.currentTimeMillis() + getTrafficData.getRoute().getTracomfort().get(0).getSummary().getDuration()).split(" ");
        tollFare = getTrafficData.getRoute().getTracomfort().get(0).getSummary().getTollFare();

        if (duration / 60 == 0) {
            traOption = new TrafficOption("편한 길", timeArrive[1] + " " + timeArrive[2], duration + " 분", distance + " km", tollFare + " 원");
        }
        else {
            traOption = new TrafficOption("편한 길", timeArrive[1] + " " + timeArrive[2], duration / 60 + " 시간 " + duration % 60 + " 분", distance + " km", tollFare + " 원");
        }
        optionAdapter.addData(traOption);
        optionView.setAdapter(optionAdapter);

        duration = getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getDuration() / 1000 / 60;
        distance = getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getDistance() / 1000;
        timeArrive = dateFormat.format(System.currentTimeMillis() + getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getDuration()).split(" ");
        tollFare = getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getTollFare();

        if (duration / 60 == 0) {
            traOption = new TrafficOption("최적", timeArrive[1] + " " + timeArrive[2], duration + " 분", distance + " km", tollFare + " 원");
        }
        else {
            traOption = new TrafficOption("최적", timeArrive[1] + " " + timeArrive[2], duration / 60 + " 시간 " + duration % 60 + " 분", distance + " km", tollFare + " 원");
        }
        optionAdapter.addData(traOption);
        optionView.setAdapter(optionAdapter);
        optionAdapter.notifyDataSetChanged();
    }

    public void toNavigation() {
        Intent toNavigationActivity = new Intent(getApplicationContext(), Navigation.class);
        toNavigationActivity.putExtra("trafficData", getTrafficData);
        toNavigationActivity.putExtra("trafficOption", trafficOption);
        startActivity(toNavigationActivity);
    }

}