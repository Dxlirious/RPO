package com.traintickets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.traintickets.R;

/**
 * Экран загрузки (Splash Screen).
 * Показывает логотип с анимацией, затем переходит на MainActivity.
 */
public class SplashActivity extends BaseActivity {

    private static final int SPLASH_DURATION_MS = 2500;

    // Handler для отмены при уничтожении Activity (избегаем утечки)
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable navigateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView   ivLogo    = findViewById(R.id.ivLogo);
        TextView    tvName    = findViewById(R.id.tvAppName);
        TextView    tvTagline = findViewById(R.id.tvTagline);
        ProgressBar progress  = findViewById(R.id.progressBar);

        // Загрузка анимаций
        Animation fadeIn  = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Логотип — сразу
        ivLogo.setVisibility(View.VISIBLE);
        ivLogo.startAnimation(fadeIn);

        // Текст — через 400 мс
        handler.postDelayed(() -> {
            if (isDestroyed() || isFinishing()) return;
            tvName.setVisibility(View.VISIBLE);
            tvTagline.setVisibility(View.VISIBLE);
            tvName.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
            tvTagline.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        }, 400);

        // Прогресс — через 800 мс
        handler.postDelayed(() -> {
            if (isDestroyed() || isFinishing()) return;
            progress.setVisibility(View.VISIBLE);
        }, 800);

        // Переход на MainActivity
        navigateRunnable = () -> {
            if (isDestroyed() || isFinishing()) return;
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        };
        handler.postDelayed(navigateRunnable, SPLASH_DURATION_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Убираем все pending callbacks при уничтожении
        handler.removeCallbacksAndMessages(null);
    }
}
