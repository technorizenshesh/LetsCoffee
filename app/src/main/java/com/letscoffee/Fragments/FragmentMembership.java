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
import com.letscoffee.Adapters.AdapterMembership;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Models.ModelCategory;
import com.letscoffee.Models.ModelMembership;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentFirstBinding;
import com.letscoffee.databinding.FragmentMembershipBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentMembership extends Fragment {
    private FragmentMembershipBinding binding;
    private ArrayList<ModelMembership>memberships=new ArrayList<>();
    private SessionManager session;
    private AdapterMembership adapter;

    public FragmentMembership() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_membership, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        session = SessionManager.get(getContext());
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
       adapter=new AdapterMembership(getContext(),memberships).Callback(this::onSelectMembership);
       binding.recycleMembership.setAdapter(adapter);
       binding.swipeRefresh.setOnRefreshListener(this::getMembership);
        getMembership();
    }

    private void onSelectMembership(ModelMembership modelMembership) {
        HashMap<String,String>param=new HashMap<>();
        param.put("membership_id",modelMembership.getId());
        Bundle bundle=new Bundle();
        bundle.putSerializable("param",param);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_choose_day,bundle);
    }

    private void getMembership(){
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getMembership())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object=new JSONObject(response);
                            boolean status=object.getString("status").contains("1");
                            if (status){
                                memberships.clear();
                                JSONArray array=object.getJSONArray("result");
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject=array.getJSONObject(i);
                                    ModelMembership membership=new ModelMembership();
                                    membership.setId(jsonObject.getString("id"));
                                    membership.setName(jsonObject.getString("name"));
                                    membership.setAmount(jsonObject.getString("amount"));
                                    memberships.add(membership);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                        binding.swipeRefresh.setRefreshing(false);
                    }
                });
    }
}
