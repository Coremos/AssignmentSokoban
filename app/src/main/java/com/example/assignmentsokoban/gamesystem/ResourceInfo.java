package com.example.assignmentsokoban.gamesystem;

import com.example.assignmentsokoban.R;

public class ResourceInfo {
    public static final int Pad = 7;
    public static final int BackButton = 8;
    public static final int Count = 9;

    public int[] Resources;

    public ResourceInfo()
    {
        Resources = new int[ResourceInfo.Count];
        Resources[TileType.Ground] = R.drawable.ground;
        Resources[TileType.Tree] = R.drawable.tree;
        Resources[TileType.Chick] = R.drawable.chick;
        Resources[TileType.Pond] = R.drawable.pond;
        Resources[TileType.ChickPond] = R.drawable.chickpond;
        Resources[TileType.Duck] = R.drawable.duck;
        Resources[ResourceInfo.Pad] = R.drawable.pad;
        Resources[ResourceInfo.BackButton] = R.drawable.backbutton;
    }
}