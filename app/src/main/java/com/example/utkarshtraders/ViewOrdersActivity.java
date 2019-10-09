package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewOrdersActivity extends AppCompatActivity {

    private ImageView searchaddorder;

    private FirebaseUser mCurrentUser;

    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference ordersRef=mFireStore.collection("orders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);
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
        searchaddorder = findViewById(R.id.searchaddorder);

        Intent intent = getIntent();
        final String customer_id = intent.getStringExtra("customer_id");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout o_list = findViewById(R.id.orderlist);

        ordersRef.
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        final Orders orders = documentSnapshot.toObject(Orders.class);

                        if(orders.getCustomerId().equals(customer_id))
                        {

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
                                    Intent editorder = new Intent(getBaseContext(),ViewOrderInfoActivity.class);
                                    editorder.putExtra("order_object",orders);
                                    editorder.putExtra("order_id",order_id);
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

        searchaddorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewOrdersActivity.this, ViewItemsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("customer_id",customer_id);
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
