package com.example.sttl_protiatomikiergasia_p18024;
import java.io.Serializable;
import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/*
Serializable interface is implemented in every class in order to pass objects through intents.
 */
public class Customer implements Serializable {

    @Exclude
    private String username;
    private String id;
    private HashMap<String,Object> routes;

    public Customer() {}

    public Customer(String username) {
        this.username = username;
        this.id = UUID.randomUUID().toString();
        this.routes = new HashMap<>();
    }

    public void saveLocation(Route route) {
        try {
            this.routes.put(route.getRouteID(),route);
        }catch(Exception e){
            this.routes = new HashMap<>();
            this.routes.put(route.getRouteID(),route);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getRoutes() {
        return routes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRoutes(HashMap<String,Object> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route) {
        this.routes.put(route.getRouteID(),route);
    }

}
