package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ViewItemsActivity extends AppCompatActivity {


    private FirebaseUser mCurrentUser;

    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference itemsRef=mFireStore.collection("items");
    private CollectionReference packageRef=mFireStore.collection("cS");
    private CollectionReference companyRef=mFireStore.collection("cN");
    private CollectionReference categoriesRef = mFireStore.collection("categories");

    private ImageView searchbtn;
    private ImageView clearsearch;
    private EditText searchtext;

    private String filtercat,filtercomp,filterpackage;

    boolean hasbeen = false;
    private LinearLayout i_list;

    private String customer_id;
    private String customer_name;
    private Button add_customer,home,settings;
    private ProgressDialog mProgressDialog;
    private ProgressDialog searchDialog;

    private Spinner filterCategory;
    private Spinner filterCompany;
    private Spinner filterPackaging;

    private String item_id;

    private Button filterItem;

    private boolean count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);
        setup();

        searchbtn = findViewById(R.id.searchbtn);
        clearsearch = findViewById(R.id.clearsearch);
        searchtext = findViewById(R.id.searchtext);

        filterCategory = findViewById(R.id.filterCategory);
        filterCompany = findViewById(R.id.filterCompany);
        filterPackaging = findViewById(R.id.filterPackaging);
        filterItem = findViewById(R.id.filteritembtn);

        add_customer = findViewById(R.id.add_customer);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);
        mProgressDialog = new ProgressDialog(this);
        searchDialog = new ProgressDialog(this);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        i_list = findViewById(R.id.itemlist);

        Intent intent = getIntent();
        customer_id = intent.getStringExtra("customer_id");
        customer_name = intent.getStringExtra("customer_name");

        final TextView loadMsg = new TextView(this);
        loadMsg.setText("Choose values from the dropdowns and press 'Filter' to load Items. Alternatively, type the item name to search");
        i_list.addView(loadMsg);


        final List<String> categories = new ArrayList<>();
        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterCategory.setAdapter(categoryAdapter);
        categoriesRef.orderBy("categoryName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String category = document.getString("categoryName");
                        categories.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });

        final List<String> companies = new ArrayList<>();
        final ArrayAdapter<String> companiesAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, companies);
        companiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterCompany.setAdapter(companiesAdapter);
        companyRef.orderBy("cn").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String company_name = document.getString("cn");
                        companies.add(company_name);
                    }
                    companiesAdapter.notifyDataSetChanged();
                }
            }
        });

        final List<String> packaging = new ArrayList<>();
        final ArrayAdapter<String> packagingAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, packaging);
        packagingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterPackaging.setAdapter(packagingAdapter);
        packageRef.orderBy("cS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String package_name = document.getString("cS");
                        packaging.add(package_name);
                    }
                    packagingAdapter.notifyDataSetChanged();
                }
            }
        });



        filterItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i_list.removeAllViews();

                searchDialog.setTitle("Loading Items");
                searchDialog.setMessage("Please wait while we load the Items");
                searchDialog.setCanceledOnTouchOutside(false);
                searchDialog.show();

                if(!TextUtils.isEmpty(filterCategory.getSelectedItem().toString()) && !TextUtils.isEmpty(filterCompany.getSelectedItem().toString()) && !TextUtils.isEmpty(filterPackaging.getSelectedItem().toString())) {


                    filtercat = filterCategory.getSelectedItem().toString();
                    filtercomp = filterCompany.getSelectedItem().toString();
                    filterpackage = filterPackaging.getSelectedItem().toString();


                    itemsRef.whereEqualTo("category",filtercat).whereEqualTo("cname",filtercomp).whereEqualTo("ics",filterpackage).
                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                item_id = documentSnapshot.getId();
                                final Items items = documentSnapshot.toObject(Items.class);

                                String item_name = items.getName();
                                String default_price = "Rs: " + items.getPrice();

                                final View Card = inflater.inflate(R.layout.activity_item_card, null);

                                final RelativeLayout viewitems = Card.findViewById(R.id.viewitems);

                                final TextView i_name = Card.findViewById(R.id.item_name);
                                final TextView i_price = Card.findViewById(R.id.item_price);
                                final ImageView additemtoorder = Card.findViewById(R.id.additemtoorder);

                                i_name.setText(item_name);
                                i_price.setText(default_price);


                                i_list.addView(Card);

                                additemtoorder.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent additemtoorder = new Intent(getBaseContext(), PlaceOrderActivity.class);
                                        additemtoorder.putExtra("item_object", items);
                                        additemtoorder.putExtra("item_id",item_id);
                                        additemtoorder.putExtra("customer_id", customer_id);
                                        additemtoorder.putExtra("customer_name",customer_name);
                                        startActivity(additemtoorder);
                                        finish();
                                        view.setOnClickListener(null);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                    }
                                });
                            }

                            searchDialog.dismiss();

                        }
                    });




                }
            }
        });





        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addcustomerintent = new Intent(ViewItemsActivity.this, AddCustomerActivity.class);
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


            searchbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    searchDialog.setTitle("Loading Items");
                    searchDialog.setMessage("Please wait while we load the Items");
                    searchDialog.setCanceledOnTouchOutside(false);
                    searchDialog.show();

                    i_list.removeAllViews();
                    hasbeen = true;

                    if(!TextUtils.isEmpty(searchtext.getText().toString())) {
                        count=true;

                        String value = searchtext.getText().toString();

                        itemsRef.orderBy("itemName").startAt(value).endAt(value + "\uf8ff")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                item_id = document.getId();
                                                final Items items = document.toObject(Items.class);

                                                String item_name = items.getName();
                                                String default_price = "Rs: " + items.getPrice();

                                                final View Card = inflater.inflate(R.layout.activity_item_card, null);

                                                final RelativeLayout viewitems = Card.findViewById(R.id.viewitems);

                                                final TextView i_name = Card.findViewById(R.id.item_name);
                                                final TextView i_price = Card.findViewById(R.id.item_price);
                                                final ImageView additemtoorder = Card.findViewById(R.id.additemtoorder);

                                                i_name.setText(item_name);
                                                i_price.setText(default_price);

                                                i_list.addView(Card);

                                                additemtoorder.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent additemtoorder = new Intent(getBaseContext(), PlaceOrderActivity.class);
                                                        additemtoorder.putExtra("item_object", items);
                                                        additemtoorder.putExtra("item_name",item_id);
                                                        additemtoorder.putExtra("customer_id", customer_id);
                                                        additemtoorder.putExtra("customer_name",customer_name);
                                                        startActivity(additemtoorder);
                                                        finish();
                                                        view.setOnClickListener(null);
                                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                                    }
                                                });
                                            }
                                        } else {
                                            count = false;
                                            Toast.makeText(ViewItemsActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                    else
                    {
                        count=false;
                        Toast.makeText(ViewItemsActivity.this, "Nothing to Show here", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewItemsActivity.this);
                        builder.setTitle("Items");
                        builder.setMessage("Cannot find Item. Data must begin with search parameter entered.");
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
                    Intent refreshViewall = new Intent(getBaseContext(),ViewItemsActivity.class);
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
                    Toast.makeText(ViewItemsActivity.this, "Nothing to clear here", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent edit = new Intent(getBaseContext(), ViewOrdersActivity.class);
        edit.putExtra("customer_id",customer_id);
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
        setTitle("View Items");
        return true;
    }


}
