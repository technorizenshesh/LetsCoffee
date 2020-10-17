package com.letscoffee.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentVerifyMobileBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentVerifyMobile extends Fragment {
    private FragmentVerifyMobileBinding binding;
    private SessionManager session;

    public FragmentVerifyMobile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_verify_mobile, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session= SessionManager.get(getContext());
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.btnVerify.setOnClickListener(v->{
         VerifyOTP();
        });
        binding.et1.addTextChangedListener(watcher);
        binding.et2.addTextChangedListener(watcher);
        binding.et3.addTextChangedListener(watcher);
        binding.et4.addTextChangedListener(watcher);
        binding.tvResend.setOnClickListener(v->{
            ResendOTP();
        });
    }
TextWatcher watcher=new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        VerifyOTP();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
};
    private void VerifyOTP(){
        if (binding.et1.getText().toString().length()==0){
            binding.et1.requestFocus();
            return;
        }if (binding.et2.getText().toString().length()==0){
            binding.et2.requestFocus();
            return;
        }if (binding.et3.getText().toString().length()==0){
            binding.et3.requestFocus();
            return;
        }if (binding.et4.getText().toString().length()==0){
            binding.et4.requestFocus();
            return;
        }
        String OTP=binding.et1.getText().toString()+binding.et2.getText().toString()+binding.et3.getText().toString()+binding.et4.getText().toString();
        HashMap<String,String>parm=new HashMap<>();
        parm.put("user_id",session.getUserID());
        parm.put("otp",OTP);
        ApiCallBuilder.build(getContext()).setUrl(BaseClass.get().verifyOtp())
                .setParam(parm)
                .isShowProgressBar(true).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    boolean status=object.getString("status").contains("1");
                    Toast.makeText(getContext(), ""+object.getString("message"), Toast.LENGTH_SHORT).show();
                    if (status){
                        startActivity(new Intent(getActivity(),HomeActivity.class));
                        getActivity().finish();
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
    private void ResendOTP(){
        HashMap<String,String>parm=new HashMap<>();
        parm.put("user_id",session.getUserID());
        ApiCallBuilder.build(getContext()).setUrl(BaseClass.get().ResendOtp())
                .setParam(parm)
                .isShowProgressBar(true).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    boolean status=object.getString("status").contains("1");
                    Toast.makeText(getContext(), ""+object.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Failed(String error) {

            }
        });
    }
}
