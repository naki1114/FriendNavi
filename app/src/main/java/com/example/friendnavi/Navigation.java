package com.example.friendnavi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.MultipartPathOverlay;
import com.naver.maps.map.overlay.OverlayImage;

import java.util.ArrayList;
import java.util.Arrays;

public class Navigation extends AppCompatActivity implements OnMapReadyCallback {

    String TAG = "네비게이션 페이지";

    private MapView mapView;
    private static NaverMap naverMap;

    LocationManager lm;
    Location currentLocation;

    double lat;
    double lng;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static int LOCATION_UPDATE_INTERVAL = 1000; // 1 seconds

    SensorManager mSensorManager;
    SensorListener mSensorListener;
    Sensor mAccelerometer;
    Sensor mMagnetometer;

    boolean mLastAccelerometerSet = false;
    boolean mLastMagnetometerSet = false;

    float[] mLastAccelerometer = new float[3];
    float[] mLastMagnetometer = new float[3];

    float[] mR = new float[9];
    float[] mOrientation = new float[3];

    float mCurrentDegree = 0f;

    TrafficData getTrafficData;
    String trafficOption;

    MultipartPathOverlay path;

    private final int TILT = 50;
    private double zoomLevel = 17;
    private int iconSize = 300;
    private int bearing = 0;

    private final int PATH_WIDTH = 50;
    private final int PATH_PATTERN = 200;

    ImageButton btnCurrentPoint;
    ImageButton btnReset;
    ImageButton btnMenu;

    TextView viewDistance;
    TextView viewDuration;

    TextView viewFirstCourse;
    TextView viewFirstDistance;
    TextView viewSecondCourse;
    TextView viewSecondDistance;

    LinearLayout secondCourse;

    private boolean handlerCheck = true;

    int guideCount;
    int guideIndex = 0;

    private Handler locationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (handlerCheck) {
                Location location = (Location) msg.obj;
                updateLocationUI(location);
                zoomLevel = naverMap.getCameraPosition().zoom;
                CameraPosition cameraPosition = new CameraPosition(new LatLng(lat, lng), zoomLevel, TILT, bearing);
                naverMap.setCameraPosition(cameraPosition);

                LocationOverlay locationOverlay = naverMap.getLocationOverlay();
                locationOverlay.setIcon(OverlayImage.fromResource(R.drawable.navi_icon));
                locationOverlay.setIconWidth(iconSize);
                locationOverlay.setIconHeight(iconSize);
                locationOverlay.setAnchor(new PointF(0.5f, 1));
                locationOverlay.setPosition(new LatLng(lat, lng));
                locationOverlay.setBearing(bearing);
                locationOverlay.setVisible(true);

                setCourse();
            }
            else {
                this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnCurrentPoint.setVisibility(View.GONE);
                        handlerCheck = true;
                    }
                }, 5000);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Log.v(TAG, "onCreate 호출");

        setMapView(savedInstanceState);
        initView();
        initLoc();
        initSensor();
        startSensor();
        getIntentTrafficData();
        setStatus();
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

        startLocationUpdates();

        btnCurrentPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCurrentPoint();
                btnCurrentPoint.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause 호출");

        stopLocationUpdates();
        stopSensor();
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
        btnCurrentPoint = findViewById(R.id.btnCurrentPoint);
        btnReset = findViewById(R.id.btnReset);
        btnMenu = findViewById(R.id.btnMenu);

        viewDistance = findViewById(R.id.viewDistance);
        viewDuration = findViewById(R.id.viewDuration);

        viewFirstCourse = findViewById(R.id.viewFirstCourse);
        viewFirstDistance = findViewById(R.id.viewFirstDistance);
        viewSecondCourse = findViewById(R.id.viewSecondCourse);
        viewSecondDistance = findViewById(R.id.viewSecondDistance);

        secondCourse = findViewById(R.id.secondCourse);
    }

    public void initLoc() {

        if (ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        lat = currentLocation.getLatitude();
        lng = currentLocation.getLongitude();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Navigation.this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Message message = locationHandler.obtainMessage();
                    message.obj = location;
                    locationHandler.sendMessage(message);
                }
            }
        };
    }

    public void initSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorListener = new SensorListener();
    }

    public void startSensor() {
        mSensorManager.registerListener(mSensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorListener, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopSensor() {
        mSensorManager.unregisterListener(mSensorListener);
        mSensorManager.unregisterListener(mSensorListener);
    }

    public void getIntentTrafficData() {
        Intent intent = getIntent();
        getTrafficData = (TrafficData) intent.getSerializableExtra("trafficData");
        trafficOption = intent.getStringExtra("trafficOption");

        if (trafficOption.equals("trafast")) {
            guideCount = getTrafficData.getRoute().getTrafast().get(0).getGuide().size();
        }
        else if (trafficOption.equals("trafast")) {
            guideCount = getTrafficData.getRoute().getTracomfort().get(0).getGuide().size();
        }
        else {
            guideCount = getTrafficData.getRoute().getTraoptimal().get(0).getGuide().size();
        }
    }

    public void setMapView(Bundle bundle) {
        //네이버 지도
        mapView = (MapView) findViewById(R.id.naverMap);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        naverMap.setMapType(NaverMap.MapType.Navi);

        setUi();

        CameraPosition cameraPosition = new CameraPosition(new LatLng(lat, lng), zoomLevel, TILT, bearing);
        naverMap.setCameraPosition(cameraPosition);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setIcon(OverlayImage.fromResource(R.drawable.navi_icon));
        locationOverlay.setIconWidth(iconSize);
        locationOverlay.setIconHeight(iconSize);
        locationOverlay.setAnchor(new PointF(0.5f, 1));
        locationOverlay.setPosition(new LatLng(lat, lng));
        locationOverlay.setBearing(bearing);
        locationOverlay.setVisible(true);

        drawPath(trafficOption);

        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
                if (i != 0) {
                    btnCurrentPoint.setVisibility(View.VISIBLE);
                    handlerCheck = false;
                }
                else {
                    handlerCheck = true;
                }
            }
        });
    }

    private void setUi() {
        UiSettings uiSettings = naverMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setLogoClickEnabled(false);
        uiSettings.setLogoMargin(-500, 0, 0, -500);
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
            else {
                ActivityCompat.requestPermissions(Navigation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void updateLocationUI(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    class SensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == mAccelerometer) {
                System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
                mLastAccelerometerSet = true;
            }
            else if (event.sensor == mMagnetometer) {
                System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
                mLastMagnetometerSet = true;
            }

            if (mLastAccelerometerSet && mLastMagnetometerSet) {
                SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
                bearing = (int) (Math.toDegrees( SensorManager.getOrientation(mR, mOrientation)[0]) + 360) % 360;
                mCurrentDegree = bearing;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }

    public void drawPath(String trafficOption) {
        double[][] pathList;

        if (trafficOption.equals("trafast")) {
            pathList = getTrafficData.getRoute().getTrafast().get(0).getPath();
        }
        else if (trafficOption.equals("tracomfort")) {
            pathList = getTrafficData.getRoute().getTracomfort().get(0).getPath();
        }
        else {
            pathList = getTrafficData.getRoute().getTraoptimal().get(0).getPath();
        }

        int pathCount = pathList.length;

        path = new MultipartPathOverlay();

        ArrayList<LatLng> pathLatLng = new ArrayList<>();

        for (int i = 0; i < pathCount; i++) {
            pathLatLng.add(new LatLng(pathList[i][1], pathList[i][0]));
        }

        path.setCoordParts(Arrays.asList(pathLatLng));
        path.setColorParts(Arrays.asList(new MultipartPathOverlay.ColorPart(Color.BLUE, Color.BLUE, Color.GRAY, Color.GRAY)));
        path.setWidth(PATH_WIDTH);
        path.setOutlineWidth(0);
        path.setPatternImage(OverlayImage.fromResource(R.drawable.path_pattern));
        path.setPatternInterval(PATH_PATTERN);
        path.setMap(naverMap);
    }

    private void toCurrentPoint() {
        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(lat, lng), 17);
        naverMap.moveCamera(cameraUpdate);
    }

    private void setStatus() {
        int distance;
        int duration;
        int hour;
        int minute;

        if (trafficOption.equals("trafast")) {
            distance = getTrafficData.getRoute().getTrafast().get(0).getSummary().getDistance();
            duration = getTrafficData.getRoute().getTrafast().get(0).getSummary().getDuration() / 1000;
        }
        else if (trafficOption.equals("tracomfort")) {
            distance = getTrafficData.getRoute().getTracomfort().get(0).getSummary().getDistance();
            duration = getTrafficData.getRoute().getTracomfort().get(0).getSummary().getDuration() / 1000;
        }
        else {
            distance = getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getDistance();
            duration = getTrafficData.getRoute().getTraoptimal().get(0).getSummary().getDuration() / 1000;
        }

        if (distance >= 1000) {
            distance = distance / 1000;
            viewDistance.setText(distance + " km");
        }
        else {
            viewDistance.setText(distance + " m");
        }

        if (duration >= 3600) {
            hour = duration / 60 / 60;
            minute = duration / 60 % 60;
            viewDuration.setText(hour + " 시간 " + minute + " 분");
        }
        else if (duration >= 60) {
            minute = duration / 60;
            viewDuration.setText(minute + " 분");
        }
        else {
            viewDuration.setText("1분");
        }
    }

    private void setCourse() {
        TrafficCourse firstCourse = new TrafficCourse();

        double[][] path;

        int firstPointIndex;
        int firstType;

        int secondPointIndex;
        int secondType;

        if (trafficOption.equals("trafast")) {
            path = getTrafficData.getRoute().getTrafast().get(0).getPath();
            firstPointIndex = getTrafficData.getRoute().getTrafast().get(0).getGuide().get(guideIndex).getPointIndex();
            firstType = getTrafficData.getRoute().getTrafast().get(0).getGuide().get(guideIndex).getType();
            if (guideIndex < guideCount - 1) {
                secondPointIndex = getTrafficData.getRoute().getTrafast().get(0).getGuide().get(guideIndex + 1).getPointIndex();
                secondType = getTrafficData.getRoute().getTrafast().get(0).getGuide().get(guideIndex + 1).getType();
            }
            else {
                secondPointIndex = getTrafficData.getRoute().getTrafast().get(0).getGuide().get(guideIndex).getPointIndex();
                secondType = getTrafficData.getRoute().getTrafast().get(0).getGuide().get(guideIndex).getType();
                secondCourse.setVisibility(View.GONE);
            }
        }
        else if (trafficOption.equals("tracomfort")) {
            path = getTrafficData.getRoute().getTracomfort().get(0).getPath();
            firstPointIndex = getTrafficData.getRoute().getTracomfort().get(0).getGuide().get(guideIndex).getPointIndex();
            firstType = getTrafficData.getRoute().getTracomfort().get(0).getGuide().get(guideIndex).getType();
            if (guideIndex < guideCount - 1) {
                secondPointIndex = getTrafficData.getRoute().getTracomfort().get(0).getGuide().get(guideIndex + 1).getPointIndex();
                secondType = getTrafficData.getRoute().getTracomfort().get(0).getGuide().get(guideIndex + 1).getType();
            }
            else {
                secondPointIndex = getTrafficData.getRoute().getTracomfort().get(0).getGuide().get(guideIndex).getPointIndex();
                secondType = getTrafficData.getRoute().getTracomfort().get(0).getGuide().get(guideIndex).getType();
                secondCourse.setVisibility(View.GONE);
            }
        }
        else {
            path = getTrafficData.getRoute().getTraoptimal().get(0).getPath();
            firstPointIndex = getTrafficData.getRoute().getTraoptimal().get(0).getGuide().get(guideIndex).getPointIndex();
            firstType = getTrafficData.getRoute().getTraoptimal().get(0).getGuide().get(guideIndex).getType();
            if (guideIndex < guideCount - 1) {
                secondPointIndex = getTrafficData.getRoute().getTraoptimal().get(0).getGuide().get(guideIndex + 1).getPointIndex();
                secondType = getTrafficData.getRoute().getTraoptimal().get(0).getGuide().get(guideIndex + 1).getType();
            }
            else {
                secondPointIndex = getTrafficData.getRoute().getTraoptimal().get(0).getGuide().get(guideIndex).getPointIndex();
                secondType = getTrafficData.getRoute().getTraoptimal().get(0).getGuide().get(guideIndex).getType();
                secondCourse.setVisibility(View.GONE);
            }
        }

        double firstLat = path[firstPointIndex][1];
        double firstLng = path[firstPointIndex][0];
        double secondLat = path[secondPointIndex][1];
        double secondLng = path[secondPointIndex][0];

        Location currentLoc = new Location("current");
        Location firstLoc = new Location("first");
        Location secondLoc = new Location("second");

        currentLoc.setLatitude(lat);
        currentLoc.setLongitude(lng);
        firstLoc.setLatitude(firstLat);
        firstLoc.setLongitude(firstLng);
        secondLoc.setLatitude(secondLat);
        secondLoc.setLongitude(secondLng);

        int firstDistance = (int) currentLoc.distanceTo(firstLoc);
        int secondDistance = (int) firstLoc.distanceTo(secondLoc);

        viewFirstCourse.setText(firstCourse.getCourse(firstType));
        viewSecondCourse.setText(firstCourse.getCourse(secondType));

        if (firstDistance >= 1000) {
            firstDistance /= 1000;
            viewFirstDistance.setText(firstDistance + " km");
        }
        else {
            viewFirstDistance.setText(firstDistance + " m");
        }

        if (secondDistance >= 1000) {
            secondDistance /= 1000;
            viewSecondDistance.setText(secondDistance + " km");
        }
        else {
            viewSecondDistance.setText(secondDistance + " m");
        }

        if (firstDistance < 15 && guideIndex < guideCount - 1) {
            guideIndex++;
        }
    }

}