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
import com.letscoffee.Adapters.AdapterMyOrder;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Models.ModelMyOrder;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentMembershipBinding;
import com.letscoffee.databinding.FragmentMyOrderBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentOrder extends Fragment {
    private FragmentMyOrderBinding binding;
    private SessionManager session;
    private ArrayList<ModelMyOrder>myOrders=new ArrayList<>();
    private AdapterMyOrder adapter;

    public FragmentOrder() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_order, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session = SessionManager.get(getContext());
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
       adapter=new AdapterMyOrder(getContext(),myOrders).Callback(order->{
           Bundle bundle=new Bundle();
           bundle.putString("orderID",order.getId());
           Navigation.findNavController(binding.getRoot()).navigate(R.id.action_order_summery,bundle);
       });
       binding.recycleOrder.setAdapter(adapter);
      binding.swipeRefresh.setOnRefreshListener(this::getOrder);
      getOrder();

    }

    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id",session.getUserID());
        return param;
    }

    private void getOrder() {
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity())
                .setParam(getParam())
                .setUrl(BaseClass.get().getMyOrder())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        binding.swipeRefresh.setRefreshing(false);
                        Log.e("getMyOrder", "=====>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                myOrders.clear();
                                JSONArray array=object.getJSONArray("result");
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject=array.getJSONObject(i);
                                    ModelMyOrder order=new ModelMyOrder();
                                    order.setId(jsonObject.getString("id"));
                                    order.setShop_name(jsonObject.getString("shop_name"));
                                    order.setShop_image(jsonObject.getString("shop_image"));
                                    order.setBook_time(jsonObject.getString("book_time"));
                                    order.setStatus(jsonObject.getString("status"));
                                    order.setAmount(jsonObject.getString("amount"));
                                    order.setPreparing_time(jsonObject.getString("preparing_time"));
                                    order.setReady_time(jsonObject.getString("ready_time"));
                                    myOrders.add(order);
                                }
                                adapter.notifyDataSetChanged();
                                binding.tvCount.setText(""+myOrders.size());
                                if (myOrders.size()>0){
                                    binding.bottomView.setVisibility(View.VISIBLE);
                                    binding.tvNoRecord.setVisibility(View.GONE);
                                }else {
                                    binding.bottomView.setVisibility(View.GONE);
                                    binding.tvNoRecord.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(getContext(), "" + object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                        binding.swipeRefresh.setRefreshing(false);
                    }
                });
    }
}
