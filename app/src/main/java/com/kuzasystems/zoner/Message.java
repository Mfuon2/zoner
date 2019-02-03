package com.kuzasystems.zoner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message extends AppCompatActivity {
ListView messages;
    EditText messageBox;
    ImageButton sendMessage;
    ProgressBar progressBar;
     Bundle bundle = null;
    private final Handler handler = new Handler();
    Runnable runnable= new Runnable() {
        @Override
        public void run() {
            // Write code for your refresh logic
            loadConversationMessages();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bundle = getIntent().getExtras();
        setTitle(bundle.get("businessName").toString());
        messageBox =(EditText) findViewById(R.id.messageBox);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        messages= (ListView) findViewById(R.id.messages);

        loadConversationMessages();
        /*Thread thread = new Thread(){
            public void run() {

                try {
                    loadConversationMessages();
                    Thread.sleep(1000);

                } catch (Exception r) {
                    r.printStackTrace();
                }

            }
        };
        thread.start();*/

    }
    public void sendMessageClick(View v){
        final String myMessage = messageBox.getText().toString().trim();
        if (myMessage.equals("")){
            messageBox.setError("Please enter your message");
        }else{
        final String recipient = bundle.get("businessId").toString();
            int sender =0;
            try {
                SQLiteDatabase sqlDb = new ZonerDB(Message.this).getWritableDatabase();
                Cursor cursor = sqlDb.rawQuery("select * from users", null);
                while (cursor.moveToNext()) {
                    // username = cursor.getString(cursor.getColumnIndex("username")) ;
                    //  password = cursor.getString(cursor.getColumnIndex("password")) ;
                    //loggedIn = true;
                    sender = cursor.getInt(cursor.getColumnIndex("id"));
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final int senderid = sender;
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue requestQueue = Volley.newRequestQueue(Message.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url + "sendMessage.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressBar.setVisibility(View.INVISIBLE);
                   // System.out.print(response);
                    //android.util.Log.d("ERROR", response);
                   Toast.makeText(Message.this, response,Toast.LENGTH_LONG).show();
                    messageBox.setText("");
                    // clear();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Message.this, "" + error, Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();

                   param.put("Sender", senderid+"");
                    param.put("Recipient", recipient);
                    param.put("Message", myMessage);

                    return param;
                }
            };

            requestQueue.add(stringRequest);
        }
    }
    public void loadConversationMessages(){
        final String recipient = bundle.get("businessId").toString();
        final int sender = Config.getMyUserId(Message.this);
        String sql = "select * from Messages where (Sender = "+sender+" and Recipient = "+recipient+") or (Recipient = "+sender+" and Sender  = "+recipient+")";
        String username= "";
        String userPassword = "";
        try {
            SQLiteDatabase sqlDb = new ZonerDB(Message.this).getWritableDatabase();
            Cursor cursor = sqlDb.rawQuery(sql, null);
            final List<MyMessage> myMessages = new ArrayList<>();
            while (cursor.moveToNext()) {
                DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
                String myDate = cursor.getString(cursor.getColumnIndex("Sent On"));
                //Toast.makeText(Message.this, myDate, Toast.LENGTH_LONG).show();
                java.sql.Date sqlDate = new java.sql.Date(df.parse(myDate).getTime());
                //(int messsageId, int senderId, int recipientId, String recipientName, String message, Date sentOn, int status) {
               MyMessage mes = new MyMessage(cursor.getInt(cursor.getColumnIndex("Id")), cursor.getInt(cursor.getColumnIndex("Sender")), cursor.getInt(cursor.getColumnIndex("Recipient")),
                       cursor.getString(cursor.getColumnIndex("RecipientName")),
                       cursor.getString(cursor.getColumnIndex("Message")),

                       myDate, cursor.getInt(cursor.getColumnIndex("Status")));
                myMessages.add(mes);
            }
            ConversationAdapter adapter = new ConversationAdapter(Message.this, myMessages);
            messages.setAdapter(adapter);
            messages.setSelection(adapter.getCount() - 1);
            cursor.close();
            //mark as read
            //where recipient= me and sender = sender

            cursor = sqlDb.rawQuery("select * from users", null);
            while (cursor.moveToNext()) {
                username = cursor.getString(cursor.getColumnIndex("Username")) ;
                userPassword = cursor.getString(cursor.getColumnIndex("Password")) ;
                // Toast.makeText(MyIntentService.this, username, Toast.LENGTH_LONG).show();

            }
            cursor.close();
        } catch (Exception e) {
//            e.printStackTrace();
        }
            final String url = Config.url + "markMessage.php?username="+username+"&password="+userPassword+"&sender="+recipient;

            JSONArray requestArray = new JSONArray();
            JSONObject object = new JSONObject();
            try {
                RequestQueue queue = Volley.newRequestQueue(Message.this);
                JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
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
            }catch (Exception t){

            }
        SQLiteDatabase sqlDb = new ZonerDB(Message.this).getWritableDatabase();
        try {
            String insertMessage = "update Messages set Status =1 where Sender ="+recipient+" and Recipient ="+Config.getMyUserId(Message.this);
            sqlDb.execSQL(insertMessage);
            Log.v("Error", "Victor "+insertMessage);
            // SQLiteStatement statement = sqlDb.compileStatement(insertMessage);
            //  statement.executeUpdateDelete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        handler.postDelayed(runnable, 5000);


    }
    public void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);
    }
    public void onResume(){
        super.onResume();
        handler.postDelayed(runnable, 5000);
    }
}
