package com.letscoffee.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.letscoffee.R;
import com.utils.Session.SessionManager;


public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 2;
    String[] mPermission = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActivityCompat.requestPermissions(this, mPermission, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAccepted=false;
       for (int i:grantResults){
           isAccepted=i==0;
           if (!isAccepted)break;
       }
        if (isAccepted) {
            Handlers();
        } else {
            ActivityCompat.requestPermissions(this, mPermission, REQUEST_CODE_PERMISSION);
        }
    }
    void Handlers(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SessionManager.get(SplashActivity.this).isUserLogin()&&SessionManager.get(SplashActivity.this).isUserActive()){
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                }else {
                    startActivity(new Intent(SplashActivity.this,FirstActivity.class));
                }
                finish();
            }
        },1000);
    }
}
