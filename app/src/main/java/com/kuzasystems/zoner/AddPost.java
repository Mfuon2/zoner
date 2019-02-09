package com.kuzasystems.zoner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddPost extends AppCompatActivity {
Button chooseImage;
    private Uri selectedImage = null;
    private Bitmap bitmap, bitmapRotate;
    private ProgressDialog pDialog;
    String imagepath = "";
    ImageView PostImage;
    String fname;
    File file;
    private Boolean upflag = false;
    EditText postText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        chooseImage = (Button) findViewById(R.id.chooseImage);
        PostImage =(ImageView)findViewById(R.id.PostImage);
        postText = (EditText)findViewById(R.id.postText);
        PostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder= new AlertDialog.Builder(AddPost.this);
                //   }
                builder//.setTitle("Delete entry")
                        //.setView(R.layout.logochoices)
                        .setMessage("From where would you like to get the post image?")
                        .setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /*Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                startActivityForResult(cameraintent, 101);*/
                                //Toast.makeText(AddPost.this,"YOLO", Toast.LENGTH_LONG).show();
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
                        /*.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
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
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder= new AlertDialog.Builder(AddPost.this);
                //   }
                builder//.setTitle("Delete entry")
                        //.setView(R.layout.logochoices)
                        .setMessage("From where would you like to get the post image?")
                        .setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /*Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                startActivityForResult(cameraintent, 101);*/
                                //Toast.makeText(AddPost.this,"YOLO", Toast.LENGTH_LONG).show();
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
                        /*.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
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
    }
    public void  onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case 101:
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            selectedImage = data.getData(); // the uri of the image taken

                            if (String.valueOf((Bitmap) data.getExtras().get("data")).equals("null")) {
                                bitmap = MediaStore.Images.Media.getBitmap(AddPost.this.getContentResolver(), selectedImage);
                            } else {
                                bitmap = (Bitmap) data.getExtras().get("data");
                            }

                            Log.w("HERE ********** ", "****** URI ****** : " + bitmap.toString() );

                            bitmapRotate = bitmap;
                            PostImage.setImageBitmap(bitmap);

//                            Saving image to mobile internal memory for sometime
                            String root = AddPost.this.getApplicationContext().getFilesDir().toString();
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
                            Log.w("HERE ********** ", "****** URI ****** : " + uri.toString() );

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddPost.this.getContentResolver(), uri);
                                bitmapRotate = bitmap;
                                PostImage.setImageBitmap(bitmap);
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
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);


        return temp;
    }
    public void addPost(View v){
       try{
        final String tPostText = postText.getText().toString().trim();
        if(tPostText.equals("")) {
            postText.setError("Please add some post text");
        }
        else if (bitmapRotate.equals(null)) {
            Snackbar.make(v, "Please select a business logo", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(AddPost.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setTitle("Adding your post");
            progressDialog.setMessage("Please wait while we add your post");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(AddPost.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "addPost.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.cancel();
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPost.this);
                    //   }
                    builder.setTitle("Response")
                            .setMessage(response)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(AddPost.this, SplashActivity.class);
                                    startActivity(intent);
                                }
                            })

                            .show();
                    postText.setText("");


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.cancel();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    String username= "";
                    String userPassword = "";
                    String images = getStringImage(bitmapRotate);
                    try {
                        SQLiteDatabase sqlDb = new ZonerDB(AddPost.this).getWritableDatabase();
                        Cursor cursor = sqlDb.rawQuery("select * from users", null);
                        while (cursor.moveToNext()) {
                            username = cursor.getString(cursor.getColumnIndex("Username")) ;
                            userPassword = cursor.getString(cursor.getColumnIndex("Password")) ;
                            // Toast.makeText(MyIntentService.this, username, Toast.LENGTH_LONG).show();

                        }
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    param.put("username", username);
                    param.put("password", userPassword);
                    param.put("postText", tPostText);
                    param.put("image", images);

                    return param;
                }
            };
            requestQueue.add(stringRequest);
        }
    } catch (NullPointerException t) {
        Toast.makeText(AddPost.this, "Please select a business logo", Toast.LENGTH_SHORT).show();
    }
    }
}
