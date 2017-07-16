package com.example.soham.sihuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {

    private Button submit;
    private EditText email;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        submit=(Button) findViewById(R.id.resetpassword);
        email = (EditText) findViewById(R.id.editText2);

        progressDialog=new ProgressDialog(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Sending Verification Email");
                progressDialog.show();
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                String emailid=email.getText().toString().trim();

                firebaseAuth.sendPasswordResetEmail(emailid).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(forgotpassword.this, "Reset link sent to email address", Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(forgotpassword.this, Login.class);
                            startActivity(intent);
                            progressDialog.dismiss();

                        }
                    }
                });

            }
        });

    }
}
