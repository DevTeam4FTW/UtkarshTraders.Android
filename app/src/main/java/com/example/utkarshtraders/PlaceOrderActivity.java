package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PlaceOrderActivity extends AppCompatActivity {

    private TextView item_name,taxrate,customer_total,date,item_price,hsnno;
    private EditText item_qty;
    private Spinner area_spinner;
    private Button placeorder;
    private ToggleButton togglespecial;
    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference customerRef=mFireStore.collection("customer");
    private CollectionReference ordersRef=mFireStore.collection("orders");
    private CollectionReference areasRef=mFireStore.collection("areas");
    private Spinner bill_spinner,unit_spinner,special;
    private FirebaseUser mCurrentUser;

    private String bill_generator;
    private String unit_type;

    private String date_string;
    private String cust_id;
    boolean val;
    private String default_area,customer_name;
    private Button add_customer,home,settings;

    private String item_id;
    private String price_typedb;

    private String[] specialtypes,specialprices;
    private HashMap<String,String> specials;

    private LinearLayout special_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        setup();


        add_customer = findViewById(R.id.add_customer);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);
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
        special = findViewById(R.id.special_spinner);
        date = findViewById(R.id.date);
        specials = new HashMap<String, String>();
        price_typedb = "default";
        hsnno = findViewById(R.id.hsnno);



        special_layout = findViewById(R.id.special_layout);

        Intent intent = getIntent();
        final Items items = intent.getParcelableExtra("item_object");
        item_id = intent.getStringExtra("item_id");
        cust_id = intent.getStringExtra("customer_id");
        customer_name = intent.getStringExtra("customer_name");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        item_price.setEnabled(false);
        special_layout.setVisibility(View.GONE);

        specialtypes = new String[items.getCatgprice().size()];
        specialprices = new String[items.getCatgprice().size()];

        togglespecial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    item_price.setEnabled(true);
                    special_layout.setVisibility(View.VISIBLE);
                    String defaultprice = special.getSelectedItem().toString();
                    String pricetoshow = specials.get(defaultprice);
                    item_price.setText(pricetoshow);
                    price_typedb = defaultprice;


                    special.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String defaultpr = special.getSelectedItem().toString();
                            String pricetoshowalso = specials.get(defaultpr);
                            item_price.setText(pricetoshowalso);
                            price_typedb = defaultpr;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });

                } else {
                    item_price.setText(items.getPrice());
                    item_price.setEnabled(false);
                    special_layout.setVisibility(View.GONE);
                    price_typedb = "default";

                }
            }

        });

        for (int i=0;i<items.getCatgprice().size();i++)
        {
            HashMap<String, String> hashmap= items.getCatgprice().get(i);
            String types= hashmap.get("ct");
            String prices= hashmap.get("price");
            specialtypes[i] = types+"(Rs:"+prices+")";
            specials.put(types+"(Rs:"+prices+")",prices);
        }


        ArrayAdapter<CharSequence> specialAdapter = new ArrayAdapter<CharSequence>(this,R.layout.spinner_item,specialtypes);
        specialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        special.setAdapter(specialAdapter);


        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = new Date();
        date_string = currentDate.format(todayDate);

        date.setText(date_string);

        item_name.setText(items.getName());
        item_price.setText(items.getPrice());
        taxrate.setText(items.getTaxRate());
        hsnno.setText(items.getHsnNo());

        ArrayAdapter<CharSequence> billadapter = ArrayAdapter.createFromResource(this, R.array.seller_prompts, R.layout.spinner_item);
        billadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bill_spinner.setAdapter(billadapter);

        ArrayAdapter<CharSequence> unitadapter = ArrayAdapter.createFromResource(this, R.array.unit_prompts, R.layout.spinner_item);
        unitadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit_spinner.setAdapter(unitadapter);


        customerRef.document(cust_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        default_area = document.getString("clientArea");
                    } else {
                        Toast.makeText(PlaceOrderActivity.this, "Area doesnt exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PlaceOrderActivity.this, "Area doesnt exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final List<String> areas = new ArrayList<>();
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, areas);
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

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if(!TextUtils.isEmpty(item_qty.getText().toString()) && !TextUtils.isEmpty(item_price.getText().toString()) && item_qty.getText().toString().matches("^[0-9]*$") && item_price.getText().toString().matches("^[0-9]*$"))
                        {
                            final Float c_total = (Float .parseFloat(item_qty.getText().toString()) * Float .parseFloat(item_price.getText().toString()));
                            customer_total.setText(c_total.toString());

                        }

                    }
                });


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
                    String datefield = date_string;
                    String hsnNo = hsnno.getText().toString();
                    String itemName = item_name.getText().toString();
                    String itemPrice = item_price.getText().toString();
                    String itemQuantity = item_qty.getText().toString();
                    String orderId = cust_id + date_string;
                    String orderStatus = "true";
                    String salesmanId = mCurrentUser.getUid();
                    String tax_rate = taxrate.getText().toString();

                    Float totalFloat = Float.parseFloat(item_qty.getText().toString()) * Float.parseFloat(item_price.getText().toString());
                    String total = totalFloat.toString();

                    Float taxTotalFloat = totalFloat - (( totalFloat * Float.parseFloat(taxrate.getText().toString())) / (100 + Float.parseFloat(taxrate.getText().toString())));
                    String taxTotal = taxTotalFloat.toString();

                    Float taxableRateFloat = (totalFloat / Float.parseFloat(item_qty.getText().toString())) - ((totalFloat / Float.parseFloat(item_qty.getText().toString())) * Float.parseFloat(taxrate.getText().toString()) / (100 + Float.parseFloat(taxrate.getText().toString())));
                    String taxableRate = String.valueOf(taxableRateFloat);


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
                    orderMap.put("hsnNo",hsnNo);
                    orderMap.put("itemId",item_id);
                    orderMap.put("itemName",itemName);
                    orderMap.put("itemPrice", itemPrice);
                    orderMap.put("itemQuantity",itemQuantity);
                    orderMap.put("orderId",orderId);
                    orderMap.put("orderStatus",orderStatus);
                    orderMap.put("priceType",price_typedb);
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
                    intent.putExtra("customer_name",customer_name);
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

        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addcustomerintent = new Intent(PlaceOrderActivity.this, AddCustomerActivity.class);
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
        Intent edit = new Intent(getBaseContext(), ViewItemsActivity.class);
        edit.putExtra("customer_id",cust_id);
        edit.putExtra("customer_name",customer_name);
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
        setTitle("Place Order");
        return true;

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
