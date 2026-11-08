package com.stan.launcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
        playButton.setOnClickListener(v -> launchMinecraft());
        layout.addView(playButton);

        // Кнопка включения оверлея
        Button overlayButton = new Button(this);
        overlayButton.setText("🎮 Включить управление");
        overlayButton.setOnClickListener(v -> checkOverlayPermission());
        layout.addView(overlayButton);

        setContentView(layout);
        createFolders();
    }

    private void launchMinecraft() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.mojang.minecraftpe");
        if (intent != null) {
            startActivity(intent);
        }
    }

    // Проверка разрешения на показ поверх других приложений
    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                startService(new Intent(this, OverlayService.class));
            }
        } else {
            startService(new Intent(this, OverlayService.class));
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
