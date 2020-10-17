package com.letscoffee.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Adapters.AdapterBanner;
import com.letscoffee.Adapters.AdapterCategory;
import com.letscoffee.Adapters.AdapterNearShops;
import com.letscoffee.Adapters.AdapterShops;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Constant.GPSTracker;
import com.letscoffee.Models.ModelCategory;
import com.letscoffee.Models.ModelShop;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentDashboardBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDashboard extends Fragment {
    private FragmentDashboardBinding binding;
    private ArrayList<ModelCategory> categories = new ArrayList<>();
    private ArrayList<String> banners = new ArrayList<>();
    private ArrayList<ModelShop> shops = new ArrayList<>();
    private AdapterCategory catAdapter;
    private AdapterBanner bannerAdapter;
    private AdapterNearShops shopAdapter;
    private GPSTracker gps;
    private SessionManager session;


    public FragmentDashboard() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session= SessionManager.get(getContext());
        gps=new GPSTracker(getContext());
        bannerAdapter = new AdapterBanner(getContext(), banners);
        catAdapter = new AdapterCategory(getContext(), categories).Callback(this::onSelectCategory);
        shopAdapter = new AdapterNearShops(getContext(), shops).Callback(this::onSelectShop);
        binding.recycleCategory.setAdapter(catAdapter);
        binding.recycleBanner.setAdapter(bannerAdapter);
        binding.recycleShop.setAdapter(shopAdapter);
        binding.imgNoti.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_notification);
        });
        binding.viewCategory.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_view_category);
        });
        binding.imgNearby.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_near_by);
        });
        binding.tvNearView.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_near_by);
        });
        binding.imgSearch.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_search);
        });
        binding.imgQrCode.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_my_membership);
        });
        binding.imgMenu.setOnClickListener(v -> {
            ((HomeActivity) getActivity()).DrawerControler();
        });
        binding.swipeRefresh.setOnRefreshListener(() -> {
            getBanner();
            getCategory();
            getShop();
        });
        getBanner();
        getCategory();
        getShop();
    }

    private void onSelectCategory(ModelCategory category) {
        Bundle bundle=new Bundle();
        bundle.putString("cat_id",category.getId());
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_view_category,bundle);
    }


    private void getCategory() {
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getCategory())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("Category","====>"+response);
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                categories.clear();
                                JSONArray array = object.getJSONArray("result");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    ModelCategory category = new ModelCategory();
                                    category.setId(jsonObject.getString("id"));
                                    category.setName(session.isArabic()?jsonObject.getString("arabic_name"):jsonObject.getString("category_name"));
                                    category.setImage(jsonObject.getString("image"));
                                    categories.add(category);
                                }
                                catAdapter.notifyDataSetChanged();
                            } else {
                                binding.tvNoRecord.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                        binding.swipeRefresh.setRefreshing(false);
                        binding.tvNoRecord.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void getShop() {
        HashMap<String, String> param = new HashMap<>();
        param.put("lat",""+gps.getLatitude());
        param.put("lon", ""+gps.getLongitude());
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getNearbyCoffeeShop())
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("getNearbyCoffeeShop","===>"+response);
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                shops.clear();
                                JSONArray array = object.getJSONArray("result");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    ModelShop shop = new ModelShop();
                                    shop.setId(jsonObject.getString("id"));
                                    shop.setName(session.isArabic()?jsonObject.getString("arabic_name"):jsonObject.getString("name"));
                                    shop.setImage(jsonObject.getString("image"));
                                    shop.setAddress(jsonObject.getString("address"));
                                    shop.setRating(jsonObject.getString("rating"));
                                    shop.setRes_status(jsonObject.getString("res_status"));
                                    shop.setOpen_time(jsonObject.getString("open_time"));
                                    shop.setClose_time(jsonObject.getString("close_time"));
                                    shop.setPhone(jsonObject.getString("phone"));
                                    shop.setLat(jsonObject.getString("lat"));
                                    shop.setLon(jsonObject.getString("lon"));
                                    shops.add(shop);
                                }
                                shopAdapter.notifyDataSetChanged();
                            } else {
                                binding.tvNoRecord.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                        binding.swipeRefresh.setRefreshing(false);
                        binding.tvNoRecord.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void getBanner() {
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getBanner())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                banners.clear();
                                JSONArray array = object.getJSONArray("result");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    banners.add(jsonObject.getString("image"));
                                }
                                bannerAdapter.notifyDataSetChanged();
                            } else {
                                binding.tvNoRecord.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                        binding.swipeRefresh.setRefreshing(false);
                        binding.tvNoRecord.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void onSelectShop(ModelShop shop) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", shop);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_shop_details, bundle);
    }
}
