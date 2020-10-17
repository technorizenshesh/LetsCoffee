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

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentCreateAccountBinding;
import com.letscoffee.databinding.FragmentEditProfileBinding;
import com.utils.Session.SessionKey;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentEditProfile extends Fragment {
    private FragmentEditProfileBinding binding;
    private SessionManager session;
    public FragmentEditProfile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session = SessionManager.get(getContext());
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.btnUpdate.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.etFullname.setText(session.getValue(SessionKey.user_name));
        binding.etEmail.setText(session.getValue(SessionKey.email));
        binding.etMobile.setText(session.getValue(SessionKey.mobile));
        binding.tvDob.setText(session.getValue(SessionKey.dob));
        binding.tvDob.setOnClickListener(v -> {
            new SingleDateAndTimePickerDialog.Builder(getContext())
                    .bottomSheet()
                    .backgroundColor(getContext().getColor(R.color.black))
                    .mainColor(getContext().getColor(R.color.white))
                    .titleTextColor(getContext().getColor(R.color.white))
                    .displayMinutes(false)
                    .displayHours(false)
                    .displayDays(false)
                    .displayMonth(true)
                    .displayYears(true)
                    .displayDaysOfMonth(true)
                    .title("Select Date of Barth")
                    .listener(date->{
                        binding.tvDob.setText(Tools.get().getChangeDateFormat(date));
                    })
                    .display();
        });
    }

    private boolean Validation() {
        boolean valid = false;
        if (binding.etEmail.getText().toString().isEmpty()) {
            binding.etEmail.setError(getResources().getString(R.string.required));
            binding.etEmail.requestFocus();
        }  else {
            valid = true;
        }
        return valid;
    }

    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("mobile", binding.etEmail.getText().toString());
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
