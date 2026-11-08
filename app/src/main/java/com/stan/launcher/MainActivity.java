package com.stan.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Создаем меню программно (чтобы не возиться с XML версткой)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setGravity(android.view.Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("Stan Launcher\nMinecraft Bedrock v1.8");
        title.setTextSize(24f);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(title);

        Button playButton = new Button(this);
        playButton.setText("▶ Играть (v1.8)");
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMinecraft();
            }
        });
        layout.addView(playButton);

        Button settingsButton = new Button(this);
        settingsButton.setText("⚙ Настройки управления");
        layout.addView(settingsButton);

        setContentView(layout);

        // Создаем папки при старте
        createFolders();
    }

    private void launchMinecraft() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.mojang.minecraftpe");
        if (intent != null) {
            startActivity(intent);
        } else {
            // Если игра не найдена, показываем ошибку
            TextView errorText = new TextView(this);
            errorText.setText("Minecraft PE не установлен на устройстве!");
            errorText.setTextColor(0xFFFF0000);
            ((LinearLayout) findViewById(android.R.id.content).getRootView()).addView(errorText);
        }
    }

    private void createFolders() {
        String[] folders = {"versions", "mods", "resourcepacks", "saves"};
        for (String folderName : folders) {
            java.io.File folder = new java.io.File(getFilesDir(), folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }
}
