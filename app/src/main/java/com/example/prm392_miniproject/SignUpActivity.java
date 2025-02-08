package com.example.prm392_miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    //View
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private TextView tvAlreadyAccount;
    private Button btnSignUp;
    //Notify
    private final String REQUIRE = "Require";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Reference from layout
        etUsername =(EditText) findViewById(R.id.etUsername);
        etPassword = (EditText)  findViewById(R.id.etPassword);
        etConfirmPassword = (EditText)  findViewById(R.id.etConfirmPassword);
        tvAlreadyAccount = (TextView)  findViewById(R.id.tvAlreadyAccount);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        //Register event
        tvAlreadyAccount.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);


    }
    private boolean checkInput(){
        if(etUsername.getText().toString().isEmpty()){
            etUsername.setError(REQUIRE);
            return false;
        }
        if(etPassword.getText().toString().isEmpty()){
            etPassword.setError(REQUIRE);
            return false;
        }
        if(etConfirmPassword.getText().toString().isEmpty()){
            etPassword.setError(REQUIRE);
            return false;
        }

        if (!TextUtils.equals(etPassword.getText().toString(),
                etConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Password are not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Valid
        return true;

    }
    private void signUp(){
        //Invalid
        if(!checkInput()){
            return;
        }

    }
    private void signInForm(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId(); // Get the ID of the clicked view
        if (id == R.id.btnSignUp) {
            signUp(); // Call the signIn method
        } else if (id == R.id.tvAlreadyAccount) {
            signInForm(); // Call the signUpForm method
        }
    }
}
