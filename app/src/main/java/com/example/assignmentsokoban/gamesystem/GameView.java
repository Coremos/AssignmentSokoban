package com.example.assignmentsokoban.gamesystem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.assignmentsokoban.GameActivity;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    private static final int GAMEVIEW_WIDTH = 1920;
    private static final int GAMEVIEW_HEIGHT = 1080;

    private GameActivity _gameActivity;
    private MainThread _mainThread;
    private ScreenGetter _screenGetter;
    private boolean _canDraw;

    public GameView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        getHolder().addCallback(this);
        _mainThread =  new MainThread(getHolder(), this);
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
        Log.v("Debug", "Screen Width : " + screenWidth + " / Screen Height : " + screenHeight);
        _screenGetter.setGameViewSize(GAMEVIEW_WIDTH, GAMEVIEW_HEIGHT);
        Log.v("Debug", "Game Width : " + GAMEVIEW_WIDTH + " / Screen Height : " + GAMEVIEW_HEIGHT);

        _canDraw = true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (!_canDraw) return;

        Paint  backPaint = new Paint();
        backPaint.setColor(Color.BLUE);
        canvas.drawRect(_screenGetter.getX(0), _screenGetter.getY(0), _screenGetter.getX(240), _screenGetter.getY(240), backPaint);
        backPaint.setColor(Color.GREEN);
        canvas.drawRect(_screenGetter.getX(1680), _screenGetter.getY(840), _screenGetter.getX(1920), _screenGetter.getY(1080), backPaint);
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