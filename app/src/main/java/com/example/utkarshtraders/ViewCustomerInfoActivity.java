package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class ViewCustomerInfoActivity extends AppCompatActivity {

    TextView c_name;
    TextView c_ph;
    TextView c_address;
    TextView c_area;
    TextView c_state;
    TextView c_type;
    TextView c_gstin;
    TextView remainingBal;
    Button edit_customer;

    private Button add_customer,home,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer_info);
        setup();

        add_customer = findViewById(R.id.add_customer);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);

        c_name = findViewById(R.id.customer_name);
        c_ph = findViewById(R.id.customer_ph);
        c_address = findViewById(R.id.customer_address);
        c_area = findViewById(R.id.customer_area);
        c_state = findViewById(R.id.customer_state);
        c_type = findViewById(R.id.customer_type);
        c_gstin = findViewById(R.id.customer_gstin);
        remainingBal = findViewById(R.id.remainingBal);
        edit_customer = findViewById(R.id.edit_customer);

        Intent intent = getIntent();
        final Customers customers = intent.getParcelableExtra("customer_object");

        final String c_id = intent.getStringExtra("customer_id");

        c_name.setText(customers.getClientName());
        c_ph.setText(customers.getClientPhoneNo());
        c_address.setText(customers.getClientAddress());
        c_area.setText(customers.getClientArea());
        c_state.setText(customers.getState());
        c_type.setText(customers.getCustType());
        c_gstin.setText(customers.getGstno());
        remainingBal.setText("Rs: "+customers.getRemainingBal());

        edit_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent edit = new Intent(getBaseContext(), Edit_CustomerActivity.class);
                edit.putExtra("customer_object",customers);
                edit.putExtra("customer_id",c_id);
                startActivity(edit);
                finish();
                view.setOnClickListener(null);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addcustomerintent = new Intent(ViewCustomerInfoActivity.this, AddCustomerActivity.class);
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
        setTitle("View Customer Info");
        return true;
    }


}
