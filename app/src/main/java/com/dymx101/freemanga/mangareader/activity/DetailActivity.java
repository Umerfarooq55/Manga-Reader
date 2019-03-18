package com.dymx101.freemanga.mangareader.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.Service.MessageEvent;
import com.dymx101.freemanga.mangareader.Service.MessageEventAPI;
import com.dymx101.freemanga.mangareader.adapter.SectionPagerAdapter;
import com.dymx101.freemanga.mangareader.database.TinyDB;
import com.dymx101.freemanga.mangareader.fragment.ChaptersFragment;
import com.dymx101.freemanga.mangareader.fragment.InfroFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;
    private String mangaId;
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences sharedPreferences;
    private Boolean snackbool = true;
    private Snackbar snackbar, snackbarjson;
    private InterstitialAd mInterstitialAds;
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

//        if (event.message.equals("nointernet")) {
//            if (snackbool) {
//                snackbar();
//            }
//        }
//        if (event.message.equals("internet")) {
//            if (!snackbool) {
//                snackbar.dismiss();
//                snackbool = true;
//
//            }
//
//        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEventAPI event)
    {

        if (event.message.equals("Updated")) {
            setSnackbarjson();
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manga Details");

        sharedPreferences = getSharedPreferences("isChecked", 0);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
TinyDB tinyDB = new TinyDB(this);
        mangaId = tinyDB.getString("ParcelId");
         int position = getIntent().getIntExtra("position",0);

        //Tabs
        mViewPager = findViewById(R.id.tab_pager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
      mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);

        mTabLayout.setupWithViewPager(mViewPager);


        TabLayout.Tab tab = mTabLayout.getTabAt(position);
        tab.select();
        mInterstitialAds = new InterstitialAd(this);
        mInterstitialAds.setAdUnitId(getString(R.string.interstitial_ads));
        mInterstitialAds.loadAd(new AdRequest.Builder().build());

        mInterstitialAds.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                DetailActivity.super.onBackPressed();
            }

            @Override
            public void onAdClosed() {
//                DetailActivity.super.onBackPressed();
            }
        });


    }

    public String sendData() {
        return mangaId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            sharedPreferences.edit().putBoolean("isChecked", false).apply();
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        sharedPreferences.edit().putBoolean("isChecked", false).apply();

        if (mInterstitialAds.isLoaded()) {
            mInterstitialAds.show();
        } else {

            super.onBackPressed();
        }
        Intent i = new Intent(DetailActivity.this,MainActivity.class);

        startActivity(i);


    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InfroFragment(),"INFO");
        adapter.addFragment(new ChaptersFragment(),"CHAPTERS");

        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter
        {
            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager)
            {
                super(manager);
            }

            @Override
            public Fragment getItem(int position)
            {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount()
            {
                return mFragmentList.size();
            }

            public void addFragment(Fragment fragment,String title)
            {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position)
            {
                return mFragmentTitleList.get(position);
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
}



