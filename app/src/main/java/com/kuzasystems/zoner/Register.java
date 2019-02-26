package com.kuzasystems.zoner;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Register extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
Switch viewSwitch;
    ViewSwitcher viewSwitcher;
    TextView heading;
    Button chooseLogo;
    EditText businessName,businessPhone,businessEmail,businessWebsite, businessLocation,businessUserName,businessPassword,businessConfirmPassword,
            individualName, individualPhone,individualEmail,individualUsername,individualPassword,individualConfirmPassword;
    /* EditText businessName,businessPhone,businessEmail,businessWebsite, businessLocation,businessUserName,businessPassword,businessConfirmPassword,
            individualName, individualPhone,individualEmail,individualUsername,individualPassword,individualConfirmPassword;*/
    private static final int REQUEST_LOCATIONS = 1;
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private Uri selectedImage = null;
    private Bitmap bitmap, bitmapRotate;
    private ProgressDialog pDialog;
    String imagepath = "";
    ImageButton businessLogo;
    String fname;
    File file;
    private Boolean upflag = false;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    double latitude, longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        viewSwitch = (Switch) findViewById(R.id.viewSwitch);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        heading = (TextView) findViewById(R.id.heading);
        businessName =(EditText) findViewById(R.id.businessName);
        businessPhone=(EditText) findViewById(R.id.businessPhone);
        businessEmail=(EditText) findViewById(R.id.businessEmail);
        businessWebsite=(EditText) findViewById(R.id.businessWebsite);
        businessLocation=(EditText) findViewById(R.id.businessLocation);
        businessUserName=(EditText) findViewById(R.id.businessUserName);
        businessPassword=(EditText) findViewById(R.id.businessPassword);
        businessConfirmPassword=(EditText) findViewById(R.id.businessConfirmPassword);
        individualName=(EditText) findViewById(R.id.individualName);
        individualPhone=(EditText) findViewById(R.id.individualPhone);
        individualEmail=(EditText) findViewById(R.id.individualEmail);
        individualUsername=(EditText) findViewById(R.id.individualUsername);
        individualPassword=(EditText) findViewById(R.id.individualPassword);
        individualConfirmPassword=(EditText) findViewById(R.id.individualConfirmPassword);
        chooseLogo = (Button) findViewById(R.id.chooseLogo) ;
        businessLogo =(ImageButton)findViewById(R.id.businessLogo);

        chooseLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder= new AlertDialog.Builder(Register.this);
                //   }
                builder//.setTitle("Delete entry")
                        //.setView(R.layout.logochoices)
                        .setMessage("From where would you like to get the logo?")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent cameraintent = new Intent(
                                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraintent, 101);

                            }
                        })
                       /* .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                // Show only images, no videos or anything else
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                // Always show the chooser (if there are multiple options available)
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 102);
                            }
                        })*/
                        .setIcon(R.drawable.camerablack)
                        .show();
            }
        });
        businessLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder= new AlertDialog.Builder(Register.this);
                //   }
                builder//.setTitle("Delete entry")
                        //.setView(R.layout.logochoices)
                        .setMessage("From where would you like to get the logo?")
                        .setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"),102);

                            }
                        })
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent cameraintent = new Intent(
                                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraintent, 101);

                            }
                        })
                       /* .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                // Show only images, no videos or anything else
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                // Always show the chooser (if there are multiple options available)
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 102);
                            }
                        })*/
                        .setIcon(R.drawable.camerablack)
                        .show();
            }
        });

        verifyLocationPermissions(Register.this);
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        mGoogleApiClient.connect();
        displayLocation();
        viewSwitch.setChecked(false);
        viewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    viewSwitcher.showPrevious();
                    heading.setText("INDIVIDUAL REGISTRATION");
                    clear();


                }else{
                    viewSwitcher.showNext();
                    heading.setText("BUSINESS REGISTRATION");
                    clear();

                }
            }
        });
    }
    public void clear(){
        businessName.setText("");
        businessPhone.setText("");
        businessEmail.setText("");
        businessWebsite.setText("");
        businessLocation.setText("");
        businessUserName.setText("");
        businessPassword.setText("");
        businessConfirmPassword.setText("");
        individualName.setText("");
        individualPhone.setText("");
        individualEmail.setText("");
        individualUsername.setText("");
        individualPassword.setText("");
        individualConfirmPassword.setText("");
    }
    public void registerIndividual(View v){
//register individual
        final String tIndividualName = individualName.getText().toString().trim();
        final String tIndividualPhone = individualPhone.getText().toString().trim();
        final String tIndividualEmail = individualEmail.getText().toString().trim();
        final String tIndividualUsername = individualUsername.getText().toString().trim();
        final String tIndividualPassword = individualPassword.getText().toString().trim();
        final String tIndividualConfirmPassword = individualConfirmPassword.getText().toString().trim();
        //validate
        Boolean error = false;
        if(tIndividualName.length()<4){
            individualName.setError("Please specify a valid name. More than 4 characters");
            individualName.requestFocus();
            error = true;
        }
        if(tIndividualUsername.length()<4){
            individualUsername.setError("Please specify a valid username. More than 4 characters");
            if(!error) {
                individualUsername.requestFocus();
            }
            error = true;
        }
        if(tIndividualPassword.length()<4){
            individualPassword.setError("Please specify a valid password. More than 4 characters");
            if(!error) {
                individualPassword.requestFocus();
            }
            error = true;
        }
        if (!tIndividualPassword.equals(tIndividualConfirmPassword)){
            individualConfirmPassword.setError("Confirm password does not match password");
            if(!error) {
                individualConfirmPassword.requestFocus();
            }
            error = true;
        }
        if(!error){
            //register
            final ProgressDialog progressDialog = new ProgressDialog(Register.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setTitle("Creating your account");
            progressDialog.setMessage("Please wait while we create your account");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(Register.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "AddUser.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.cancel();
                     System.out.print(response);
                     android.util.Log.d("ERROR", response);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    //   }
                    builder.setTitle("Response")
                            .setMessage(response)

                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Register.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();

                  // clear();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.cancel();
                    Util.getBuilderWithOneControl(Register.this,"Check your Internet Connection");

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();

                   param.put("Name", tIndividualName);
                   param.put("PhoneNumber", tIndividualPhone);
                   param.put("Email", tIndividualEmail);
                   param.put("Website", "");
                   param.put("Location", "");
                   param.put("Latitude", "0");
                   param.put("Longitude", "0");
                   param.put("Username", tIndividualUsername);
                   param.put("Password", tIndividualPassword);
                   param.put("cPassword", tIndividualConfirmPassword);
                   param.put("Usertype", "1");


                    return param;
                }
            };

            requestQueue.add(stringRequest);
        }
    }




    public void registerBusiness(View v) {

        final String tBusinessName = businessName.getText().toString().trim();
        final String tBusinessPhone = businessPhone.getText().toString().trim();
        final String tBusinessEmail = businessEmail.getText().toString().trim();
        final String tBusinessWebsite = businessWebsite.getText().toString().trim();
        final String tBusinessLocation = businessLocation.getText().toString().trim();
        final String tBusinessUserName = businessUserName.getText().toString().trim();
        final String tBusinessPassword = businessPassword.getText().toString().trim();
        final String tBusinessConfirmPassword = businessConfirmPassword.getText().toString().trim();
        Boolean error = false;

        /*Map<String, String> param = new HashMap<>();

        String images = getStringImage(bitmapRotate);
        param.put("Name", tBusinessName);
        param.put("PhoneNumber", tBusinessPhone);
        param.put("Email", tBusinessEmail);
        param.put("Website", tBusinessWebsite);
        param.put("Location", tBusinessLocation);
        param.put("Latitude", mLastLocation.getLatitude() + "");
        param.put("Longitude", mLastLocation.getLongitude() + "");
        param.put("Username", tBusinessUserName);
        param.put("Password", tBusinessPassword);
        param.put("cPassword", tBusinessConfirmPassword);
        param.put("Usertype", "0");
        param.put("Images", images);
        System.out.print(param.toString()+"inotry");
        android.util.Log.d("ERROR", param.toString()+"inotry");
        Toast.makeText(Register.this, param.toString()+"inotry", Toast.LENGTH_LONG).show();*/

        try{
        if (bitmapRotate.equals(null)) {
           throw new NullPointerException();
        }}catch(NullPointerException e){
            Snackbar.make(v, "Please select a business logo", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            error = true;
        }
        if (tBusinessName.length() < 4) {
            businessName.setError("Please specify a valid business name. More than 4 characters");
            businessName.requestFocus();
            error = true;
        }
        if (tBusinessUserName.length() < 4) {
            businessUserName.setError("Please specify a valid username. More than 4 characters");
            if (!error) {
                businessUserName.requestFocus();
            }
            error = true;
        }
        if (tBusinessPassword.length() < 4) {
            businessPassword.setError("Please specify a valid password. More than 4 characters");
            if (!error) {
                businessPassword.requestFocus();
            }
            error = true;
        }

        if (!tBusinessPassword.equals(tBusinessConfirmPassword)) {
            businessConfirmPassword.setError("Confirm password does not match password");
            if (!error) {
                businessConfirmPassword.requestFocus();
            }
            error = true;
        }
        boolean gps_enabled = false;
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gps_enabled) {
                throw new Exception();
            }
        } catch (Exception ex) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
            dialog.setMessage("Please enable gps");
            dialog.setTitle("GPS NOT ENABLED");
            error = true;
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                }
            });
            dialog.show();
        }
        if (gps_enabled) {

            if (ActivityCompat.checkSelfPermission(Register.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Register.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                // return;
            }

            /*mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);*/

           if (mLastLocation == null) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
                dialog.setTitle("Error getting your location");
                dialog.setMessage("We could not establish your location. Please enable GPS and try again later. ");
                error = true;
                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                    }
                });
                dialog.show();
            }

            if (!error) {
//register
                final ProgressDialog progressDialog = new ProgressDialog(Register.this);
                progressDialog.setIndeterminate(false);
                progressDialog.setTitle("Creating your account");
                progressDialog.setMessage("Please wait while we create your account");
                progressDialog.show();
                RequestQueue requestQueue = Volley.newRequestQueue(Register.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "AddUser.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.cancel();
                        System.out.print(response);
                        android.util.Log.d("ERROR", response);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                        //   }
                        builder.setTitle("Response")
                                .setMessage(response)

                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Register.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .show();

                        // clear();


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel();
                        Toast.makeText(Register.this, "" + error, Toast.LENGTH_SHORT).show();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();

                        String images = getStringImage(bitmapRotate);
                        param.put("Name", tBusinessName);
                        param.put("PhoneNumber", tBusinessPhone);
                        param.put("Email", tBusinessEmail);
                        param.put("Website", tBusinessWebsite);
                        param.put("Location", tBusinessLocation);
                        param.put("Latitude", mLastLocation.getLatitude() + "");
                        param.put("Longitude", mLastLocation.getLongitude() + "");
                        param.put("Username", tBusinessUserName);
                        param.put("Password", tBusinessPassword);
                        param.put("cPassword", tBusinessConfirmPassword);
                        param.put("Usertype", "0");
                        param.put("Images", images);
                        System.out.print(param.toString()+"inotry");
                        android.util.Log.d("ERROR", param.toString()+"inotry");
                        return param;
                    }
                };


                int socketTimeout = 30000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);

                requestQueue.add(stringRequest);


                //

            }

        }

    }
    public void loginClick(View v){
        Intent intent = new Intent(Register.this, LoginActivity.class);
        startActivity(intent);
    }
    public void ForgotPasswordClick(View v){
        Intent intent = new Intent(Register.this, ForgotPassword.class);
        startActivity(intent);
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(Register.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(Register.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, Register.this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(Register.this,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();

            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
        if (ActivityCompat.checkSelfPermission(Register.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Register.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

           //   Toast.makeText(Register.this,latitude + ", " + longitude,Toast.LENGTH_LONG).show();

        } else {

            //  Toast.makeText(getActivity(),"(Couldn't get the location. Make sure location is enabled on the device)",Toast.LENGTH_LONG).show();
        }
    }
    public static void verifyLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if ((permission != PackageManager.PERMISSION_GRANTED) ||(permission1 != PackageManager.PERMISSION_GRANTED)) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATIONS
            );
        }
    }
    public void  onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case 101:
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            selectedImage = data.getData(); // the uri of the image taken
                            if (String.valueOf((Bitmap) data.getExtras().get("data")).equals("null")) {
                                bitmap = MediaStore.Images.Media.getBitmap(Register.this.getContentResolver(), selectedImage);
                            } else {
                                bitmap = (Bitmap) data.getExtras().get("data");
                            }


                            bitmapRotate = bitmap;
                            businessLogo.setImageBitmap(bitmap);

//                            Saving image to mobile internal memory for sometime
                            String root = Register.this.getApplicationContext().getFilesDir().toString();
                            File myDir = new File(root + "/zoner");
                            myDir.mkdirs();

                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);

//                            Give the file name that u want
                            fname = "null" + n + ".jpg";

                            imagepath = root + "/zoner/" + fname;
                            file = new File(myDir, fname);
                            upflag = true;
                        }
                    }
                case 102:
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            Uri uri = data.getData();

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Register.this.getContentResolver(), uri);
                                bitmapRotate = bitmap;
                                businessLogo.setImageBitmap(bitmap);
                                imagepath =uri.toString();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);


        return temp;
    }
}
