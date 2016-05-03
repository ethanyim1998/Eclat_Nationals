package ru.ifsoft.network;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.ifsoft.network.adapter.NotifyListAdapter;
import ru.ifsoft.network.app.App;
import ru.ifsoft.network.constants.Constants;
import ru.ifsoft.network.model.Notify;
import ru.ifsoft.network.util.CustomRequest;

public class NotificationsFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    ListView mListView;
    TextView mMessage;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<Notify> notificationsList;
    private NotifyListAdapter notificationsAdapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            notificationsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            notificationsAdapter = new NotifyListAdapter(getActivity(), notificationsList);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");

        } else {

            notificationsList = new ArrayList<Notify>();
            notificationsAdapter = new NotifyListAdapter(getActivity(), notificationsList);

            restore = false;
            itemId = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) rootView.findViewById(R.id.message);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mListView.setAdapter(notificationsAdapter);

        if (notificationsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Notify notify = (Notify) adapterView.getItemAtPosition(position);

                switch (notify.getType()) {

                    case NOTIFY_TYPE_FOLLOWER: {

                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("profileId", notify.getFromUserId());
                        startActivity(intent);

                        break;
                    }

                    case NOTIFY_TYPE_LIKE: {

                        Intent intent = new Intent(getActivity(), ViewItemActivity.class);
                        intent.putExtra("itemId", notify.getItemId());
                        startActivity(intent);

                        break;
                    }

                    default: {

                        Intent intent = new Intent(getActivity(), ViewItemActivity.class);
                        intent.putExtra("itemId", notify.getItemId());
                        startActivity(intent);

                        break;
                    }
                }
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mItemsContainer.isRefreshing())) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getNotifications();
                    }
                }
            }
        });

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getNotifications();
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;
            getNotifications();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, notificationsList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getNotifications() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_NOTIFICATIONS_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            arrayLength = 0;

                            if (!loadingMore) {

                                notificationsList.clear();
                            }

                            if (!response.getBoolean("error")) {

                                App.getInstance().setNotificationsCount(0);

                                itemId = response.getInt("notifyId");

                                JSONArray notificationsArray = response.getJSONArray("notifications");

                                arrayLength = notificationsArray.length();

                                if (arrayLength > 0) {

                                    for (int i = 0; i < notificationsArray.length(); i++) {

                                        JSONObject notifyObj = (JSONObject) notificationsArray.get(i);

                                        Notify notify = new Notify(notifyObj);

                                        notificationsList.add(notify);
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
//                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("notifyId", Integer.toString(itemId));

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

        notificationsAdapter.notifyDataSetChanged();

        if (notificationsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
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