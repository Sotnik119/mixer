package com.sotnik.mixer.ViewModels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.sotnik.mixer.BR;

public class MainModel extends BaseObservable {
    private int crossfadeProgress = 0;
    private String firstTrackName = "Не выбрано", secondTrackName = "Не выбрано";
    private View.OnClickListener selectFirstTrack, selectSecondTrack, playStopButton;
    private boolean playing = false;


    @Bindable
    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        notifyPropertyChanged(BR.playing);
    }

    @Bindable
    public String getFirstTrackName() {
        return firstTrackName;
    }

    public void setFirstTrackName(String firstTrackName) {
        this.firstTrackName = firstTrackName;
        notifyPropertyChanged(BR.firstTrackName);
    }

    @Bindable
    public String getSecondTrackName() {
        return secondTrackName;
    }

    public void setSecondTrackName(String secondTrackName) {
        this.secondTrackName = secondTrackName;
        notifyPropertyChanged(BR.secondTrackName);
    }

    @Bindable
    public View.OnClickListener getSelectFirstTrack() {
        return selectFirstTrack;
    }

    public void setSelectFirstTrack(View.OnClickListener selectFirstTrack) {
        this.selectFirstTrack = selectFirstTrack;
        notifyPropertyChanged(BR.selectFirstTrack);
    }

    @Bindable
    public View.OnClickListener getSelectSecondTrack() {
        return selectSecondTrack;
    }

    public void setSelectSecondTrack(View.OnClickListener selectSecondTrack) {
        this.selectSecondTrack = selectSecondTrack;
        notifyPropertyChanged(BR.selectSecondTrack);
    }

    @Bindable
    public View.OnClickListener getPlayStopButton() {
        return playStopButton;
    }

    public void setPlayStopButton(View.OnClickListener playStopButton) {
        this.playStopButton = playStopButton;
        notifyPropertyChanged(BR.playStopButton);
    }

    @Bindable
    public int getCrossfadeProgress() {
        return crossfadeProgress;
    }

    public void setCrossfadeProgress(int crossfadeProgress) {
        this.crossfadeProgress = crossfadeProgress;
        notifyPropertyChanged(BR.crossfadeProgress);
    }
}
