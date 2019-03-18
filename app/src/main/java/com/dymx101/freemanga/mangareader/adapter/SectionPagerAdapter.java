package com.dymx101.freemanga.mangareader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dymx101.freemanga.mangareader.fragment.ChaptersFragment;
import com.dymx101.freemanga.mangareader.fragment.InfroFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new InfroFragment();

            case 1:
                return new ChaptersFragment();

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "INFO";
            case 1:
                return "CHAPTERS";
            default:
                return null;
        }

    }
}
