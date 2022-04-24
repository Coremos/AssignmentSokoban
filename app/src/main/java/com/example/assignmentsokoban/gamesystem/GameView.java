package com.example.assignmentsokoban.gamesystem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.assignmentsokoban.GameActivity;

public class GameView extends ParentView {
    private static final int UNIT_SIZE = 32;
    private static final int GAMEVIEW_WIDTH = UNIT_SIZE * 16;
    private static final int GAMEVIEW_HEIGHT = UNIT_SIZE * 9;
    private static final int STAGE_PIVOT_X = (int)(UNIT_SIZE * 3);
    private static final int STAGE_PIVOT_Y = (int)(UNIT_SIZE * 0);
    private static final int MAP_WIDTH = 9;
    private static final int MAP_HEIGHT = 9;

    private GameActivity _gameActivity;
    private MainThread _mainThread;
    private ScreenGetter _screenGetter;
    private ResourceInfo _resourceInfo;
    private Bitmap[] _bitmaps;
    private boolean _canDraw;
    private Resources _resources;

    private int playerX = 3;
    private int playerY = 3;

    public GameView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        getHolder().addCallback(this);
        _mainThread = new MainThread(getHolder(), this);
        _canDraw = false;
    }

    public void initialize(GameActivity gameActivity, int screenWidth, int screenHeight)
    {
        _gameActivity = gameActivity;
        _screenGetter = new ScreenGetter(screenWidth, screenHeight);
        _screenGetter.setGameViewSize(GAMEVIEW_WIDTH, GAMEVIEW_HEIGHT);
        _canDraw = true;

        _resourceInfo = new ResourceInfo();
        _resources = getResources();

        _bitmaps = new Bitmap[TileType.Count];
        for (int i = 0; i < TileType.Count; i++)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(_resources, _resourceInfo.Resources[i]);
            _bitmaps[i] = Bitmap.createScaledBitmap(bitmap, _screenGetter.getX(UNIT_SIZE), _screenGetter.getY(UNIT_SIZE), false);
        }
    }

    private void drawDebug(Canvas canvas)
    {
        Paint  backPaint = new Paint();
        backPaint.setColor(Color.BLUE);
        canvas.drawRect(_screenGetter.getX(0), _screenGetter.getY(0), _screenGetter.getX(UNIT_SIZE), _screenGetter.getY(UNIT_SIZE), backPaint);
        backPaint.setColor(Color.GREEN);
        canvas.drawRect(_screenGetter.getX(GAMEVIEW_WIDTH - UNIT_SIZE), _screenGetter.getY(GAMEVIEW_HEIGHT - UNIT_SIZE), _screenGetter.getX(GAMEVIEW_WIDTH), _screenGetter.getY(GAMEVIEW_HEIGHT), backPaint);
    }

    private void drawMap(Canvas canvas)
    {
        int map[][] = new int[MAP_WIDTH][MAP_HEIGHT];
        int scaledPivotX = _screenGetter.getX(STAGE_PIVOT_X);
        int scaledPivotY = _screenGetter.getY(STAGE_PIVOT_Y);
        int scaledUnitX = _screenGetter.getX(UNIT_SIZE);
        int scaledUnitY = _screenGetter.getY(UNIT_SIZE);
        Paint pixelPaint = new Paint();
        pixelPaint.setAntiAlias(false);
        pixelPaint.setFilterBitmap(false);
        Log.w("Paint", "isAntiAlias " + pixelPaint.isAntiAlias());
        for (int x = 0; x < MAP_WIDTH; x++)
        {
            for (int y = 0; y < MAP_HEIGHT; y++)
            {
                map[x][y] = TileType.Ground;
                if (x == 0 || x == MAP_WIDTH - 1 || y == 0 || y == MAP_HEIGHT - 1)
                    map[x][y] = TileType.Tree;
            }
        }

        map[1][1] = TileType.Pond;
        map[2][1] = TileType.Pond;
        map[3][1] = TileType.Pond;

        map[1][2] = TileType.Chick;
        map[2][2] = TileType.Chick;
        map[3][2] = TileType.Chick;

        for (int x = 0; x < MAP_WIDTH; x++)
        {
            for (int y = 0; y < MAP_HEIGHT; y++)
            {
                canvas.drawBitmap(_bitmaps[map[x][y]], scaledPivotX + scaledUnitX * x,
                        scaledPivotY + scaledUnitY * y, pixelPaint);
            }
        }

        canvas.drawBitmap(_bitmaps[TileType.Duck], scaledPivotX + scaledUnitX * playerX,
                scaledPivotY + scaledUnitY * playerY, pixelPaint);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (!_canDraw) return;
        //drawDebug(canvas);
        drawMap(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) { return true; }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    public void surfaceCreated(SurfaceHolder holder)
    {
        _mainThread.setRunning(true);
        try
        {
            if (_mainThread.getState() == Thread.State.TERMINATED)
            {
                _mainThread = new MainThread(getHolder(), this);
                _mainThread.setRunning(true);
                setFocusable(true);
            }
            _mainThread.start();
        }
        catch (Exception e) { }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        _mainThread.setRunning(false);
        while(retry)
        {
            try
            {
                _mainThread.join();
                retry = false;
            }
            catch(Exception e)
            {
            }
        }
    }
}