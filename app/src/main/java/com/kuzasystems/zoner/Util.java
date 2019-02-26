package com.kuzasystems.zoner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

public class Util {
    public static void getBuilderWithOneControl(Context cxt,String msg){
         AlertDialog.Builder builder= new AlertDialog.Builder(cxt);
        //   }
        builder//.setTitle("Delete entry")
                //.setView(R.layout.logochoices)
                .setMessage(msg)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.mipmap.zoner_notification_ic)
                .show();
    }
}
