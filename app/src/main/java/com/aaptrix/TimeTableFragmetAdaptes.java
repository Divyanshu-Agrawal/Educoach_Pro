package com.aaptrix;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aaptrix.fragments.ClassTimeTableFragment;
import com.aaptrix.fragments.ExamTimeTableFragment;

/**
 * Created by google on 5/11/17.
 */

public class TimeTableFragmetAdaptes extends FragmentPagerAdapter {

    private Context mContext;
    private String loc;

    public TimeTableFragmetAdaptes(Context context, FragmentManager fm, String loc) {
        super(fm);
        mContext = context;
        this.loc = loc;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Fragment fragment = new ClassTimeTableFragment();
            Bundle bundle = new Bundle();
            bundle.putString("loc", loc);
            fragment.setArguments(bundle);
            return fragment;
        } else {
            return new ExamTimeTableFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Class Time Table";
            case 1:
                return "Exam Time Table";
            default:
                return null;
        }
    }
}
