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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentLoginBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentLogin extends Fragment {
    private FragmentLoginBinding binding;
    private SessionManager session;
    private String FireBaseToken;

    public FragmentLogin() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session = SessionManager.get(getContext());
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FireBaseToken=instanceIdResult.getToken();
                Log.e("FireBaseToken",""+FireBaseToken);
            }
        });
        binding.btnLogin.setOnClickListener(v -> {
            if (Validation()) {
                Continue();
            }
        });
        binding.btnCreateAccount.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_register);
        });
        binding.tvForgotPassword.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_forgot_password);
        });

    }

    private boolean Validation() {
        boolean valid = false;
        if (binding.etEmail.getText().toString().isEmpty()) {
            binding.etEmail.setError(getResources().getString(R.string.required));
            binding.etEmail.requestFocus();
        } else if (binding.etPassword.getText().toString().isEmpty()) {
            binding.etPassword.setError(getResources().getString(R.string.required));
            binding.etPassword.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }

    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("password", binding.etPassword.getText().toString());
        param.put("email", binding.etEmail.getText().toString());
        param.put("register_id", FireBaseToken);
        return param;
    }

    private void Continue() {
        ApiCallBuilder.build(getActivity())
                .isShowProgressBar(true, ProgressStyle.STYLE_2)
                .setParam(getParam())
                .setUrl(BaseClass.get().Login())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("Login", "=====>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                session.CreateSession(object.getString("result"));
                                if (object.getJSONObject("result").getString("status").equals("Deactive")) {
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_verify);
                                }else {
                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                    getActivity().finish();
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

                    }
                });
    }
}
