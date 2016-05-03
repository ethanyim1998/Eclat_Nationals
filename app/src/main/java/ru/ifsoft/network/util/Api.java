package ru.ifsoft.network.util;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.ifsoft.network.R;
import ru.ifsoft.network.app.App;
import ru.ifsoft.network.constants.Constants;

public class Api extends Application implements Constants {

    Context context;

    public Api (Context context) {

        this.context = context;
    }

    public void profileReport(final long profileId, final int reason) {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_REPORT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Toast.makeText(context, context.getText(R.string.label_profile_reported), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, context.getText(R.string.label_profile_reported), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profileId));
                params.put("reason", Integer.toString(reason));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void postDelete(final long postId) {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_REMOVE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, context.getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(postId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void postReport(final long postId, final int reasonId) {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_REPORT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Toast.makeText(context, context.getString(R.string.label_post_reported), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, context.getString(R.string.label_post_reported), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("postId", Long.toString(postId));
                params.put("abuseId", Integer.toString(reasonId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void commentDelete(final long commentId) {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_COMMENTS_REMOVE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, context.getString(R.string.label_post_reported), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("commentId", Long.toString(commentId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
}
