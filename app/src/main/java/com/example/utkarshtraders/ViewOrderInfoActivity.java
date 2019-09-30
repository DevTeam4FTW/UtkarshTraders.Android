package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firestore.v1beta1.StructuredQuery;

public class ViewOrderInfoActivity extends AppCompatActivity {

    TextView date;
    TextView item_name;
    TextView item_price;
    TextView item_quantity;
    TextView area;
    TextView unit;
    TextView order_total;
    ImageView edit_customer;
    public String c_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_info);


        date = findViewById(R.id.date);
        item_name = findViewById(R.id.item_name);
        item_price = findViewById(R.id.item_price);
        item_quantity = findViewById(R.id.item_quantity);
        area = findViewById(R.id.area);
        unit = findViewById(R.id.unit);
        order_total = findViewById(R.id.order_total);
        edit_customer = findViewById(R.id.edit_customer);

        Intent intent = getIntent();
        final Orders orders = intent.getParcelableExtra("order_object");

        date.setText(orders.getDate());
        item_name.setText(orders.getItemName());
        item_price.setText(orders.getItemPrice());
        item_quantity.setText(orders.getItemQuantity());
        area.setText(orders.getCustomerArea());
        unit.setText(orders.getUnit());
        order_total.setText(orders.getTotal());

        c_id = orders.getCustomerId();

        edit_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent edit = new Intent(getBaseContext(), Edit_OrderActivity.class);
                edit.putExtra("order_object",orders);
                startActivity(edit);
                finish();
                view.setOnClickListener(null);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent edit = new Intent(getBaseContext(), ViewOrdersActivity.class);
        edit.putExtra("customer_id",c_id);
        startActivity(edit);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
