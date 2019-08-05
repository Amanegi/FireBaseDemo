package com.amannegi.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText email, password;
    private MaterialButton btnSignIn, btnSignUp;
    private ProgressDialog mDialog;
    // FireBase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        }
        mDialog = new ProgressDialog(this);

        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = email.getText().toString();
                String strPass = password.getText().toString();
                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPass)) {
                    Toast.makeText(MainActivity.this, "Fields Empty!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDialog.setMessage("Logging In...");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(strEmail,strPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = email.getText().toString();
                String strPass = password.getText().toString();
                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPass)) {
                    Toast.makeText(MainActivity.this, "Fields Empty!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDialog.setMessage("Registering...");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(strEmail, strPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });

            }
        });
    }
}
