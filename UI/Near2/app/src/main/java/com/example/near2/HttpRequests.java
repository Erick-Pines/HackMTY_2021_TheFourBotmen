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
    private JSONObject res;
    private boolean success = false;
    private boolean notif = false;

    public HttpRequests(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    public boolean addUser(User user) {

        JSONObject params = new JSONObject();

        try {
            params.put("user_id", user.user_id);
            params.put("name", user.name);
            params.put("is_active", user.is_active);
            params.put("is_infected", user.is_infected);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest addUserRequest = new JsonObjectRequest(Request.Method.POST, url + "user/new", params,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    success = true;
                    Log.d(TAG, response.toString());
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    success = false;
                }
        });

        queue.add(addUserRequest);

        return success;

    }

    public boolean sendUserLocation(int user_id, double longitude, double latitude) {

        JSONObject params = new JSONObject();
        res = new JSONObject();

        try {
            params.put("user_id", user_id);
            params.put("lon", longitude);
            params.put("lat", latitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, params.toString());

        JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.POST, url + "location/new", params,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    res = response;
                    //Log.d(TAG, params.toString());
                    try {
                        //Log.d(TAG, Boolean.toString(response.getBoolean("is_colliding")));
                        if(response.getBoolean("is_colliding"))
                            notif = true;
                        else
                            notif = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    notif = false;
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
        });

        queue.add(locationRequest);

        //Log.d(TAG, Boolean.toString(notif));
        return notif;

    }

    public boolean setUserInfected(int user_id, boolean is_infected) {

        JSONObject params = new JSONObject();

        try {
            params.put("user_id", user_id);
            params.put("is_infected", is_infected);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest userInfectedRequest = new JsonObjectRequest(Request.Method.POST, url + "user/infected", params,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    success = true;
                    Log.d(TAG, response.toString());
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    success = false;
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
        });

        queue.add(userInfectedRequest);

        return success;

    }

    public boolean setUserActive(int user_id, boolean is_active) {

        JSONObject params = new JSONObject();

        try {
            params.put("user_id", user_id);
            params.put("is_active", is_active);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest userActiveRequest = new JsonObjectRequest(Request.Method.POST, url + "user/active", params,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    success = true;
                    //Log.d(TAG, response.toString());
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    success = false;
                    //VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
        });

        queue.add(userActiveRequest);

        return success;

    }

}
