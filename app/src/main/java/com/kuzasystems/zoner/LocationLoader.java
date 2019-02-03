package com.kuzasystems.zoner;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationLoader extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_LOCATIONS = 1;
    private static final String TAG = "Connect Log";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Button retry;
    LocationManager locationManager;
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    public static void verifyLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if ((permission != PackageManager.PERMISSION_GRANTED) || (permission1 != PackageManager.PERMISSION_GRANTED) || (permission2 != PackageManager.PERMISSION_GRANTED) || (permission3 != PackageManager.PERMISSION_GRANTED)) {
            // We don't have permission so prompt the user

            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATIONS
            );

        }
    }

    public static boolean hasLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if ((permission != PackageManager.PERMISSION_GRANTED) || (permission1 != PackageManager.PERMISSION_GRANTED) || (permission2 != PackageManager.PERMISSION_GRANTED) || (permission3 != PackageManager.PERMISSION_GRANTED)) {
            // We don't have permission so prompt the user
            return false;

        } else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_loader);
        verifyLocationPermissions(LocationLoader.this);
        retry = (Button) findViewById(R.id.retry);
        if (hasLocationPermissions(LocationLoader.this)) {
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

                            if (location != null) {
                                // Logic to handle location object

                                Toast.makeText(LocationLoader.this, "Success " + location.getLatitude(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LocationLoader.this, "Can't get location ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            startLocationUpdates();
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            }

            ;
        };

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        }

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void reload(View view) {
        Intent intent = new Intent(LocationLoader.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int permission = ActivityCompat.checkSelfPermission(LocationLoader.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(LocationLoader.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(LocationLoader.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission3 = ActivityCompat.checkSelfPermission(LocationLoader.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if ((permission == PackageManager.PERMISSION_GRANTED) && (permission1 == PackageManager.PERMISSION_GRANTED) && (permission2 == PackageManager.PERMISSION_GRANTED) && (permission3 == PackageManager.PERMISSION_GRANTED)) {
            // We don't have permission so prompt the user
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(LocationLoader.this, SplashActivity.class);
                startActivity(intent);
                finish();
            } else {
                showSettingsAlert();
            }


        }

        //Toast.makeText(LocationLoader.this,"Permissions granted",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        int permission = ActivityCompat.checkSelfPermission(LocationLoader.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(LocationLoader.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(LocationLoader.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission3 = ActivityCompat.checkSelfPermission(LocationLoader.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!((permission != PackageManager.PERMISSION_GRANTED) || (permission1 != PackageManager.PERMISSION_GRANTED) || (permission2 != PackageManager.PERMISSION_GRANTED) || (permission3 != PackageManager.PERMISSION_GRANTED))) {
            Intent intent = new Intent(LocationLoader.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(100)        // 10 seconds, in milliseconds
                        .setFastestInterval(100),
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {

                        }
                    }

                    ;

                }, null);
    }
}
