package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class CustomersActivity extends AppCompatActivity {


    private FirebaseUser mCurrentUser;
    private ImageView searchadd;

    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference salesmanRef=mFireStore.collection("salesman");
    private CollectionReference customerRef=mFireStore.collection("customer");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        searchadd = findViewById(R.id.searchadd);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);


        setup();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final LinearLayout c_list = findViewById(R.id.customerlist);

        if (mCurrentUser == null) {
            Intent loginintent = new Intent(CustomersActivity.this, LoginActivity.class);
            loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginintent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }





        customerRef.
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {




                    final Customers customers = documentSnapshot.toObject(Customers.class);
                    final String customer_id = documentSnapshot.getId();

                    String name = customers.getClientName();
                    String phoneno = customers.getClientPhoneNo();


                    final View Card = inflater.inflate(R.layout.activity_customer_card, null);

                    final RelativeLayout viewCustomerorders = Card.findViewById(R.id.viewcustomers);

                    final TextView c_name = Card.findViewById(R.id.c_name);
                    final TextView c_phno = Card.findViewById(R.id.c_phno);
                    final ImageView edit_customer = Card.findViewById(R.id.edit_customer);

                    c_name.setText(name);
                    c_phno.setText(phoneno);

                    c_list.addView(Card);

                    edit_customer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent edit = new Intent(getBaseContext(),ViewCustomerInfoActivity.class);
                            edit.putExtra("customer_object",customers);
                            edit.putExtra("customer_id",customer_id);
                            startActivity(edit);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            view.setOnClickListener(null);
                        }
                    });

                    viewCustomerorders.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent order = new Intent(getBaseContext(), ViewOrdersActivity.class);
                            order.putExtra("customer_id",customer_id);
                            startActivity(order);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            view.setOnClickListener(null);

                        }
                    });


                }


            }
        });

        searchadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addcustomerintent = new Intent(CustomersActivity.this, AddCustomerActivity.class);
                addcustomerintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(addcustomerintent);
                finish();
                view.setOnClickListener(null);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });






    }
    public void setup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }



}


