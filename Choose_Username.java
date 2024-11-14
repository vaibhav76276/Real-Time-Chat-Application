package com.example.chatapp6;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChooseUsername extends AppCompatActivity
{
    String phoneRecieved;
    private TextView welcome;
    private EditText userName;
    private Button check;
    private FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chooseusername);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db=FirebaseFirestore.getInstance();
        welcome=findViewById(R.id.tv2);
        userName=findViewById(R.id.et1);
        check =findViewById(R.id.bt1);
        progressBar=findViewById(R.id.pg);
        phoneRecieved=getIntent().getExtras().getString("phone");
        welcome.setText("User Number \n"+phoneRecieved);
        check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String usern=userName.getText().toString();
                if(usern.isBlank() || usern.isEmpty() || usern.equals(" "))
                {
                    userName.setError("UserName Empty");
                    return;
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    check.setVisibility(View.GONE);
                    cnaUsername(usern);
                }
            }
        });
    }
    private void cnaUsername(String usr) {
        // Check if phone number exists in Firestore
        db.collection("usernames")  // Assuming collection is 'users'
                .whereEqualTo("username", usr)  // Searching by 'phoneNumber' field
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {

                                showToast("Username not Available.");
                                userName.setError("Username Not Available");
                                setInProgress(false);
                                return;

                            } else{
                                addUsername(usr);
                                Intent xxx=new Intent(ChooseUsername.this, TestActivity.class);
                                startActivity(xxx);
                            }
                        } else {
                            showToast("Error checking Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }
    private void addUsername(String usr) {
        // Create a map to hold the phone number data
        Map<String, Object> data = new HashMap<>();
        data.put("username", usr);  // Store phone number as a Long

        // Add the phone number to Firestore
        db.collection("usernames")
                .add(data)
                .addOnSuccessListener(documentReference ->showToast("Welcome to ChatRoom"))
                .addOnFailureListener(e -> showToast("Error adding Username: " + e.getMessage()));
    }
    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
    void setInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            check.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            check.setVisibility(View.VISIBLE);
        }
    }
}
