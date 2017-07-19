package com.linked_sys.hns.ui.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrusoft.scatter.ChartData;
import com.intrusoft.scatter.PieChart;
import com.linked_sys.hns.R;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.ui.Activities.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class SkillsFragment extends Fragment {
    MainActivity activity;
    public List<ChartData> data = new ArrayList<>();
    public PieChart skillsChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.skills_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        skillsChart = (PieChart) view.findViewById(R.id.pie_chart);
        data.add(new ChartData(String.valueOf(CacheHelper.getInstance().skills_100), 0, Color.BLACK, activity.getResources().getColor(R.color.green_300)));
        data.add(new ChartData(String.valueOf(CacheHelper.getInstance().skills_90_100), 0, Color.BLACK, activity.getResources().getColor(R.color.yellow_300)));
        data.add(new ChartData(String.valueOf(CacheHelper.getInstance().skills_80_90), 0, Color.BLACK, activity.getResources().getColor(R.color.orange_300)));
        data.add(new ChartData(String.valueOf(CacheHelper.getInstance().skills_80), 0, Color.BLACK, activity.getResources().getColor(R.color.red_300)));
        skillsChart.setChartData(data);
    }
}
