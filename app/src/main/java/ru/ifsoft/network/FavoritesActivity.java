package ru.ifsoft.network;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.ifsoft.network.common.ActivityBase;
import ru.ifsoft.network.dialogs.MyPostActionDialog;
import ru.ifsoft.network.dialogs.PostActionDialog;
import ru.ifsoft.network.dialogs.PostDeleteDialog;
import ru.ifsoft.network.dialogs.PostReportDialog;

public class FavoritesActivity extends Activity {

    Toolbar mToolbar;

    Fragment fragment;
    Boolean restore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorites);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.setTitleTextColor(Color.WHITE);

        mToolbar.setTitle("FBLA Dress Code");
    }


}