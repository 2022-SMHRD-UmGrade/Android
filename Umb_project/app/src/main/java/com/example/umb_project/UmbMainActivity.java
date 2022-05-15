package com.example.umb_project;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.umb_project.info.UserInfo;
import com.example.umb_project.vo.User;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;

public class UmbMainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.CurrentLocationEventListener {

    private static final String TAG = "UmbMainActivity";
    private Button scanQRBtn;
    TextView tvUser1, tvUserPoint2;
    Button btnMypage, btnLogout, btnBoard;
    RequestQueue queue;
    StringRequest request;

    User vo = UserInfo.info;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 0;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umb_main);

        tvUser1 = findViewById(R.id.tvUser1);
        tvUserPoint2 = findViewById(R.id.tvUserPoint2);
        btnMypage = findViewById(R.id.btnMypage);
        btnBoard = findViewById(R.id.btnBoard);
        btnLogout = findViewById(R.id.btnLogout);

        tvUser1.setText(vo.getUser_id());
        tvUserPoint2.setText(vo.getUser_point());

        queue = Volley.newRequestQueue(UmbMainActivity.this);

        scanQRBtn = (Button) findViewById(R.id.scanQR);
        //QR코드 스캔
        scanQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UmbMainActivity.this, UmbQrActivity.class);
                startActivity(intent);
            }
        });

        int method = Request.Method.POST;
        String server_url = "http://192.168.0.3:8081/myapp/Android/Login";

        request = new StringRequest(
                method,
                server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        getAppKeyHash();
                        initView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UmbMainActivity.this,
                                "" + error.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(request);
        //마이페이지
        btnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                intent.getStringExtra("response");

                int method = Request.Method.POST;
                String server_url = "http://220.71.97.131:8081/myapp/Android/Login";

                request = new StringRequest(
                        method,
                        server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Intent intent = new Intent(UmbMainActivity.this, UmbMypageActivity.class);

                                intent.putExtra("response", response);

                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(UmbMainActivity.this,
                                        "" + error.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                queue.add(request);
            }
        });
        //로그아웃
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vo = null;

                Intent intent = new Intent(UmbMainActivity.this, UmbLoginActivity.class);

                startActivity(intent);
            }
        });

        btnBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vo = UserInfo.info;
                Log.d("info", vo.toString());
                Intent intent = new Intent(UmbMainActivity.this, UmbBoardActivity.class);
                startActivity(intent);
            }
        });
    }

    //카카오 지도 API 사용하기위해 디버그 해쉬 키 발급
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash Key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

    public void initView() {

        mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_View);
        mapViewContainer.addView(mapView);

        mapView.setMapViewEventListener(this); // this에 MapView.MapViewEventListener 구현.
        if (!checkLocationServiceStatus()){
            showDialogForLocationServiceSetting();}
        else{
            checkRunTimePermission();
        }

        //setCurrentLocationTrackingMode (지도랑 현재위치 좌표 찍어주고 따라다닌다.)
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setCurrentLocationEventListener(UmbMainActivity.this);
        mapView.setPOIItemEventListener(this);

        mapView.setPOIItemEventListener(this);

        MapPOIItem marker = new MapPOIItem();
        //맵 포인트 위도경도 설정
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(35.14983, 126.91977);

        marker.setItemName("Default Name");

        marker.setTag(0);
        marker.setMapPoint(mapPoint);

        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);


        MapPOIItem marker1 = new MapPOIItem();

        MapPoint mapPoint1 = MapPoint.mapPointWithGeoCoord(35.14953, 126.91937);

        marker1.setItemName("Default Name");

        marker1.setTag(0);
        marker1.setMapPoint(mapPoint1);

        marker1.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker1.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker1);


    }

    // 시스템 위치 서비스에 대한 액세스를 제공하는 메서드
    public boolean checkLocationServiceStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // 현재 위치 업데이트 setCurrentLocationEventListener
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        MapPoint currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        //이 좌표로 지도 중심 이동
        mapView.setMapCenterPoint(currentMapPoint, true);
        //전역변수로 현재 좌표 저장
        double mCurrentLat = mapPointGeo.latitude;
        double mCurrentLng = mapPointGeo.longitude;
        Log.d(TAG, "현재위치 => " + mCurrentLat + "  " + mCurrentLng);

        //트래킹 모드가 아닌 단순 현재위치 업데이트일 경우, 한번만 위치 업데이트하고 트래킹을 중단시키기 위한 로직
        boolean isTrackingMode = true;
        if (!isTrackingMode) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        Log.i(TAG, "onCurrentLocationUpdateFailed");
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        Log.i(TAG, "onCurrentLocationUpdateCancelled");
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    //퍼미션 체크 메서드
    void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(UmbMainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED){
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(UmbMainActivity.this,REQUIRED_PERMISSIONS[0])){
                Toast.makeText(UmbMainActivity.this,
                        "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(UmbMainActivity.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(UmbMainActivity.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }
    }
    //gps정보를 얻기 위해 위치정보 제공 동의를 받는 메서드
    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UmbMainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해 위치 서비스가 필요합니다.");
        builder.setCancelable(true);
        builder.setPositiveButton("설정",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent,GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.create().show();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}