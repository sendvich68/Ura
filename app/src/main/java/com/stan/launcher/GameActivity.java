package com.stan.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.graphics.Color;

public class GameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        FrameLayout layout = new FrameLayout(this);

        // Фон "Грязь" (имя файла строго gryaz.png)
        ImageView bg = new ImageView(this);
        try {
            bg.setImageResource(R.drawable.gryaz);
            bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } catch (Exception e) {}
        layout.addView(bg);

        // 3D-мир (куб)
        GameSurfaceView gameView = new GameSurfaceView(this);
        layout.addView(gameView);

        // Мобильное управление (Джойстик и кнопка прыжка)
        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.BOTTOM);
        
        // Прозрачный фон для контроля
        controls.setPadding(50, 50, 50, 50); 

        Button jumpBtn = new Button(this);
        jumpBtn.setText("JUMP");
        jumpBtn.setAlpha(0.5f);
        
        Button joystickBtn = new Button(this);
        joystickBtn.setText("JOYSTICK");
        joystickBtn.setAlpha(0.5f);

        controls.addView(jumpBtn);
        controls.addView(joystickBtn);
        
        layout.addView(controls);

        setContentView(layout);
    }
}
