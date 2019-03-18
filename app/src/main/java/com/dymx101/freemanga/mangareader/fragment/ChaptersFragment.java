package com.dymx101.freemanga.mangareader.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.Service.MessageEvent;
import com.dymx101.freemanga.mangareader.Service.MessageEventAPI;
import com.dymx101.freemanga.mangareader.activity.DetailActivity;
import com.dymx101.freemanga.mangareader.adapter.ChaptersAdapter;
import com.dymx101.freemanga.mangareader.database.TinyDB;
import com.dymx101.freemanga.mangareader.model.Detail;
import com.dymx101.freemanga.mangareader.retrofit.MangaRequest;
import com.dymx101.freemanga.mangareader.retrofit.MangaUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChaptersFragment extends Fragment {

    private RecyclerView recyclerView;
    public static ChaptersAdapter adapter;
    private String getData;
    private SharedPreferences sharedPreferences;
    private CoordinatorLayout coordinatorLayout;
    private Boolean snackbool = true;
    private Snackbar snackbar, snackbarjson;
    private ProgressBar progressBar;
   public TinyDB tinydb ;

    public ChaptersFragment() {

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

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        tinydb.putBoolean("ifloading",true);
        tinydb.putBoolean("cancelasync",true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventAPI event)
    {

        if (event.message.equals("Updated")) {
            setSnackbarjson();
        }


    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_chapters, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get the manga id from activity
        DetailActivity activity = (DetailActivity) getActivity();
        getData = activity.sendData();
        tinydb= new TinyDB(getActivity());
        tinydb.putBoolean("ifloading",false);
        tinydb.putBoolean("cancelasync",false);
        tinydb.putBoolean("Progressing",false);
        progressBar = view.findViewById(R.id.progressbar);

        AdView mAdView = view.findViewById(R.id.adDetailView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        adapter = new ChaptersAdapter(getContext(), new ArrayList<List<String>>(),progressBar, tinydb);
        recyclerView.setAdapter(adapter);

        MangaRequest client = MangaUtils.getAPIService();
        Call<Detail> call = client.getDetail(getData);
        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(@NonNull Call<Detail> call, @NonNull Response<Detail> response) {

                List<List<String>> list = response.body().getChapters();
                for (int i = 0; i < list.size(); i++) {
                    adapter.add(list.get(i));
                }

            }

            @Override
            public void onFailure(@NonNull Call<Detail> call, @NonNull Throwable t) {
                snackbar();
            }
        });


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
        inflater.inflate(R.menu.reverse, menu);

        Switch mSwitchShowSecure;
        mSwitchShowSecure = menu.findItem(R.id.show_secure).getActionView().findViewById(R.id.switch_show_protected);

        sharedPreferences = getActivity().getSharedPreferences("isChecked", 0);

        boolean value = sharedPreferences.getBoolean("isChecked", false); // retrieve the value of your key
        mSwitchShowSecure.setChecked(value);

        mSwitchShowSecure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sharedPreferences.edit().putBoolean("isChecked", true).apply();
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    mLayoutManager.setReverseLayout(true);
                    mLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(mLayoutManager);

                } else {
                    sharedPreferences.edit().putBoolean("isChecked", false).apply();
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);

                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


}
