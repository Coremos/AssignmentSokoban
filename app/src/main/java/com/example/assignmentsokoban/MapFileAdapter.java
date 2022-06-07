package com.example.assignmentsokoban;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignmentsokoban.gamesystem.Map;

import java.util.List;

public class MapFileAdapter extends ArrayAdapter<String> {
    public MapFileAdapter(Context context, int resource, List<String> mapList)
    {
        super(context, resource, mapList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        String name = getItem(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mapitem, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.textView_name);
        ImageView imageView = convertView.findViewById(R.id.imageView_thumbnail);

        String thumbnailPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Maps" + name + ".png";
        Bitmap bitmap = BitmapFactory.decodeFile(thumbnailPath);

        textView.setText(name);
        imageView.setImageBitmap(bitmap);

        return convertView;
    }
}