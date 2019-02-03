package com.kuzasystems.zoner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 30-Sep-18.
 */

public class MessagesFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
    TextView noMessage = null;
    ListView allMessages = null;
    //android.support.v4.widget.SwipeRefreshLayout refresh;
    private final Handler handler = new Handler();
    Runnable runnable= new Runnable() {
        @Override
        public void run() {
            // Write code for your refresh logic
            loadMessages();
        }
    };
        public MessagesFragment() {
        }


        public static MessagesFragment newInstance(int sectionNumber) {
            MessagesFragment fragment = new MessagesFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.messageslist, container, false);

            noMessage= (TextView)rootView.findViewById(R.id.noMessage);

            allMessages= (ListView) rootView.findViewById(R.id.allMessages);
           /* refresh = (android.support.v4.widget.SwipeRefreshLayout)rootView.findViewById(R.id.refresh);
            refresh.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            loadMessages();
                        }
                    });*/
            loadMessages();

           /* MyMessage mes = new MyMessage(1, 1, 1,
                    "",
                    "",

                    "", 1);*/
           // myMessages.add(mes);



           return rootView;
        }
        public void loadMessages() {
            //refresh.setRefreshing(true);
            try {
                String sql = "SELECT DISTINCT(Sender) FROM `Messages` ";
                SQLiteDatabase sqlDb = new ZonerDB(getActivity()).getWritableDatabase();
                Cursor cursor = sqlDb.rawQuery(sql, null);
                List<Integer> allSenders = new ArrayList<>();

                final List<MyMessage> myMessages = new ArrayList<>();
                while (cursor.moveToNext()) {
                    allSenders.add(cursor.getInt(cursor.getColumnIndex("Sender")));
                }
                String recipients = "SELECT DISTINCT(Recipient) FROM `Messages` ";
                cursor = sqlDb.rawQuery(recipients, null);
                while (cursor.moveToNext()) {
                    int sId = cursor.getInt(cursor.getColumnIndex("Recipient"));
                    if (!allSenders.contains(sId)) {
                        allSenders.add(sId);
                    }
                }
                //we have all ids
                int myId = Config.getMyUserId(getActivity());
                String subQuery = "";
                for (int a : allSenders) {
                    if (!subQuery.equals("")) {
                        subQuery += ",";
                    }
                    subQuery += "(SELECT MAX(Id) from Messages where (Sender=" + a + " AND Recipient= " + myId + ") or (Sender=" + myId + " And Recipient= " + a + "))";
                }
                if (!subQuery.equals("")) {
                    String latestMessages = "SELECT * FROM `Messages` where Id in ( " +
                            subQuery +
                            " ) ORDER By Id DESC ";
                    cursor = sqlDb.rawQuery(latestMessages, null);
                    while (cursor.moveToNext()) {
                        try {
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
                        } catch (Exception r) {

                        }
                    }

                    final MessageListAdapter adapter = new MessageListAdapter(getActivity(), myMessages);
                    if (myMessages.size() == 0) {
                        noMessage.setVisibility(View.VISIBLE);
                    } else {
                        noMessage.setVisibility(View.INVISIBLE);
                    }
                    allMessages.setAdapter(adapter);
                    allMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MyMessage message = adapter.getItem(position);
                            Intent intent = new Intent(getActivity(), Message.class);
                            Bundle bundle = new Bundle();
                            int otherParty = message.getSenderId() == Config.getMyUserId(getActivity()) ? message.getRecipientId() : message.getSenderId();
                            bundle.putString("businessId", otherParty + "");
                            bundle.putString("businessName", message.getRecipientName());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    //businessName businessId
                }
                handler.postDelayed(runnable, 5000);
            }catch(Exception o){

            }
            //refresh.setRefreshing(false);
            }

}