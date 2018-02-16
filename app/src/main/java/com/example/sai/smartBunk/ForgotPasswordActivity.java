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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Sai on 2/9/18.
 */

public class ForgotPasswordActivity extends AppCompatActivity {

    String TAG = null;
    private FirebaseAuth mAuth;
    EditText email;
    String Email;
    Intent i=new Intent(this,CreateAccountActivity.class);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.forgot_password_layout);
        email = (EditText) findViewById(R.id.forgotEmail);
        mAuth=FirebaseAuth.getInstance();
    }

    //onclick sned Request button
    public void sendRequest(View view){

        Email= email.getText().toString().trim();
        if (TextUtils.isEmpty(Email) || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toast.makeText(ForgotPasswordActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        //sending reset link to the mail.
        mAuth.sendPasswordResetEmail(Email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset link is sent to your email.");
                            Toast.makeText(ForgotPasswordActivity.this,"Password reset link is sent to your email.",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //If there is no internet, diplay a message to the user.
                            if(!isNetworkAvailable()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Oops! its seems like you are offline.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else {
                                Log.d(TAG, "You have not yet created the account.");
                                Toast.makeText(ForgotPasswordActivity.this, "You have not yet created the account.", Toast.LENGTH_SHORT).show();
                                startActivity(i);
                            }
                        }
                    }
               });
        startActivity(new Intent(this,LoginActivity.class));
    }

    //to check your internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
