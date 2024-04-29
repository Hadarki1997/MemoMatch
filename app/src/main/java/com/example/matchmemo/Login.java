package com.example.matchmemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail,editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    Button buttonAbout;
    Button buttonPrivacyPolicy;
    Button buttonTermsOfUse;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            checkIfUserIsPremium(currentUser);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar=findViewById(R.id.progressBar);
        textView  = findViewById(R.id.RegisterNow);
        buttonAbout = findViewById(R.id.button_about);
        buttonPrivacyPolicy = findViewById(R.id.button_privacy_policy);
        buttonTermsOfUse = findViewById(R.id.button_terms_of_use);



        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aboutContent = "About Us\n\n" +
                        "Welcome to the Children's Memory Game! Developed by Hadar givoni, my app is designed to provide fun and engaging memory challenges for children of all ages.\n\n" +
                        "My Mission\n" +
                        "MY mission is to create educational and entertaining experiences that can help children improve their cognitive skills, including memory, concentration, and problem-solving.\n\n" +
                        "MY Game\n" +
                        "The Children's Memory Game offers a variety of levels and themes to keep the gameplay exciting and challenging. With vibrant graphics and user-friendly interfaces, our game ensures a safe and enjoyable environment for kids to learn and grow.\n\n" +
                        "Features\n" +
                        "- A wide range of categories and themes\n" +
                        "- Ad-free gameplay in premium versions\n" +
                        "- Regular updates with new features and content\n\n" +
                        "Contact Us\n" +
                        "For support or inquiries, feel free to contact us at [hadarki1996@gmail.com]. Your feedback helps us to improve and expand our game.\n\n" +
                        "Join us on a journey of fun and learning with the Children's Memory Game!";

                showCustomDialog("About Us", aboutContent);
            }
        });

        buttonPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privacyPolicyContent = "Privacy Policy\n\n" +
                        "1. Introduction\n" +
                        "I operate the Children's Memory Game app. This policy outlines how we collect, use, and protect the personal information of our users.\n\n" +
                        "2. Information Collection\n" +
                        "We collect personal information when users register or use our app, including name, email address, and details about how the app is used. Additionally, technical information such as IP address, device type, and operating system version may be collected.\n\n" +
                        "3. Use of Information\n" +
                        "Collected information is used to analyze app usage, enhance our services, provide technical support, and send updates and promotional messages, subject to user consent.\n\n" +
                        "4. Information Sharing\n" +
                        "We do not share personal information with third parties without user consent, except as required by law or to protect our rights and safety.\n\n" +
                        "5. Security\n" +
                        "We implement advanced technologies and security policies to protect user privacy and prevent unauthorized access to user information.\n\n" +
                        "6. User Rights\n" +
                        "Users have the right to access, correct, or delete their personal information collected by us at any time.\n\n" +
                        "7. Privacy Policy Changes\n" +
                        "We reserve the right to update this privacy policy at any time. Updates will be posted on our website and/or communicated through the app.\n\n" +
                        "8. Contact Us\n" +
                        "For any questions or comments regarding privacy, please contact us at [email or phone].";

                showCustomDialog("Privacy Policy", privacyPolicyContent);
            }
        });

        buttonTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the content of the Terms of Use as a string
                String termsOfUseContent = "Terms of Use\n\n" +
                        "Welcome to the Children's Memory Game! By accessing or using our app, you agree to be bound by these terms. Please read them carefully.\n\n" +
                        "Usage\n" +
                        "The Children's Memory Game is intended for users of all ages, especially designed for children. Parents or guardians should supervise the use of the app for children under the age of 13.\n\n" +
                        "Content\n" +
                        "All content provided in the app, including graphics, sounds, and text, is owned by Hadar givoni or its content suppliers and is protected by intellectual property laws.\n\n" +
                        "Prohibited Use\n" +
                        "You may not use the app for any illegal or unauthorized purpose. You must not, in the use of the app, violate any laws in your jurisdiction.\n\n" +
                        "Changes to Terms\n" +
                        "We reserve the right to modify these terms at any time. Changes and clarifications will take effect immediately upon their posting on the website or through the app.\n\n" +
                        "Contact Us\n" +
                        "If you have any questions about these Terms of Use, please contact us at [hadarki1996@gmail.com].";

                // Display the dialog with the Terms of Use content
                showCustomDialog("Terms of Use", termsOfUseContent);
            }
        });



        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this,"Enter email",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this,"Enter password",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        checkIfUserIsPremium(user);
                                    }
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });

    }

    private void checkIfUserIsPremium(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Boolean isPremium = task.getResult().getBoolean("isPremium");
                if (Boolean.TRUE.equals(isPremium)) {
                    navigateToActivity(CategorySelectionActivitypremium.class);
                } else {
                    navigateToActivity(CategorySelectionActivity.class);
                }
            } else {
                Log.e("Login", "Failed to check if user is premium", task.getException());

                navigateToActivity(CategorySelectionActivity.class);
            }
        });
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
        finish();
    }


    private void showCustomDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}