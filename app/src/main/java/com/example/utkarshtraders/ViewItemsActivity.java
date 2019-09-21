package com.example.utkarshtraders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewItemsActivity extends AppCompatActivity {


    private FirebaseUser mCurrentUser;

    private FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    private CollectionReference itemsRef=mFireStore.collection("items");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout i_list = findViewById(R.id.itemlist);

        Intent intent = getIntent();
        final String customer_id = intent.getStringExtra("customer_id");

        itemsRef.
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                    final Items items = documentSnapshot.toObject(Items.class);

                    documentSnapshot.get("city");


                    String item_name = items.getItemName();
                    String default_price = "Rs: "+items.getItemPrice().toString();


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
                            Intent additemtoorder = new Intent(getBaseContext(),PlaceOrderActivity.class);
                            additemtoorder.putExtra("item_object",items);
                            additemtoorder.putExtra("customer_id",customer_id);
                            startActivity(additemtoorder);
                        }
                    });
                }


            }
        });
    }
}
