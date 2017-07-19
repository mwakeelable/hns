package com.linked_sys.hns.ui.Activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.linked_sys.hns.Model.Teacher;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.SpinnerDialog;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.linked_sys.hns.core.CacheHelper.KEY_BODY;
import static com.linked_sys.hns.core.CacheHelper.KEY_RECEIVERS;
import static com.linked_sys.hns.core.CacheHelper.KEY_SENDER_ID;
import static com.linked_sys.hns.core.CacheHelper.KEY_SUBJECT;
import static com.linked_sys.hns.core.CacheHelper.SCHOOL_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;

public class ComposeMessageToTeacherActivity extends ParentActivity {
    SpinnerDialog mProgress;
    Spinner teachersList;
    ArrayAdapter<Teacher> teacherAdapter;
    private String receivers = "", subject = "", body = "";
    private int senderID;
    ImageView btn_send;
    CheckBox manager, assistant, guide;
    EditText txt_subject, txt_body;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Some Definitions
        mProgress = new SpinnerDialog(this);
        TextView txt_header = (TextView) findViewById(R.id.txt_compose_header);
        TextView txt_receiver = (TextView) findViewById(R.id.txt_receiver);
        LinearLayout teachers_container = (LinearLayout) findViewById(R.id.teacher_container);
        LinearLayout reply_receiver = (LinearLayout) findViewById(R.id.reply_receiver);
        teachersList = (Spinner) findViewById(R.id.teachersList);
        btn_send = (ImageView) findViewById(R.id.btn_send_mail);
        manager = (CheckBox) findViewById(R.id.cb_manager);
        assistant = (CheckBox) findViewById(R.id.cb_assis);
        guide = (CheckBox) findViewById(R.id.cb_guide);
        txt_body = (EditText) findViewById(R.id.txt_body);
        txt_subject = (EditText) findViewById(R.id.txt_subject);
        //New Message
        if (CacheHelper.getInstance().isNewMsg) {
            txt_header.setText(getResources().getString(R.string.txt_new_message));
            txt_receiver.setVisibility(View.GONE);
            reply_receiver.setVisibility(View.GONE);
            teachers_container.setVisibility(View.VISIBLE);
            receivers = "";
            subject = "";
            txt_subject.setText(subject);
        }
        //Reply
        else if (CacheHelper.getInstance().isReply) {
            txt_header.setText(getResources().getString(R.string.txt_reply));
            txt_receiver.setVisibility(View.VISIBLE);
            reply_receiver.setVisibility(View.VISIBLE);
            txt_receiver.setText(CacheHelper.getInstance().body.getSenderName1()
                    + " "
                    + CacheHelper.getInstance().body.getSenderName4());
            teachers_container.setVisibility(View.GONE);
            receivers = String.valueOf(CacheHelper.getInstance().body.getSenderID());
            subject = getResources().getString(R.string.txt_reply_prefix)+ " " + CacheHelper.getInstance().body.getSubject();
            txt_subject.setText(subject);
            txt_body.requestFocus();
        }
        //Forward
        else {
            txt_header.setText(getResources().getString(R.string.txt_forward));
            txt_receiver.setVisibility(View.GONE);
            reply_receiver.setVisibility(View.GONE);
            teachers_container.setVisibility(View.VISIBLE);
            subject = CacheHelper.getInstance().body.getSubject();
            txt_subject.setText(getResources().getString(R.string.txt_forward_prefix)+ " " + subject);
            txt_body.requestFocus();
        }
        //Fill list with school teachers
        getTeachersList();
        //Send Mail
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSending();
            }
        });
        //Teacher Selection
        teachersList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Teacher teacher = (Teacher) parent.getSelectedItem();
                receivers = String.valueOf(teacher.getTeacherID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Manage CC
        manager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    receivers = receivers + "," + String.valueOf(CacheHelper.getInstance().staffList.get(0).getId());
                else
                    receivers = receivers.replace("," + String.valueOf(CacheHelper.getInstance().staffList.get(0).getId()), "");
            }
        });

        assistant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.compose_message_to_teacher_activity;
    }

    private void setTeachersData() {
        teacherAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                CacheHelper.getInstance().teachersList);
        teachersList.setAdapter(teacherAdapter);
    }

    private void getTeachersList() {
        CacheHelper.getInstance().teachersList.clear();
        String url = ApiEndPoints.GET_ALL_TEACHERS
                + "?SchoolID=" + CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY);
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONArray teachersList = res.optJSONArray("teachers");
                    for (int i = 0; i < teachersList.length(); i++) {
                        JSONObject obj = teachersList.optJSONObject(i);
                        Teacher teacher = new Teacher(obj.optInt("ID"), obj.optString("name"));
                        CacheHelper.getInstance().teachersList.add(teacher);
                    }
                    setTeachersData();
                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executeRequest(true, false);
    }

    private void composeMail() {
        senderID = Integer.parseInt(CacheHelper.getInstance().userData.get(USER_ID_KEY));
        Map<String, String> map = new HashMap<>();
        map.put(KEY_SENDER_ID, String.valueOf(senderID));
        map.put(KEY_RECEIVERS, receivers);
        map.put(KEY_SUBJECT, subject);
        map.put(KEY_BODY, body);
        ApiHelper api = new ApiHelper(this, ApiEndPoints.SEND_MSG, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Toast.makeText(ComposeMessageToTeacherActivity.this, getResources().getString(R.string.txt_done), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(ComposeMessageToTeacherActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
        api.executePostRequest(true);
    }

    private void doSending() {
        subject = txt_subject.getText().toString().trim();
        body = txt_body.getText().toString();
        if (receivers.equals(""))
            new MaterialDialog.Builder(ComposeMessageToTeacherActivity.this)
                    .title(getResources().getString(R.string.login_error))
                    .content(getResources().getString(R.string.send_msg_error_no_receiver))
                    .positiveText(getResources().getString(R.string.login_positive_btn))
                    .show();
        else if (subject.equals(""))
            new MaterialDialog.Builder(ComposeMessageToTeacherActivity.this)
                    .title(getResources().getString(R.string.login_error))
                    .content(getResources().getString(R.string.send_msg_error_no_subject))
                    .positiveText(getResources().getString(R.string.login_positive_btn))
                    .show();
        else if (body.equals(""))
            new MaterialDialog.Builder(ComposeMessageToTeacherActivity.this)
                    .title(getResources().getString(R.string.login_error))
                    .content(getResources().getString(R.string.send_msg_error_no_body))
                    .positiveText(getResources().getString(R.string.login_positive_btn))
                    .show();
        else
            composeMail();
    }
}
