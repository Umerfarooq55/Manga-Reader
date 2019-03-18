package com.dymx101.freemanga.mangareader.fragment;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.Service.MessageEvent;
import com.dymx101.freemanga.mangareader.Service.MessageEventAPI;
import com.dymx101.freemanga.mangareader.adapter.CustomCursorAdapter;
import com.dymx101.freemanga.mangareader.data.MangaContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CustomCursorAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private Boolean snackbool = true;
    private Snackbar snackbar, snackbarjson;
    private CoordinatorLayout coordinatorLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        return rootView;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Favorites");
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        mAdapter = new CustomCursorAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        AdView mAdView = view.findViewById(R.id.adViewMainActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getActivity().getSupportLoaderManager().initLoader(0, null, this);

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {

                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }


            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {

                try {
                    return getActivity().getContentResolver().query(MangaContract.ImagesContract.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e("FavouritesActivity", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);

    }
}
