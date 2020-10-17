package com.letscoffee.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.R;
import com.letscoffee.databinding.FragmentChooseTimeBinding;
import com.letscoffee.databinding.FragmentPaymentTypeBinding;
import com.utils.Utils.Tools;

import java.util.HashMap;

public class FragmentPaymentType extends Fragment {
    private FragmentPaymentTypeBinding binding;
    private HashMap<String, String> param;

    public FragmentPaymentType() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_type, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
       param= (HashMap<String,String>)getArguments().getSerializable("param");;
     binding.btnNext.setOnClickListener(v -> {
         Bundle bundle=new Bundle();
         bundle.putSerializable("param",param);
         if (binding.applePay.isChecked()) {
             param.put("payment_type","apple_pay");
             Toast.makeText(getContext(), "On Development Mode please select other one", Toast.LENGTH_SHORT).show();
         }if (binding.csloPay.isChecked()) {
             param.put("payment_type","cslo");
             Navigation.findNavController(binding.getRoot()).navigate(R.id.action_add_to_card,bundle);
         }if (binding.masterCardPay.isChecked()) {
             param.put("payment_type","master_card");
             Navigation.findNavController(binding.getRoot()).navigate(R.id.action_add_to_card,bundle);
         }if (binding.visaPay.isChecked()) {
             param.put("payment_type","visa");
             Navigation.findNavController(binding.getRoot()).navigate(R.id.action_add_to_card,bundle);
         }

        });
     binding.applePay.setOnClickListener(this::updateCheck);
     binding.csloPay.setOnClickListener(this::updateCheck);
     binding.masterCardPay.setOnClickListener(this::updateCheck);
     binding.visaPay.setOnClickListener(this::updateCheck);
    }

    private void updateCheck(View view) {
        binding.applePay.setChecked(false);
        binding.csloPay.setChecked(false);
        binding.masterCardPay.setChecked(false);
        binding.visaPay.setChecked(false);
        ((RadioButton)view).setChecked(true);
    }



}
