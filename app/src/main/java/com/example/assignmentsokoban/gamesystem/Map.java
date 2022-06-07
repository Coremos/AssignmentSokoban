package com.example.assignmentsokoban.gamesystem;

import org.json.JSONException;
import org.json.JSONObject;

public class Map
{
    public static int WIDTH = 9;
    public static int HEIGHT = 9;
    public static Map Map;
    public String Name;
    public String Author;
    public String Description;
    public int Level;
    public int[][] Data;

    public Map()
    {
        Name = "NewMap";
        Author = "User";
        Description = "Explain";
        Level = 1;
        Data = new int[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++)
        {
            for (int y = 0; y < HEIGHT; y++)
            {
                Data[x][y] = TileType.Ground;
                if (x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1)
                    Data[x][y] = TileType.Tree;
            }
        }
        Data[1][1] = TileType.Duck;
    }

    static Map loadMap(String path)
    {
        Map map = new Map();

        return new Map();
    }

    public String dataToString()
    {
        String data = "";
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                data += Data[x][y];
            }
        }
        return data;
    }

    public String saveMap()
    {
        String data = "";
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                data += Data[x][y];
            }
        }
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("Name", Name);
            jsonObject.put("Author", Author);
            jsonObject.put("Description", Description);
            jsonObject.put("Level", Level);
            jsonObject.put("Data", data);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}
