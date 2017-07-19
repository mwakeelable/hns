package com.linked_sys.hns.ui.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.linked_sys.hns.Model.Lessons;
import com.linked_sys.hns.R;
import com.linked_sys.hns.adapters.LessonsAdapter;
import com.linked_sys.hns.components.DividerItemDecoration;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;


public class E_LearningActivity extends ParentActivity implements SwipeRefreshLayout.OnRefreshListener, LessonsAdapter.LessonsAdapterListener, SearchView.OnQueryTextListener {
    public ArrayList<Lessons> lessons = new ArrayList<>();
    LessonsAdapter mAdapter;
    RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.txt_e_learn));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.lessons_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new LessonsAdapter(this, lessons, this);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getLessons();
                    }
                }
        );

    }

    private void getLessons() {
        String url = ApiEndPoints.GET_LESSONS + "?StudentID=" + CacheHelper.getInstance().userData.get(USER_ID_KEY);
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                lessons.clear();
                JSONObject res = (JSONObject) response;
                try {
                    JSONArray lessonsArray = res.optJSONArray("Lessons");
                    for (int i = 0; i < lessonsArray.length(); i++) {
                        JSONObject obj = lessonsArray.optJSONObject(i);
                        Lessons lessonsObj = new Lessons
                                (obj.optInt("LessonID"),
                                        obj.optInt("CourseID"),
                                        obj.optString("CourseName"),
                                        obj.optString("LessonName"),
                                        obj.optString("VideoPath"));
                        if (!lessonsObj.getVideoURL().equals("null"))
                            lessons.add(lessonsObj);
                    }
                    recyclerView.setAdapter(mAdapter);
                    swipeRefreshLayout.setRefreshing(false);
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
                E_LearningActivity.this.finish();
                hideSoftKeyboard(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.e_learning_activity;
    }

    @Override
    public void onRefresh() {
        getLessons();
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

    @Override
    public void onLessonRowClicked(int position) {
        Intent intent = new Intent(E_LearningActivity.this, VideoActivity.class);
        intent.putExtra("url",mAdapter.filteredList.get(position).getVideoURL());
        intent.putExtra("name",mAdapter.filteredList.get(position).getLessonName());
        startActivity(intent);
    }
}
