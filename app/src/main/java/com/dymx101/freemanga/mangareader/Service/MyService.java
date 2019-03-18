package com.dymx101.freemanga.mangareader.Service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;


public class MyService extends Service
    {

        public static Boolean downloadDataBase = true;

        static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
        NotificationManager manager;
        public Context mconext;

        @Nullable
        @Override
        public IBinder onBind(Intent intent)
        {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent,int flags,int startId)
        {
            // Let it continue running until it is stopped.

//        Toast.makeText(this, "start download", Toast.LENGTH_SHORT).show();
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {


                EventBus.getDefault().post(new MessageEvent("internet"));

//             Toast.makeText(this, "download", Toast.LENGTH_SHORT).show();
            } else {
                EventBus.getDefault().post(new MessageEvent("nointernet"));
//             Toast.makeText(this, "no download", Toast.LENGTH_SHORT).show();
            }

            startService(new Intent(this, MyService.class));
            return START_STICKY;
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();
//            Toast.makeText(this,"Service Destroyed",Toast.LENGTH_LONG).show();
        }

    }


