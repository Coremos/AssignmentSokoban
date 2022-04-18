package com.example.assignmentsokoban.gamesystem;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private SurfaceHolder _surfaceHolder;
    private GameView _gameView;
    private boolean _isRunning;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView)
    {
        _surfaceHolder = surfaceHolder;
        _gameView = gameView;
        _isRunning = false;
    }

    public SurfaceHolder getSurfaceHolder() { return _surfaceHolder; }

    public void setRunning(boolean value) { _isRunning = value; }

    public void run()
    {
        try
        {
            Canvas canvas;
            while(_isRunning)
            {
                canvas = null;
                try
                {
                    canvas = _surfaceHolder.lockCanvas(null);
                    synchronized(_surfaceHolder)
                    {
                        try
                        {
                            _gameView.onDraw(canvas);
                            Thread.sleep(2);
                        }
                        catch(Exception e) { }
                    }
                }
                finally
                {
                    if (canvas != null)
                    {
                        _surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
        catch (Exception e) { }
    }

}
