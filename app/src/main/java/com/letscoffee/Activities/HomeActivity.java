package com.letscoffee.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.letscoffee.Constant.BaseClass;
import com.letscoffee.Fragments.FragmentWebView;
import com.letscoffee.R;
import com.letscoffee.databinding.ActivityHomeBinding;
import com.squareup.picasso.Picasso;
import com.utils.Session.SessionKey;
import com.utils.Session.SessionManager;
import com.utils.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private SessionManager session;
    private NavController navControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.get().updateResources(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        Tools.get().updateView(this,binding.getRoot());
        BindView();
        OpenRequest();
    }

    private void OpenRequest() {
        if (getIntent().getExtras()!=null){
            String OpenRequest=getIntent().getExtras().getString("open_request");
            if (OpenRequest.equals("MyOrder")){
                navControler.navigate(R.id.nav_order);
            }
        }
    }

    private void BindView() {
        session = SessionManager.get(this);
        navControler = Navigation.findNavController(this, R.id.nav_host_home);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onMenuItemSelected);
        binding.tvMyProfile.setOnClickListener(v->{
            if (session.isUserLogin()) {
                navControler.navigate(R.id.nav_edit_profile);
            }else {
                navControler.navigate(R.id.nav_login);
            }
            DrawerControler();
        });
        binding.tvMembership.setOnClickListener(v->{
            DrawerControler();
            if (!session.isUserLogin()){
                navControler.navigate(R.id.nav_login);
                return;
            }
            navControler.navigate(R.id.nav_membership);

        });
        binding.tvMyMembership.setOnClickListener(v->{
            navControler.navigate(R.id.nav_my_membership);
            DrawerControler();
        });
        binding.tvOrder.setOnClickListener(v->{
            navControler.navigate(R.id.nav_order);
            DrawerControler();
        });
        binding.tvSetting.setOnClickListener(v->{
            navControler.navigate(R.id.nav_setting);
            DrawerControler();
        });
        binding.tvAboutUs.setOnClickListener(v->{
            new FragmentWebView().setData(getString(R.string.about_us),BaseClass.get().AboutUs()).show(getSupportFragmentManager(),"");
            DrawerControler();
        });
        binding.tvPrivacyPolicy.setOnClickListener(v->{
            new FragmentWebView().setData(getString(R.string.privacy_policy),BaseClass.get().PrivacyPolicy()).show(getSupportFragmentManager(),"");
            DrawerControler();
        });
        binding.btnLogout.setOnClickListener(v->{
            session.Logout();
            startActivity(new Intent(this, FirstActivity.class));
            finish();
        });
        setData();
        setLayout();
    }
    private void setLayout() {
        if (session.getSelectedLanguage().equals("ar")) {
            binding.tvMyProfile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvMembership.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvOrder.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvSetting.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvAboutUs.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvPrivacyPolicy.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.btnLogout.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvContactUs.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
            binding.tvMyMembership.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_left, 0);
        }
    }

    private void setData() {
        Menu menu =binding.bottomNavigation.getMenu();
        if (session.isUserLogin()){
            Picasso.get().load(session.getValue(SessionKey.image)).placeholder(R.drawable.defult_user).into(binding.userImage);
            binding.tvUserName.setText(session.getValue(SessionKey.user_name));
        }
        menu.getItem(1).setVisible(session.isUserLogin());
        menu.getItem(2).setVisible(session.isUserLogin());
        binding.imgCard.setImageResource(session.isUserLogin()?R.drawable.ic_shopping_cart:R.drawable.ic_home);
        binding.tvMyProfile.setVisibility(session.isUserLogin()?View.VISIBLE:View.GONE);
        binding.btnLogout.setVisibility(session.isUserLogin()?View.VISIBLE:View.GONE);
        binding.tvOrder.setVisibility(session.isUserLogin()?View.VISIBLE:View.GONE);
        binding.tvMyMembership.setVisibility(session.isUserLogin()?View.VISIBLE:View.GONE);

    }

    public void DrawerControler(){
        if (binding.navDrawer.isDrawerOpen(GravityCompat.START)){
            binding.navDrawer.closeDrawer(GravityCompat.START);
        }else {
            binding.navDrawer.openDrawer(GravityCompat.START);
        }
    }

    private boolean onMenuItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                navControler.navigate(R.id.nav_home);
                break;
            case R.id.menu_profile:
                navControler.navigate(R.id.nav_profile);
                break;
            case R.id.menu_shop:
                navControler.navigate(R.id.nav_cart);
                break;
        }
        return true;
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.isUserLogin()) {
            getProfile();
        }
    }

    private void getProfile(){
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id",session.getUserID());
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getProfile())
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("UserDetails","====>"+response);
                try {
                    JSONObject object=new JSONObject(response);
                    boolean status=object.getString("status").contains("1");
                    if (status){
                        session.CreateSession(object.getString("result"));
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
