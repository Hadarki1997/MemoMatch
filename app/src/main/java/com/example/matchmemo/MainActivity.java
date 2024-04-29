package com.example.matchmemo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private CardAdapter cardAdapter;
    private ArrayList<Card> cardsList = new ArrayList<>();
    private Card firstCardSelected = null;
    private Card secondCardSelected = null;
    private boolean isBusy = false;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private long startTime;
    private TextView timerTextView;

    private int currentLevel = 1;
    private String category = "Default";
    private boolean isPremium = false;

    private InterstitialAd mInterstitialAd;

    private MediaPlayer correctSoundPlayer;
    private MediaPlayer incorrectSoundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d("MainActivity", "Before extracting isUserPremium: " + isPremium);
        isPremium = getIntent().getBooleanExtra("isPremium", false);
        Log.d("MainActivity", "After extracting isUserPremium: " + isPremium);

        if (!isPremium) {
            MobileAds.initialize(this);
            loadInterstitialAd();
        }


        gridView = findViewById(R.id.gridview);
        timerTextView = findViewById(R.id.timerTextView);

        if (getIntent().hasExtra("level")) {
            currentLevel = getIntent().getIntExtra("level", 1);
        }

        if (getIntent().hasExtra("Category")) {
            category = getIntent().getStringExtra("Category");
        }

        correctSoundPlayer = MediaPlayer.create(this, R.raw.app_src_main_res_raw_correct_answer_sound);
        incorrectSoundPlayer = MediaPlayer.create(this, R.raw.app_src_main_res_raw_incorrect_answer_sound);

        initializeCards(category);
        setupGridView();
        startTimer();
        setupBackButton();
    }

    private void initializeCards(String category) {
        int[] images = new int[0]; // Initialize with default empty array to handle the default case

        switch (category) {
            case "Animals":
                images = new int[]{
                        R.drawable.ic_dolphin, R.drawable.ic_happy,
                        R.drawable.ic_hen, R.drawable.ic_owl,
                        R.drawable.ic_pig, R.drawable.ic_snail,
                        R.drawable.ic_cow, R.drawable.ic_jellyfish,
                        R.drawable.ic_crab, R.drawable.ic_dog
                };
                break;
            case "Food":
                images = new int[]{
                        R.drawable.ic_biryani, R.drawable.ic_cannedfood,
                        R.drawable.ic_donut, R.drawable.ic_fastfood,
                        R.drawable.ic_friedegg, R.drawable.ic_grape,
                        R.drawable.ic_burger, R.drawable.ic_noodles,
                        R.drawable.ic_pizza, R.drawable.ic_vegetable
                };
                break;

            case "Music":
                images = new int[]{
                        R.drawable.ic_accordion, R.drawable.ic_drum,
                        R.drawable.ic_gong, R.drawable.ic_guitar,
                        R.drawable.ic_piano, R.drawable.ic_recorder,
                        R.drawable.ic_tambourine, R.drawable.ic_triangle,
                        R.drawable.ic_trumpet, R.drawable.ic_xylophone
                };
                break;
            case "Sport":
                images = new int[]{
                        R.drawable.ic_americanfootball, R.drawable.ic_dumbbell,
                        R.drawable.ic_fight, R.drawable.ic_snail,
                        R.drawable.ic_game, R.drawable.ic_gong,
                        R.drawable.ic_pool, R.drawable.ic_bowling,
                        R.drawable.ic_tennis, R.drawable.ic_swimming
                };
                break;
            case "Face":
                images = new int[]{
                        R.drawable.ic_star, R.drawable.ic_smile,
                        R.drawable.ic_emoji, R.drawable.ic_party,
                        R.drawable.ic_emoji2, R.drawable.ic_sad,
                        R.drawable.ic_happy2, R.drawable.ic_angry,
                        R.drawable.ic_smiling, R.drawable.ic_thinking
                };
                break;

        }


        int maxCardsToAdd = Math.min(currentLevel * 2, images.length);
        cardsList.clear();

        for (int i = 0; i < maxCardsToAdd; i++) {
            cardsList.add(new Card(images[i]));
            cardsList.add(new Card(images[i]));
        }

        Collections.shuffle(cardsList);
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("MainActivity", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("MainActivity", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void showEndGameOptions(int currentLevel) {
        RelativeLayout layout = findViewById(R.id.rootLayout);


        Button returnToCategorySelectionButton = new Button(this);
        returnToCategorySelectionButton.setText("Return to Category Selection");
        returnToCategorySelectionButton.setId(View.generateViewId());
        returnToCategorySelectionButton.setOnClickListener(v -> {
            Intent intent;
            if (isPremium) {
                intent = new Intent(MainActivity.this, CategorySelectionActivitypremium.class);
            } else {
                intent = new Intent(MainActivity.this, CategorySelectionActivity.class);
            }
            startActivity(intent);
            finish();
        });

        Button nextLevelButton = new Button(this);
        nextLevelButton.setText("Next Level");
        nextLevelButton.setId(View.generateViewId());
        nextLevelButton.setOnClickListener(v -> {

            if (currentLevel % 2 == 0 && mInterstitialAd != null && !isPremium) {
                mInterstitialAd.show(MainActivity.this);
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d("TAG", "Ad was shown.");

                        goToNextLevel();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.e("TAG", "Ad failed to show.");
                        goToNextLevel();
                    }
                });
            } else {
                goToNextLevel();
            }
        });


        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params2.addRule(RelativeLayout.BELOW, returnToCategorySelectionButton.getId());
        params2.setMargins(0, 50, 0, 0);

        layout.addView(returnToCategorySelectionButton, params1);
        layout.addView(nextLevelButton, params2);
    }

    private void goToNextLevel() {
        Intent nextLevelIntent = new Intent(MainActivity.this, MainActivity.class);
        nextLevelIntent.putExtra("level", currentLevel + 1);
        nextLevelIntent.putExtra("Category", category);
        nextLevelIntent.putExtra("isPremium", isPremium);
        startActivity(nextLevelIntent);
        finish();
    }


    private void setupGridView() {
        cardAdapter = new CardAdapter(this, cardsList);
        gridView.setAdapter(cardAdapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> handleCardClick(position));
    }

    private void handleCardClick(int position) {
        if (isBusy || cardsList.get(position).isMatched()) return;

        Card selectedCard = cardsList.get(position);
        if (!selectedCard.isFlipped()) {
            selectedCard.setFlipped(true);
            cardAdapter.notifyDataSetChanged();

            if (firstCardSelected == null) {
                firstCardSelected = selectedCard;
            } else {
                secondCardSelected = selectedCard;
                isBusy = true;
                gridView.postDelayed(this::checkForMatch, 1000);
            }
        }
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerRunnable = () -> {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds %= 60;
            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
            timerHandler.postDelayed(timerRunnable, 500);
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void checkForMatch() {
        if (firstCardSelected != null && secondCardSelected != null) {
            if (firstCardSelected.getImageId() == secondCardSelected.getImageId()) {

                firstCardSelected.setMatched(true);
                secondCardSelected.setMatched(true);
                if (correctSoundPlayer != null) correctSoundPlayer.start();
                Toast.makeText(this, "Well done! You found a match!", Toast.LENGTH_SHORT).show();
            } else {

                firstCardSelected.setFlipped(false);
                secondCardSelected.setFlipped(false);
                if (incorrectSoundPlayer != null) incorrectSoundPlayer.start();
                Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
            }

            firstCardSelected = null;
            secondCardSelected = null;
            isBusy = false;
            cardAdapter.notifyDataSetChanged();

            if (isGameWon()) {
                onGameWon();
            }
        }
    }

    private boolean isGameWon() {
        for (Card card : cardsList) {
            if (!card.isMatched()) return false;
        }
        return true;
    }

    private void onGameWon() {
        timerHandler.removeCallbacks(timerRunnable);
        Toast.makeText(this, "Congratulations! You've completed the levle :)", Toast.LENGTH_LONG).show();
        View dimmerView = new View(this);
        dimmerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        dimmerView.setBackgroundColor(Color.argb(150, 0, 0, 0));
        RelativeLayout rootLayout = findViewById(R.id.rootLayout);
        rootLayout.addView(dimmerView);
        showEndGameOptions(currentLevel);
    }


    @Override
    protected void onDestroy() {

        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }


        if (correctSoundPlayer != null) {
            correctSoundPlayer.release();
            correctSoundPlayer = null;
        }
        if (incorrectSoundPlayer != null) {
            incorrectSoundPlayer.release();
            incorrectSoundPlayer = null;
        }

        super.onDestroy();
    }
}
