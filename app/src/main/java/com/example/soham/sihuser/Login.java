package com.example.soham.sihuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    EditText etemail, etpassword;
    Button btlogin, registertext;
    TextView passwordtext;

    private ProgressDialog progressDialog;


    private static final String TAG="Login_activity";

    FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseDatabase.keepSynced(true);


        progressDialog=new ProgressDialog(this);



        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() !=null)
                {

                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseAuth.getCurrentUser().getUid());
                        Intent intent = new Intent(Login.this, mainpage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    else
                    {
                        Toast.makeText(Login.this, "Please verify your email", Toast.LENGTH_LONG).show();
                        firebaseAuth.getCurrentUser().sendEmailVerification();
                        return;

                    }
                }
                else
                {
                    Log.d(TAG,"onAuthStateChanged:signed_out:" );
                }
            }
        };


        etemail=(EditText) findViewById(R.id.email);
        etpassword=(EditText) findViewById(R.id.password);
        registertext=(Button) findViewById(R.id.registertext);
        passwordtext=(TextView) findViewById(R.id.forgot_password);
        btlogin=(Button) findViewById(R.id.btlogin);



        passwordtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this, password.class);
                startActivity(intent);
            }
        });

        registertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this, register.class);
                startActivity(intent);
            }
        });

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login(){
        String email=etemail.getText().toString().trim();
        String password=etpassword.getText().toString().trim();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(Login.this, "Please Enter your email", Toast.LENGTH_LONG).show();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(Login.this, "Please Enter a correct email", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(Login.this, "Please Enter your Password", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Logging In");
        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful())
                        {
                            Toast.makeText(Login.this, "Your Email or Password is Incorrect", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}

