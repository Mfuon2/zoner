package com.kuzasystems.zoner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by victor on 30-Sep-18.
 */

public class MyPostsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

    ListView myposts = null;
    TextView noPost = null;
        public MyPostsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MyPostsFragment newInstance(int sectionNumber) {
            MyPostsFragment fragment = new MyPostsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.myposts, container, false);
            myposts = (ListView) rootView.findViewById(R.id.myposts);
            registerForContextMenu(myposts);
            noPost = (TextView) rootView.findViewById(R.id.noPost);
            loadBusinesses();
            return rootView;
        }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.myposts) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Post obj = (Post) lv.getItemAtPosition(acmi.position);
            //Toast.makeText(getActivity(), obj.getPostText(), Toast.LENGTH_LONG).show();

            menu.add(obj.getId(),obj.getId(),obj.getId(), "Delete");
        }
    }
    @Override
    public boolean onContextItemSelected(final MenuItem menuItem){
        super.onContextItemSelected(menuItem);
       // Toast.makeText(getActivity(), menuItem.getTitle(), Toast.LENGTH_LONG).show();
        if(menuItem.getTitle().equals("Delete")){
            AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
            //   }
            builder//.setTitle("Delete entry")
                    //.setView(R.layout.logochoices)
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            final ProgressDialog progress = new ProgressDialog(getActivity());
                            progress.setTitle("Deleting your post");
                            progress.setMessage("Please wait while we delete your post");
                            progress.show();
                            String myUsername= "";
                            String myPassword = "";
                            try {
                                SQLiteDatabase sqlDb = new ZonerDB(getActivity()).getWritableDatabase();
                                Cursor cursor = sqlDb.rawQuery("select * from users", null);
                                while (cursor.moveToNext()) {
                                    myUsername = cursor.getString(cursor.getColumnIndex("Username")) ;
                                    myPassword = cursor.getString(cursor.getColumnIndex("Password")) ;
                                    //loggedIn = true;

                                }
                                cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            final String username=myUsername;
                            final String password = myPassword;



                            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "deletepost.php", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progress.hide();
                                    loadBusinesses();

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(), "We could not delete your post. Please try again later", Toast.LENGTH_LONG).show();
                                    progress.hide();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> param = new HashMap<>();

                                    param.put("username", username);
                                    param.put("password", password);
                                    param.put("postId", menuItem.getItemId()+"");

                                    return param;
                                }
                            };

                            requestQueue.add(stringRequest);
                        //delete post
                           // menuItem.
                        }
                    })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               /* Intent intent = new Intent();
                                // Show only images, no videos or anything else
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                // Always show the chooser (if there are multiple options available)
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 102);*/

                            }
                        })

                    .show();
        }
        return true;
    }
    public void loadBusinesses() {
        String username= "";
        String userPassword= "";
        int userId = 0;
        try {
            SQLiteDatabase sqlDb = new ZonerDB(getActivity()).getWritableDatabase();
            Cursor cursor = sqlDb.rawQuery("select * from users", null);
            while (cursor.moveToNext()) {
                username = cursor.getString(cursor.getColumnIndex("Username")) ;
                userPassword = cursor.getString(cursor.getColumnIndex("Password")) ;
                userId = cursor.getInt(cursor.getColumnIndex("id")) ;

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            final String url = Config.url + "getBusinessPosts.php?id="+userId;
            JSONArray requestArray = new JSONArray();
            JSONObject object = new JSONObject();


            JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            int length = response.length();
                            if(length==0){
                                noPost.setVisibility(View.VISIBLE);
                            }else{
                                noPost.setVisibility(View.INVISIBLE);
                            }
                            List<Post> allPosts = new ArrayList<>();
                            for (int a = 0; a < length; a++) {
                                try {
                                    JSONObject object = (JSONObject) response.get(a);
                                    String Image =Config.url+"posts/"+(String) object.get("Image");
                                    String PostText = (String) object.get("PostText");
                                    String AddedOn = (String) object.get("AddedOn");
                                    String Status = (String) object.get("Status");
                                    String Id = (String) object.get("Id");
                                    String User = (String) object.get("User");

                Post post = new Post(Integer.parseInt(Id), Integer.parseInt(User), Image, PostText, AddedOn, Integer.parseInt(Status));
                                    allPosts.add(post);

                                } catch (JSONException e) {

                                }
                            }
//assign
                            try{
                                PostAdapter adapter = new PostAdapter(getActivity(), allPosts);
                                myposts.setAdapter(adapter);
                            }catch (Exception e){
                                e.printStackTrace();
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

    }
    }


