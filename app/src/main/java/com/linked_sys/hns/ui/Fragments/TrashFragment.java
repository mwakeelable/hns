package com.linked_sys.hns.ui.Fragments;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.linked_sys.hns.Model.MailBody;
import com.linked_sys.hns.Model.Message;
import com.linked_sys.hns.R;
import com.linked_sys.hns.adapters.MessagesAdapter;
import com.linked_sys.hns.components.DividerItemDecoration;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Activities.MailActivity;
import com.linked_sys.hns.ui.Activities.MessageBodyActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;

public class TrashFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener{
    MailActivity activity;
    public static ArrayList<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    public MessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    int limit = 10;
    int skip = 0;
    boolean loadMore = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    LinearLayout placeholder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MailActivity) getActivity();
        return inflater.inflate(R.layout.trash_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        placeholder = (LinearLayout) view.findViewById(R.id.no_data_placeholder);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new MessagesAdapter(activity, messages, this, false);
        mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        actionModeCallback = new ActionModeCallback();
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getTrash();
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
                            loadMoreTrash();
                        }
                    }
                }
            }
        });
    }

    private void getTrash() {
        String url = ApiEndPoints.GET_TRASH
                + "?RecieverID=" + CacheHelper.getInstance().userData.get(USER_ID_KEY)
                + "&length=" + skip
                + "&limit=" + limit;
        ApiHelper api = new ApiHelper(activity, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                messages.clear();
                JSONObject obj = (JSONObject) response;
                try {
                    JSONArray mailArray = obj.optJSONArray("Trash");
                    if (mailArray.length() > 0) {
                        placeholder.setVisibility(View.GONE);
                        for (int i = 0; i < mailArray.length(); i++) {
                            JSONObject mailData = mailArray.optJSONObject(i);
                            Message message = new Message();
                            message.setId(mailData.optInt("MailID"));
                            message.setSenderID(mailData.optInt("SenderID"));
                            message.setSenderName1(mailData.optString("SenderName1"));
                            message.setSenderName4(mailData.optString("SenderName4"));
                            message.setPicture(mailData.optString("SenderImage"));
                            message.setSubject(mailData.optString("Subject"));
                            message.setTimestamp(mailData.optString("SentDate"));
                            message.setRead(true);
                            message.setMessage(mailData.optString("MailBody"));
                            message.setColor(getRandomMaterialColor("400"));
                            messages.add(message);
                        }
                        recyclerView.setAdapter(mAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                        if (mailArray.length() < 10)
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
                Toast.makeText(activity, "Unable to fetch json: " + error.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        api.executeRequest(true, false);
    }

    private void loadMoreTrash() {
        String url = ApiEndPoints.GET_TRASH
                + "?RecieverID=" + CacheHelper.getInstance().userData.get(USER_ID_KEY)
                + "&length=" + skip
                + "&limit=" + limit;
        ApiHelper api = new ApiHelper(activity, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject obj = (JSONObject) response;
                try {
                    JSONArray mailArray = obj.optJSONArray("Trash");
                    if (mailArray.length() > 0) {
                        for (int i = 0; i < mailArray.length(); i++) {
                            JSONObject mailData = mailArray.optJSONObject(i);
                            Message message = new Message();
                            message.setId(mailData.optInt("MailID"));
                            message.setSenderID(mailData.optInt("SenderID"));
                            message.setSenderName1(mailData.optString("SenderName1"));
                            message.setSenderName4(mailData.optString("SenderName4"));
                            message.setPicture(mailData.optString("SenderImage"));
                            message.setSubject(mailData.optString("Subject"));
                            message.setTimestamp(mailData.optString("SentDate"));
                            message.setRead(true);
                            message.setMessage(mailData.optString("MailBody"));
                            message.setColor(getRandomMaterialColor("400"));
                            messages.add(message);
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        if (mailArray.length() < 10)
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
                Toast.makeText(activity, "Unable to fetch json: " + error.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        api.executeRequest(true, false);
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", activity.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        limit = 10;
        skip = 0;
        getTrash();
    }

    @Override
    public void onIconClicked(int position) {
//        if (actionMode == null) {
//            actionMode = activity.startSupportActionMode(actionModeCallback);
//        }
//        toggleSelection(position);
        if (actionMode == null) {
            limit = 10;
            skip = 0;
            openMsgBody(position, mAdapter.filteredList.get(position).getId());
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (actionMode == null) {
            limit = 10;
            skip = 0;
            openMsgBody(position, mAdapter.filteredList.get(position).getId());
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
//        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = activity.startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void openMsgBody(int pos, int mailID) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId() == mailID) {
                CacheHelper.getInstance().body = new MailBody(
                        messages.get(i).getId(),
                        messages.get(i).getSenderID(),
                        messages.get(i).getSenderName1(),
                        "", "", messages.get(i).getSenderName4(),
                        messages.get(i).getPicture(),
                        messages.get(i).getSubject(),
                        messages.get(i).getTimestamp(),
                        messages.get(i).isRead(),
                        messages.get(i).getMessage(),pos,
                        messages.get(i).getColor()
                );
            }
        }
        Intent intent = new Intent(activity,MessageBodyActivity.class);
        intent.putExtra("inbox",false);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
