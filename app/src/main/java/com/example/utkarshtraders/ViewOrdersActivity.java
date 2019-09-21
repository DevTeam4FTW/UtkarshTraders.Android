package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ViewOrdersActivity extends AppCompatActivity {

    private TextView addorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        addorder = findViewById(R.id.addorder);

        Intent intent = getIntent();
        final String customer_id = intent.getStringExtra("customer_id");

        addorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewOrdersActivity.this, ViewItemsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("customer_id",customer_id);
                startActivity(intent);
                finish();
            }
        });
    }
}
