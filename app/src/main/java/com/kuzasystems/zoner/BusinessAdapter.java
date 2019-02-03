package com.kuzasystems.zoner;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by victor on 10/18/2017.
 */

public class BusinessAdapter extends ArrayAdapter<Users> {
        int resource;
        Context context;
        LayoutInflater layoutInflater;

        public BusinessAdapter(Context _context, List<Users> objects) {
            super(_context, R.layout.businessrow, objects);
            resource = R.layout.businessrow;
            context= _context;
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Users user= getItem(position);
            View row = layoutInflater.inflate(resource, parent, false);
            final ImageView businessLogo = (ImageView) row.findViewById(R.id.businessLogo);
            try{
                Picasso.with(context).load(user.getLogo()).into(businessLogo)

                ;
            }catch(Exception r){

            }

           /* try {
                RequestQueue queue = Volley.newRequestQueue(context);
                ImageLoader mImageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }

                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }
                });
                //Image URL - This can point to any image file supported by Android

                mImageLoader.get(user.getLogo(), ImageLoader.getImageListener(businessLogo,
                        R.drawable.load, android.R.drawable
                                .ic_dialog_alert));
                businessLogo.setImageUrl(user.getLogo(), mImageLoader);
            }catch (Exception y){

            }*/
            TextView businessName = (TextView) row.findViewById(R.id.businessName);
            businessName.setText(user.getName());
            TextView businessLocation = (TextView) row.findViewById(R.id.location);
            businessLocation.setText(user.getLocation());

            TextView phone = (TextView) row.findViewById(R.id.phone);
            phone.setText(user.getPhoneNumber());
            TextView status = (TextView) row.findViewById(R.id.status);
            status.setText(user.getBusinessStatus());
            String myStatus = user.getBusinessStatus().toLowerCase();
            //Open Vacant
            if (myStatus.equals("open")||myStatus.equals("vacant")) {
                status.setTextColor(context.getResources().getColor(R.color.colorGreen));
            }else{
                status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }


            return row;
        }




    }




