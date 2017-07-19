package com.linked_sys.hns.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.kelin.scrollablepanel.library.PanelAdapter;
import com.linked_sys.hns.Model.DaysInfo;
import com.linked_sys.hns.Model.LecInfo;
import com.linked_sys.hns.Model.WeekPlan;
import com.linked_sys.hns.Model.WeekPlanContent;
import com.linked_sys.hns.R;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Activities.MainActivity;
import com.linked_sys.hns.ui.Fragments.WeekPlanContentFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;
import static com.linked_sys.hns.core.CacheHelper.selectedSonID;

public class WeekPlanAdapter extends PanelAdapter {
    private static final int DAY_TYPE = 0;
    private static final int LEC_TYPE = 1;
    private static final int LESSON_TYPE = 2;
    private static final int TITLE_TYPE = 4;
    private List<DaysInfo> daysInfoList;
    private List<LecInfo> lecInfoList;
    ArrayList<ArrayList<WeekPlan>> lectures = new ArrayList<>();

    @Override
    public int getRowCount() {
        return daysInfoList.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return lecInfoList.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
        int viewType = getItemViewType(row, column);
        switch (viewType) {
            case DAY_TYPE:
                setDayView(row, (WeekPlanAdapter.DayViewHolder) holder);
                break;
            case LEC_TYPE:
                setLecView(column, (WeekPlanAdapter.LecViewHolder) holder);
                break;
            case LESSON_TYPE:
                setLessonView(row, column, (WeekPlanAdapter.LessonViewHolder) holder);
                break;
            case TITLE_TYPE:
                break;
            default:
                setLessonView(row, column, (WeekPlanAdapter.LessonViewHolder) holder);
        }
    }

    public int getItemViewType(int row, int column) {
        if (column == 0 && row == 0) {
            return TITLE_TYPE;
        }
        if (column == 0) {
            return DAY_TYPE;
        }
        if (row == 0) {
            return LEC_TYPE;
        }
        return LESSON_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case LEC_TYPE:
                return new WeekPlanAdapter.LecViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_lec_info, parent, false));
            case DAY_TYPE:
                return new WeekPlanAdapter.DayViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_day_info, parent, false));
            case LESSON_TYPE:
                return new WeekPlanAdapter.LessonViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_lesson_info, parent, false));
            case TITLE_TYPE:
                return new WeekPlanAdapter.TitleViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_title, parent, false));
            default:
                break;
        }
        return new WeekPlanAdapter.LessonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(com.linked_sys.hns.R.layout.listitem_lesson_info, parent, false));
    }

    private void setDayView(int pos, WeekPlanAdapter.DayViewHolder viewHolder) {
        DaysInfo daysInfo = daysInfoList.get(pos - 1);
        if (daysInfo != null) {
            viewHolder.txt_day.setText(daysInfo.getDayName());
        }
    }

    private void setLecView(int pos, WeekPlanAdapter.LecViewHolder viewHolder) {
        LecInfo lecInfo = lecInfoList.get(pos - 1);
        if (lecInfo != null && pos > 0) {
            viewHolder.txt_lec.setText(lecInfo.getLecName());
        }
    }

    private void setLessonView(final int row, final int column, WeekPlanAdapter.LessonViewHolder viewHolder) {
        viewHolder.txt_class.setVisibility(View.GONE);
        viewHolder.txt_lesson.setText(lectures.get(row - 1).get(column - 1).courseName);
        if (viewHolder.txt_lesson.getText().equals(""))
            viewHolder.itemView.setClickable(true);
        else
            viewHolder.itemView.setClickable(false);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlanBody(v.getContext(), row, column);
            }
        });
    }

    private static class DayViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_day;

        public DayViewHolder(View view) {
            super(view);
            this.txt_day = (TextView) view.findViewById(R.id.txt_day);
        }
    }

    private static class LecViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_lec;

        public LecViewHolder(View itemView) {
            super(itemView);
            this.txt_lec = (TextView) itemView.findViewById(R.id.txt_lec);
        }
    }

    private static class LessonViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_lesson, txt_class;
        public View view;

        public LessonViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.txt_lesson = (TextView) itemView.findViewById(R.id.txt_lesson);
            this.txt_class = (TextView) itemView.findViewById(R.id.txt_class);
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public TitleViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.title);
        }
    }

    public void setDaysInfoList(List<DaysInfo> daysInfoList) {
        this.daysInfoList = daysInfoList;
    }

    public void setLecInfoList(List<LecInfo> lecInfoList) {
        this.lecInfoList = lecInfoList;
    }

    public void setLessonInfoList(ArrayList<ArrayList<WeekPlan>> lectures) {
        this.lectures = lectures;
    }

    private void openPlanBody(final Context context, int row, final int column) {
        String url = "";
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3"))
            url = ApiEndPoints.PLAN_BODY
                    + "?WeekPlanID=" + lectures.get(row - 1).get(column - 1).planID
                    + "&StudentID=" + selectedSonID;
        else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2"))
            url = ApiEndPoints.PLAN_BODY
                    + "?WeekPlanID=" + lectures.get(row - 1).get(column - 1).planID
                    + "&StudentID=" + CacheHelper.getInstance().userData.get(USER_ID_KEY);
        ApiHelper api = new ApiHelper(context, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Log.d(AppController.TAG, response.toString());
                JSONObject res = (JSONObject) response;
                try {
                    JSONObject planBody = res.optJSONObject("sWeekPlans");
                    if (res.optString("sWeekPlans").equals("null")) {
                        Toast.makeText(context, context.getResources().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                    } else {
                        CacheHelper.getInstance().content = new WeekPlanContent(planBody.optInt("ID"),
                                planBody.optString("Subject"),
                                planBody.optString("HomeWork"),
                                planBody.optString("StudentHomeWork"),
                                planBody.optString("ParentNote"),
                                planBody.optString("TeacherNote"));
                        /*if (CacheHelper.getInstance().content.getParentNote().equals("null"))
                            WeekPlanFragment.parent_comment = "";
                        else
                            WeekPlanFragment.parent_comment = CacheHelper.getInstance().content.getParentNote();

                        if (!CacheHelper.getInstance().content.getSubject().equals("null"))
                            WeekPlanFragment.txt_subject.setText(CacheHelper.getInstance().content.getSubject());

                        if (!CacheHelper.getInstance().content.getHomeWork().equals("null"))
                            WeekPlanFragment.txt_homework.setText(CacheHelper.getInstance().content.getHomeWork());

                        if (!CacheHelper.getInstance().content.getTeacherNote().equals("null"))
                            WeekPlanFragment.txt_teacher_note.setText(CacheHelper.getInstance().content.getTeacherNote());

                        if (!CacheHelper.getInstance().content.getParentNote().equals("null"))
                            WeekPlanFragment.txt_parent_note.setText(CacheHelper.getInstance().content.getParentNote());

                        if (CacheHelper.getInstance().content.isStudentHomeWork().equals("null"))
                            WeekPlanFragment.txt_solved.setText(" ");
                        else if (CacheHelper.getInstance().content.isStudentHomeWork().equals("true"))
                            WeekPlanFragment.txt_solved.setText(context.getResources().getString(txt_solved));
                        else
                            WeekPlanFragment.txt_solved.setText(context.getResources().getString(R.string.txt_not_solved));
                        WeekPlanFragment.planDetailsContainer.setVisibility(View.VISIBLE);*/
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            WeekPlanContentFragment fragment = new WeekPlanContentFragment();
                            FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
//                            fragment.btn_close.setVisibility(View.VISIBLE);
                            transaction.add(R.id.containerView, fragment, "week_plan_details");
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else{
                            WeekPlanContentFragment fragment = new WeekPlanContentFragment();
                            FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
//                            fragment.btn_close.setVisibility(View.VISIBLE);
                            transaction.replace(R.id.containerView, fragment, "week_plan_details");
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
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
        api.executeRequest(true, false);
    }
}
