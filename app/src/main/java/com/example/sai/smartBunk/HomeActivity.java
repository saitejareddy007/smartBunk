package com.example.sai.smartBunk;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Sai on 2/9/18.
 */

public class HomeActivity extends AppCompatActivity  {

    FirebaseFirestore db;
    Button myButton;
    int i=0;
    LinearLayout layout;
    private LocationManager locationManager;
    private LocationListener listener;
    String odometer;
    String quantityOfFuel;
    String email;
    Map<String, Object> history = new HashMap<>();
    AlertDialog.Builder builder1,builder2;
    TextView textView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_layout);
        db = FirebaseFirestore.getInstance();
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        email = sharedpreferences.getString("email", "");
        textView=(TextView) findViewById(R.id.tips);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Odometer readings");
        builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Quantity of fuel");

// Set up the input
        final EditText input1 = new EditText(this);
        final EditText input2 = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder1.setView(input1);

        input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder2.setView(input2);


// Set up the buttons
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                odometer = input1.getText().toString();

            }
        });
        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quantityOfFuel = input2.getText().toString();


            }
        });
        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });




        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                history.put("Email",email);
                history.put("Latitude",location.getLatitude());
                history.put("Longitude",location.getLongitude());
                history.put("odometer",odometer);
                history.put("quantity",quantityOfFuel);
                history.put("timeStamp", DateFormat.getDateTimeInstance().format(new Date()));
                OncheckIn(history);
                locationManager.removeUpdates(this);
                System.out.println("\n " + location.getLongitude() + " " + location.getLatitude()+"$$$$$$$$$$$$$$$$$$$$$");
                Toast.makeText(HomeActivity.this, "Entered email or password is incorrect.",
                        LENGTH_SHORT).show();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };


        getdata(email);
        myButton = new Button(this);
        db.collection("vehicles")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    public final String TAG = null;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //myButton=myButton.append( i);
                                myButton.setText((String)document.getData().get("VehicleName"));
                                myButton.setId(i);
                                layout = (LinearLayout) findViewById(R.id.dynamiclayout);
                                layout.removeView(myButton);
                                layout.addView(myButton);
                                myButton.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        Toast.makeText(HomeActivity.this,
                                                "Button clicked index = " + myButton.getId(), Toast.LENGTH_SHORT)
                                                .show();
                                        getloc();


                                    }
                                });
                                System.out.println(document.getId() + " => " + document.getData()+"********************************************");
                                i++;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void getloc(){
        System.out.println("clicked");
        builder1.show();
        builder2.show();
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    private void OncheckIn(Map<String,Object> user) {
        final String TAG =null ;
        db.collection("/history/")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {


                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }




    public void getdata(String email) {

    }


    public void addVehicle(View view){
        startActivity(new Intent(this,AddVehicle.class));
    }

    public void tips(View view){
        startActivity(new Intent(this,Tips.class));
    }


}
