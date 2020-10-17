package com.letscoffee.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentProfileBinding;
import com.letscoffee.databinding.FragmentSettingBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentSetting extends Fragment {
    private FragmentSettingBinding binding;
    private SessionManager session;

    public FragmentSetting() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session = SessionManager.get(getContext());
        binding.rbEnglish.setChecked(session.getSelectedLanguage().equals("en"));
        binding.rbArabic.setChecked(session.getSelectedLanguage().equals("ar"));
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.rbArabic.setOnClickListener(this::AllCheckListener);
        binding.rbEnglish.setOnClickListener(this::AllCheckListener);
       binding.btnApply.setOnClickListener(v->{
           if (binding.rbEnglish.isChecked()){
               session.setLanguage("en");
           } else if (binding.rbArabic.isChecked()){
               session.setLanguage("ar");
           }
           startActivity(new Intent(getActivity(),HomeActivity.class));
           getActivity().finish();
       });
    }
    void AllCheckListener(View buttonView){
        binding.rbEnglish.setChecked(false);
        binding.rbArabic.setChecked(false);
        ((RadioButton)buttonView).setChecked(true);
    }

}
