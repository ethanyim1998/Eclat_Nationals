package ru.hhsfbla.network;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

import ru.hhsfbla.network.adapter.StreamListAdapter;
import ru.hhsfbla.network.app.App;
import ru.hhsfbla.network.constants.Constants;
import ru.hhsfbla.network.dialogs.MyPostActionDialog;
import ru.hhsfbla.network.dialogs.PostActionDialog;
import ru.hhsfbla.network.dialogs.PostDeleteDialog;
import ru.hhsfbla.network.dialogs.PostReportDialog;
import ru.hhsfbla.network.dialogs.PostShareDialog;
import ru.hhsfbla.network.model.Item;
import ru.hhsfbla.network.util.Api;
import ru.hhsfbla.network.util.CustomRequest;
import ru.hhsfbla.network.util.ItemInterface;

public class FavoritesFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener, ItemInterface {

    private static final String STATE_LIST = "State Adapter Data";

    ListView mListView;
    TextView mMessage;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<Item> itemsList;
    private StreamListAdapter itemsAdapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

}
