package com.example.utkarshtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class LoginActivity extends AppCompatActivity {

    private TextView email;
    private TextView password;
    private Button login;
    private FirebaseAuth uAuth;
    private ProgressDialog mLoginProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uAuth = FirebaseAuth.getInstance();
        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        mLoginProgress = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_email = email.getText().toString();
                String s_password = password.getText().toString();

                if (!TextUtils.isEmpty(s_email) && !TextUtils.isEmpty(s_password)) {
                    v.setOnClickListener(null);
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we redirect you !");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    login_user(s_email, s_password);
                } else {
                    Toast.makeText(LoginActivity.this, "Required fields are empty", Toast.LENGTH_LONG).show();
                }

            }
        });


        }
    private void login_user(String s_email, String s_password){

        uAuth.signInWithEmailAndPassword(s_email, s_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {



                    Intent mainintent = new Intent(LoginActivity.this, CustomersActivity.class);
                    mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainintent);

                    Toast.makeText(LoginActivity.this, "Logged In successfully",
                            Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {

                    Toast.makeText(LoginActivity.this, "Login Failed,please check email/password",
                            Toast.LENGTH_LONG).show();
                    mLoginProgress.dismiss();
                    Intent mainintent = new Intent(LoginActivity.this, LoginActivity.class);
                    mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainintent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_bar_empty, menu);
        setTitle("Login");
        return true;
    }

}
