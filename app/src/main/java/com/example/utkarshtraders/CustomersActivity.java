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
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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

import java.util.Timer;
import java.util.TimerTask;

public class CustomersActivity extends AppCompatActivity {


    private FirebaseUser mCurrentUser;
    private ImageView searchbtn;
    private ImageView clearsearch;
    private ImageView addcust;
    private EditText searchtext;

    boolean hasbeen = false;
    LinearLayout c_list;
    String value;

    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference salesmanRef=mFireStore.collection("salesman");
    private CollectionReference customerRef=mFireStore.collection("customer");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        addcust = findViewById(R.id.addcust);
        searchtext = findViewById(R.id.search_customer);
        searchbtn = findViewById(R.id.searchbtn);
        clearsearch = findViewById(R.id.clearsearch);



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

        c_list = findViewById(R.id.customerlist);

        if (mCurrentUser == null) {
            Intent loginintent = new Intent(CustomersActivity.this, LoginActivity.class);
            loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginintent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if(TextUtils.isEmpty(searchtext.getText().toString()))
                        {
                            searchbtn.setClickable(false);
                        }
                        else
                        {
                            searchbtn.setClickable(true);
                        }

                    }
                });


            }
        }, 0, 500);






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

        addcust.setOnClickListener(new View.OnClickListener() {
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

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c_list.removeAllViews();
                hasbeen = true;

                if(!TextUtils.isEmpty(searchtext.getText().toString())) {

                    value = searchtext.getText().toString();

                    if (Character.isDigit(value.charAt(0))) {

                        customerRef
                                .whereEqualTo("clientPhoneNo", value)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                final Customers customers = document.toObject(Customers.class);

                                                final String customer_id = document.getId();

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
                                        } else {

                                            Toast.makeText(CustomersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    } else if(Character.isLetter(value.charAt(0))) {
                        customerRef
                                .whereEqualTo("clientName", value)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                final Customers customers = document.toObject(Customers.class);
                                                final String customer_id = document.getId();

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

                                                        Intent edit = new Intent(getBaseContext(), ViewCustomerInfoActivity.class);
                                                        edit.putExtra("customer_object", customers);
                                                        edit.putExtra("customer_id", customer_id);
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
                                                        order.putExtra("customer_id", customer_id);
                                                        startActivity(order);
                                                        finish();
                                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                        view.setOnClickListener(null);

                                                    }
                                                });
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(CustomersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(CustomersActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();
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
                    Intent refreshViewall = new Intent(getBaseContext(),CustomersActivity.class);
                    startActivity(refreshViewall);
                    finish();
                    view.setOnClickListener(null);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }
                else
                {
                    Toast.makeText(CustomersActivity.this, "Nothing to clear here", Toast.LENGTH_SHORT).show();
                }

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


