package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CustomersActivity extends AppCompatActivity {

    private TextView hello;
    private TextView addcustomer;
    private FirebaseUser mCurrentUser;

    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference salesmanRef=mFireStore.collection("salesman");
    private CollectionReference customerRef=mFireStore.collection("customer");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        hello = findViewById(R.id.hellomessage);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        addcustomer = findViewById(R.id.addcustomer);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final LinearLayout c_list = findViewById(R.id.customerlist);

        if (mCurrentUser == null) {
            Intent loginintent = new Intent(CustomersActivity.this, LoginActivity.class);
            loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginintent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        salesmanRef.document(mCurrentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists() && documentSnapshot != null) {


                        String salesman_name = documentSnapshot.getString("salesmanName");
                        hello.setText("Hello " + salesman_name);


                    }
                }
            }
        });




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
                        }
                    });

                    viewCustomerorders.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent order = new Intent(getBaseContext(), ViewOrdersActivity.class);
                            order.putExtra("customer_id",customer_id);
                            startActivity(order);
                        }
                    });


                }


            }
        });

        addcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addcustomerintent = new Intent(CustomersActivity.this, AddCustomerActivity.class);
                addcustomerintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(addcustomerintent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });






    }



    }


