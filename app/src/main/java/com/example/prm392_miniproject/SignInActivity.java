package com.example.prm392_miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_miniproject.utilities.AccountManager;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    // Views
    EditText etUsername;
    EditText etPassword;
    TextView tvNoAccountYet;
    Button btnSignIn;
    // Notify
    private final String REQUIRE = "Require";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize views
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvNoAccountYet = (TextView) findViewById(R.id.tvNoAccountYet);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);


        //Register event
        tvNoAccountYet.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    private boolean checkInput() {
        //Username
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            etUsername.setError(REQUIRE);
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError(REQUIRE);
            return false;
        }

        //Valid
        return true;
    }

    private void signIn() {
        AccountManager accountManager = new AccountManager(this);
        //Invalid
        if (!checkInput()) {
            return;
        }
        boolean loginSuccess = accountManager.login(etUsername.getText().toString(),
                etPassword.getText().toString());

        if (loginSuccess) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Đăng nhập thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void signUpForm() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId(); // Get the ID of the clicked view
        if (id == R.id.btnSignIn) {
            signIn(); // Call the signIn method
        } else if (id == R.id.tvNoAccountYet) {
            signUpForm(); // Call the signUpForm method
        }
    }

}
