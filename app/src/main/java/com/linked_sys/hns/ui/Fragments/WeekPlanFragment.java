package com.linked_sys.hns.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.kelin.scrollablepanel.library.ScrollablePanel;
import com.linked_sys.hns.Model.WeekPlan;
import com.linked_sys.hns.Model.Weeks;
import com.linked_sys.hns.R;
import com.linked_sys.hns.adapters.WeekPlanAdapter;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.linked_sys.hns.core.CacheHelper.CLASS_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;

public class WeekPlanFragment extends Fragment {
    MainActivity activity;
    ScrollablePanel weekPlanPanel;
    Spinner spinner;
    LinearLayout btn_curr_week;
    ArrayAdapter<Weeks> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.week_plan_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        weekPlanPanel = (ScrollablePanel) view.findViewById(R.id.week_plan_table);
        final WeekPlanAdapter weekPlanAdapter = new WeekPlanAdapter();
//        generateWeekPlan(weekPlanAdapter);
//        weekPlanPanel.setPanelAdapter(weekPlanAdapter);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        btn_curr_week = (LinearLayout) view.findViewById(R.id.btn_curr_week);
        setData();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Weeks weeks = (Weeks) parent.getSelectedItem();
                getSelectedWeekPlan(weeks.getWeekID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_curr_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentWeek();
            }
        });
    }

    private void setData() {
        adapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_dropdown_item,
                CacheHelper.getInstance().weeksList);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(CacheHelper.getInstance().currWeek));
    }

    private void generateWeekPlan(WeekPlanAdapter weekPlanAdapter) {
        weekPlanAdapter.setDaysInfoList(CacheHelper.getInstance().planDaysInfo);
        weekPlanAdapter.setLecInfoList(CacheHelper.getInstance().planLecInfo);
        weekPlanAdapter.setLessonInfoList(CacheHelper.getInstance().planLectures);
    }

    private void getSelectedWeekPlan(int weekID) {
        CacheHelper.getInstance().planWeekPlanList.clear();
        CacheHelper.getInstance().planLectures.clear();
        String url = "";
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3")){
            url = ApiEndPoints.SELECTED_WEEK
                    + "?ClassID=" + CacheHelper.getInstance().selectedSon.get(CLASS_ID_KEY)
                    + "&WeekID=" + weekID;
        }else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")){
            url = ApiEndPoints.SELECTED_WEEK
                    + "?ClassID=" + CacheHelper.getInstance().userData.get(CLASS_ID_KEY)
                    + "&WeekID=" + weekID;
        }
        ApiHelper api = new ApiHelper(activity, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONArray planArray = res.optJSONArray("weekPlans");
                    for (int i = 0; i < planArray.length(); i++) {
                        JSONObject planObj = planArray.optJSONObject(i);
                        WeekPlan weekPlan = new WeekPlan(
                                planObj.optInt("LectureID"),
                                planObj.optInt("WeekDayID"),
                                planObj.optString("CourseName"),
                                planObj.optInt("ID"));
                        CacheHelper.getInstance().planWeekPlanList.add(weekPlan);
                    }
                    WeekPlan WeekPlanPadding;
                    for (int i = activity.startDay; i <= activity.endDay; i++) {
                        ArrayList<WeekPlan> Rows = new ArrayList<>();
                        for (int j = 1; j <= activity.maxLec; j++) {
                            WeekPlanPadding = new WeekPlan(0,0,"",0);
                            for (WeekPlan w : CacheHelper.getInstance().planWeekPlanList) {
                                if (w.weekDayID == i && w.lectureID == j)
                                    WeekPlanPadding = new WeekPlan(w.lectureID,w.weekDayID,w.courseName,w.planID);
                            }
                            Rows.add(WeekPlanPadding);
                        }
                        CacheHelper.getInstance().planLectures.add(Rows);
                    }
                    WeekPlanAdapter adapter = new WeekPlanAdapter();
                    generateWeekPlan(adapter);
                    weekPlanPanel.setPanelAdapter(adapter);
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

    private void getCurrentWeek() {
        CacheHelper.getInstance().planWeekPlanList.clear();
        CacheHelper.getInstance().planLectures.clear();
        String url = "";
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3")){
            url = ApiEndPoints.SELECTED_WEEK
                    + "?ClassID=" + CacheHelper.getInstance().selectedSon.get(CLASS_ID_KEY)
                    + "&WeekID=" + CacheHelper.getInstance().currWeekID;
        }else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")){
            url = ApiEndPoints.SELECTED_WEEK
                    + "?ClassID=" + CacheHelper.getInstance().userData.get(CLASS_ID_KEY)
                    + "&WeekID=" + CacheHelper.getInstance().currWeekID;
        }
        ApiHelper api = new ApiHelper(activity, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONArray planArray = res.optJSONArray("weekPlans");
                    for (int i = 0; i < planArray.length(); i++) {
                        JSONObject planObj = planArray.optJSONObject(i);
                        WeekPlan weekPlan = new WeekPlan(
                                planObj.optInt("LectureID"),
                                planObj.optInt("WeekDayID"),
                                planObj.optString("CourseName"),
                                planObj.optInt("ID"));
                        CacheHelper.getInstance().planWeekPlanList.add(weekPlan);
                    }
                    WeekPlan WeekPlanPadding;
                    for (int i = activity.startDay; i <= activity.endDay; i++) {
                        ArrayList<WeekPlan> Rows = new ArrayList<>();
                        for (int j = 1; j <= activity.maxLec; j++) {
                            WeekPlanPadding = new WeekPlan(0,0,"",0);
                            for (WeekPlan w : CacheHelper.getInstance().planWeekPlanList) {
                                if (w.weekDayID == i && w.lectureID == j)
                                    WeekPlanPadding = new WeekPlan(w.lectureID,w.weekDayID,w.courseName,w.planID);
                            }
                            Rows.add(WeekPlanPadding);
                        }
                        CacheHelper.getInstance().planLectures.add(Rows);
                    }
                    WeekPlanAdapter weekPlanAdapter = new WeekPlanAdapter();
                    generateWeekPlan(weekPlanAdapter);
                    weekPlanPanel.setPanelAdapter(weekPlanAdapter);
                    spinner.setSelection(adapter.getPosition(CacheHelper.getInstance().currWeek));
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



//    @Override
//    public void onResume() {
//        super.onResume();
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Weeks weeks = (Weeks) parent.getSelectedItem();
//                getSelectedWeekPlan(weeks.getWeekID());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//    }
}
