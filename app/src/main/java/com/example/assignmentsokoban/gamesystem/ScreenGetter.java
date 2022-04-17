package com.example.assignmentsokoban.gamesystem;

import android.util.Log;

public class ScreenGetter {
    public int ScreenWidth;
    public int ScreenHeight;
    public int GameViewWidth;
    public int GameViewHeight;
    private double widthMultiplier;
    private double heightMultiplier;

    public ScreenGetter(int screenWidth, int screenHeight)
    {
        ScreenWidth = screenWidth;
        ScreenHeight = screenHeight;
    }

    public void setGameViewSize(int width, int height)
    {
        GameViewWidth = width;
        GameViewHeight = height;
        widthMultiplier = ScreenWidth / GameViewWidth;
        Log.v("Debug", widthMultiplier + "Screen Width : " + ScreenWidth + " / Screen Height : " + GameViewWidth);
        heightMultiplier = ScreenHeight / GameViewHeight;
        Log.v("Debug", heightMultiplier + "Screen Width : " + ScreenHeight + " / Screen Height : " + GameViewHeight);
    }

    public int getX(int x) { return (int)(x * widthMultiplier); }
    public int getY(int y) { return (int)(y * heightMultiplier); }
}