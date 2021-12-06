package com.example.covid_onetool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.infoactivity.InfoActivity;
import android.libraryactivity.LibraryActivity;
import android.newsroom.NewsroomActivity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.selftestactivity.SelfTestActivity;
import android.text.TextUtils;
import android.util.Log;
import android.utils.Country;
import android.utils.DBOpenHelper;
import android.utils.FetchAddressIntentService;
import android.utils.Place;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import android.location.Geocoder;
import org.json.JSONException;

import com.google.android.gms.common.api.GoogleApiClient;
import android.os.ResultReceiver;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerDragListener,ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    /**
     * 用来判断用户在连接上Google Play services之前是否有请求地址的操作
     */
    private boolean mAddressRequested;
    /**
     * 地图上锚点
     */
    private Marker perth ;
    private LatLng lastLatLng,perthLatLng;
    private List<CovidData> list = new ArrayList<>();
    private TextView tvHeal,tvDied,tvConfirm,tvCurConfirm;
    private EditText etSearch;
    private String baiduMapKey = "B5j2lNjl4TtDwa73vpqeF9aSX3edmam6";
    private DBOpenHelper dbOpenHelper;
    private ArrayList<Country> allCountries = new ArrayList<>();
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    //加载菜单栏
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        dbOpenHelper = new DBOpenHelper(this);

        try {
            Country.load(this);
            allCountries.addAll(Country.getAll());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initData();
        etSearch = findViewById(R.id.etLocation);
        findViewById(R.id.searchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSearch();
            }
        });

        tvCurConfirm = findViewById(R.id.tvCurConfirm);
        tvConfirm = findViewById(R.id.tvConfirm);
        tvHeal = findViewById(R.id.tvHeal);
        tvDied = findViewById(R.id.tvDied);

        //接收FetchAddressIntentService返回的结果
        mResultReceiver = new AddressResultReceiver(new Handler());
        //创建GoogleAPIClient实例
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //取得SupportMapFragment,并在地图准备好后调用onMapReady
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //菜单栏功能：页面跳转
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_Statistics:{
                Intent goStatistics = new Intent(MainActivity.this, MainActivity.class);
                startActivity(goStatistics);
                break;
            }
            case R.id.menu_MyDocument:{
                Intent goMyDocument = new Intent(MainActivity.this, LibraryActivity.class);
                startActivity(goMyDocument);
                break;
            }
            case R.id.menu_SelfTest:{
                Intent goSelfTest = new Intent(MainActivity.this, SelfTestActivity.class);
                startActivity(goSelfTest);
                break;
            }
            case R.id.menu_Newsroom:{
                Intent goNewsroom = new Intent(MainActivity.this, NewsroomActivity.class);
                startActivity(goNewsroom);
                break;
            }
            case R.id.menu_MoreInfo:{
                Intent goMoreInfo = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(goMoreInfo);
                break;
            }

        }
        return true;
    }

    private void initData(){
        //data: https://www.wapi.cn/pneumonia.html
        String appid="12571";
        String secret="4173b99a74d74a4fed17e0216fa88510";
        //data API key above
        String str="appid"+appid+"formatjson"+secret;
        String md5str=md5(str);
        String url="https://oyen.api.storeapi.net/api/94/220?appid="+appid+"&format=json&sign="+md5str;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = ERROR;
                msg.obj = "网络请求失败";
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String info = response.body().string();//获取服务器返回的json格式数据
                    JSONObject jsonObject = JSON.parseObject(info);//获得一个JsonObject的对象
                    if(jsonObject.getInteger("codeid")==10000){
                        JSONArray dataArr = jsonObject.getJSONArray("retdata");
                        Message msg = new Message();
                        msg.what = SUCCESS;
                        msg.obj=dataArr;
                        mHandler.sendMessage(msg);
                    }
                    else{
                        Message msg = new Message();
                        msg.what = ERROR;
                        msg.obj="get data fail";
                        mHandler.sendMessage(msg);
                    }
                }
            }
        });
    }

    @NonNull
    private static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static int SUCCESS=0x123;
    private static int ERROR=0x124;

    //Handler方式实现子线程和主线程间的通信
    Handler mHandler=new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            if (SUCCESS == msg.what) {
                JSONArray jsonArray = (JSONArray) msg.obj;
                list.clear();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String xArea = obj.getString("xArea");//地区
                    String confirm = obj.getString("confirm");//确认病例
                    String died=obj.getString("died");
                    String curConfirm = obj.getString("curConfirm");//当前确诊病例
                    String heal = obj.getString("heal");//治愈人数

                    CovidData covidData = new CovidData();
                    String en=xArea;
                    String code = xArea;

                    for (Country country : allCountries) {
                        if(xArea.contains(country.name)){
                            en=country.name_en;
                            code=country.code;
                            break;
                        }
                    }
                    covidData.setxArea(en);
                    covidData.setCity("");
                    covidData.setCode(code);
                    covidData.setConfirm(confirm);
                    covidData.setCurConfirm(curConfirm);
                    covidData.setDied(died);
                    covidData.setHeal(heal);

                    Place place = dbOpenHelper.getPlace(xArea,"");
                    if(place==null) {
                        getLocation(xArea, covidData);
                    }else{
                        covidData.setLatitude(Double.parseDouble(place.getLat()));
                        covidData.setLongitude(Double.parseDouble(place.getLng()));
                        list.add(covidData);
                    }

                    JSONArray sublist = obj.getJSONArray("subList");

                    for(int j=0;j<sublist.size();j++){
                        JSONObject subobj = sublist.getJSONObject(j);
                        String city = subobj.getString("city");
                        String sub_confirm = subobj.getString("confirm");
                        String sub_died = subobj.getString("died");
                        String sub_curConfirm=subobj.getString("curConfirm");
                        String sub_heal = subobj.getString("heal");

                        CovidData sub_covidData = new CovidData();
                        if(en.equals("")) {
                            sub_covidData.setxArea(xArea);
                        }else{
                            sub_covidData.setxArea(en);
                        }
                        String en_city=city;
                        String code_city = city;
                        for (Country country : allCountries) {
                            if(city.contains(country.name)){
                                en_city=country.name_en;
                                code_city = country.code;
                                break;
                            }
                        }
                        sub_covidData.setCity(en_city);
                        sub_covidData.setCode(code_city);
                        sub_covidData.setConfirm(sub_confirm);
                        sub_covidData.setCurConfirm(sub_curConfirm);
                        sub_covidData.setDied(sub_died);
                        sub_covidData.setHeal(sub_heal);
                        if(place==null){
                            place = dbOpenHelper.getPlace(xArea,"");
                        }
                        if(place==null){
                            sub_covidData.setLatitude(0);
                            sub_covidData.setLongitude(0);
                        }else {
                            sub_covidData.setLatitude(Double.parseDouble(place.getLat()));
                            sub_covidData.setLongitude(Double.parseDouble(place.getLng()));
                        }
                        list.add(sub_covidData);
                    }
                }
            } else if (ERROR == msg.what) {
                String info = (String) msg.obj;
                Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 根据地名返回一个有经纬度location,如果查询不到经纬度  则默认经纬度是0
     * @param address
     * @return
     */
    public void getLocation(String address,CovidData covidData) {
        System.out.println(address);
        try {
            address = java.net.URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String url = String.format("http://api.map.baidu.com/geocoder?address=%s&output=json&key=%s&city=", address, baiduMapKey, "");
        new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .get()
                            .build();
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        String info = response.body().string();
                        if(info.startsWith("<!DOCTYPE html>")){

                        }else {
                            JSONObject jsonObject = JSON.parseObject(info);
                            String status = jsonObject.getString("status");
                            if (status.equals("OK")) {
                                Object object = jsonObject.get("result");
                                if (object instanceof JSONArray) {
                                    JSONArray resultArr = jsonObject.getJSONArray("result");
                                    if (resultArr.size() == 0) {
                                        covidData.setLongitude(0);
                                        covidData.setLatitude(0);
                                        list.add(covidData);
                                        Place place = dbOpenHelper.getPlace(covidData.getxArea(), covidData.getCity());
                                        if (place == null) {
                                            dbOpenHelper.addPlace(covidData.getxArea(), covidData.getCity(), covidData.getLatitude() + "", covidData.getLongitude() + "");
                                        }
                                    }
                                }
                                if (object instanceof JSONObject) {
                                    JSONObject resultObject = jsonObject.getJSONObject("result");
                                    if (resultObject.getJSONObject("location") != null) {
                                        JSONObject locationObj = resultObject.getJSONObject("location");
                                        covidData.setLongitude(locationObj.getFloatValue("lng"));
                                        covidData.setLatitude(locationObj.getFloatValue("lat"));
                                        list.add(covidData);
                                        Place place = dbOpenHelper.getPlace(covidData.getxArea(), covidData.getCity());
                                        if (place == null) {
                                            dbOpenHelper.addPlace(covidData.getxArea(), covidData.getCity(), covidData.getLatitude() + "", covidData.getLongitude() + "");
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void toSearch(){
        String location = etSearch.getText().toString().replaceAll(" ","").toLowerCase();
        if(location.equals("")){
            Toast.makeText(MainActivity.this,"input country or city",Toast.LENGTH_SHORT).show();
        }else{
            boolean flag=true;
            for(int i=0;i<list.size();i++) {
                CovidData covidData = list.get(i);
                String xarea = covidData.getxArea().replaceAll(" ","").toLowerCase();
                String city = covidData.getCity().replaceAll(" ","").toLowerCase();
                String code = covidData.getCode().toLowerCase();
                if(xarea.equals(location)||city.equals(location)||code.equals(location)){
                    if(covidData.getLatitude()!=0) {
                        LatLng latLng = new LatLng(covidData.getLatitude(), covidData.getLongitude());
                        MarkerOptions mMarkOption = new MarkerOptions();
                        mMarkOption.draggable(true);
                        mMarkOption.position(latLng);
                        mMarkOption.title(covidData.getxArea());
                        mMap.addMarker(mMarkOption);
                     //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                    }else{
                        Toast.makeText(MainActivity.this,"no get LatLng",Toast.LENGTH_SHORT).show();
                    }
                    tvDied.setText("Died : "+covidData.getDied());
                    tvConfirm.setText("Confirm : "+covidData.getConfirm());
                    tvCurConfirm.setText("Curconfirm : "+covidData.getCurConfirm());
                    tvHeal.setText("Heal : "+covidData.getHeal());
                    flag=false;
                    break;
                }
            }
            if(flag){
                Toast.makeText(MainActivity.this,"no find",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerDragListener(this);
        enableMyLocation();
    }

    /**
     * 检查是否已经连接到 Google Play services
     */
    private void checkIsGooglePlayConn() {
        Log.i("MapsActivity", "checkIsGooglePlayConn-->" +mGoogleApiClient.isConnected());
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        }
        mAddressRequested = true;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * '我的位置'按钮点击时的调用
     * @return
     */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        if (mLastLocation != null) {
            Log.i("MapsActivity", "Latitude-->" + String.valueOf(mLastLocation.getLatitude()));
            Log.i("MapsActivity", "Longitude-->" + String.valueOf(mLastLocation.getLongitude()));
        }
        if (lastLatLng != null)
            perth.setPosition(lastLatLng);
        checkIsGooglePlayConn();
        return false;
    }

    /**
     * 启动地址搜索Service
     */
    protected void startIntentService(LatLng latLng) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.LATLNG_DATA_EXTRA, latLng);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            Toast.makeText(getApplicationContext(), "Permission to access the location is missing.", Toast.LENGTH_LONG).show();
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("MapsActivity", "--onConnected--" );
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Permission to access the location is missing.",Toast.LENGTH_LONG).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            displayPerth(true,lastLatLng);
            initCamera(lastLatLng);
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "No geocoder available",Toast.LENGTH_LONG).show();
                return;
            }
            if (mAddressRequested) {
                startIntentService(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
            }
        }
    }

    /**
     * 添加标记
     */
    private void displayPerth(boolean isDraggable,LatLng latLng) {
        if (perth==null){
            perth = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Position"));
            perth.setDraggable(isDraggable); //设置可移动
        }

    }

    /**
     * 将地图视角切换到定位的位置
     */
    private void initCamera(final LatLng sydney) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,14));
                    }
                });
            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        perthLatLng = marker.getPosition() ;
        startIntentService(perthLatLng);
    }


    class AddressResultReceiver extends ResultReceiver {
        private String mAddressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            mAddressOutput = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
            if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
                Log.i("MapsActivity", "mAddressOutput-->" + mAddressOutput);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Position")
                        .setMessage(mAddressOutput)
                        .create()
                        .show();
            }

        }
    }
}