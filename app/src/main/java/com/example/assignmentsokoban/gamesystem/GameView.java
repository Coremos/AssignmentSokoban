package com.example.assignmentsokoban.gamesystem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.assignmentsokoban.GameActivity;

public class GameView extends ParentView {
    private static final int UNIT_SIZE = 32;
    private static final int GAMEVIEW_WIDTH = UNIT_SIZE * 16;
    private static final int GAMEVIEW_HEIGHT = UNIT_SIZE * 9;
    private static final int STAGE_PIVOT_X = (int)(UNIT_SIZE * 3.5);
    private static final int STAGE_PIVOT_Y = (int)(UNIT_SIZE * 0);

    private GameActivity _gameActivity;

    private ScreenGetter _screenGetter;
    private ResourceInfo _resourceInfo;
    private Bitmap[] _bitmaps;
    private Resources _resources;
    private Collider[] _buttonCollider;
    private Collider _backButtonCollider;

    private int _scaledPivotX;
    private int _scaledPivotY;
    private int _scaledUnitX;
    private int _scaledUnitY;
    private Paint _paint;

    private int _playerX;
    private int _playerY;

    public GameView(Context context, AttributeSet attributeSet) { super(context, attributeSet); }

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
        _bitmaps[ResourceInfo.Pad] = Bitmap.createScaledBitmap(bitmap, _screenGetter.getX(UNIT_SIZE * 3), _screenGetter.getY(UNIT_SIZE * 3), false);
        bitmap = BitmapFactory.decodeResource(_resources, _resourceInfo.Resources[ResourceInfo.BackButton]);
        _bitmaps[ResourceInfo.BackButton] = Bitmap.createScaledBitmap(bitmap, _screenGetter.getX(UNIT_SIZE), _screenGetter.getY(UNIT_SIZE), false);

        _scaledPivotX = _screenGetter.getX(STAGE_PIVOT_X);
        _scaledPivotY = _screenGetter.getY(STAGE_PIVOT_Y);
        _scaledUnitX = _screenGetter.getX(UNIT_SIZE);
        _scaledUnitY = _screenGetter.getY(UNIT_SIZE);

        _paint = new Paint();
        _paint.setAntiAlias(false);
        _paint.setFilterBitmap(false);

        for (int x = 0; x < Map.WIDTH; x++)
        {
            for (int y = 0; y < Map.HEIGHT; y++)
            {
                if (Map.Map.Data[x][y] == TileType.Duck)
                {
                    Map.Map.Data[x][y] = TileType.Ground;
                    _playerX = x;
                    _playerY = y;
                }

            }
        }

        _buttonCollider = new Collider[DirectionType.Count];
        _buttonCollider[DirectionType.Up] = new Collider(UNIT_SIZE * 1, UNIT_SIZE * 3, UNIT_SIZE, UNIT_SIZE);
        _buttonCollider[DirectionType.Down] = new Collider(UNIT_SIZE * 1, UNIT_SIZE * 5, UNIT_SIZE, UNIT_SIZE);
        _buttonCollider[DirectionType.Left] = new Collider(UNIT_SIZE * 0, UNIT_SIZE * 4, UNIT_SIZE, UNIT_SIZE);
        _buttonCollider[DirectionType.Right] = new Collider(UNIT_SIZE * 2, UNIT_SIZE * 4, UNIT_SIZE, UNIT_SIZE);
        _backButtonCollider = new Collider(0, 0, UNIT_SIZE, UNIT_SIZE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (!_canDraw) return;
        drawMap(canvas);
        drawUI(canvas);
    }

    @Override
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

    private void drawMap(Canvas canvas)
    {
        for (int x = 0; x < Map.WIDTH; x++)
        {
            for (int y = 0; y < Map.HEIGHT; y++)
            {
                canvas.drawBitmap(_bitmaps[Map.Map.Data[x][y]], _scaledPivotX + _scaledUnitX * x,
                        _scaledPivotY + _scaledUnitY * y, _paint);
            }
        }

        canvas.drawBitmap(_bitmaps[TileType.Duck], _scaledPivotX + _scaledUnitX * _playerX,
                _scaledPivotY + _scaledUnitY * _playerY, _paint);
    }

    private void drawUI(Canvas canvas)
    {
        canvas.drawBitmap(_bitmaps[ResourceInfo.Pad], _scaledUnitX * 0,_scaledUnitY * 3, _paint);
        canvas.drawBitmap(_bitmaps[ResourceInfo.BackButton], 0, 0, _paint);
    }

    public boolean checkClickButton(int x, int y)
    {
        x = _screenGetter.getOriginalX(x);
        y = _screenGetter.getOriginalY(y);
        if (_backButtonCollider.isOverlap(x, y)) onClickBackButton();
        for (int direction = 0; direction < DirectionType.Count; direction++)
        {
            if (_buttonCollider[direction].isOverlap(x, y))
            {
                movePlayer(direction);
                return true;
            }
        }
        return false;
    }

    public void onClickBackButton()
    {
        _gameActivity.finish();
    }

    public void movePlayer(int direction)
    {
        int forwardX = _playerX;
        int forwardY = _playerY;
        forwardX += DirectionType.X[direction];
        forwardY += DirectionType.Y[direction];

        int forwardTile = Map.Map.Data[forwardX][forwardY];
        if (forwardTile == TileType.Tree) return;
        if (forwardTile == TileType.Chick || forwardTile == TileType.ChickPond)
        {
            int forwardX2 = forwardX + DirectionType.X[direction];
            int forwardY2 = forwardY + DirectionType.Y[direction];
            int forwardTile2 = Map.Map.Data[forwardX2][forwardY2];
            switch (forwardTile2)
            {
                case TileType.Tree:
                case TileType.Chick:
                case TileType.ChickPond:
                    return;
                case TileType.Pond:
                    Map.Map.Data[forwardX2][forwardY2] = TileType.ChickPond;
                    break;
                case TileType.Ground:
                default:
                    Map.Map.Data[forwardX2][forwardY2] = TileType.Chick;
                    break;
            }

            switch (forwardTile)
            {
                case TileType.Chick:
                    Map.Map.Data[forwardX][forwardY] = TileType.Ground;
                    break;
                case TileType.ChickPond:
                    Map.Map.Data[forwardX][forwardY] = TileType.Pond;
                    break;
            }
        }
        _playerX += DirectionType.X[direction];
        _playerY += DirectionType.Y[direction];
        checkClear();
    }

    public void checkClear()
    {
        for (int x = 0; x < Map.WIDTH; x++)
        {
            for (int y = 0; y < Map.HEIGHT; y++)
            {
                if (Map.Map.Data[x][y] == TileType.Pond) break;
            }
        }
        _gameActivity.showClearMessage();
    }
}