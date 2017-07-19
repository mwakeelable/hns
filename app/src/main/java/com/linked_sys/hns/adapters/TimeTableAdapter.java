package com.linked_sys.hns.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelin.scrollablepanel.library.PanelAdapter;
import com.linked_sys.hns.Model.DaysInfo;
import com.linked_sys.hns.Model.LecInfo;
import com.linked_sys.hns.core.CacheHelper;

import java.util.ArrayList;
import java.util.List;

import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;

public class TimeTableAdapter extends PanelAdapter {
    private static final int DAY_TYPE = 0;
    private static final int LEC_TYPE = 1;
    private static final int LESSON_TYPE = 2;
    private static final int TITLE_TYPE = 4;

    private List<DaysInfo> daysInfoList;
    private List<LecInfo> lecInfoList;
    ArrayList<ArrayList<String>> lectures = new ArrayList<>();
    ArrayList<ArrayList<String>> classes = new ArrayList<>();

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
                setDayView(row, (DayViewHolder) holder);
                break;
            case LEC_TYPE:
                setLecView(column, (LecViewHolder) holder);
                break;
            case LESSON_TYPE:
                setLessonView(row, column, (LessonViewHolder) holder);
                break;
            case TITLE_TYPE:
                break;
            default:
                setLessonView(row, column, (LessonViewHolder) holder);
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
                return new LecViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(com.linked_sys.hns.R.layout.listitem_lec_info, parent, false));
            case DAY_TYPE:
                return new DayViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(com.linked_sys.hns.R.layout.listitem_day_info, parent, false));
            case LESSON_TYPE:
                return new LessonViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(com.linked_sys.hns.R.layout.listitem_lesson_info, parent, false));
            case TITLE_TYPE:
                return new TitleViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(com.linked_sys.hns.R.layout.listitem_title, parent, false));
            default:
                break;
        }
        return new LessonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(com.linked_sys.hns.R.layout.listitem_lesson_info, parent, false));
    }


    private void setDayView(int pos, DayViewHolder viewHolder) {
        DaysInfo daysInfo = daysInfoList.get(pos - 1);
        if (daysInfo != null) {
            viewHolder.txt_day.setText(daysInfo.getDayName());
        }
    }

    private void setLecView(int pos, LecViewHolder viewHolder) {
        LecInfo lecInfo = lecInfoList.get(pos - 1);
        if (lecInfo != null && pos > 0) {
            viewHolder.txt_lec.setText(lecInfo.getLecName());
        }
    }

    private void setLessonView(final int row, final int column, LessonViewHolder viewHolder) {
//        final LessonInfo lessonInfo = lessonInfoList.get(row - 1).get(column - 1);
        viewHolder.txt_lesson.setText(lectures.get(row - 1).get(column - 1));
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("1")) {
            viewHolder.txt_class.setVisibility(View.VISIBLE);
            viewHolder.txt_class.setText(classes.get(row - 1).get(column - 1));
        } else
            viewHolder.txt_class.setVisibility(View.GONE);
//            viewHolder.view.setBackgroundResource(R.drawable.bg_white_gray_stroke);
//            viewHolder.txt_lesson.setText(lessonInfo.courseName);
    }


    private static class DayViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_day;

        public DayViewHolder(View view) {
            super(view);
            this.txt_day = (TextView) view.findViewById(com.linked_sys.hns.R.id.txt_day);
        }
    }

    private static class LecViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_lec;

        public LecViewHolder(View itemView) {
            super(itemView);
            this.txt_lec = (TextView) itemView.findViewById(com.linked_sys.hns.R.id.txt_lec);
        }
    }

    private static class LessonViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_lesson, txt_class;
        public View view;

        public LessonViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.txt_lesson = (TextView) itemView.findViewById(com.linked_sys.hns.R.id.txt_lesson);
            this.txt_class = (TextView) itemView.findViewById(com.linked_sys.hns.R.id.txt_class);
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public TitleViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(com.linked_sys.hns.R.id.title);
        }
    }


    public void setDaysInfoList(List<DaysInfo> daysInfoList) {
        this.daysInfoList = daysInfoList;
    }

    public void setLecInfoList(List<LecInfo> lecInfoList) {
        this.lecInfoList = lecInfoList;
    }

    public void setLessonInfoList(ArrayList<ArrayList<String>> lectures) {
        this.lectures = lectures;
    }

    public void setClassesInfoList(ArrayList<ArrayList<String>> classes) {
        this.classes = classes;
    }
}
