package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    TextView c_state;
    Spinner c_type;
    EditText c_gstin;
    EditText c_bal;
    Button saveCust;
    private Button add_customer,home,settings;

    private CollectionReference areasRef = mFirestore.collection("areas");
    private CollectionReference customerTypeRef = mFirestore.collection("cT");
    private CollectionReference customerRef = mFirestore.collection("customer");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__customer);

        add_customer = findViewById(R.id.add_customer);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);
        setup();
        c_name = findViewById(R.id.cname);
        c_phno = findViewById(R.id.cphno);
        c_address = findViewById(R.id.caddress);
        c_area = findViewById(R.id.carea);
        c_state = findViewById(R.id.cstate);
        c_type = findViewById(R.id.ctype);
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
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final String area = customers.getClientArea();
        c_area.setAdapter(areaAdapter);
        areasRef.orderBy("areaName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        final List<String> customertypes = new ArrayList<>();
        final ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, customertypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final String type = customers.getCustType();
        c_type.setAdapter(typeAdapter);
        customerTypeRef.orderBy("ct").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String ctype = document.getString("ct");
                        customertypes.add(ctype);
                    }
                    typeAdapter.notifyDataSetChanged();
                    c_type.setSelection(typeAdapter.getPosition(type));
                }
            }
        });

        c_state.setText(customers.getState());
        c_gstin.setText(customers.getGstno());
        c_bal.setText(customers.getRemainingBal());


        saveCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(c_name.getText().toString())) {


                    String remainingbal = c_bal.getText().toString().isEmpty() ? "0" : c_bal.getText().toString();


                    customerRef.document(c_id)
                            .update(
                                    "clientAddress", c_address.getText().toString(),
                                    "clientArea", c_area.getSelectedItem().toString(),
                                    "clientName", c_name.getText().toString(),
                                    "clientPhoneNo", c_phno.getText().toString(),
                                    "custType", c_type.getSelectedItem().toString(),
                                    "gstno", c_gstin.getText().toString(),
                                    "remainingBal", remainingbal,
                                    "state", "Goa"
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

        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addcustomerintent = new Intent(Edit_CustomerActivity.this, AddCustomerActivity.class);
                addcustomerintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(addcustomerintent);
                finish();
                view.setOnClickListener(null);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit = new Intent(getBaseContext(), CustomersActivity.class);
                startActivity(edit);
                finish();
                view.setOnClickListener(null);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(edit);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_bar_empty, menu);
        setTitle("Edit Customer");
        return true;
    }


}
