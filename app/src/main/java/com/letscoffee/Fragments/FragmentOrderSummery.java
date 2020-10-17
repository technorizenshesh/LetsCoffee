package com.letscoffee.Fragments;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.zxing.WriterException;
import com.letscoffee.Adapters.AdapterCartItem;
import com.letscoffee.Adapters.AdapterSemmeryItem;
import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Interfaces.onSelectItemListener;
import com.letscoffee.Models.ModelItem;
import com.letscoffee.R;
import com.letscoffee.databinding.FragmentAddAddressBinding;
import com.letscoffee.databinding.FragmentOrderSummeryBinding;
import com.letscoffee.databinding.LayoutMyOrderBinding;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.WriteAbortedException;
import java.util.ArrayList;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.ProgressStyle;

import static android.content.ContentValues.TAG;

public class FragmentOrderSummery extends Fragment {
    private FragmentOrderSummeryBinding binding;
    private SessionManager session;
    private ArrayList<ModelItem> items=new ArrayList<>();
    private AdapterSemmeryItem ItemAdapter;
    private String orderID;

    public FragmentOrderSummery() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tools.get().updateResources(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_summery, container, false);
        BindView();
        return binding.getRoot();
    }

    private void BindView() {
        if (getArguments().getString("result")!=null) {
            orderID = getArguments().getString("result");
        }else {
            orderID = getArguments().getString("orderID");
        }
        session = SessionManager.get(getContext());
        ItemAdapter=new AdapterSemmeryItem(getContext(),items);
        binding.recycleItem.setAdapter(ItemAdapter);
        binding.imgBack.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
        binding.barcode.setImageBitmap(generateQrCode(orderID));
        getItemList();
    }
    private void getItemList(){
        HashMap<String,String>param=new HashMap<>();
        param.put("order_id",orderID);
        ApiCallBuilder.build(getActivity()).setUrl(BaseClass.get().getOrderDetails())
                .setParam(param).isShowProgressBar(true)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("OrderDetails","===>"+response);
                        String req_datetime="",date_time="",order_id="";
                        try {
                            JSONObject object=new JSONObject(response);
                            boolean status=object.getString("status").contains("1");
                            if (status){
                                items.clear();
                                JSONArray array=object.getJSONArray("result");
                                JSONObject obj=array.getJSONObject(0);
                                req_datetime=obj.getString("req_datetime");
                                date_time=obj.getString("times");
                                order_id=obj.getString("order_id");
                                String tax = "";
                                JSONArray itemList=obj.getJSONArray("item_list");
                                for (int i=0;i<itemList.length();i++){
                                    JSONObject jsonObject=itemList.getJSONObject(i);
                                    ModelItem item=new ModelItem();
                                    item.setId(jsonObject.getString("id"));
                                    item.setShop_id(jsonObject.getString("shop_id"));
                                    item.setName(session.isArabic()?jsonObject.getString("arabic_name"):jsonObject.getString("name"));
                                    item.setSize(jsonObject.getString("size"));
                                    item.setSelectedQty(Integer.parseInt(jsonObject.getString("new_quantity")));
                                    if (!jsonObject.getString("tax").equals("")){
                                        tax=jsonObject.getString("tax");
                                    }
                                    items.add(item);
                                }
                                ItemAdapter.notifyDataSetChanged();
                                binding.tvReceiveDate.setText(Tools.ChangeDateFormat("dd-MMM-yyyy",date_time));
                                binding.tvRequestDate.setText(Tools.ChangeDateFormat("dd-MMM-yyyy",req_datetime));
                                binding.tvReceiveTime.setText(Tools.ChangeDateFormat("hh:mm a",date_time));
                                binding.tvRequestTime.setText(Tools.ChangeDateFormat("hh:mm a",req_datetime));
                                binding.tvTotal.setText(getResources().getString(R.string.currency)+" " +object.getString("total_with_tax"));
                                binding.tvItemTotal.setText(getResources().getString(R.string.currency)+" " +object.getString("total_amount"));
                                binding.tvOrderId.setText("ORDER ID: "+order_id);
                                binding.tvItemTax.setText(getResources().getString(R.string.currency)+" "+tax);
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


    public static Bitmap generateQrCode(String myCodeText) {
        QRGEncoder qrgEncoder = new QRGEncoder(myCodeText, null, QRGContents.Type.TEXT, 100);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);
        return qrgEncoder.getBitmap();
    }
    }

