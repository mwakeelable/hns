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


public class AbsenceFragment extends Fragment {
    MainActivity activity;
    TextView txt_absence;
    public ProgressBar absence_progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.absence_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txt_absence = (TextView) view.findViewById(R.id.txt_absence);
        absence_progress = (ProgressBar) view.findViewById(R.id.absence_count);
        absence_progress.setProgress(CacheHelper.getInstance().absence);
        txt_absence.setText(String.valueOf(CacheHelper.getInstance().absence));
    }
}
