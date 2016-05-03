package ru.ifsoft.network;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.ifsoft.network.adapter.PeopleListAdapter;
import ru.ifsoft.network.app.App;
import ru.ifsoft.network.constants.Constants;
import ru.ifsoft.network.model.Profile;
import ru.ifsoft.network.util.CustomRequest;

public class SearchFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    SearchView searchView = null;

    ListView mListView;
    TextView mMessage, mHeaderText;

    LinearLayout mHeaderContainer;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<Profile> itemsList;
    private PeopleListAdapter itemsAdapter;

    public String queryText, currentQuery, oldQuery;

    public int itemCount;
    private int userId = 0;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new PeopleListAdapter(getActivity(), itemsList);

            currentQuery = queryText = savedInstanceState.getString("queryText");

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");
            userId = savedInstanceState.getInt("userId");
            itemCount = savedInstanceState.getInt("itemCount");

        } else {

            itemsList = new ArrayList<Profile>();
            itemsAdapter = new PeopleListAdapter(getActivity(), itemsList);

            currentQuery = queryText = "";

            restore = false;
            itemId = 0;
            userId = 0;
            itemCount = 0;
        }

        mHeaderContainer = (LinearLayout) rootView.findViewById(R.id.container_header);
        mHeaderText = (TextView) rootView.findViewById(R.id.headerText);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) rootView.findViewById(R.id.message);

        mListView = (ListView) rootView.findViewById(R.id.listView);

        mListView.setAdapter(itemsAdapter);

        if (itemsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Profile profile = (Profile) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", profile.getId());
                startActivity(intent);
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

                    currentQuery = getCurrentQuery();

                    if (currentQuery.equals(oldQuery)) {

                        loadingMore = true;

                        search();
                    }
                }
            }
        });

        if (queryText.length() == 0) {

            if (mListView.getAdapter().getCount() == 0) {

                showMessage(getString(R.string.label_search_start_screen_msg));
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                mHeaderContainer.setVisibility(View.VISIBLE);
                mHeaderText.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));

                hideMessage();
            }

        } else {

            if (mListView.getAdapter().getCount() == 0) {

                showMessage(getString(R.string.label_search_results_error));
                mHeaderContainer.setVisibility(View.GONE);

            } else {

                mHeaderContainer.setVisibility(View.VISIBLE);
                mHeaderText.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));

                hideMessage();
            }
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onRefresh() {

        currentQuery = queryText;

        currentQuery = currentQuery.trim();

        if (App.getInstance().isConnected() && currentQuery.length() != 0) {

            userId = 0;
            search();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    public String getCurrentQuery() {

        String searchText = searchView.getQuery().toString();
        searchText = searchText.trim();

        return searchText;
    }

    public void searchStart() {

        currentQuery = getCurrentQuery();

        if (App.getInstance().isConnected()) {

            userId = 0;
            search();

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putString("queryText", queryText);
        outState.putBoolean("restore", true);
        outState.putInt("itemId", itemId);
        outState.putInt("userId", userId);
        outState.putInt("itemCount", itemCount);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

//        MenuInflater menuInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.options_menu_main_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }

        if (searchView != null) {

            searchView.setQuery(queryText, false);

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);

            SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchAutoComplete.setHint(getText(R.string.placeholder_search));
            searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white));

            searchView.clearFocus();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    queryText = newText;

                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {

                    queryText = query;
                    searchStart();

                    return false;
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void search() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_SEARCH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!loadingMore) {

                                itemsList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemCount = response.getInt("itemCount");
                                oldQuery = response.getString("query");
                                userId = response.getInt("userId");

                                if (response.has("users")) {

                                    JSONArray usersArray = response.getJSONArray("users");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject profileObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(profileObj);

                                            itemsList.add(profile);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
                Toast.makeText(getActivity(), getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("query", currentQuery);
                params.put("userId", Integer.toString(userId));

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

        loadingMore = false;

        mItemsContainer.setRefreshing(false);

        if (mListView.getAdapter().getCount() == 0) {

            showMessage(getString(R.string.label_search_results_error));
            mHeaderContainer.setVisibility(View.GONE);

        } else {

            hideMessage();
            mHeaderContainer.setVisibility(View.VISIBLE);

            mHeaderText.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));
        }
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