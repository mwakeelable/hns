package com.linked_sys.hns.ui.Activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.linked_sys.hns.R;
import com.linked_sys.hns.components.SpinnerDialog;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;
import com.linked_sys.hns.network.ApiEndPoints;
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class LoginActivity extends ParentActivity {
    EditText txt_email, txt_password;
    SpinnerDialog mProgress;
    AwesomeValidation awesomeValidation;
    String LOGIN_URL = ApiEndPoints.BASE_URL + ApiEndPoints.GET_TOKEN;
    String KEY_USERNAME = "username";
    String KEY_PASSWORD = "password";
    String KEY_GRANT_TYPE = "grant_type";
    Uri REGISTER_URL = Uri.parse("http://hns.edu.sa/account/Register");
    Uri FORGET_PASSWORD_URL = Uri.parse("http://hns.edu.sa/Account/ForgotPassword");
    String username, password, grant_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mProgress = new SpinnerDialog(this);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_password = (EditText) findViewById(R.id.txt_password);
        final Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txt_email.getText().toString().trim();
                password = txt_password.getText().toString().trim();
                grant_type = "password";
                String emailPattern = "([a-zA-Z0-9_-]+\\.)*[a-zA-Z0-9_-]+@[a-z]+(\\.[a-z]+)+";
                if (!username.matches(emailPattern)) {
                    awesomeValidation.validate();
                    return;
                }
                if (password.equalsIgnoreCase("")) {
                    awesomeValidation.validate();
                    return;
                }
                doLogin();
            }
        });
        Button btn_forget_password = (Button) findViewById(R.id.btn_forget_password);
        btn_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, FORGET_PASSWORD_URL);
                    LoginActivity.this.startActivity(intent);
                } else {
                    SimpleChromeCustomTabs
                            .getInstance()
                            .navigateTo(FORGET_PASSWORD_URL, LoginActivity.this);
                }
            }
        });

        TextView btn_register = (TextView) findViewById(R.id.txt_register);
        btn_register.setPaintFlags(btn_register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, REGISTER_URL);
                    LoginActivity.this.startActivity(intent);
                } else {
                    SimpleChromeCustomTabs
                            .getInstance()
                            .navigateTo(REGISTER_URL, LoginActivity.this);
                }
            }
        });

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.txt_email, android.util.Patterns.EMAIL_ADDRESS, R.string.invalidemailformat);
        awesomeValidation.addValidation(this, R.id.txt_password, " ", R.string.passwordCannotBeEmpty);

        txt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Button btn_login = (Button) findViewById(R.id.btn_login);
                    btn_login.performClick();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.login_activity;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void doLogin() {
        mProgress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            Log.d(AppController.TAG, response);
                        } else {
                            CacheHelper.getInstance().token = "";
                            StringTokenizer token = new StringTokenizer(response, ",");
                            String fullToken = token.nextToken();
                            CacheHelper.getInstance().token = fullToken.substring(17, fullToken.length() - 1);
                            session.createLoginSession(
                                    username,
                                    password,
                                    CacheHelper.getInstance().token);
                            getID_SendToken();
                            getData();
                            mProgress.hide();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new MaterialDialog.Builder(LoginActivity.this)
                                .title(getResources().getString(R.string.login_error))
                                .content(getResources().getString(R.string.login_failed_msg))
                                .positiveText(getResources().getString(R.string.login_positive_btn))
                                .show();
                        mProgress.hide();
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put(KEY_USERNAME, username);
                map.put(KEY_PASSWORD, password);
                map.put(KEY_GRANT_TYPE, grant_type);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}