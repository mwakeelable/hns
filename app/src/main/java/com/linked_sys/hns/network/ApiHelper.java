package com.linked_sys.hns.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.linked_sys.hns.components.SpinnerDialog;
import com.linked_sys.hns.core.AppController;
import com.linked_sys.hns.core.CacheHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiHelper {
    Context context;
    int requestMethod;
    SpinnerDialog mProgress;
    String url;
    JSONObject params;
    Map<String, String> mapParams;
    ApiCallback callback;
    String tag = "json_req";

    /*
    ApiHelper Constructor
    url : Request EndPoint
    requestMethod : POST / GET / PUT
    params : JSONObject Params for POST requests
    ApiCallback : Interface that implements success and failure callbacks
     */
    public ApiHelper(Context context, String url, int requestMethod, ApiCallback callback) {
        this.context = context;
        this.url = ApiEndPoints.BASE_URL + url;
        this.requestMethod = requestMethod;
        this.callback = callback;
        mProgress = new SpinnerDialog(context);
    }

    public ApiHelper(Context context, String url, int requestMethod, Map<String, String> mapParams, ApiCallback callback) {
        this.mapParams = mapParams;
        this.context = context;
        this.url = ApiEndPoints.BASE_URL + url;
        this.requestMethod = requestMethod;
        this.callback = callback;
        mProgress = new SpinnerDialog(context);
    }

    public void executePostRequest(boolean showDialog){
        if (showDialog)
            mProgress.show();
        StringRequest request = new StringRequest(requestMethod, url, mListener, mErrorListener) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return mapParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "bearer " + CacheHelper.getInstance().token);
                return params;
            }
        };
        request.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(request, tag);
    }

    public void executeRequest(boolean showDialog, boolean responseIsArray) {
        if (showDialog)
            mProgress.show();
        Request request = null;
        if (responseIsArray)
            request = executeJsonArrayRequest();
        else
            request = executeJsonObjectRequest();
        request.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(request, tag);
    }

    private Request executeJsonObjectRequest() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestMethod,
                url, params, mListener, mErrorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "bearer " + CacheHelper.getInstance().token);
                return params;
            }

            @Override
            public byte[] getBody() {
                if (params == null)
                    params = new JSONObject();
                return params.toString().getBytes();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        return jsonObjReq;
    }


    private Request executeJsonArrayRequest() {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(requestMethod,
                url, null, mListener, mErrorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map params = new HashMap();
                params.put("Authorization", "bearer " + CacheHelper.getInstance().token);
                return params;
            }

            /*
            Overriding the getBody function animationType the JSONArrayRequest Type
            Because the /User/ Api Endpoint needs the request body sent as bytes for some reason
            Please revise the backend security check
             */
            @Override
            public byte[] getBody() {
                if (params == null)
                    params = new JSONObject();
                return params.toString().getBytes();
            }
        };
        return jsonArrReq;
    }


    Response.Listener mListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            mProgress.hide();
            callback.onSuccess(response);
        }
    };


    Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mProgress.hide();
            callback.onFailure(error);
        }
    };
}
