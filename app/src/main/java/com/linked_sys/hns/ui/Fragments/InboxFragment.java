package com.linked_sys.hns.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;

public class InboxFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener {
    MailActivity activity;
    public static ArrayList<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    public static MessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        return inflater.inflate(R.layout.inbox_fragment, container, false);
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
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
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
                            loadMoreInbox();
                        }
                    }
                }
            }
        });
    }

    private void getInbox() {
        String url = ApiEndPoints.GET_INBOX
                + "?RecieverID=" + CacheHelper.getInstance().userData.get(USER_ID_KEY)
                + "&length=" + skip
                + "&limit=" + limit;
        ApiHelper api = new ApiHelper(activity, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                messages.clear();
                JSONObject obj = (JSONObject) response;
                try {
                    JSONArray mailArray = obj.optJSONArray("Inbox");
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
                            message.setRead(mailData.optBoolean("IsRead"));
                            message.setMessage(mailData.optString("MailBody"));
                            message.setColor(activity.getRandomMaterialColor("400"));
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

    private void loadMoreInbox() {
        String url = ApiEndPoints.GET_INBOX
                + "?RecieverID=" + CacheHelper.getInstance().userData.get(USER_ID_KEY)
                + "&length=" + skip
                + "&limit=" + limit;
        ApiHelper api = new ApiHelper(activity, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject obj = (JSONObject) response;
                try {
                    JSONArray mailArray = obj.optJSONArray("Inbox");
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
                            message.setRead(mailData.optBoolean("IsRead"));
                            message.setMessage(mailData.optString("MailBody"));
                            message.setColor(activity.getRandomMaterialColor("400"));
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

    @Override
    public void onRefresh() {
        limit = 10;
        skip = 0;
        getInbox();
    }

    @Override
    public void onIconClicked(int position) {
        limit = 10;
        skip = 0;
        openMsgBody(position, mAdapter.filteredList.get(position).getId());
    }

    @Override
    public void onMessageRowClicked(int position) {
        limit = 10;
        skip = 0;
        openMsgBody(position, mAdapter.filteredList.get(position).getId());
    }

    @Override
    public void onRowLongClicked(int position) {

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
                        messages.get(i).getMessage(), pos,
                        messages.get(i).getColor()
                );
            }
        }
        Intent intent = new Intent(activity, MessageBodyActivity.class);
        intent.putExtra("inbox", true);
        intent.putExtra("pos", pos);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
