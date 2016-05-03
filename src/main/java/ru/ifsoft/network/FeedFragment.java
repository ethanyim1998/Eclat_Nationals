package ru.ifsoft.network;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.ifsoft.network.adapter.StreamListAdapter;
import ru.ifsoft.network.app.App;
import ru.ifsoft.network.constants.Constants;
import ru.ifsoft.network.dialogs.MyPostActionDialog;
import ru.ifsoft.network.dialogs.PostActionDialog;
import ru.ifsoft.network.dialogs.PostDeleteDialog;
import ru.ifsoft.network.dialogs.PostReportDialog;
import ru.ifsoft.network.dialogs.PostShareDialog;
import ru.ifsoft.network.model.Item;
import ru.ifsoft.network.util.Api;
import ru.ifsoft.network.util.CustomRequest;
import ru.ifsoft.network.util.ItemInterface;

public class FeedFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener, ItemInterface {

    private static final String STATE_LIST = "State Adapter Data";

    private static final int PROFILE_NEW_POST = 4;

    ListView mListView;
    TextView mMessage;

    SwipeRefreshLayout mItemsContainer;

    FloatingActionButton mFabButton;

    private ArrayList<Item> itemsList;
    private StreamListAdapter itemsAdapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new StreamListAdapter(getActivity(), itemsList, this);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");

        } else {

            itemsList = new ArrayList<Item>();
            itemsAdapter = new StreamListAdapter(getActivity(), itemsList, this);

            restore = false;
            itemId = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) rootView.findViewById(R.id.message);

        mFabButton = (FloatingActionButton) rootView.findViewById(R.id.fabButton);
        mFabButton.setImageResource(R.drawable.ic_action_new);

        mListView = (ListView) rootView.findViewById(R.id.listView);

        mFabButton.attachToListView(mListView, new ScrollDirectionListener() {

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

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mItemsContainer.isRefreshing())) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getItems();
                    }
                }
            }
        });

        mListView.setAdapter(itemsAdapter);

        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), NewItemActivity.class);
                startActivityForResult(intent, FEED_NEW_POST);
            }
        });

        if (itemsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;
            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FEED_NEW_POST && resultCode == getActivity().RESULT_OK && null != data) {

            itemId = 0;
            getItems();

        } else if (requestCode == ITEM_EDIT && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setPost(data.getStringExtra("post"));
            item.setImgUrl(data.getStringExtra("imgUrl"));

            itemsAdapter.notifyDataSetChanged();

        } else if (requestCode == ITEM_REPOST && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setMyRePost(true);
            item.setRePostsCount(item.getRePostsCount() + 1);

            itemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FEEDS_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!loadingMore) {

                            itemsList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Integer.toString(itemId));
                params.put("language", "en");

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

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getCount() == 0) {

            if (FeedFragment.this.isVisible()) {

                showMessage(getText(R.string.label_empty_list).toString());
            }

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
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

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostDeleteDialog alert = new PostDeleteDialog();

        Bundle b  = new Bundle();
        b.putInt("position", position);

        alert.setArguments(b);
        alert.show(fm, "alert_dialog_post_delete");
    }

    public void onPostDelete(int position) {

        final Item item = itemsList.get(position);

        itemsList.remove(position);
        itemsAdapter.notifyDataSetChanged();

        if (mListView.getAdapter().getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
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

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}