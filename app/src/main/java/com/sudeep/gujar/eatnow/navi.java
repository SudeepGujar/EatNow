package com.sudeep.gujar.eatnow;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sudeep.gujar.eatnow.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class navi extends Fragment {

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    public navi() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navi, container, false);
    }


    public void setup(DrawerLayout drwrly, Toolbar toolbar) {
        drawerLayout = drwrly;

        drawerToggle = new ActionBarDrawerToggle(getActivity(),drwrly,toolbar,R.string.draweropen,R.string.drawerclose){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getActivity().invalidateOptionsMenu();
            }

        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
    }
}
