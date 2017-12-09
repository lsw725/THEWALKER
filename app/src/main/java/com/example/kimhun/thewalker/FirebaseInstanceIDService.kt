package com.example.kimhun.thewalker

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by kimhun on 2017-12-02.
 */
class FirebaseInstanceIDService : FirebaseInstanceIdService() {
    private val TAG = "MyFirebaseIIDService"

    override fun onTokenRefresh() {
        var refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed Token: " + refreshedToken)

        sendRegistrationToServer(refreshedToken!!)
    }

    private fun sendRegistrationToServer(token : String){

    }

}