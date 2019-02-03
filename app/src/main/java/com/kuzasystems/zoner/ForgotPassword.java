package com.kuzasystems.zoner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ForgotPassword extends AppCompatActivity  {


    private AutoCompleteTextView mEmailView;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }



    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString().trim();
        if(email.equals("")||email.equals(null)){
            mEmailView.setError("Please specify your username");
        }else {
            final ProgressDialog progress = new ProgressDialog(ForgotPassword.this);
            progress.setTitle("Please wait..");
            progress.setMessage("Please wait while we reset your password");
            progress.show();
            RequestQueue requestQueue = Volley.newRequestQueue(ForgotPassword.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "forgotpassword.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progress.cancel();
                    android.util.Log.d("ERROR", response);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                    //   }
                    builder.setTitle("Response")
                            .setMessage(response)

                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();

                    // clear();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.cancel();
                    Toast.makeText(ForgotPassword.this, "" + "We encountered an error while resetting you password. Please try again later", Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    param.put("Username", email);


                    return param;
                }
            };


            requestQueue.add(stringRequest);
        }
    }

    public void loginClick(View v){
        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
        startActivity(intent);
    }
    public void registerClick(View v){
        Intent intent = new Intent(ForgotPassword.this, Register.class);
        startActivity(intent);
    }

}

