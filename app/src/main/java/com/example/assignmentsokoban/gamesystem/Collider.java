package com.example.assignmentsokoban.gamesystem;

public class Collider {
    public int PivotX;
    public int PivotY;
    public int Width;
    public int Height;

    public Collider(int pivotX, int pivotY, int width, int height)
    {
        PivotX = pivotX;
        PivotY = pivotY;
        Width = width;
        Height = height;
    }

    public boolean isOverlap(int x, int y)
    {
        return (x >= PivotX && x < PivotX + Width && y >= PivotY && y < PivotY + Height);
    }
}