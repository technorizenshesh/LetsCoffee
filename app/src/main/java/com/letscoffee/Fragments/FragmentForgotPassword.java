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
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentForgotPasswordBinding;
import com.letscoffee.databinding.FragmentLoginBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentForgotPassword extends Fragment {
    private FragmentForgotPasswordBinding binding;
    public FragmentForgotPassword() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.btnSubmit.setOnClickListener(v -> {
            if (Validation()) {
                Continue();
            }
        });
    }

    private boolean Validation() {
        boolean valid = false;
        if (binding.etEmail.getText().toString().isEmpty()) {
            binding.etEmail.setError(getResources().getString(R.string.required));
            binding.etEmail.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }

    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("email", binding.etEmail.getText().toString());
        return param;
    }

    private void Continue() {
        ApiCallBuilder.build(getActivity())
                .isShowProgressBar(true, ProgressStyle.STYLE_2)
                .setParam(getParam())
                .setUrl(BaseClass.get().forgotPassword())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("ForgetPassword", "=====>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            Toast.makeText(getContext(), "" + object.getString("result"), Toast.LENGTH_SHORT).show();
                            if (status) {
                                Navigation.findNavController(binding.getRoot()).popBackStack();
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
}
