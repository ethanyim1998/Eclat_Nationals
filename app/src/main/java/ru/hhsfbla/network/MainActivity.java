package ru.hhsfbla.network;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ru.hhsfbla.network.common.ActivityBase;
import ru.hhsfbla.network.dialogs.CoverChooseDialog;
import ru.hhsfbla.network.dialogs.MyPostActionDialog;
import ru.hhsfbla.network.dialogs.PhotoChooseDialog;
import ru.hhsfbla.network.dialogs.PostActionDialog;
import ru.hhsfbla.network.dialogs.PostDeleteDialog;
import ru.hhsfbla.network.dialogs.PostReportDialog;
import ru.hhsfbla.network.dialogs.PostShareDialog;
import ru.hhsfbla.network.dialogs.ProfileBlockDialog;
import ru.hhsfbla.network.dialogs.ProfileReportDialog;


public class MainActivity extends ActivityBase implements FragmentDrawer.FragmentDrawerListener, PhotoChooseDialog.AlertPositiveListener, CoverChooseDialog.AlertPositiveListener, ProfileReportDialog.AlertPositiveListener, ProfileBlockDialog.AlertPositiveListener, PostDeleteDialog.AlertPositiveListener, PostReportDialog.AlertPositiveListener, MyPostActionDialog.AlertPositiveListener, PostActionDialog.AlertPositiveListener, PostShareDialog.AlertPositiveListener {

    Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    // used to store app title
    private CharSequence mTitle;


    Fragment fragment;
    Boolean action = false;
    int page = 0;

    private Boolean restore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {

            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");
            mTitle = savedInstanceState.getString("mTitle");

        } else {

            fragment = new FeedFragment();

            restore = false;
            mTitle = getString(R.string.app_name);
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mTitle);

        drawerFragment = (FragmentDrawer) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        if (!restore) {

            displayView(2);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putString("mTitle", getSupportActionBar().getTitle().toString());
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    public void onPhotoFromGallery() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.photoFromGallery();
    }

    @Override
    public void onPhotoFromCamera() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.photoFromCamera();
    }

    @Override
    public void onCoverFromGallery() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.coverFromGallery();
    }

    @Override
    public void onCoverFromCamera() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.coverFromCamera();
    }

    @Override
    public void onProfileReport(int position) {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onProfileReport(position);
    }

    @Override
    public void onProfileBlock() {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onProfileBlock();
    }

    @Override
    public void onPostRePost(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            case 2: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            /*
            case 3: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            case 4: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostRePost(position);

                break;
            }

            case 8: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostRePost(position);

                break;
            }
            */

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostDelete(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            case 2: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            /*
            case 3: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            case 4: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostDelete(position);

                break;
            }

            case 8: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostDelete(position);

                break;
            }
            */

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostRemove(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            case 2: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            /*
            case 3: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            case 4: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostRemove(position);

                break;
            }

            case 8: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostRemove(position);

                break;
            }
            */

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostEdit(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            case 2: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            /*
            case 3: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            case 4: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostEdit(position);

                break;
            }

            case 8: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostEdit(position);

                break;
            }
            */

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostCopyLink(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            case 2: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            /*
            case 3: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            case 4: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }

            case 8: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostCopyLink(position);

                break;
            }
            */

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostReportDialog(int position) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.report(position);

                break;
            }

            case 2: {

                StreamFragment p = (StreamFragment) fragment;
                p.report(position);

                break;
            }

            /*
            case 3: {

                StreamFragment p = (StreamFragment) fragment;
                p.report(position);

                break;
            }

            case 4: {

                PopularFragment p = (PopularFragment) fragment;
                p.report(position);

                break;
            }


            case 8: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.report(position);

                break;
            }
            */

            default: {

                break;
            }
        }
    }

    @Override
    public void onPostReport(int position, int reasonId) {

        switch (page) {

            case 1: {

                ProfileFragment p = (ProfileFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            case 2: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            /*
            case 3: {

                StreamFragment p = (StreamFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            case 4: {

                PopularFragment p = (PopularFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }

            case 8: {

                FavoritesFragment p = (FavoritesFragment) fragment;
                p.onPostReport(position, reasonId);

                break;
            }
            */

            default: {

                break;
            }
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        displayView(position);
    }

    private void displayView(int position) {

        switch (position) {

            case 0: {

                break;
            }

            case 1: {

                page = 1;

                fragment = new ProfileFragment();
                getSupportActionBar().setTitle(R.string.page_1);

                action = true;

                break;
            }

            case 2: {

                page = 2;

                fragment = new StreamFragment();
                getSupportActionBar().setTitle(R.string.page_2);

                action = true;

                break;
            }

            case 3: {

                page =3;

                fragment = new FriendsFragment();
                getSupportActionBar().setTitle(R.string.page_3);

                action = true;

                break;
            }

            case 4: {

                page = 4;

                fragment = new DialogsFragment();
                getSupportActionBar().setTitle(R.string.page_4);

                action = true;

                break;
            }

            case 5: {

                page = 5;

                fragment = new NotificationsFragment();
                getSupportActionBar().setTitle(R.string.page_5);

                action = true;

                break;
            }

            case 6: {

                page = 6;

                fragment = new FavoritesFragment();
                getSupportActionBar().setTitle(R.string.page_6);

                action = true;

                break;
            }

            case 7: {

                page = 7;

                fragment = new SearchFragment();
                getSupportActionBar().setTitle(R.string.page_7);

                action = true;

                break;
            }

            default: {

                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        }

        if (action) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_body, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (drawerFragment.isDrawerOpen()) {

            drawerFragment.closeDrawer();

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
}
