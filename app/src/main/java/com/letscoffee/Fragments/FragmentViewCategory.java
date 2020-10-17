package com.letscoffee.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Adapters.AdapterCategory;
import com.letscoffee.Adapters.AdapterShops;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Interfaces.onSelectShopListener;
import com.letscoffee.Models.ModelCategory;
import com.letscoffee.Models.ModelShop;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentMyOrderBinding;
import com.letscoffee.databinding.FragmentViewCategoryBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentViewCategory extends Fragment {
    private FragmentViewCategoryBinding binding;
    private SessionManager session;
    private ArrayList<ModelCategory> categories=new ArrayList<>();
    private ArrayList<ModelShop>shops=new ArrayList<>();
    private AdapterCategory catAdapter;
    private AdapterShops shopAdapter;
    private String CatID;

    public FragmentViewCategory() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_category, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        CatID=getArguments().getString("cat_id");
        session = SessionManager.get(getContext());
        catAdapter=new AdapterCategory(getContext(),categories).Callback(this::onSelectCategory);
        shopAdapter=new AdapterShops(getContext(),shops).Callback(this::onSelectShop);
        binding.recycleCategory.setAdapter(catAdapter);
        binding.recycleShop.setAdapter(shopAdapter);
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
        binding.details.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_shop_details);
        });
        getCategory();
        getShop();
        binding.swipeRefresh.setOnRefreshListener(()->{
            getCategory();
            getShop();
        });
    }

    private void onSelectCategory(ModelCategory category) {
        CatID=category.getId();
        getShop();
    }

    private void getCategory(){
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getCategory())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object=new JSONObject(response);
                            boolean status=object.getString("status").contains("1");
                            if (status){
                                categories.clear();
                                JSONArray array=object.getJSONArray("result");
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject=array.getJSONObject(i);
                                    ModelCategory category=new ModelCategory();
                                    category.setId(jsonObject.getString("id"));
                                    category.setName(session.isArabic()?jsonObject.getString("arabic_name"):jsonObject.getString("category_name"));
                                    category.setImage(jsonObject.getString("image"));
                                    categories.add(category);
                                }
                                catAdapter.notifyDataSetChanged();
                            }else {
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
    private void getShop(){
        HashMap<String,String>param=new HashMap<>();
        param.put("category_id",CatID);
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().shop_list_according_cat_id())
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("shopAcordingCat","===>"+response);
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object=new JSONObject(response);
                            boolean status=object.getString("status").contains("1");
                            shops.clear();
                            if (status){
                                JSONArray array=object.getJSONArray("result");
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject=array.getJSONObject(i);
                                    ModelShop shop=new ModelShop();
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
                            }
                            shopAdapter.notifyDataSetChanged();
                            binding.tvNoRecord.setVisibility(shops.size()>0?View.GONE:View.VISIBLE);

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
        Bundle bundle=new Bundle();
        bundle.putSerializable("data",shop);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_shop_details,bundle);
    }
}
