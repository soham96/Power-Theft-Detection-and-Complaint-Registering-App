package com.example.soham.sihuser;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class mainpage extends AppCompatActivity {

    Button complain;

    private DatabaseReference databaseReference;

    FirebaseAuth firebaseAuth;
    String UID;

    private RecyclerView profilelist;

    profile profilepage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        complain = (Button) findViewById(R.id.complain);

        firebaseAuth = FirebaseAuth.getInstance();

        String UID = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("people");
        databaseReference.keepSynced(true);

        databaseReference.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                profilepage = dataSnapshot.getValue(profile.class);

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profilelist = (RecyclerView) findViewById(R.id.profile);
        profilelist.setHasFixedSize(true);
        profilelist.setLayoutManager(new LinearLayoutManager(this));


        complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mainpage.this, complain.class);
                startActivity(intent);
            }
        });

        profilelist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<profile, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<profile, BlogViewHolder>(
                profile.class,
                R.layout.profile_row,
                BlogViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, profile model, int position) {

                viewHolder.setName(profilepage.getName());
                viewHolder.setEmail(profilepage.getEmail());
                viewHolder.setAadhar(profilepage.getAadhar());
                viewHolder.setImage(getApplicationContext(), profilepage.getImage());

            }
        };

        profilelist.setAdapter(firebaseRecyclerAdapter);

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mview;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mview=itemView;
        }

        public void setName(String name)
        {
            TextView postname=(TextView) mview.findViewById(R.id.name);
            postname.setText(name);
        }

        public void setAadhar(String Aadhar)
        {
            TextView aadhar=(TextView) mview.findViewById(R.id.useradhar);
            aadhar.setText(Aadhar);
        }

        public void setEmail(String email)
        {
            TextView Email = (TextView) mview.findViewById(R.id.email);
            Email.setText(email);
        }

        public void setImage(Context ctx, String Image) {
            ImageView post_image = (ImageView) mview.findViewById(R.id.picprofile);
            Picasso.with(ctx).load(Image).into(post_image);
        }
    }
}
