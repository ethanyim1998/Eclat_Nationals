package ru.ifsoft.network;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.ifsoft.network.common.ActivityBase;
import ru.ifsoft.network.dialogs.CoverChooseDialog;
import ru.ifsoft.network.dialogs.MyPostActionDialog;
import ru.ifsoft.network.dialogs.PhotoChooseDialog;
import ru.ifsoft.network.dialogs.PostActionDialog;
import ru.ifsoft.network.dialogs.PostDeleteDialog;
import ru.ifsoft.network.dialogs.PostReportDialog;
import ru.ifsoft.network.dialogs.PostShareDialog;
import ru.ifsoft.network.dialogs.ProfileBlockDialog;
import ru.ifsoft.network.dialogs.ProfileReportDialog;


public class ProfileActivity extends ActivityBase implements PhotoChooseDialog.AlertPositiveListener, CoverChooseDialog.AlertPositiveListener, ProfileReportDialog.AlertPositiveListener, ProfileBlockDialog.AlertPositiveListener, PostDeleteDialog.AlertPositiveListener, PostReportDialog.AlertPositiveListener, MyPostActionDialog.AlertPositiveListener, PostActionDialog.AlertPositiveListener, PostShareDialog.AlertPositiveListener {

    Toolbar mToolbar;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment = new ProfileFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        fragment.onActivityResult(requestCode, resultCode, data);
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

        ProfileFragment p = (ProfileFragment) fragment;
        p.onPostRePost(position);
    }

    @Override
    public void onPostDelete(int position) {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onPostDelete(position);
    }

    @Override
    public void onPostReport(int position, int reasonId) {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onPostReport(position, reasonId);
    }

    @Override
    public void onPostRemove(int position) {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onPostRemove(position);
    }

    @Override
    public void onPostEdit(int position) {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onPostEdit(position);
    }

    @Override
    public void onPostCopyLink(int position) {

        ProfileFragment p = (ProfileFragment) fragment;
        p.onPostCopyLink(position);
    }

    @Override
    public void onPostReportDialog(int position) {

        ProfileFragment p = (ProfileFragment) fragment;
        p.report(position);
    }

    @Override
    public void onBackPressed(){

        finish();
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

                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }
}
