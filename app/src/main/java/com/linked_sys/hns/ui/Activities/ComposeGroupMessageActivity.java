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
import com.linked_sys.hns.Model.SchoolStages;
import com.linked_sys.hns.Model.Student;
import com.linked_sys.hns.Model.TeacherClasses;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.SpinnerDialog;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.linked_sys.hns.core.CacheHelper.KEY_BODY;
import static com.linked_sys.hns.core.CacheHelper.KEY_RECEIVERS;
import static com.linked_sys.hns.core.CacheHelper.KEY_SENDER_ID;
import static com.linked_sys.hns.core.CacheHelper.KEY_SUBJECT;
import static com.linked_sys.hns.core.CacheHelper.SCHOOL_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;

public class ComposeGroupMessageActivity extends ParentActivity {
    SpinnerDialog mProgress;
    Spinner stageList, classList, studentList;
    CheckBox cb_manager, cb_assistant, cb_guide, cb_parent, cb_student;
    ArrayAdapter<SchoolStages> stagesAdapter;
    ArrayAdapter<TeacherClasses> classesAdapter;
    private int stageID, classID, senderID;
    private String receivers = "", subject = "", body = "";
    ImageView btn_send;
    EditText txt_subject, txt_body;
    LinearLayout stage_container;
    String studentIDs = "", parentIDs = "";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.compose_group_message_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.txt_new_message));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgress = new SpinnerDialog(this);
        TextView txt_header = (TextView) findViewById(R.id.txt_compose_header);
        stageList = (Spinner) findViewById(R.id.stageList);
        classList = (Spinner) findViewById(R.id.classList);
        studentList = (Spinner) findViewById(R.id.studentList);
        cb_manager = (CheckBox) findViewById(R.id.cb_manager);
        cb_assistant = (CheckBox) findViewById(R.id.cb_assis);
        cb_guide = (CheckBox) findViewById(R.id.cb_guide);
        cb_parent = (CheckBox) findViewById(R.id.cb_parent);
        cb_student = (CheckBox) findViewById(R.id.cb_student);
        btn_send = (ImageView) findViewById(R.id.btn_send_mail);
        txt_body = (EditText) findViewById(R.id.txt_body);
        txt_subject = (EditText) findViewById(R.id.txt_subject);
        stage_container = (LinearLayout) findViewById(R.id.stage_container);
        //New Message
        if (CacheHelper.getInstance().isNewMsg) {
            txt_header.setText(getResources().getString(R.string.txt_new_message));
        }
        //Reply
        else if (CacheHelper.getInstance().isReply) {
            txt_header.setText(getResources().getString(R.string.txt_reply));
        }
        //Forward
        else {
            txt_header.setText(getResources().getString(R.string.txt_forward));
        }
        //Disable CC
        ccValidation(false);
        //Get User ID
        int userID = Integer.parseInt(CacheHelper.getInstance().userData.get(USER_ID_KEY));
        //MANAGER
        if (CacheHelper.getInstance().staffList.get(0).getId() == userID) {
            cb_manager.setVisibility(View.GONE);
            cb_assistant.setVisibility(View.VISIBLE);
            cb_guide.setVisibility(View.VISIBLE);
            stage_container.setVisibility(View.VISIBLE);
            getStages(Integer.parseInt(CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY)));
        }
        //ASSISTANT
        else if (CacheHelper.getInstance().staffList.get(1).getId() == userID) {
            cb_manager.setVisibility(View.VISIBLE);
            cb_assistant.setVisibility(View.GONE);
            cb_guide.setVisibility(View.VISIBLE);
            stage_container.setVisibility(View.VISIBLE);
            getStages(Integer.parseInt(CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY)));
        }
        //GUIDE
        else if (CacheHelper.getInstance().staffList.get(2).getId() == userID) {
            cb_manager.setVisibility(View.VISIBLE);
            cb_assistant.setVisibility(View.VISIBLE);
            cb_guide.setVisibility(View.GONE);
            stage_container.setVisibility(View.VISIBLE);
            getStages(Integer.parseInt(CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY)));
        }
        //TEACHER
        else {
            cb_manager.setVisibility(View.VISIBLE);
            cb_assistant.setVisibility(View.VISIBLE);
            cb_guide.setVisibility(View.VISIBLE);
            stage_container.setVisibility(View.GONE);
            setClassesData();
        }
        //Select Manager
        cb_manager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    receivers = receivers + "," + String.valueOf(CacheHelper.getInstance().staffList.get(0).getId());
                else
                    receivers = receivers.replace("," + String.valueOf(CacheHelper.getInstance().staffList.get(0).getId()), "");
            }
        });
        //Select Assistant
        cb_assistant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    receivers = receivers + "," + String.valueOf(CacheHelper.getInstance().staffList.get(1).getId());
                else
                    receivers = receivers.replace("," + String.valueOf(CacheHelper.getInstance().staffList.get(1).getId()), "");
            }
        });
        //Select Guide
        cb_guide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    receivers = receivers + "," + String.valueOf(CacheHelper.getInstance().staffList.get(2).getId());
                else
                    receivers = receivers.replace("," + String.valueOf(CacheHelper.getInstance().staffList.get(2).getId()), "");
            }
        });
        //Stage Selection
        stageList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SchoolStages stages = (SchoolStages) parent.getSelectedItem();
                stageID = stages.getStageID();
                getClasses(stageID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Class Selection
        classList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TeacherClasses classes = (TeacherClasses) parent.getSelectedItem();
                classID = classes.getClassID();
                CacheHelper.getInstance().studentList.clear();
                String url = ApiEndPoints.GET_STUDENTS
                        + "?ClassID=" + String.valueOf(classID);
                ApiHelper api = new ApiHelper(ComposeGroupMessageActivity.this, url, Request.Method.GET, new ApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        JSONObject res = (JSONObject) response;
                        try {
                            JSONArray student = res.optJSONArray("ClassStudent");
                            for (int i = 0; i < student.length(); i++) {
                                JSONObject studentObj = student.getJSONObject(i);
                                Student students = new Student(studentObj.optInt("StudentID"),
                                        studentObj.optInt("ParentID"),
                                        studentObj.optString("StudentName"));
                                CacheHelper.getInstance().studentList.add(students);
                            }
                            studentIDs = "";
                            parentIDs = "";
                            for (int i = 0; i < CacheHelper.getInstance().studentList.size(); i++) {
                                studentIDs = studentIDs + String.valueOf(CacheHelper.getInstance().studentList.get(i).getStudentID()) + ",";
                                if (CacheHelper.getInstance().studentList.get(i).getParentID() > 0)
                                    parentIDs = parentIDs + String.valueOf(CacheHelper.getInstance().studentList.get(i).getParentID()) + ",";
                            }
                            studentIDs = studentIDs.substring(0, studentIDs.length() - 1);

                            parentIDs = parentIDs.substring(0, parentIDs.length() - 1);
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Select Parent
        cb_parent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (receivers.equals("")) {
                        receivers = parentIDs;
                        ccValidation(true);
                    } else {
                        receivers = receivers + "," + parentIDs;
                        ccValidation(true);
                    }
                } else {
                    if (receivers.contains("," + parentIDs)) {
                        receivers = receivers.replace("," + parentIDs, "");
                        if (receivers.equals(""))
                            ccValidation(false);
                        else
                            ccValidation(true);
                    } else if (receivers.contains(parentIDs)) {
                        receivers = receivers.replace(parentIDs, "");
                        if (receivers.equals(""))
                            ccValidation(false);
                        else
                            ccValidation(true);
                    }
                }
            }
        });
        //Select Student
        cb_student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (receivers.equals("")) {
                        receivers = studentIDs;
                        ccValidation(true);
                    } else {
                        receivers = receivers + "," + studentIDs;
                        ccValidation(true);
                    }
                } else {
                    if (receivers.contains("," + studentIDs)) {
                        receivers = receivers.replace("," + studentIDs, "");
                        if (receivers.equals(""))
                            ccValidation(false);
                        else
                            ccValidation(true);
                    } else if (receivers.contains(studentIDs)) {
                        receivers = receivers.replace(studentIDs, "");
                        if (receivers.equals(""))
                            ccValidation(false);
                        else
                            ccValidation(true);
                    }
                }

            }
        });
        //Sending...
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSending();
            }
        });
    }

    //Stages
    private void getStages(int schoolID) {
        CacheHelper.getInstance().stagesList.clear();
        String url = ApiEndPoints.GET_STAGES
                + "?SchoolID=" + String.valueOf(schoolID);
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONArray stages = res.optJSONArray("schoolStages");
                    for (int i = 0; i < stages.length(); i++) {
                        JSONObject stageObj = stages.getJSONObject(i);
                        SchoolStages schoolStages = new SchoolStages(
                                stageObj.optInt("stageId"),
                                stageObj.optString("stageName"));
                        CacheHelper.getInstance().stagesList.add(schoolStages);
                    }
                    setStagesData();
                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Failed");
            }
        });
        api.executeRequest(true, false);
    }

    private void setStagesData() {
        stagesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                CacheHelper.getInstance().stagesList);
        stageList.setAdapter(stagesAdapter);
    }

    //Classes
    private void getClasses(int stageID) {
        CacheHelper.getInstance().teacherClassesList.clear();
        String url = ApiEndPoints.GET_CLASSES
                + "?StageID=" + String.valueOf(stageID);
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONArray classes = res.optJSONArray("StageClasses");
                    for (int i = 0; i < classes.length(); i++) {
                        JSONObject classObj = classes.getJSONObject(i);
                        TeacherClasses teacherClasses = new TeacherClasses(
                                classObj.optInt("classId"),
                                classObj.optString("className"));
                        CacheHelper.getInstance().teacherClassesList.add(teacherClasses);
                    }
                    setClassesData();
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

    private void setClassesData() {
        classesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                CacheHelper.getInstance().teacherClassesList);
        classList.setAdapter(classesAdapter);
    }

    //Manage Sending Message
    private void composeMail() {
        senderID = Integer.parseInt(CacheHelper.getInstance().userData.get(USER_ID_KEY));
        Map<String, String> map = new LinkedHashMap<>();
        map.put(KEY_SENDER_ID, String.valueOf(senderID));
        map.put(KEY_RECEIVERS, receivers);
        map.put(KEY_SUBJECT, subject);
        map.put(KEY_BODY, body);
        ApiHelper api = new ApiHelper(this, ApiEndPoints.SEND_MSG, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Toast.makeText(ComposeGroupMessageActivity.this, getResources().getString(R.string.txt_done), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(ComposeGroupMessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
        api.executePostRequest(true);
    }

    private void doSending() {
        subject = txt_subject.getText().toString().trim();
        body = txt_body.getText().toString();
        if (receivers.equals(""))
            new MaterialDialog.Builder(ComposeGroupMessageActivity.this)
                    .title(getResources().getString(R.string.login_error))
                    .content(getResources().getString(R.string.send_msg_error_no_receiver))
                    .positiveText(getResources().getString(R.string.login_positive_btn))
                    .show();
        else if (subject.equals(""))
            new MaterialDialog.Builder(ComposeGroupMessageActivity.this)
                    .title(getResources().getString(R.string.login_error))
                    .content(getResources().getString(R.string.send_msg_error_no_subject))
                    .positiveText(getResources().getString(R.string.login_positive_btn))
                    .show();
        else if (body.equals(""))
            new MaterialDialog.Builder(ComposeGroupMessageActivity.this)
                    .title(getResources().getString(R.string.login_error))
                    .content(getResources().getString(R.string.send_msg_error_no_body))
                    .positiveText(getResources().getString(R.string.login_positive_btn))
                    .show();
        else
            composeMail();
    }

    private void ccValidation(boolean enable) {
        cb_manager.setEnabled(enable);
        cb_assistant.setEnabled(enable);
        cb_guide.setEnabled(enable);
    }
}
