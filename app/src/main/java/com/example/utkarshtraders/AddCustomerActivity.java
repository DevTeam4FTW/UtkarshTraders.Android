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
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AddCustomerActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private EditText c_name, c_phno, c_address, c_type, c_gst;
    private Button addcust;
    private CollectionReference customerRef = mFirestore.collection("customer");
    private CollectionReference areasRef = mFirestore.collection("areas");
    private Spinner c_area;
    boolean val;

    private Button home,settings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        setup();
        mFirestore = FirebaseFirestore.getInstance();
        c_name = findViewById(R.id.cname);
        c_phno = findViewById(R.id.cphno);
        c_address = findViewById(R.id.caddress);
        c_area = findViewById(R.id.carea);
        c_type = findViewById(R.id.ctype);
        c_gst = findViewById(R.id.cgst);

        addcust = findViewById(R.id.addcustbtn);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);

        final List<String> areas = new ArrayList<>();
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                }
            }
        });



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


        addcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String cname = c_name.getText().toString();
                String cphno = c_phno.getText().toString();
                String caddress = c_address.getText().toString();
                String carea = c_area.getSelectedItem().toString();
                String ctype = c_type.getText().toString();
                String cgst = c_gst.getText().toString();


                if (!TextUtils.isEmpty(cname) &&
                        !TextUtils.isEmpty(cphno) &&
                        !TextUtils.isEmpty(caddress)&&
                        !TextUtils.isEmpty(ctype)&& val) {
                    Map<String, String> userMap = new HashMap<>();

                    userMap.put("city", "");
                    userMap.put("clientAddress", caddress);
                    userMap.put("clientArea", carea);
                    userMap.put("clientName", cname);
                    userMap.put("clientPhoneNo", cphno);
                    userMap.put("custType", ctype);
                    userMap.put("fssaino", "");
                    userMap.put("gstno", cgst);
                    userMap.put("remainingBal", "0");
                    userMap.put("pincode", "");
                    userMap.put("state", "Goa");


                    customerRef.add(userMap);

                    Toast.makeText(AddCustomerActivity.this, "Customer added successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AddCustomerActivity.this, CustomersActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Toast.makeText(AddCustomerActivity.this, "Please dont leave any fields Empty or enter right values", Toast.LENGTH_LONG).show();
                }

            }


        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit = new Intent(getBaseContext(), CustomersActivity.class);
                startActivity(edit);
                finish();
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
        setTitle("Add Customer");
        return true;
    }

    boolean validations()
    {
        Boolean val = true;
        String cname = c_name.getText().toString();
        String cphno = c_phno.getText().toString();
        String ctype = c_type.getText().toString();

        if(!cname.isEmpty())
        {
            if(!cname.matches("^[A-Z][a-zA-Z ]+"))
            {
                c_name.setError("Enter characters only(start with capital letter)");
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
        if(!ctype.isEmpty())
        {
            if(!ctype.matches("[a-zA-Z ]+"))
            {
                c_type.setError("Enter characters only");
                val = false;
            }
        }


        return val;
    }
}




