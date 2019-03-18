package com.dymx101.freemanga.mangareader.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dymx101.freemanga.mangareader.activity.JSONParser;
import com.dymx101.freemanga.mangareader.database.TinyDB;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by sonu on 10/04/17.
 */

public class UpdateMangaList
        extends Service
    {

        private final JSONParser jParser = new JSONParser();
        TinyDB tinydb;
        File root;
        File gpxfile;
        FileWriter writer =null;
        @Nullable
        @Override
        public IBinder onBind(Intent intent)
        {
            return null;
        }

        @Override
        public void onCreate()
        {
            super.onCreate();
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {


                new GetupdateJson().execute();

//             Toast.makeText(this, "download", Toast.LENGTH_SHORT).show();
            } else {

//             Toast.makeText(this, "no download", Toast.LENGTH_SHORT).show();
            }






            tinydb = new TinyDB(this);

        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();


        }

        private class GetupdateJson
                extends AsyncTask<String, String, String>
            {


                @Override
                protected void onPreExecute()
                {
                    super.onPreExecute();
                    Log.d("updationstart","updationstart");

                }


                protected String doInBackground(String... args)
                {


                    String json = jParser.makeHttpRequest("https://www.mangaeden.com/api/list/0/");

//                    Log.d("jsonresponse",String.valueOf(json));

//                    String jsonupdated = String.valueOf(json);

                    tinydb.putBoolean("SetAlarm",false);


                    try {
                        root = new File(Environment.getExternalStorageDirectory(),"/");
                        gpxfile = new File(root,"updatedMangaLIst.json");
                        Log.d("updationstop",json+"");
                        writer = new FileWriter(gpxfile);
                        writer.append(json);


                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }




                    tinydb.putBoolean("readfromfile",true);

                    return json;


                }


                @SuppressLint("SetTextI18n")
                protected void onPostExecute(String json)
                {

                    EventBus.getDefault().post(new MessageEventAPI("Updated"));


                }
            }
    }
