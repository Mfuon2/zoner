package com.kuzasystems.zoner;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends AppCompatActivity {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int REQUEST_LOCATIONS = 1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static Location mLastLocation;
    public boolean type = false;
    double latitude, longitude;
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Method to verify google play services on the device
     */


    public static boolean verifyLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return (permission == PackageManager.PERMISSION_GRANTED) && (permission1 == PackageManager.PERMISSION_GRANTED) && (permission2 == PackageManager.PERMISSION_GRANTED) && (permission3 == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Intent intent = new Intent(SplashActivity.this, SyncService.class);
        // startService(intent);
        if (verifyLocationPermissions(SplashActivity.this)) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return;
            }
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            //  Toast.makeText(LocationLoader.this, "Success ",Toast.LENGTH_LONG).show();
                            if (location != null) {
                                // Logic to handle location object
                                //Toast.makeText(LocationLoader.this, "Success "+location.getLatitude(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        MyIntentService is = new MyIntentService();
        MyIntentService.startActionFoo(SplashActivity.this, "", "");
//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception t) {

                } finally {
                    //check location permissions
                    if (!verifyLocationPermissions(SplashActivity.this)) {
                        //Toast.makeText(SplashActivity.this, "No location permission", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SplashActivity.this, LocationLoader.class);
                        startActivity(intent);

                    } else {
                        boolean gps_enabled = false;
                        boolean error = false;
                        LocationManager lm = (LocationManager) SplashActivity.this.getSystemService(Context.LOCATION_SERVICE);

                        try {
                            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            if (!gps_enabled) {

                                throw new Exception();
                            }
                        } catch (Exception ex) {
                            //Toast.makeText(SplashActivity.this, "GPS not enabled", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SplashActivity.this, LocationLoader.class);
                            startActivity(intent);
                            error = true;
                        }
                        if (gps_enabled) {

                            if (ActivityCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                //   Toast.makeText(SplashActivity.this, "No location permission", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SplashActivity.this, LocationLoader.class);
                                startActivity(intent);
                                error = true;
                            }


                        }

                        if (mLastLocation == null) {
                            //  Toast.makeText(SplashActivity.this, "Can't get last location", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SplashActivity.this, LocationLoader.class);
                            startActivity(intent);
                            error = true;
                            finish();

                        }
                        //   Toast.makeText(SplashActivity.this, mLastLocation.getLatitude()+" as the latitude", Toast.LENGTH_LONG).show();
                        if (!error) {
                            //check if logged in
                            Boolean loggedIn = false;
                            String username = null;
                            int userType = 0;
                            try {
                                SQLiteDatabase sqlDb = new ZonerDB(SplashActivity.this).getWritableDatabase();
                                Cursor cursor = sqlDb.rawQuery("select * from users", null);
                                while (cursor.moveToNext()) {
                                    // username = cursor.getString(cursor.getColumnIndex("username")) ;
                                    //  password = cursor.getString(cursor.getColumnIndex("password")) ;
                                    loggedIn = true;
                                    userType = cursor.getInt(cursor.getColumnIndex("Usertype"));
                                    username = cursor.getString(cursor.getColumnIndex("Username"));

                                }
                                cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (loggedIn) {

                                if (userType == 0) {//business
                                    //logged in as business, take to business screen
                                    Intent intent = new Intent(SplashActivity.this, BusinessHomeActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("latitude", mLastLocation.getLatitude() + "");
                                    bundle.putString("longitude", mLastLocation.getLongitude() + "");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (userType == 1) {//customers
                                    //logged in as customer, take to customer screen
                                    Intent intent = new Intent(SplashActivity.this, CustomerHomeActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("latitude", mLastLocation.getLatitude() + "");
                                    bundle.putString("longitude", mLastLocation.getLongitude() + "");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            //not logged in, take to login screen
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }

        };
        thread.start();
        //try getting my location
        if (verifyLocationPermissions(SplashActivity.this)) {


            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            //  Toast.makeText(LocationLoader.this, "Success ",Toast.LENGTH_LONG).show();
                            if (location != null) {
                                mLastLocation = location;
                            }
                        }
                    });

        }
        try {
            Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
        } catch (Exception e) {
            // logger.log(Log.ERROR, e, e.getMessage());
        }
    }

    public void onPause() {
        super.onPause();
        finish();
    }

    public void onResume() {
        super.onResume();
        if (verifyLocationPermissions(SplashActivity.this)) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                // return;
            }
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            //  Toast.makeText(LocationLoader.this, "Success ",Toast.LENGTH_LONG).show();
                            if (location != null) {
                                mLastLocation = location;
                            }
                        }
                    });
        }
    }
}
