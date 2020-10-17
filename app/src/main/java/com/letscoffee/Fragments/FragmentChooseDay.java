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

import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentChooseDayBinding;
import com.letscoffee.databinding.FragmentForgotPasswordBinding;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentChooseDay extends Fragment {
    private FragmentChooseDayBinding binding;
    private HashMap<String,Boolean>selectedDys=new HashMap<String, Boolean>();
    private HashMap<String, String> param;

    public FragmentChooseDay() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_day, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        param=(HashMap<String,String>)getArguments().getSerializable("param");
      binding.tvMonthly.setOnClickListener(this::UpdateType);
      binding.tvWeekly.setOnClickListener(this::UpdateType);
      binding.tvSunday.setOnClickListener(this::UpdateSelectedDays);
      binding.tvMonday.setOnClickListener(this::UpdateSelectedDays);
      binding.tvTuesday.setOnClickListener(this::UpdateSelectedDays);
      binding.tvWednesday.setOnClickListener(this::UpdateSelectedDays);
      binding.tvThursday.setOnClickListener(this::UpdateSelectedDays);
      binding.tvFriday.setOnClickListener(this::UpdateSelectedDays);
      binding.tvSaturday.setOnClickListener(this::UpdateSelectedDays);
      binding.tvEveryday.setOnClickListener(this::UpdateSelectedDays);
      binding.tvTime.setOnClickListener(v->Tools.TimePicker(getContext(),binding.tvTime::setText,true,true));
        binding.btnSubmit.setOnClickListener(v -> {
            StringBuilder days=new StringBuilder();
            for (Map.Entry<String, Boolean> set:selectedDys.entrySet()){
                if (set.getValue()){
                    days.append(set.getKey()+",");
                }
            }
            if (days.length()>0){
                param.put("days",days.toString());
                Bundle bundle=new Bundle();
                bundle.putSerializable("param",param);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_choose_time,bundle);
            }else {
                Toast.makeText(getContext(), "Select Day", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void UpdateSelectedDays(View view) {
        TextView tv=(TextView)view;
        boolean isAllSelected=false;
        String day=tv.getText().toString().toLowerCase();
        if (day.equals("everyday")){
            binding.tvEveryday.setBackgroundResource(R.drawable.btn_primary);
            binding.tvEveryday.setTextColor(getActivity().getResources().getColor(R.color.white));
            return;
        }
        boolean isDay=selectedDys.get(day)==null||!selectedDys.get(day);
        selectedDys.put(day,isDay);
        if (selectedDys.size()==7) {
            for (Map.Entry<String, Boolean> entry : selectedDys.entrySet()) {
                isAllSelected = entry.getValue();
                if (!isAllSelected) break;
            }
        }
        Log.e(day,"===>"+isAllSelected);
        tv.setBackgroundResource(isDay?R.drawable.btn_primary:R.drawable.border_gray);
        tv.setTextColor(getActivity().getResources().getColor(isDay?R.color.white:R.color.black));
        binding.tvEveryday.setBackgroundResource(isAllSelected?R.drawable.btn_primary:R.drawable.border_gray);
        binding.tvEveryday.setTextColor(getActivity().getResources().getColor(isAllSelected?R.color.white:R.color.black));
    }

    private void UpdateType(View view) {
        binding.tvMonthly.setBackgroundResource(R.drawable.border_gray);
        binding.tvWeekly.setBackgroundResource(R.drawable.border_gray);
        binding.tvWeekly.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tvMonthly.setTextColor(getActivity().getResources().getColor(R.color.black));
        ((TextView)view).setBackgroundResource(R.drawable.btn_primary);
        ((TextView)view).setTextColor(getActivity().getResources().getColor(R.color.white));
    }

    private boolean Validation() {
        boolean valid = false;

        return valid;
    }

    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();

        return param;
    }


}
