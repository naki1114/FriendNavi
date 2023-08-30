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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;

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

    private static final int LOCATION_UPDATE_INTERVAL = 1000; // 1 seconds

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

    int bearing = 0;
    int zoomLevel = 19;

    TrafficData getTrafficData;
    String trafficOption;

    PathOverlay path;

    private Handler locationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Location location = (Location) msg.obj;
            updateLocationUI(location);
            moveMap();
            CameraPosition cameraPosition = new CameraPosition(new LatLng(lat, lng), zoomLevel, 100, bearing);
            naverMap.setCameraPosition(cameraPosition);

            LocationOverlay locationOverlay = naverMap.getLocationOverlay();
            locationOverlay.setVisible(true);
            locationOverlay.setPosition(new LatLng(lat, lng));
            locationOverlay.setBearing(bearing);
            locationOverlay.setSubIcon(OverlayImage.fromResource(R.drawable.pointer));
            locationOverlay.setSubIconWidth(50);
            locationOverlay.setSubIconHeight(50);
            locationOverlay.setSubAnchor(new PointF(0.5f, 1));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Log.v(TAG, "onCreate 호출");

        setMapView(savedInstanceState);
        initLoc();
        initSensor();
        startSensor();
        getIntentTrafficData();
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

        CameraPosition cameraPosition = new CameraPosition(new LatLng(lat, lng), zoomLevel, 100, bearing);
        naverMap.setCameraPosition(cameraPosition);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(new LatLng(lat, lng));
        locationOverlay.setBearing(bearing);
        locationOverlay.setSubIcon(OverlayImage.fromResource(R.drawable.pointer));
        locationOverlay.setSubIconWidth(50);
        locationOverlay.setSubIconHeight(50);
        locationOverlay.setSubAnchor(new PointF(0.5f, 1));

        drawPath(trafficOption);
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

    public void moveMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                locationHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 1; i++) {
                            try {
                                Thread.sleep(1000);
                            }
                            catch (Exception e) {

                            }
                            i--;
                            CameraPosition cameraPosition = new CameraPosition(new LatLng(lat, lng), zoomLevel, 100, bearing);
                            naverMap.setCameraPosition(cameraPosition);

                            LocationOverlay locationOverlay = naverMap.getLocationOverlay();
                            locationOverlay.setVisible(true);
                            locationOverlay.setPosition(new LatLng(lat, lng));
                            locationOverlay.setBearing(bearing);
                            locationOverlay.setSubIcon(OverlayImage.fromResource(R.drawable.pointer));
                            locationOverlay.setSubIconWidth(50);
                            locationOverlay.setSubIconHeight(50);
                            locationOverlay.setSubAnchor(new PointF(0.5f, 1));

                        }
                    }
                });
            }
        });
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
        double[][] pathList = new double[0][0];
        ArrayList<TrafficData.Guide> guideList = new ArrayList<>();

        if (trafficOption.equals("trafast")) {
            pathList = getTrafficData.getRoute().getTrafast().get(0).getPath();
            guideList = getTrafficData.getRoute().getTrafast().get(0).getGuide();
        }
        else if (trafficOption.equals("tracomfort")) {
            pathList = getTrafficData.getRoute().getTracomfort().get(0).getPath();
            guideList = getTrafficData.getRoute().getTracomfort().get(0).getGuide();
        }
        else {
            pathList = getTrafficData.getRoute().getTraoptimal().get(0).getPath();
            guideList = getTrafficData.getRoute().getTraoptimal().get(0).getGuide();
        }

        int pathCount = pathList.length;
        int guideCount = guideList.size();
        path = new PathOverlay();
        path.setMap(null);

        for (int i = pathCount - 1; i > 0; i--) {
            path = new PathOverlay();
            path.setCoords(Arrays.asList(
                    new LatLng(pathList[i][1], pathList[i][0]),
                    new LatLng(pathList[i - 1][1], pathList[i - 1][0])));
            path.setWidth(500);
            path.setOutlineWidth(0);
            path.setColor(Color.BLUE);

            if (i == guideList.get(guideCount - 1).getPointIndex() && guideCount > 1) {
                guideCount--;
            }
            path.setMap(naverMap);
        }
    }

}