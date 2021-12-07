package com.example.sttl_protiatomikiergasia_p18024;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Both routes and locations collections in Customer and Route classes respectively
 * are HashMap<String,Object>. This is because Firebase only accepts the Object class
 * as the second generic parameter in HashMap when reading/writing in the DB.
 */
@IgnoreExtraProperties
public class Route implements Serializable {

    private String routeID;
    private HashMap<String, Object> locations;

    public Route() {
        this.routeID = UUID.randomUUID().toString();
        this.locations = new HashMap<>();
    }

    public Route(UserLocation location) {
        this.routeID = UUID.randomUUID().toString();
        this.locations = new HashMap<>();
        locations.put(String.valueOf(location.getLocationID()),location);
    }

    public void addLocation(UserLocation location) {
        this.locations.put(String.valueOf(location.getLocationID()),location);
    }

    public String getRouteID() {
        return routeID;
    }

    public HashMap<String,Object> getLocations() {
        return locations;
    }

    public void setLocations(HashMap<String,Object> locations){
        this.locations = locations;
    }
}
