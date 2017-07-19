package com.linked_sys.hns.ui.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.CircleTransform;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiEndPoints;

import static com.linked_sys.hns.core.CacheHelper.USERNAME_1_KEY;
import static com.linked_sys.hns.core.CacheHelper.USERNAME_4_KEY;
import static com.linked_sys.hns.core.CacheHelper.USER_IMAGE_KEY;

public class ProfileActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ImageView img_user = (ImageView) findViewById(R.id.img_user);
        TextView txt_user_name = (TextView) findViewById(R.id.txt_user_name);
        TextView txt_user_email = (TextView) findViewById(R.id.txt_user_email);
        TextView txt_user_dob = (TextView) findViewById(R.id.txt_user_dob);
        TextView txt_user_id = (TextView) findViewById(R.id.txt_user_id);
        if (!CacheHelper.getInstance().userData.get(USER_IMAGE_KEY).equals("null")) {
            Glide
                    .with(this)
                    .load(ApiEndPoints.BASE_URL + CacheHelper.getInstance().userData.get(USER_IMAGE_KEY) + "?width=320")
                    .asBitmap()
                    .transform(new CircleTransform(this))
                    .into(new SimpleTarget<Bitmap>(300, 300) {
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
        txt_user_name.setText(CacheHelper.getInstance().userData.get(USERNAME_1_KEY) + " " + CacheHelper.getInstance().userData.get(USERNAME_4_KEY));
        txt_user_email.setText("");
        txt_user_dob.setText("");
        txt_user_id.setText("");
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }
}
