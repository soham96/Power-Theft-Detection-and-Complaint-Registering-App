package com.example.soham.sihuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class register extends AppCompatActivity {

    EditText etname, etemail, etadhar, etpassword1, etpassword;
    Button btregister,aadharlogin;
    ImageButton profile;

    String name="", email="", regno="", password="", password1="";

    private Firebase Users, adduser;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference mStorageImage;

    private Uri mImageUri=null;
    public int check=0;

    public String useremail, userpassword;

    public DatabaseReference databaseReference;

    public static final int GALLERY_REQUEST_CODE=1;

    IntentIntegrator qrScan;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        qrScan = new IntentIntegrator(this);

        check =0;

        Firebase.setAndroidContext(this);

        Users = new Firebase("https://sihuser-11acb.firebaseio.com").child("people");

        mStorageImage= FirebaseStorage.getInstance().getReference().child("Profile_Pics");

        etname = (EditText) findViewById(R.id.name);
        etemail = (EditText) findViewById(R.id.email);
        etadhar = (EditText) findViewById(R.id.regno);
        etpassword = (EditText) findViewById(R.id.password);
        etpassword1 = (EditText) findViewById(R.id.password1);
        profile= (ImageButton) findViewById(R.id.profilepic);
        aadharlogin=(Button) findViewById(R.id.aadharlogin);


        aadharlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        btregister = (Button) findViewById(R.id.btregister);

        progressDialog = new ProgressDialog(this);

        btregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser()
    {

        if(check>=1) {
            name = etname.getText().toString().trim();
            email = etemail.getText().toString().trim();
            regno = etadhar.getText().toString().trim();
            password = etpassword.getText().toString().trim();
            password1 = etpassword1.getText().toString().trim();

            if(TextUtils.isEmpty(name))
            {
                Toast.makeText(register.this, "Please Enter your name", Toast.LENGTH_LONG).show();
                return;
            }


            if(TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                Toast.makeText(register.this, "Please Enter your email", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(regno))
            {
                Toast.makeText(register.this, "Please Enter your Registration Number", Toast.LENGTH_LONG).show();
                return;
            }

            if(TextUtils.isEmpty(password) || TextUtils.isEmpty(password1))
            {
                Toast.makeText(register.this, "Please Enter your Password Twice", Toast.LENGTH_LONG).show();
                return;
            }

            if(password.length()<6 && password1.length()<6)
            {
                Toast.makeText(register.this, "Password too short", Toast.LENGTH_LONG).show();
                return;

            }

            if (!password.equals(password1)) {
                Toast.makeText(register.this, "Passwords dont Match", Toast.LENGTH_LONG).show();
                return;

            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)

                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                StorageReference filepath= mStorageImage.child(mImageUri.getLastPathSegment());
                                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadUri=taskSnapshot.getDownloadUrl().toString();

                                        String UID=firebaseAuth.getCurrentUser().getUid();
                                        adduser=Users.child(UID);
                                        adduser.child("Name").setValue(name);
                                        adduser.child("Email").setValue(email);
                                        adduser.child("Aadhar").setValue(regno);
                                        adduser.child("Password").setValue(password);
                                        adduser.child("Image").setValue(downloadUri);
                                        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    Toast.makeText(register.this, "Signup successful. Verification email sent", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name).build();
                                        firebaseUser.updateProfile(profileUpdates);
                                        progressDialog.dismiss();




                                        Intent intent=new Intent(register.this, Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                });

                            }
                            else{
                                Toast.makeText(register.this,"Registration Error",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }

                        }
                    });

            user=FirebaseAuth.getInstance().getCurrentUser();
        }
        if(check==0) {

            name = etname.getText().toString().trim();
            regno = etadhar.getText().toString().trim();
            email = etemail.getText().toString().trim();
            useremail="96soham96@gmail.com";
            userpassword="qwerty123456";

            databaseReference= FirebaseDatabase.getInstance().getReference().child("people").child("0Azqtpi3VHdwxwkea7y3evXbewd2");
            databaseReference.child("Name").setValue(name);
            databaseReference.child("Aadhar").setValue(regno);
            databaseReference.child("Address").setValue(email);

            Log.d("registerFirst","-----------------Reached here------------");

            StorageReference filepath= mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri=taskSnapshot.getDownloadUrl().toString();
                    Log.d("Register","----------------------------------Entering onSuccess----------------------");
                  //  DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("people").child("0Azqtpi3VHdwxwkea7y3evXbewd2");
                    databaseReference.child("Image").setValue(downloadUri);


                }
            });

            firebaseAuth.signInWithEmailAndPassword(useremail, userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(register.this, mainpage.class);
                        startActivity(intent);
                    }
                }
            });




        }



        progressDialog.setMessage("Registering User");
        progressDialog.show();

        user=FirebaseAuth.getInstance().getCurrentUser();

        check=0;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result1 = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result1 != null) {
            //if qrcode has nothing in it
            if (result1.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    //converting the data to json
                    JSONObject obj = new JSONObject(result1.getContents());
                    //setting values to textviews
                    etname.setText(obj.getString("name"));
                    etemail.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    String aadhaarInfo = result1.getContents();
                    String aadhaarInfoName, aadhaarCardUID, aadhaarCardAddr, addrH,addrLoc,addrStr,addrDist,addrState,addrPc;
//                    Toast.makeText(this, aadhaarInfo, Toast.LENGTH_SHORT).show();
                    aadhaarInfo = aadhaarInfo.split(">")[1];
//                    Toast.makeText(this, aadhaarInfo, Toast.LENGTH_SHORT).show();
                    aadhaarInfoName = aadhaarInfo.split("name=\"")[1];
                    aadhaarCardUID = aadhaarInfo.split("uid=\"")[1];
//                    Toast.makeText(this, aadhaarInfoName, Toast.LENGTH_SHORT).show();
                    aadhaarInfoName = aadhaarInfoName.split("\"")[0];
                    aadhaarCardUID = aadhaarCardUID.split("\"")[0];
                    addrH = aadhaarInfo.split("house=\"")[1];
                    addrH = addrH.split("\"")[0];
                    addrStr = aadhaarInfo.split("street=\"")[1];
                    addrStr = addrStr.split("\"")[0];
//                    addrLoc = aadhaarInfo.split("loc=\"")[1];
//                    addrLoc = addrLoc.split("\"")[0];
//                    addrVtc = aadhaarInfo.split("vtc=\"")[1];
//                    addrVtc = addrVtc.split("\"")[0];
//                    addrPo = aadhaarInfo.split("po=\"")[1];
//                    addrPo = addrPo.split("\"")[0];
                    addrDist = aadhaarInfo.split("dist=\"")[1];
                    addrDist = addrDist.split("\"")[0];
//                    addrSubD = aadhaarInfo.split("subdist=\"")[1];
//                    addrSubD = addrSubD.split("\"")[0];
                    addrState = aadhaarInfo.split("state=\"")[1];
                    addrState = addrState.split("\"")[0];
                    addrPc = aadhaarInfo.split("pc=\"")[1];
                    addrPc = addrPc.split("\"")[0];
                    aadhaarCardAddr = addrH + " "  + addrStr + " " + addrDist + " " + addrState + " " + addrPc;

                    //setting values to textviews
//                    Toast.makeText(this, aadhaarInfoName, Toast.LENGTH_SHORT).show();
                    etname.setText(aadhaarInfoName);
                    etadhar.setText(aadhaarCardUID);
                    etemail.setText(aadhaarCardAddr);
                    check=0;
//                    textViewAddress.setText(obj.getString("house"));
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


        if(requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK)
        {

            Uri imageUri=data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                profile.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        check=0;
//    }
}


