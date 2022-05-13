package com.example.assignmentsokoban.gamesystem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.assignmentsokoban.GameActivity;

public class GameView extends ParentView {
    private static final int UNIT_SIZE = 32;
    private static final int GAMEVIEW_WIDTH = UNIT_SIZE * 16;
    private static final int GAMEVIEW_HEIGHT = UNIT_SIZE * 9;
    private static final int STAGE_PIVOT_X = (int)(UNIT_SIZE * 3.5);
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
    private Collider[] _buttonCollider;

    private int[][] _map;
    private int _scaledPivotX;
    private int _scaledPivotY;
    private int _scaledUnitX;
    private int _scaledUnitY;
    private Paint _paint;

    private int _playerX;
    private int _playerY;

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

        _bitmaps = new Bitmap[ResourceInfo.Count];
        for (int i = 0; i < TileType.Count; i++)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(_resources, _resourceInfo.Resources[i]);
            _bitmaps[i] = Bitmap.createScaledBitmap(bitmap, _screenGetter.getX(UNIT_SIZE), _screenGetter.getY(UNIT_SIZE), false);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(_resources, _resourceInfo.Resources[ResourceInfo.Pad]);
        //bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        _bitmaps[ResourceInfo.Pad] = Bitmap.createScaledBitmap(bitmap, _screenGetter.getX(UNIT_SIZE * 3), _screenGetter.getY(UNIT_SIZE * 3), false);

        _scaledPivotX = _screenGetter.getX(STAGE_PIVOT_X);
        _scaledPivotY = _screenGetter.getY(STAGE_PIVOT_Y);
        _scaledUnitX = _screenGetter.getX(UNIT_SIZE);
        _scaledUnitY = _screenGetter.getY(UNIT_SIZE);

        _paint = new Paint();
        _paint.setAntiAlias(false);
        _paint.setFilterBitmap(false);

        _map = new int[MAP_WIDTH][MAP_HEIGHT];
        for (int x = 0; x < MAP_WIDTH; x++)
        {
            for (int y = 0; y < MAP_HEIGHT; y++)
            {
                _map[x][y] = TileType.Ground;
                if (x == 0 || x == MAP_WIDTH - 1 || y == 0 || y == MAP_HEIGHT - 1)
                    _map[x][y] = TileType.Tree;
            }
        }
        _map[1][1] = TileType.Pond;
        _map[2][1] = TileType.Pond;
        _map[3][1] = TileType.Pond;
        _map[1][2] = TileType.Chick;
        _map[2][2] = TileType.Chick;
        _map[3][2] = TileType.Chick;

        _playerX = 3;
        _playerY = 3;

        _buttonCollider = new Collider[DirectionType.Count];
        _buttonCollider[DirectionType.Up] = new Collider(UNIT_SIZE * 1, UNIT_SIZE * 3, UNIT_SIZE, UNIT_SIZE);
        _buttonCollider[DirectionType.Down] = new Collider(UNIT_SIZE * 1, UNIT_SIZE * 5, UNIT_SIZE, UNIT_SIZE);
        _buttonCollider[DirectionType.Left] = new Collider(UNIT_SIZE * 0, UNIT_SIZE * 4, UNIT_SIZE, UNIT_SIZE);
        _buttonCollider[DirectionType.Right] = new Collider(UNIT_SIZE * 2, UNIT_SIZE * 4, UNIT_SIZE, UNIT_SIZE);
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
        for (int x = 0; x < MAP_WIDTH; x++)
        {
            for (int y = 0; y < MAP_HEIGHT; y++)
            {
                canvas.drawBitmap(_bitmaps[_map[x][y]], _scaledPivotX + _scaledUnitX * x,
                        _scaledPivotY + _scaledUnitY * y, _paint);
            }
        }

        canvas.drawBitmap(_bitmaps[TileType.Duck], _scaledPivotX + _scaledUnitX * _playerX,
                _scaledPivotY + _scaledUnitY * _playerY, _paint);
    }

    private void drawUI(Canvas canvas)
    {
        canvas.drawBitmap(_bitmaps[ResourceInfo.Pad], _scaledUnitX * 0,_scaledUnitY * 3, _paint);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (!_canDraw) return;
        drawMap(canvas);
        drawUI(canvas);
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                checkClickButton(x, y);
                return true;
        }
        return false;
    }

    public boolean checkClickButton(int x, int y)
    {
        int originalX = _screenGetter.getOriginalX(x);
        int originalY = _screenGetter.getOriginalY(y);
        for (int direction = 0; direction < DirectionType.Count; direction++)
        {
            if (_buttonCollider[direction].isOverlap(originalX, originalY))
            {
                movePlayer(direction);
                return true;
            }
        }
        return false;
    }

    public void movePlayer(int direction)
    {
        int forwardX = _playerX;
        int forwardY = _playerY;
        forwardX += DirectionType.X[direction];
        forwardY += DirectionType.Y[direction];

        int forwardTile = _map[forwardX][forwardY];
        if (forwardTile == TileType.Tree) return;
        if (forwardTile == TileType.Chick || forwardTile == TileType.ChickPond)
        {
            int forwardX2 = forwardX + DirectionType.X[direction];
            int forwardY2 = forwardY + DirectionType.Y[direction];
            int forwardTile2 = _map[forwardX2][forwardY2];
            switch (forwardTile2)
            {
                case TileType.Tree:
                case TileType.Chick:
                case TileType.ChickPond:
                    return;
                case TileType.Pond:
                    _map[forwardX2][forwardY2] = TileType.ChickPond;
                    break;
                case TileType.Ground:
                default:
                    _map[forwardX2][forwardY2] = TileType.Chick;
                    break;
            }

            switch (forwardTile)
            {
                case TileType.Chick:
                    _map[forwardX][forwardY] = TileType.Ground;
                    break;
                case TileType.ChickPond:
                    _map[forwardX][forwardY] = TileType.Pond;
                    break;
            }
        }
        _playerX += DirectionType.X[direction];
        _playerY += DirectionType.Y[direction];
    }

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