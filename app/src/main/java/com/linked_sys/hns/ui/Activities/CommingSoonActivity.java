package com.linked_sys.hns.ui.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.linked_sys.hns.R;


public class CommingSoonActivity extends ParentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.comming_soon_activity;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
