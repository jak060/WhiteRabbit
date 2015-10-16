package com.whiterabbitt.whiterabbitt;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentPageAdapter extends FragmentPagerAdapter{

    public FragmentPageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return EventFragment.newInstance("Page # 1", 0);
            case 1:
                return ContactListFragment.newInstance("Page # 2", 1);
            case 2:
                return MyProfileFragment.newInstance("Page # 3", 2);
            case 3:
                return HistoryFragment.newInstance("Page # 4", 3);
            default:
                break;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Events";
            case 1:
                return "Friends";
            case 2:
                return "My Profile";
            case 3:
                return "History";
        }
        return "";
    }
}
