package com.example.assignmentsokoban;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button_start;
    private Button button_edit;
    private Button button_download;
    private Button button_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_start = findViewById(R.id.button_start);
        button_edit = findViewById(R.id.button_edit);
        button_download = findViewById(R.id.button_download);
        button_exit = findViewById(R.id.button_exit);

        button_start.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
            intent.putExtra("Mode", "start");
            startActivity(intent);
        });

        button_edit.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
            intent.putExtra("Mode", "edit");
            startActivity(intent);
        });

        button_download.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DownloadActivity.class);
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