package com.example.matchmemo;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CategorySelectionActivitypremium extends AppCompatActivity{

    FirebaseAuth auth;
    Button button;FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection_premium);

        auth=FirebaseAuth.getInstance();
        button = findViewById(R.id.logOutt);
        user= auth.getCurrentUser();
        if(user == null){
            Intent intent  =  new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent  =  new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }

        });

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

        ImageButton animalsButton = findViewById(R.id.animalsButton);
        ImageButton  foodButton = findViewById(R.id.foodButton);
        ImageButton musicButton = findViewById(R.id.musicButton);
        ImageButton sportButton = findViewById(R.id.sportsButton);
        ImageButton facesButton = findViewById(R.id.facesButton);

        animalsButton.setOnClickListener(v -> startGameWithCategory("Animals"));
        foodButton.setOnClickListener(v -> startGameWithCategory("Food"));
        musicButton.setOnClickListener(v -> startGameWithCategory("Music"));
        sportButton.setOnClickListener(v -> startGameWithCategory("Sport"));
        facesButton.setOnClickListener(v -> startGameWithCategory("Face"));
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

    private void startGameWithCategory(String category) {
        Intent intent = new Intent(CategorySelectionActivitypremium.this, MainActivity.class);
        intent.putExtra("Category", category);
        intent.putExtra("isPremium", true);
        startActivity(intent);

    }
}


