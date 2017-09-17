package com.example.kimhun.thewalker

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class StartActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private val RC_SIGN_IN = 9001
    private val TAG = "StartAcitivty"

    private var mAuth: FirebaseAuth? = null
    private lateinit var mGoogleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        findViewById(R.id.sign_in_button).setOnClickListener(this)

        var gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        mAuth = FirebaseAuth.getInstance()

    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()

        var currentUser : FirebaseUser? = mAuth?.currentUser
        updateUI(currentUser)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account : GoogleSignInAccount? = result.signInAccount
                firebaseAuthWithGoogle(account)
            } else {

                updateUI(null)
            }
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                        val user = mAuth!!.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@StartActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                })
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            findViewById(R.id.sign_in_button).visibility = View.VISIBLE
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult)
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.sign_in_button) {
            signIn()
        }
    }
}
