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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentAddAddressBinding;
import com.letscoffee.databinding.FragmentCreateAccountBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentAddAddress extends Fragment {
    private FragmentAddAddressBinding binding;
    private SessionManager session;
    private HashMap<String, String> param;

    public FragmentAddAddress() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_address, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        binding.tvHome.setBackgroundResource(R.drawable.btn_primary);
        binding.tvHome.setTextColor(getActivity().getResources().getColor(R.color.white));
        param=(HashMap<String,String>)getArguments().getSerializable("param");
        session = SessionManager.get(getContext());
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.tvHome.setOnClickListener(this::UpdateType);
        binding.tvWork.setOnClickListener(this::UpdateType);
        binding.tvOther.setOnClickListener(this::UpdateType);
        binding.btnSubmit.setOnClickListener(v -> {
            if (Validation()){
                String address=binding.etHouseFlatNo.getText().toString()+","+binding.etLandmark.getText().toString()+","+binding.etLocation.getText().toString();
                param.put("address",address);
                Bundle bundle=new Bundle();
                bundle.putSerializable("param",param);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_order_summery,bundle);
            }
        });

    }
    private void UpdateType(View view) {
        binding.tvHome.setBackgroundResource(R.drawable.border_gray);
        binding.tvWork.setBackgroundResource(R.drawable.border_gray);
        binding.tvOther.setBackgroundResource(R.drawable.border_gray);
        binding.tvHome.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tvWork.setTextColor(getActivity().getResources().getColor(R.color.black));
        binding.tvOther.setTextColor(getActivity().getResources().getColor(R.color.black));
        ((TextView)view).setBackgroundResource(R.drawable.btn_primary);
        ((TextView)view).setTextColor(getActivity().getResources().getColor(R.color.white));
    }
    private boolean Validation() {
        boolean valid = false;
        if (binding.etLocation.getText().toString().isEmpty()) {
            binding.etLocation.setError(getResources().getString(R.string.required));
            binding.etLocation.requestFocus();
        }else if (binding.etHouseFlatNo.getText().toString().isEmpty()) {
            binding.etHouseFlatNo.setError(getResources().getString(R.string.required));
            binding.etHouseFlatNo.requestFocus();
        }else if (binding.etLandmark.getText().toString().isEmpty()) {
            binding.etLandmark.setError(getResources().getString(R.string.required));
            binding.etLandmark.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }

    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("lat", "");
        param.put("lon", "");
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
