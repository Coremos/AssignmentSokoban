package com.example.assignmentsokoban.gamesystem;

public class DirectionType
{
    public static int None = -1;
    public static int Up = 0;
    public static int Down = 1;
    public static int Left = 2;
    public static int Right = 3;
    public static int Count = 4;

    public static int[] X = { 0, 0, -1, 1 };
    public static int[] Y = { -1, 1, 0, 0 };
}