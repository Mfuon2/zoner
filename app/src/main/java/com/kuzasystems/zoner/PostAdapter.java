package com.kuzasystems.zoner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by victor on 10/18/2017.
 */

public class PostAdapter extends ArrayAdapter<Post> {
        int resource;
        Context context;
        LayoutInflater layoutInflater;

        public PostAdapter(Context _context, List<Post> objects) {
            super(_context, R.layout.postrow, objects);
            resource = R.layout.postrow;
            context= _context;
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Post post= getItem(position);
            View row = layoutInflater.inflate(resource, parent, false);
            final ImageView PostImage = (ImageView) row.findViewById(R.id.PostImage);
            try{
                Picasso.with(context).load(post.getImage()).into(PostImage)                ;
            }catch(Exception r){

            }
            TextView postText = (TextView) row.findViewById(R.id.postText);
            postText.setText(post.getPostText());
            TextView addedOn = (TextView) row.findViewById(R.id.addedOn);
            addedOn.setText(post.getAddedOn());

            return row;
        }




    }




