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
import com.letscoffee.Models.ModelMyOrder;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentMyOrderBinding;
import com.letscoffee.databinding.FragmentOrderDetailsBinding;
import com.squareup.picasso.Picasso;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentOrderDetails extends Fragment {
    private FragmentOrderDetailsBinding binding;
    private SessionManager session;
    private ModelMyOrder order;

    public FragmentOrderDetails() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_details, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        order=(ModelMyOrder)getArguments().getSerializable("data");
        session = SessionManager.get(getContext());
        setData();
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
       binding.tvTracking.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).navigate(R.id.action_tracking);
       });

    }

    private void setData() {
        binding.tvShopName.setText(order.getShop_name());
        binding.tvAmount.setText(getString(R.string.currency)+order.getAmount());
        binding.tvPrepairTime.setText(""+order.getPreparing_time());
        binding.tvReadyTime.setText(""+order.getReady_time());
        binding.tvBookTime.setText(order.getBook_time());
        Picasso.get().load(order.getShop_image()).placeholder(R.drawable.card_bg).into(binding.img);
        if (order.getStatus().equals("Received")){
            binding.imgDone.setImageResource(R.drawable.ic_done);
            binding.imgDone.setBackgroundResource(R.drawable.bg_black_circle);
        } if (order.getStatus().equals("Preparing")){
            binding.imgDone.setImageResource(R.drawable.ic_done);
            binding.imgDone.setBackgroundResource(R.drawable.bg_black_circle);
            binding.imgOrderPrepair.setImageResource(R.drawable.ic_done);
            binding.imgOrderPrepair.setBackgroundResource(R.drawable.bg_black_circle);
        }if (order.getStatus().equals("Ready")){
            binding.imgDone.setImageResource(R.drawable.ic_done);
            binding.imgDone.setBackgroundResource(R.drawable.bg_black_circle);
            binding.imgOrderPrepair.setImageResource(R.drawable.ic_done);
            binding.imgOrderPrepair.setBackgroundResource(R.drawable.bg_black_circle);
            binding.imgOrderReady.setImageResource(R.drawable.ic_done);
            binding.imgOrderReady.setBackgroundResource(R.drawable.bg_black_circle);
        }

    }
}
