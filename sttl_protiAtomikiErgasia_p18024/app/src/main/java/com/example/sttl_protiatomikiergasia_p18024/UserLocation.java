package com.example.sttl_protiatomikiergasia_p18024;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.UUID;

/**
 * Here, Comparable is used to  sort locations with the TreeMap collection class.
 * The sorting is based on the timestamp the location has.
 */
@IgnoreExtraProperties
public class UserLocation implements Serializable,Comparable<UserLocation> {

    private long locationID;
    private Customer customer;
    private double latitude, longitude,altitude;
    private long timestamp;
    private double speed;
    private boolean didAccelerate;
    private double acceleration;
    public UserLocation() {

    }

    public UserLocation(double latitude, double longitude, double altitude, long timestamp, double speed, boolean didAccelerate, double acceleration) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.speed = speed;
        this.didAccelerate = didAccelerate;
        this.locationID = timestamp;
        this.acceleration = acceleration;
    }

    public long getLocationID() {
        return locationID;
    }

    public Customer getUser() {
        return customer;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isDidAccelerate() {
        return didAccelerate;
    }

    @Override
    public int compareTo(UserLocation o) {
        return (int)(this.timestamp - o.getTimestamp());
    }
}
