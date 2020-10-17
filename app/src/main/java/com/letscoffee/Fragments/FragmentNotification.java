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
import com.letscoffee.databinding.FragmentMyOrderBinding;
import com.letscoffee.databinding.FragmentNotificationBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentNotification extends Fragment {
    private FragmentNotificationBinding binding;
    private SessionManager session;

    public FragmentNotification() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session = SessionManager.get(getContext());
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
       getNotification();
    }



    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id",session.getUserID());
        return param;
    }

    private void getNotification() {
        ApiCallBuilder.build(getActivity())
                .isShowProgressBar(true, ProgressStyle.STYLE_2)
                .setParam(getParam())
                .setUrl(BaseClass.get().notificationList())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("notificationList", "=====>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {

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
