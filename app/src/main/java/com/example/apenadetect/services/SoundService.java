package com.example.apenadetect.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.apenadetect.R;

public class SoundService extends Service {

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private boolean isPlaying = false;  // Biến để kiểm tra trạng thái phát

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.sound); // Thay sound_file bằng file âm thanh của bạn
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Nếu âm thanh đang chơi, không phát lại
        if (isPlaying) {
            Toast.makeText(this, "Âm thanh đang được phát", Toast.LENGTH_SHORT).show();
            return START_NOT_STICKY;
        }

        // Bắt đầu phát âm thanh
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;  // Đặt cờ trạng thái phát
            Toast.makeText(this, "Phát âm thanh", Toast.LENGTH_SHORT).show();
        }

        // Tự động dừng sau 5 giây
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSound();
            }
        }, 5000); // 5 giây

        return START_NOT_STICKY;
    }

    // Hàm để dừng âm thanh
    private void stopSound() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;  // Đặt lại cờ trạng thái
            Toast.makeText(SoundService.this, "Đã dừng âm thanh", Toast.LENGTH_SHORT).show();
            stopSelf();  // Dừng service
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSound();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Hàm để dừng âm thanh khi nhận tín hiệu dừng từ bên ngoài
    public void stopServiceManually() {
        stopSound();
    }
}

