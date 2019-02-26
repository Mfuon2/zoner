package com.kuzasystems.zoner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class BusinessHomeActivity extends AppCompatActivity {

    String latitude = "";
    String longitude = "";
    Snackbar snackStatus;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        Bundle bundle = getIntent().getExtras();
        latitude = bundle.get("latitude").toString();
        longitude = bundle.get("longitude").toString();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab_refresh = findViewById(R.id.refresh_business);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusinessHomeActivity.this, AddPost.class);
                startActivity(intent);
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                startActivity(intent);
            }
        });


        snackStatus = Snackbar.make(tabLayout, "getting status", Snackbar.LENGTH_INDEFINITE);
        snackStatus.show();
        Thread thread = new Thread() {
            public void run() {
                try {
                    SQLiteDatabase sqlDb = new ZonerDB(BusinessHomeActivity.this).getWritableDatabase();
                    Cursor cursor = sqlDb.rawQuery("select * from users", null);
                    while (cursor.moveToNext()) {
                        // username = cursor.getString(cursor.getColumnIndex("username")) ;
                        //  password = cursor.getString(cursor.getColumnIndex("password")) ;
                        ;
                        setTitle(cursor.getString(cursor.getColumnIndex("Name")));
                    }
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                loadStatus();
            }
        };
        thread.start();

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            try {
                SQLiteDatabase sqlDb = new ZonerDB(BusinessHomeActivity.this).getWritableDatabase();
                sqlDb.execSQL("delete from users");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.changeStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BusinessHomeActivity.this);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(BusinessHomeActivity.this, android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Open");
            arrayAdapter.add("Closed");
            arrayAdapter.add("Vacant");
            arrayAdapter.add("Occupied");
            builder
                    .setTitle("Change Business status")

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, final int which) {
                    final String strName = arrayAdapter.getItem(which);
                    final ProgressDialog progress = new ProgressDialog(BusinessHomeActivity.this);
                    String myUsername = "";
                    String myPassword = "";
                    try {
                        SQLiteDatabase sqlDb = new ZonerDB(BusinessHomeActivity.this).getWritableDatabase();
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

                    progress.setTitle("Updating status");
                    progress.setMessage("Please wait while we update your status");
                    progress.show();


                    RequestQueue requestQueue = Volley.newRequestQueue(BusinessHomeActivity.this);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "updateBusinessStatus.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            progress.cancel();
                            Toast.makeText(BusinessHomeActivity.this, response, Toast.LENGTH_SHORT).show();
                            System.out.print(response);
                            android.util.Log.d("ERROR", response);
                            snackStatus.setText(response);


                            // clear();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.cancel();
                            Toast.makeText(BusinessHomeActivity.this, "We encountered an error. Please try again later", Toast.LENGTH_SHORT).show();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> param = new HashMap<>();

                            param.put("username", username);
                            param.put("password", password);
                            param.put("status", strName);

                            return param;
                        }
                    };

                    requestQueue.add(stringRequest);
                }
            });
            // Create the AlertDialog object and return it
            builder.create().show();
            return true;
        }

        if (id == R.id.logout) {
            try {
                SQLiteDatabase sqlDb = new ZonerDB(BusinessHomeActivity.this).getWritableDatabase();
                sqlDb.execSQL("delete from users");
                sqlDb.execSQL("delete from Messages");

            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(BusinessHomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_settings) {

            Intent intent = new Intent(BusinessHomeActivity.this, Settings.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    public void loadStatus() {

        String myUsername = "";
        String myPassword = "";
        try {
            SQLiteDatabase sqlDb = new ZonerDB(BusinessHomeActivity.this).getWritableDatabase();
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


        RequestQueue requestQueue = Volley.newRequestQueue(BusinessHomeActivity.this);
        final View snackBarView = snackStatus.getView();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "getBusinessStatus.php", new Response.Listener<String>() {


            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(String response) {
                /*
                snackBarView.setBackgroundColor(Color.GREEN);*/
                snackStatus.setText(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                snackBarView.setBackgroundColor(Color.RED);
                snackStatus.setText("Error getting status");
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MyPostsFragment.newInstance(1);
            } else if (position == 1) {
                return BusinessesFragment.newInstance(latitude, longitude);
            } else if (position == 2) {
                return MessagesFragment.newInstance(position + 1);
            }
            return BusinessesFragment.newInstance(latitude, longitude);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MY POSTS";
                case 1:
                    return "BUSINESSES";
                case 2:
                    return "MESSAGING";
            }
            return null;
        }
    }
}
