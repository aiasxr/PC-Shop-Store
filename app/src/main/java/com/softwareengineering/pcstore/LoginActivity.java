package com.softwareengineering.pcstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {
    Button login, loginGoogle, loginFacebook;
    EditText eMail, pass;
    ImageButton showPass;
    TextView forgotPass, signUp;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // flag for showpass
    int showPassFlag = 0;

    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Binding Layout items
        login = findViewById(R.id.CustomLogin);
        loginGoogle = findViewById(R.id.loginGoogle);
        loginFacebook = findViewById(R.id.loginFB);
        eMail = findViewById(R.id.editTextTextEmailAddress);
        pass = findViewById(R.id.editTextTextPassword);
        showPass = findViewById(R.id.showPass);
        forgotPass = findViewById(R.id.forgotPass);
        signUp = findViewById(R.id.signUp);

        //onClick listeners
        login.setOnClickListener(view -> login());
        loginGoogle.setOnClickListener(view -> loginG());
        loginFacebook.setOnClickListener(view -> loginF());
        showPass.setOnClickListener(view -> showPass());
        forgotPass.setOnClickListener(view -> forgotPass());
        signUp.setOnClickListener(view -> signUP());
    }

    protected void login(){
        mAuth.signInWithEmailAndPassword(eMail.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void loginG(){

    }


    protected void loginF(){

    }


    protected void showPass(){
        if(showPassFlag == 0) {
            pass.setTransformationMethod(new PasswordTransformationMethod());
            showPassFlag = 1;
        }
        else{
            pass.setTransformationMethod(null);
            showPassFlag = 0;
        }
    }


    protected void forgotPass(){

    }


    protected void signUP(){
        startActivity(new Intent(this, SignUpActivity.class));
    }


}