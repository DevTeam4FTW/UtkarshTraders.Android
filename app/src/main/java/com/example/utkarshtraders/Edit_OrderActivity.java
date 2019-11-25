package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Edit_OrderActivity extends AppCompatActivity {

    private TextView editdate,edititem_name,edittaxrate,editcustomer_total,edititem_price;
    private EditText edititem_qty;
    private Button editplaceorder;
    private ToggleButton edittogglespecial;
    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference ordersRef=mFireStore.collection("orders");
    private CollectionReference areasRef=mFireStore.collection("areas");
    private CollectionReference itemRef = mFireStore.collection("items");
    private Spinner editbill_spinner,editunit_spinner,editarea_spinner,editspecial_spinner;

    private LinearLayout special_layout;

    private String editbill_generator;
    private String editunit_type,customer_name;

    public String cust_id;
    String default_bill;
    String default_unit;
    String default_area;
    Float c_total;
    boolean val;
    String item_id;
    String price_typedb;

    String dbsetprice;

    private Button add_customer,home,settings;

    private String[] specialtypes,specialprices;
    private HashMap<String,String> specials;

    private ArrayList<HashMap<String,String>> item_price_types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__order);
        setup();

        add_customer = findViewById(R.id.add_customer);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);

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
        editspecial_spinner = findViewById(R.id.editspecial_spinner);
        special_layout = findViewById(R.id.special_layout);
        specials = new HashMap<String, String>();

        Intent intent = getIntent();
        final Orders orders = intent.getParcelableExtra("order_object");
        final String order_id = intent.getStringExtra("order_id");
        customer_name = intent.getStringExtra("customer_name");
        dbsetprice = intent.getStringExtra("dbsetprice");

        item_price_types = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("itempricetypes");


        cust_id = orders.getCustomerId();



        specialtypes = new String[item_price_types.size()];
        specialprices = new String[item_price_types.size()];


        edittogglespecial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    special_layout.setVisibility(View.VISIBLE);
                    String defaultprice = editspecial_spinner.getSelectedItem().toString();
                    String pricetoshow = specials.get(defaultprice);
                    edititem_price.setText(pricetoshow);
                    price_typedb = defaultprice;

                    editspecial_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String defaultpr = editspecial_spinner.getSelectedItem().toString();
                            String pricetoshowalso = specials.get(defaultpr);
                            edititem_price.setText(pricetoshowalso);
                            price_typedb = defaultpr;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });
                } else {

                    edititem_price.setText(dbsetprice);
                    special_layout.setVisibility(View.GONE);
                    price_typedb = "default";
                }
            }

        });

        for (int i=0;i<item_price_types.size();i++)
        {
            HashMap<String, String> hashmap= item_price_types.get(i);
            String types= hashmap.get("ct");
            String prices= hashmap.get("price");
            specialtypes[i] = types+"(Rs:"+prices+")";
            specials.put(types+"(Rs:"+prices+")",prices);
        }


        ArrayAdapter<CharSequence> specialAdapter = new ArrayAdapter<CharSequence>(this,R.layout.spinner_item,specialtypes);
        specialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editspecial_spinner.setAdapter(specialAdapter);


        //defaults
        if(!orders.getPriceType().equals("default"))
        {
            edittogglespecial.setChecked(true);
            special_layout.setVisibility(View.VISIBLE);
            editspecial_spinner.setSelection(specialAdapter.getPosition(orders.getPriceType()));

            String defaultprice = editspecial_spinner.getSelectedItem().toString();
            String price2show = specials.get(defaultprice);
            edititem_price.setText(price2show);
            price_typedb = defaultprice;


        }
        else
        {
            edititem_price.setText(dbsetprice);
            special_layout.setVisibility(View.GONE);
            price_typedb = "default";

        }


        editdate.setText(orders.getDate());
        edititem_name.setText(orders.getItemName());
        edittaxrate.setText(orders.getTaxrate());

        default_area = orders.getCustomerArea();

        final List<String> areas = new ArrayList<>();
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, areas);
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

        ArrayAdapter<CharSequence> billadapter = ArrayAdapter.createFromResource(this, R.array.seller_prompts, R.layout.spinner_item);
        billadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editbill_spinner.setAdapter(billadapter);

        editbill_spinner.setSelection(billadapter.getPosition(default_bill));

        ArrayAdapter<CharSequence> unitadapter = ArrayAdapter.createFromResource(this, R.array.unit_prompts, R.layout.spinner_item);
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

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                if(!TextUtils.isEmpty(edititem_qty.getText().toString()) && !TextUtils.isEmpty(edititem_price.getText().toString()) && edititem_qty.getText().toString().matches("^[0-9]*$") && edititem_price.getText().toString().matches("^[0-9]*$") )
                {
                    c_total = (Float .parseFloat(edititem_qty.getText().toString()) * Float .parseFloat(edititem_price.getText().toString()));
                    editcustomer_total.setText(c_total.toString());

                }

                    }
                });

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


                    Float edittotalFloat = Float.parseFloat(edititem_qty.getText().toString()) * Float.parseFloat(edititem_price.getText().toString());
                    String edittotal = edittotalFloat.toString();

                    Float edittaxTotalFloat = edittotalFloat - (( edittotalFloat * Float.parseFloat(edittaxrate.getText().toString())) / (100 + Float.parseFloat(edittaxrate.getText().toString())));
                    String edittaxTotal = edittaxTotalFloat.toString();

                    Float edittaxableRateFloat = (edittotalFloat / Float.parseFloat(edititem_qty.getText().toString())) - ((edittotalFloat / Float.parseFloat(edititem_qty.getText().toString())) * Float.parseFloat(edittaxrate.getText().toString()) / (100 + Float.parseFloat(edittaxrate.getText().toString())));
                    String edittaxableRate = String.valueOf(edittaxableRateFloat);


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
                                    "priceType",price_typedb,
                                    "taxTotal",edittaxTotal ,
                                    "taxableRate",edittaxableRate ,
                                    "total",edittotal,
                                    "unit",editunit_type
                            );
                    Toast.makeText(Edit_OrderActivity.this, "Order edited successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Edit_OrderActivity.this, ViewOrdersActivity.class);
                    intent.putExtra("customer_id",cust_id);
                    intent.putExtra("customer_name",customer_name);
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

        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addcustomerintent = new Intent(Edit_OrderActivity.this, AddCustomerActivity.class);
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
        setTitle("Edit Order");
        return true;
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
