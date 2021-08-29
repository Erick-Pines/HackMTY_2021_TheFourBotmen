package com.example.near2.ui.slideshow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.near2.R;
import com.example.near2.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment implements LocationListener {

    private SlideshowViewModel slideshowViewModel;
    private FragmentSlideshowBinding binding;

    private LocationManager locationManager;
    private Position usrLocation;

    private TextView longitudeText;
    private TextView latitudeText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        longitudeText = root.findViewById(R.id.longitudeText);
        latitudeText = root.findViewById(R.id.latitudeText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();


        final Window root = getActivity().getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getContext().checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && getContext().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    112);
            return;
        }
        locationManager = (LocationManager) root.getContext().getSystemService(
                Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0,
                0, this
        );
        Criteria ct=new Criteria();
        ct.setAccuracy(Criteria.ACCURACY_COARSE);
        locationManager.requestSingleUpdate(ct,this, Looper.myLooper());

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        usrLocation = new Position();
        usrLocation.set(location.getLongitude(),
                location.getLatitude());
        longitudeText.setText(Double.toString(usrLocation.getX()));
        latitudeText.setText(Double.toString(usrLocation.getY()));
        Log.d("Location", String.valueOf(usrLocation));
        //Toast.makeText(this, String.valueOf(usrLocation), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    /*@Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/
    class Position {

        private double x,y;

        public Position() {
            this.x = this.y = 0;
        }

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Position subtraction(Position p){
            Position res=null;
            if(p!=null){
                res=new Position(this.x-p.x,
                        this.y-p.y);
            }
            return res;

        }

        public Position addition(Position p){
            Position res=null;
            if(p!=null){
                res=new Position(this.x+p.x,
                        this.y+p.y);
            }
            return res;

        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void set(double x, double y){
            setX(x);
            setY(y);
        }

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

}