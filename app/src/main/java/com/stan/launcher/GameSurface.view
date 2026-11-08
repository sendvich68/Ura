package com.stan.launcher;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GameSurfaceView extends GLSurfaceView {
    private GameRenderer renderer;

    public GameSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new GameRenderer(context);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            renderer.angle += (x - renderer.lastX) * 0.5f;
            renderer.lastX = x;
            requestRender();
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            renderer.lastX = event.getX();
        }
        return true;
    }
}
