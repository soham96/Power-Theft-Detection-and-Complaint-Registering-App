package com.example.soham.sihuser;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class complain extends AppCompatActivity {

    private ImageButton theftpic;

    private Button submitc;
    private Uri imageUri = null;
    String UID;

    private EditText comments;

    private StorageReference storageref;
    private DatabaseReference databaseref, databaseref2;
    private FirebaseAuth firebaseAuth;

//    ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);

        storageref= FirebaseStorage.getInstance().getReference();
        databaseref= FirebaseDatabase.getInstance().getReference().child("complaints");
        firebaseAuth = FirebaseAuth.getInstance();

        theftpic = (ImageButton) findViewById(R.id.theftpic);
        comments=(EditText) findViewById(R.id.comments);

        submitc=(Button) findViewById(R.id.submitcomplaint);

        theftpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

        submitc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startcomplain();
            }
        });

    }



    public void startcomplain() {

//        mprogress.setMessage("Uploading");
//        mprogress.show();

        final String username, useradhar;
        final String einfo= comments.getText().toString().trim();

        String UID=firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference name=FirebaseDatabase.getInstance().getReference().child("people").child(UID).child("Name");
        final DatabaseReference adhar =FirebaseDatabase.getInstance().getReference().child("people").child(UID).child("Aadhar");



        if(!TextUtils.isEmpty(einfo) && imageUri!=null)
        {

            StorageReference filepath=storageref.child("complaintpics").child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost= databaseref.push();
                    newPost.child("Comments").setValue(einfo);
                    newPost.child("image").setValue(downloadUrl.toString());

                    name.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String username=(String) dataSnapshot.getValue();
                            newPost.child("Name").setValue(username);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    adhar.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String useradhar = (String) dataSnapshot.getValue();
                            newPost.child("Aadhar").setValue(useradhar);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //mprogress.dismiss();

                    Intent intent = new Intent(complain.this, mainpage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2 && resultCode==RESULT_OK)
        {
            imageUri= data.getData();
            theftpic.setImageURI(imageUri);
        }
    }

}
