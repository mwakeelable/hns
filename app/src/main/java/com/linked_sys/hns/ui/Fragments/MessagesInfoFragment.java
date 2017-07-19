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
import com.linked_sys.hns.ui.Activities.MailActivity;
import com.linked_sys.hns.ui.Activities.MainActivity;

import static com.linked_sys.hns.core.CacheHelper.newMails;


public class MessagesInfoFragment extends Fragment {
    MainActivity activity;
    TextView txt_new_messages;
    LinearLayout btn_new_msgs;
    public ProgressBar msg_progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.messages_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        msg_progress = (ProgressBar) view.findViewById(R.id.msg_count);
        txt_new_messages = (TextView) view.findViewById(R.id.txt_new_messages);
        btn_new_msgs = (LinearLayout) view.findViewById(R.id.btn_new_msgs);
        msg_progress.setProgress(newMails);
        txt_new_messages.setText(String.valueOf(newMails));
        btn_new_msgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openActivity(MailActivity.class);
            }
        });
    }
}
