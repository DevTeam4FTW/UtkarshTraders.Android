package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_CustomerActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore=FirebaseFirestore.getInstance();
    EditText c_name;
    EditText c_phno;
    EditText c_address;
    Spinner c_area;
    EditText c_city;
    EditText c_pin;
    EditText c_state;
    EditText c_type;
    EditText c_fssai;
    EditText c_gstin;
    EditText c_bal;
    CircleImageView saveCust;
    Boolean val;

    private CollectionReference areasRef = mFirestore.collection("areas");
    private CollectionReference customerRef = mFirestore.collection("customer");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__customer);
        setup();
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


        final List<String> areas = new ArrayList<>();
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final String area = customers.getClientArea();
        c_area.setAdapter(areaAdapter);
        areasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String areaName = document.getString("areaName");
                        areas.add(areaName);
                    }
                    areaAdapter.notifyDataSetChanged();
                    c_area.setSelection(areaAdapter.getPosition(area));
                }
            }
        });





        c_city.setText(customers.getCity());
        c_pin.setText(customers.getPincode());
        c_state.setText(customers.getState());
        c_type.setText(customers.getCustType());
        c_fssai.setText(customers.getFssaino());
        c_gstin.setText(customers.getGstno());
        c_bal.setText(customers.getRemainingBal());

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        val = validations();
                    }
                });


            }
        }, 0, 1000);

        saveCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(c_city.getText().toString()) &&
                        !TextUtils.isEmpty(c_address.getText().toString())&&
                        !TextUtils.isEmpty(c_name.getText().toString()) &&
                        !TextUtils.isEmpty(c_phno.getText().toString()) &&
                        !TextUtils.isEmpty(c_type.getText().toString()) &&
                        !TextUtils.isEmpty(c_fssai.getText().toString()) &&
                        !TextUtils.isEmpty(c_gstin.getText().toString()) &&
                        !TextUtils.isEmpty(c_bal.getText().toString())&&
                        !TextUtils.isEmpty(c_pin.getText().toString())&&
                        !TextUtils.isEmpty(c_state.getText().toString())&&
                                                                            val) {


                    customerRef.document(c_id)
                            .update(
                                    "city", c_city.getText().toString(),
                                    "clientAddress", c_address.getText().toString(),
                                    "clientArea", c_area.getSelectedItem().toString(),
                                    "clientName", c_name.getText().toString(),
                                    "clientPhoneNo", c_phno.getText().toString(),
                                    "custType", c_type.getText().toString(),
                                    "fssaino", c_fssai.getText().toString(),
                                    "gstno", c_gstin.getText().toString(),
                                    "remainingBal", c_bal.getText().toString(),
                                    "pincode", c_pin.getText().toString(),
                                    "state", c_state.getText().toString()
                            );

                    Toast.makeText(Edit_CustomerActivity.this, "The Customer has been updated",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Edit_CustomerActivity.this, CustomersActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else
                {
                    Toast.makeText(Edit_CustomerActivity.this, "Please dont leave any fields Empty or enter right values", Toast.LENGTH_LONG).show();
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

    public void setup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    Boolean validations()
    {
        Boolean val = true;
        String cname = c_name.getText().toString();
        String cphno = c_phno.getText().toString();
        String ccity = c_city.getText().toString();
        String cpin = c_pin.getText().toString();
        String cstate = c_state.getText().toString();
        String ctype = c_type.getText().toString();
        String bal   =c_bal.getText().toString();

        if(!cname.isEmpty())
        {
            if(!cname.matches("[a-zA-Z ]+"))
            {
                c_name.setError("Enter characters only");
                val = false;
            }
        }
        if(!cphno.isEmpty())
        {
            if(!cphno.matches("\\A[0-9]{10}\\z"))
            {
                c_phno.setError("Enter 10 digit number");
                val = false;
            }
        }

        if(!ccity.isEmpty())
        {
            if(!ccity.matches("[a-zA-Z ]+"))
            {
                c_city.setError("Enter characters only");
                val = false;
            }
        }

        if(!cpin.isEmpty())
        {
            if(!cpin.matches("\\A[0-9]{6}\\z"))
            {
                c_pin.setError("Enter 6 digit number ");
                val = false;
            }
        }
        if(!cstate.isEmpty())
        {
            if(!cstate.matches("[a-zA-Z ]+"))
            {
                c_state.setError("Enter characters only");
                val = false;
            }
        }
        if(!ctype.isEmpty())
        {
            if(!ctype.matches("[a-zA-Z ]+"))
            {
                c_type.setError("Enter characters only");
                val = false;
            }
        }

        if(!bal.isEmpty())
        {
            if(!bal.matches("^[0-9]*$"))
            {
                c_bal.setError("Enter numbers only");
                val = false;
            }
        }






        return val;
    }


}
