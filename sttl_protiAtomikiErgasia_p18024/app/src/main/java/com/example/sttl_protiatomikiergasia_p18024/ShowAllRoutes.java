package com.example.sttl_protiatomikiergasia_p18024;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.sttl_protiatomikiergasia_p18024.databinding.ActivityShowAllRoutesBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.TreeMap;

public class ShowAllRoutes extends FragmentActivity implements OnMapReadyCallback {

    Customer myCustomer;
    private DatabaseReference myDatabase;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.sttl_protiatomikiergasia_p18024.databinding.ActivityShowAllRoutesBinding binding = ActivityShowAllRoutesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        myCustomer = (Customer)getIntent().getSerializableExtra("LOGGED_USER");
        myDatabase = FirebaseDatabase.getInstance().getReference("Customer");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        myDatabase.child(myCustomer.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    /**
                     * Each child from .getChildren is a HashMap<String,Object> Route.
                     */
                    HashMap<String,Object> route = (HashMap<String, Object>) snap.getValue();
                    HashMap<String,Object> unsortedlocations = (HashMap)route.get("locations");
                    TreeMap<String,Object> sortedLocations = new TreeMap<>(unsortedlocations);
                    /**
                     * TreeMap is used because HashMap returned by the Database is unsorted
                     * and as a result, drawing poly lines later is impossible.
                     */
                    PolylineOptions polyOptions = new PolylineOptions();
                    int counter = 0;
                    for(Object location: sortedLocations.values()){
                        counter++;
                        HashMap<String,Object> loc = (HashMap)location;
                        /**
                         * Although I insert location as an object, it is returned as a HashMap from the DB.
                         */
                        LatLng marker = new LatLng(new Double(loc.get("latitude").toString()),new Double(loc.get("longitude").toString()));
                        mMap.addMarker(new MarkerOptions().position(marker).title("MARKER"+String.valueOf(counter)));
                        /**
                         * The goal is to draw a new line between each point of the route.
                         * Apart from the first point, which is where the route begins, for every other point
                         * I create a new PolylineOptions object to connect the points.
                         */
                        if(counter == 1){
                            polyOptions = new PolylineOptions();
                            polyOptions.add(marker);
                            float zoomLevel = 17.0f;
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, zoomLevel));
                        }
                        else if(!((Boolean) loc.get("didAccelerate"))){
                            //second,fourth etc point and car did not accelerate
                            polyOptions.add(marker);
                            polyOptions.color(Color.RED);
                            mMap.addPolyline(polyOptions);
                            //prepare for the 2n+1 location
                            polyOptions = new PolylineOptions();
                            polyOptions.add(marker);
                        }
                        else if((Boolean) loc.get("didAccelerate")) {
                            //second, fourth etc point and car did acceleare
                            polyOptions.add(marker);
                            polyOptions.color(Color.BLUE);
                            mMap.addPolyline(polyOptions);
                            //prepare for the 2n+1 location
                            polyOptions = new PolylineOptions();
                            polyOptions.add(marker);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}