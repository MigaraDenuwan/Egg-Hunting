package com.example.mobilegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {

    TextView tvPoints, personalBest;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        tvPoints = findViewById(R.id.tvPoints);
        int points = getIntent().getExtras().getInt("points");
        tvPoints.setText("" + points);

        SharedPreferences pref = getSharedPreferences("MyPref", 0);
        int scoreSp = pref.getInt("scoreSp", 0);
        SharedPreferences.Editor editor = pref.edit();
        if (points > scoreSp){
            scoreSp = points;
            editor.putInt("scoreSp", scoreSp);
            editor.commit();
        }
        personalBest = findViewById(R.id.bestScore);
        personalBest.setText("" + scoreSp);
    }

    public void restart(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }
    public void goToMainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
