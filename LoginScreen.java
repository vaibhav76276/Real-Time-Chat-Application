package com.example.chatapp6;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp6.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity
{
    EditText name,phone;
    Button login, signup,check;
    CountryCodePicker ccp;
    FirebaseFirestore db;
    String phoneNum;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name = findViewById(R.id.et1);
        phone = findViewById(R.id.et2);
        login = findViewById(R.id.bt1);
        signup = findViewById(R.id.bt2);
        check = findViewById(R.id.bt3);
        ccp = findViewById(R.id.ccp1);
        progressBar=findViewById(R.id.prog_p1);
        ccp.registerCarrierNumberEditText(phone);
        db=FirebaseFirestore.getInstance();
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1 = name.getText().toString();
                if (name1.isEmpty() || name1 == " ") {
                    name.setError("Empty Name");
                    return;
                }
                String num=phone.getText().toString();
                if(!ccp.isValidFullNumber()){
                    phone.setError("Wrong Number Entered");
                    return;
                }else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    check.setVisibility(View.GONE);
                    String phonewithcc=ccp.getFullNumberWithPlus();
                    checkInFireStore(phonewithcc);

                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(LoginActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1=name.getText().toString();
                Intent x=new Intent(LoginActivity.this,OtpActivity.class);
                x.putExtra("phone",ccp.getFullNumberWithPlus());
                x.putExtra("name",name1);
                startActivity(x);
            }
        });
    }
    private void checkInFireStore(String phoneNumber)
    {
        db.collection("users")
                .whereEqualTo("identifier",phoneNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if(task.isSuccessful()) {
                            if (!task.getResult().isEmpty())
                            {
                                showToast("User Found");
                                new Handler().postDelayed(() ->
                                {
                                    login.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }, 100);

                            } else
                            {
                                showToast("User Not Found");
                                new Handler().postDelayed(() ->
                                {
                                    signup.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }, 100);
                            }
                        }
                        else
                        {
                            showToast("Error checking Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }
    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
