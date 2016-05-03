package ru.ifsoft.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.pkmmte.view.CircularImageView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.ifsoft.network.adapter.ProfileListAdapter;
import ru.ifsoft.network.app.App;
import ru.ifsoft.network.constants.Constants;
import ru.ifsoft.network.dialogs.CoverChooseDialog;
import ru.ifsoft.network.dialogs.MyPostActionDialog;
import ru.ifsoft.network.dialogs.PhotoChooseDialog;
import ru.ifsoft.network.dialogs.PostActionDialog;
import ru.ifsoft.network.dialogs.PostDeleteDialog;
import ru.ifsoft.network.dialogs.PostReportDialog;
import ru.ifsoft.network.dialogs.PostShareDialog;
import ru.ifsoft.network.dialogs.ProfileBlockDialog;
import ru.ifsoft.network.dialogs.ProfileReportDialog;
import ru.ifsoft.network.model.Item;
import ru.ifsoft.network.model.Profile;
import ru.ifsoft.network.util.Api;
import ru.ifsoft.network.util.CustomRequest;
import ru.ifsoft.network.util.ItemInterface;

public class ProfileFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener, ItemInterface {

    private static final String STATE_LIST = "State Adapter Data";

    private ProgressDialog pDialog;

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private static final int SELECT_PHOTO = 1;
    private static final int SELECT_COVER = 2;
    private static final int PROFILE_EDIT = 3;
    private static final int PROFILE_NEW_POST = 4;
    private static final int CREATE_PHOTO = 5;
    private static final int CREATE_COVER = 6;

    String [] names = {};

    Toolbar mToolbar;

    Button profileActionBtn;

    TextView profileFullname, profileUsername, profileFollowersCount, profileFollowingsCount, profileItemsCount, profileLikesCount, mProfileWallMsg, mProfileErrorScreenMsg, mProfileDisabledScreenMsg;
    TextView mItemsCount, mLikesCount, mFollowersCount, mFollowingsCount, mProfileLocation, mProfileActive, mProfileFacebookPage, mProfileInstagramPage, mProfileBio;

    SwipeRefreshLayout mProfileContentScreen;
    RelativeLayout mProfileLoadingScreen, mProfileErrorScreen, mProfileDisabledScreen;
    LinearLayout mProfileContainer;

    FloatingActionButton mFabButton;

    ListView profileListView;
    View profileListViewHeader;

    ImageView profileCover;
    CircularImageView profilePhoto;

    Profile profile;

    private ArrayList<Item> itemsList;

    private ProfileListAdapter adapter;

    private String selectedPhoto, selectedCover;
    private Uri outputFileUri;

    private Boolean loadingComplete = false;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;

    private String profile_mention;
    public long profile_id;
    int itemId = 0;
    int arrayLength = 0;
    int accessMode = 0;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;

    private Boolean isMainScreen = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();

        profile_id = i.getLongExtra("profileId", 0);
        profile_mention = i.getStringExtra("profileMention");

        if (profile_id == 0 && (profile_mention == null || profile_mention.length() == 0)) {

            profile_id = App.getInstance().getId();
            isMainScreen = true;
        }

        profile = new Profile();
        profile.setId(profile_id);

        itemsList = new ArrayList<Item>();
        adapter = new ProfileListAdapter(getActivity(), itemsList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            adapter = new ProfileListAdapter(getActivity(), itemsList, this);

            itemId = savedInstanceState.getInt("itemId");

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");

            profile = savedInstanceState.getParcelable("profileObj");

        } else {

            itemsList = new ArrayList<Item>();
            adapter = new ProfileListAdapter(getActivity(), itemsList, this);

            itemId = 0;

            restore = false;
            loading = false;
            preload = false;
        }

        if (loading) {


            showpDialog();
        }


        mProfileContentScreen = (SwipeRefreshLayout) rootView.findViewById(R.id.profileContentScreen);
        mProfileContentScreen.setOnRefreshListener(this);

        mProfileLoadingScreen = (RelativeLayout) rootView.findViewById(R.id.profileLoadingScreen);
        mProfileErrorScreen = (RelativeLayout) rootView.findViewById(R.id.profileErrorScreen);
        mProfileDisabledScreen = (RelativeLayout) rootView.findViewById(R.id.profileDisabledScreen);

        mProfileContainer = (LinearLayout) rootView.findViewById(R.id.profileContainer);

        mProfileErrorScreenMsg = (TextView) rootView.findViewById(R.id.profileErrorScreenMsg);
        mProfileDisabledScreenMsg = (TextView) rootView.findViewById(R.id.profileDisabledScreenMsg);

        mFabButton = (FloatingActionButton) rootView.findViewById(R.id.fabButton);

        mFabButton.setVisibility(View.GONE);

        profileListView = (ListView) rootView.findViewById(R.id.profileListView);
        mFabButton.attachToListView(profileListView, new ScrollDirectionListener() {

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onScrollUp() {

            }

        }, new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mProfileContentScreen.isRefreshing())) {

                    loadingMore = true;

                    getItems();
                }
            }
        });

        profileListViewHeader = getActivity().getLayoutInflater().inflate(R.layout.profile_listview_header, null);

        profileListView.addHeaderView(profileListViewHeader);

        profileActionBtn = (Button) profileListViewHeader.findViewById(R.id.profileActionBtn);

        profileFullname = (TextView) profileListViewHeader.findViewById(R.id.profileFullname);
        profileUsername = (TextView) profileListViewHeader.findViewById(R.id.profileUsername);
        profileFollowersCount = (TextView) profileListViewHeader.findViewById(R.id.profileFollowersCount);
        profileFollowingsCount = (TextView) profileListViewHeader.findViewById(R.id.profileFollowingsCount);
        profileItemsCount = (TextView) profileListViewHeader.findViewById(R.id.profileItemsCount);
        profileLikesCount = (TextView) profileListViewHeader.findViewById(R.id.profileLikesCount);

        mProfileActive = (TextView) profileListViewHeader.findViewById(R.id.profileActive);
        mProfileLocation = (TextView) profileListViewHeader.findViewById(R.id.profileLocation);
        mProfileFacebookPage = (TextView) profileListViewHeader.findViewById(R.id.profileFacebookPage);
        mProfileInstagramPage = (TextView) profileListViewHeader.findViewById(R.id.profileInstagramPage);
        mProfileBio = (TextView) profileListViewHeader.findViewById(R.id.profileBio);

        mItemsCount = (TextView) profileListViewHeader.findViewById(R.id.itemsCount);
        mLikesCount = (TextView) profileListViewHeader.findViewById(R.id.likesCount);
        mFollowingsCount = (TextView) profileListViewHeader.findViewById(R.id.followingsCount);
        mFollowersCount = (TextView) profileListViewHeader.findViewById(R.id.followersCount);

        mProfileFacebookPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!profile.getFacebookPage().startsWith("https://") && !profile.getFacebookPage().startsWith("http://")){

                    profile.setFacebookPage("http://" + profile.getFacebookPage());
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(profile.getFacebookPage()));
                startActivity(i);
            }
        });

        mProfileInstagramPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!profile.getInstagramPage().startsWith("https://") && !profile.getInstagramPage().startsWith("http://")){

                    profile.setInstagramPage("http://" + profile.getInstagramPage());
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(profile.getInstagramPage()));
                startActivity(i);
            }
        });

        profileFollowingsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFollowings();
            }
        });

        mFollowingsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFollowings();
            }
        });

        profileFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFollowers();
            }
        });

        mFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFollowers();
            }
        });

        mProfileWallMsg = (TextView) profileListViewHeader.findViewById(R.id.profileWallMsg);

        profilePhoto = (CircularImageView) profileListViewHeader.findViewById(R.id.profilePhoto);
        profileCover = (ImageView) profileListViewHeader.findViewById(R.id.profileCover);

        profileListView.setAdapter(adapter);

        profileActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getId() == profile.getId()) {

                    Intent i = new Intent(getActivity(), AccountSettingsActivity.class);
                    i.putExtra("profileId", App.getInstance().getId());
                    i.putExtra("sex", profile.getSex());
                    i.putExtra("year", profile.getYear());
                    i.putExtra("month", profile.getMonth());
                    i.putExtra("day", profile.getDay());
                    i.putExtra("fullname", profile.getFullname());
                    i.putExtra("location", profile.getLocation());
                    i.putExtra("facebookPage", profile.getFacebookPage());
                    i.putExtra("instagramPage", profile.getInstagramPage());
                    i.putExtra("bio", profile.getBio());
                    startActivityForResult(i, PROFILE_EDIT);

                } else {

                    addFollower();
                }
            }
        });

        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (App.getInstance().getId() == profile.getId()) {

                    Intent intent = new Intent(getActivity(), NewItemActivity.class);
                    startActivityForResult(intent, PROFILE_NEW_POST);

                } else {

                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatId", 0);
                    i.putExtra("profileId", profile.getId());
                    i.putExtra("withProfile", profile.getFullname());
                    startActivityForResult(i, PROFILE_EDIT);
                }
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (profile.getNormalPhotoUrl().length() > 0) {

                    Intent i = new Intent(getActivity(), PhotoViewActivity.class);
                    i.putExtra("imgUrl", profile.getNormalPhotoUrl());
                    startActivity(i);
                }
            }
        });

        if (profile.getFullname() == null || profile.getFullname().length() == 0) {

            if (App.getInstance().isConnected()) {

                showLoadingScreen();
                getData();

                Log.e("Profile", "OnReload");

            } else {

                showErrorScreen();
            }

        } else {

            if (App.getInstance().isConnected()) {

                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                    showContentScreen();

                    loadingComplete();
                    updateProfile();

                } else {

                    showDisabledScreen();
                }

            } else {

                showErrorScreen();
            }
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putInt("itemId", itemId);

        outState.putBoolean("restore", restore);
        outState.putBoolean("loading", loading);
        outState.putBoolean("preload", preload);

        outState.putParcelable("profileObj", profile);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            selectedPhoto = getImageUrlWithAuthority(getActivity(), selectedImage, "photo.jpg");

            if (selectedPhoto != null) {

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "photo.jpg");

                uploadFile(METHOD_PROFILE_UPLOADPHOTO, f, UPLOAD_TYPE_PHOTO);
            }

        } else if (requestCode == SELECT_COVER && resultCode == getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            selectedCover = getImageUrlWithAuthority(getActivity(), selectedImage, "cover.jpg");

            if (selectedCover != null) {

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "cover.jpg");

                uploadFile(METHOD_PROFILE_UPLOADCOVER, f, UPLOAD_TYPE_COVER);
            }

        } else if (requestCode == PROFILE_EDIT && resultCode == getActivity().RESULT_OK) {

            profile.setFullname(data.getStringExtra("fullname"));
            profile.setLocation(data.getStringExtra("location"));
            profile.setFacebookPage(data.getStringExtra("facebookPage"));
            profile.setInstagramPage(data.getStringExtra("instagramPage"));
            profile.setBio(data.getStringExtra("bio"));

            profile.setSex(data.getIntExtra("sex", 0));

            profile.setYear(data.getIntExtra("year", 0));
            profile.setMonth(data.getIntExtra("month", 0));
            profile.setDay(data.getIntExtra("day", 0));

            updateProfile();

        } else if (requestCode == PROFILE_NEW_POST && resultCode == getActivity().RESULT_OK) {

            getData();

        } else if (requestCode == CREATE_PHOTO && resultCode == getActivity().RESULT_OK) {

            try {

                selectedPhoto = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "photo.jpg";

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "photo.jpg");

                uploadFile(METHOD_PROFILE_UPLOADPHOTO, f, UPLOAD_TYPE_PHOTO);

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == CREATE_COVER && resultCode == getActivity().RESULT_OK) {

            try {

                selectedCover = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "cover.jpg";

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "cover.jpg");

                uploadFile(METHOD_PROFILE_UPLOADCOVER, f, UPLOAD_TYPE_COVER);

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == ITEM_EDIT && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setPost(data.getStringExtra("post"));
            item.setImgUrl(data.getStringExtra("imgUrl"));

            adapter.notifyDataSetChanged();

        } else if (requestCode == ITEM_REPOST && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setMyRePost(true);
            item.setRePostsCount(item.getRePostsCount() + 1);

            adapter.notifyDataSetChanged();
        }
    }

    public void choicePhoto() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PhotoChooseDialog alert = new PhotoChooseDialog();

        alert.show(fm, "alert_dialog_photo_choose");
    }

    public void photoFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_PHOTO);
    }

    public void photoFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "photo.jpg");
            outputFileUri = Uri.fromFile(sdImageMainDirectory);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, CREATE_PHOTO);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void choiceCover() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        CoverChooseDialog alert = new CoverChooseDialog();

        alert.show(fm, "alert_dialog_cover_choose");
    }

    public void coverFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_COVER);
    }

    public void coverFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "cover.jpg");
            outputFileUri = Uri.fromFile(sdImageMainDirectory);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, CREATE_COVER);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getImageUrlWithAuthority(Context context, Uri uri, String fileName) {

        InputStream is = null;

        if (uri.getAuthority() != null) {

            try {

                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);

                return writeToTempImageAndGetPathUri(context, bmp, fileName).toString();

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } finally {

                try {

                    if (is != null) {

                        is.close();
                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage, String fileName) {

        String file_path = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER;
        File dir = new File(file_path);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, fileName);

        try {

            FileOutputStream fos = new FileOutputStream(file);

            inImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {

            Toast.makeText(inContext, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "photo.jpg";
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            getData();

        } else {

            mProfileContentScreen.setRefreshing(false);
        }
    }

    public void getFollowings() {

        Intent intent = new Intent(getActivity(), FollowingsActivity.class);
        intent.putExtra("profileId", profile.getId());
        startActivity(intent);
    }

    public void getFollowers() {

        Intent intent = new Intent(getActivity(), FollowersActivity.class);
        intent.putExtra("profileId", profile.getId());
        startActivity(intent);
    }

    public void updateProfile() {

        if (profile.getLastActive() == 0) {

            mProfileActive.setText(getString(R.string.label_offline));

        } else {

            if (profile.isOnline()) {

                mProfileActive.setText(getString(R.string.label_online));

            } else {

                mProfileActive.setText(profile.getLastActiveTimeAgo());
            }
        }

        profileUsername.setText("@" + profile.getUsername());
        mProfileLocation.setText(profile.getLocation());
        mProfileFacebookPage.setText(profile.getFacebookPage());
        mProfileInstagramPage.setText(profile.getInstagramPage());
        mProfileBio.setText(profile.getBio());

        // hide follow button is your profile
        if (profile.getId() == App.getInstance().getId()) {

//            profileMsgBtn.setVisibility(View.GONE);
            profileActionBtn.setText(R.string.action_profile_edit);
            mFabButton.setImageResource(R.drawable.ic_action_new);

            mFabButton.setVisibility(View.VISIBLE);

        } else {

            if (!profile.isInBlackList()) {

                if ((profile.getAllowMessages() == 1) || (profile.getAllowMessages() == 0 && profile.isFollower())) {

                    mFabButton.setImageResource(R.drawable.ic_action_mail);

                    mFabButton.setVisibility(View.VISIBLE);
                }
            }
        }

        if (profile.getLocation() != null && profile.getLocation().length() != 0) {

            mProfileLocation.setVisibility(View.VISIBLE);

        } else {

            mProfileLocation.setVisibility(View.GONE);
        }

        if (profile.getFacebookPage() != null && profile.getFacebookPage().length() != 0) {

            mProfileFacebookPage.setVisibility(View.VISIBLE);

        } else {

            mProfileFacebookPage.setVisibility(View.GONE);
        }

        if (profile.getInstagramPage() != null && profile.getInstagramPage().length() != 0) {

            mProfileInstagramPage.setVisibility(View.VISIBLE);

        } else {

            mProfileInstagramPage.setVisibility(View.GONE);
        }

        if (profile.getBio() != null && profile.getBio().length() != 0) {

            mProfileBio.setVisibility(View.VISIBLE);

        } else {

            mProfileBio.setVisibility(View.GONE);
        }

        updateFullname();
        updateFollowersCount();
        updateItemsCount();
        updateFollowingsCount();
        updateLikesCount();

        showPhoto(profile.getLowPhotoUrl());
        showCover(profile.getNormalCoverUrl());

        showContentScreen();

        if (this.isVisible()) {

            try {

                getActivity().invalidateOptionsMenu();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void updateFullname() {

        if (profile.getFullname().length() == 0) {

            profileFullname.setText(profile.getUsername());
            if (!isMainScreen) getActivity().setTitle(profile.getUsername());

        } else {

            profileFullname.setText(profile.getFullname());
            if (!isMainScreen) getActivity().setTitle(profile.getFullname());
        }

        if (!profile.isVerify()) {

            profileFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void updateFollowingsCount() {

        mFollowingsCount.setText(Integer.toString(profile.getFollowingsCount()));
    }

    private void updateFollowersCount() {

        mFollowersCount.setText(Integer.toString(profile.getFollowersCount()));
    }

    private void updateLikesCount() {

        mLikesCount.setText(Integer.toString(profile.getLikesCount()));
    }

    private void updateItemsCount() {

        if (profile.getItemsCount() == 0) {

            mProfileWallMsg.setVisibility(View.VISIBLE);

            if (App.getInstance().getId() == profile.getId()) {

                mProfileWallMsg.setText(getText(R.string.label_you_havent_items));

            } else {

                mProfileWallMsg.setText(getText(R.string.label_user_havent_items));
            }

            mLikesCount.setText("0");

        } else {

            mProfileWallMsg.setVisibility(View.GONE);
        }

        mItemsCount.setText(Integer.toString(profile.getItemsCount()));
    }

    public void updateFollowButton(Boolean follow) {

        updateFollowersCount();

        if (follow) {

            profileActionBtn.setText(R.string.action_unfollow);

        } else {

            profileActionBtn.setText(R.string.action_follow);
        }
    }

    public void getData() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                profile = new Profile(response);

                                changeAccessMode();

                                if (profile.getItemsCount() > 0) {

                                    getItems();
                                }

                                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                                    showContentScreen();

                                    updateFollowButton(profile.isFollow());
                                    updateProfile();

                                } else {

                                    showDisabledScreen();
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                showErrorScreen();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void addFollower() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_FOLLOW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                profile.setFollowersCount(response.getInt("followersCount"));

                                updateFollowButton(response.getBoolean("follow"));

                                changeAccessMode();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void changeAccessMode() {

        if (App.getInstance().getId() == profile.getId() || profile.isFollow()) {

            accessMode = 1;

        } else {

            accessMode = 0;
        }
    }

    public void showPhoto(String photoUrl) {

        if (photoUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(photoUrl, ImageLoader.getImageListener(profilePhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }
    }

    public void showCover(String coverUrl) {

        if (coverUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(coverUrl, ImageLoader.getImageListener(profileCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));

            if (Build.VERSION.SDK_INT > 15) {

                profileCover.setImageAlpha(200);
            }
        }
    }

    public void getItems() {

        if (loadingMore) {

            mProfileContentScreen.setRefreshing(true);

        } else{

            itemId = 0;
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_WALL_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!loadingMore) {

                                itemsList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("postId");

                                if (response.has("posts")) {

                                    JSONArray itemsArray = response.getJSONArray("posts");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            Item item = new Item(itemObj);

                                            itemsList.add(item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile.getId()));
                params.put("itemId", Integer.toString(itemId));
                params.put("accessMode", Integer.toString(accessMode));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        adapter.notifyDataSetChanged();

        mProfileContentScreen.setRefreshing(false);

        loadingMore = false;

//        if (this.isVisible()) getActivity().invalidateOptionsMenu();
    }

    public void showLoadingScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_profile));

        mProfileContainer.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);

        mProfileLoadingScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showErrorScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_profile));

        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileContainer.setVisibility(View.GONE);

        mProfileErrorScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showDisabledScreen() {

        if (profile.getState() != ACCOUNT_STATE_ENABLED) {

            mProfileDisabledScreenMsg.setText(getText(R.string.msg_account_blocked));
        }

        getActivity().setTitle(getText(R.string.label_account_disabled));

        mProfileContainer.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);

        mProfileDisabledScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showContentScreen() {

        if (!isMainScreen) {

            getActivity().setTitle(profile.getFullname());
        }

        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);

        mProfileContainer.setVisibility(View.VISIBLE);
        mProfileContentScreen.setVisibility(View.VISIBLE);
        mProfileContentScreen.setRefreshing(false);

        loadingComplete = true;
        restore = true;
    }

    public void report(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostReportDialog alert = new PostReportDialog();

        Bundle b  = new Bundle();
        b.putInt("position", position);
        b.putInt("reason", 0);

        alert.setArguments(b);
        alert.show(fm, "alert_dialog_post_report");
    }

    public void onPostReport(int position, int reasonId) {

        final Item item = itemsList.get(position);

        if (App.getInstance().isConnected()) {

            Api api = new Api(getActivity());

            api.postReport(item.getId(), reasonId);

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void remove(int position) {

        /** Getting the fragment manager */
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        /** Instantiating the DialogFragment class */
        PostDeleteDialog alert = new PostDeleteDialog();

        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("position", position);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */

        alert.show(fm, "alert_dialog_post_delete");
    }

    public void onPostDelete(final int position) {

        final Item item = itemsList.get(position);

        itemsList.remove(position);
        adapter.notifyDataSetChanged();

        profile.setItemsCount(profile.getItemsCount() - 1);

        updateProfile();

        if (profileListView.getAdapter().getCount() == 0) {

//            showEmptyScreen();

        } else {

            showContentScreen();
        }

        if (App.getInstance().isConnected()) {

            Api api = new Api(getActivity());

            api.postDelete(item.getId());

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void onPostRemove(final int position) {

        /** Getting the fragment manager */
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        /** Instantiating the DialogFragment class */
        PostDeleteDialog alert = new PostDeleteDialog();

        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("position", position);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */

        alert.show(fm, "alert_dialog_post_delete");
    }

    public void onPostEdit(final int position) {

        Item item = itemsList.get(position);

        Intent i = new Intent(getActivity(), EditItemActivity.class);
        i.putExtra("position", position);
        i.putExtra("postId", item.getId());
        i.putExtra("post", item.getPost());
        i.putExtra("imgUrl", item.getImgUrl());
        startActivityForResult(i, ITEM_EDIT);
    }

    public void onPostCopyLink(final int position) {

        final Item item = itemsList.get(position);

        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("post url", item.getLink());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getActivity(), getText(R.string.msg_post_link_copied), Toast.LENGTH_SHORT).show();
    }

    public void action(int position) {

        final Item item = itemsList.get(position);

        if (item.getFromUserId() == App.getInstance().getId()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            MyPostActionDialog alert = new MyPostActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_my_post_action");

        } else {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            PostActionDialog alert = new PostActionDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putInt("position", position);

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_post_action");
        }
    }

    public void repost(int position) {

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostShareDialog alert = new PostShareDialog();

        Bundle b  = new Bundle();

        b.putInt("position", position);

        alert.setArguments(b);

        alert.show(fm, "alert_repost_action");
    }

    public void onPostRePost(final int position) {

        Item item = itemsList.get(position);

        long rePostId = item.getId();

        if (item.getRePostId() != 0) {

            rePostId = item.getRePostId();
        }

        Intent i = new Intent(getActivity(), RePostItemActivity.class);
        i.putExtra("position", position);
        i.putExtra("rePostId", rePostId);
        startActivityForResult(i, ITEM_REPOST);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.menu_profile, menu);

//        MainMenu = menu;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        if (loadingComplete) {

            if (profile.getState() != ACCOUNT_STATE_ENABLED) {

                //hide all menu items
                hideMenuItems(menu, false);
            }

            if (App.getInstance().getId() != profile.getId()) {

                MenuItem menuItem = menu.findItem(R.id.action_profile_block);

                if (profile.isBlocked()) {

                    menuItem.setTitle(getString(R.string.action_unblock));

                } else {

                    menuItem.setTitle(getString(R.string.action_block));
                }

                menu.removeItem(R.id.action_profile_edit_photo);
                menu.removeItem(R.id.action_profile_edit_cover);

            } else {

                // your profile

                menu.removeItem(R.id.action_profile_report);
                menu.removeItem(R.id.action_profile_block);
            }

            //show all menu items
            hideMenuItems(menu, true);

        } else {

            //hide all menu items
            hideMenuItems(menu, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_profile_refresh: {

                profileListView.smoothScrollToPosition(0);
                mProfileContentScreen.setRefreshing(true);
                onRefresh();

                return true;
            }

            case R.id.action_profile_block: {

                profileBlock();

                return true;
            }

            case R.id.action_profile_report: {

                profileReport();

                return true;
            }

            case R.id.action_profile_edit_photo: {

                choicePhoto();

                return true;
            }

            case R.id.action_profile_edit_cover: {

                choiceCover();

                return true;
            }

            case R.id.action_profile_copy_url: {

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(profile.getUsername(), API_DOMAIN + profile.getUsername());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getText(R.string.msg_profile_link_copied), Toast.LENGTH_SHORT).show();

                return true;
            }

            case R.id.action_profile_open_url: {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(API_DOMAIN + profile.getUsername()));
                startActivity(i);

                return true;
            }

            case R.id.action_profile_settings: {

                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void hideMenuItems(Menu menu, boolean visible) {

        for (int i = 0; i < menu.size(); i++){

            menu.getItem(i).setVisible(visible);
        }
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void profileReport() {

        /** Getting the fragment manager */
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        /** Instantiating the DialogFragment class */
        ProfileReportDialog alert = new ProfileReportDialog();

        /** Creating a bundle object to store the selected item's index */
        Bundle b  = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("position", 0);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */

        alert.show(fm, "alert_dialog_profile_report");
    }

    public  void onProfileReport(final int position) {

        Api api = new Api(getActivity());

        api.profileReport(profile.getId(), position);
    }

    public void profileBlock() {

        if (!profile.isBlocked()) {

            /** Getting the fragment manager */
            android.app.FragmentManager fm = getActivity().getFragmentManager();

            /** Instantiating the DialogFragment class */
            ProfileBlockDialog alert = new ProfileBlockDialog();

            /** Creating a bundle object to store the selected item's index */
            Bundle b  = new Bundle();

            /** Storing the selected item's index in the bundle object */
            b.putString("blockUsername", profile.getUsername());

            /** Setting the bundle object to the dialog fragment object */
            alert.setArguments(b);

            /** Creating the dialog fragment object, which will in turn open the alert dialog window */

            alert.show(fm, "alert_dialog_profile_block");

        } else {

            loading = true;

            showpDialog();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_REMOVE, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {

                                    profile.setBlocked(false);

                                    Toast.makeText(getActivity(), getString(R.string.msg_profile_removed_from_blacklist), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                loading = false;

                                hidepDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    loading = false;

                    hidepDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("profileId", Long.toString(profile.getId()));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public  void onProfileBlock() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_ADD, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                profile.setBlocked(true);

                                Toast.makeText(getActivity(), getString(R.string.msg_profile_added_to_blacklist), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile.getId()));
                params.put("reason", "example");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public Boolean uploadFile(String serverURL, File file, final int type) {

        loading = true;

        showpDialog();

        final OkHttpClient client = new OkHttpClient();

        try {

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                    .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {

                    loading = false;

                    hidepDialog();

                    Log.e("failure", request.toString());
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                    String jsonData = response.body().string();

                    Log.e("response", jsonData);

                    try {

                        JSONObject result = new JSONObject(jsonData);

                        if (!result.getBoolean("error")) {

                            switch (type) {

                                case 0: {

                                    profile.setLowPhotoUrl(result.getString("lowPhotoUrl"));
                                    profile.setBigPhotoUrl(result.getString("bigPhotoUrl"));
                                    profile.setNormalPhotoUrl(result.getString("normalPhotoUrl"));

                                    App.getInstance().setPhotoUrl(result.getString("lowPhotoUrl"));

                                    break;
                                }

                                default: {

                                    profile.setNormalCoverUrl(result.getString("normalCoverUrl"));

                                    App.getInstance().setCoverUrl(result.getString("normalCoverUrl"));

                                    break;
                                }
                            }
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + response.body().string() + "\"");

                    } finally {

                        loading = false;

                        hidepDialog();

                        getData();
                    }

                }
            });

            return true;

        } catch (Exception ex) {
            // Handle the error

            loading = false;

            hidepDialog();
        }

        return false;
    }
}