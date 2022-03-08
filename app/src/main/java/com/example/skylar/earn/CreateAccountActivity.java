package com.example.skylar.earn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skylar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private FirebaseDatabase userDatabase;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String firebaseId;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        EditText account = findViewById(R.id.enterAccount);
        String accountId = account.getText().toString();


        userDatabase = FirebaseDatabase.getInstance();
        reference = userDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseId = user.getUid();

        SharedPreferences sharedPreferences = getSharedPreferences("myKeys", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Button btnAccount = findViewById(R.id.buttonAccount);
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("accountId", accountId);
                editor.apply();
                Intent intent = new Intent(CreateAccountActivity.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addingPublicKey(accountId);


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateAccountActivity.this, EarnActivity.class);
        startActivity(intent);
        finish();
    }


    private void addingPublicKey(String accountId){
        reference.child("users").child(firebaseId).child("accountId").setValue(accountId);
    }





}