package com.example.hp.chatapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class controller extends FragmentPagerAdapter
{
    public controller(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {


                groupsFragment groupsFragment=new groupsFragment();
                return  groupsFragment;

        }



    @Override
    public int getCount() {
        return 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {


                return  "groups";

}}

