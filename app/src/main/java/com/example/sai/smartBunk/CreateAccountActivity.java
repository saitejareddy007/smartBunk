package com.example.sai.smartBunk;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sai on 2/9/18.
 */

public class CreateAccountActivity extends AppCompatActivity {

    String TAG = null;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText    name, email, password;
    String      Name, Email, Password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_layout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.regName);
        email = (EditText) findViewById(R.id.regEmail);
        password = (EditText) findViewById(R.id.regPassword);
    }

    public void createAccount(View view) {
        Name = name.getText().toString().trim();
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        System.out.println(TextUtils.isEmpty(Email)+"********************************");
        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(CreateAccountActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Email) || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toast.makeText(CreateAccountActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Password)) {
            Toast.makeText(CreateAccountActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        } else if (Password.length() < 6) {
            Toast.makeText(CreateAccountActivity.this, "Password must be greater then 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        //attempting login with the given email and password by the user
        mAuth.createUserWithEmailAndPassword(Email, Password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success,
                        FirebaseUser user = mAuth.getCurrentUser();
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("email",Email);
                        docData.put("displayName",Name);
                        docData.put("Uid",user.getUid());
                        Log.d(TAG, "createUserWithEmail:success");
                        OnAuth(docData);
                    }
                    else {
                        //If there is no internet, diplay a message to the user.
                        if(!isNetworkAvailable()) {
                            Toast.makeText(CreateAccountActivity.this, "Oops! its seems like you are offline.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // If sign in fails, display a message to the user.
                        else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "You already have an account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            });
    }

    //Sending verification link to the user
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateAccountActivity.this, "Check your Email for verification", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
        startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class));
    }

    //store the user data to the DB and initiating the email verfication
    private void OnAuth(Map<String,Object> user) {
        db.collection("/users/")
            .add(user)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    sendEmailVerification();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });
    }

    //to check your internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}


