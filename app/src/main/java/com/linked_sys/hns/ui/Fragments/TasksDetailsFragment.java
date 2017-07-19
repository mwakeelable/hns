package com.linked_sys.hns.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.linked_sys.hns.R;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiCallback;
import com.linked_sys.hns.network.ApiEndPoints;
import com.linked_sys.hns.network.ApiHelper;
import com.linked_sys.hns.ui.Activities.TasksActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.linked_sys.hns.core.CacheHelper.USER_TYPE_KEY;


public class TasksDetailsFragment extends Fragment {
    TasksActivity activity;
    String parent_comment;
    TextView txt_subject, txt_homework, txt_solved, txt_teacher_note, txt_parent_note;
    public ImageView btn_close;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (TasksActivity) getActivity();
        return inflater.inflate(R.layout.week_plan_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txt_subject = (TextView) view.findViewById(R.id.txt_sub_content);
        txt_homework = (TextView) view.findViewById(R.id.txt_homework_content);
        txt_solved = (TextView) view.findViewById(R.id.txt_solved_content);
        txt_teacher_note = (TextView) view.findViewById(R.id.txt_teacher_note_content);
        txt_parent_note = (TextView) view.findViewById(R.id.txt_parent_note_content);
        btn_close = (ImageView) view.findViewById(R.id.btn_close);
        btn_close.setVisibility(View.GONE);
        if (CacheHelper.getInstance().content.getParentNote().equals("null"))
            parent_comment = "";
        else
            parent_comment = CacheHelper.getInstance().content.getParentNote();

        if (!CacheHelper.getInstance().content.getSubject().equals("null"))
            txt_subject.setText(CacheHelper.getInstance().content.getSubject());

        if (!CacheHelper.getInstance().content.getHomeWork().equals("null"))
            txt_homework.setText(CacheHelper.getInstance().content.getHomeWork());

        if (!CacheHelper.getInstance().content.getTeacherNote().equals("null"))
            txt_teacher_note.setText(CacheHelper.getInstance().content.getTeacherNote());

        if (!CacheHelper.getInstance().content.getParentNote().equals("null"))
            txt_parent_note.setText(CacheHelper.getInstance().content.getParentNote());

        if (CacheHelper.getInstance().content.isStudentHomeWork().equals("null"))
            txt_solved.setText(" ");
        else if (CacheHelper.getInstance().content.isStudentHomeWork().equals("true"))
            txt_solved.setText(activity.getResources().getString(R.string.txt_solved));
        else
            txt_solved.setText(activity.getResources().getString(R.string.txt_not_solved));

        ImageView btn_comment = (ImageView) view.findViewById(R.id.btn_parent_note);

        if (CacheHelper.getInstance().userData.get(USER_TYPE_KEY).equals("3"))
            btn_comment.setVisibility(View.VISIBLE);
        else
            btn_comment.setVisibility(View.GONE);

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(activity)
                        .title(activity.getResources().getString(R.string.txt_add_comment))
                        .input(null, parent_comment, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                try {
                                    String comment = URLEncoder.encode(String.valueOf(input), "utf-8");
                                    addComment(comment);
                                    txt_parent_note.setText(String.valueOf(input));
                                    parent_comment = String.valueOf(input);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();
            }
        });

    }

    private void addComment(String comment) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("StudentWeekPlanID", String.valueOf(CacheHelper.getInstance().content.getID()));
        map.put("ParentNote", comment);
        ApiHelper api = new ApiHelper(activity, ApiEndPoints.ADD_COMMENT, Request.Method.POST, map, new ApiCallback() {
            @Override
            public void onSuccess(Object response) {

            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(AppController.TAG, "failed");
            }
        });
        api.executePostRequest(true);
    }
}
