package com.example.soham.sihuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class qrscanner extends AppCompatActivity {

    Button scan;

    private TextView textViewName, textViewAddress;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        textViewAddress = (TextView) findViewById(R.id.TVaddress);
        textViewName=(TextView) findViewById(R.id.TVname);

        qrScan = new IntentIntegrator(this);

        scan = (Button) findViewById(R.id.scan);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    String aadhaarInfoName;
                    String aadhaarInfo = result.getContents();
                    Toast.makeText(this, aadhaarInfo, Toast.LENGTH_SHORT).show();
                    aadhaarInfo = aadhaarInfo.split(">")[1];
                    Toast.makeText(this, aadhaarInfo, Toast.LENGTH_SHORT).show();
                    aadhaarInfoName = aadhaarInfo.split("name=\"")[1];
                    Toast.makeText(this, aadhaarInfoName, Toast.LENGTH_SHORT).show();
                    aadhaarInfoName = aadhaarInfoName.split("\"")[0];
                    //setting values to textviews
                    Toast.makeText(this, aadhaarInfoName, Toast.LENGTH_SHORT).show();
                    textViewName.setText(aadhaarInfoName);
//                    textViewAddress.setText(obj.getString("house"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    String aadhaarInfoName;
                    String aadhaarInfo = result.getContents();
                    Toast.makeText(this, aadhaarInfo, Toast.LENGTH_SHORT).show();
                    aadhaarInfo = aadhaarInfo.split(">")[1];
                    Toast.makeText(this, aadhaarInfo, Toast.LENGTH_SHORT).show();
                    aadhaarInfoName = aadhaarInfo.split("name=\"")[1];
                    Toast.makeText(this, aadhaarInfoName, Toast.LENGTH_SHORT).show();
                    aadhaarInfoName = aadhaarInfoName.split("\"")[0];
                    //setting values to textviews
                    Toast.makeText(this, aadhaarInfoName, Toast.LENGTH_SHORT).show();
                    textViewName.setText(aadhaarInfoName);
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
    }


}

