package ru.ifsoft.network;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.ifsoft.network.common.ActivityBase;
import ru.ifsoft.network.dialogs.CommentActionDialog;
import ru.ifsoft.network.dialogs.CommentDeleteDialog;
import ru.ifsoft.network.dialogs.MyCommentActionDialog;
import ru.ifsoft.network.dialogs.MyPostActionDialog;
import ru.ifsoft.network.dialogs.PostActionDialog;
import ru.ifsoft.network.dialogs.PostDeleteDialog;
import ru.ifsoft.network.dialogs.PostReportDialog;
import ru.ifsoft.network.dialogs.PostShareDialog;


public class ViewItemActivity extends ActivityBase implements CommentDeleteDialog.AlertPositiveListener, PostDeleteDialog.AlertPositiveListener, PostReportDialog.AlertPositiveListener, MyPostActionDialog.AlertPositiveListener, PostActionDialog.AlertPositiveListener, CommentActionDialog.AlertPositiveListener, MyCommentActionDialog.AlertPositiveListener, PostShareDialog.AlertPositiveListener {

    Toolbar mToolbar;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment = new ViewItemFragment();
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
    protected void onPause() {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.hideEmojiKeyboard();

        super.onPause();
    }

    @Override
    public void onPostRePost(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onPostRePost(position);
    }

    @Override
    public void onPostDelete(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onPostDelete(position);
    }

    @Override
    public void onPostReport(int position, int reasonId) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onPostReport(position, reasonId);
    }

    @Override
    public void onPostRemove(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onPostRemove(position);
    }

    @Override
    public void onPostEdit(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onPostEdit(position);
    }

    @Override
    public void onPostCopyLink(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onPostCopyLink(position);
    }

    @Override
    public void onPostReportDialog(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.report(position);
    }

    @Override
    public void onCommentRemove(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onCommentRemove(position);
    }

    @Override
    public void onCommentDelete(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onCommentDelete(position);
    }

    @Override
    public void onCommentReply(int position) {

        ViewItemFragment p = (ViewItemFragment) fragment;
        p.onCommentReply(position);
    }

    @Override
    public void onBackPressed(){

        finish();
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
