package com.example.sai.smartBunk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sai on 2/9/18.
 */

public class AddVehicle extends AppCompatActivity {

    EditText VehicleName;
    EditText ModelOfVehicle;
    String vehicleName;
    String modelName;
    String fuelType;
    String email;
    FirebaseFirestore db;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle_layout);
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        email= sharedpreferences.getString("email","");
        VehicleName = (EditText) findViewById(R.id.vehicleName);
        ModelOfVehicle = (EditText) findViewById(R.id.modelName);
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.petrol:
                if (checked)
                    fuelType= (String) ((RadioButton) view).getText();
                    break;
            case R.id.diesel:
                if (checked)
                    fuelType= (String) ((RadioButton) view).getText();
                    break;
        }
    }

    public void addVehicleToDb(View view){
        vehicleName=VehicleName.getText().toString();
        modelName= ModelOfVehicle.getText().toString();
        Map<String, Object> vehicleData = new HashMap<>();
        vehicleData.put("Email",email);
        vehicleData.put("VehicleName",vehicleName);
        vehicleData.put("ModelOfVehicle",modelName);
        vehicleData.put("fuelType",fuelType);
        OnAuth(vehicleData);
    }
    private void OnAuth(Map<String,Object> user) {
        final String TAG =null ;
        db.collection("/vehicles/")
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
        startActivity(new Intent(this,HomeActivity.class));
    }

}
