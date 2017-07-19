package com.linked_sys.hns.Services;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.linked_sys.hns.core.CacheHelper;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // TODO: Implement this method to send any registration to your app's servers.
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(CacheHelper.getInstance().REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
//                Map<String, String> map = new LinkedHashMap<>();
//                map.put("UserID", CacheHelper.getInstance().userData.get(USER_ID_KEY));
//                map.put("Token", token);
//                ApiHelper api = new ApiHelper(getApplicationContext(), ApiEndPoints.SEND_FB_TOKEN, Request.Method.POST, map, new ApiCallback() {
//                    @Override
//                    public void onSuccess(Object response) {
//                        Log.d(AppController.TAG, (String) response);
//                    }
//
//                    @Override
//                    public void onFailure(VolleyError error) {
//                        Log.d(AppController.TAG, "error");
//                    }
//                });
//                api.executePostRequest(false);
            }
        });
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(CacheHelper.getInstance().SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}