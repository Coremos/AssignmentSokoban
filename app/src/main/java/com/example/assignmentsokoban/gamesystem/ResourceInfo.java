package com.example.assignmentsokoban.gamesystem;

import com.example.assignmentsokoban.R;

public class ResourceInfo {
    public int[] Resources;

    public ResourceInfo()
    {
        Resources = new int[TileType.Count];
        Resources[TileType.Ground] = R.drawable.ground;
        Resources[TileType.Tree] = R.drawable.tree;
        Resources[TileType.Chick] = R.drawable.chick;
        Resources[TileType.Pond] = R.drawable.pond;
        Resources[TileType.ChickPond] = R.drawable.chickpond;
        Resources[TileType.Duck] = R.drawable.duck;
    }
}