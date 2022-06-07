package com.example.assignmentsokoban;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

import com.example.assignmentsokoban.gamesystem.GameView;

public class GameActivity extends AppCompatActivity {
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.view_game);
        Display display = getWindowManager().getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        gameView.initialize(this, screenWidth, screenHeight);
    }

    public void showClearMessage()
    {
        Toast.makeText(getApplicationContext(), "Map clear!", Toast.LENGTH_SHORT).show();
    }
}