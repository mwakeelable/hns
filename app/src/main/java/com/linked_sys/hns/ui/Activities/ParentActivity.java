package com.linked_sys.hns.ui.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.linked_sys.hns.Model.DaysInfo;
import com.linked_sys.hns.Model.LecInfo;
import com.linked_sys.hns.Model.Son;
import com.linked_sys.hns.Model.Staff;
import com.linked_sys.hns.Model.Teacher;
import com.linked_sys.hns.Model.TeacherClasses;
import com.linked_sys.hns.Model.TimeTable;
import com.linked_sys.hns.R;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.core.ConnectionReceiver;
import com.linked_sys.hns.core.SessionManager;
import com.linked_sys.hns.core.SharedManager;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.linked_sys.hns.core.CacheHelper.CLASS_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.CLASS_NAME_KEY;
import static com.linked_sys.hns.core.CacheHelper.SCHOOL_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.STAGE_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.STAGE_NAME_KEY;
import static com.linked_sys.hns.core.CacheHelper.TERM_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_1_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_2_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_3_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_4_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_IMAGE_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;
import static com.linked_sys.hns.core.CacheHelper.absence;
import static com.linked_sys.hns.core.CacheHelper.activities;
import static com.linked_sys.hns.core.CacheHelper.behaviour;
import static com.linked_sys.hns.core.CacheHelper.newMails;
import static com.linked_sys.hns.core.CacheHelper.newTasks;
import static com.linked_sys.hns.core.CacheHelper.selectedSonID;

public abstract class ParentActivity extends AppCompatActivity implements ConnectionReceiver.ConnectionReceiverListener {
    SessionManager session;
    SharedManager manager;
    int studentID, classID, schoolID, termID;
    int maxLec, startDay, endDay;
    DaysInfo daysInfo;
    LecInfo lecInfo;
    TimeTable timeTable;
    Bitmap studentAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        session = new SessionManager(this);
        manager = new SharedManager();
        //check the network connectivity when activity is created
        checkConnection();
    }

    protected abstract int getLayoutResourceId();

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            //show a No Internet Alert or Dialog
            showNoConnectionDialog(this);
        } else {
            //refresh the activity
            restartActivity(getIntent());
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            //show a No Internet Alert or Dialog
            showNoConnectionDialog(this);
        }
    }

    public static void showNoConnectionDialog(Context ctx1) {
        final Context ctx = ctx1;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage(R.string.no_connection);
        builder.setTitle(R.string.no_connection_title);
        builder.setPositiveButton(R.string.settings_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        builder.show();
    }

    public void openActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setBadge(this, newMails);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setBadge(this, newMails);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBadge(this, newMails);
        AppController.getInstance().setConnectionListener(this);
    }

    public void getData() {
        if (session.getUserToken() != null)
            CacheHelper.getInstance().token = session.getUserToken();
        ApiHelper api = new ApiHelper(this, ApiEndPoints.GET_USER_DATA, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                CacheHelper.getInstance().userData.clear();
                CacheHelper.getInstance().sonData.clear();
                JSONObject res = (JSONObject) response;
                try {
                    JSONObject dataObj = res.optJSONObject("UserData");
                    CacheHelper.getInstance().userData.put(USER_ID_KEY, dataObj.optString("Id"));
                    CacheHelper.getInstance().userData.put(USERNAME_1_KEY, dataObj.optString("Name1"));
                    CacheHelper.getInstance().userData.put(USERNAME_2_KEY, dataObj.optString("Name2"));
                    CacheHelper.getInstance().userData.put(USERNAME_3_KEY, dataObj.optString("Name3"));
                    CacheHelper.getInstance().userData.put(USERNAME_4_KEY, dataObj.optString("Name4"));
                    CacheHelper.getInstance().userData.put(USER_IMAGE_KEY, dataObj.optString("Image"));
                    CacheHelper.getInstance().userData.put(USER_TYPE_KEY, String.valueOf(dataObj.optInt("UserType")));
                    newMails = res.optInt("CountUnReadMails");

                    //TEACHER
                    if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("1")) {
                        JSONObject teacher = res.optJSONObject("Teacher");
                        CacheHelper.getInstance().userData.put(TERM_ID_KEY, String.valueOf(teacher.optInt("TermID")));
                        CacheHelper.getInstance().userData.put(SCHOOL_ID_KEY, String.valueOf(teacher.optInt("SchoolID")));
                        JSONArray classes = res.optJSONArray("TeacherClasses");
                        CacheHelper.getInstance().teacherClassesList.clear();
                        for (int i = 0; i < classes.length(); i++) {
                            JSONObject classObj = classes.getJSONObject(i);
                            TeacherClasses teacherClasses = new TeacherClasses(
                                    classObj.optInt("ID"),
                                    classObj.optString("Name"));
                            CacheHelper.getInstance().teacherClassesList.add(teacherClasses);

                        }
//                        sendFBToken();
                        getTeacherTimeTable();
                        openActivity(MainActivity.class);
                        finish();
                    }

                    //STUDENT
                    else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")) {
                        JSONObject stObj = res.optJSONObject("Student");
                        CacheHelper.getInstance().userData.put(CLASS_ID_KEY, String.valueOf(stObj.optInt("ClassID")));
                        CacheHelper.getInstance().userData.put(CLASS_NAME_KEY, stObj.optString("ClassName"));
                        CacheHelper.getInstance().userData.put(STAGE_ID_KEY, String.valueOf(stObj.optInt("StageID")));
                        CacheHelper.getInstance().userData.put(STAGE_NAME_KEY, stObj.optString("StageName"));
                        CacheHelper.getInstance().userData.put(TERM_ID_KEY, String.valueOf(stObj.optInt("ThisTerm")));
                        CacheHelper.getInstance().userData.put(SCHOOL_ID_KEY, String.valueOf(stObj.optInt("SchoolID")));
                        newTasks = res.optInt("CountStudentTasksNotDone");
                        getTimeTable();
//                        sendFBToken();
                        getDashboard(CacheHelper.getInstance().userData.get(USER_ID_KEY));
                    }

                    //PARENT
                    else {
                        JSONArray sonsArray = res.getJSONArray("ListOfSonsData");
                        if (sonsArray.length() > 0) {
                            for (int i = 0; i < sonsArray.length(); i++) {
                                JSONObject sonsObj = sonsArray.getJSONObject(i);
                                Son son = null;
                                if (sonsObj.optString("Image").equals("null")) {
                                    son = new Son
                                            (sonsObj.optInt("SonID"),
                                                    sonsObj.optString("Name1"),
                                                    sonsObj.optString("Name4"),
                                                    sonsObj.optInt("ClassID"),
                                                    sonsObj.optString("ClassName"),
                                                    sonsObj.optInt("StageID"),
                                                    sonsObj.optString("StageName"),
                                                    "",
                                                    sonsObj.optInt("TermID"),
                                                    sonsObj.optInt("SchoolID"),
                                                    sonsObj.optInt("CountTasksNotDone"));
                                } else {
                                    son = new Son
                                            (sonsObj.optInt("SonID"),
                                                    sonsObj.optString("Name1"),
                                                    sonsObj.optString("Name4"),
                                                    sonsObj.optInt("ClassID"),
                                                    sonsObj.optString("ClassName"),
                                                    sonsObj.optInt("StageID"),
                                                    sonsObj.optString("StageName"),
                                                    ApiEndPoints.BASE_URL + sonsObj.optString("Image") + "?width=320",
                                                    sonsObj.optInt("TermID"),
                                                    sonsObj.optInt("SchoolID"),
                                                    sonsObj.optInt("CountTasksNotDone"));
                                }
                                CacheHelper.getInstance().sonData.add(son);
                            }
//                            sendFBToken();
                            openActivity(MainActivity.class);
                            finish();
                        } else {
//                            sendFBToken();
                            openActivity(AddSonActivity.class);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executeRequest(false, false);
    }

    public void getNewMsgCount() {
        String url = ApiEndPoints.NEW_MSG_COUNT
                + "?RecieverID=" + String.valueOf(CacheHelper.getInstance().userData.get(USER_ID_KEY));
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                newMails = res.optInt("count");
                setBadge(ParentActivity.this, res.optInt("count"));
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Failed");
            }
        });
        api.executeRequest(false, false);
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public String getDateFormat(String date) {
        String dateFormat = "";
        if (date.charAt(5) != '0')
            dateFormat += date.charAt(5);
        dateFormat += date.charAt(6);
        dateFormat += '/';
        if (date.charAt(8) != '0')
            dateFormat += date.charAt(8);
        dateFormat += date.charAt(9);
        dateFormat += '/';
        dateFormat += date.charAt(2);
        dateFormat += date.charAt(3);
        return dateFormat;
    }

    public void getClassTeachers() {
        CacheHelper.getInstance().teachersList.clear();
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3")) {
            classID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(CLASS_ID_KEY));
            termID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(TERM_ID_KEY));
        } else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")) {
            classID = Integer.parseInt(CacheHelper.getInstance().userData.get(CLASS_ID_KEY));
            termID = Integer.parseInt(CacheHelper.getInstance().userData.get(TERM_ID_KEY));
        }

        CacheHelper.getInstance().teachersList.clear();
        String url = ApiEndPoints.GET_TEACHERS
                + "?classID=" + classID
                + "&termId=" + termID;
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONArray teachersList = res.optJSONArray("teachersLst");
                    for (int i = 0; i < teachersList.length(); i++) {
                        JSONObject obj = teachersList.optJSONObject(i);
                        Teacher teacher = new Teacher(obj.optInt("ID"), obj.optString("Name"));
                        CacheHelper.getInstance().teachersList.add(teacher);
                    }
                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executeRequest(false, false);
    }

    public void sendFBToken(String userID) {
        final String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("UserID", userID);
        map.put("Token", token);

        ApiHelper api = new ApiHelper(this, ApiEndPoints.SEND_FB_TOKEN, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Log.d(AppController.TAG, (String) response);
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, error.getMessage());
            }
        });
        api.executePostRequest(false);
    }

    public void getTeacherTimeTable() {
        CacheHelper.getInstance().lectures.clear();
        CacheHelper.getInstance().lecInfo.clear();
        CacheHelper.getInstance().daysInfo.clear();
        CacheHelper.getInstance().timeTableList.clear();
        CacheHelper.getInstance().classes.clear();

        String url = ApiEndPoints.TEACHER_TIME_TABLE
                + "?TeacherID=" + String.valueOf(CacheHelper.getInstance().userData.get(USER_ID_KEY))
                + "&TermID=" + String.valueOf(CacheHelper.getInstance().userData.get(TERM_ID_KEY));

        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                int maxLec, startDay, endDay;
                JSONObject obj = (JSONObject) response;
                try {
                    JSONObject settings = obj.getJSONObject("settings");
                    maxLec = settings.optInt("MaxLectures");
                    startDay = settings.optInt("StartDay");
                    endDay = settings.optInt("EndDay");
                    JSONArray weekDays = settings.optJSONArray("WeekDays");
                    for (int i = startDay - 1; i < endDay; i++) {
                        JSONObject dayObj = weekDays.getJSONObject(i);
                        daysInfo = new DaysInfo(
                                dayObj.optString("Name")
                                , dayObj.optInt("ID"));
                        CacheHelper.getInstance().daysInfo.add(daysInfo);
                    }
                    for (int i = 0; i < maxLec; i++) {
                        lecInfo = new LecInfo(String.valueOf(i + 1), i);
                        CacheHelper.getInstance().lecInfo.add(lecInfo);
                    }

                    JSONArray schedule = obj.optJSONArray("teacherTimeTable");
                    for (int i = 0; i < schedule.length(); i++) {
                        JSONObject scheduleObj = schedule.getJSONObject(i);
                        timeTable = new TimeTable(
                                scheduleObj.optInt("LectureID")
                                , scheduleObj.optInt("WeekDayID")
                                , scheduleObj.optString("CourseName"),
                                scheduleObj.optString("ClassName"));
                        CacheHelper.getInstance().timeTableList.add(timeTable);
                    }
                    String padding;
                    for (int i = startDay; i <= endDay; i++) {
                        ArrayList<String> Rows = new ArrayList<>();
                        for (int j = 1; j <= maxLec; j++) {
                            padding = " ";
                            for (TimeTable t : CacheHelper.getInstance().timeTableList) {
                                if (t.weekDayID == i && t.lectureID == j)
                                    padding = t.courseName;
                            }
                            Rows.add(padding);
                        }
                        CacheHelper.getInstance().lectures.add(Rows);
                    }

                    String classPadding;
                    for (int i = startDay; i <= endDay; i++) {
                        ArrayList<String> Rows = new ArrayList<>();
                        for (int j = 1; j <= maxLec; j++) {
                            classPadding = " ";
                            for (TimeTable t : CacheHelper.getInstance().timeTableList) {
                                if (t.weekDayID == i && t.lectureID == j)
                                    classPadding = t.className;
                            }
                            Rows.add(classPadding);
                        }
                        CacheHelper.getInstance().classes.add(Rows);
                    }

                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Failed");
            }
        });
        api.executeRequest(false, false);
    }

    public void getStaff() {
        CacheHelper.getInstance().staffList.clear();

        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3"))
            schoolID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(SCHOOL_ID_KEY));
        else
            schoolID = Integer.parseInt(CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY));

        String url = ApiEndPoints.GET_STAFF
                + "?SchoolID=" + schoolID;

        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                JSONObject data = res.optJSONObject("MailCCData");
                String managerName = data.optString("ManagerName");
                int managerID = data.optInt("ManagerID");
                String assistantName = data.optString("AssistantManagerName");
                int assistantID = data.optInt("AssistantManagerID");
                String guideName = data.optString("StudentsGuideName");
                int gudieID = data.optInt("StudentsGuideID");
                Staff staff = new Staff(getResources().getString(R.string.txt_manager), managerName, managerID);
                CacheHelper.getInstance().staffList.add(staff);
                staff = new Staff(getResources().getString(R.string.txt_assistant), assistantName, assistantID);
                CacheHelper.getInstance().staffList.add(staff);
                staff = new Staff(getResources().getString(R.string.txt_guide), guideName, gudieID);
                CacheHelper.getInstance().staffList.add(staff);
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executeRequest(false, false);
    }

    public void getTimeTable() {
        CacheHelper.getInstance().lectures.clear();
        CacheHelper.getInstance().lecInfo.clear();
        CacheHelper.getInstance().daysInfo.clear();
        CacheHelper.getInstance().timeTableList.clear();

        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3")) {
            studentID = selectedSonID;
            classID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(CLASS_ID_KEY));
            schoolID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(SCHOOL_ID_KEY));
            termID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(TERM_ID_KEY));
        } else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")) {
            studentID = Integer.parseInt(CacheHelper.getInstance().userData.get(USER_ID_KEY));
            classID = Integer.parseInt(CacheHelper.getInstance().userData.get(CLASS_ID_KEY));
            schoolID = Integer.parseInt(CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY));
            termID = Integer.parseInt(CacheHelper.getInstance().userData.get(TERM_ID_KEY));
        }

        String url = ApiEndPoints.TIME_TABLE
                + "?StudentID=" + String.valueOf(studentID)
                + "&ClassID=" + String.valueOf(classID)
                + "&SchoolID=" + String.valueOf(schoolID)
                + "&TermID=" + String.valueOf(termID);
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject obj = (JSONObject) response;

                try {
                    JSONObject settings = obj.getJSONObject("settings");
                    maxLec = settings.optInt("MaxLectures");
                    startDay = settings.optInt("StartDay");
                    endDay = settings.optInt("EndDay");
                    JSONArray weekDays = settings.optJSONArray("WeekDays");
                    for (int i = startDay - 1; i < endDay; i++) {
                        JSONObject dayObj = weekDays.getJSONObject(i);
                        daysInfo = new DaysInfo(
                                dayObj.optString("Name")
                                , dayObj.optInt("ID"));
                        CacheHelper.getInstance().daysInfo.add(daysInfo);
                    }
                    for (int i = 0; i < maxLec; i++) {
                        lecInfo = new LecInfo(String.valueOf(i + 1), i);
                        CacheHelper.getInstance().lecInfo.add(lecInfo);
                    }

                    JSONArray schedule = obj.optJSONArray("TimeTable");
                    for (int i = 0; i < schedule.length(); i++) {
                        JSONObject scheduleObj = schedule.getJSONObject(i);
                        timeTable = new TimeTable(
                                scheduleObj.optInt("LectureID")
                                , scheduleObj.optInt("WeekDayID")
                                , scheduleObj.optString("CourseName"), "");
                        CacheHelper.getInstance().timeTableList.add(timeTable);
                    }
                    String padding;
                    for (int i = startDay; i <= endDay; i++) {
                        ArrayList<String> Rows = new ArrayList<>();
                        for (int j = 1; j <= maxLec; j++) {
                            padding = " ";
                            for (TimeTable t : CacheHelper.getInstance().timeTableList) {
                                if (t.weekDayID == i && t.lectureID == j)
                                    padding = t.courseName;
                            }
                            Rows.add(padding);
                        }
                        CacheHelper.getInstance().lectures.add(Rows);
                    }
                    String classPadding;
                    for (int i = startDay; i <= endDay; i++) {
                        ArrayList<String> Rows = new ArrayList<>();
                        for (int j = 1; j <= maxLec; j++) {
                            classPadding = " ";
                            for (TimeTable t : CacheHelper.getInstance().timeTableList) {
                                if (t.weekDayID == i && t.lectureID == j)
                                    classPadding = t.className;
                            }
                            Rows.add(classPadding);
                        }
                        CacheHelper.getInstance().classes.add(Rows);
                    }

                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Failed");
            }
        });
        api.executeRequest(false, false);
    }

    public static void setBadge(Context context, int count) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            String launcherClassName = getLauncherClassName(context);
            if (launcherClassName == null) {
                return;
            }
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intent);
        } else {
            ShortcutBadger.applyCount(context, count);
        }
    }

    public static String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }

    public void restartActivity(Intent intent) {
        finish();
        startActivity(intent);
    }

    public void getDashboard(String userID) {
        String url = ApiEndPoints.GET_DASHBOARD + "?StudentID=" + userID;
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONObject dashboard = res.optJSONObject("Eval");
                    CacheHelper.getInstance().skills_100 = dashboard.optInt("Green");
                    CacheHelper.getInstance().skills_90_100 = dashboard.optInt("Yellow");
                    CacheHelper.getInstance().skills_80_90 = dashboard.optInt("Orange");
                    CacheHelper.getInstance().skills_80 = dashboard.optInt("Red");
                    activities = dashboard.optInt("Activities");
                    absence = dashboard.optInt("Absences");
                    behaviour = dashboard.optInt("Behaviors");
                    openActivity(MainActivity.class);
                    finish();
                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executeRequest(false, false);
    }

    /**
     * chooses a random color from array.xml
     */
    public int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    public void mark_read(int mailID) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("MailID", String.valueOf(mailID));
        map.put("IsRead", "true");
        ApiHelper api = new ApiHelper(this, ApiEndPoints.MARK_MSG_UNREAD, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                getNewMsgCount();
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executePostRequest(false);
    }

    public void getID_SendToken() {
        if (session.getUserToken() != null)
            CacheHelper.getInstance().token = session.getUserToken();
        ApiHelper api = new ApiHelper(this, ApiEndPoints.GET_USER_DATA, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONObject dataObj = res.optJSONObject("UserData");
                    sendFBToken(dataObj.optString("Id"));
                }catch (Exception e){
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executeRequest(false, false);
    }
}
