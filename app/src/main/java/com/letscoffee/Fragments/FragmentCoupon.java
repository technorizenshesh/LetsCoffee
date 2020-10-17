package com.letscoffee.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.letscoffee.Adapters.AdapterCoupon;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Interfaces.onSelectCouponListener;
import com.letscoffee.Models.ModelCoupon;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentAddAddressBinding;
import com.letscoffee.databinding.FragmentCouponBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentCoupon extends BottomSheetDialogFragment {
    private FragmentCouponBinding binding;
    private SessionManager session;
    private AdapterCoupon adapter;
    private ArrayList<ModelCoupon>coupons=new ArrayList<>();
    private onSelectCouponListener listener;
    private String shopID;

    public static FragmentCoupon get() {
        return new FragmentCoupon();
    }
    public FragmentCoupon() {

    }
    public FragmentCoupon Callback(String shopID,onSelectCouponListener listener){
        this.listener=listener;
        this.shopID=shopID;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_coupon, null, false);
        dialog.setContentView(binding.getRoot());
        BindView();
        return dialog;
    }

    private void BindView() {
        session = SessionManager.get(getContext());
        adapter=new AdapterCoupon(getActivity(),coupons).Callback(coupon ->{
            listener.onSelect(coupon);
            dismiss();
        });
        binding.recycleCoupon.setAdapter(adapter);
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.swipeRefresh.setOnRefreshListener(this::getCoupon);
        getCoupon();
    }


    private void getCoupon() {
        binding.swipeRefresh.setRefreshing(true);
        HashMap<String,String>param=new HashMap<>();
        param.put("shop_id",shopID);
        ApiCallBuilder.build(getActivity())
                .isShowProgressBar(true, ProgressStyle.STYLE_2)
                .setParam(param)
                .setUrl(BaseClass.get().getCoupon())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        binding.swipeRefresh.setRefreshing(false);
                        Log.e("getCoupon", "=====>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                coupons.clear();
                                JSONArray array=object.getJSONArray("result");
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject=array.getJSONObject(i);
                                    ModelCoupon coupon=new ModelCoupon();
                                    coupon.setId(jsonObject.getString("id"));
                                    coupon.setCoupon_code(jsonObject.getString("coupon_code"));
                                    coupon.setType(jsonObject.getString("type"));
                                    coupon.setAmount(jsonObject.getString("amount"));
                                    coupon.setDiscount_percent(jsonObject.getString("discount_percent"));
                                    coupon.setValid_from(jsonObject.getString("valid_from"));
                                    coupon.setValid_to(jsonObject.getString("valid_to"));
                                    coupons.add(coupon);
                                }
                                adapter.notifyDataSetChanged();
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
