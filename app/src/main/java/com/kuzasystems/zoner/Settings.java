package com.kuzasystems.zoner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {
    EditText currentPassword, newpassword, confirmpassword, name;
    Button resetPassword, updateName, deleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        currentPassword = (EditText) findViewById(R.id.currentPassword);
        newpassword = (EditText) findViewById(R.id.newpassword);
        confirmpassword = (EditText) findViewById(R.id.confirmpassword);
        name = (EditText) findViewById(R.id.name);

        resetPassword = (Button) findViewById(R.id.resetPassword);
        updateName = (Button) findViewById(R.id.updateName);
        deleteAccount = (Button) findViewById(R.id.deleteAccount);
        try {
            SQLiteDatabase sqlDb = new ZonerDB(Settings.this).getWritableDatabase();
            Cursor cursor = sqlDb.rawQuery("select * from users", null);
            while (cursor.moveToNext()) {
                name.setText(cursor.getString(cursor.getColumnIndex("Name")));
                //loggedIn = true;

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetPass(View v) {
        final String tCurrentPass = currentPassword.getText().toString().trim();
        final String tNewPassword = newpassword.getText().toString().trim();
        final String tConfirm = confirmpassword.getText().toString().trim();
        boolean error = false;
        if (!tNewPassword.equals(tConfirm)) {
            confirmpassword.setError("Password does not match confirm password");
            error = true;
        }
        if (tNewPassword.length() < 4) {
            newpassword.setError("Password must be at least 4 characters");
            error = true;
        }
        if (tCurrentPass.length() < 4) {
            currentPassword.setError("Password must be at least 4 characters");
            error = true;
        }
        if (!error) {
            final ProgressDialog progress = new ProgressDialog(Settings.this);
            progress.setTitle("Changing password");
            progress.setMessage("Please wait while we change your password.");
            progress.show();
            String myUsername = "";
            String myPassword = "";
            try {
                SQLiteDatabase sqlDb = new ZonerDB(Settings.this).getWritableDatabase();
                Cursor cursor = sqlDb.rawQuery("select * from users", null);
                while (cursor.moveToNext()) {
                    myUsername = cursor.getString(cursor.getColumnIndex("Username"));
                    myPassword = cursor.getString(cursor.getColumnIndex("Password"));
                    //loggedIn = true;
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final String username = myUsername;
            final String password = myPassword;


            RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "updatepassword.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Toast.makeText(Settings.this, response, Toast.LENGTH_LONG).show();
                    progress.hide();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Settings.this, "Error getting status", Toast.LENGTH_LONG).show();
                    progress.hide();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();

                    param.put("username", username);
                    param.put("password", password);
                    param.put("currentPassword", tCurrentPass);
                    param.put("newPassword", tNewPassword);
                    param.put("confirmPassword", tConfirm);

                    return param;
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    public void updateName(View v) {
        final String tName = name.getText().toString().trim();

        boolean error = false;

        if (tName.length() < 4) {
            name.setError("Name must be at least 4 characters");
            error = true;
        }
        if (!error) {
            final ProgressDialog progress = new ProgressDialog(Settings.this);
            progress.setTitle("Changing name");
            progress.setMessage("Please wait while we change your name.");
            progress.show();
            String myUsername = "";
            String myPassword = "";
            try {
                SQLiteDatabase sqlDb = new ZonerDB(Settings.this).getWritableDatabase();
                Cursor cursor = sqlDb.rawQuery("select * from users", null);
                while (cursor.moveToNext()) {
                    myUsername = cursor.getString(cursor.getColumnIndex("Username"));
                    myPassword = cursor.getString(cursor.getColumnIndex("Password"));
                    //loggedIn = true;

                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final String username = myUsername;
            final String password = myPassword;


            RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "updatename.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Toast.makeText(Settings.this, "Username Update was successful", Toast.LENGTH_LONG).show();
                    progress.hide();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Settings.this, "Error getting status", Toast.LENGTH_LONG).show();
                    progress.hide();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();

                    param.put("username", username);
                    param.put("password", password);
                    param.put("name", tName);

                    return param;
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    public void deleteAccount(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        //   }
        builder//.setTitle("Delete entry")
                //.setView(R.layout.logochoices)
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progress = new ProgressDialog(Settings.this);
                        progress.setTitle("Deleting account");
                        progress.setMessage("Please wait while we delete your account.");
                        progress.show();
                        String myUsername = "";
                        String myPassword = "";
                        try {
                            SQLiteDatabase sqlDb = new ZonerDB(Settings.this).getWritableDatabase();
                            Cursor cursor = sqlDb.rawQuery("select * from users", null);
                            while (cursor.moveToNext()) {
                                myUsername = cursor.getString(cursor.getColumnIndex("Username"));
                                myPassword = cursor.getString(cursor.getColumnIndex("Password"));
                                //loggedIn = true;

                            }
                            cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final String username = myUsername;
                        final String password = myPassword;


                        RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "deleteaccount.php", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("deleted")) {
                                    try {
                                        SQLiteDatabase sqlDb = new ZonerDB(Settings.this).getWritableDatabase();
                                        sqlDb.execSQL("delete from users");
                                        sqlDb.execSQL("delete from Messages");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(Settings.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Settings.this, response, Toast.LENGTH_LONG).show();
                                }

                                progress.hide();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(Settings.this, "Error getting status", Toast.LENGTH_LONG).show();
                                progress.hide();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> param = new HashMap<>();

                                param.put("username", username);
                                param.put("password", password);


                                return param;
                            }
                        };

                        requestQueue.add(stringRequest);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                               /* Intent intent = new Intent();
                                // Show only images, no videos or anything else
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                // Always show the chooser (if there are multiple options available)
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 102);*/

            }
        }).show();
    }

}
