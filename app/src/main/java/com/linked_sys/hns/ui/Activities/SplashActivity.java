package com.linked_sys.hns.ui.Activities;

import android.os.Bundle;
import android.os.Handler;

import com.linked_sys.hns.R;


public class SplashActivity extends ParentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SPLASH_TIME_OUT = 0;
        if (session.isLoggedIn()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            }, SPLASH_TIME_OUT);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    session.checkLogin();
//                    openActivity(CommingSoonActivity.class);
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.splash_activity;
    }
}
