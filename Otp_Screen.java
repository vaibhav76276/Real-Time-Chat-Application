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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity
{
    String phone,name;
    TextView welcome;
    EditText otpInput;
    Button verify,resend;
    ProgressBar progressBar;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    Long timeoutSeconds=15L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        phone=getIntent().getExtras().getString("phone");
        name=getIntent().getExtras().getString("name");
        welcome=findViewById(R.id.tv2);
        otpInput=findViewById(R.id.et1);
        verify=findViewById(R.id.bt1);
        resend=findViewById(R.id.bt2);
        progressBar=findViewById(R.id.pg);
        welcome.setText("Welcome "+name);
        sendOtp(phone,false);

        verify.setOnClickListener(v -> {
            String enteredOtp=otpInput.getText().toString();
            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(credential);
            setInProgress(true);
        });
        resend.setOnClickListener(v ->{
            showToast("OTP Send Successfully");
        });
    }
    void sendOtp(String phoneNumber,boolean isResend)
    {
        setInProgress(true);
        PhoneAuthOptions.Builder builder=
                new PhoneAuthOptions.Builder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds,TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                            {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e)
                            {
                                showToast("OTP Verification Failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
                            {
                                super.onCodeSent(s, forceResendingToken);

                                verificationCode=s;
                                resendingToken=forceResendingToken;
                                showToast("OTP Sent Successfully");
                                setInProgress(false);
                            }
                        });
        if(isResend)
        {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }
    void setInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            verify.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            verify.setVisibility(View.VISIBLE);
        }
    }
    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
    void signIn(PhoneAuthCredential prc)
    {
        setInProgress(true);
        mAuth.signInWithCredential(prc).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    setInProgress(false);
                    showToast("Verification Successful");
                    Intent xyz=new Intent(OtpActivity.this, ChooseUsername.class);
                    xyz.putExtra("phone",phone);
                    startActivity(xyz);
                }else
                {
                    showToast("OTP Verification Failed");
                }
            }
        });
    }


}
