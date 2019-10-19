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
import com.google.firestore.v1beta1.StructuredQuery;

public class ViewOrderInfoActivity extends AppCompatActivity {

    TextView date;
    TextView item_name;
    TextView item_price;
    TextView item_quantity;
    TextView area;
    TextView unit;
    TextView bill;
    TextView order_total;
    Button edit_order;
    public String c_id;
    private Button add_customer,home,settings;

    private String customer_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_info);

        add_customer = findViewById(R.id.add_customer);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);

        date = findViewById(R.id.date);
        item_name = findViewById(R.id.item_name);
        item_price = findViewById(R.id.item_price);
        item_quantity = findViewById(R.id.item_quantity);
        area = findViewById(R.id.area);
        unit = findViewById(R.id.unit);
        bill = findViewById(R.id.bill);
        order_total = findViewById(R.id.order_total);
        edit_order = findViewById(R.id.edit_order);

        Intent intent = getIntent();
        final Orders orders = intent.getParcelableExtra("order_object");
        final String order_id = intent.getStringExtra("order_id");
        customer_name = intent.getStringExtra("customer_name");


        date.setText(orders.getDate());
        item_name.setText(orders.getItemName());
        item_price.setText(orders.getItemPrice());
        item_quantity.setText(orders.getItemQuantity());
        area.setText(orders.getCustomerArea());
        unit.setText(orders.getUnit());

        if(orders.getBillGenerator().equals("1")) {
            bill.setText("Utkarsh");
        }
        else
        {
            bill.setText("Shanti");
        }
        order_total.setText(orders.getTotal());


        c_id = orders.getCustomerId();

        edit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent edit = new Intent(getBaseContext(), Edit_OrderActivity.class);
                edit.putExtra("order_object",orders);
                edit.putExtra("order_id",order_id);
                edit.putExtra("customer_name",customer_name);
                startActivity(edit);
                finish();
                view.setOnClickListener(null);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addcustomerintent = new Intent(ViewOrderInfoActivity.this, AddCustomerActivity.class);
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
        Intent edit = new Intent(getBaseContext(), ViewOrdersActivity.class);
        edit.putExtra("customer_id",c_id);
        edit.putExtra("customer_name",customer_name);
        startActivity(edit);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_bar_empty, menu);
        setTitle("View Order Info");
        return true;
    }
}
