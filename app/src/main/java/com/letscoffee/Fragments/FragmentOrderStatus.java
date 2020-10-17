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
import com.letscoffee.databinding.FragmentOrderDetailsBinding;
import com.letscoffee.databinding.FragmentOrderStatusBinding;
import com.letscoffee.databinding.FragmentOrderSummeryBindingImpl;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentOrderStatus extends Fragment {
    private FragmentOrderStatusBinding binding;
    private SessionManager session;

    public FragmentOrderStatus() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_status, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        String result=getArguments().getString("result");
        try {
            JSONObject object=new JSONObject(result);
            binding.tvOrderId.setText("AWB#"+object.getString("id"));
            binding.tvDate.setText(object.getString("date_time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        session = SessionManager.get(getContext());
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
       binding.tvTracking.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).navigate(R.id.action_tracking);
       });
        binding.btnOrderHistory.setOnClickListener(v -> {
          startActivity(new Intent(getActivity(),HomeActivity.class).putExtra("open_request","MyOrder"));
        });

    }

}
