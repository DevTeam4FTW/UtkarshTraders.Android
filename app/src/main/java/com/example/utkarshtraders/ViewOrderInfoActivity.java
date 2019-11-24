package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1beta1.StructuredQuery;

public class ViewOrderInfoActivity extends AppCompatActivity {

    TextView date;
    TextView item_name;
    TextView item_price;
    TextView item_quantity;
    TextView area;
    TextView unit;
    TextView bill;
    TextView pricetype;
    TextView hsnno;
    TextView order_total;
    Button edit_order;
    public String c_id;
    private Button add_customer,home,settings,delete_order;

    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference ordersRef=mFireStore.collection("orders");

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
        delete_order = findViewById(R.id.delete_order);
        pricetype = findViewById(R.id.item_pricetype);
        hsnno = findViewById(R.id.hsnno);

        Intent intent = getIntent();
        final Orders orders = intent.getParcelableExtra("order_object");
        final String order_id = intent.getStringExtra("order_id");
        customer_name = intent.getStringExtra("customer_name");


        date.setText(orders.getDate());
        item_name.setText(orders.getItemName());
        item_price.setText("Rs: "+orders.getItemPrice());
        item_quantity.setText(orders.getItemQuantity());
        area.setText(orders.getCustomerArea());
        unit.setText(orders.getUnit());
        hsnno.setText(orders.getHsnNo());

        String itempricetype = orders.getPriceType().equals("default") ? "Default" : orders.getPriceType();
        pricetype.setText(itempricetype);


        if(orders.getBillGenerator().equals("1")) {
            bill.setText("Utkarsh");
        }
        else
        {
            bill.setText("Shanti");
        }
        order_total.setText("Rs: "+orders.getTotal());


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

        delete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrderInfoActivity.this);
                builder.setMessage("Deleting order");
                builder.setTitle("Delete");

                builder.setMessage("Are you sure you want to delete this order?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ordersRef.document(order_id).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ViewOrderInfoActivity.this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ViewOrderInfoActivity.this, "Error deleting document", Toast.LENGTH_SHORT).show();
                                    }
                                });


                        Intent startIntent = new Intent(ViewOrderInfoActivity.this,ViewOrdersActivity.class);
                        startIntent.putExtra("customer_id",c_id);
                        startIntent.putExtra("customer_name",customer_name);
                        startActivity(startIntent);
                        finish();
                        view.setOnClickListener(null);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                        Toast.makeText(ViewOrderInfoActivity.this, "You have successfully logged out",
                                Toast.LENGTH_LONG).show();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

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
