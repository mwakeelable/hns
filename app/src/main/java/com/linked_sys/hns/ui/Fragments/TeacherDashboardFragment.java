package com.linked_sys.hns.ui.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.CircleTransform;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.ui.Activities.MailActivity;
import com.linked_sys.hns.ui.Activities.MainActivity;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_1_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_4_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_IMAGE_KEY;
import static com.linked_sys.hns.core.CacheHelper.newMails;

public class TeacherDashboardFragment extends Fragment {
    MainActivity activity;
    public TextView txt_name;
    public ImageView img_user;
    SwipeRefreshLayout refreshLayout;
    TextView txt_new_messages;
    LinearLayout btn_new_msgs;
    public ProgressBar msg_progress, total_msg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.teacher_dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txt_name = (TextView) view.findViewById(R.id.txt_username);
        img_user = (ImageView) view.findViewById(R.id.profile_image);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_main);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                msg_progress.setProgress(newMails);
                txt_new_messages.setText(String.valueOf(newMails));
                refreshLayout.setRefreshing(false);
            }
        });
        txt_name.setText(CacheHelper.getInstance().userData.get(USERNAME_1_KEY) + " " + CacheHelper.getInstance().userData.get(USERNAME_4_KEY));
        if (!CacheHelper.getInstance().userData.get(USER_IMAGE_KEY).equals("null")) {
                Glide
                        .with(activity)
                        .load(ApiEndPoints.BASE_URL + CacheHelper.getInstance().userData.get(USER_IMAGE_KEY) + "?width=320")
                        .asBitmap()
                        .transform(new CircleTransform(activity))
                        .into(new SimpleTarget<Bitmap>(100, 100) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                img_user.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);

                            }
                        });
        }
        msg_progress = (ProgressBar) view.findViewById(R.id.msg_count);
        total_msg = (ProgressBar) view.findViewById(R.id.total_msg_count);
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

    @Override
    public void onResume() {
        super.onResume();
        msg_progress.setProgress(newMails);
        txt_new_messages.setText(String.valueOf(newMails));
        refreshLayout.setRefreshing(false);
    }

}
