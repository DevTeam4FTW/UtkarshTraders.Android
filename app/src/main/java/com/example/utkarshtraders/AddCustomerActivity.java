package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCustomerActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private EditText c_name, c__phno, c_address, c_area, c_city, c_pin, c_state, c_type, c_fssai, c_gst,c_bal;
    private ImageView addcust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        mFirestore = FirebaseFirestore.getInstance();
        c_name = findViewById(R.id.cname);
        c__phno = findViewById(R.id.cphno);
        c_address = findViewById(R.id.caddress);
        c_area = findViewById(R.id.carea);
        c_city = findViewById(R.id.ccity);
        c_pin = findViewById(R.id.cpin);
        c_state = findViewById(R.id.cstate);
        c_type = findViewById(R.id.ctype);
        c_fssai = findViewById(R.id.cfssai);
        c_gst = findViewById(R.id.cgst);
        c_bal = findViewById(R.id.c_bal);

        addcust = findViewById(R.id.addcustbtn);

        addcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String cname = c_name.getText().toString();
                String cphno = c__phno.getText().toString();
                String caddress = c_address.getText().toString();
                String carea = c_area.getText().toString();
                String ccity = c_city.getText().toString();
                String cpin = c_pin.getText().toString();
                String cstate = c_state.getText().toString();
                String ctype = c_type.getText().toString();
                String cfssai = c_fssai.getText().toString();
                String cgst = c_gst.getText().toString();
                String cbal = c_bal.getText().toString();



                if (!TextUtils.isEmpty(cname) && !TextUtils.isEmpty(cphno) && !TextUtils.isEmpty(caddress) && !TextUtils.isEmpty(carea) && !TextUtils.isEmpty(ccity) && !TextUtils.isEmpty(cpin) && !TextUtils.isEmpty(cstate) && !TextUtils.isEmpty(ctype) && !TextUtils.isEmpty(cfssai) && !TextUtils.isEmpty(cgst) && !TextUtils.isEmpty(cbal)) {


                    Map<String, String> userMap = new HashMap<>();

                    userMap.put("city", ccity);
                    userMap.put("clientAddress", caddress);
                    userMap.put("clientArea", carea);
                    userMap.put("clientName", cname);
                    userMap.put("clientPhoneNo",cphno);
                    userMap.put("custType", ctype);
                    userMap.put("fssaino",cfssai);
                    userMap.put("gstno",cgst);
                    userMap.put("remainingBal",cbal);
                    userMap.put("pincode",cpin);
                    userMap.put("state", cstate);


                    mFirestore.collection("customer").add(userMap);

                    Toast.makeText(AddCustomerActivity.this, "Customer added successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AddCustomerActivity.this, CustomersActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Toast.makeText(AddCustomerActivity.this, "Please dont leave any fields Empty", Toast.LENGTH_LONG).show();
                }

            }


        });

    }

    @Override
    public void onBackPressed() {
        Intent edit = new Intent(getBaseContext(), CustomersActivity.class);
        startActivity(edit);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}




