package com.thefourbotmen.hackmty.ui.home;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.thefourbotmen.hackmty.R;
import com.thefourbotmen.hackmty.User;
import com.thefourbotmen.hackmty.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private JSONObject jsonObject;

    private Handler handler;
    private LocationManager locationManager;
    private double longitude;
    private double latitude;

    private TextView txtResponse;
    private TextView txtJson;

    private Button btnGETRequest;
    private Button btnPOSTRequest;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        txtResponse = root.findViewById(R.id.resultText);
        txtJson = root.findViewById(R.id.jsonText);

        btnGETRequest = root.findViewById(R.id.getRequestBtn);
        btnPOSTRequest = root.findViewById(R.id.postRequestBtn);

        btnGETRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage("a");
                getRequest("json");
            }
        });

        btnPOSTRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User testUser = new User(2, "Pedro", false);
                toastMessage("b");
                postRequest("user/active", testUser.user_id);
                txtJson.setText("");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getRequest(String route){

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="http://192.168.1.81:3000/" + route;
        String json = "";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    jsonObject = response;
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    txtResponse.setText("Error");
                }
        });

        queue.add(jsonObjectRequest);

        if(jsonObject != null) {
            txtResponse.setText(jsonObject.toString());
            try {
                txtJson.setText(jsonObject.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void postRequest(String route, int data) {

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="http://192.168.1.81:3000/" + route;

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
            new Response.Listener<JSONObject>() {
                @Override
                    public void onResponse(JSONObject response)
                    {
                        txtResponse.setText(response.toString());
                        Log.d(TAG, response.toString());
                        //pDialog.hide();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        //pDialog.hide();
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}