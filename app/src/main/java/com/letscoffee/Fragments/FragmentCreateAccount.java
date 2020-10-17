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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentCreateAccountBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentCreateAccount extends Fragment {
    private FragmentCreateAccountBinding binding;
    private SessionManager session;
    private String FireBaseToken;

    public FragmentCreateAccount() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_account, container, false);
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
            }
        });
        binding.btnCreateAccount.setOnClickListener(v -> {
            if (Validation()){
                Continue();
            }
        });
        binding.tvDob.setOnClickListener(v -> {
//          Tools.DatePicker(getContext(),binding.tvDob::setText);
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
        if (binding.etFullname.getText().toString().isEmpty()) {
            binding.etFullname.setError(getResources().getString(R.string.required));
            binding.etFullname.requestFocus();
        } if (binding.etEmail.getText().toString().isEmpty()) {
            binding.etEmail.setError(getResources().getString(R.string.required));
            binding.etEmail.requestFocus();
        }if (binding.etMobile.getText().toString().isEmpty()) {
            binding.etMobile.setError(getResources().getString(R.string.required));
            binding.etMobile.requestFocus();
        } else if (binding.etPassword.getText().toString().isEmpty()) {
            binding.etPassword.setError(getResources().getString(R.string.required));
            binding.etPassword.requestFocus();
        }else if (binding.etCnfpassword.getText().toString().isEmpty()) {
            binding.etCnfpassword.setError(getResources().getString(R.string.required));
            binding.etCnfpassword.requestFocus();
        }else if (binding.tvDob.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), getResources().getString(R.string.date_of_barth_required), Toast.LENGTH_SHORT).show();
        } else if (!binding.etPassword.getText().toString().equals(binding.etCnfpassword.getText().toString())) {
            binding.etCnfpassword.setError(getResources().getString(R.string.no_match));
            binding.etCnfpassword.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }

    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("password", binding.etPassword.getText().toString());
        param.put("email", binding.etEmail.getText().toString());
        param.put("user_name", binding.etFullname.getText().toString());
        param.put("mobile", binding.etMobile.getText().toString());
        param.put("dob", binding.tvDob.getText().toString());
        param.put("lat", "");
        param.put("lon", "");
        param.put("register_id", FireBaseToken);
        return param;
    }

    private void Continue() {
        ApiCallBuilder.build(getActivity())
                .isShowProgressBar(true, ProgressStyle.STYLE_2)
                .setParam(getParam())
                .setUrl(BaseClass.get().SignUp())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("Login", "=====>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean status = object.getString("status").contains("1");
                            if (status) {
                                session.CreateSession(object.getString("result"));
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_verify);
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
