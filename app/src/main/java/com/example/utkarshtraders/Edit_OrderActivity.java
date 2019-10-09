package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Edit_OrderActivity extends AppCompatActivity {

    private TextView editdate,edititem_name,edittaxrate,editcustomer_total;
    private EditText edititem_qty, edititem_price;
    private Button editplaceorder;
    private ToggleButton edittogglespecial;
    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference ordersRef=mFireStore.collection("orders");
    private CollectionReference areasRef=mFireStore.collection("areas");
    private Spinner editbill_spinner,editunit_spinner,editarea_spinner;

    private String editbill_generator;
    private String editunit_type;

    public String cust_id;
    String default_bill;
    String default_unit;
    String default_area;
    boolean val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__order);
        setup();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
        editdate = findViewById(R.id.editdate);
        edititem_name = findViewById(R.id.edititem_name);
        edittaxrate = findViewById(R.id.edittaxrate);
        editcustomer_total = findViewById(R.id.editcustomer_total);
        edititem_qty = findViewById(R.id.edititem_qty);
        edititem_price = findViewById(R.id.edititem_price);
        editarea_spinner = findViewById(R.id.editarea_spinner);
        editplaceorder = findViewById(R.id.editplaceorder);
        edittogglespecial = findViewById(R.id.edittogglespecial);
        editbill_spinner = findViewById(R.id.editbill_spinner);
        editunit_spinner = findViewById(R.id.editunit_spinner);

        Intent intent = getIntent();
        final Orders orders = intent.getParcelableExtra("order_object");
        final String order_id = intent.getStringExtra("order_id");
        cust_id = orders.getCustomerId();
        edititem_price.setEnabled(false);

        edittogglespecial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edititem_price.setEnabled(true);
                } else {
                    edititem_price.setEnabled(false);
                    edititem_price.setText(orders.getItemPrice());
                }
            }

        });
        editdate.setText(orders.getDate());
        edititem_name.setText(orders.getItemName());
        edittaxrate.setText(orders.getTaxrate());

        default_area = orders.getCustomerArea();

        final List<String> areas = new ArrayList<>();
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editarea_spinner.setAdapter(areaAdapter);
        areasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String areaName = document.getString("areaName");
                        areas.add(areaName);
                    }
                    areaAdapter.notifyDataSetChanged();
                    editarea_spinner.setSelection(areaAdapter.getPosition(default_area));
                }
            }
        });




        edititem_qty.setText(orders.getItemQuantity());
        edititem_price.setText(orders.getItemPrice());

        String bill_gen = orders.getBillGenerator();
        switch (bill_gen)
        {
            case "1": default_bill = "Utkarsh";
                break;
            case "2" :default_bill = "Shanti";
                break;
                default: default_bill = "Utkarsh";
        }
        final String unit = orders.getUnit();

        switch (unit)
        {
            case "per/pc": default_unit = "Per/Piece";
                break;
            case "per/dozen" :default_unit = "Per/Dozen";
                break;
            case "per/kg" :default_unit = "Per/Kg";
                break;
            default: default_unit = "Per/Piece";
        }

        ArrayAdapter<CharSequence> billadapter = ArrayAdapter.createFromResource(this, R.array.seller_prompts, android.R.layout.simple_spinner_item);
        billadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editbill_spinner.setAdapter(billadapter);

        editbill_spinner.setSelection(billadapter.getPosition(default_bill));

        ArrayAdapter<CharSequence> unitadapter = ArrayAdapter.createFromResource(this, R.array.unit_prompts, android.R.layout.simple_spinner_item);
        unitadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editunit_spinner.setAdapter(unitadapter);

        editunit_spinner.setSelection(unitadapter.getPosition(default_unit));

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
        }, 0, 500);


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(edititem_qty.getText().toString()) && !TextUtils.isEmpty(edititem_price.getText().toString()) && edititem_qty.getText().toString().matches("^[0-9]*$") && edititem_price.getText().toString().matches("^[0-9]*$") )
                {
                    final Float c_total = (Float .parseFloat(edititem_qty.getText().toString()) * Float .parseFloat(edititem_price.getText().toString()));
                    editcustomer_total.setText(c_total.toString());

                }

            }
        }, 0, 1000);


        editplaceorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(editcustomer_total.getText().toString()) && !TextUtils.isEmpty(edititem_qty.getText().toString())&& !TextUtils.isEmpty(edititem_price.getText().toString()) && val) {

                    if (editbill_spinner.getSelectedItem().toString().equals("Utkarsh")) {
                        editbill_generator = "1";
                    } else {
                        editbill_generator = "2";
                    }

                    String editcustomerArea = editarea_spinner.getSelectedItem().toString();
                    String edititemPrice = edititem_price.getText().toString();
                    String edititemQuantity = edititem_qty.getText().toString();
                    Float taxTotalFloat = Float.parseFloat(edititem_qty.getText().toString()) * Float.parseFloat(edititem_price.getText().toString());
                    String edittaxTotal = taxTotalFloat.toString();
                    Float taxableRateFloat = Float.parseFloat(edititem_price.getText().toString()) - (Float.parseFloat(edititem_price.getText().toString()) * (Float.parseFloat(edittaxrate.getText().toString())/100));
                    String edittaxableRate = taxableRateFloat.toString();
                    String edittotal = edittaxTotal;


                    if (editunit_spinner.getSelectedItem().toString().equals("Per/Kg")) {
                        editunit_type = "per/kg";
                    } else if (editunit_spinner.getSelectedItem().toString().equals("Per/Piece")) {
                        editunit_type = "per/pc";
                    } else {
                        editunit_type = "per/dozen";
                    }

                    ordersRef.document(order_id)
                            .update(
                                    "billGenerator", editbill_generator,
                                    "customerArea",editcustomerArea ,
                                    "itemPrice",edititemPrice ,
                                    "itemQuantity",edititemQuantity,
                                    "taxTotal",edittaxTotal ,
                                    "taxableRate",edittaxableRate ,
                                    "total",edittotal,
                                    "unit",editunit_type
                            );
                    Toast.makeText(Edit_OrderActivity.this, "Order edited successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Edit_OrderActivity.this, ViewOrdersActivity.class);
                    intent.putExtra("customer_id",cust_id);
                    startActivity(intent);
                    finish();
                    view.setOnClickListener(null);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }

                else
                {
                    Toast.makeText(Edit_OrderActivity.this,"Enter all fields before placing order ",Toast.LENGTH_SHORT).show();
                }







            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent edit = new Intent(getBaseContext(), ViewOrdersActivity.class);
        edit.putExtra("customer_id",cust_id);
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

    boolean validations()
    {
        Boolean val = true;
        String qty = edititem_qty.getText().toString();
        String price = edititem_price.getText().toString();

        if(!qty.isEmpty())
        {
            if(!qty.matches("^[0-9]*$"))
            {
                edititem_qty.setError("Enter numbers only");
                val = false;
            }
        }

        if(!price.isEmpty())
        {
            if(!price.matches("^[0-9]*$"))
            {
                edititem_price.setError("Enter numbers only");
                val = false;
            }
        }




        return val;
    }
}
