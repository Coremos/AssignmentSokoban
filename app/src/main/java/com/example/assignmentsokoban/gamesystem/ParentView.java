package com.example.assignmentsokoban.gamesystem;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ParentView extends SurfaceView implements SurfaceHolder.Callback {
    public ParentView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    public void surfaceCreated(SurfaceHolder holder) { }

    public void surfaceDestroyed(SurfaceHolder holder) { }

    protected void onDraw(Canvas canvas) { }
}