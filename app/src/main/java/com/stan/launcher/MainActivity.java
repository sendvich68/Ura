package com.stan.launcher;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setBackgroundColor(0xFF1E1E1E);

        TextView title = new TextView(this);
        title.setText("Stan Launcher\nMinecraft Java Edition for Android\nVersion 0.1 Alpha");
        title.setTextSize(24f);
        title.setTextColor(0xFFFFFFFF);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(title);

        Button playButton = new Button(this);
        playButton.setText("▶ Запустить игру");
        playButton.setTextSize(20f);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMinecraft();
            }
        });
        layout.addView(playButton);

        TextView infoText = new TextView(this);
        infoText.setText("\nOpen-Source проект\nНе является пиратским лаунчером.");
        infoText.setTextColor(0xFFAAAAAA);
        infoText.setTextSize(14f);
        infoText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(infoText);

        setContentView(layout);

        // Создаем папки и копируем туда ресурсы
        createFolders();
        copyAssetsToStorage();
    }

    private void launchMinecraft() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.mojang.minecraftpe");
        if (intent != null) {
            startActivity(intent);
        } else {
            TextView errorText = new TextView(this);
            errorText.setText("\nОшибка: Игра не установлена!\nСкачайте Minecraft Bedrock для запуска.");
            errorText.setTextColor(0xFFFF0000);
            errorText.setTextSize(16f);
            LinearLayout layout = (LinearLayout) findViewById(android.R.id.content).getRootView();
            layout.addView(errorText);
        }
    }

    private void createFolders() {
        String[] folders = {"versions", "mods", "resourcepacks", "saves", "textures", "sounds", "songs"};
        for (String folderName : folders) {
            File folder = new File(getFilesDir(), folderName);
            if (!folder.exists()) folder.mkdirs();
        }
    }

    // Функция распаковки файлов из APK в память телефона
    private void copyAssetsToStorage() {
        AssetManager assetManager = getAssets();
        String[] folders = {"textures", "sounds", "songs"};
        
        for (String folder : folders) {
            try {
                String[] files = assetManager.list(folder);
                if (files == null || files.length == 0) continue;
                
                File destDir = new File(getFilesDir(), folder);
                for (String filename : files) {
                    File destFile = new File(destDir, filename);
                    // Копируем только если файла еще нет (чтобы не перезаписывать каждый раз)
                    if (!destFile.exists()) {
                        InputStream in = assetManager.open(folder + "/" + filename);
                        OutputStream out = new FileOutputStream(destFile);
                        byte[] buffer = new byte[4096];
                        int length;
                        while ((read = in.read(buffer)) > 0) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        out.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
