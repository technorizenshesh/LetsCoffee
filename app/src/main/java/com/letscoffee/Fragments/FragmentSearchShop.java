package com.letscoffee.Fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.Adapters.AdapterCategory;
import com.letscoffee.Adapters.AdapterShops;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Models.ModelCategory;
import com.letscoffee.Models.ModelShop;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentSearchShopBinding;
import com.letscoffee.databinding.FragmentViewCategoryBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;

public class FragmentSearchShop extends Fragment {
    private FragmentSearchShopBinding binding;
    private SessionManager session;
    private ArrayList<ModelShop>shops=new ArrayList<>();
    private AdapterShops shopAdapter;
    private HashMap<String, String> param;

    public FragmentSearchShop() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_shop, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        if (getArguments()!=null){
            param=(HashMap<String,String>)getArguments().getSerializable("param");
        }
        session = SessionManager.get(getContext());
        shopAdapter=new AdapterShops(getContext(),shops).Callback(this::onSelectShop);
        binding.recycleShop.setAdapter(shopAdapter);
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
        binding.swipeRefresh.setOnRefreshListener(()->{
            binding.swipeRefresh.setRefreshing(false);
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!binding.etSearch.getText().toString().isEmpty())
                getShop(binding.etSearch.getText().toString());
            }
        });
    }
    private void getShop(String name){
        HashMap<String,String>param=new HashMap<>();
        param.put("name",name);
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().searchShop())
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
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
                                binding.tvNoRecord.setVisibility(View.GONE);
                            }else {
                                binding.tvNoRecord.setVisibility(View.VISIBLE);
                            }
                            shopAdapter.notifyDataSetChanged();
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
        if (param!=null){
            bundle.putSerializable("param",param);
        }
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_shop_details,bundle);
    }
}
