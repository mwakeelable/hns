package com.linked_sys.hns.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linked_sys.hns.R;
import com.linked_sys.hns.ui.Activities.MainActivity;
import com.linked_sys.hns.ui.Activities.TasksActivity;

import static com.linked_sys.hns.core.CacheHelper.newTasks;


public class TasksInfoFragment extends Fragment {
    MainActivity activity;
    public ProgressBar tasks_progress;
    public TextView txt_new_tasks;
    LinearLayout btn_new_tasks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.tasks_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tasks_progress = (ProgressBar) view.findViewById(R.id.tasks_count);
        txt_new_tasks = (TextView) view.findViewById(R.id.txt_new_tasks);
        btn_new_tasks = (LinearLayout) view.findViewById(R.id.btn_new_tasks);
        tasks_progress.setProgress(newTasks);
        txt_new_tasks.setText(String.valueOf(newTasks));
        btn_new_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                activity.drawFragment(activity.FRAGMENT_MY_TASKS);
                activity.openActivity(TasksActivity.class);
            }
        });
    }
}
