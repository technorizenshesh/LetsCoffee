package com.letscoffee.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.letscoffee.Adapters.AdapterShops;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Models.ModelShop;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentNearByShopBinding;
import com.letscoffee.databinding.FragmentSearchShopBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class FragmentNearByShop extends Fragment implements LocationListener {
    private FragmentNearByShopBinding binding;
    private SessionManager session;
    private ArrayList<ModelShop>shops=new ArrayList<>();
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location location;
    public FragmentNearByShop() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_near_by_shop, container, false);
        initLocation();
        bindMap();
        BindView();
        return binding.getRoot();
    }
    private void initLocation() {
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (getActivity().checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] arr={ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
            getActivity().requestPermissions(arr,100);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        Tools.get().isLocationEnabled(getActivity(),locationManager);
    }
    private void bindMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap map) {
                mMap=map;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.clear();
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(15)
                        .bearing(0)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
                getShop();
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        for (ModelShop shop:shops){
                            if (shop.getId().equals(marker.getSnippet())){
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("data",shop);
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_shop_details,bundle);
                                break;
                            }
                        }
                    }
                });
            }
        });
    }
    private void BindView() {
        session = SessionManager.get(getContext());
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
//        getShop();
    }
    private void getShop(){
        HashMap<String,String>param=new HashMap<>();
        param.put("lat",""+location.getLatitude());
        param.put("lon",""+location.getLongitude());
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getNearbyCoffeeShop())
                .setParam(param)
                .isShowProgressBar(true)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            boolean status=object.getString("status").contains("1");
                            if (status){
                                shops.clear();
                                JSONArray array=object.getJSONArray("result");
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject=array.getJSONObject(i);
                                    ModelShop shop=new ModelShop();
                                    shop.setId(jsonObject.getString("id"));
                                    shop.setName(jsonObject.getString("name"));
                                    shop.setImage(jsonObject.getString("image"));
                                    shop.setAddress(jsonObject.getString("address"));
                                    shop.setRating(jsonObject.getString("rating"));
                                    shop.setRes_status(jsonObject.getString("res_status"));
                                    shop.setOpen_time(jsonObject.getString("open_time"));
                                    shop.setClose_time(jsonObject.getString("close_time"));
                                    shop.setPhone(jsonObject.getString("phone"));
                                    shops.add(shop);
                                    if(mMap!=null){
                                        if (jsonObject.getString("lat")!="") {
                                            Double lat = Double.valueOf(jsonObject.getString("lat"));
                                            Double lon = Double.valueOf(jsonObject.getString("lon"));
                                            String name=jsonObject.getString("name");
                                            View markerView=LayoutInflater.from(getContext()).inflate(R.layout.custom_marker,null,false);
                                            TextView tv_name=markerView.findViewById(R.id.tv_name);
                                            tv_name.setText(name);
                                            tv_name.setSelected(true);
                                            mMap.addMarker(new MarkerOptions()
                                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(markerView)))
                                                    .position(new LatLng(lat, lon))
                                                    .title(name).snippet(jsonObject.getString("id")));
                                            if (i==0){
                                                CameraPosition googlePlex = CameraPosition.builder()
                                                        .target(new LatLng(lat, lon))
                                                        .zoom(15)
                                                        .bearing(0)
                                                        .tilt(45)
                                                        .build();
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 500, null);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                    }
                });
    }
    private Bitmap getMarkerBitmapFromView(View customMarkerView) {
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.e("OnLocationChanged","=====>"+(loc!=null));
        location=loc;

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
