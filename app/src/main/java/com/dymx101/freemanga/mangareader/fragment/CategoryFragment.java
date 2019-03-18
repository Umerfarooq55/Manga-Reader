package com.dymx101.freemanga.mangareader.fragment;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.widget.TextView;
import android.widget.Toast;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.Service.MessageEvent;
import com.dymx101.freemanga.mangareader.Service.MessageEventAPI;
import com.dymx101.freemanga.mangareader.adapter.MangaListAdapter;
import com.dymx101.freemanga.mangareader.data.MangaProvider;
import com.dymx101.freemanga.mangareader.database.TinyDB;
import com.dymx101.freemanga.mangareader.model.Anime;
import com.dymx101.freemanga.mangareader.model.Manga;
import com.dymx101.freemanga.mangareader.retrofit.MangaRequest;
import com.dymx101.freemanga.mangareader.retrofit.MangaUtils;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment implements SearchView.OnQueryTextListener {

    private MangaListAdapter adapter;
    private RecyclerView rv;
    private ArrayList<Manga> mangaList;
    private String categories;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog progressDialog;
    private Boolean snackbool = true;
    private Snackbar snackbar, snackbarjson;
    private TinyDB tinydb;
    private Boolean readfromfile=false;


    public CategoryFragment() {

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        categories = getArguments().getString("Category");
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tinydb = new TinyDB(getActivity());
        readfromfile= tinydb.getBoolean("readfromfile");
        rv = view.findViewById(R.id.recycler_view);
        if ((getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        } else {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        getActivity().setTitle(categories);

        progressDialog = new ProgressDialog(getContext());

        progressDialog.setTitle("Loading All " + categories + " Manga");
        progressDialog.setMessage("Please wait a moment...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mangaList = new ArrayList<>();
        adapter = new MangaListAdapter(getContext(), mangaList);
        rv.setAdapter(adapter);

//        AdView mAdView = view.findViewById(R.id.adViewMainActivity);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        // Try Read from cache
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MangaData", getContext().MODE_PRIVATE);

//        long lastSaveTime = sharedPreferences.getLong("savetime", 0);
//        Date currentDate = new Date(System.currentTimeMillis());
//        Boolean aDayHasPassed = true;//(currentDate.getTime() - lastSaveTime > 1000 * 60 * 60 * 24);

        Boolean readFromCache = false;
        if (MangaProvider.MANGA_LIST_STRING == null) {
            MangaProvider.MANGA_LIST_STRING = sharedPreferences.getString("mangalist", null);
            if (MangaProvider.MANGA_LIST_STRING == null) {
                // load the default data


                if (!readfromfile) {
                    MangaProvider.MANGA_LIST_STRING = loadJSONFromAsset("manga_list.json");
                    Toast.makeText(getActivity(),"Reading Api From Assets",Toast.LENGTH_SHORT).show();
                } else {
                    MangaProvider.MANGA_LIST_STRING = readFromFile(getActivity());
                    Toast.makeText(getActivity(),"Reading Updaint Api",Toast.LENGTH_SHORT).show();
                }
            }
        }

        // load data from cache, no request
//        Gson gson = new Gson();
//        Anime anime = gson.fromJson(MangaProvider.MANGA_LIST_STRING, Anime.class);
//        if (anime != null) {
//            reloadData(new ReloadParams(anime,categories));
//            readFromCache = true;
//            progressDialog.dismiss();
//        }
        if (!readfromfile) {
            MangaProvider.MANGA_LIST_STRING = loadJSONFromAsset("manga_list.json");

        }
        reloadData(new ReloadParams(MangaProvider.MANGA_LIST_STRING,categories));
        readFromCache = true;
//        progressDialog.dismiss();

        if (readFromCache) {
            return;
        }

        final TextView emptyView = view.findViewById(R.id.empty_view);

        // How to use Retrofit - https://www.youtube.com/watch?v=R4XU8yPzSx0
        // Gson Serialization - https://www.youtube.com/watch?v=BbI8FdQOKNs
        MangaRequest client = MangaUtils.getAPIService();
        Call<Anime> call = client.getAnime();

        call.enqueue(new Callback<Anime>() {
            @Override
            public void onResponse(@NonNull Call<Anime> call, @NonNull Response<Anime> response) {
                //&& response.body().getManga().get(i).getC().contains("Action")

                Anime anime = response.body();

                Gson gson = new Gson();
                String responseJsonString = gson.toJson(anime);

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MangaData", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("mangalist", responseJsonString);

                Date date = new Date(System.currentTimeMillis());
                editor.putLong("savetime", date.getTime());

                editor.commit();

//                reloadData(new ReloadParams(anime,categories));
                MangaProvider.MANGA_LIST_STRING = responseJsonString;
                reloadData(new ReloadParams(MangaProvider.MANGA_LIST_STRING,categories));
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<Anime> call, @NonNull Throwable t) {
                progressDialog.hide();
//                emptyView.setText(R.string.nothing_found);

                if (adapter.getItemCount() == 0) {

                    if (MangaProvider.MANGA_LIST_STRING == null) {
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MangaData", getContext().MODE_PRIVATE);
                        MangaProvider.MANGA_LIST_STRING = sharedPreferences.getString("mangalist", null);
                        if (MangaProvider.MANGA_LIST_STRING == null) {
                            if (!readfromfile) {
                                MangaProvider.MANGA_LIST_STRING = loadJSONFromAsset("manga_list.json");
                                Toast.makeText(getActivity(),"Reading Api From Assets",Toast.LENGTH_SHORT).show();
                            } else {
                                MangaProvider.MANGA_LIST_STRING = readFromFile(getActivity());
                                Toast.makeText(getActivity(),"Reading Updaint Api",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    // load data from cache, no request
//                    Gson gson = new Gson();
//                    Anime anime = gson.fromJson(MangaProvider.MANGA_LIST_STRING, Anime.class);
//                    if (anime != null) {
//                        reloadData(new ReloadParams(anime,categories));
//                    }
                    if (!readfromfile) {
                        MangaProvider.MANGA_LIST_STRING = loadJSONFromAsset("manga_list.json");
                       
                    }
                    reloadData(new ReloadParams(MangaProvider.MANGA_LIST_STRING,categories));
                }
            }
        });


    }

    private String loadJSONFromAsset(String jsonFileName) {
        String json = null;
        try {
            InputStream is = getContext().getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void reloadData(ReloadParams params) {


    new DataLoadTask(params).execute();




    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        filter(newText);

        return true;
    }

    private void filter(String text) {
        ArrayList<Manga> filteredList = new ArrayList<>();

        for (Manga item : mangaList) {
            if (item.getT().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);

    }

    private static class ReloadParams {
        //        Anime anime;
        String mangaListString;
        String categories;

        ReloadParams(String mangaListString, String categories) {
            this.mangaListString = mangaListString;
            this.categories = categories;
        }
    }

    private class DataLoadTask extends AsyncTask<Void, Void, MangaListAdapter> {

        ReloadParams reloadParams;

        DataLoadTask(ReloadParams reloadParams) {
            this.reloadParams = reloadParams;
        }

        @Override
        protected MangaListAdapter doInBackground(Void... params) {
            Gson gson = new Gson();
            Anime anime = gson.fromJson(reloadParams.mangaListString, Anime.class);
            List<Manga> list = anime.getManga();
            Collections.sort(list, new Comparator<Manga>() {
                public int compare(Manga v1, Manga v2) {
                    return v2.getH().compareTo(v1.getH());
                }
            });

            mangaList = new ArrayList<>();
            MangaListAdapter newAdapter = new MangaListAdapter(getContext(), mangaList);

            for (int i = 0; i < list.size(); i++) {
                if (reloadParams.categories == null) {
                    if (list.get(i).getIm() != null
                            && !list.get(i).getC().contains("Adult")
                            ) {
                        newAdapter.add(anime.getManga().get(i));
                    }
                }else {
                    if (list.get(i).getIm() != null && list.get(i).getC().contains(reloadParams.categories)
                            && !list.get(i).getC().contains("Adult")
                            ) {
                        newAdapter.add(anime.getManga().get(i));
                    }
                }
            }

            return newAdapter;
        }
        @Override
        protected void onPostExecute(MangaListAdapter newAdapter) {
            super.onPostExecute(newAdapter);

            adapter = newAdapter;
            rv.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }
    private String readFromFile(Context context) {

        String json = "";
        File file = new File(Environment.getExternalStorageDirectory(),"updatedMangaLIst.json");
        try {
            InputStream inputStream = new FileInputStream(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                json = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity","File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return json;
    }
}