package com.example.sai.smartBunk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends AppCompatActivity {



    EditText email,password;
    String Email, Password;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences sharedpreferences;
    static String MyPREFERENCES="myref";
    String TAG =null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.login_layout);
        db = FirebaseFirestore.getInstance();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        email=(EditText) findViewById(R.id.loginEmail);
        password=(EditText) findViewById(R.id.loginPassword);
        email.setText("tejareddy.d321@gmail.com");
        password.setText("saiteja.d");
    }

    //onclick login button
    public void login(View view){
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
        if (TextUtils.isEmpty(Email) || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toast.makeText(LoginActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Password)) {
            Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        } else if (Password.length() < 6) {
            Toast.makeText(LoginActivity.this, "Password must be greater then 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()){
                                getdata(user.getUid());
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "your email address is not yet verfied",
                                        LENGTH_SHORT).show();
                            }

                        } else {
                            //If there is no internet, diplay a message to the user.
                            if(!isNetworkAvailable()) {
                                Toast.makeText(LoginActivity.this, "Oops! its seems like you are offline.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // If sign in fails, display a message to the user.
                            else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Entered email or password is incorrect.",
                                        LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    //getting information from DB to display the name
    public void getdata(String Uid) {
        Query docRef = db.collection("users").whereEqualTo("Uid",Uid );
        docRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                // displaying the message
                DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                        .get(documentSnapshots.size() -1);
                gotoHome();
                Toast.makeText(LoginActivity.this,"Hello "+lastVisible.getData().get("displayName"),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //onclick "no account yet? Create one" text
    public void createAccount(View view){
        startActivity(new Intent(this,CreateAccountActivity.class));

    }

    //onclick forgot password
    public void forgotActivity(View view){
        startActivity(new Intent(this,ForgotPasswordActivity.class));
    }

    //to check your internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void gotoHome(){
        Intent i = new Intent(this,HomeActivity.class);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("email", email.getText().toString());
        editor.commit();
        startActivity(i);
    }

}
