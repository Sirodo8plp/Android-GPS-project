package com.example.sttl_protiatomikiergasia_p18024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    private Button logInButton;
    private DatabaseReference myDatabase;
    boolean userFound = false;
    Intent myIntent;
    Customer myCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        myDatabase = FirebaseDatabase.getInstance().getReference();

        logInButton = findViewById(R.id.playButton);
        logInButton.setOnClickListener(v -> {
            logInButton.setEnabled(false);
            EditText usernameTextField = findViewById(R.id.editTextTextPersonName);
            /*
            Check if username equals "". If no username has been given show Toast message.
             */
            if(usernameTextField.getText().toString().equals("")) {
                Toast.makeText(context,"Username field is empty.", Toast.LENGTH_SHORT).show();
                logInButton.setEnabled(true);
                return;
            }
            DAOcustomer daocustomer = new DAOcustomer();

              myDatabase.child("Customer").addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      /*
                      If username exists in the DB, create User and Intent and later with the if statement proceed.
                       */
                      for(DataSnapshot snap: snapshot.getChildren()){
                          if(usernameTextField.getText().toString().equals(Objects.requireNonNull(snap.getValue(Customer.class)).getUsername())){
                              userFound = true;
                              myCustomer = snap.getValue(Customer.class);
                              myIntent = new Intent(MainActivity.this,UserOptionsActivity.class);
                              myIntent.putExtra("LOGGED_USER",myCustomer);
                              break;
                          }
                      }
                      if(userFound){
                          Toast.makeText(getBaseContext(),"Welcome back "+myCustomer.getUsername(),Toast.LENGTH_LONG).show();
                          startActivity(myIntent);

                      }else{
                          myCustomer = new Customer(usernameTextField.getText().toString());
                          myIntent = new Intent(MainActivity.this,UserOptionsActivity.class);
                          myIntent.putExtra("LOGGED_USER",myCustomer);
                          daocustomer.addUser(myCustomer).addOnSuccessListener(task -> {
                              Toast.makeText(getBaseContext(),"New user added: "+myCustomer.getUsername(),Toast.LENGTH_LONG).show();
                              startActivity(myIntent);
                          });
                      }
                  }
                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
              });
        });
    }
}
