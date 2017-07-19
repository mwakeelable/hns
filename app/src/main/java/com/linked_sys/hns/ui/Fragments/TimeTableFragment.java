package com.linked_sys.hns.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelin.scrollablepanel.library.ScrollablePanel;
import com.linked_sys.hns.R;
import com.linked_sys.hns.adapters.TimeTableAdapter;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.ui.Activities.MainActivity;


public class TimeTableFragment extends Fragment {
    MainActivity activity;
    ScrollablePanel timeTablePanel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.time_table_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        timeTablePanel = (ScrollablePanel) view.findViewById(R.id.time_table);
        TimeTableAdapter timeTableAdapter = new TimeTableAdapter();
        generateTimeTableData(timeTableAdapter);
        timeTablePanel.setPanelAdapter(timeTableAdapter);
    }

    private void generateTimeTableData(TimeTableAdapter timeTableAdapter) {
        timeTableAdapter.setDaysInfoList(CacheHelper.getInstance().daysInfo);
        timeTableAdapter.setLecInfoList(CacheHelper.getInstance().lecInfo);
        timeTableAdapter.setLessonInfoList(CacheHelper.getInstance().lectures);
        timeTableAdapter.setClassesInfoList(CacheHelper.getInstance().classes);
    }
}
