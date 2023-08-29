package com.example.friendnavi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.OverlayImage;

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

    private Handler locationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Location location = (Location) msg.obj;
            updateLocationUI(location);
            moveMap();
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(lat, lng));
            naverMap.moveCamera(cameraUpdate);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(TAG, "onAttach()");

        this.context = context;
        initLoc();
        initSensor();
        startSensor();
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

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
        startLocationUpdates();

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
        stopLocationUpdates();
        stopSensor();
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

    public void initSensor() {
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
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
        locationOverlay.setBearing(bearing);
        locationOverlay.setSubIcon(OverlayImage.fromResource(R.drawable.pointer));
        locationOverlay.setSubIconWidth(50);
        locationOverlay.setSubIconHeight(50);
        locationOverlay.setSubAnchor(new PointF(0.5f, 1));

    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
                            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(lat, lng));
                            naverMap.moveCamera(cameraUpdate);

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
                search.setText("각도 : " + Float.toString(bearing));
                mCurrentDegree = bearing;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }

}
