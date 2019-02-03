package com.kuzasystems.zoner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnConnected extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        MyIntentService is = new MyIntentService();
        is.startActionFoo(context, "", "");
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
