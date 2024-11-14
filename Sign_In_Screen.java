package com.example.chatapp6;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity
{
    EditText name,phone,pass,repass;
    Button signUp,next;
    CountryCodePicker ccp;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        phone=findViewById(R.id.signup_et2);
        pass=findViewById(R.id.signup_et3);
        repass=findViewById(R.id.signup_et4);
        name=findViewById(R.id.signup_et1);
        signUp=findViewById(R.id.signup_bt2);
        db=FirebaseFirestore.getInstance();
        ccp=findViewById(R.id.ccp1);
        next=findViewById(R.id.signup_bt3);
        ccp.registerCarrierNumberEditText(phone);

        signUp.setOnClickListener(v -> {
            String name1=name.getText().toString();
            String num=phone.getText().toString();
            String password=pass.getText().toString();
            String rePassword=repass.getText().toString();
            if(name1.equals(" ") || name1.isEmpty() || name1.isBlank())
            {
                name.setError("Please Type Your Name");
                return;
            }
            if(!password.equals(rePassword))
            {
                pass.setError("Password Mismatch");
                return;
            }
            if(!ccp.isValidFullNumber())
            {
                phone.setError("Wrong Phone Number Entered");
                return;
            }
            addToFireStore(ccp.getFullNumberWithPlus());
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1=name.getText().toString();
                Intent x=new Intent(SignInActivity.this, OtpActivity.class);
                x.putExtra("phone",ccp.getFullNumberWithPlus());
                x.putExtra("name",name1);
                startActivity(x);
            }
        });
    }
    private  void addToFireStore(String phoneNumber)
    {
        Map<String,Object> data=new HashMap<>();
        data.put("identifier",phoneNumber);

        db.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> showToast("Added Successfully to Firebase"))
                .addOnFailureListener(e -> showToast("Error Adding to FireBase"+e.getMessage()));
        new Handler().postDelayed(()->
        {
            signUp.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        },1000);
    }
    private void showToast(String message) {
        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
