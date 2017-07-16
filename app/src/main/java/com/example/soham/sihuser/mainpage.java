package com.example.soham.sihuser;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private Button complain, home, notification;

    private DatabaseReference databaseReference;

    private TextView notificationtext;

    FirebaseAuth firebaseAuth;
    String UID;

    private boolean doubleBackToExitPressedOnce = false;

    private RecyclerView profilelist;

    profile profilepage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        complain = (Button) findViewById(R.id.complain);
        notificationtext=(TextView) findViewById(R.id.textView2);
        home=(Button) findViewById(R.id.profilepage);
        notification=(Button) findViewById(R.id.notificationpage);

        firebaseAuth = FirebaseAuth.getInstance();
        UID = firebaseAuth.getCurrentUser().getUid();

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


        notificationtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mainpage.this, com.example.soham.sihuser.notification.class);
                startActivity(intent);
            }
        });
        complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mainpage.this, complain.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mainpage.this, mainpage.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mainpage.this, notification.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logoutmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_aboutus) {
//            Intent intent = new Intent(mainpage.this, aboutus.class);
//            startActivity(intent);
//
//        }

        if (item.getItemId() == R.id.action_logout) {
            firebaseAuth.getInstance().signOut();
            Intent intent = new Intent(mainpage.this, startpage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.action_aboutus)
        {
            Intent intent=new Intent(mainpage.this, aboutus.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);


    }


    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);

            startActivity(intent);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

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
                viewHolder.setAddress(profilepage.getAddress());
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

        public void setAddress(String address)
        {
            TextView Email = (TextView) mview.findViewById(R.id.email);
            Email.setText(address);
        }

        public void setImage(Context ctx, String Image) {
            ImageView post_image = (ImageView) mview.findViewById(R.id.picprofile);
            Picasso.with(ctx).load(Image).into(post_image);
        }
    }
}
