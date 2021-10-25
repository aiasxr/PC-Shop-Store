package com.softwareengineering.pcstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    Button signup, signupGoogle, signupFacebook;
    EditText eMail, pass;
    ImageButton showPass;
    TextView login;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // flag for showpass
    int showPassFlag = 0;

    private static final String TAG = "Sign up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Binding Layout items
        signup = findViewById(R.id.CustomSignUp);
        signupGoogle = findViewById(R.id.SignUpGoogle);
        signupFacebook = findViewById(R.id.SignUpFB);
        eMail = findViewById(R.id.editTextTextEmailAddress);
        pass = findViewById(R.id.editTextTextPassword);
        showPass = findViewById(R.id.showPass);
        login = findViewById(R.id.login);

        //onClick listeners
        signup.setOnClickListener(view -> signup());
        signupGoogle.setOnClickListener(view -> signupG());
        signupFacebook.setOnClickListener(view -> signupF());
        showPass.setOnClickListener(view -> showPass());
        login.setOnClickListener(view -> login());
    }

    private void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void showPass() {
        if(showPassFlag == 0) {
            pass.setTransformationMethod(new PasswordTransformationMethod());
            showPassFlag = 1;
        }
        else{
            pass.setTransformationMethod(null);
            showPassFlag = 0;
        }
    }

    private void signupF() {
    }

    private void signupG() {
    }

    private void signup() {
        mAuth.createUserWithEmailAndPassword(eMail.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}