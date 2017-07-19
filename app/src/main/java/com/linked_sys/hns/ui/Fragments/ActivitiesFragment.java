package com.linked_sys.hns.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linked_sys.hns.R;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.ui.Activities.MainActivity;

public class ActivitiesFragment extends Fragment {
    MainActivity activity;
    TextView txt_activities;
    public ProgressBar activities_progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return  inflater.inflate(R.layout.activities_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txt_activities = (TextView) view.findViewById(R.id.txt_activities);
        activities_progress = (ProgressBar) view.findViewById(R.id.activities_count);
        activities_progress.setProgress(CacheHelper.getInstance().activities);
        txt_activities.setText(String.valueOf(CacheHelper.getInstance().activities));

    }
}
