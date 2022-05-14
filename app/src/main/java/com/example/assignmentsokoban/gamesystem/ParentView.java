package com.example.assignmentsokoban.gamesystem;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ParentView extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread _mainThread;
    protected boolean _canDraw;

    public ParentView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        getHolder().addCallback(this);
        _mainThread = new MainThread(getHolder(), this);
        _canDraw = false;
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

    protected void onDraw(Canvas canvas) { }
}