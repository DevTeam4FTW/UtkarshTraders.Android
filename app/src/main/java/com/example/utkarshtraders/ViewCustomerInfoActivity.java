package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCustomerInfoActivity extends AppCompatActivity {

    TextView c_name;
    TextView c_ph;
    TextView c_address;
    TextView c_area;
    TextView c_city;
    TextView c_pin;
    TextView c_state;
    TextView c_type;
    TextView c_fssai;
    TextView c_gstin;
    TextView remainingBal;
    ImageView edit_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer_info);


        c_name = findViewById(R.id.customer_name);
        c_ph = findViewById(R.id.customer_ph);
        c_address = findViewById(R.id.customer_address);
        c_area = findViewById(R.id.customer_area);
        c_city = findViewById(R.id.customer_city);
        c_pin = findViewById(R.id.customer_pin);
        c_state = findViewById(R.id.customer_state);
        c_type = findViewById(R.id.customer_type);
        c_fssai = findViewById(R.id.customer_fssai);
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
        c_city.setText(customers.getCity());
        c_pin.setText(customers.getPincode());
        c_state.setText(customers.getState());
        c_type.setText(customers.getCustType());
        c_fssai.setText(customers.getFssaino());
        c_gstin.setText(customers.getGstno());
        remainingBal.setText(customers.getRemainingBal());

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


    }

    @Override
    public void onBackPressed() {
        Intent edit = new Intent(getBaseContext(), CustomersActivity.class);
        startActivity(edit);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
