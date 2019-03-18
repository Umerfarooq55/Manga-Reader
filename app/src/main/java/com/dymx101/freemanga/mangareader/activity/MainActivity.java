package com.dymx101.freemanga.mangareader.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.Service.MyService;
import com.dymx101.freemanga.mangareader.fragment.CategoryFragment;
import com.dymx101.freemanga.mangareader.fragment.FavoritesFragment;
import com.dymx101.freemanga.mangareader.fragment.MainActivityFragment;
import com.google.android.gms.ads.MobileAds;
import com.kobakei.ratethisapp.RateThisApp;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, MyService.class));
        MobileAds.initialize(this, getString(R.string.mobileadsIntitialize));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DetailActivity instance = new DetailActivity();
        instance.sendData();
        //rate this app
        RateThisApp.init(new RateThisApp.Config(3, 3));
        RateThisApp.onCreate(this);
//        RateThisApp.showRateDialogIfNeeded(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            MainActivityFragment mainActivityFragment = new MainActivityFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, mainActivityFragment)

                    .commit();

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.do_you_want_to_exit)
                    .negativeText(R.string.no)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .positiveText(R.string.yes)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            MainActivity.super.onBackPressed();
                            finish();
                        }
                    })
                    .show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.all_manga) {
            Intent favIntent = new Intent(this, MainActivity.class);
            favIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(favIntent);
            finish();
        } else if (id == R.id.action_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Action");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.adventure_category) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Adventure");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.comedy_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Comedy");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.harem_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Harem");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.horror_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Horror");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.supernatural_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Supernatural");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.crime_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Crime");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.mystery_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Mystery");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.romance_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Romance");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.physcological) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Psychological");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.drama_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Drama");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.fantasy_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Fantasy");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        } else if (id == R.id.school_category) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "School Life");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        }else if (id == R.id.recentlyUpdated){
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Recently Updated");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();

        }else if (id==R.id.seinen){
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Seinen");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();
        }

        else if (id==R.id.shoujo){
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Shoujo");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();
        }

        else if (id==R.id.martial_arts){
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();

            Bundle bundle = new Bundle();
            bundle.putString("Category", "Martial Arts");
            categoryFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, categoryFragment)
                    .commit();
        }

        else if (id == R.id.favorites) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FavoritesFragment favoritesFragment = new FavoritesFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, favoritesFragment)
                    .commit();

        } else if (id == R.id.feedback) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.email_reports)));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_reports));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.app_name)));

        } else if (id == R.id.rate_us) {

            Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));

            }
        } else if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.sharethisapp) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this App");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + this.getPackageName());
            startActivity(Intent.createChooser(intent, "Share Via"));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}

// 打包的密码: yiming