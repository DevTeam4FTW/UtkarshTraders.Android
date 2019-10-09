package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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

public class PlaceOrderActivity extends AppCompatActivity {

    private TextView date,item_name,taxrate,customer_total;
    private EditText item_qty, item_price;
    private Spinner area_spinner;
    private Button placeorder;
    private ToggleButton togglespecial;
    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference customerRef=mFireStore.collection("customer");
    private CollectionReference ordersRef=mFireStore.collection("orders");
    private CollectionReference areasRef=mFireStore.collection("areas");
    private Spinner bill_spinner,unit_spinner;
    private FirebaseUser mCurrentUser;

    private String bill_generator;
    private String unit_type;

    public String cust_id;
    boolean val;
    String default_area;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
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
        date = findViewById(R.id.date);
        item_name = findViewById(R.id.item_name);
        taxrate = findViewById(R.id.taxrate);
        customer_total = findViewById(R.id.customer_total);
        item_qty = findViewById(R.id.item_qty);
        item_price = findViewById(R.id.item_price);
        area_spinner = findViewById(R.id.area_spinner);
        placeorder = findViewById(R.id.placeorder);
        togglespecial = findViewById(R.id.togglespecial);
        bill_spinner = findViewById(R.id.bill_spinner);
        unit_spinner = findViewById(R.id.unit_spinner);

        Intent intent = getIntent();
        final Items items = intent.getParcelableExtra("item_object");
        cust_id = intent.getStringExtra("customer_id");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        item_price.setEnabled(false);

        togglespecial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    item_price.setEnabled(true);
                } else {
                    item_price.setText(items.getItemPrice());
                    item_price.setEnabled(false);

                }
            }

        });

        long datemnow = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String dateString = sdf.format(datemnow);
        date.setText(dateString);

        item_name.setText(items.getItemName());
        item_price.setText(items.getItemPrice());
        taxrate.setText(items.getTaxRate());

        ArrayAdapter<CharSequence> billadapter = ArrayAdapter.createFromResource(this, R.array.seller_prompts, android.R.layout.simple_spinner_item);
        billadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bill_spinner.setAdapter(billadapter);

        ArrayAdapter<CharSequence> unitadapter = ArrayAdapter.createFromResource(this, R.array.unit_prompts, android.R.layout.simple_spinner_item);
        unitadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit_spinner.setAdapter(unitadapter);


        customerRef.document(cust_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists() && documentSnapshot != null) {

                        default_area = documentSnapshot.getString("clientArea");
                    }
                }
            }
        });

        final List<String> areas = new ArrayList<>();
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        area_spinner.setAdapter(areaAdapter);
        areasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String areaName = document.getString("areaName");
                        areas.add(areaName);
                    }
                    areaAdapter.notifyDataSetChanged();
                    area_spinner.setSelection(areaAdapter.getPosition(default_area));
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
        }, 0, 500);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(item_qty.getText().toString()) && !TextUtils.isEmpty(item_price.getText().toString()) && item_qty.getText().toString().matches("^[0-9]*$") && item_price.getText().toString().matches("^[0-9]*$"))
                {
                    final Float c_total = (Float .parseFloat(item_qty.getText().toString()) * Float .parseFloat(item_price.getText().toString()));
                    customer_total.setText(c_total.toString());

                }

            }
        }, 0, 1000);


        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(customer_total.getText().toString()) && !TextUtils.isEmpty(item_qty.getText().toString()) && val) {

                    if (bill_spinner.getSelectedItem().toString().equals("Utkarsh")) {
                        bill_generator = "1";
                    } else {
                        bill_generator = "2";
                    }

                    String customerArea = area_spinner.getSelectedItem().toString();
                    String customerId = cust_id;
                    String datefield = date.getText().toString();
                    String itemName = item_name.getText().toString();
                    String itemPrice = item_price.getText().toString();
                    String itemQuantity = item_qty.getText().toString();
                    String orderId = cust_id + date.getText().toString();
                    String orderStatus = "true";
                    String salesmanId = mCurrentUser.getUid();
                    Float taxTotalFloat = Float.parseFloat(item_qty.getText().toString()) * Float.parseFloat(item_price.getText().toString());
                    String taxTotal = taxTotalFloat.toString();
                    Float taxableRateFloat = Float.parseFloat(item_price.getText().toString()) - (Float.parseFloat(item_price.getText().toString()) * (Float.parseFloat(taxrate.getText().toString())/100));
                    String taxableRate = taxableRateFloat.toString();
                    String tax_rate = taxrate.getText().toString();
                    String total = taxTotal;


                    if (unit_spinner.getSelectedItem().toString().equals("Per/Kg")) {
                         unit_type = "per/kg";
                    } else if (unit_spinner.getSelectedItem().toString().equals("Per/Piece")) {
                         unit_type = "per/pc";
                    } else {
                         unit_type = "per/dozen";
                    }

                    Map<String, String> orderMap = new HashMap<>();

                    orderMap.put("billGenerator", bill_generator);
                    orderMap.put("customerArea", customerArea);
                    orderMap.put("customerId", cust_id);
                    orderMap.put("date", datefield);
                    orderMap.put("itemName",itemName);
                    orderMap.put("itemPrice", itemPrice);
                    orderMap.put("itemQuantity",itemQuantity);
                    orderMap.put("orderId",orderId);
                    orderMap.put("orderStatus",orderStatus);
                    orderMap.put("salesmanId",salesmanId);
                    orderMap.put("taxTotal", taxTotal);
                    orderMap.put("taxableRate", taxableRate);
                    orderMap.put("taxrate", tax_rate);
                    orderMap.put("total", total);
                    orderMap.put("unit", unit_type);

                    ordersRef.add(orderMap);
                    Toast.makeText(PlaceOrderActivity.this, "Order added successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(PlaceOrderActivity.this, ViewOrdersActivity.class);
                    intent.putExtra("customer_id",cust_id);
                    startActivity(intent);
                    finish();
                    view.setOnClickListener(null);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }

                else
                {
                    Toast.makeText(PlaceOrderActivity.this,"Enter all fields before placing order ",Toast.LENGTH_SHORT).show();
                }







            }
        });




    }

    @Override
    public void onBackPressed() {
        Intent edit = new Intent(getBaseContext(), ViewItemsActivity.class);
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
        String qty = item_qty.getText().toString();
        String price = item_price.getText().toString();

        if(!qty.isEmpty())
        {
            if(!qty.matches("^[0-9]*$"))
            {
                item_qty.setError("Enter numbers only");
                val = false;
            }
        }

        if(!price.isEmpty())
        {
            if(!price.matches("^[0-9]*$"))
            {
                item_price.setError("Enter numbers only");
                val = false;
            }
        }




        return val;
    }
}
