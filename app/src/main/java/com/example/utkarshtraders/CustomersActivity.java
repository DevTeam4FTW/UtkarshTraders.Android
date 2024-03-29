package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CustomersActivity extends AppCompatActivity {


    private FirebaseUser mCurrentUser;
    private ImageView searchbtn;
    private ImageView clearsearch;
    private EditText searchtext;

    boolean hasbeen = false;
    LinearLayout c_list;
    String value;
    String filtervalue;
    private ProgressDialog mProgressDialog;
    private ProgressDialog searchDialog;
    private Spinner filterarea;
    private Button filterbtn;

    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference salesmanRef=mFireStore.collection("salesman");
    private CollectionReference customerRef=mFireStore.collection("customer");
    private CollectionReference areasRef=mFireStore.collection("areas");

    Boolean filtercount = false;


    private Button add_customer,settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

          add_customer = findViewById(R.id.add_customer);
          settings = findViewById(R.id.settings);
          mProgressDialog = new ProgressDialog(this);
        searchDialog = new ProgressDialog(this);
        searchtext = findViewById(R.id.searchtext);
        searchbtn = findViewById(R.id.searchbtn);
        clearsearch = findViewById(R.id.clearsearch);
        filterarea = findViewById(R.id.filterarea);
        filterbtn = findViewById(R.id.filterbtn);



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

        final List<String> areas = new ArrayList<>();
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterarea.setAdapter(areaAdapter);
        areasRef.orderBy("areaName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String areaName = document.getString("areaName");
                        areas.add(areaName);
                    }
                    areaAdapter.notifyDataSetChanged();
                }
            }
        });

        final TextView loadMsg = new TextView(this);
        loadMsg.setText("Choose area from the dropdown and press 'Filter' to load customers. Alternatively, type the name or phone number to search for required customer");
        c_list.addView(loadMsg);


        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c_list.removeAllViews();

                searchDialog.setTitle("Loading Customers");
                searchDialog.setMessage("Please wait while we load the Customers");
                searchDialog.setCanceledOnTouchOutside(false);
                searchDialog.show();

                if(!TextUtils.isEmpty(filterarea.getSelectedItem().toString())) {

                    filtervalue = filterarea.getSelectedItem().toString();

                    customerRef.whereEqualTo("clientArea", filtervalue).
                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                filtercount = true;


                                final Customers customers = documentSnapshot.toObject(Customers.class);
                                final String customer_id = documentSnapshot.getId();

                                String name = customers.getClientName();
                                String phoneno = customers.getClientPhoneNo();


                                final View Card = inflater.inflate(R.layout.activity_customer_card, null);

                                final RelativeLayout viewCustomerorders = Card.findViewById(R.id.viewcustomers);

                                final TextView c_name = Card.findViewById(R.id.c_name);
                                final TextView c_phno = Card.findViewById(R.id.c_phno);
                                final Button edit_customer = Card.findViewById(R.id.edit_customer);

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
                                        order.putExtra("customer_name", c_name.getText().toString());
                                        startActivity(order);
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        view.setOnClickListener(null);

                                    }
                                });


                            }

                            searchDialog.dismiss();

                            if(!filtercount)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomersActivity.this);
                                builder.setTitle("Customers");
                                builder.setMessage("No customer are registered with the Area");
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
                }

                filtercount = false;

                }
        });




        searchbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                c_list.removeAllViews();

                searchDialog.setTitle("Loading Customers");
                searchDialog.setMessage("Please wait while we load the Customers");
                searchDialog.setCanceledOnTouchOutside(false);
                searchDialog.show();

                hasbeen = true;

                if(!TextUtils.isEmpty(searchtext.getText().toString())) {

                    value = searchtext.getText().toString();

                    if (Character.isDigit(value.charAt(0))) {
                        customerRef.orderBy("clientPhoneNo").startAt(value).endAt(value + "\uf8ff")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {



                                                if(document.exists()) {

                                                    final Customers customers = document.toObject(Customers.class);

                                                    final String customer_id = document.getId();

                                                    String name = customers.getClientName();
                                                    String phoneno = customers.getClientPhoneNo();


                                                    final View Card = inflater.inflate(R.layout.activity_customer_card, null);

                                                    final RelativeLayout viewCustomerorders = Card.findViewById(R.id.viewcustomers);

                                                    final TextView c_name = Card.findViewById(R.id.c_name);
                                                    final TextView c_phno = Card.findViewById(R.id.c_phno);
                                                    final Button edit_customer = Card.findViewById(R.id.edit_customer);

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
                                                            order.putExtra("customer_name", c_name.getText().toString());
                                                            startActivity(order);
                                                            finish();
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                            view.setOnClickListener(null);

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }


                                });
                    } else if(Character.isLetter(value.charAt(0))) {

                        customerRef.orderBy("clientName").startAt(value).endAt(value + "\uf8ff")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {



                                                if(document.exists()) {

                                                    final Customers customers = document.toObject(Customers.class);
                                                    final String customer_id = document.getId();

                                                    String name = customers.getClientName();
                                                    String phoneno = customers.getClientPhoneNo();


                                                    final View Card = inflater.inflate(R.layout.activity_customer_card, null);

                                                    final RelativeLayout viewCustomerorders = Card.findViewById(R.id.viewcustomers);

                                                    final TextView c_name = Card.findViewById(R.id.c_name);
                                                    final TextView c_phno = Card.findViewById(R.id.c_phno);
                                                    final Button edit_customer = Card.findViewById(R.id.edit_customer);

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
                                                            order.putExtra("customer_name", c_name.getText().toString());
                                                            startActivity(order);
                                                            finish();
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                            view.setOnClickListener(null);

                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    }
                                });
                    }

                }

                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        searchDialog.dismiss();
                        t.cancel();
                    }
                }, 1000);
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
                    searchtext.getText().clear();
                    Toast.makeText(CustomersActivity.this, "Nothing to clear here", Toast.LENGTH_SHORT).show();
                }

            }
        });

        add_customer.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_bar_empty, menu);
        setTitle("View Customers");
        return true;
    }

    @Override
    protected void onDestroy()
    {
        mProgressDialog.dismiss();
        super.onDestroy();
    }

}


