package com.linked_sys.hns.ui.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jrummyapps.android.util.HtmlBuilder;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.CircleTransform;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Fragments.InboxFragment;
import com.linked_sys.hns.ui.Fragments.TrashFragment;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;


public class MessageBodyActivity extends ParentActivity {
    public TextView msg_subject, msg_content, txt_sender, txt_date, iconText;
    public ImageView btn_reply, btn_forward, imgProfile, btn_back, btn_delete, btn_mark_read;
    public RelativeLayout iconFront;
    Toolbar mToolbar;
    int pos;
    boolean inbox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controlsDef();
        applySenderImage();
        applyMessageData();
        applyMessageActions();
        if (!CacheHelper.getInstance().body.isRead()) {
            mark_read(CacheHelper.getInstance().body.getMailID());
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.message_body_activity;
    }

    private void controlsDef() {
        Intent in = getIntent();
        Bundle extra = in.getExtras();
        if (extra != null) {
            pos = extra.getInt("pos");
            inbox = extra.getBoolean("inbox");
        }
        msg_subject = (TextView) findViewById(R.id.txt_mail_subject);
        msg_content = (TextView) findViewById(R.id.msg_content);
        txt_sender = (TextView) findViewById(R.id.txt_sender_name);
        txt_date = (TextView) findViewById(R.id.txt_date);
        iconText = (TextView) findViewById(R.id.icon_text);
        imgProfile = (ImageView) findViewById(R.id.icon_profile);
        btn_reply = (ImageView) findViewById(R.id.btn_reply);
        btn_forward = (ImageView) findViewById(R.id.btn_forward);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_delete = (ImageView) findViewById(R.id.btn_delete);
//        btn_delete_trash = (ImageView) findViewById(R.id.btn_delete_trash);
        btn_mark_read = (ImageView) findViewById(R.id.btn_mark_unread);
        iconFront = (RelativeLayout) findViewById(R.id.icon_front);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (inbox) {
            btn_delete.setVisibility(View.VISIBLE);
            btn_mark_read.setVisibility(View.VISIBLE);
//            btn_delete_trash.setVisibility(View.GONE);
        } else {
            btn_delete.setVisibility(View.GONE);
            btn_mark_read.setVisibility(View.GONE);
//            btn_delete_trash.setVisibility(View.GONE);
        }
    }

    private void applySenderImage() {
        if (!CacheHelper.getInstance().body.getSenderImage().equals("null")) {
            Glide
                    .with(this)
                    .load(ApiEndPoints.BASE_URL + CacheHelper.getInstance().body.getSenderImage() + "?width=320")
                    .asBitmap()
                    .transform(new CircleTransform(this))
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            imgProfile.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            imgProfile.setColorFilter(getResources().getColor(R.color.teal_300));
                            iconText.setText(CacheHelper.getInstance().body.getSenderName1().substring(0, 1).toUpperCase() + CacheHelper.getInstance().body.getSenderName4().substring(0, 1).toUpperCase());
                        }
                    });
        } else {
            imgProfile.setColorFilter(CacheHelper.getInstance().body.getColor());
            iconText.setText(CacheHelper.getInstance().body.getSenderName1().substring(0, 1).toUpperCase() + CacheHelper.getInstance().body.getSenderName4().substring(0, 1).toUpperCase());
        }
    }

    private void applyMessageData() {
        txt_sender.setText(CacheHelper.getInstance().body.getSenderName1()
                + " "
                + CacheHelper.getInstance().body.getSenderName4());
        String mail_date = getDateFormat(CacheHelper.getInstance().body.getSentDate());
        txt_date.setText(mail_date);
        msg_subject.setText(CacheHelper.getInstance().body.getSubject());
        msg_content.setMovementMethod(LinkMovementMethod.getInstance());
        msg_content.setText(getFromHTML());
    }

    private Spanned getFromHTML() {
        HtmlBuilder html = new HtmlBuilder();
        html.p(CacheHelper.getInstance().body.getBody());
        return html.build();
    }

    private void applyMessageActions() {
        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("1")) {
                    CacheHelper.getInstance().isNewMsg = false;
                    CacheHelper.getInstance().isReply = true;
                    CacheHelper.getInstance().isForward = false;
                    openActivity(ComposeMessageToTeacherActivity.class);
                } else {
                    CacheHelper.getInstance().isNewMsg = false;
                    CacheHelper.getInstance().isReply = true;
                    CacheHelper.getInstance().isForward = false;
                    openActivity(ComposeMessageActivity.class);
                }
            }
        });

        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("1")) {
                    CacheHelper.getInstance().isNewMsg = false;
                    CacheHelper.getInstance().isReply = false;
                    CacheHelper.getInstance().isForward = true;
                    openActivity(ComposeMessageToTeacherActivity.class);
                } else {
                    CacheHelper.getInstance().isNewMsg = false;
                    CacheHelper.getInstance().isReply = false;
                    CacheHelper.getInstance().isForward = true;
                    openActivity(ComposeMessageActivity.class);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MessageBodyActivity.this)
                        .title(getResources().getString(R.string.delete_msg_title))
                        .content(getResources().getString(R.string.delete_msg_message))
                        .positiveText(getResources().getString(R.string.logout_positive_btn))
                        .negativeText(getResources().getString(R.string.logout_negative_btn))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                delete_msg();
                            }
                        })
                        .show();
            }
        });

//        btn_delete_trash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new MaterialDialog.Builder(MessageBodyActivity.this)
//                        .title(getString(R.string.delete_msg_title))
//
//  .content(getString(R.string.delete_from_trash_message))
//                        .positiveText(getString(R.string.logout_positive_btn))
//                        .negativeText(getString(R.string.logout_negative_btn))
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                delete_from_trash();
//                            }
//                        })
//                        .show();
//            }
//        });

        btn_mark_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark_unread();
            }
        });
    }

    public void delete_msg() {
        String url = ApiEndPoints.DELETE_MSG + "?MailID=" + CacheHelper.getInstance().body.getMailID();
        ApiHelper api = new ApiHelper(this, url, Request.Method.GET, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Toast.makeText(MessageBodyActivity.this, getResources().getString(R.string.txt_done), Toast.LENGTH_SHORT).show();
                getNewMsgCount();
                InboxFragment.mAdapter.filteredList.remove(pos);
                finish();
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Fail");
            }
        });
        api.executeRequest(true, false);
    }

    public void delete_from_trash() {
        String url = ApiEndPoints.DELETE_MSG_FROM_TRASH + "?MailID=" + CacheHelper.getInstance().body.getMailID();
        ApiHelper api = new ApiHelper(this, url, Request.Method.POST, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject res = (JSONObject) response;
                if (res.optString("deleted").equals("true")) {
                    Toast.makeText(MessageBodyActivity.this, getResources().getString(R.string.txt_done), Toast.LENGTH_SHORT).show();
                    getNewMsgCount();
                    TrashFragment.messages.remove(pos);
                    finish();
                } else
                    Toast.makeText(MessageBodyActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(MessageBodyActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        api.executeRequest(true, false);
    }

    private void mark_unread() {
        boolean isRead = false;
        Map<String, String> map = new LinkedHashMap<>();
        map.put("MailID", String.valueOf(CacheHelper.getInstance().body.getMailID()));
        map.put("IsRead", String.valueOf(isRead));
        ApiHelper api = new ApiHelper(this, ApiEndPoints.MARK_MSG_UNREAD, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                Toast.makeText(MessageBodyActivity.this, getResources().getString(R.string.txt_done), Toast.LENGTH_SHORT).show();
                getNewMsgCount();
                InboxFragment.mAdapter.filteredList.get(pos).setRead(false);
                finish();
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "Fail");
            }
        });
        api.executePostRequest(true);
    }
}