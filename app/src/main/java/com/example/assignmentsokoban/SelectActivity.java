package com.example.assignmentsokoban;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.assignmentsokoban.gamesystem.Map;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectActivity extends AppCompatActivity {
    private static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Maps";
    private ListView listView;
    private Button button_back;
    private Button button_new;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_select);

        listView = findViewById(R.id.fileListView);
        button_back = findViewById(R.id.button_back);
        button_new = findViewById(R.id.button_new);

        List<String> filesNameList = getMapFilesNameList(PATH);
        MapFileAdapter adapter = new MapFileAdapter(getApplicationContext(), 0, filesNameList);

        Intent mainIntent = getIntent();
        String mode = mainIntent.getStringExtra("Mode");

        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            String name = (String) listView.getItemAtPosition(position);
            Map.Map = loadMapFile(name);
            Intent intent;
            if (mode.equals("start"))
            {
                intent = new Intent(getApplicationContext(), GameActivity.class);
            }
            else
            {
                intent = new Intent(getApplicationContext(), EditActivity.class);
            }
            startActivity(intent);
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (mode.equals("start")) button_new.setVisibility(View.GONE);
        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map.Map = new Map();
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(intent);
            }
        });
    }

    private List<String> getMapFilesNameList(String path)
    {
        File directory = new File(path);
        File[] files = directory.listFiles();
        List<String> filesNameList = new ArrayList<>();
        if (files == null || files.length == 0) return filesNameList;
        for (int i = 0; i < files.length; i++)
        {
            String name = files[i].getName();
            String fileExtension = name.substring(name.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
            if (fileExtension.equals("map"))
            {
                int pos = name.lastIndexOf(".");
                name = name.substring(0, pos);
                filesNameList.add(name);
            }
        }
        return filesNameList;
    }

    private Map loadMapFile(String fileName)
    {
        Map map = new Map();
        String data = null;
        try
        {
            File file = new File(PATH + "/" + fileName + ".map");
            InputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];

            int offset = 0;
            while (offset < bytes.length)
            {
                int result = inputStream.read(bytes, offset, bytes.length - offset);
                if (result == -1) {
                    break;
                }
                offset += result;
            }

            data = new String(bytes, StandardCharsets.UTF_8);
            Log.w("Mapasd", data);

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            JSONObject jsonObject = new JSONObject(data);
            map.Name = jsonObject.getString("Name");
            map.Author = jsonObject.getString("Author");
            map.Description = jsonObject.getString("Description");
            String mapData = jsonObject.getString("Data");
            Log.w("asadsf", mapData);
            for (int y = 0; y < Map.HEIGHT; y++)
            {
                for (int x = 0; x < Map.WIDTH; x++)
                {
                    map.Data[x][y] = Integer.parseInt(String.valueOf(mapData.charAt(Map.WIDTH * y + x)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }
}