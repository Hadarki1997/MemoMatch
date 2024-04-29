package com.example.matchmemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textViewLogin;

    CheckBox checkboxPremium;


    @Override
    public void onStart() {
        super.onStart();
        setContentView(R.layout.activity_register);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.loginNow);
        checkboxPremium = findViewById(R.id.checkbox_premium);
        buttonReg.setOnClickListener(v -> attemptRegistration());
        textViewLogin.setOnClickListener(v -> redirectToLogin());


    }





    private void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void redirectToCategorySelection() {
        Intent intent = new Intent(getApplicationContext(), CategorySelectionActivity.class);
        Log.d("RegisterActivity", "Redirecting to regular category selection");
        startActivity(intent);
        finish();
    }

    private void attemptRegistration() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        boolean isPremium = checkboxPremium.isChecked();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showToast("Enter email and password");
            progressBar.setVisibility(View.GONE);
        } else {
            registerUser(email, password, isPremium);
        }
    }


    private void registerUser(String email, String password, boolean isPremium) {
        mAuth = FirebaseAuth.getInstance();
        buttonReg.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserToFirestore(firebaseUser, email, isPremium);
                        }
                    } else {
                        showToast("Registration failed: " + task.getException().getMessage());
                        buttonReg.setEnabled(true);
                    }
                });
    }

    void saveUserToFirestore(FirebaseUser firebaseUser, String email, boolean isPremium) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("isPremium", isPremium);

        db.collection("users").document(firebaseUser.getUid()).set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register.this, "Registered successfully. Welcome!", Toast.LENGTH_LONG).show();
                        decideNavigation(isPremium);
                    } else {
                        Toast.makeText(Register.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        buttonReg.setEnabled(true);
                    }
                });
    }

    private void decideNavigation(boolean isPremium) {
        if (isPremium) {
            navigateToCategorySelectionActivityPremium();
        } else {
            redirectToCategorySelection();
        }
    }
    private void navigateToCategorySelectionActivityPremium() {
        Intent intent = new Intent(Register.this, CategorySelectionActivitypremium.class);
        Log.d("RegisterActivity", "Redirecting to premium category selection");
        startActivity(intent);
        finish();
    }
    private void showToast(String message) {
        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
