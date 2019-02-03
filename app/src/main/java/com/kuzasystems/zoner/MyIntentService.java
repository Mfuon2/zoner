package com.kuzasystems.zoner;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.PreparedStatement;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.kuzasystems.zoner.action.FOO";
    private static final String ACTION_BAZ = "com.kuzasystems.zoner.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.kuzasystems.zoner.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.kuzasystems.zoner.extra.PARAM2";

    public MyIntentService() {
        super("MyIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        try{
            int count= 0;

            while(true) {
                count++;
                Thread.sleep(10000);
                //Toast.makeText(MyIntentService.this, "service count : " , Toast.LENGTH_SHORT).show();
                //get messages and insert into database
                String username= "";
                String userPassword = "";
                try {
                    RequestQueue queue = Volley.newRequestQueue(MyIntentService.this);
                    try {
                        SQLiteDatabase sqlDb = new ZonerDB(MyIntentService.this).getWritableDatabase();
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
                    //get last id
                    int lastId= 0;
                    try {
                        SQLiteDatabase sqlDb = new ZonerDB(MyIntentService.this).getWritableDatabase();
                        Cursor cursor = sqlDb.rawQuery("select * from Messages order by Id desc", null);
                        while (cursor.moveToNext()) {
                            lastId = cursor.getInt(cursor.getColumnIndex("Id"));
                                    break;

                        }
                        Toast.makeText(MyIntentService.this, "last id"+lastId, Toast.LENGTH_LONG).show();
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final String url = Config.url + "getMessages.php?username="+username+"&password="+userPassword+"&id="+lastId;
                    JSONArray requestArray = new JSONArray();
                    JSONObject object = new JSONObject();


                    JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {

                                    SQLiteDatabase sqlDb = new ZonerDB(MyIntentService.this).getWritableDatabase();

                                    int length = response.length();

                                    for (int a = 0; a < length; a++) {
                                        try {
                                            JSONObject object = (JSONObject) response.get(a);
                                            String Id = (String) object.get("Id");
                                            String Sender = (String) object.get("Sender");
                                            String Recipient = (String) object.get("Recipient");
                                            String Name = (String) object.get("Name");
                                            String Message = (String) object.get("Message");
                                            String SentOn = (String) object.get("Sent On");
                                            String Status = (String) object.get("Status");

                                            String insertMessage = "INSERT INTO `Messages`(`Id`,`Sender`, `Recipient`, `RecipientName`, `Message`, `Sent On`, `Status`) VALUES" +
                                                    "('"+Id+"','"+Sender+"','"+Recipient+"',?,?,'"+SentOn+"','"+Status+"')";
                                            // sqlDb.execSQL(insertMessage);
                                            SQLiteStatement statement = sqlDb.compileStatement(insertMessage);
                                            statement.bindString(2,Message);
                                            statement.bindString(1,Name);
                                            statement.executeInsert();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
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
                int nMessages = 0;
                String senderName = "";
                String message= "";
                try {
                    SQLiteDatabase sqlDb = new ZonerDB(MyIntentService.this).getWritableDatabase();
                    Cursor cursor = sqlDb.rawQuery("select * from Messages where Status = 0 and not Sender = "+Config.getMyUserId(MyIntentService.this), null);


                    while (cursor.moveToNext()) {
                        nMessages++;
                        senderName = cursor.getString(cursor.getColumnIndex("RecipientName"));
                        message= cursor.getString(cursor.getColumnIndex("Message"));

                    }
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int NOTIFICATION_ID = 12345;
                if(nMessages==1) {
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.marker)
                                    .setContentTitle(senderName)
                                    .setContentText(message);
                    Intent targetIntent = new Intent(this, SplashActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nManager.notify(NOTIFICATION_ID, builder.build());

                }else if(nMessages>1){
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.marker)
                                    .setContentTitle("You Have Unread Messages")
                                    .setContentText("You have "+nMessages+" unread messages");
                    Intent targetIntent = new Intent(this, SplashActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nManager.notify(NOTIFICATION_ID, builder.build());
                }else if(nMessages==0){
                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nManager.cancel(NOTIFICATION_ID);
                            //.notify(NOTIFICATION_ID, builder.build());
                }

            }
        }catch (Exception e){

        }
       // throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
       // throw new UnsupportedOperationException("Not yet implemented");
    }
}
