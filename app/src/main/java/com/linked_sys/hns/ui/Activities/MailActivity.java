package com.linked_sys.hns.ui.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.linked_sys.hns.R;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Fragments.MessagesFragment;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;
//
//@Shortcut(
//        id = "Email",
//        icon = R.drawable.ic_email,
//        shortLabel = "Email",
//        shortLabelRes = R.string.shortcut_books_short_label,
//        longLabel = "List of books",
//        longLabelRes = R.string.shortcut_books_long_label,
//        rank = 2, // order in list, relative to other shortcuts
//        disabledMessage = "No books are available",
//        disabledMessageRes = R.string.shortcut_books_disabled_message,
//        enabled = true, // default
//        backStack = {MainActivity.class, LibraryActivity.class},
//        activity = MainActivity.class, // the launcher activity to which the shortcut should be attached
//        action = "shortcut_books" // intent action to identify the shortcut from the launched activity
//)
//@Shortcut(id = "email", action = "email_shortcut", icon = R.drawable.ic_email_white_24dp, rank = 3,
//        backStack = {MainActivity.class, MailActivity.class})
public class MailActivity extends ParentActivity implements SearchView.OnQueryTextListener {
    private FloatingActionButton btn_compose_parent_msg;
    private FloatingActionMenu btn_compose_teacher_msg;
    private FrameLayout container;
    private MessagesFragment FRAGMENT_MESSAGES;
    private ImageView btn_delete, btn_unread, btn_back;
    private TextView txt_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View shadow = findViewById(R.id.toolbar_shadow);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            shadow.setVisibility(View.VISIBLE);
        else
            shadow.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_inbox));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txt_title = (TextView) findViewById(R.id.mail_title);
        container = (FrameLayout) findViewById(R.id.messages_container);
        btn_compose_parent_msg = (FloatingActionButton) findViewById(R.id.btn_new_parent_message);
        btn_compose_teacher_msg = (FloatingActionMenu) findViewById(R.id.btn_new_teacher_message);
        FloatingActionButton btn_private_msg = (FloatingActionButton) findViewById(R.id.btn_private_message);
        FloatingActionButton btn_group_msg = (FloatingActionButton) findViewById(R.id.btn_group_message);
        btn_delete = (ImageView) findViewById(R.id.btn_delete);
        btn_unread = (ImageView) findViewById(R.id.btn_unread);
        btn_back = (ImageView) findViewById(R.id.btn_back_mail);
        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("1")) {
            btn_compose_teacher_msg.setVisibility(View.VISIBLE);
            btn_compose_parent_msg.setVisibility(View.GONE);
        } else {
            btn_compose_parent_msg.setVisibility(View.VISIBLE);
            btn_compose_teacher_msg.setVisibility(View.GONE);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_compose_teacher_msg.isOpened())
                    btn_compose_teacher_msg.close(true);
                else
                    getSupportFragmentManager().popBackStack();
            }
        });

        btn_compose_parent_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheHelper.getInstance().isNewMsg = true;
                CacheHelper.getInstance().isReply = false;
                CacheHelper.getInstance().isForward = false;
                openActivity(ComposeMessageActivity.class);
            }
        });
        btn_private_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MailActivity.this)
                        .title(getResources().getString(R.string.txt_private_msg))
                        .items(R.array.private_msg_list)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which == 0) {
                                    //TEACHER
                                    CacheHelper.getInstance().isNewMsg = true;
                                    CacheHelper.getInstance().isReply = false;
                                    CacheHelper.getInstance().isForward = false;
                                    openActivity(ComposeMessageToTeacherActivity.class);
                                } else {
                                    //STUDENT
                                    CacheHelper.getInstance().isNewMsg = true;
                                    CacheHelper.getInstance().isReply = false;
                                    CacheHelper.getInstance().isForward = false;
                                    openActivity(ComposeMessageToStudentActivity.class);
                                }
                                return true;
                            }
                        })
                        .show();
            }
        });
        btn_group_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheHelper.getInstance().isNewMsg = true;
                CacheHelper.getInstance().isReply = false;
                CacheHelper.getInstance().isForward = false;
                openActivity(ComposeGroupMessageActivity.class);
            }
        });
        drawTabs();
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_msg();
            }
        });

        btn_unread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark_unread();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.mail_activity;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (btn_compose_teacher_msg.isOpened()) {
                btn_compose_teacher_msg.close(true);
            } else if (btn_compose_teacher_msg.isOpened())
                btn_compose_teacher_msg.close(true);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (btn_compose_teacher_msg.isOpened())
            btn_compose_teacher_msg.close(true);
        else
            super.onBackPressed();
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
                MailActivity.this.finish();
                hideSoftKeyboard(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        FRAGMENT_MESSAGES.inboxFragment.mAdapter.getFilter().filter(newText);
        FRAGMENT_MESSAGES.outboxFragment.mAdapter.getFilter().filter(newText);
        FRAGMENT_MESSAGES.trashFragment.mAdapter.getFilter().filter(newText);
        return true;
    }

    private void drawTabs() {
        FRAGMENT_MESSAGES = new MessagesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (container.getChildCount() == 0) {
            transaction.replace(R.id.messages_container, FRAGMENT_MESSAGES);
        } else {
            transaction.add(R.id.messages_container, FRAGMENT_MESSAGES);
        }
        transaction.commit();
    }

    public void delete_msg() {
        String url = ApiEndPoints.DELETE_MSG + "?MailID=" + CacheHelper.getInstance().body.getMailID();
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Toast.makeText(MailActivity.this, getResources().getString(R.string.txt_done), Toast.LENGTH_SHORT).show();
                getNewMsgCount();
                getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Fail");
            }
        });
        api.executeRequest(true, false);
    }

    private void mark_unread() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("MailID", String.valueOf(CacheHelper.getInstance().body.getMailID()));
        map.put("IsRead", "false");
        ApiHelper api = new ApiHelper(this, ApiEndPoints.MARK_MSG_UNREAD, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Toast.makeText(MailActivity.this, getResources().getString(R.string.txt_done), Toast.LENGTH_SHORT).show();
                getNewMsgCount();
                getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Fail");
            }
        });
        api.executePostRequest(true);
    }
}
