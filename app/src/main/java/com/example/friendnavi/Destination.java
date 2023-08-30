package com.example.friendnavi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    LinearLayout slideLayout;
    LinearLayout trafastLayout;
    LinearLayout tracomfortLayout;
    LinearLayout traoptimalLayout;

    TextView durationTrafast;
    TextView distanceTrafast;
    TextView timeArriveTrafast;
    TextView tollFareTrafast;
    TextView durationTracomfort;
    TextView distanceTracomfort;
    TextView timeArriveTracomfort;
    TextView tollFareTracomfort;
    TextView durationTraoptimal;
    TextView distanceTraoptimal;
    TextView timeArriveTraoptimal;
    TextView tollFareTraoptimal;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        setMapView(savedInstanceState);
        initRetrofit();
        initLoc();
        initSearchInfoView();
        trafastLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.theme));
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

        trafastLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPathTracomfort(false);
                drawPathTraoptimal(false);
                drawPathTrafast(true);
                trafficOption = "trafast";
                trafastLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.theme));
                tracomfortLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                traoptimalLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        });

        tracomfortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPathTrafast(false);
                drawPathTraoptimal(false);
                drawPathTracomfort(true);
                trafficOption = "tracomfort";
                trafastLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tracomfortLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.theme));
                traoptimalLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        });

        traoptimalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPathTrafast(false);
                drawPathTracomfort(false);
                drawPathTraoptimal(true);
                trafficOption = "traoptimal";
                trafastLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tracomfortLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                traoptimalLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.theme));
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
        slideLayout = findViewById(R.id.slideLayout);
        trafastLayout = findViewById(R.id.trafastLayout);
        tracomfortLayout = findViewById(R.id.tracomfortLayout);
        traoptimalLayout = findViewById(R.id.traoptimalLayout);

        durationTrafast = findViewById(R.id.durationTrafast);
        distanceTrafast = findViewById(R.id.distanceTrafast);
        timeArriveTrafast = findViewById(R.id.timeArriveTrafast);
        tollFareTrafast = findViewById(R.id.tollFareTrafast);
        durationTracomfort = findViewById(R.id.durationTracomfort);
        distanceTracomfort = findViewById(R.id.distanceTracomfort);
        timeArriveTracomfort = findViewById(R.id.timeArriveTracomfort);
        tollFareTracomfort = findViewById(R.id.tollFareTracomfort);
        durationTraoptimal = findViewById(R.id.durationTraoptimal);
        distanceTraoptimal = findViewById(R.id.distanceTraoptimal);
        timeArriveTraoptimal = findViewById(R.id.timeArriveTraoptimal);
        tollFareTraoptimal = findViewById(R.id.tollFareTraoptimal);

        btnGuide = findViewById(R.id.btnGuide);
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

                if(!getTrafficData.equals(null)) {
                    Log.v(TAG, "성공 : " + getTrafficData.getMessage());
                    drawPathTracomfort(false);
                    drawPathTraoptimal(false);
                    drawPathTrafast(true);
                    setResult();
                }
                else {
                    Log.v(TAG, "실패 : " + getTrafficData.getMessage());
                    slideLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TrafficData> call, Throwable t) {
                Toast.makeText(Destination.this, "Sign up Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getMessage());
                t.printStackTrace();
                slideLayout.setVisibility(View.INVISIBLE);
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

        durationTrafast.setText(duration + " 분");
        distanceTrafast.setText(distance + " km");
        timeArriveTrafast.setText(timeArrive[1] + " " + timeArrive[2]);
        tollFareTrafast.setText(tollFare + " 원");

        duration = getTrafficData.getRoute().getTracomfort().get(0).getSummary().getDuration() / 1000 / 60;
        distance = getTrafficData.getRoute().getTracomfort().get(0).getSummary().getDistance() / 1000;
        timeArrive = dateFormat.format(System.currentTimeMillis() + getTrafficData.getRoute().getTracomfort().get(0).getSummary().getDuration()).split(" ");
        tollFare = getTrafficData.getRoute().getTracomfort().get(0).getSummary().getTollFare();

        durationTracomfort.setText(duration + " 분");
        distanceTracomfort.setText(distance + " km");
        timeArriveTracomfort.setText(timeArrive[1] + " " + timeArrive[2]);
        tollFareTracomfort.setText(tollFare + " 원");

        duration = getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getDuration() / 1000 / 60;
        distance = getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getDistance() / 1000;
        timeArrive = dateFormat.format(System.currentTimeMillis() + getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getDuration()).split(" ");
        tollFare = getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getTollFare();

        durationTraoptimal.setText(duration + " 분");
        distanceTraoptimal.setText(distance + " km");
        timeArriveTraoptimal.setText(timeArrive[1] + " " + timeArrive[2]);
        tollFareTraoptimal.setText(tollFare + " 원");
    }

    public void toNavigation() {
        Intent toNavigationActivity = new Intent(getApplicationContext(), Navigation.class);
        toNavigationActivity.putExtra("trafficData", getTrafficData);
        toNavigationActivity.putExtra("trafficOption", trafficOption);
        startActivity(toNavigationActivity);
    }

}