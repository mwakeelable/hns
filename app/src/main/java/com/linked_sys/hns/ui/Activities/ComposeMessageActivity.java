package com.linked_sys.hns.ui.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.linked_sys.hns.Model.Teacher;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.SpinnerDialog;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;

import java.util.HashMap;
import java.util.Map;

import static com.linked_sys.hns.core.CacheHelper.KEY_BODY;
import static com.linked_sys.hns.core.CacheHelper.KEY_RECEIVERS;
import static com.linked_sys.hns.core.CacheHelper.KEY_SENDER_ID;
import static com.linked_sys.hns.core.CacheHelper.KEY_SUBJECT;
import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;

public class ComposeMessageActivity extends ParentActivity {
    ArrayAdapter<Teacher> adapter;
    Spinner spinner;
    CheckBox manager, assis, guide;
    SpinnerDialog mProgress;
    private String receivers = "", subject, body;
    private int senderID;
    EditText txt_subject, txt_body;
    TextView txt_header, txt_receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.txt_new_message));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgress = new SpinnerDialog(this);

        spinner = (Spinner) findViewById(R.id.teachers_list);
        manager = (CheckBox) findViewById(R.id.cb_manager);
        assis = (CheckBox) findViewById(R.id.cb_assis);
        guide = (CheckBox) findViewById(R.id.cb_guide);
        txt_header = (TextView) findViewById(R.id.txt_compose_header);
        txt_receiver = (TextView) findViewById(R.id.txt_receiver);
        txt_body = (EditText) findViewById(R.id.txt_body);
        txt_subject = (EditText) findViewById(R.id.txt_subject);
        ImageView btn_send = (ImageView) findViewById(R.id.btn_send_mail);
        LinearLayout cc_container = (LinearLayout) findViewById(R.id.cc_container);
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2"))
            cc_container.setVisibility(View.GONE);
        else
            cc_container.setVisibility(View.VISIBLE);
        //New Message
        if (CacheHelper.getInstance().isNewMsg) {
            txt_header.setText(getResources().getString(R.string.txt_new_message));
            txt_receiver.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            receivers = "";
            subject = "";
            txt_subject.setText(subject);
        }
        //Reply
        else if (CacheHelper.getInstance().isReply) {
            txt_header.setText(getResources().getString(R.string.txt_reply));
            txt_receiver.setVisibility(View.VISIBLE);
            txt_receiver.setText(CacheHelper.getInstance().body.getSenderName1()
                    + " "
                    + CacheHelper.getInstance().body.getSenderName4());
            spinner.setVisibility(View.GONE);
            receivers = String.valueOf(CacheHelper.getInstance().body.getSenderID());
            subject = getResources().getString(R.string.txt_reply_prefix) + " " + CacheHelper.getInstance().body.getSubject();
            txt_subject.setText(subject);
            txt_body.requestFocus();
        }
        //Forward
        else {
            txt_header.setText(getResources().getString(R.string.txt_forward));
            txt_receiver.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            subject = CacheHelper.getInstance().body.getSubject();
            txt_subject.setText(getResources().getString(R.string.txt_forward_prefix) + " " + subject);
            txt_body.requestFocus();
        }

        manager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    receivers = receivers + "," + String.valueOf(CacheHelper.getInstance().staffList.get(0).getId());
                else
                    receivers = receivers.replace("," + String.valueOf(CacheHelper.getInstance().staffList.get(0).getId()), "");
            }
        });

        assis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    receivers = receivers + "," + String.valueOf(CacheHelper.getInstance().staffList.get(1).getId());
                else
                    receivers = receivers.replace("," + String.valueOf(CacheHelper.getInstance().staffList.get(1).getId()), "");
            }
        });

        guide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    receivers = receivers + "," + String.valueOf(CacheHelper.getInstance().staffList.get(2).getId());
                else
                    receivers = receivers.replace("," + String.valueOf(CacheHelper.getInstance().staffList.get(2).getId()), "");
            }
        });
        setData();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Teacher teacher = (Teacher) parent.getSelectedItem();
                receivers = String.valueOf(teacher.getTeacherID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeMail();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.compose_message_activity;
    }

    private void setData() {
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                CacheHelper.getInstance().teachersList);
        spinner.setAdapter(adapter);
    }

    private void composeMail() {
        subject = txt_subject.getText().toString().trim();
        body = txt_body.getText().toString();
        senderID = Integer.parseInt(CacheHelper.getInstance().userData.get(USER_ID_KEY));
        Map<String, String> map = new HashMap<>();
        map.put(KEY_SENDER_ID, String.valueOf(senderID));
        map.put(KEY_RECEIVERS, receivers);
        map.put(KEY_SUBJECT, subject);
        map.put(KEY_BODY, body);
        ApiHelper api = new ApiHelper(this, ApiEndPoints.SEND_MSG, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Toast.makeText(ComposeMessageActivity.this, getResources().getString(R.string.txt_done), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(ComposeMessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
        api.executePostRequest(true);
    }
}
