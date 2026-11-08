package com.stan.launcher;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {
    private MediaPlayer clickSound;
    private boolean isRussian = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            clickSound = MediaPlayer.create(this, R.raw.click);
        } catch (Exception e) {
            clickSound = null; // Если звука нет, приложение не вылетит
        }

        LinearLayout menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setPadding(50, 50, 50, 50);
        menuLayout.setGravity(android.view.Gravity.CENTER);
        menuLayout.setBackgroundColor(0xFF0058E3); 

        // Логотип (Имя файла строго minecraft.png)
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.minecraft); 
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(400, 200);
        menuLayout.addView(logo, logoParams);

        TextView alphaText = new TextView(this);
        alphaText.setText("ALPHA VERSION");
        alphaText.setTextColor(Color.YELLOW); 
        alphaText.setTextSize(20f);
        alphaText.setPadding(0, 0, 0, 30);
        menuLayout.addView(alphaText);

        // Текстура версии (Имя файла строго a01_java_edition.png)
        ImageView versionImg = new ImageView(this);
        versionImg.setImageResource(R.drawable.a01_java_edition); 
        LinearLayout.LayoutParams verParams = new LinearLayout.LayoutParams(300, 150);
        menuLayout.addView(versionImg, verParams);

        Button playBtn = new Button(this);
        playBtn.setText(isRussian ? "Создать мир" : "Create World");
        playBtn.setOnClickListener(v -> {
            playSound();
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        });
        menuLayout.addView(playBtn);

        Button langBtn = new Button(this);
        langBtn.setText(isRussian ? "Язык: Русский" : "Language: English");
        langBtn.setOnClickListener(v -> {
            playSound();
            isRussian = !isRussian;
            recreate(); 
        });
        menuLayout.addView(langBtn);

        setContentView(menuLayout);
    }

    private void playSound() {
        if (clickSound != null) clickSound.start();
    }
}
