package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_CustomerActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    EditText c_name;
    EditText c_phno;
    EditText c_address;
    EditText c_area;
    EditText c_city;
    EditText c_pin;
    EditText c_state;
    EditText c_type;
    EditText c_fssai;
    EditText c_gstin;
    EditText c_bal;
    CircleImageView saveCust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__customer);

        mFirestore = FirebaseFirestore.getInstance();
        c_name = findViewById(R.id.cname);
        c_phno = findViewById(R.id.cphno);
        c_address = findViewById(R.id.caddress);
        c_area = findViewById(R.id.carea);
        c_city = findViewById(R.id.ccity);
        c_pin = findViewById(R.id.cpin);
        c_state = findViewById(R.id.cstate);
        c_type = findViewById(R.id.ctype);
        c_fssai = findViewById(R.id.cfssai);
        c_gstin = findViewById(R.id.cgst);
        c_bal = findViewById(R.id.c_bal);
        saveCust = findViewById(R.id.savecustbtn);

        Intent intent = getIntent();
        final Customers customers = intent.getParcelableExtra("customer_object");
        final String c_id = intent.getStringExtra("customer_id");

        c_name.setText(customers.getClientName());
        c_phno.setText(customers.getClientPhoneNo());
        c_address.setText(customers.getClientAddress());
        c_area.setText(customers.getClientArea());
        c_city.setText(customers.getCity());
        c_pin.setText(customers.getPincode());
        c_state.setText(customers.getState());
        c_type.setText(customers.getCustType());
        c_fssai.setText(customers.getFssaino());
        c_gstin.setText(customers.getGstno());
        c_bal.setText(customers.getRemainingBal());

        saveCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mFirestore.collection("customer").document(c_id)
                        .update(
                                "city", c_city.getText().toString(),
                                "clientAddress",c_address.getText().toString() ,
                                "clientArea",c_area.getText().toString(),
                                "clientName",c_name.getText().toString() ,
                                "clientPhoneNo",c_phno.getText().toString(),
                                "custType",c_type.getText().toString() ,
                                "fssaino",c_fssai.getText().toString() ,
                                "gstno",c_gstin.getText().toString() ,
                                "remainingBal",c_bal.getText().toString(),
                                "pincode",c_pin.getText().toString() ,
                                "state",c_state.getText().toString()
                        );

                        Toast.makeText(Edit_CustomerActivity.this, "The Customer has been updated",
                        Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Edit_CustomerActivity.this, CustomersActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);


            }
        });





    }
}
