package com.kuzasystems.zoner;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SingleBusiness extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private SliderLayout mDemoSlider;
    private static final int REQUEST_LOCATIONS = 1;
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    String businessId = "";
    FloatingActionButton fab2 =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        verifyLocationPermissions(SingleBusiness.this);
        setContentView(R.layout.activity_single_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Bundle bundle = getIntent().getExtras();
        businessId = bundle.get("businessId").toString();
        setTitle(bundle.get("businessName").toString());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(SingleBusiness.this, Message.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

                fab2= (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Adding to your favourites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                favourite();
            }
        });
        try {
            SQLiteDatabase sqlDb = new ZonerDB(SingleBusiness.this).getWritableDatabase();
            Cursor cursor = sqlDb.rawQuery("select * from users", null);
            while (cursor.moveToNext()) {
                // username = cursor.getString(cursor.getColumnIndex("username")) ;
                //  password = cursor.getString(cursor.getColumnIndex("password")) ;

               if(cursor.getInt(cursor.getColumnIndex("Usertype"))==0){
                   fab2.setVisibility(View.GONE);
               }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        final HashMap<String,String> url_maps = new HashMap<String, String>();
        //try loading posts from the database
try{
        RequestQueue queue = Volley.newRequestQueue(SingleBusiness.this);
        final String url = Config.url + "getBusinessPosts.php?id="+businessId;
        JSONArray requestArray = new JSONArray();
        JSONObject object = new JSONObject();


        JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int length = response.length();

                        for (int a = 0; a < length; a++) {
                            try {
                                JSONObject object = (JSONObject) response.get(a);
                                String Image =Config.url+"posts/"+(String) object.get("Image");
                                String PostText = (String) object.get("PostText");


                                url_maps.put(PostText, Image);

                            } catch (JSONException e) {

                            }
                        }
                        for(String name : url_maps.keySet()){
                            TextSliderView textSliderView = new TextSliderView(SingleBusiness.this);
                            // initialize a SliderLayout
                            textSliderView
                                    .description(name)
                                    .image(url_maps.get(name))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(SingleBusiness.this);

                            //add your extra information
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle()
                                    .putString("extra",name);

                            mDemoSlider.addSlider(textSliderView);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getActivity(), "We encountered a problem while processing your request. Please try again", Toast.LENGTH_LONG).show();

                    }
                }
        ) {


        };
        queue.add(postRequest);
    } catch (Exception e) {
        // Toast.makeText(getActivity(), "We encountered a problem while processing your request. Please try again", Toast.LENGTH_LONG).show();

    }








        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(10000);
        mDemoSlider.addOnPageChangeListener(this);



    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
    public static void verifyLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if ((permission != PackageManager.PERMISSION_GRANTED) ||(permission1 != PackageManager.PERMISSION_GRANTED)) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATIONS
            );
        }
    }
    public void favourite(){
        RequestQueue requestQueue = Volley.newRequestQueue(SingleBusiness.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "favourite.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {




                Snackbar.make(fab2, response, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // clear();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SingleBusiness.this, "We encountered an error while adding your favourite" , Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                String username= "";
                String userPassword= "";
                try {
                    SQLiteDatabase sqlDb = new ZonerDB(SingleBusiness.this).getWritableDatabase();
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
                param.put("username", username+"");
                param.put("password", userPassword);
                param.put("id", businessId);

                return param;
            }
        };

        requestQueue.add(stringRequest);
    }

}
