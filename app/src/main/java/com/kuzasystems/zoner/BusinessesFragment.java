package com.kuzasystems.zoner;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 30-Sep-18.
 */

public class BusinessesFragment extends Fragment  {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
android.support.v4.widget.SwipeRefreshLayout swiperefresh = null;
    ListView allBusinesses = null;
    TextView noElement = null;

    static String latitude;
    static String longitude;


    public BusinessesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BusinessesFragment newInstance(String myLatitude, String myLongitude) {
        BusinessesFragment fragment = new BusinessesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        latitude = myLatitude;
        longitude = myLongitude;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.businesslist, container, false);
        /*swiperefresh = (android.support.v4.widget.SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swiperefresh.setRefreshing(true);*/
        ScrollView scrollView = rootView.findViewById(R.id.scroller);

        ObjectAnimator.ofInt(scrollView, "scrollY",  5).setDuration(5).start();

        allBusinesses = (ListView) rootView.findViewById(R.id.allBusinesses);
        noElement = (TextView) rootView.findViewById(R.id.noElement);

           /* swiperefresh.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            loadBusinesses();
                        }
                    });*/
            loadBusinesses();
            return rootView;
        }


    public void loadBusinesses() {
           //swiperefresh.setRefreshing(true);
            try {
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                final String url = Config.url + "loadBusinesses.php?latitude="+latitude+"&&longitude="+longitude;
                //Toast.makeText(getActivity(), latitude+longitude,Toast.LENGTH_LONG).show();
                JSONArray requestArray = new JSONArray();
                JSONObject object = new JSONObject();


                JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                SQLiteDatabase sqlDb = new ZonerDB(getActivity()).getWritableDatabase();
                                //check if exists
                                String delete = "delete from businesses";
                                sqlDb.execSQL(delete);
                                int length = response.length();

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
                                        String Password = "";//(String)object.get("Password");
                                        String RegistrationDate = (String) object.get("RegistrationDate");
                                        String Usertype = (String) object.get("Usertype");
                                        String Status = (String) object.get("Status");
                                        String logo = (String) object.get("Logo");
                                        String BusinessStatus = (String) object.get("BusinessStatus");
                                        String insertUser = "INSERT INTO `businesses`(`id`,`Name`, `PhoneNumber`, `Email`, `Website`, `Location`, `Latitude`, `Longitude`,  `Username`, `Password`, `Usertype`, `RegistrationDate`, `Status`,`Logo`,`BusinessStatus`) VALUES" +
                                                "(" + id + ",'" + Name + "','" + PhoneNumber + "','" + Email + "','" + Website + "','" + Location + "','" + Latitude + "','" + Longitude + "','" + Username + "','" + Password + "','" + Usertype + "','" + RegistrationDate + "', '" + Status + "','"+logo+"','"+BusinessStatus+"')";


                                        sqlDb.execSQL(insertUser);

                                    } catch (JSONException e) {

                                    }
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
            //load from the local database
            String load = "select * from businesses where not id = "+Config.getMyUserId(getActivity());
            SQLiteDatabase sqlDb = new ZonerDB(getActivity()).getWritableDatabase();
            try {
                Cursor cursor = sqlDb.rawQuery(load, null);
                Boolean hasValues = false;
                final List<Users> myBusinesses = new ArrayList<>();
                while (cursor.moveToNext()) {
                    hasValues = true;
                    Users user = new Users(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("Name")), cursor.getString(cursor.getColumnIndex("PhoneNumber")), cursor.getString(cursor.getColumnIndex("Email")), cursor.getString(cursor.getColumnIndex("Website")),
                            cursor.getString(cursor.getColumnIndex("Location")), cursor.getDouble(cursor.getColumnIndex("Latitude")), cursor.getDouble(cursor.getColumnIndex("Longitude")),
                            cursor.getString(cursor.getColumnIndex("Logo")), cursor.getString(cursor.getColumnIndex("Username")), cursor.getString(cursor.getColumnIndex("Password")), cursor.getInt(cursor.getColumnIndex("Usertype")), new Date(cursor.getLong(cursor.getColumnIndex("RegistrationDate"))),
                            cursor.getInt(cursor.getColumnIndex("Status")),cursor.getString(cursor.getColumnIndex("BusinessStatus")) );
                    myBusinesses.add(user);
                }

                if (!hasValues) {
                    //no businesses were found
                    noElement.setText("No businesses were found near you");
                } else {
                    BusinessAdapter adapter = new BusinessAdapter(getActivity(), myBusinesses);
                    allBusinesses.setAdapter(adapter);
                    allBusinesses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Users user = myBusinesses.get(position);
                            Intent intent = new Intent(getActivity(), SingleBusiness.class);
                            Bundle b = new Bundle();
                            b.putString("businessId", user.getId()+"");
                            b.putString("businessName", user.getName());
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });
                    noElement.setText("");

                }
            } catch (Exception t) {

            }

           // swiperefresh.setRefreshing(false);
        }
    }


