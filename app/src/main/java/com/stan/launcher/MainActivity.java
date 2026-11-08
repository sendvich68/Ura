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
    private TextView titleText, alphaText;
    private Button playBtn, langBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            clickSound = MediaPlayer.create(this, R.raw.click);
        } catch (Exception e) {
            clickSound = null;
        }

        createFolders();

        LinearLayout menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setPadding(50, 50, 50, 50);
        menuLayout.setGravity(android.view.Gravity.CENTER);
        // В стиле Windows XP: используем стандартный серый фон
        menuLayout.setBackgroundColor(Color.rgb(236, 233, 216)); 

        // Логотип
        ImageView logo = new ImageView(this);
        try {
            logo.setImageResource(R.drawable.minecraft);
        } catch (Exception e) {}
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(300, 150);
        menuLayout.addView(logo, logoParams);

        alphaText = new TextView(this);
        alphaText.setText("ALPHA VERSION");
        alphaText.setTextColor(Color.YELLOW); 
        alphaText.setTextSize(20f);
        alphaText.setPadding(0, 10, 0, 30);
        menuLayout.addView(alphaText);

        // Текстура версии
        ImageView versionImg = new ImageView(this);
        try {
            versionImg.setImageResource(R.drawable.a01_java_edition);
        } catch (Exception e) {}
        LinearLayout.LayoutParams verParams = new LinearLayout.LayoutParams(200, 100);
        menuLayout.addView(versionImg, verParams);

        // Кнопка Играть
        playBtn = new Button(this);
        playBtn.setOnClickListener(v -> {
            playSound();
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        });
        menuLayout.addView(playBtn);

        // Кнопка Языка
        langBtn = new Button(this);
        langBtn.setOnClickListener(v -> {
            playSound();
            isRussian = !isRussian;
            updateLanguage();
        });
        menuLayout.addView(langBtn);

        setContentView(menuLayout);
        updateLanguage(); // Устанавливаем текст по умолчанию
    }

    private void updateLanguage() {
        if (isRussian) {
            playBtn.setText("Создать мир");
            langBtn.setText("Язык: Русский");
        } else {
            playBtn.setText("Create World");
            langBtn.setText("Language: English");
        }
    }

    private void playSound() {
        if (clickSound != null) {
            clickSound.seekTo(0);
            clickSound.start();
        }
    }

    // Создание папок при каждом запуске
    private void createFolders() {
        String[] folders = {"save_worlds", "resourcepacks", "mods", "versions"};
        for (String folderName : folders) {
            java.io.File folder = new java.io.File(getFilesDir(), folderName);
            if (!folder.exists()) folder.mkdirs();
        }
    }
}
