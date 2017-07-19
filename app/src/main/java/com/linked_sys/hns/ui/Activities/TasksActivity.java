package com.linked_sys.hns.ui.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.linked_sys.hns.Model.Tasks;
import com.linked_sys.hns.Model.WeekPlanContent;
import com.linked_sys.hns.R;
import com.linked_sys.hns.adapters.TasksAdapter;
import com.linked_sys.hns.components.DividerItemDecoration;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Fragments.TasksDetailsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.linked_sys.hns.core.CacheHelper.SCHOOL_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.TERM_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;
import static com.linked_sys.hns.core.CacheHelper.selectedSonID;

public class TasksActivity extends ParentActivity implements SwipeRefreshLayout.OnRefreshListener, TasksAdapter.TaskAdapterListener, SearchView.OnQueryTextListener {
    public ArrayList<Tasks> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    TasksAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int limit = 10;
    int skip = 0;
    boolean loadMore = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    LinearLayout placeholder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.nav_tasks));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View shadow = findViewById(R.id.toolbar_shadow);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            shadow.setVisibility(View.VISIBLE);
        else
            shadow.setVisibility(View.GONE);
        placeholder = (LinearLayout) findViewById(R.id.no_data_placeholder);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new TasksAdapter(this, tasks, this);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getTasks();
                    }
                }
        );
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loadMore) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            skip = skip + limit;
                            loadMoreTasks();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.tasks_activity;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                TasksActivity.this.finish();
                hideSoftKeyboard(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        limit = 10;
        skip = 0;
        getTasks();
    }

    @Override
    public void onTaskRowClicked(int position) {
        limit = 10;
        skip = 0;
        openPlanBody(mAdapter.filteredList.get(position).getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    private void getTasks() {
        String url = "";
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")) {
            url = ApiEndPoints.GET_TASKS
                    + "?StudentID=" + Integer.parseInt(CacheHelper.getInstance().userData.get(USER_ID_KEY))
                    + "&TermID=" + Integer.parseInt(CacheHelper.getInstance().userData.get(TERM_ID_KEY))
                    + "&SchoolID=" + Integer.parseInt(CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY))
                    + "&length=" + skip
                    + "&limit=" + limit;
        } else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3")) {
            url = ApiEndPoints.GET_TASKS
                    + "?StudentID=" + selectedSonID
                    + "&TermID=" + Integer.parseInt(CacheHelper.getInstance().selectedSon.get(TERM_ID_KEY))
                    + "&SchoolID=" + Integer.parseInt(CacheHelper.getInstance().selectedSon.get(SCHOOL_ID_KEY))
                    + "&length=" + skip
                    + "&limit=" + limit;
        }
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                tasks.clear();
                JSONObject obj = (JSONObject) response;
                try {
                    JSONArray tasksArray = obj.optJSONArray("tasks");
                    if (tasksArray.length() > 0) {
                        placeholder.setVisibility(View.GONE);
                        for (int i = 0; i < tasksArray.length(); i++) {
                            JSONObject taskData = tasksArray.optJSONObject(i);
                            Tasks task = new Tasks();
                            task.setId(taskData.optInt("WeekPlanID"));
                            task.setColor(getRandomMaterialColor("400"));
                            task.setSubject(taskData.optString("CourseName"));
                            task.setHomework(taskData.optString("HomeWork"));
                            task.setStatus(taskData.optString("HomeWorkFlag"));
                            tasks.add(task);
                        }
                        recyclerView.setAdapter(mAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                        if (tasksArray.length() < 10)
                            loadMore = false;
                        else
                            loadMore = true;
                    } else {
                        placeholder.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(TasksActivity.this, "Unable to fetch json: " + error.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        api.executeRequest(true, false);
    }

    private void loadMoreTasks() {
        String url = "";
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2")) {
            url = ApiEndPoints.GET_TASKS
                    + "?StudentID=" + Integer.parseInt(CacheHelper.getInstance().userData.get(USER_ID_KEY))
                    + "&TermID=" + Integer.parseInt(CacheHelper.getInstance().userData.get(TERM_ID_KEY))
                    + "&SchoolID=" + Integer.parseInt(CacheHelper.getInstance().userData.get(SCHOOL_ID_KEY))
                    + "&length=" + skip
                    + "&limit=" + limit;
        } else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3")) {
            url = ApiEndPoints.GET_TASKS
                    + "?StudentID=" + selectedSonID
                    + "&TermID=" + Integer.parseInt(CacheHelper.getInstance().selectedSon.get(TERM_ID_KEY))
                    + "&SchoolID=" + Integer.parseInt(CacheHelper.getInstance().selectedSon.get(SCHOOL_ID_KEY))
                    + "&length=" + skip
                    + "&limit=" + limit;
        }
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject obj = (JSONObject) response;
                try {
                    JSONArray tasksArray = obj.optJSONArray("tasks");
                    if (tasksArray.length() > 0) {
                        for (int i = 0; i < tasksArray.length(); i++) {
                            JSONObject taskData = tasksArray.optJSONObject(i);
                            Tasks task = new Tasks();
                            task.setId(taskData.optInt("WeekPlanID"));
                            task.setColor(getRandomMaterialColor("400"));
                            task.setSubject(taskData.optString("CourseName"));
                            task.setHomework(taskData.optString("HomeWork"));
                            task.setStatus(taskData.optString("HomeWorkFlag"));
                            tasks.add(task);
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        if (tasksArray.length() < 10)
                            loadMore = false;
                        else
                            loadMore = true;
                    }
                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(TasksActivity.this, "Unable to fetch json: " + error.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        api.executeRequest(true, false);
    }

    private void openPlanBody(int id) {
        String url = "";
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3"))
            url = ApiEndPoints.PLAN_BODY
                    + "?WeekPlanID=" + id
                    + "&StudentID=" + selectedSonID;
        else if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("2"))
            url = ApiEndPoints.PLAN_BODY
                    + "?WeekPlanID=" + id
                    + "&StudentID=" + CacheHelper.getInstance().userData.get(USER_ID_KEY);
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Log.d(AppController.TAG, response.toString());
                JSONObject res = (JSONObject) response;
                try {
                    JSONObject planBody = res.optJSONObject("sWeekPlans");
                    if (res.optString("sWeekPlans").equals("null")) {
                        Toast.makeText(TasksActivity.this, TasksActivity.this.getResources().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                    } else {
                        CacheHelper.getInstance().content = new WeekPlanContent(planBody.optInt("ID"),
                                planBody.optString("Subject"),
                                planBody.optString("HomeWork"),
                                planBody.optString("StudentHomeWork"),
                                planBody.optString("ParentNote"),
                                planBody.optString("TeacherNote"));
                        TasksDetailsFragment fragment = new TasksDetailsFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                        fragment.btn_close.setVisibility(View.GONE);
                        transaction.replace(R.id.tasks_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                } catch (Exception e) {
                    Log.d(AppController.TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Failed");
            }
        });
        api.executeRequest(true, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
