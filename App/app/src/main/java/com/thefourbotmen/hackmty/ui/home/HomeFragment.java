package com.thefourbotmen.hackmty.ui.home;

import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.thefourbotmen.hackmty.R;
import com.thefourbotmen.hackmty.User;
import com.thefourbotmen.hackmty.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private JSONObject jsonObject;

    private TextView txtResponse;
    private TextView txtJson;

    private Button btnShowResult;

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

        btnShowResult = root.findViewById(R.id.showResultBtn);

        btnShowResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage("a");
                request("json");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void request(String route){

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

        // Request a string response from the provided URL.
        /*StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //r = response;
                    txtResponse.setText(response);
                }
            },
            new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtResponse.setText("Error");
            }
        });

        queue.add(stringRequest);*/

        //json = txtResponse.getText().toString();
        //res = gson.fromJson(txtResponse.getText().toString(), User.class);
        //Log.d(TAG, txtResponse.getText().toString());
        //txtJson.setText("id: " + res.user_id + " name: \"" + res.name + " is_active: " + res.is_active);
    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}