package com.example.near2;

import android.content.Context;
import android.security.keystore.StrongBoxUnavailableException;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpRequests {

    private static final String TAG = "HttpRequests";
    private String url = "http://192.168.1.81:3000/";

    private Context context;
    private RequestQueue queue;

    public HttpRequests(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    public void addUser(User user) {

        JSONObject params = new JSONObject();

        try {
            params.put("user_id", user.user_id);
            params.put("name", user.name);
            params.put("is_active", user.is_active);
            params.put("is_infected", user.is_infected);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "user/new", params,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
        });

        queue.add(jsonObjectRequest);

    }

    public void setUserInfected(int user_id) {

        JSONObject params = new JSONObject();

        try {
            params.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "user/infected", params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });

        queue.add(jsonObjectRequest);
    }

    public void setUserActive(int user_id) {

        JSONObject params = new JSONObject();

        try {
            params.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "user/active", params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });
    }

}
