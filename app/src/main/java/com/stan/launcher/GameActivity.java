package com.stan.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;

public class GameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        FrameLayout layout = new FrameLayout(this);
        
        // Запускаем Mojo Launcher
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.movtery.mojolauncher");
            if (launchIntent != null) {
                startActivity(launchIntent);
                // Если лаунчер запустился, мы можем закрыть нашу активити,
                // чтобы остаться на экране Java-игры
            } else {
                TextView errorText = new TextView(this);
                errorText.setText("Установите Mojo Launcher!\n\n(Package: com.movtery.mojolauncher)");
                errorText.setTextColor(Color.RED);
                errorText.setTextSize(20f);
                errorText.setGravity(Gravity.CENTER);
                errorText.setBackgroundColor(Color.BLACK);
                layout.addView(errorText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(layout);
    }
}
