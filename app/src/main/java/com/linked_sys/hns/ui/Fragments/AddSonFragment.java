package com.linked_sys.hns.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.linked_sys.hns.R;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Activities.MainActivity;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.linked_sys.hns.core.CacheHelper.USER_ID_KEY;

public class AddSonFragment extends Fragment {
    MainActivity activity;
    RelativeLayout msg_container;
    LinearLayout btn_add;
    EditText txt_sID;
    TextView txt_msg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.add_son_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        msg_container = (RelativeLayout) view.findViewById(R.id.add_msg_container);
        txt_sID = (EditText) view.findViewById(R.id.txt_son_id);
        btn_add = (LinearLayout) view.findViewById(R.id.btn_add);
        txt_msg = (TextView) view.findViewById(R.id.txt_add_msg);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_sID.getText().toString().equals("")) {
                    new MaterialDialog.Builder(activity)
                            .title(getResources().getString(R.string.login_error))
                            .content(getResources().getString(R.string.txt_add_son_error))
                            .positiveText(getResources().getString(R.string.login_positive_btn))
                            .show();
                } else
                    addSon(txt_sID.getEditableText().toString());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        msg_container.setVisibility(View.GONE);
    }

    private void addSon(String id) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("ParentID", CacheHelper.getInstance().userData.get(USER_ID_KEY));
        map.put("StudentIdentityNo", id);
        ApiHelper api = new ApiHelper(activity, ApiEndPoints.ADD_SON, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {
                String msg = (String) response;
                if (msg.contains("success")) {
                    txt_msg.setText(getResources().getString(R.string.txt_add_success));
                    refreshMain();
                } else if (msg.contains("Fail"))
                    txt_msg.setText(getResources().getString(R.string.txt_add_fail));
                else if (msg.contains("Has Parent"))
                    txt_msg.setText(getResources().getString(R.string.txt_add_has_parent));
                else
                    txt_msg.setText(getResources().getString(R.string.txt_add_already_added));
                msg_container.setVisibility(View.VISIBLE);
                txt_sID.getText().clear();
                activity.hideSoftKeyboard(activity);
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
        api.executePostRequest(true);
    }

    private void refreshMain() {
        activity.finish();
        activity.getData();
    }
}