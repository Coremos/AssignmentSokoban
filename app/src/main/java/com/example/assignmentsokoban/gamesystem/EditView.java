package com.example.assignmentsokoban.gamesystem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.assignmentsokoban.EditActivity;

public class EditView extends ParentView {
    private EditActivity _editActivity;
    private static final int UNIT_SIZE = 32;
    private static final double UNIT_SIZE_DIVIDER = 1.0 / 32;
    private static final int GAMEVIEW_WIDTH = UNIT_SIZE * 16;
    private static final int GAMEVIEW_HEIGHT = UNIT_SIZE * 9;
    private static final int STAGE_PIVOT_X = (int)(UNIT_SIZE * 3.5);
    private static final int STAGE_PIVOT_Y = (int)(UNIT_SIZE * 0);
    private static final int PALETTE_PIVOT_X = (int)(UNIT_SIZE * 15);
    private static final int PALETTE_PIVOT_Y = (int)(UNIT_SIZE * 1.5);
    private static final int MAP_WIDTH = 9;
    private static final int MAP_HEIGHT = 9;

    private ScreenGetter _screenGetter;
    private ResourceInfo _resourceInfo;
    private Bitmap[] _bitmaps;
    private Resources _resources;
    private Collider[] _paletteCollider;
    private Collider _mapCollider;

    private int[][] _map;
    private int _scaledPivotX;
    private int _scaledPivotY;
    private int _scaledUnitX;
    private int _scaledUnitY;
    private int _scaledPalettePivotX;
    private int _scaledPalettePivotY;
    private Paint _paint;

    private int _selectedUnit;

    public EditView(Context context, AttributeSet attributeSet) { super(context, attributeSet); }

    public void initialize(EditActivity editActivity, int screenWidth, int screenHeight)
    {
        _editActivity = _editActivity;
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
        Bitmap bitmap = BitmapFactory.decodeResource(_resources, _resourceInfo.Resources[ResourceInfo.Select]);
        _bitmaps[ResourceInfo.Select] = Bitmap.createScaledBitmap(bitmap, _screenGetter.getX(UNIT_SIZE), _screenGetter.getY(UNIT_SIZE), false);

        _scaledPivotX = _screenGetter.getX(STAGE_PIVOT_X);
        _scaledPivotY = _screenGetter.getY(STAGE_PIVOT_Y);
        _scaledPalettePivotX = _screenGetter.getX(PALETTE_PIVOT_X);
        _scaledPalettePivotY = _screenGetter.getY(PALETTE_PIVOT_Y);
        _scaledUnitX = _screenGetter.getX(UNIT_SIZE);
        _scaledUnitY = _screenGetter.getY(UNIT_SIZE);

        _paint = new Paint();
        _paint.setAntiAlias(false);
        _paint.setFilterBitmap(false);

        _selectedUnit = TileType.Ground;

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

        _paletteCollider = new Collider[TileType.Count];
        for (int i = TileType.Ground; i < TileType.Count; i++)
        {
            _paletteCollider[i] = new Collider(PALETTE_PIVOT_X, PALETTE_PIVOT_Y + UNIT_SIZE * i, UNIT_SIZE, UNIT_SIZE);
        }

        _mapCollider = new Collider(STAGE_PIVOT_X, STAGE_PIVOT_Y, UNIT_SIZE * MAP_WIDTH, UNIT_SIZE * MAP_HEIGHT);
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
            case MotionEvent.ACTION_MOVE:
                checkClickButton(x, y);
                return true;
        }
        return false;
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
    }

    private void drawUI(Canvas canvas)
    {
        for (int i = TileType.Ground; i < TileType.Count; i++)
        {
            canvas.drawBitmap(_bitmaps[i], _scaledPalettePivotX,_scaledPalettePivotY + _scaledUnitY * i, _paint);
        }
        canvas.drawBitmap(_bitmaps[ResourceInfo.Select], _scaledPalettePivotX, _scaledPalettePivotY + _scaledUnitY * _selectedUnit, _paint);
    }

    public boolean checkClickButton(int x, int y)
    {
        x = _screenGetter.getOriginalX(x);
        y = _screenGetter.getOriginalY(y);
        int tiledX = (int)((x - STAGE_PIVOT_X) * UNIT_SIZE_DIVIDER);
        int tiledY = (int)((y - STAGE_PIVOT_Y) * UNIT_SIZE_DIVIDER);
        if (_mapCollider.isOverlap(x, y)) paintMap(tiledX, tiledY, _selectedUnit);

        for (int tile = TileType.Ground; tile < TileType.Count; tile++)
        {
            if (_paletteCollider[tile].isOverlap(x, y))
            {
                _selectedUnit = tile;
                return true;
            }
        }
        return false;
    }

    public void paintMap(int tiledX, int tiledY, int tile)
    {
        if (_map[tiledX][tiledY] == tile) return;
        if (tile == TileType.Duck)
        {
            for (int x = 0; x < MAP_WIDTH; x++)
            {
                for (int y = 0; y < MAP_HEIGHT; y++)
                {
                    if (_map[x][y] == TileType.Duck) _map[x][y] = TileType.Ground;
                }
            }
        }
        _map[tiledX][tiledY] = tile;
    }
}