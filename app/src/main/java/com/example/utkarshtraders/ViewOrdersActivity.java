package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;

public class ViewOrdersActivity extends AppCompatActivity {

    private ImageView searchorder;
    private ImageView clearsearch;
    private EditText searchtext;
    private ImageView addcustomer;

    private FirebaseUser mCurrentUser;
    private boolean hasbeen;
    private String value;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private CollectionReference ordersRef = mFireStore.collection("orders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);
        setup();

        searchorder = findViewById(R.id.searchorder);
        clearsearch = findViewById(R.id.clearsearch);
        searchtext = findViewById(R.id.search_orders);
        addcustomer = findViewById(R.id.addorder);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);


        Intent intent = getIntent();
        final String customer_id = intent.getStringExtra("customer_id");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout o_list = findViewById(R.id.orderlist);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (TextUtils.isEmpty(searchtext.getText().toString())) {
                            searchorder.setClickable(false);
                        } else {
                            searchorder.setClickable(true);
                        }
                    }
                });
            }
        }, 0, 500);


        ordersRef.
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

                        vieworders.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent editorder = new Intent(getBaseContext(), ViewOrderInfoActivity.class);
                                editorder.putExtra("order_object", orders);
                                editorder.putExtra("order_id", order_id);
                                startActivity(editorder);
                                finish();
                                view.setOnClickListener(null);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        });
                    }
                }


            }

        });


        searchorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                o_list.removeAllViews();
                hasbeen = true;

                if (!TextUtils.isEmpty(searchtext.getText().toString())) {

                    value = searchtext.getText().toString();

                    if (Character.isDigit(value.charAt(0))) {

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
                                                            startActivity(editorder);
                                                            finish();
                                                            view.setOnClickListener(null);
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                        }
                                                    });
                                                }
                                            }


                                        } else {

                                            Toast.makeText(ViewOrdersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    } else if (Character.isLetter(value.charAt(0))) {
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
                                                            startActivity(editorder);
                                                            finish();
                                                            view.setOnClickListener(null);
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            Toast.makeText(ViewOrdersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(ViewOrdersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();
                    }
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
                    startActivity(refreshViewall);
                    finish();
                    view.setOnClickListener(null);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }
                else
                {
                    Toast.makeText(ViewOrdersActivity.this, "Nothing to clear here", Toast.LENGTH_SHORT).show();
                }

            }
        });


        addcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewOrdersActivity.this, ViewItemsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
                finish();
                view.setOnClickListener(null);
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
}