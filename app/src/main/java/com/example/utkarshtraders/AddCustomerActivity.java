package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCustomerActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore=FirebaseFirestore.getInstance();
    private EditText c_name, c__phno, c_address, c_city, c_pin, c_state, c_type, c_fssai, c_gst;
    private ImageView addcust;
    private CollectionReference customerRef=mFirestore.collection("customer");
    private CollectionReference areasRef = mFirestore.collection("areas");
    private Spinner c_area;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        setup();
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

        addcust = findViewById(R.id.addcustbtn);

        final List<String> areas = new ArrayList<>();
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                }
            }
        });


        addcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String cname = c_name.getText().toString();
                String cphno = c__phno.getText().toString();
                String caddress = c_address.getText().toString();
                String carea = c_area.getSelectedItem().toString();
                String ccity = c_city.getText().toString();
                String cpin = c_pin.getText().toString();
                String cstate = c_state.getText().toString();
                String ctype = c_type.getText().toString();
                String cfssai = c_fssai.getText().toString();
                String cgst = c_gst.getText().toString();



                if (!TextUtils.isEmpty(cname) && !TextUtils.isEmpty(cphno) && !TextUtils.isEmpty(caddress) && !TextUtils.isEmpty(ccity) && !TextUtils.isEmpty(cpin) && !TextUtils.isEmpty(cstate) && !TextUtils.isEmpty(ctype) && !TextUtils.isEmpty(cfssai) && !TextUtils.isEmpty(cgst)) {


                    Map<String, String> userMap = new HashMap<>();

                    userMap.put("city", ccity);
                    userMap.put("clientAddress", caddress);
                    userMap.put("clientArea", carea);
                    userMap.put("clientName", cname);
                    userMap.put("clientPhoneNo",cphno);
                    userMap.put("custType", ctype);
                    userMap.put("fssaino",cfssai);
                    userMap.put("gstno",cgst);
                    userMap.put("remainingBal","0");
                    userMap.put("pincode",cpin);
                    userMap.put("state", cstate);


                    customerRef.add(userMap);

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

    public void setup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }
}




