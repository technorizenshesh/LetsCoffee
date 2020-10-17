package com.letscoffee.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.letscoffee.Adapters.AdapterCartItem;
import com.letscoffee.Adapters.AdapterCategoryName;
import com.letscoffee.Adapters.AdapterItem;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Interfaces.onSelectItemListener;
import com.letscoffee.Models.ModelCategory;
import com.letscoffee.Models.ModelItem;
import com.letscoffee.Models.ModelShop;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentCartBinding;
import com.letscoffee.databinding.FragmentShopDetailsBinding;
import com.squareup.picasso.Picasso;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;

public class FragmentCart extends Fragment implements onSelectItemListener {
    private FragmentCartBinding binding;
    private ArrayList<ModelItem> items=new ArrayList<>();
    private SessionManager session;
    private AdapterCartItem ItemAdapter;
    private HashMap<String, String> param;
    private String total="";

    public FragmentCart() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        if (getArguments()!=null){
          param = (HashMap<String, String>) getArguments().getSerializable("param");

        }
        session = SessionManager.get(getContext());
        ItemAdapter=new AdapterCartItem(getContext(),items).Callback(this);
        binding.recycleItem.setAdapter(ItemAdapter);
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
        getItemList();
        binding.swipeRefresh.setOnRefreshListener(()->{ getItemList();});
        binding.btnPayNow.setOnClickListener(v->{
            if (param==null) {
                param = new HashMap<>();
            }
            param.put("user_id", session.getUserID());
            double total = 0;
            StringBuilder shop_id = new StringBuilder();
            StringBuilder item_id = new StringBuilder();
            StringBuilder quantity = new StringBuilder();
            try {
                JSONArray array=new JSONArray();
                for (ModelItem item : items) {
                    if (item.getSelectedQty() > 0) {
                        JSONObject object=new JSONObject();
                        object.put("item_id",item.getId());
                        object.put("item_name",item.getName());
                        object.put("item_price",item.getPrice());
                        object.put("item_qty",item.getSelectedQty());
                        array.put(object);
                        total = total + (item.getSelectedQty() * Double.valueOf(item.getPrice()));
                        shop_id.append(item.getShop_id() + ",");
                        item_id.append(item.getId() + ",");
                        quantity.append(item.getSelectedQty() + ",");
                    }
                    param.put("order", "" + array.toString());
                }}catch (JSONException e){
            }
                param.put("amount", "" + total);
                param.put("total", "" + total);
                param.put("shop_id", shop_id.toString().substring(0, shop_id.length() - 1));
                param.put("item_id", item_id.toString().substring(0, item_id.length() - 1));
                param.put("cart_id", item_id.toString().substring(0, item_id.length() - 1));
                param.put("quantity", quantity.toString().substring(0, quantity.length() - 1));

            Bundle bundle=new Bundle();
            bundle.putSerializable("param",param);
            if (param.get("membership_id")!=null) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_payment_type, bundle);
            }else {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_choose_time, bundle);
            }
        });
    }
    private void getItemList(){
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id",session.getUserID());
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getCart())
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("Card","=====>"+response);
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object=new JSONObject(response);
                            boolean status=object.getString("status").contains("1");
                            if (status){
                                String tax="0";
                                items.clear();
                                JSONArray array=object.getJSONArray("result");
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject=array.getJSONObject(i);
                                    ModelItem item=new ModelItem();
                                    item.setId(jsonObject.getString("id"));
                                    item.setShop_id(jsonObject.getString("shop_id"));
                                    item.setName(session.isArabic()?jsonObject.getString("arabic_name"):jsonObject.getString("name"));
                                    item.setImage(jsonObject.getString("image"));
                                    item.setDescription(session.isArabic()?jsonObject.getString("arabic_description"):jsonObject.getString("description"));
                                    item.setPrice(jsonObject.getString("price"));
                                    item.setMax_quantity(jsonObject.getString("max_quantity"));
                                    item.setSize(jsonObject.getString("size"));
                                    item.setDiscount_type(jsonObject.getString("discount_type"));
                                    item.setDiscount(jsonObject.getString("discount"));
                                    item.setSelectedQty(Integer.parseInt(jsonObject.getString("quantity")));
                                    if (!jsonObject.getString("tax").equals("")){
                                        tax=jsonObject.getString("tax");
                                    }
                                    items.add(item);
                                }
                                total = object.getString("total_with_tax");
                                session.setCardCount(items.size());
                                ItemAdapter.notifyDataSetChanged();
                                binding.tvItemTax.setText(getResources().getString(R.string.currency)+" "+tax);
                                binding.tvItemTotal.setText(getResources().getString(R.string.currency)+" " +object.getString("total_amount"));
                                binding.tvTotal.setText(getResources().getString(R.string.currency)+" " +total);
                                binding.bottomView.setVisibility(items.size()>0?View.VISIBLE:View.GONE);
                                binding.tvNoRecord.setVisibility(items.size()>0?View.GONE:View.VISIBLE);
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


    @Override
    public void onAddToCart(ModelItem item) {
        AddToCart(item,+1);
    }

    @Override
    public void onRemoverToCart(ModelItem item) {
        AddToCart(item,-1);
    }

    @Override
    public void onItemDetails(ModelItem item) {

    }

    private void AddToCart(ModelItem item,int qty){
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id",session.getUserID());
        param.put("shop_id",item.getShop_id());
        param.put("item_id",item.getId());
        param.put("quantity",""+qty);
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getContext()).setUrl(BaseClass.get().addCart())
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                binding.swipeRefresh.setRefreshing(false);
                try {
                    JSONObject object=new JSONObject(response);
                    boolean status=object.getString("status").contains("1");
                    Toast.makeText(getContext(), ""+object.getString("message"), Toast.LENGTH_SHORT).show();
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
