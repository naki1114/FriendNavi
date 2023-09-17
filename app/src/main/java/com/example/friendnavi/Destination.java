package com.example.friendnavi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.MultipartPathOverlay;

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

    MultipartPathOverlay pathTrafast = new MultipartPathOverlay();
    MultipartPathOverlay pathTracomfort = new MultipartPathOverlay();
    MultipartPathOverlay pathTraoptimal = new MultipartPathOverlay();

    RecyclerView optionView;
    TrafficOptionAdapter optionAdapter;
    TrafficOption traOption;

    private double zoomLevel = 0;

    private final int PATH_WIDTH = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        setMapView(savedInstanceState);
        initRetrofit();
        initLoc();
        initSearchInfoView();
        getSearchRoutes();
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
                optionAdapter.notifyDataSetChanged();
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

        optionAdapter = new TrafficOptionAdapter();
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
        naverMap.setContentPadding(0, 0, 0, 500);

        CameraPosition cameraPosition = new CameraPosition(new LatLng((latStart + latGoal) / 2, (lngStart + lngGoal) / 2), zoomLevel);
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
                setZoomLevel();

                CameraPosition cameraPosition = new CameraPosition(new LatLng((latStart + latGoal) / 2, (lngStart + lngGoal) / 2), zoomLevel);
                naverMap.setCameraPosition(cameraPosition);

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

    public void setZoomLevel() {
        double[][] bbox = getTrafficData.getRoute().getTrafast().get(0).getSummary().getBbox();
        double sizeLat = Math.abs(bbox[0][1] - bbox[1][1]);
        double sizeLng = Math.abs(bbox[0][0] - bbox[1][0]);
        Log.v(TAG, "Lat : " + sizeLat);
        Log.v(TAG, "Lng : " + sizeLng);

        if (sizeLat > 2.56 || sizeLng > 2.56) {
            zoomLevel = 5.5;
        }
        else if (sizeLat > 1.28 || sizeLng > 1.28) {
            zoomLevel = 6.5;
        }
        else if (sizeLat > 0.64 || sizeLng > 0.64) {
            zoomLevel = 7.5;
        }
        else if (sizeLat > 0.32 || sizeLng > 0.32) {
            zoomLevel = 8.5;
        }
        else if (sizeLat > 0.16 || sizeLng > 0.16) {
            zoomLevel = 9.5;
        }
        else if (sizeLat > 0.08 || sizeLng > 0.08) {
            zoomLevel = 10.5;
        }
        else if (sizeLat > 0.04 || sizeLng > 0.04) {
            zoomLevel = 11.5;
        }
        else if (sizeLat > 0.02 || sizeLng > 0.02) {
            zoomLevel = 12.5;
        }
        else if (sizeLat > 0.01 || sizeLng > 0.01) {
            zoomLevel = 13.5;
        }
        else {
            zoomLevel = 14.5;
        }
    }

    public void drawPathTrafast(boolean check) {
        double[][] pathList = getTrafficData.getRoute().getTrafast().get(0).getPath();
        ArrayList<TrafficData.Guide> guideList = getTrafficData.getRoute().getTrafast().get(0).getGuide();
        int pathCount = pathList.length;
        int guideCount = 0;

        pathTrafast.setMap(null);

        List<List<LatLng>> pathLatLng = new ArrayList<>();
        ArrayList<MultipartPathOverlay.ColorPart> pathColor = new ArrayList<>();

        for (int i = 0; i < pathCount - 1; i++) {
            double speed = speedPath(guideList.get(guideCount).getDistance(), guideList.get(guideCount).getDuration());
            pathLatLng.add(Arrays.asList(new LatLng(pathList[i][1], pathList[i][0]), new LatLng(pathList[i + 1][1], pathList[i + 1][0])));

            if (check == true) {
                if (speed >= 0.0195) {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.GREEN, Color.GREEN, Color.GRAY, Color.GRAY));
                }
                else if (speed >= 0.0111) {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.YELLOW, Color.YELLOW, Color.GRAY, Color.GRAY));
                }
                else {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.RED, Color.RED, Color.GRAY, Color.GRAY));
                }
            }
            else {
                pathColor.add(new MultipartPathOverlay.ColorPart(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY));
            }

            if (i == guideList.get(guideCount).getPointIndex() && guideCount < guideList.size() - 1) {
                guideCount++;
            }
        }

        pathTrafast.setCoordParts(pathLatLng);
        pathTrafast.setColorParts(pathColor);
        pathTrafast.setWidth(PATH_WIDTH);
        pathTrafast.setOutlineWidth(0);
        pathTrafast.setMap(naverMap);
    }

    public void drawPathTracomfort(boolean check) {
        double[][] pathList = getTrafficData.getRoute().getTracomfort().get(0).getPath();
        ArrayList<TrafficData.Guide> guideList = getTrafficData.getRoute().getTracomfort().get(0).getGuide();
        int pathCount = pathList.length;
        int guideCount = 0;

        pathTracomfort.setMap(null);

        List<List<LatLng>> pathLatLng = new ArrayList<>();
        ArrayList<MultipartPathOverlay.ColorPart> pathColor = new ArrayList<>();

        for (int i = 0; i < pathCount - 1; i++) {
            double speed = speedPath(guideList.get(guideCount).getDistance(), guideList.get(guideCount).getDuration());
            pathLatLng.add(Arrays.asList(new LatLng(pathList[i][1], pathList[i][0]), new LatLng(pathList[i + 1][1], pathList[i + 1][0])));

            if (check == true) {
                if (speed >= 0.0195) {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.GREEN, Color.GREEN, Color.GRAY, Color.GRAY));
                }
                else if (speed >= 0.0111) {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.YELLOW, Color.YELLOW, Color.GRAY, Color.GRAY));
                }
                else {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.RED, Color.RED, Color.GRAY, Color.GRAY));
                }
            }
            else {
                pathColor.add(new MultipartPathOverlay.ColorPart(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY));
            }

            if (i == guideList.get(guideCount).getPointIndex() && guideCount < guideList.size() - 1) {
                guideCount++;
            }
        }

        pathTracomfort.setCoordParts(pathLatLng);
        pathTracomfort.setColorParts(pathColor);
        pathTracomfort.setWidth(PATH_WIDTH);
        pathTracomfort.setOutlineWidth(0);
        pathTracomfort.setMap(naverMap);
    }

    public void drawPathTraoptimal(boolean check) {
        double[][] pathList = getTrafficData.getRoute().getTraoptimal().get(0).getPath();
        ArrayList<TrafficData.Guide> guideList = getTrafficData.getRoute().getTraoptimal().get(0).getGuide();
        int pathCount = pathList.length;
        int guideCount = 0;

        pathTraoptimal.setMap(null);

        List<List<LatLng>> pathLatLng = new ArrayList<>();
        ArrayList<MultipartPathOverlay.ColorPart> pathColor = new ArrayList<>();

        for (int i = 0; i < pathCount - 1; i++) {
            double speed = speedPath(guideList.get(guideCount).getDistance(), guideList.get(guideCount).getDuration());
            pathLatLng.add(Arrays.asList(new LatLng(pathList[i][1], pathList[i][0]), new LatLng(pathList[i + 1][1], pathList[i + 1][0])));

            if (check == true) {
                if (speed >= 0.0195) {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.GREEN, Color.GREEN, Color.GRAY, Color.GRAY));
                }
                else if (speed >= 0.0111) {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.YELLOW, Color.YELLOW, Color.GRAY, Color.GRAY));
                }
                else {
                    pathColor.add(new MultipartPathOverlay.ColorPart(Color.RED, Color.RED, Color.GRAY, Color.GRAY));
                }
            }
            else {
                pathColor.add(new MultipartPathOverlay.ColorPart(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY));
            }

            if (i == guideList.get(guideCount).getPointIndex() && guideCount < guideList.size() - 1) {
                guideCount++;
            }
        }

        pathTraoptimal.setCoordParts(pathLatLng);
        pathTraoptimal.setColorParts(pathColor);
        pathTraoptimal.setWidth(PATH_WIDTH);
        pathTraoptimal.setOutlineWidth(0);
        pathTraoptimal.setMap(naverMap);
    }

    public double speedPath(int distance, int duration) {
        return (double) distance / (double) duration;
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
        toNavigationActivity.putExtra("goalLat",latGoal);
        toNavigationActivity.putExtra("goalLng",lngGoal);
        startActivity(toNavigationActivity);
        finish();
    }

}