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

import com.letscoffee.Activities.FirstActivity;
import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentFirstBinding;
import com.letscoffee.databinding.FragmentProfileBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentProfile extends Fragment {
    private FragmentProfileBinding binding;
    private SessionManager session;

    public FragmentProfile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session = SessionManager.get(getContext());
        binding.imgBack.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.tvMyProfile.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_my_profile);
        });
        binding.tvMembership.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_membership);
        });
        binding.tvOrder.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_order);
        });
        binding.tvSetting.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_setting);
        });
        binding.tvAboutUs.setOnClickListener(v -> {
            new FragmentWebView().setData(getString(R.string.about_us), BaseClass.get().AboutUs()).show(getChildFragmentManager(), "");
        });
        binding.tvPrivacyPolicy.setOnClickListener(v -> {
            new FragmentWebView().setData(getString(R.string.privacy_policy), BaseClass.get().PrivacyPolicy()).show(getChildFragmentManager(), "");
        });
        binding.btnLogout.setOnClickListener(v -> {
            session.Logout();
            startActivity(new Intent(getActivity(), FirstActivity.class));
            getActivity().finish();
        });
        setLayout();
    }

    private void setLayout() {
        if (session.getSelectedLanguage().equals("ar")) {
            binding.tvMyProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvMembership.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvOrder.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvSetting.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvAboutUs.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvPrivacyPolicy.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.btnLogout.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvContactUs.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
        }
    }


    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
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
                                startActivity(new Intent(getActivity(), HomeActivity.class));
                                getActivity().finish();
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
