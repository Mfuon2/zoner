package com.kuzasystems.zoner;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by victor on 10/18/2017.
 */

public class ConversationAdapter extends ArrayAdapter<MyMessage> {
        int resource;
        Context context;
        LayoutInflater layoutInflater;

        public ConversationAdapter(Context _context, List<MyMessage> objects) {
            super(_context, R.layout.conversationrow, objects);
            resource = R.layout.conversationrow;
            context= _context;
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MyMessage myMessage= getItem(position);
            View row = layoutInflater.inflate(resource, parent, false);
            TextView messageReceived = (TextView) row.findViewById(R.id.messageReceived);
            TextView messageReceivedDate = (TextView) row.findViewById(R.id.messageReceivedDate);
            TextView messageSent = (TextView) row.findViewById(R.id.messageSent);
            TextView messageSentDate = (TextView) row.findViewById(R.id.messageSentDate);
            LinearLayout receivedMessage =(LinearLayout) row.findViewById(R.id.receivedMessage);
            LinearLayout sendMessage =(LinearLayout) row.findViewById(R.id.sendMessage);
            //check if sender is logged in user
            int loggedInUserId =Config.getMyUserId(context);
            if(myMessage.getSenderId()==loggedInUserId){//message was sent
                messageSent.setText(myMessage.getMessage());
                messageSentDate.setText(myMessage.getSentOn().toString());
                receivedMessage.setVisibility(View.INVISIBLE);
            }else{//message was received
                messageReceived.setText(myMessage.getMessage());
                messageReceivedDate.setText(myMessage.getSentOn().toString());
                sendMessage.setVisibility(View.INVISIBLE);
            }


            return row;
        }




    }




