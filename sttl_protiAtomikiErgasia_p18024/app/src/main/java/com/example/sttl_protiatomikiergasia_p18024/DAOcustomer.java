package com.example.sttl_protiatomikiergasia_p18024;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * DAOcustomer is used in order to communicate with the database.
 * Both functions return Tasks which are checked with success listeners
 * where they are called.
 */

public class DAOcustomer {
    private DatabaseReference databaseReference;

    public DAOcustomer(){
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Customer.class.getSimpleName());
    }

    public Task<Void> addUser(Customer customer)
    {
        return databaseReference.push().setValue(customer);
    }

    public Task<Void> saveRoute(Route route, Customer customer) {
        customer.saveLocation(route);
        return databaseReference.child(customer.getUsername()).updateChildren(customer.getRoutes());
    }
}
