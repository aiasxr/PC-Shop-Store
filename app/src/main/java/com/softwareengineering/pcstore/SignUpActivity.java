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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUpActivity extends AppCompatActivity {

    Button signup, signupGoogle, signupFacebook;
    EditText eMail, pass;
    ImageButton showPass;
    TextView login;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    GoogleSignInClient mGoogleSignInClient;
    // flag for showpass
    int showPassFlag = 0;

    int RC_SIGN_IN = 111;


    private static final String TAG = "Sign up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("641126251083-utknqe9agf475au06kuul1uohqjub80k.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Binding Layout items
        signup = findViewById(R.id.CustomSignUp);
        signupGoogle = findViewById(R.id.SignUpGoogle);
        eMail = findViewById(R.id.editTextTextEmailAddress);
        pass = findViewById(R.id.editTextTextPassword);
        showPass = findViewById(R.id.showPass);
        login = findViewById(R.id.login);

        //onClick listeners
        signup.setOnClickListener(view -> signup());
        signupGoogle.setOnClickListener(view -> signupG());
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
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            toCreateProfile();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    public void toCreateProfile(){
        Intent intent=new Intent(SignUpActivity.this,CreateProfileActivity.class);
        intent.putExtra("id",mAuth.getCurrentUser().getUid());
        intent.putExtra("email",mAuth.getCurrentUser().getEmail());
        startActivity(intent);
        finish();
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
                            Intent intent=new Intent(SignUpActivity.this,CreateProfileActivity.class);
                            intent.putExtra("id",mAuth.getCurrentUser().getUid());
                            intent.putExtra("email",eMail.getText().toString());
                            startActivity(intent);
                            finish();
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