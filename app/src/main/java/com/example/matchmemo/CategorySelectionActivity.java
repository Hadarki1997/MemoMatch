package com.example.matchmemo;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;


public class CategorySelectionActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Button buttonLogOut;
    private FirebaseUser user;
    private boolean isPremium = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");


        LinearLayout adContainer = findViewById(R.id.adContainer);
        adContainer.addView(adView);
        ImageButton shareButton = findViewById(R.id.button_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShareDialog();
            }
        });

        ImageButton rateButton = findViewById(R.id.button_rate);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
        checkFirstLogin();
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        initFirebaseAuth();
        setUpViews();
        handleUserStatus();

    }

    private void initFirebaseAuth() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void setUpViews() {
        buttonLogOut = findViewById(R.id.logOut);
        setUpCategoryButtons();
        setUpLogOutButton();
    }

    private void handleUserStatus() {
        if (user == null) {
            redirectToLogin();
        } else {
            checkIfUserIsPremium();
        }
    }

    private void setUpLogOutButton() {
        buttonLogOut.setOnClickListener(v -> {
            auth.signOut();
            redirectToLogin();
        });
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, Login.class));
        finish();
    }

    private void setUpCategoryButtons() {
        ImageButton animalsButton = findViewById(R.id.animalsButton);
        ImageButton foodButton = findViewById(R.id.foodButton);
        ImageButton musicButton = findViewById(R.id.musicButton);
        ImageButton sportButton = findViewById(R.id.sportsButton);
        ImageButton facesButton = findViewById(R.id.facesButton);

        animalsButton.setOnClickListener(v -> startGameWithCategory("Animals"));
        musicButton.setOnClickListener(v -> startGameWithCategory("Music"));


        foodButton.setOnClickListener(v -> checkPremiumAndStartGame("Food"));
        sportButton.setOnClickListener(v -> checkPremiumAndStartGame("Sport"));
        facesButton.setOnClickListener(v -> checkPremiumAndStartGame("Face"));
    }

    private void checkPremiumAndStartGame(String category) {
        if (isPremium) {
            startGameWithCategory(category);
        } else {
            Toast.makeText(this, "This category is available for premium users only.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGameWithCategory(String category) {
        Intent intent = new Intent(CategorySelectionActivity.this, MainActivity.class);
        intent.putExtra("Category", category);
        intent.putExtra("isPremium", false);
        startActivity(intent);
    }

    private void showShareDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Do you like our app?")
                .setMessage("Share our app with your community")

                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool app!");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                })
                .setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Rate our app");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final TextView tv = new TextView(this);
        tv.setText("Please rate our app");
        tv.setPadding(10, 10, 10, 10);
        layout.addView(tv);

        final RatingBar ratingBar = new RatingBar(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
        ratingBar.setLayoutParams(layoutParams);

        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(0);
        layout.addView(ratingBar);
        builder.setView(layout);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float rating = ratingBar.getRating();
                Toast.makeText(getApplicationContext(), "Rating: " + rating, Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkFirstLogin() {
        SharedPreferences prefs = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        boolean isFirstLogin = prefs.getBoolean("isFirstLogin", true);

        if (isFirstLogin) {
            showWhatsNewDialog();
            prefs.edit().putBoolean("isFirstLogin", false).apply();
        }
    }

    private void showWhatsNewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What's new");
        builder.setMessage("What's new in version 1.01.02\n\n- Design improvements for a fresh and engaging user interface\n- Introducing new challenging levels for endless fun.\n- - Improved compatibility with Android 14 for seamless gameplay.\n\n- Enhanced game experience with new memory card animations");
        builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void checkIfUserIsPremium() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user != null) {
            db.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Boolean isPremium = task.getResult().getBoolean("isPremium");
                    isPremium = Boolean.TRUE.equals(isPremium);
                } else {
                    Toast.makeText(CategorySelectionActivity.this, "Failed to check premium status.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
