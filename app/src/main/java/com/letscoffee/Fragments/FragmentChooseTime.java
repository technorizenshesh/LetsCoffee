package com.letscoffee.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.R;
import com.letscoffee.databinding.FragmentChooseTimeBinding;
import com.utils.Utils.Tools;

import java.util.HashMap;
import java.util.Map;

public class FragmentChooseTime extends Fragment {
    private FragmentChooseTimeBinding binding;
    private HashMap<String,Boolean>selectedDys=new HashMap<String, Boolean>();
    private HashMap<String, String> param;
    private String day="";

    public FragmentChooseTime() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_time, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        param=(HashMap<String,String>)getArguments().getSerializable("param");
      binding.tv01PM.setOnClickListener(this::onSelectedTime);
      binding.tv02PM.setOnClickListener(this::onSelectedTime);
      binding.tv03PM.setOnClickListener(this::onSelectedTime);
      binding.tv04PM.setOnClickListener(this::onSelectedTime);
      binding.tv05Pm.setOnClickListener(this::onSelectedTime);
      binding.tv06Pm.setOnClickListener(this::onSelectedTime);
      binding.tv07PM.setOnClickListener(this::onSelectedTime);
      binding.tv08PM.setOnClickListener(this::onSelectedTime);
      binding.tv09PM.setOnClickListener(this::onSelectedTime);
      binding.tv10PM.setOnClickListener(this::onSelectedTime);
      binding.tv10AM.setOnClickListener(this::onSelectedTime);
      binding.tv11AM.setOnClickListener(this::onSelectedTime);
      binding.tv12AM.setOnClickListener(this::onSelectedTime);
      binding.tvTime.setOnClickListener(v->Tools.TimePicker(getContext(),binding.tvTime::setText,true,true));
      /*  binding.btnNext.setOnClickListener(v -> {
            StringBuilder times=new StringBuilder();
            for (Map.Entry<String, Boolean> set:selectedDys.entrySet()){
                if (set.getValue()){
                    times.append(set.getKey()+",");
                }
            }
            if (times.length()>0||!binding.tvTime.getText().toString().isEmpty()) {
                param.put("times",timesday);
                Bundle bundle=new Bundle();
                bundle.putSerializable("param",param);
                if (param.get("membership_id")!=null){
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_search_shop,bundle);
                }else {
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_payment_type,bundle);
                }
            }else {
                Toast.makeText(getContext(), "Select Time", Toast.LENGTH_SHORT).show();
            }
        });*/
        binding.btnNext.setOnClickListener(v -> {
            if (day.length()>0||!binding.tvTime.getText().toString().isEmpty()) {
                param.put("times",Tools.getCurrentDate()+" "+Tools.get().ChangeTimeFormat("hh:mm a","HH:mm:ss",day));
                Bundle bundle=new Bundle();
                bundle.putSerializable("param",param);
                if (param.get("membership_id")!=null){
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_search_shop,bundle);
                }else {
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_payment_type,bundle);
                }
            }else {
                Toast.makeText(getContext(), "Select Time", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateSelectedTime(View view) {
        TextView tv=(TextView)view;
        String day=tv.getText().toString().toLowerCase();
        boolean isDay=selectedDys.get(day)==null||!selectedDys.get(day);
        selectedDys.put(day,isDay);
        tv.setBackgroundResource(isDay?R.drawable.btn_primary:R.drawable.border_gray);
        tv.setTextColor(getActivity().getResources().getColor(isDay?R.color.white:R.color.black));


    }
    private void onSelectedTime(View view) {
        TextView tv=(TextView)view;
        binding.tv01PM.setBackgroundResource(R.drawable.border_gray);
        binding.tv02PM.setBackgroundResource(R.drawable.border_gray);
        binding.tv03PM.setBackgroundResource(R.drawable.border_gray);
        binding.tv04PM.setBackgroundResource(R.drawable.border_gray);
        binding.tv05Pm.setBackgroundResource(R.drawable.border_gray);
        binding.tv06Pm.setBackgroundResource(R.drawable.border_gray);
        binding.tv07PM.setBackgroundResource(R.drawable.border_gray);
        binding.tv08PM.setBackgroundResource(R.drawable.border_gray);
        binding.tv09PM.setBackgroundResource(R.drawable.border_gray);
        binding.tv10PM.setBackgroundResource(R.drawable.border_gray);
        binding.tv10AM.setBackgroundResource(R.drawable.border_gray);
        binding.tv11AM.setBackgroundResource(R.drawable.border_gray);
        binding.tv12AM.setBackgroundResource(R.drawable.border_gray);
        binding.tv01PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv02PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv03PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv04PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv04PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv05Pm.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv06Pm.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv07PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv08PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv09PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv10PM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv10AM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv11AM.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tv12AM.setTextColor(getActivity().getResources().getColor(R.color.black));
        tv.setBackgroundResource(R.drawable.btn_primary);
        tv.setTextColor(getActivity().getResources().getColor(R.color.white));
        day=tv.getText().toString().toLowerCase();
        Log.e("Time","==>"+Tools.getCurrentDate()+" "+Tools.get().ChangeTimeFormat("hh:mm a","HH:mm:ss",day));

    }



}
