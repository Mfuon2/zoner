package com.kuzasystems.zoner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by victor on 10/18/2017.
 */

public class MessageListAdapter extends ArrayAdapter<MyMessage> {
        int resource;
        Context context;
        LayoutInflater layoutInflater;

        public MessageListAdapter(Context _context, List<MyMessage> objects) {
            super(_context, R.layout.messagerow, objects);
            resource = R.layout.messagerow;
            context= _context;
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MyMessage message= getItem(position);
            View row = layoutInflater.inflate(resource, parent, false);
            TextView senderId = (TextView)row.findViewById(R.id.senderId);
            senderId.setText(message.getRecipientName());
            TextView messageDate = (TextView)row.findViewById(R.id.messageDate);
            messageDate.setText(message.getSentOn());
            TextView messagePreview = (TextView)row.findViewById(R.id.messagePreview);
            messagePreview.setText(message.getMessage());
            TextView unreadCount = (TextView)row.findViewById(R.id.unreadCount);
            try {
                //get unread
                int count = 0;
                int sender = message.getSenderId();
                int recipient = Config.getMyUserId(context);
                int mySender = 0;
                if (sender == recipient) {
                    mySender = message.getRecipientId();
                } else {
                    mySender = message.getSenderId();
                }
                String sql = "select * from Messages where Recipient =" + recipient + " and Sender =" + mySender + " and Status = 0";
                SQLiteDatabase sqlDb = new ZonerDB(context).getWritableDatabase();
                Cursor cursor = sqlDb.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    count++;
                }
                if (count == 0) {
                    unreadCount.setVisibility(View.INVISIBLE);
                } else {
                    unreadCount.setVisibility(View.VISIBLE);
                    unreadCount.setText(count + "");
                }
            }catch (Exception t){

            }
            return row;
        }




    }




