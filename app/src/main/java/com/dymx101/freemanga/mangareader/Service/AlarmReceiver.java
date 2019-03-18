package com.dymx101.freemanga.mangareader.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sonu on 09/04/17.
 */

public class AlarmReceiver
        extends BroadcastReceiver
    {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"Updation Star!",Toast.LENGTH_SHORT).show();

        //Stop sound service to play sound for alarm
        context.startService(new Intent(context,UpdateMangaList.class));

        //This will send a notification message and show notification in notification tray

//        startWakefulService(context, (intent.setComponent(comp)));

    }


}
