package com.example.assignmentsokoban;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button_start;
    Button button_edit;
    Button button_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_start = findViewById(R.id.button_start);
        button_edit = findViewById(R.id.button_edit);
        button_exit = findViewById(R.id.button_exit);

        button_start.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        });

        button_edit.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), EditActivity.class);
            startActivity(intent);
        });

        button_exit.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Alert");
            builder.setMessage("게임을 종료하시겠습니까?");
            builder.setNegativeButton("예", (dialogInterface, i) -> finish());
            builder.setPositiveButton("아니오", (dialogInterface, i) -> dialogInterface.cancel());
            builder.create().show();
        });
    }
}