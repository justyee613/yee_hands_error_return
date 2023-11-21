package com.example.app;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class NotificationsFragment extends Fragment {

    private Button startButton;
    private Button stopButton;
    private MediaPlayer mediaPlayer;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // 獲取對開始Button控件的引用
        startButton = view.findViewById(R.id.start_button);

        // 設置單擊監聽器
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMetronome();
            }
        });

        // 獲取對停止Button控件的引用
        stopButton = view.findViewById(R.id.stop_button);

        // 設置單擊監聽器
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMetronome();
            }
        });

        return view;
    }

    private void startMetronome() {
        if (mediaPlayer == null) {
            // 創建MediaPlayer對象並關聯到聲音文件
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.click_sound);

            // 設置循環播放
            mediaPlayer.setLooping(true);
        }
        // 開始播放聲音
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMetronome();
    }

    private void stopMetronome() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}