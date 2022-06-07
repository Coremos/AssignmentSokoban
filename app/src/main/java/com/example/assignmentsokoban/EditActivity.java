package com.example.assignmentsokoban;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignmentsokoban.gamesystem.EditView;
import com.example.assignmentsokoban.gamesystem.Map;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EditActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private EditView editView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        editView = findViewById(R.id.view_edit);
        drawerLayout = findViewById(R.id.drawerlayout_edit);
        navigationView = findViewById(R.id.navigation_view_edit);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openMenu,
                R.string.closeMenu
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Display display = getWindowManager().getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            screenHeight -= TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        screenHeight -= toolbar.getHeight();

        Log.w("Debug", "ScreenSize = " + screenHeight + "Original = " + display.getHeight());
        editView.initialize(this, screenWidth, screenHeight);
    }

    private void saveMapFile(Map map)
    {
        ActivityCompat.requestPermissions(this, new  String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        try
        {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Maps";
            File directory = new File(path);
            if (!(directory.exists() && directory.isDirectory())) directory.mkdirs();
            path += "/" + map.Name + ".map";
            File file = new File(path);
            file.createNewFile();

            String data = map.saveMap();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));
            writer.write(data);
            writer.close();

            Toast.makeText(getApplicationContext(), "Saved map file " + map.Name, Toast.LENGTH_SHORT).show();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void uploadMapFile(Map map)
    {
        InsertData insert = new InsertData();
        insert.execute("http://146.56.167.250/upload.php", map.Name, map.Author, map.Description, map.dataToString());
    }

    private void editName(Map map)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Map");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                map.Name = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.navigation_save:
                saveMapFile(Map.Map);
                break;
            case R.id.navigation_upload:
                uploadMapFile(Map.Map);
                break;
            case R.id.navigation_close:
                finish();
                break;
            case R.id.navigation_editName:
                editName(Map.Map);
                break;
        }
        return false;
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EditActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String Name = (String)params[1];
            String Author = (String)params[2];
            String Description = (String)params[3];
            String ArrayData = (String)params[4];

            String serverURL = (String)params[0];
            String postParameters = "Name=" + Name + "&Author=" + Author + "&Description=" + Description + "&ArrayData" + ArrayData;

            try
            {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("InsertData", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK)
                {
                    inputStream = httpURLConnection.getInputStream();
                }
                else
                {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null)
                {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();
            }
            catch (Exception e)
            {
                Log.d("InsertData", ": Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            progressDialog.dismiss();
        }
    }
}