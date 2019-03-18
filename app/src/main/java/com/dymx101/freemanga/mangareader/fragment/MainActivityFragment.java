package com.dymx101.freemanga.mangareader.fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.Service.AlarmReceiver;
import com.dymx101.freemanga.mangareader.Service.MessageEvent;
import com.dymx101.freemanga.mangareader.Service.MessageEventAPI;
import com.dymx101.freemanga.mangareader.adapter.MangaListAdapter;
import com.dymx101.freemanga.mangareader.data.MangaProvider;
import com.dymx101.freemanga.mangareader.database.TinyDB;
import com.dymx101.freemanga.mangareader.model.Anime;
import com.dymx101.freemanga.mangareader.model.Manga;
import com.dymx101.freemanga.mangareader.retrofit.MangaRequest;
import com.dymx101.freemanga.mangareader.retrofit.MangaUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityFragment
        extends Fragment
        implements SearchView.OnQueryTextListener
    {

        //Alarm Request Code
        private static final int ALARM_REQUEST_CODE = 133;
        private Boolean setAlarm;
        private Boolean jsonupdate;
        private Boolean readfromfile;
        private TinyDB tinydb;
        private MangaListAdapter adapter;
        private RecyclerView rv;
        private ArrayList<Manga> mangaList;
        private CoordinatorLayout coordinatorLayout;
        private ProgressDialog progressDialog, progressDialogJson;
        private Boolean snackbool = true;
        private Snackbar snackbar, snackbarjson;
        private ProgressBar progressBar;
        private PendingIntent pendingIntent;
       private  SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;
        public MainActivityFragment()
        {

        }

        @Override
        public void onStart()
        {
            super.onStart();
            EventBus.getDefault().register(this);
        }

        @Override
        public void onStop()
        {
            super.onStop();
            EventBus.getDefault().unregister(this);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMessageEvent(MessageEvent event)
        {

            if (event.message.equals("nointernet")) {
                if (snackbool) {
                    snackbar();
                }
            }
            if (event.message.equals("internet")) {
                if (!snackbool) {
                    snackbar.dismiss();
                    snackbool = true;

                }

            }

        }
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMessageEvent(MessageEventAPI event)
        {

            if (event.message.equals("Updated")) {
                setSnackbarjson();
            }


            }



        // This method will be called when a SomeOtherEvent is posted

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState
                                )
        {
            setHasOptionsMenu(true);
            return inflater.inflate(R.layout.fragment_main,container,false);

        }

        @Override
        public void onViewCreated(View view,@Nullable Bundle savedInstanceState)
        {
            super.onViewCreated(view,savedInstanceState);
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MangaData",getContext().MODE_PRIVATE);
            editor = sharedPreferences.edit();

            tinydb = new TinyDB(getActivity());

            setAlarm = tinydb.getBoolean("SetAlarm");
            readfromfile = tinydb.getBoolean("readfromfile");




            if (!setAlarm) {

                Intent alarmIntent = new Intent(getActivity(),AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(getActivity(),ALARM_REQUEST_CODE,alarmIntent,0);
                int time = 24;
                String getInterval = String.valueOf(time);//Alarm Time
                if (!getInterval.equals("") && !getInterval.equals("0"))
                    //finally trigger alarm manager
                    triggerAlarmManager(getTimeInterval(getInterval));
            }



            progressBar = view.findViewById(R.id.progressbar);
            rv = view.findViewById(R.id.recycler_view);
            coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
            rv.setLayoutManager(new GridLayoutManager(getContext(),3));

            progressDialog = new ProgressDialog(getContext());

            progressDialog.setTitle("Loading All Manga");
            progressDialog.setMessage("Please wait a moment...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mangaList = new ArrayList<>();
            adapter = new MangaListAdapter(getContext(),mangaList);
            rv.setAdapter(adapter);

            AdView mAdView = view.findViewById(R.id.adViewMainActivity);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


            // Try Read from cache
             sharedPreferences = getContext().getSharedPreferences("MangaData",getContext().MODE_PRIVATE);
            if (readfromfile) {
                editor.putString("mangalist",readFromFile(getActivity()));
//                Toast.makeText(getActivity(),"save",Toast.LENGTH_SHORT).show();
            }
//        long lastSaveTime = sharedPreferences.getLong("savetime", 0);
//        Date currentDate = new Date(System.currentTimeMillis());
//        Boolean aDayHasPassed = true;//(currentDate.getTime() - lastSaveTime > 1000 * 60 * 60 * 24);

            Boolean readFromCache = false;
            if (MangaProvider.MANGA_LIST_STRING == null) {
                MangaProvider.MANGA_LIST_STRING = sharedPreferences.getString("mangalist",null);
                if (MangaProvider.MANGA_LIST_STRING == null) {
                    // load the default data

                    if (!readfromfile) {
                        MangaProvider.MANGA_LIST_STRING = loadJSONFromAsset("manga_list.json");
//                        Toast.makeText(getActivity(),"Reading Api From Assets",Toast.LENGTH_SHORT).show();
                    } else {
                        MangaProvider.MANGA_LIST_STRING = readFromFile(getActivity());
//                        Toast.makeText(getActivity(),"save",Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getActivity(),"Reading Updaint Api",Toast.LENGTH_SHORT).show();
                    }
                    ;
                }
            }

            // load data from cache, no request
//        Gson gson = new Gson();
//        Anime anime = gson.fromJson(MangaProvider.MANGA_LIST_STRING, Anime.class);
//        if (anime != null) {
//
 if (readfromfile) {
     MangaProvider.MANGA_LIST_STRING = readFromFile(getActivity());
        }
            reloadData(new ReloadParams(MangaProvider.MANGA_LIST_STRING,null));
            readFromCache = true;
            progressDialog.dismiss();
//            new GetupdateJson().execute();
            if (readFromCache) {
                return;
            }

            final TextView emptyView = view.findViewById(R.id.empty_view);

            MangaRequest client = MangaUtils.getAPIService();
            Call<Anime> call = client.getAnime();

            call.enqueue(new Callback<Anime>()
                {
                    @Override
                    public void onResponse(@NonNull Call<Anime> call,
                            @NonNull Response<Anime> response
                                          )
                    {

                        Anime anime = response.body();

                        Gson gson = new Gson();
                        String responseJsonString = gson.toJson(anime);



                        if (!readfromfile) {

                            editor.putString("mangalist",responseJsonString);
                            MangaProvider.MANGA_LIST_STRING = responseJsonString;
                        }else{
                            editor.putString("mangalist",readFromFile(getActivity()));
//                            Toast.makeText(getActivity(),"save",Toast.LENGTH_SHORT).show();
                            MangaProvider.MANGA_LIST_STRING = readFromFile(getActivity());
                        }
                        Date date = new Date(System.currentTimeMillis());
                        editor.putLong("savetime",date.getTime());

                        editor.commit();

                        Log.d("mangi",MangaProvider.MANGA_LIST_STRING);
                        reloadData(new ReloadParams(responseJsonString,null));
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onFailure(@NonNull Call<Anime> call,@NonNull Throwable t)
                    {

                        progressDialog.hide();
//                emptyView.setText(R.string.nothing_found);
                        if (readfromfile) {
//                            Toast.makeText(getActivity(),"save",Toast.LENGTH_SHORT).show();
                            editor.putString("mangalist",readFromFile(getActivity()));
                        }
                        if (adapter.getItemCount() == 0) {

                            if (MangaProvider.MANGA_LIST_STRING == null) {
                                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MangaData",getContext().MODE_PRIVATE);
                                MangaProvider.MANGA_LIST_STRING = sharedPreferences.getString("mangalist",null);
                                if (MangaProvider.MANGA_LIST_STRING == null) {

                                    if (!readfromfile) {
                                        MangaProvider.MANGA_LIST_STRING = loadJSONFromAsset("manga_list.json");
//                                        Toast.makeText(getActivity(),"Reading Api From Assets",Toast.LENGTH_SHORT).show();
                                    } else {
                                        MangaProvider.MANGA_LIST_STRING = readFromFile(getActivity());
//                                        Toast.makeText(getActivity(),"Reading Updaint Api",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            // load data from cache, no request
//                    Gson gson = new Gson();
//                    Anime anime = gson.fromJson(MangaProvider.MANGA_LIST_STRING, Anime.class);
//                    if (anime != null) {
//
//                    }
                            reloadData(new ReloadParams(MangaProvider.MANGA_LIST_STRING,null));

                        }
                    }
                });

        }

        private String loadJSONFromAsset(String jsonFileName)
        {
            String json = null;
            try {
                InputStream is = getContext().getAssets().open(jsonFileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer,"UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }

        private void reloadData(ReloadParams params)
        {

            new DataLoadTask(params).execute();


        }

        public boolean isNetworkAvailable()
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        public void snackbar()
        {
            snackbar = Snackbar.make(coordinatorLayout,R.string.snackmsg,Snackbar.LENGTH_INDEFINITE);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.snackBarInternet));
            snackbar.show();

            snackbool = false;

        }

        public void setSnackbarjson()
        {
            snackbarjson = Snackbar.make(coordinatorLayout,R.string.snackJsonMsg,Snackbar.LENGTH_LONG);
            View snackBarView = snackbarjson.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.snackBarjson));
            snackbarjson.show();



        }
        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
        {
            inflater.inflate(R.menu.main,menu);
            super.onCreateOptionsMenu(menu,inflater);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            MenuItem searchMenuItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchMenuItem.getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();

            if (id == R.id.action_search) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onQueryTextSubmit(String query)
        {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText)
        {
            newText = newText.toLowerCase();
            filter(newText);

            return true;
        }

        private void filter(String text)
        {
            ArrayList<Manga> filteredList = new ArrayList<>();

            for (Manga item : mangaList) {
                if (item.getT().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            adapter.filterList(filteredList);

        }



        public void triggerAlarmManager(int alarmTriggerTime)
        {
            // get a Calendar object with current time
            Calendar cal = Calendar.getInstance();
            // add alarmTriggerTime seconds to the calendar object
            cal.add(Calendar.SECOND,alarmTriggerTime);

            AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager
            manager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);//set alarm manager with entered timer by converting into milliseconds

//            .makeText(getActivity(),"Api will Update after 24 Hours",Toast.LENGTH_SHORT).show();
            tinydb.putBoolean("SetAlarm",true);
        }

        //Stop/Cancel alarm manager
        public void stopAlarmManager()
        {


        }

        private int getTimeInterval(String getInterval)
        {
            int interval = Integer.parseInt(getInterval);


            return interval*60*60;


        }

        private String readFromFile(Context context)
        {

            String json = "";
            File file = new File(Environment.getExternalStorageDirectory(),"updatedMangaLIst.json");
            try {
                InputStream inputStream = new FileInputStream(file);

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    json = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                Log.e("login activity","File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity","Can not read file: " + e.toString());
            }

            return json;
        }

        private static class ReloadParams
            {
                //        Anime anime;
                String mangaListString;
                String categories;

                ReloadParams(String mangaListString,String categories)
                {
                    this.mangaListString = mangaListString;
                    this.categories = categories;
                }

            }

        private class DataLoadTask
                extends AsyncTask<Void, Void, MangaListAdapter>
            {

                ReloadParams reloadParams;

                DataLoadTask(ReloadParams reloadParams)
                {
                    this.reloadParams = reloadParams;


                }

                @Override
                protected MangaListAdapter doInBackground(Void... params)
                {
                    Gson gson = new Gson();
                    Anime anime = gson.fromJson(reloadParams.mangaListString,Anime.class);
                    List<Manga> list = anime.getManga();
                    Collections.sort(list,new Comparator<Manga>()
                        {
                            public int compare(Manga v1,Manga v2)
                            {
                                return v2.getH().compareTo(v1.getH());
                            }
                        });

                    mangaList = new ArrayList<>();
                    MangaListAdapter newAdapter = new MangaListAdapter(getContext(),mangaList);

                    for (int i = 0; i < list.size(); i++) {
                        if (reloadParams.categories == null) {
                            if (list.get(i).getIm() != null && !list.get(i).getC().contains("Adult")) {
                                newAdapter.add(anime.getManga().get(i));
                            }
                        } else {
                            if (list.get(i).getIm() != null && list.get(i).getC().contains(reloadParams.categories) && !list.get(i).getC().contains("Adult")) {
                                newAdapter.add(anime.getManga().get(i));
                            }
                        }
                    }

                    return newAdapter;
                }

                @Override
                protected void onPostExecute(MangaListAdapter newAdapter)
                {
                    super.onPostExecute(newAdapter);

                    adapter = newAdapter;
                    rv.setAdapter(adapter);
                }
            }

    }
