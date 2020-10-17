package com.letscoffee.Fragments;


import android.content.Intent;
import android.net.Uri;
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
import com.letscoffee.Adapters.AdapterCategory;
import com.letscoffee.Adapters.AdapterCategoryName;
import com.letscoffee.Adapters.AdapterItem;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Interfaces.onSelectItemListener;
import com.letscoffee.Models.ModelCategory;
import com.letscoffee.Models.ModelItem;
import com.letscoffee.Models.ModelShop;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentShopDetailsBinding;
import com.letscoffee.databinding.FragmentViewCategoryBinding;
import com.squareup.picasso.Picasso;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

public class FragmentShopDetails extends Fragment implements onSelectItemListener {
    private FragmentShopDetailsBinding binding;
    private ArrayList<ModelCategory> categories=new ArrayList<>();
    private ArrayList<ModelItem> items=new ArrayList<>();
    private SessionManager session;
    private ModelShop shop;
    private AdapterCategoryName catAdapter;
    private AdapterItem ItemAdapter;
    private String CatID;
    private HashMap<String, String> param;

    public FragmentShopDetails() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop_details, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        if (getArguments().getSerializable("param")!=null){
            param=(HashMap<String,String>)getArguments().getSerializable("param");
        }
        shop=(ModelShop)getArguments().getSerializable("data");
        session = SessionManager.get(getContext());
        catAdapter=new AdapterCategoryName(getContext(),categories).Callback(this::onSelectCategory);
        ItemAdapter=new AdapterItem(getContext(),items).Callback(this);
        binding.recycleCategory.setAdapter(catAdapter);
        binding.recycleItem.setAdapter(ItemAdapter);
       binding.imgBack.setOnClickListener(v -> {
           Navigation.findNavController(binding.getRoot()).popBackStack();
       });
        Picasso.get().load(shop.getImage()).placeholder(R.drawable.logo).into(binding.img);
        binding.tvTitle.setText(shop.getName());
        binding.ratingbar.setRating(Float.valueOf(shop.getRating()));
        binding.tvRating.setText(shop.getRating());
        binding.tvTime.setText("Open Time "+shop.getOpen_time()+" Close  Time "+shop.getClose_time());
        binding.tvAddress.setText(shop.getAddress());
        getCategory();
        binding.swipeRefresh.setOnRefreshListener(()->{binding.swipeRefresh.setRefreshing(false);});
        NotifyCard();
        binding.card.setOnClickListener(v->{
            if (!session.isUserLogin()){
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_login);
                return;
            }
            Bundle bundle=new Bundle();
            if (param!=null){
                bundle.putSerializable("param",param);
            }
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_card,bundle);
        });
        binding.tvDirections.setOnClickListener(v->{
         /*   Bundle bundle=new Bundle();
            bundle.putSerializable("data",shop);
           Navigation.findNavController(binding.getRoot()).navigate(R.id.action_tracking,bundle);*/
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+shop.getLat()+","+shop.getLon()));
            startActivity(intent);
        });
        binding.imgShare.setOnClickListener(v->{
            Tools.get().Share(getActivity(),shop.getName(),shop.getDescription(),shop.getImage());
        });
        binding.imgFavorite.setOnClickListener(v->{
            AddToFavorite();
        });
        getFavorite();
    }

    private void AddToFavorite() {
        binding.swipeRefresh.setRefreshing(true);
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id",session.getUserID());
        param.put("shop_id",shop.getId());
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().addFavRestaurant())
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("AddToFavorite","======>"+response);
                binding.swipeRefresh.setRefreshing(false);
                try {
                    JSONObject object=new JSONObject(response);
                    String message=object.getString("status");
                    binding.imgFavorite.setImageResource(message.equals("1")?R.drawable.ic_star_fill:R.drawable.ic_star_border);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Failed(String error) {

            }
        });
    }
    private void getFavorite() {
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id",session.getUserID());
        param.put("shop_id",shop.getId());
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getFavRestaurant())
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("getFavRestaurant","======>"+response);
                try {
                    JSONObject object=new JSONObject(response);
                    String message=object.getString("status");
                    binding.imgFavorite.setImageResource(message.equals("1")?R.drawable.ic_star_fill:R.drawable.ic_star_border);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Failed(String error) {

            }
        });
    }

    private void onSelectCategory(ModelCategory category) {
        this.CatID=category.getId();
        getItemList();
    }

    private void getCategory(){
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getCategory())
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("Category","====>"+response);
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object=new JSONObject(response);
                            boolean status=object.getString("status").contains("1");
                            if (status){
                                categories.clear();
                                JSONArray array=object.getJSONArray("result");
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject=array.getJSONObject(i);
                                    ModelCategory category=new ModelCategory();
                                    category.setId(jsonObject.getString("id"));
                                    category.setName(session.isArabic()?jsonObject.getString("arabic_name"):jsonObject.getString("category_name"));
                                    category.setImage(jsonObject.getString("image"));
                                    category.setSelected(i==0);
                                    categories.add(category);
                                }
                                catAdapter.notifyDataSetChanged();
                                if (categories.size()>0) {
                                    CatID =categories.get(0).getId();
                                    getItemList();
                                }
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
    private void getItemList(){
        HashMap<String,String>param=new HashMap<>();
        param.put("category_id",CatID);
        param.put("shop_id",shop.getId());
        binding.swipeRefresh.setRefreshing(true);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getItemListByCatShopID())
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("Items","===>"+response);
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object=new JSONObject(response);
                            boolean status=object.getString("status").contains("1");
                            if (status){
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
                                    item.setType(jsonObject.getString("type"));
                                    items.add(item);
                                }
                                ItemAdapter.notifyDataSetChanged();
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
    private void NotifyCard(){
       binding.tvCount.setText(""+session.getCardCount());
//       binding.card.setVisibility(session.getCardCount()>0?View.VISIBLE:View.GONE);
       binding.card.setVisibility(View.GONE);
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
        if (!session.isUserLogin()){
         Navigation.findNavController(binding.getRoot()).navigate(R.id.action_login);
         return;
        }
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
                Log.e("Response","====>"+response);
                binding.swipeRefresh.setRefreshing(false);
                try {
                    JSONObject object=new JSONObject(response);
                    boolean status=object.getString("status").contains("1");
                    if (status){
                        session.setCardCount(session.getCardCount()+qty);
                        NotifyCard();
                    }
                    Toast.makeText(getContext(), "Card Updated", Toast.LENGTH_SHORT).show();
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
