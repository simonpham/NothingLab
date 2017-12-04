package com.simonvn.nothinglab;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;


public class SigninActivity extends AppCompatActivity {

    // initializing variables
    private static final String PATH_TOS = "";
    private Button btnELogin;
    private Button btnGLogin;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 123;
    private RelativeLayout relativeLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Choose auth providers
    List<AuthUI.IdpConfig> email_providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
    List<AuthUI.IdpConfig> google_providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());


    private void initView() {
        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
        btnELogin = (Button) findViewById(R.id.btnELogin);
        btnGLogin = (Button) findViewById(R.id.btnGLogin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        if(isUserLogin()){loginUser();}
        setContentView(R.layout.activity_signin);

        initView();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        btnELogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setTosUrl(PATH_TOS)
                        .setAvailableProviders(email_providers)
                        .build(), RC_SIGN_IN);
            }
        });
        btnGLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setTosUrl(PATH_TOS)
                        .setAvailableProviders(google_providers)
                        .build(), RC_SIGN_IN);
            }
        });

    // end onCreate
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                loginUser();
            }
            if(resultCode == RESULT_CANCELED){
                displayMessage(getString(R.string.signin_failed));
            }
            return;
        }
        displayMessage(getString(R.string.unknown_response));
    }
    private boolean isUserLogin(){
        if(auth.getCurrentUser() != null){
            return true;
        }
        return false;
    }
    private void loginUser(){
        Intent loginIntent = new Intent(SigninActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }
    private void displayMessage(String message){
        Snackbar snackbar = Snackbar.make(relativeLayout,message,Snackbar.LENGTH_LONG);
        snackbar.show();
    }





}
