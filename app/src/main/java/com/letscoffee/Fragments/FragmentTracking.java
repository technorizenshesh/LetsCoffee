package com.letscoffee.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Constant.DataParser;
import com.letscoffee.Models.ModelShop;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentOrderDetailsBinding;
import com.letscoffee.databinding.FragmentTrackingBinding;
import com.squareup.picasso.Picasso;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class FragmentTracking extends Fragment implements LocationListener {
    private FragmentTrackingBinding binding;
    private SessionManager session;
    private LocationManager locationManager;
    private Location location;
    private GoogleMap mMap;
    private double current_lat, current_lng;
    private ModelShop shop;
    private BitmapDescriptor icon;
    private MarkerOptions marker;
    ArrayList<LatLng> points = new ArrayList<>();
    private Marker mR;
    private boolean isOpen = true;
    public FragmentTracking() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tracking, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session = SessionManager.get(getContext());
        shop=(ModelShop)getArguments().getSerializable("data");
        binding.tvAddress.setText(shop.getAddress());
        icon = BitmapDescriptorFactory.fromResource(R.drawable.user_pin);
        marker = new MarkerOptions()
                .position(new LatLng(current_lat, current_lng))
                .title("You are here")
                .icon(icon);
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
        initLocation();
        bindMap();
        FetchRout();
    }

    private void BindView2() {

//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(address.getLat()), Double.valueOf(address.getLon())), 18.0f));
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.map_key);
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("URL", "===>" + url);
        return url;
    }

    private void FetchRout() {
        ApiCallBuilder.build(getContext())
                .setUrl(getUrl(new LatLng(current_lat, current_lng), new LatLng(Double.valueOf(shop.getLat()), Double.valueOf(shop.getLon()))))
                .execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                points.clear();
                Log.e("RootResponse", "======>" + response);
                List<List<HashMap<String, String>>> routes = null;
                try {
                    JSONObject jObject = new JSONObject(response);
                    DataParser parser = new DataParser();
                    routes = parser.parse(jObject);
                    PolylineOptions lineOptions = null;
                    for (int i = 0; i < routes.size(); i++) {
                        lineOptions = new PolylineOptions();
                        List<HashMap<String, String>> path = routes.get(i);
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        }
                        lineOptions.addAll(points);
                        lineOptions.width(20);
                        Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                    }
                    if (lineOptions != null) {
                        mMap.clear();
                        mR = mMap.addMarker(marker);
                        mMap.addPolyline(lineOptions);
                        LatLng latLng = new LatLng(Double.valueOf(shop.getLat()), Double.valueOf(shop.getLon()));
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Destination")
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                        if (location != null)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
                    } else {
                        Log.d("onPostExecute", "without Polylines drawn");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void Failed(String error) {
                Log.e("Root_Error", error);
            }
        });
    }

    private void initLocation() {
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (getActivity().checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] arr = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
            requestPermissions(arr, 100);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        Tools.get().isLocationEnabled(getActivity(), locationManager);
        current_lat = location.getLatitude();
        current_lng = location.getLongitude();
//        binding.tvAddress.setText(Tools.getCompleteAddressString(this, location.getLatitude(), location.getLongitude()));
    }

    private void bindMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(current_lat, current_lng))
                        .title("You are here")
                        .icon(icon));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(shop.getLat()), Double.valueOf(shop.getLon())))
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location != null) {
            current_lat = location.getLatitude();
            current_lng = location.getLongitude();
            LatLng newLatLng = new LatLng(current_lat, current_lng);
            if (mMap != null&&mR!=null) {
                mR.setRotation(location.getBearing());
                mR.setPosition(newLatLng);
                if (!PolyUtil.isLocationOnPath(newLatLng, points, true, 100)) {
                    FetchRout();
                }
            }
            Log.e("Distance", "===>" + distance(current_lat, current_lng, Double.valueOf(shop.getLat()), Double.valueOf(shop.getLon())));
            if (distance(current_lat, current_lng, Double.valueOf(shop.getLat()), Double.valueOf(shop.getLon())) < 1) {
                /*if (isOpen) {
                    isOpen = false;
                    DialogMessage.get(this).setMessage("Arrived").Callback(new onNotify() {
                        @Override
                        public void refresh() {
                            isOpen = true;
                            finish();
                        }
                    }).show();
                }*/
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
