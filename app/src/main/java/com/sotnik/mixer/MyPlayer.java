package com.sotnik.mixer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyPlayer {
    //Величина, на которую каждый шаг меняется громкость.
    private float deltaValue;
    private Context context;
    private MediaPlayer firstPlayer, secondPlayer;
    //Время затухания/возникновения (кроссфейда)
    private int crossfadeTime;
    //Переменные для хранения текущих громкостей плееров.
    private float fadeInVolume, fadeOutVolume;
    //Лист таймеров, что б остановить все
    private List<Timer> timerList = new ArrayList<>();

    MyPlayer(Context context) {
        this.context = context;
    }

    public void setCrossfadeTime(int crossfadeTime) {
        this.crossfadeTime = crossfadeTime * 1000;
        this.deltaValue = 1f / (this.crossfadeTime / Consts.FADE_STEP_INTERVAL);
    }

    public void setFirstTrack(Uri firstTrack) {
        if (firstPlayer == null) {
            firstPlayer = new MediaPlayer();
        } else {
            firstPlayer.reset();
        }

        try {
            firstPlayer.setDataSource(context, firstTrack);
            firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            firstPlayer.prepareAsync();
        } catch (IOException ex) {
            ex.printStackTrace();
            firstPlayer = null;
        }
    }

    public void setSecondTrack(Uri secondTrack) {
        if (secondPlayer == null) {
            secondPlayer = new MediaPlayer();
        } else {
            secondPlayer.reset();
        }

        try {
            secondPlayer.setDataSource(context, secondTrack);
            secondPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            secondPlayer.prepareAsync();
        } catch (IOException ex) {
            ex.printStackTrace();
            secondPlayer = null;
        }

    }

    /***
     * Проверка плееров и старт воспроизведения
     * @return Возвращает удалось ли начать проигрывание.
     */
    public Exception start() {
        if (firstPlayer != null & secondPlayer != null) {
            if (firstPlayer.getDuration() > crossfadeTime & secondPlayer.getDuration() > crossfadeTime) {
                play(firstPlayer, secondPlayer, false);
                return null;
            } else {
                return new Exception("Длительность аудиофайла должна быть больше чем величина кроссфейда.");
            }
        } else {
            return new Exception("Возможно, не выбраны файлы.");
        }


    }


    public void stop() {
        if (firstPlayer != null) {
            if (firstPlayer.isPlaying()) {
                firstPlayer.pause();
                firstPlayer.seekTo(0);
            }
        }

        if (secondPlayer != null) {
            if (secondPlayer.isPlaying()) {
                secondPlayer.pause();
                secondPlayer.seekTo(0);
            }
        }

        cleanTimerList(timerList);
    }

    private void cleanTimerList(List<Timer> timerList) {
        for (Timer timer : timerList) {
            timer.cancel();
            timer.purge();
        }
        timerList.clear();

    }


    private void play(MediaPlayer currentTrack, MediaPlayer nextTrack, boolean needFadeIn) {
        if (needFadeIn) {
            startWithFadeIn(currentTrack);
        } else {
            currentTrack.setVolume(1f, 1f);
            currentTrack.start();
        }

        Timer timer = new Timer();
        timerList.add(timer);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (currentTrack.getCurrentPosition() >= currentTrack.getDuration() - crossfadeTime) {
                    play(nextTrack, currentTrack, true);
                    fadeOut(currentTrack);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }


    private void startWithFadeIn(MediaPlayer player) {
        player.setVolume(0f, 0f);
        player.start();
        fadeInVolume = 0f;
        fadePlayer(player, Consts.FadeMode.FADE_IN);
    }

    private void fadeOut(MediaPlayer player) {
        fadeOutVolume = 1f;
        fadePlayer(player, Consts.FadeMode.FADE_OUT);

    }

    private void fadePlayer(MediaPlayer player, int fadeMode) {
        final Timer timer = new Timer();
        timerList.add(timer);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                makeFadeStep(player, fadeMode == Consts.FadeMode.FADE_OUT ? deltaValue * -1 : deltaValue);
                if (fadeMode == Consts.FadeMode.FADE_OUT) {
                    if (fadeOutVolume <= 0f) {
                        timer.cancel();
                        timer.purge();
                    }
                } else {
                    if (fadeInVolume >= 1f) {
                        timer.cancel();
                        timer.purge();
                    }
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, Consts.FADE_STEP_INTERVAL, Consts.FADE_STEP_INTERVAL);
    }

    private void makeFadeStep(MediaPlayer player, float v) {
        float newVolume;

        if (v > 0) {
            fadeInVolume += v;
            newVolume = fadeInVolume;
        } else {
            fadeOutVolume += v;
            newVolume = fadeOutVolume;
        }
        player.setVolume(newVolume, newVolume);
    }
}
