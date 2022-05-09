package com.example.assignmentsokoban.gamesystem;

public class ScreenGetter {
    public int ScreenWidth;
    public int ScreenHeight;
    public int GameViewWidth;
    public int GameViewHeight;
    private double widthMultiplier;
    private double heightMultiplier;
    private double widthDivider;
    private double heightDivider;

    public ScreenGetter(int screenWidth, int screenHeight)
    {
        ScreenWidth = screenWidth;
        ScreenHeight = screenHeight;
    }

    public void setGameViewSize(int width, int height)
    {
        GameViewWidth = width;
        GameViewHeight = height;
        widthMultiplier = (double)ScreenWidth / GameViewWidth;
        heightMultiplier = (double)ScreenHeight / GameViewHeight;
        widthDivider = 1.0 / widthMultiplier;
        heightDivider = 1.0 / heightMultiplier;
    }

    public int getX(int x) { return (int)(x * widthMultiplier); }
    public int getY(int y) { return (int)(y * heightMultiplier); }
    public int getOriginalX(int x) { return (int)(x * widthDivider); }
    public int getOriginalY(int y) { return (int)(y * heightDivider); }
}