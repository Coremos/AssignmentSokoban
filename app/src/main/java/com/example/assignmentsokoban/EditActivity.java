package com.example.assignmentsokoban;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

import com.example.assignmentsokoban.gamesystem.EditView;

public class EditActivity extends AppCompatActivity {
    EditView editView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editView = findViewById(R.id.view_edit);

        Display display = getWindowManager().getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            screenHeight -= TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        Log.w("Debug", "ScreenSize = " + screenHeight + "Original = " + display.getHeight());
        editView.initialize(this, screenWidth, screenHeight);
    }
}