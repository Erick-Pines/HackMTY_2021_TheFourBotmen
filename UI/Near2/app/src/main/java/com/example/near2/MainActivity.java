package com.example.near2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.example.near2.ui.slideshow.SlideshowFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.near2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    /*private Handler vibrationHandler;
    private Vibrator vibrator;*/

    private HttpRequests httpRequests;

    private SavedCache savedCache;

    private LocationManager locationManager;
    private Handler locationHandler;
    private Position usrLocation;

    private final static String CHANNEL_ID = "NOTIFICATION";
    private final static int NOTIFICATION_ID = 0;

    private int user_id;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        httpRequests = new HttpRequests(this);

        savedCache = new SavedCache(this);

        locationHandler = new Handler();
        locationHandler.postDelayed(updateLocation, 0);

        longitude = 0.0;
        latitude = 0.0;

    }

    private Runnable updateLocation = new Runnable() {
        @Override
        public void run() {
            if (savedCache.getUserId() >= 0) {
                if (httpRequests.sendUserLocation(savedCache.getUserId(), longitude, latitude)) {
                    sendNotification("Proximity Alert", "Please mind your social distancing");
                    locationHandler.postDelayed(this, 5000);
                } else
                    locationHandler.postDelayed(this, 2000);
            }

            else {
                Toast.makeText(getApplicationContext(), "Please fill the user form", Toast.LENGTH_SHORT).show();
                sendNotification("Required Action", "Please fill the user form");
                locationHandler.postDelayed(this, 30000);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void sendNotification(String notificationTitle, String notificationText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel();
            notificationAlert(notificationTitle, notificationText);
        }
    }

    private void notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void notificationAlert(String notificationTitle, String notificationText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_menu_camera);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationText);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.RED, 1000, 1000);
        builder.setVibrate(new long[]{1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    @Override
    public void onResume() {
        super.onResume();


        final Window root = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
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
        ct.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.requestSingleUpdate(ct,this, Looper.myLooper());

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        usrLocation = new Position();
        usrLocation.set(location.getLongitude(),
                location.getLatitude());
        longitude = usrLocation.getX();
        latitude = usrLocation.getY();
        Log.d("Location", String.valueOf(usrLocation));
        //Toast.makeText(this, String.valueOf(usrLocation), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
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