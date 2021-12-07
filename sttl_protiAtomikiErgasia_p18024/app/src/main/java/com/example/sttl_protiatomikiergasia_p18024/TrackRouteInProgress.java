package com.example.sttl_protiatomikiergasia_p18024;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;

public class TrackRouteInProgress extends AppCompatActivity {
    private final int PERMISSIONS_FINE_LOCATION = 99;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    private TextView tv_lat, tv_long, tv_speed;
    LocationCallback locationCallback;

    private double sp_lat,sp_lon,sp_alt;
    private double ep_lat,ep_lon,ep_alt;
    private long sp_time, ep_time;
    private double prev_acceleration = 0;

    private Route route;
    private Customer customer;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_route_in_progress);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationRequest = LocationRequest.create();
        int locationInterval = 3000;
        /**
         * Every 3000 milliseconds / 3 seconds the location is updated.
         */
        locationRequest.setInterval(locationInterval);
        /**
         * Priority_high_accuracy required more battery but provides better results.
         */
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(5000);

        customer = (Customer)getIntent().getSerializableExtra("LOGGED_USER");
        route = new Route();

        tv_lat = findViewById(R.id.tv_latitude);
        tv_long = findViewById(R.id.tv_longtitute);
        tv_speed = findViewById(R.id.tv_speed);

        /**
         * LocationCallback function is called every 3 seconds and Updates the User Interface.
         */
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                sp_lat = ep_lat;
                sp_lon = ep_lon;
                sp_alt = ep_alt;
                sp_time = ep_time;
                ep_lat = location.getLatitude();
                ep_lon = location.getLongitude();
                ep_alt = location.getAltitude();
                ep_time = location.getTime();
                UpdateUI(location);
            }
        };

        UpdateLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,null );
        Button confirmButton = findViewById(R.id.addRoute);

        /**
         * The confirm button. The route is saved in the DB and the state of the application returns to the previous
         * activity.
         */
        confirmButton.setOnClickListener(v -> {
            customer.saveLocation(route);
            DAOcustomer daouser = new DAOcustomer();
            daouser.saveRoute(route, customer).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(TrackRouteInProgress.this,"Route was successfully saved.",Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(TrackRouteInProgress.this, UserOptionsActivity.class);
                    myIntent.putExtra("LOGGED_USER", customer);
                    startActivity(myIntent);
                }
                else {
                    Toast.makeText(TrackRouteInProgress.this,"An error has occurred.",Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                UpdateLocation();
            }
        else {
                Toast.makeText(this, "ACCEPT LOCATION OR ELSE!", Toast.LENGTH_SHORT).show();
                finish();
            }
    }

    private void UpdateLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(TrackRouteInProgress.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            /**
             * This is the first location that is received. As a result, the starting_point values
             * are the same as the ending_point ones.
             */
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                sp_lat = location.getLatitude();
                ep_lat = location.getAltitude();
                sp_lon = location.getLongitude();
                ep_lon = location.getLongitude();
                sp_alt = location.getAltitude();
                ep_alt = location.getAltitude();
                sp_time = location.getTime();
                ep_time = location.getTime();
                UpdateUI(location);
            });
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION );
        }
    }

    @SuppressLint("SetTextI18n")
    private void UpdateUI(Location location) {
        tv_long.setText("Longitude: " + location.getLongitude());
        tv_lat.setText("Latitude: " + location.getLatitude());
        if(location.hasSpeed()){
            /**
             * Here I calculate the acceleration according to the formula: m/s^2. Then I save the location.
             */
            double acceleration = acceleration(sp_lat,ep_lat,sp_lon,ep_lon,sp_alt,ep_alt,sp_time,ep_time);
            tv_speed.setText("Speed: " + acceleration);
            UserLocation userlocation = new UserLocation(ep_lat,ep_lon,ep_alt,ep_time,location.getSpeed(),
                    acceleration > prev_acceleration,acceleration);
            route.addLocation(userlocation);
            prev_acceleration = acceleration;
        }
        else {
            tv_speed.setText("Speed: Not available");
        }
    }

    /**
     *Various functions to calculate distance,seconds^2 and acceleration.
     */
    private double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private double secondsToSquare(long milliseconds1,long milliseconds2) {
        if(milliseconds1 == milliseconds2) return 1;
        return Math.pow((double)((milliseconds2 - milliseconds1) / 1000) % 60,2);
    }

    private double  acceleration(double lat1, double lat2, double lon1,
                                 double lon2, double el1, double el2 ,long time1,long time2) {
        double meters = distance(lat1,lat2,lon1,lon2,el1,el2 );
        double seconds = secondsToSquare(time1,time2);
        return meters / seconds;
    }
}