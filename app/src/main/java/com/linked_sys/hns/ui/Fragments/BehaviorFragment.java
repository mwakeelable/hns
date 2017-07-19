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


public class BehaviorFragment extends Fragment {
    MainActivity activity;
    TextView txt_behaviour;
    public ProgressBar behaviour_progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.behavior_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txt_behaviour = (TextView) view.findViewById(R.id.txt_behaviour);
        behaviour_progress = (ProgressBar) view.findViewById(R.id.behaviour_count);
        behaviour_progress.setProgress(CacheHelper.getInstance().behaviour);
        txt_behaviour.setText(String.valueOf(CacheHelper.getInstance().behaviour));
    }
}
