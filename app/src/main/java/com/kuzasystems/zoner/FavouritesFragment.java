package com.kuzasystems.zoner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 30-Sep-18.
 */

public class FavouritesFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
    //android.support.v4.widget.SwipeRefreshLayout swiperefresh = null;
    ListView allBusinesses = null;
    TextView noElement = null;
        public FavouritesFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FavouritesFragment newInstance(int sectionNumber) {
            FavouritesFragment fragment = new FavouritesFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.businesslist, container, false);
            /*swiperefresh = (android.support.v4.widget.SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
            swiperefresh.setRefreshing(true);*/
            allBusinesses = (ListView) rootView.findViewById(R.id.allBusinesses);
//            noElement = (TextView) rootView.findViewById(R.id.noElement);




            /*swiperefresh.setOnRefreshListener(
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
        String username= "";
        String userPassword= "";
        try {
            SQLiteDatabase sqlDb = new ZonerDB(getActivity()).getWritableDatabase();
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
        //swiperefresh.setRefreshing(true);
        try {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            Log.wtf("**** FEVORITE ***** " , " FEVO " + username + "  pass " + userPassword);
            final String url = Config.url + "getFavourites.php?username="+username+"&password="+userPassword;
            JSONArray requestArray = new JSONArray();
            JSONObject object = new JSONObject();


            JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            Log.wtf("**** FEVORITE ***** " , " FEVO " + response.toString());

                            int length = response.length();
                            if(length==0) {
                               // noElement.setText("You have not added any business as a favourite");
                            }else {
                                final List<Users> myBusinesses = new ArrayList<>();
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

                                        Users user = new Users(Integer.parseInt(id), Name, PhoneNumber, Email, Website,
                                                Location, Double.parseDouble(Latitude), Double.parseDouble(Longitude),
                                                logo, Username, Password,Integer.parseInt(Usertype), null,
                                               Integer.parseInt(Status),BusinessStatus);
                                        myBusinesses.add(user);


                                    } catch (JSONException e) {

                                    }
                                }


                                BusinessAdapter adapter = new BusinessAdapter(getActivity(), myBusinesses);
                                allBusinesses.setAdapter(adapter);
                                allBusinesses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Users user = myBusinesses.get(position);
                                        Intent intent = new Intent(getActivity(), SingleBusiness.class);
                                        Bundle b = new Bundle();
                                        b.putString("businessId", user.getId() + "");
                                        b.putString("businessName", user.getName());
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                               // noElement.setText("");
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


       // swiperefresh.setRefreshing(false);
    }
    }


