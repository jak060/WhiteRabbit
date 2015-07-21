package com.example.whiterabbit;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentPageAdapter extends FragmentPagerAdapter{
    private String indicator;

    public FragmentPageAdapter(FragmentManager fragmentManager, String indicator) {
        super(fragmentManager);
        this.indicator = indicator;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return EventFragment.newInstance("Page # 1", 0, indicator);
            case 1:
                return ContactListFragment.newInstance("Page # 2", 1);
            case 2:
                return InviteFragment.newInstance("Page # 3", 2);
            default:
                break;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
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
                return "Invite";
        }
        return "";
    }
}
