package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;

public class PlaceOrderActivity extends AppCompatActivity {

    private TextView date,item_name,taxrate,grand_total;
    private EditText item_qty, item_price, area;
    private ImageView placeorder;
    private ToggleButton togglespecial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        date = findViewById(R.id.date);
        item_name = findViewById(R.id.item_name);
        taxrate = findViewById(R.id.taxrate);
        grand_total = findViewById(R.id.grand_total);
        item_qty = findViewById(R.id.item_qty);
        item_price = findViewById(R.id.item_price);
        area = findViewById(R.id.area);
        placeorder = findViewById(R.id.placeorder);
        togglespecial = findViewById(R.id.togglespecial);

        Intent intent = getIntent();
        final Items items = intent.getParcelableExtra("item_object");
        final String c_id = intent.getStringExtra("customer_id");

        item_price.setEnabled(false);

        togglespecial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    item_price.setEnabled(true);
                } else {
                    item_price.setEnabled(false);
                }
            }

        });

        long datemnow = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        String dateString = sdf.format(datemnow);
        date.setText(dateString);

        item_name.setText(items.getItemName());











    }
}
