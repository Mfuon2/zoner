package com.kuzasystems.zoner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int REQUEST_LOCATIONS = 1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };
    double latitude, longitude;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public static void verifyLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if ((permission != PackageManager.PERMISSION_GRANTED) || (permission1 != PackageManager.PERMISSION_GRANTED)) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATIONS
            );

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        /*mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
*/
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAccountStatus(mEmailView.getText().toString());
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        verifyLocationPermissions(LoginActivity.this);

    }

    public void onPause() {
        super.onPause();
        //finish();
    }

    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(LoginActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, LoginActivity.this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(LoginActivity.this,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();

            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(LoginActivity.this, "Now connected", Toast.LENGTH_LONG).show();
        displayLocation();


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void displayLocation() {
        // Toast.makeText(Register.this,"getting here 123" ,Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            // Toast.makeText(LoginActivity.this,latitude + ", " + longitude,Toast.LENGTH_LONG).show();

        } else {

            //  Toast.makeText(LoginActivity.this,"(Couldn't get the location. Make sure location is enabled on the device)",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
/*
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        mGoogleApiClient.connect();
        displayLocation();*/
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


            boolean error = false;
            if (email.length() < 4) {
                error = true;
                mEmailView.setError("Email or username must be at least four characters");
            }
            if (password.length() < 4) {
                error = true;
                mEmailView.setError("password must be at least four characters");
            } else {
                //login
                final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                progress.setTitle("Logging in");
                progress.setMessage("Please wait while we log you in");
                progress.show();

                try {
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    final String url = Config.url + "login.php?username=" + email + "&&password=" + password;
                    JSONArray requestArray = new JSONArray();
                    JSONObject object = new JSONObject();
                    final String username = email;


                    JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {

                                    int length = response.length();
                                    if (length == 0) {
                                        //wrong username or password
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                        dialog.setTitle("Wrong username or password");
                                        dialog.setMessage("The username and password combination you entered is wrong. Please try again");

                                        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                                            }
                                        });
                                        dialog.show();
                                    }

                                    for (int a = 0; a < length; a++) {
                                        try {
                                            JSONObject object = (JSONObject) response.get(a);
                                            String id = (String) object.get("id");
                                            String Name = (String) object.get("Name");
                                            String PhoneNumber = (String) object.get("PhoneNumber");
                                            String Email = (String) object.get("Email");
                                            String Website = (String) object.get("Website");
                                            String Location = (String) object.get("Location");
                                            String Latitude = (String) object.get("Latitude");
                                            String Longitude = (String) object.get("Longitude");
                                            String Username = (String) object.get("Username");
                                            String Password = (String) object.get("Password");
                                            String RegistrationDate = (String) object.get("RegistrationDate");
                                            String Usertype = (String) object.get("Usertype");
                                            String Status = (String) object.get("Status");
                                            String insertUser = "INSERT INTO `users`(`id`,`Name`, `PhoneNumber`, `Email`, `Website`, `Location`, `Latitude`, `Longitude`,  `Username`, `Password`, `Usertype`, `RegistrationDate`, `Status`,`Logo`) VALUES" +
                                                    "(" + id + ",'" + Name + "','" + PhoneNumber + "','" + Email + "','" + Website + "','" + Location + "','" + Latitude + "','" + Longitude + "','" + Username + "','" + Password + "','" + Usertype + "','" + RegistrationDate + "', '" + Status + "','')";
                                            SQLiteDatabase sqlDb = new ZonerDB(LoginActivity.this).getWritableDatabase();
                                            sqlDb.execSQL(insertUser);
                                            int userType = Integer.parseInt(id);
                                            // if (userType==0){//business
                                            //logged in as business, take to business screen
                                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                            startActivity(intent);
                                            finish();
                                       /* }else  if(userType==1){//customers
                                            //logged in as customer, take to customer screen
                                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class)  ;
                                            startActivity(intent);
                                            finish();
                                        }*/
                                        } catch (JSONException e) {

                                        }
                                    }

                                    progress.cancel();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(LoginActivity.this, "We encountered a problem while processing your request. Please try again", Toast.LENGTH_LONG).show();
                                    progress.cancel();
                                }
                            }
                    ) {


                    };
                    queue.add(postRequest);
                } catch (Exception e) {
                    progress.cancel();
                    Toast.makeText(LoginActivity.this, "We encountered a problem while processing your request. Please try again", Toast.LENGTH_LONG).show();

                }
                progress.dismiss();

            }


    }


    public void checkAccountStatus(final String tBusinessUserName) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Confirming");
        progressDialog.setMessage("Checking Payment status...");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "ConfirmPayment.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.cancel();
                System.out.print(response);

                Log.wtf("IN CHECK Account STATUS **************** ", response);

                if(response.equalsIgnoreCase("EXPIRED")){
                    //wrong username or password
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                    dialog.setTitle("Subscription Expired");
                    dialog.setMessage("Please proceed to make a payment of 100 KES to  \n Till No: 823874 \n to renew your Subscription");
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    dialog.show();
                }else{
                    attemptLogin();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("Username", tBusinessUserName);
                return param;
            }
        };


        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        requestQueue.add(stringRequest);

    }

    public void forgotPasswordClick(View v) {
        Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
        startActivity(intent);
    }

    public void registerClick(View v) {
        Intent intent = new Intent(LoginActivity.this, Register.class);
        startActivity(intent);
    }

}

