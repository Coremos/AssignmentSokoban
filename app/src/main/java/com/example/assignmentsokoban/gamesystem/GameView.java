package com.example.assignmentsokoban.gamesystem;

import android.content.Context;
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
    private static final int GAMEVIEW_WIDTH = UNIT_SIZE * 32;
    private static final int GAMEVIEW_HEIGHT = UNIT_SIZE * 18;

    private GameActivity _gameActivity;
    private MainThread _mainThread;
    private ScreenGetter _screenGetter;
    private boolean _canDraw;

    public GameView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        getHolder().addCallback(this);
        _mainThread = new MainThread(getHolder(), this);
        _canDraw = false;
        loadResources();
    }

    public void loadResources()
    {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.);
    }

    public void initialize(GameActivity gameActivity, int screenWidth, int screenHeight)
    {
        _gameActivity = gameActivity;
        _screenGetter = new ScreenGetter(screenWidth, screenHeight);
        _screenGetter.setGameViewSize(GAMEVIEW_WIDTH, GAMEVIEW_HEIGHT);
        _canDraw = true;
    }

    private void drawDebug(Canvas canvas)
    {
        Paint  backPaint = new Paint();
        backPaint.setColor(Color.BLUE);
        canvas.drawRect(_screenGetter.getX(0), _screenGetter.getY(0), _screenGetter.getX(UNIT_SIZE), _screenGetter.getY(UNIT_SIZE), backPaint);
        backPaint.setColor(Color.GREEN);
        canvas.drawRect(_screenGetter.getX(GAMEVIEW_WIDTH - UNIT_SIZE), _screenGetter.getY(GAMEVIEW_HEIGHT - UNIT_SIZE), _screenGetter.getX(GAMEVIEW_WIDTH), _screenGetter.getY(GAMEVIEW_HEIGHT), backPaint);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (!_canDraw) return;
        drawDebug(canvas);
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