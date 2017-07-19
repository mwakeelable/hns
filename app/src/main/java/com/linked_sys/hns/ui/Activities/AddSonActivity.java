package com.linked_sys.hns.ui.Activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.linked_sys.hns.R;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class AddSonActivity extends ParentActivity {
    RelativeLayout msg_container;
    LinearLayout btn_add;
    EditText txt_sID;
    TextView txt_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.nav_add_son));
        msg_container = (RelativeLayout) findViewById(R.id.add_msg_container);
        txt_sID = (EditText) findViewById(R.id.txt_son_id);
        btn_add = (LinearLayout) findViewById(R.id.btn_add);
        txt_msg = (TextView) findViewById(R.id.txt_add_msg);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_sID.getText().toString().equals("")) {
                    new MaterialDialog.Builder(AddSonActivity.this)
                            .title(getResources().getString(R.string.login_error))
                            .content(getResources().getString(R.string.txt_add_son_error))
                            .positiveText(getResources().getString(R.string.login_positive_btn))
                            .show();
                } else
                    addSon(txt_sID.getEditableText().toString());
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_son;
    }

    private void addSon(String id) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ParentID", CacheHelper.getInstance().userData.get(CacheHelper.USER_ID_KEY));
        map.put("StudentIdentityNo", id);
        ApiHelper api = new ApiHelper(this, ApiEndPoints.ADD_SON, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                String msg = (String) response;
                if (msg.contains("success")) {
                    txt_msg.setText(getResources().getString(R.string.txt_add_success));
                    getData();
                } else if (msg.contains("Fail"))
                    txt_msg.setText(getResources().getString(R.string.txt_add_fail));
                else if (msg.contains("Has Parent"))
                    txt_msg.setText(getResources().getString(R.string.txt_add_has_parent));
                else
                    txt_msg.setText(getResources().getString(R.string.txt_add_already_added));
                msg_container.setVisibility(View.VISIBLE);
                txt_sID.getText().clear();
                hideSoftKeyboard(AddSonActivity.this);
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executePostRequest(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
