package com.linked_sys.hns.network;

import com.android.volley.error.VolleyError;

public interface ApiCallback {

    public void onSuccess(Object response);
    public void onFailure(VolleyError error);

}
