package com.linked_sys.hns.ui.Activities;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.iid.FirebaseInstanceId;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.linked_sys.hns.Model.DaysInfo;
import com.linked_sys.hns.Model.LecInfo;
import com.linked_sys.hns.Model.WeekPlan;
import com.linked_sys.hns.Model.Weeks;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.CircleTransform;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Fragments.AddSonFragment;
import com.linked_sys.hns.ui.Fragments.MainFragment;
import com.linked_sys.hns.ui.Fragments.TeacherDashboardFragment;
import com.linked_sys.hns.ui.Fragments.TimeTableFragment;
import com.linked_sys.hns.ui.Fragments.WeekPlanContentFragment;
import com.linked_sys.hns.ui.Fragments.WeekPlanFragment;
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.linked_sys.hns.core.CacheHelper.CLASS_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.CLASS_NAME_KEY;
import static com.linked_sys.hns.core.CacheHelper.SCHOOL_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.STAGE_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.STAGE_NAME_KEY;
import static com.linked_sys.hns.core.CacheHelper.TERM_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_1_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_4_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_IMAGE_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;
import static com.linked_sys.hns.core.CacheHelper.newMails;
import static com.linked_sys.hns.core.CacheHelper.newTasks;
import static com.linked_sys.hns.core.CacheHelper.selectedSonID;


public class MainActivity extends ParentActivity {
    DrawerLayout mDrawerLayout;
    public FrameLayout mFrameLayout;
    CoordinatorLayout mCoordinatorLayout;
    ActionBar mActionBar;
    Toolbar mToolbar;
    MainFragment FRAGMENT_MAIN;
    TeacherDashboardFragment FRAGMENT_TEACHER_DASHBOARD;
    WeekPlanFragment FRAGMENT_WEEK_PLAN;
    AddSonFragment FRAGMENT_SON;
    TimeTableFragment FRAGMENT_TIME_TABLE;
    public DrawerView drawer;
    ImageView son;
    DaysInfo daysInfo;
    LecInfo lecInfo;
    public int maxLec, startDay, endDay;
    View shadow;
    Uri E_LIBRARY_URL = Uri.parse("http://hns.edu.sa/Ebooks/Grade" + CacheHelper.getInstance().userData.get(STAGE_ID_KEY));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        shadow = findViewById(R.id.toolbar_shadow);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            shadow.setVisibility(View.VISIBLE);
        else
            shadow.setVisibility(View.GONE);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(com.linked_sys.hns.R.id.coordinator);
        mDrawerLayout = (DrawerLayout) findViewById(com.linked_sys.hns.R.id.drawer_layout);
        mFrameLayout = (FrameLayout) findViewById(com.linked_sys.hns.R.id.containerView);
        drawer = (DrawerView) findViewById(com.linked_sys.hns.R.id.drawer);
        mToolbar = (Toolbar) findViewById(com.linked_sys.hns.R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeAsUpIndicator(com.linked_sys.hns.R.drawable.ic_menu_black_36dp);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        FRAGMENT_MY_TASKS = new TasksFragment();
        FRAGMENT_TIME_TABLE = new TimeTableFragment();
        FRAGMENT_WEEK_PLAN = new WeekPlanFragment();
        FRAGMENT_SON = new AddSonFragment();
        FRAGMENT_MAIN = new MainFragment();
        FRAGMENT_TEACHER_DASHBOARD = new TeacherDashboardFragment();
        son = (ImageView) findViewById(com.linked_sys.hns.R.id.img_son);
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("1"))
            drawTeacherDashboard();
        else
            drawMainFragment();
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                com.linked_sys.hns.R.string.drawer_open,
                com.linked_sys.hns.R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, com.linked_sys.hns.R.color.colorPrimary));
        mDrawerLayout.addDrawerListener(drawerToggle);
        mDrawerLayout.closeDrawer(drawer);

        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("1")) {
            getStaff();
            drawTeacherNav();
        } else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")) {
            getStudentWeekPlan();
            getClassTeachers();
            getStaff();
            drawStudentNav();
        } else {
            drawParentNav();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START))
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        else if (FRAGMENT_MAIN.isVisible())
            finishAffinity();
        else if (FRAGMENT_TEACHER_DASHBOARD.isVisible())
            finishAffinity();
        else if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    private void drawMainFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mFrameLayout.getChildCount() == 0) {
            transaction.replace(R.id.containerView, FRAGMENT_MAIN);
        } else {
            transaction.add(R.id.containerView, FRAGMENT_MAIN);
        }
        transaction.commit();
    }

    private void drawTeacherDashboard() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mFrameLayout.getChildCount() == 0) {
            transaction.replace(R.id.containerView, FRAGMENT_TEACHER_DASHBOARD);
        } else {
            transaction.add(R.id.containerView, FRAGMENT_TEACHER_DASHBOARD);
        }
        transaction.commit();
    }

    public void drawFragment(Fragment fragment) {
        closeFragments();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void getStudentWeekPlan() {
        CacheHelper.getInstance().planLectures.clear();
        CacheHelper.getInstance().planLecInfo.clear();
        CacheHelper.getInstance().planDaysInfo.clear();
        CacheHelper.getInstance().planWeekPlanList.clear();
        CacheHelper.getInstance().weeksList.clear();

        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3")) {
            studentID = selectedSonID;
            classID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(CLASS_ID_KEY));
            schoolID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(SCHOOL_ID_KEY));
            termID = Integer.parseInt(CacheHelper.getInstance().selectedSon.get(TERM_ID_KEY));
        } else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")) {
            studentID = Integer.parseInt(CacheHelper.getInstance().userData.get(USER_ID_KEY));
            classID = Integer.parseInt(CacheHelper.getInstance().userData.get(CLASS_ID_KEY));
            schoolID = Integer.parseInt(CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY));
            termID = Integer.parseInt(CacheHelper.getInstance().userData.get(TERM_ID_KEY));
        }

        String url = ApiEndPoints.WEEK_PLAN
                + "?ClassID=" + studentID
                + "&TermID=" + termID
                + "&SchoolID=" + schoolID;

        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                try {
                    JSONObject settings = res.getJSONObject("settings");
                    maxLec = settings.optInt("MaxLectures");
                    startDay = settings.optInt("StartDay");
                    endDay = settings.optInt("EndDay");
                    JSONArray weekDays = settings.optJSONArray("WeekDays");
                    for (int i = startDay - 1; i < endDay; i++) {
                        JSONObject dayObj = weekDays.getJSONObject(i);
                        daysInfo = new DaysInfo(
                                dayObj.optString("Name")
                                , dayObj.optInt("ID"));
                        CacheHelper.getInstance().planDaysInfo.add(daysInfo);
                    }
                    for (int i = 0; i < maxLec; i++) {
                        lecInfo = new LecInfo(String.valueOf(i + 1), i);
                        CacheHelper.getInstance().planLecInfo.add(lecInfo);
                    }
                    //Weeks List
                    JSONArray weeksArray = settings.optJSONArray("Weeks");
                    for (int i = 0; i < weeksArray.length(); i++) {
                        JSONObject weeksObj = weeksArray.optJSONObject(i);
                        Weeks weeks = new Weeks(
                                weeksObj.optInt("ID"),
                                weeksObj.optString("Name"));
                        CacheHelper.getInstance().currWeekID = weeksObj.optInt("ID");
                        CacheHelper.getInstance().weeksList.add(weeks);
                    }
                    //Current Week Details
                    JSONObject weekDetails = settings.optJSONObject("thisWeek");
                    CacheHelper.getInstance().currWeek = new Weeks(weekDetails.optInt("ID"), weekDetails.optString("Name"));
                    //Week Plan
                    JSONArray planArray = res.optJSONArray("weekPlans");
                    for (int i = 0; i < planArray.length(); i++) {
                        JSONObject planObj = planArray.optJSONObject(i);
                        WeekPlan weekPlan = new WeekPlan(
                                planObj.optInt("LectureID"),
                                planObj.optInt("WeekDayID"),
                                planObj.optString("CourseName"),
                                planObj.optInt("ID"));
                        CacheHelper.getInstance().planWeekPlanList.add(weekPlan);
                    }
                    WeekPlan WeekPlanPadding;
                    for (int i = startDay; i <= endDay; i++) {
                        ArrayList<WeekPlan> Rows = new ArrayList<>();
                        for (int j = 1; j <= maxLec; j++) {
                            WeekPlanPadding = new WeekPlan(0, 0, "", 0);
                            for (WeekPlan w : CacheHelper.getInstance().planWeekPlanList) {
                                if (w.weekDayID == i && w.lectureID == j)
                                    WeekPlanPadding = new WeekPlan(w.lectureID, w.weekDayID, w.courseName, w.planID);
                            }
                            Rows.add(WeekPlanPadding);
                        }
                        CacheHelper.getInstance().planLectures.add(Rows);
                    }
                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executeRequest(false, false);

    }

    private void closeFragments() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getFragmentManager().popBackStackImmediate();
            }
    }

    private void drawParentNav() {
        //Dashboard
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_dashboard_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_home))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        if (!FRAGMENT_MAIN.isVisible()) {
                            drawMainFragment();
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                })
        );
        drawer.addDivider();
        //Messages
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_mail_outline_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_inbox))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        openActivity(MailActivity.class);
                    }
                })
        );
        //Tasks
        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_assignment_black_24dp))
                        .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_tasks))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long l, int i) {
                                mDrawerLayout.closeDrawer(GravityCompat.START);
//                        drawFragment(FRAGMENT_MY_TASKS);
                                openActivity(TasksActivity.class);
                            }
                        })
        );
        drawer.addDivider();
        //Add Son
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_person_add_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_add_son))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        drawFragment(FRAGMENT_SON);
                    }
                })
        );
        //Time Table
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_grid_on_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_time_table))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        if (!FRAGMENT_TIME_TABLE.isVisible()) {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            drawFragment(FRAGMENT_TIME_TABLE);
                        } else
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                })
        );
        //Week Plan
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_grid_on_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_week_plan))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        WeekPlanContentFragment fragment = (WeekPlanContentFragment) getSupportFragmentManager().findFragmentByTag("week_plan_details");
                        if (FRAGMENT_WEEK_PLAN.isVisible() || fragment != null)
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        else {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            drawFragment(FRAGMENT_WEEK_PLAN);
                        }
                    }
                })
        );
        drawer.addDivider();
        //Profile
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_person_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.title_activity_profile))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        openActivity(ProfileActivity.class);
                    }
                })
        );
        //Settings
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_settings_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_settings))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        openActivity(SettingsActivity.class);
                    }
                })
        );
        drawer.addDivider();
        //Logout
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_power_settings_new_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_logout))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        logout();
                    }
                })
        );
        //change son
        drawer.setOnProfileSwitchListener(new DrawerProfile.OnProfileSwitchListener() {
            @Override
            public void onSwitch(DrawerProfile drawerProfile, long l, DrawerProfile drawerProfile1, long l1) {
                selectedSonID = (int) drawerProfile1.getId();
                for (int i = 0; i < CacheHelper.getInstance().sonData.size(); i++) {
                    if (CacheHelper.getInstance().sonData.get(i).ID == selectedSonID) {
                        CacheHelper.getInstance().selectedSon.put(USERNAME_1_KEY, CacheHelper.getInstance().sonData.get(i).Name1);
                        CacheHelper.getInstance().selectedSon.put(USERNAME_4_KEY, CacheHelper.getInstance().sonData.get(i).Name4);
                        CacheHelper.getInstance().selectedSon.put(STAGE_NAME_KEY, CacheHelper.getInstance().sonData.get(i).stageName);
                        CacheHelper.getInstance().selectedSon.put(USER_IMAGE_KEY, CacheHelper.getInstance().sonData.get(i).imgURL);
                        CacheHelper.getInstance().selectedSon.put(CLASS_NAME_KEY, CacheHelper.getInstance().sonData.get(i).className);
                        CacheHelper.getInstance().selectedSon.put(CLASS_ID_KEY, String.valueOf(CacheHelper.getInstance().sonData.get(i).classID));
                        CacheHelper.getInstance().selectedSon.put(TERM_ID_KEY, String.valueOf(CacheHelper.getInstance().sonData.get(i).termID));
                        CacheHelper.getInstance().selectedSon.put(SCHOOL_ID_KEY, String.valueOf(CacheHelper.getInstance().sonData.get(i).schoolID));
                        newTasks = CacheHelper.getInstance().sonData.get(i).tasksCount;
                        if (FRAGMENT_MAIN.FRAGMENT_TASKS != null) {
                            if (FRAGMENT_MAIN.FRAGMENT_TASKS.txt_new_tasks != null &&
                                    FRAGMENT_MAIN.FRAGMENT_TASKS.tasks_progress != null) {
                                FRAGMENT_MAIN.FRAGMENT_TASKS.tasks_progress.setProgress(newTasks);
                                FRAGMENT_MAIN.FRAGMENT_TASKS.txt_new_tasks.setText(String.valueOf(newTasks));
                            }
                        }
                    }
                }
                CacheHelper.getInstance().userData.get(String.valueOf(selectedSonID));
                getTimeTable();
                getStudentWeekPlan();
                getClassTeachers();
                getStaff();
            }
        });

        for (int i = 0; i < CacheHelper.getInstance().sonData.size(); i++) {
            final int finalI = i;
            if (CacheHelper.getInstance().sonData.get(finalI).imgURL.equals("")) {
                drawer.addProfile(new DrawerProfile()
                        .setId(CacheHelper.getInstance().sonData.get(finalI).ID)
                        .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                        .setName(
                                CacheHelper.getInstance().sonData.get(finalI).Name1
                                        + " "
                                        + CacheHelper.getInstance().sonData.get(finalI).Name4)
                        .setDescription(CacheHelper.getInstance().sonData.get(finalI).stageName)
                        .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.avatar_placeholder))
                );
            } else {
                Glide
                        .with(this)
                        .load(CacheHelper.getInstance().sonData.get(i).imgURL)
                        .asBitmap()
                        .transform(new CircleTransform(this))
                        .into(new SimpleTarget<Bitmap>(100, 100) {
                            @Override
                            public void onLoadStarted(Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                                studentAvatar = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                                        R.drawable.avatar_placeholder);
                                drawer.addProfile(new DrawerProfile()
                                        .setId(CacheHelper.getInstance().sonData.get(finalI).ID)
                                        .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                        .setName(
                                                CacheHelper.getInstance().sonData.get(finalI).Name1
                                                        + " "
                                                        + CacheHelper.getInstance().sonData.get(finalI).Name4)
                                        .setDescription(CacheHelper.getInstance().sonData.get(finalI).stageName)
                                        .setAvatar(new BitmapDrawable(getResources(), studentAvatar))
                                );
                            }

                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                studentAvatar = resource;
                                drawer.addProfile(new DrawerProfile()
                                        .setId(CacheHelper.getInstance().sonData.get(finalI).ID)
                                        .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                        .setName(
                                                CacheHelper.getInstance().sonData.get(finalI).Name1
                                                        + " "
                                                        + CacheHelper.getInstance().sonData.get(finalI).Name4)
                                        .setDescription(CacheHelper.getInstance().sonData.get(finalI).stageName)
                                        .setAvatar(new BitmapDrawable(getResources(), studentAvatar))
                                );
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                studentAvatar = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                                        R.drawable.avatar_placeholder);
                                drawer.addProfile(new DrawerProfile()
                                        .setId(CacheHelper.getInstance().sonData.get(finalI).ID)
                                        .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                        .setName(
                                                CacheHelper.getInstance().sonData.get(finalI).Name1
                                                        + " "
                                                        + CacheHelper.getInstance().sonData.get(finalI).Name4)
                                        .setDescription(CacheHelper.getInstance().sonData.get(finalI).stageName)
                                        .setAvatar(new BitmapDrawable(getResources(), studentAvatar))
                                );
                            }
                        });
            }
        }
    }

    private void drawStudentNav() {
        //Dashboard
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_dashboard_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_home))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        if (!FRAGMENT_MAIN.isVisible()) {
                            drawMainFragment();
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                })
        );
        drawer.addDivider();
        //Messages
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_mail_outline_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_inbox))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        openActivity(MailActivity.class);
                    }
                })
        );
        //Tasks
        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_assignment_black_24dp))
                        .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_tasks))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long l, int i) {
                                mDrawerLayout.closeDrawer(GravityCompat.START);
//                        drawFragment(FRAGMENT_MY_TASKS);
                                openActivity(TasksActivity.class);
                            }
                        })
        );
        drawer.addDivider();
        //Time Table
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_grid_on_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_time_table))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        if (!FRAGMENT_TIME_TABLE.isVisible()) {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            drawFragment(FRAGMENT_TIME_TABLE);
                        } else
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                })
        );
        //Week Plan
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_grid_on_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_week_plan))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        WeekPlanContentFragment fragment = (WeekPlanContentFragment) getSupportFragmentManager().findFragmentByTag("week_plan_details");
                        if (FRAGMENT_WEEK_PLAN.isVisible() || fragment != null)
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        else {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            drawFragment(FRAGMENT_WEEK_PLAN);
                        }
                    }
                })
        );
        //E-Library
//        drawer.addItem(new DrawerItem()
//                .setImage(ContextCompat.getDrawable(this, R.drawable.ic_grid_on_black_24dp))
//                .setTextPrimary(getString(R.string.txt_e_library))
//                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
//                    @Override
//                    public void onClick(DrawerItem drawerItem, long l, int i) {
//                        mDrawerLayout.closeDrawer(GravityCompat.START);
//                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW, E_LIBRARY_URL);
//                            MainActivity.this.startActivity(intent);
//                        } else {
//                            SimpleChromeCustomTabs
//                                    .getInstance()
//                                    .navigateTo(E_LIBRARY_URL, MainActivity.this);
//                        }
//                    }
//                })
//        );
        //E-Learn
//        drawer.addItem(new DrawerItem()
//                .setImage(ContextCompat.getDrawable(this, R.drawable.ic_grid_on_black_24dp))
//                .setTextPrimary(getString(R.string.txt_e_learn))
//                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
//                    @Override
//                    public void onClick(DrawerItem drawerItem, long l, int i) {
//                        mDrawerLayout.closeDrawer(GravityCompat.START);
//                        openActivity(E_LearningActivity.class);
//                    }
//                })
//        );
        drawer.addDivider();
        //Profile
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_person_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.title_activity_profile))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        openActivity(ProfileActivity.class);
                    }
                })
        );
        //Settings
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_settings_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_settings))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        openActivity(SettingsActivity.class);
                    }
                })
        );
        drawer.addDivider();
        //Logout
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_power_settings_new_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_logout))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        logout();
                    }
                })
        );
        //Add Profile
        if (CacheHelper.getInstance().userData.get(USER_IMAGE_KEY).equals("")) {
            drawer.addProfile(new DrawerProfile()
                    .setId(Long.parseLong(CacheHelper.getInstance().userData.get(USER_ID_KEY)))
                    .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                    .setName(
                            CacheHelper.getInstance().userData.get(USERNAME_1_KEY)
                                    + " "
                                    + CacheHelper.getInstance().userData.get(USERNAME_4_KEY))
                    .setDescription(CacheHelper.getInstance().userData.get(STAGE_NAME_KEY))
                    .setAvatar(ContextCompat.getDrawable(this, R.drawable.avatar_placeholder))
            );
        } else {
            Glide
                    .with(this)
                    .load(ApiEndPoints.BASE_URL + CacheHelper.getInstance().userData.get(USER_IMAGE_KEY) + "?width=320")
                    .asBitmap()
                    .transform(new CircleTransform(this))
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            drawer.addProfile(new DrawerProfile()
                                    .setId(Long.parseLong(CacheHelper.getInstance().userData.get(USER_ID_KEY)))
                                    .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                    .setName(
                                            CacheHelper.getInstance().userData.get(USERNAME_1_KEY)
                                                    + " "
                                                    + CacheHelper.getInstance().userData.get(USERNAME_4_KEY))
                                    .setDescription(CacheHelper.getInstance().userData.get(STAGE_NAME_KEY))
                                    .setAvatar(ContextCompat.getDrawable(MainActivity.this, R.drawable.avatar_placeholder))
                            );
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            studentAvatar = resource;
                            drawer.addProfile(new DrawerProfile()
                                    .setId(Long.parseLong(CacheHelper.getInstance().userData.get(USER_ID_KEY)))
                                    .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                    .setName(
                                            CacheHelper.getInstance().userData.get(USERNAME_1_KEY)
                                                    + " "
                                                    + CacheHelper.getInstance().userData.get(USERNAME_4_KEY))
                                    .setDescription(CacheHelper.getInstance().userData.get(STAGE_NAME_KEY))
                                    .setAvatar(new BitmapDrawable(getResources(), studentAvatar))
                            );
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            drawer.addProfile(new DrawerProfile()
                                    .setId(Long.parseLong(CacheHelper.getInstance().userData.get(USER_ID_KEY)))
                                    .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                    .setName(
                                            CacheHelper.getInstance().userData.get(USERNAME_1_KEY)
                                                    + " "
                                                    + CacheHelper.getInstance().userData.get(USERNAME_4_KEY))
                                    .setDescription(CacheHelper.getInstance().userData.get(STAGE_NAME_KEY))
                                    .setAvatar(ContextCompat.getDrawable(MainActivity.this, R.drawable.avatar_placeholder))
                            );
                        }
                    });
        }
    }

    private void drawTeacherNav() {
        //Dashboard
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_dashboard_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_home))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        if (!FRAGMENT_TEACHER_DASHBOARD.isVisible()) {
                            drawTeacherDashboard();
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                })
        );
        drawer.addDivider();
        //Messages
        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_mail_outline_black_24dp))
                        .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_inbox))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long l, int i) {
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                openActivity(MailActivity.class);
//                                drawFragment(FRAGMENT_MESSAGES);
                            }
                        })
        );
        drawer.addDivider();
        //Time Table
        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_grid_on_black_24dp))
                        .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_time_table))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, long l, int i) {
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                FRAGMENT_TIME_TABLE = new TimeTableFragment();
                                drawFragment(FRAGMENT_TIME_TABLE);
//                                showToast();
                            }
                        })
        );
        drawer.addDivider();
        //Profile
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_person_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.title_activity_profile))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        openActivity(ProfileActivity.class);
                    }
                })
        );
        //Settings
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_settings_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_settings))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        openActivity(SettingsActivity.class);
                    }
                })
        );
        drawer.addDivider();
        //Logout
        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, com.linked_sys.hns.R.drawable.ic_power_settings_new_black_24dp))
                .setTextPrimary(getString(com.linked_sys.hns.R.string.nav_logout))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        logout();
                    }
                })
        );
        //Add Profile
        if (CacheHelper.getInstance().userData.get(USER_IMAGE_KEY).equals("")) {
            drawer.addProfile(new DrawerProfile()
                    .setId(Long.parseLong(CacheHelper.getInstance().userData.get(USER_ID_KEY)))
                    .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                    .setName(
                            CacheHelper.getInstance().userData.get(USERNAME_1_KEY)
                                    + " "
                                    + CacheHelper.getInstance().userData.get(USERNAME_4_KEY))
                    .setDescription(CacheHelper.getInstance().userData.get(STAGE_NAME_KEY))
                    .setAvatar(ContextCompat.getDrawable(this, R.drawable.avatar_placeholder))
            );
        } else {
            Glide
                    .with(this)
                    .load(ApiEndPoints.BASE_URL + CacheHelper.getInstance().userData.get(USER_IMAGE_KEY) + "?width=320")
                    .asBitmap()
                    .transform(new CircleTransform(this))
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            drawer.addProfile(new DrawerProfile()
                                    .setId(Long.parseLong(CacheHelper.getInstance().userData.get(USER_ID_KEY)))
                                    .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                    .setName(
                                            CacheHelper.getInstance().userData.get(USERNAME_1_KEY)
                                                    + " "
                                                    + CacheHelper.getInstance().userData.get(USERNAME_4_KEY))
                                    .setDescription(CacheHelper.getInstance().userData.get(STAGE_NAME_KEY))
                                    .setAvatar(ContextCompat.getDrawable(MainActivity.this, R.drawable.avatar_placeholder))
                            );
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            studentAvatar = resource;
                            drawer.addProfile(new DrawerProfile()
                                    .setId(Long.parseLong(CacheHelper.getInstance().userData.get(USER_ID_KEY)))
                                    .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                    .setName(
                                            CacheHelper.getInstance().userData.get(USERNAME_1_KEY)
                                                    + " "
                                                    + CacheHelper.getInstance().userData.get(USERNAME_4_KEY))
                                    .setDescription(CacheHelper.getInstance().userData.get(STAGE_NAME_KEY))
                                    .setAvatar(new BitmapDrawable(getResources(), studentAvatar))
                            );
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            drawer.addProfile(new DrawerProfile()
                                    .setId(Long.parseLong(CacheHelper.getInstance().userData.get(USER_ID_KEY)))
                                    .setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary))
                                    .setName(
                                            CacheHelper.getInstance().userData.get(USERNAME_1_KEY)
                                                    + " "
                                                    + CacheHelper.getInstance().userData.get(USERNAME_4_KEY))
                                    .setDescription(CacheHelper.getInstance().userData.get(STAGE_NAME_KEY))
                                    .setAvatar(ContextCompat.getDrawable(MainActivity.this, R.drawable.avatar_placeholder))
                            );
                        }
                    });
        }
    }

    private void removeFBToken() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("UserID", CacheHelper.getInstance().userData.get(USER_ID_KEY));
        map.put("Token", FirebaseInstanceId.getInstance().getToken());
        ApiHelper api = new ApiHelper(this, ApiEndPoints.REMOVE_FB_TOKEN, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                session.logoutUser();
                newMails = 0;
                setBadge(MainActivity.this, newMails);
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, error.getMessage());
            }
        });
        api.executePostRequest(true);
    }

    private void logout() {
        new MaterialDialog.Builder(MainActivity.this)
                .title(getResources().getString(R.string.logout_title))
                .content(getResources().getString(R.string.logout_msg))
                .positiveText(getResources().getString(R.string.logout_positive_btn))
                .negativeText(getResources().getString(R.string.logout_negative_btn))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        removeFBToken();
                    }
                })
                .show();
    }

    @Override
    protected void onPause() {
        SimpleChromeCustomTabs.getInstance().disconnectFrom(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        SimpleChromeCustomTabs.getInstance().connectTo(this);
    }
}
