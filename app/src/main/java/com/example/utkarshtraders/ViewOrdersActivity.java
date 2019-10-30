package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.Timer;
import java.util.TimerTask;

public class ViewOrdersActivity extends AppCompatActivity {

    private ImageView searchbtn;
    private ImageView clearsearch;
    private EditText searchtext;

    private FirebaseUser mCurrentUser;
    private boolean hasbeen;
    private String value;
    private String customer_id;
    private String customer_name;

    private Button add_customer,home,settings;
    private ProgressDialog mProgressDialog;
    private ProgressDialog searchDialog;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private CollectionReference ordersRef = mFireStore.collection("orders");
    private Boolean count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);
        setup();

        searchbtn = findViewById(R.id.searchbtn);
        clearsearch = findViewById(R.id.clearsearch);
        searchtext = findViewById(R.id.searchtext);



        add_customer = findViewById(R.id.add_customer);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);
        mProgressDialog = new ProgressDialog(this);
        searchDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        customer_id = intent.getStringExtra("customer_id");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        customer_name = intent.getStringExtra("customer_name");



        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout o_list = findViewById(R.id.orderlist);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (TextUtils.isEmpty(searchtext.getText().toString())) {
                            searchbtn.setClickable(false);
                        } else {
                            searchbtn.setClickable(true);
                        }
                    }
                });
            }
        }, 0, 500);

        mProgressDialog.setTitle("Loading Orders");
        mProgressDialog.setMessage("Please wait while we load the Orders for the Customer");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        ordersRef.orderBy("date", Query.Direction.DESCENDING).
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    final Orders orders = documentSnapshot.toObject(Orders.class);

                    if (orders.getCustomerId().equals(customer_id)) {

                        String order_date = orders.getDate();
                        String order_item_name = orders.getItemName();
                        String order_price = "Rs: " + orders.getTotal();
                        final String order_id = documentSnapshot.getId();

                        final View Card = inflater.inflate(R.layout.activity_order_card, null);

                        final RelativeLayout vieworders = Card.findViewById(R.id.vieworders);

                        final TextView orderdate = Card.findViewById(R.id.order_date);
                        final TextView orderitemname = Card.findViewById(R.id.order_item_name);
                        final TextView orderprice = Card.findViewById(R.id.order_price);

                        orderdate.setText(order_date);
                        orderitemname.setText(order_item_name);
                        orderprice.setText(order_price);

                        o_list.addView(Card);
                        count = true;

                        vieworders.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent editorder = new Intent(getBaseContext(), ViewOrderInfoActivity.class);
                                editorder.putExtra("order_object", orders);
                                editorder.putExtra("order_id", order_id);
                                editorder.putExtra("customer_name",customer_name);
                                startActivity(editorder);
                                finish();
                                view.setOnClickListener(null);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        });
                    }
                    else
                    {
                        if(count == null) {
                            count = false;
                        }

                        if(count == true)
                        {
                            count=true;
                        }
                    }
                }
                mProgressDialog.dismiss();
                if(!count)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrdersActivity.this);
                    builder.setTitle("Orders");
                    builder.setMessage("No Orders Exist for this Customer");
                    builder.setNeutralButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        });




        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchDialog.setTitle("Loading Customers");
                searchDialog.setMessage("Please wait while we load the Customers");
                searchDialog.setCanceledOnTouchOutside(false);
                searchDialog.show();

                o_list.removeAllViews();
                hasbeen = true;

                if (!TextUtils.isEmpty(searchtext.getText().toString())) {

                    value = searchtext.getText().toString();

                    if (Character.isDigit(value.charAt(0))) {
                        count= true;
                        ordersRef
                                .whereEqualTo("date", value)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                final Orders orders = document.toObject(Orders.class);

                                                if (orders.getCustomerId().equals(customer_id)) {

                                                    String order_date = orders.getDate();
                                                    String order_item_name = orders.getItemName();
                                                    String order_price = "Rs: " + orders.getTotal();
                                                    final String order_id = document.getId();

                                                    final View Card = inflater.inflate(R.layout.activity_order_card, null);

                                                    final RelativeLayout vieworders = Card.findViewById(R.id.vieworders);

                                                    final TextView orderdate = Card.findViewById(R.id.order_date);
                                                    final TextView orderitemname = Card.findViewById(R.id.order_item_name);
                                                    final TextView orderprice = Card.findViewById(R.id.order_price);

                                                    orderdate.setText(order_date);
                                                    orderitemname.setText(order_item_name);
                                                    orderprice.setText(order_price);

                                                    o_list.addView(Card);

                                                    vieworders.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent editorder = new Intent(getBaseContext(), ViewOrderInfoActivity.class);
                                                            editorder.putExtra("order_object", orders);
                                                            editorder.putExtra("order_id", order_id);
                                                            editorder.putExtra("customer_name",customer_name);
                                                            startActivity(editorder);
                                                            finish();
                                                            view.setOnClickListener(null);
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                        }
                                                    });
                                                }
                                            }


                                        } else {
                                            count = false;
                                            Toast.makeText(ViewOrdersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    } else if (Character.isLetter(value.charAt(0))) {
                        count= true;
                        ordersRef
                                .whereEqualTo("itemName", value)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                final Orders orders = document.toObject(Orders.class);

                                                if (orders.getCustomerId().equals(customer_id)) {

                                                    String order_date = orders.getDate();
                                                    String order_item_name = orders.getItemName();
                                                    String order_price = "Rs: " + orders.getTotal();
                                                    final String order_id = document.getId();

                                                    final View Card = inflater.inflate(R.layout.activity_order_card, null);

                                                    final RelativeLayout vieworders = Card.findViewById(R.id.vieworders);

                                                    final TextView orderdate = Card.findViewById(R.id.order_date);
                                                    final TextView orderitemname = Card.findViewById(R.id.order_item_name);
                                                    final TextView orderprice = Card.findViewById(R.id.order_price);

                                                    orderdate.setText(order_date);
                                                    orderitemname.setText(order_item_name);
                                                    orderprice.setText(order_price);

                                                    o_list.addView(Card);

                                                    vieworders.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent editorder = new Intent(getBaseContext(), ViewOrderInfoActivity.class);
                                                            editorder.putExtra("order_object", orders);
                                                            editorder.putExtra("order_id", order_id);
                                                            editorder.putExtra("customer_name",customer_name);
                                                            startActivity(editorder);
                                                            finish();
                                                            view.setOnClickListener(null);
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            count = false;
                                            Toast.makeText(ViewOrdersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        count = false;
                        Toast.makeText(ViewOrdersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();
                    }
                }

                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        searchDialog.dismiss();
                        t.cancel();
                    }
                }, 1000);

                if(!count)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrdersActivity.this);
                    builder.setTitle("Customers");
                    builder.setMessage("Cannot find customer. Must be exact match");
                    builder.setNeutralButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }


            }
        });

        clearsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(hasbeen)
                {
                    searchtext.getText().clear();
                    Intent refreshViewall = new Intent(getBaseContext(),ViewOrdersActivity.class);
                    refreshViewall.putExtra("customer_id",customer_id);
                    refreshViewall.putExtra("customer_name",customer_name);
                    startActivity(refreshViewall);
                    finish();
                    view.setOnClickListener(null);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }
                else
                {
                    searchtext.getText().clear();
                    Toast.makeText(ViewOrdersActivity.this, "Nothing to clear here", Toast.LENGTH_SHORT).show();
                }

            }
        });

        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addcustomerintent = new Intent(ViewOrdersActivity.this, AddCustomerActivity.class);
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
        Intent edit = new Intent(getBaseContext(), CustomersActivity.class);
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
        getMenuInflater().inflate(R.menu.main_activity_bar, menu);
        setTitle("Orders for: " + customer_name);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.action_add:

            Intent intent = new Intent(ViewOrdersActivity.this, ViewItemsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("customer_id", customer_id);
            intent.putExtra("customer_name",customer_name);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
}