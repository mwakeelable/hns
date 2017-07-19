package com.linked_sys.hns.ui.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.etiya.etiyabadgetablib.EtiyaBadgeTab;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.CircleTransform;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.ui.Activities.MainActivity;

import static com.linked_sys.hns.core.CacheHelper.USERNAME_1_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_4_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_IMAGE_KEY;
import static com.linked_sys.hns.core.CacheHelper.absence;
import static com.linked_sys.hns.core.CacheHelper.activities;
import static com.linked_sys.hns.core.CacheHelper.behaviour;
import static com.linked_sys.hns.core.CacheHelper.newMails;
import static com.linked_sys.hns.core.CacheHelper.newTasks;


public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private MainActivity activity;
    private ViewPager viewPager;
    public MessagesInfoFragment FRAGMENT_MESSAGES;
    public TasksInfoFragment FRAGMENT_TASKS;
    public AbsenceFragment FRAGMENT_ABSENCE;
    public ActivitiesFragment FRAGMENT_ACTIVITIES;
    public BehaviorFragment FRAGMENT_BEHAVIOR;
    private TextView txt_name;
    private ImageView img_user;
    private SwipeRefreshLayout refreshLayout;
    private EtiyaBadgeTab etiyaBadgeTab;
    private String[] titles = new String[5];
    private MainAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new MainAdapter(getChildFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        defineControls(view);
        applyHeader();
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
        etiyaBadgeTab.setupWithViewPager(viewPager);
        setupTabs();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FRAGMENT_MESSAGES != null) {
            if (FRAGMENT_MESSAGES.msg_progress != null && FRAGMENT_MESSAGES.txt_new_messages != null) {
                FRAGMENT_MESSAGES.msg_progress.setProgress(newMails);
                FRAGMENT_MESSAGES.txt_new_messages.setText(String.valueOf(newMails));
            }
        }

        if (FRAGMENT_TASKS != null) {
            if (FRAGMENT_TASKS.txt_new_tasks != null && FRAGMENT_TASKS.tasks_progress != null) {
                FRAGMENT_TASKS.tasks_progress.setProgress(newTasks);
                FRAGMENT_TASKS.txt_new_tasks.setText(String.valueOf(newTasks));
            }
        }

        if (FRAGMENT_ABSENCE != null) {
            if (FRAGMENT_ABSENCE.txt_absence != null && FRAGMENT_ABSENCE.absence_progress != null) {
                FRAGMENT_ABSENCE.absence_progress.setProgress(absence);
                FRAGMENT_ABSENCE.txt_absence.setText(String.valueOf(absence));
            }
        }

        if (FRAGMENT_BEHAVIOR != null) {
            if (FRAGMENT_BEHAVIOR.txt_behaviour != null && FRAGMENT_BEHAVIOR.behaviour_progress != null) {
                FRAGMENT_BEHAVIOR.behaviour_progress.setProgress(behaviour);
                FRAGMENT_BEHAVIOR.txt_behaviour.setText(String.valueOf(behaviour));
            }
        }

        if (FRAGMENT_ACTIVITIES != null) {
            if (FRAGMENT_ACTIVITIES.txt_activities != null && FRAGMENT_ACTIVITIES.activities_progress != null) {
                FRAGMENT_ACTIVITIES.activities_progress.setProgress(activities);
                FRAGMENT_ACTIVITIES.txt_activities.setText(String.valueOf(activities));
            }
        }
    }

    @Override
    public void onRefresh() {
        if (FRAGMENT_MESSAGES.msg_progress != null && FRAGMENT_MESSAGES.txt_new_messages != null) {
            FRAGMENT_MESSAGES.msg_progress.setProgress(newMails);
            FRAGMENT_MESSAGES.txt_new_messages.setText(String.valueOf(newMails));
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setRefreshing(false);
        }
        if (FRAGMENT_TASKS.txt_new_tasks != null && FRAGMENT_TASKS.tasks_progress != null) {
            FRAGMENT_TASKS.tasks_progress.setProgress(newTasks);
            FRAGMENT_TASKS.txt_new_tasks.setText(String.valueOf(newTasks));
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setRefreshing(false);
        }
        if (FRAGMENT_ABSENCE.absence_progress != null && FRAGMENT_ABSENCE.txt_absence != null) {
            FRAGMENT_ABSENCE.absence_progress.setProgress(absence);
            FRAGMENT_ABSENCE.txt_absence.setText(String.valueOf(absence));
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setRefreshing(false);
        }
        if (FRAGMENT_ACTIVITIES.activities_progress != null && FRAGMENT_ACTIVITIES.txt_activities != null) {
            FRAGMENT_ACTIVITIES.activities_progress.setProgress(activities);
            FRAGMENT_ACTIVITIES.txt_activities.setText(String.valueOf(activities));
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setRefreshing(false);
        }
        if (FRAGMENT_BEHAVIOR.behaviour_progress != null && FRAGMENT_BEHAVIOR.txt_behaviour != null) {
            FRAGMENT_BEHAVIOR.behaviour_progress.setProgress(behaviour);
            FRAGMENT_BEHAVIOR.txt_behaviour.setText(String.valueOf(behaviour));
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setRefreshing(false);
        }
        setupTabs();
        refreshLayout.setRefreshing(false);
    }

    private void applyHeader() {
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
    }

    private void defineControls(View view) {
        FRAGMENT_ABSENCE = new AbsenceFragment();
        FRAGMENT_ACTIVITIES = new ActivitiesFragment();
        FRAGMENT_BEHAVIOR = new BehaviorFragment();
        FRAGMENT_MESSAGES = new MessagesInfoFragment();
        FRAGMENT_TASKS = new TasksInfoFragment();
        txt_name = (TextView) view.findViewById(R.id.txt_username);
        img_user = (ImageView) view.findViewById(R.id.profile_image);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_main);
        refreshLayout.setOnRefreshListener(this);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        etiyaBadgeTab = (EtiyaBadgeTab) view.findViewById(R.id.etiyaBadgeTabs);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    private void setupTabs() {
        etiyaBadgeTab.setSelectedTabIndicatorColor(activity.getResources().getColor(R.color.black));
        etiyaBadgeTab.setSelectedTabIndicatorHeight(5);
        etiyaBadgeTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        etiyaBadgeTab.setTabGravity(TabLayout.GRAVITY_CENTER);
        etiyaBadgeTab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        etiyaBadgeTab.selectEtiyaBadgeTab(0)
                .tabTitle(activity.getResources().getString(R.string.tab1))
                .tabTitleColor(R.color.white)
                .tabIconColor(R.color.white)
                .tabBadge(true)
                .tabBadgeCount(newMails)
                .tabBadgeBgColor(R.color.red_400)
                .tabBadgeTextColor(R.color.white)
                .tabBadgeStroke(1, R.color.white)
                .tabBadgeCornerRadius(10)
                .createEtiyaBadgeTab();

        etiyaBadgeTab.selectEtiyaBadgeTab(1)
                .tabTitle(activity.getResources().getString(R.string.tab2))
                .tabTitleColor(R.color.white)
                .tabIconColor(R.color.white)
                .tabBadge(true)
                .tabBadgeCount(newTasks)
                .tabBadgeBgColor(R.color.red_400)
                .tabBadgeTextColor(R.color.white)
                .tabBadgeStroke(1, R.color.white)
                .tabBadgeCornerRadius(10)
                .createEtiyaBadgeTab();

        etiyaBadgeTab.selectEtiyaBadgeTab(2)
                .tabTitle(activity.getResources().getString(R.string.tab4))
                .tabTitleColor(R.color.white)
                .tabIconColor(R.color.white)
                .tabBadge(true)
                .tabBadgeCount(absence)
                .tabBadgeBgColor(R.color.red_400)
                .tabBadgeTextColor(R.color.white)
                .tabBadgeStroke(1, R.color.white)
                .tabBadgeCornerRadius(10)
                .createEtiyaBadgeTab();

        etiyaBadgeTab.selectEtiyaBadgeTab(3)
                .tabTitle(activity.getResources().getString(R.string.tab5))
                .tabTitleColor(R.color.white)
                .tabIconColor(R.color.white)
                .tabBadge(true)
                .tabBadgeCount(behaviour)
                .tabBadgeBgColor(R.color.red_400)
                .tabBadgeTextColor(R.color.white)
                .tabBadgeStroke(1, R.color.white)
                .tabBadgeCornerRadius(10)
                .createEtiyaBadgeTab();

        etiyaBadgeTab.selectEtiyaBadgeTab(4)
                .tabTitle(activity.getResources().getString(R.string.tab6))
                .tabTitleColor(R.color.white)
                .tabIconColor(R.color.white)
                .tabBadge(true)
                .tabBadgeCount(activities)
                .tabBadgeBgColor(R.color.red_400)
                .tabBadgeTextColor(R.color.white)
                .tabBadgeStroke(1, R.color.white)
                .tabBadgeCornerRadius(10)
                .createEtiyaBadgeTab();
    }

    private class MainAdapter extends FragmentStatePagerAdapter {
        MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (FRAGMENT_MESSAGES == null)
                    FRAGMENT_MESSAGES = new MessagesInfoFragment();
                return FRAGMENT_MESSAGES;
            } else if (position == 1) {
                if (FRAGMENT_TASKS == null)
                    FRAGMENT_TASKS = new TasksInfoFragment();
                return FRAGMENT_TASKS;
            } else if (position == 2) {
                if (FRAGMENT_ABSENCE == null)
                    FRAGMENT_ABSENCE = new AbsenceFragment();
                return FRAGMENT_ABSENCE;
            } else if (position == 3) {
                if (FRAGMENT_BEHAVIOR == null)
                    FRAGMENT_BEHAVIOR = new BehaviorFragment();
                return FRAGMENT_BEHAVIOR;
            } else {
                if (FRAGMENT_ACTIVITIES == null)
                    FRAGMENT_ACTIVITIES = new ActivitiesFragment();
                return FRAGMENT_ACTIVITIES;
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (refreshLayout != null) {
            refreshLayout.setEnabled(enable);
        }
    }
}
