package com.example.near2.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.near2.HttpRequests;
import com.example.near2.R;
import com.example.near2.SavedCache;
import com.example.near2.User;
import com.example.near2.databinding.FragmentHomeBinding;

import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private JSONObject jsonObject;
    private HttpRequests httpRequests;

    private SavedCache savedCache;

    private EditText editTextName;
    private Spinner spinnerID;
    private SwitchCompat switchInfected;
    private Button btnSend;

    private int userId;
    private boolean isActive;
    private boolean isInfected;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        httpRequests = new HttpRequests(getContext());
        savedCache = new SavedCache(getContext());

        editTextName = root.findViewById(R.id.editTextName);
        spinnerID = root.findViewById(R.id.spinnerID);
        switchInfected = root.findViewById(R.id.switchInfected);
        btnSend = root.findViewById(R.id.sendButton);

        userId = 1;
        isActive = savedCache.isUserActive();
        isInfected = savedCache.isUserInfected();

        if(!savedCache.getUserName().isEmpty())
            editTextName.setText(savedCache.getUserName());

        if(savedCache.getUserId() >= 0)
            editTextName.setEnabled(false);

        switchInfected.setChecked(isInfected);

        spinnerID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userId = Integer.parseInt(spinnerID.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

        switchInfected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isInfected = b;
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(savedCache.getUserId() < 0) {
                    User user = new User(userId, editTextName.getText().toString(), isActive, isInfected);
                    httpRequests.addUser(user);
                }

                savedCache.saveUserId(userId);
                savedCache.saveUserName(editTextName.getText().toString());
                savedCache.saveUserInfected(isInfected);
                savedCache.saveUserActive(isActive);

                if(!httpRequests.setUserInfected(userId, isInfected))
                    toastMessage("Error while updating data");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void toastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}