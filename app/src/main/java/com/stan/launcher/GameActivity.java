package com.stan.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class GameActivity extends Activity {
    private GameSurfaceView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        FrameLayout layout = new FrameLayout(this);
        gameView = new GameSurfaceView(this);
        layout.addView(gameView);

        // Тут мы позже добавим джойстик (мобильное управление)
        setContentView(layout);
    }
}
