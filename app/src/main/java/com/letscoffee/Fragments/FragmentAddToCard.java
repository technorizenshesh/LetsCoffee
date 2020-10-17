package com.letscoffee.Fragments;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.Adapters.AdapterCartItem;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Interfaces.onSelectItemListener;
import com.letscoffee.Models.ModelItem;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentAddToCardBinding;
import com.letscoffee.databinding.FragmentOrderSummeryBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentAddToCard extends Fragment {
    private FragmentAddToCardBinding binding;
    private SessionManager session;
    private HashMap<String, String> param;
    public FragmentAddToCard() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_to_card, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        param=(HashMap<String,String>)getArguments().getSerializable("param");
        binding.paymentType.setText(param.get("payment_type"));
        session = SessionManager.get(getContext());
        binding.number1.addTextChangedListener(watcher);
        binding.number2.addTextChangedListener(watcher);
        binding.number3.addTextChangedListener(watcher);
        binding.number4.addTextChangedListener(watcher);
        binding.month.addTextChangedListener(watcher);
        binding.year.addTextChangedListener(watcher);
        binding.cvv.addTextChangedListener(watcher);
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.tvCoupon.setOnClickListener(v -> {
            String shopID=param.get("shop_id").split(",")[0];
            FragmentCoupon.get().Callback(shopID,coupon->{
                binding.tvCoupon.setText(getString(R.string.select_coupon)+coupon.getCoupon_code());
            }).show(getChildFragmentManager(),"");
        });
        binding.btnPayNow.setOnClickListener(v -> {
            if (Validation()) {
                param.put("remark", "");
                if (param.get("membership_id") != null) {
                    ContinueMembership();
                    return;
                }
                Continue();
            }
        });
    }
    private boolean Validation(){
        if (binding.holderName.getText().toString().isEmpty()){
            binding.holderName.setError(getString(R.string.required));
            binding.holderName.requestFocus();
            return false;
        }
        if (binding.number1.getText().toString().isEmpty()){
            binding.number1.setError(getString(R.string.required));
            binding.number1.requestFocus();
            return false;
        }if (binding.number2.getText().toString().isEmpty()){
            binding.number2.setError(getString(R.string.required));
            binding.number2.requestFocus();
            return false;
        }if (binding.number3.getText().toString().isEmpty()){
            binding.number3.setError(getString(R.string.required));
            binding.number3.requestFocus();
            return false;
        }if (binding.number4.getText().toString().isEmpty()){
            binding.number4.setError(getString(R.string.required));
            binding.number4.requestFocus();
            return false;
        }if (binding.month.getText().toString().isEmpty()){
            binding.month.setError(getString(R.string.required));
            binding.month.requestFocus();
            return false;
        }if (binding.year.getText().toString().isEmpty()){
            binding.year.setError(getString(R.string.required));
            binding.year.requestFocus();
            return false;
        }if (binding.cvv.getText().toString().isEmpty()){
            binding.cvv.setError(getString(R.string.required));
            binding.cvv.requestFocus();
            return false;
        }
        return true;
    }

    private void ContinueMembership() {
        param.put("start_date",Tools.getCurrent(Tools.Type.DATE));
        param.put("payment_method",param.get("payment_type"));
        param.put("payment_token","");
        ApiCallBuilder.build(getActivity())
                .isShowProgressBar(true, ProgressStyle.STYLE_2)
                .setParam(param)
                .setUrl(BaseClass.get().addMembership())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("addMembership", "=====>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                String result=object.getString("result");
                                Bundle bundle =new Bundle();
                                bundle.putString("result",result);
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_order_summery,bundle);
                            } else {
                                Toast.makeText(getContext(), "" + object.getString("message"), Toast.LENGTH_SHORT).show();
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

    private void Continue() {
        param.put("address","");
        ApiCallBuilder.build(getActivity())
                .isShowProgressBar(true, ProgressStyle.STYLE_2)
                .setParam(param)
                .setUrl(BaseClass.get().addPlaceorder())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("addPlaceorder", "=====>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                String result=object.getString("order_id");
                                Bundle bundle =new Bundle();
                                bundle.putString("result",result);
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_order_summery,bundle);
                            } else {
                                Toast.makeText(getContext(), "" + object.getString("message"), Toast.LENGTH_SHORT).show();
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
    TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (binding.number1.getText().toString().length()<4){
                binding.number1.requestFocus();
            }else if (binding.number2.getText().toString().length()<4){
                binding.number2.requestFocus();
            }else if (binding.number3.getText().toString().length()<4){
                binding.number3.requestFocus();
            }else if (binding.number4.getText().toString().length()<4){
                binding.number4.requestFocus();
            }else if (binding.month.getText().toString().length()<2){
                binding.month.requestFocus();
            }else if (binding.year.getText().toString().length()<4){
                binding.year.requestFocus();
            }else if (binding.cvv.getText().toString().length()<4){
                binding.cvv.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    }

