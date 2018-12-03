package com.sotnik.mixer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sotnik.mixer.ViewModels.MainModel;
import com.sotnik.mixer.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private MainModel model;
    private MyPlayer myPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mainBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main, null, false);
        mainBinding.setModel(model = new MainModel());

        myPlayer = new MyPlayer(getApplicationContext());

        model.setSelectFirstTrack(v -> selectTrack(Consts.FileRequestType.FIRST_TRACK));
        model.setSelectSecondTrack(v -> selectTrack(Consts.FileRequestType.SECOND_TRACK));

        model.setPlayStopButton(v -> {
            if (model.isPlaying()) {
                myPlayer.stop();
                model.setPlaying(false);
                //костыль (через xml не сработало:( )
                mainBinding.seekBar.setEnabled(true);
            } else {
                myPlayer.setCrossfadeTime(model.getCrossfadeProgress() + 2);
                Exception ex = myPlayer.start();
                if (ex == null) {
                    model.setPlaying(true);
                    //костыль (через xml не сработало:( )
                    mainBinding.seekBar.setEnabled(false);
                } else {
                    makeToast(String.format("Ошибка! %s", ex.getMessage()));
                }
            }
        });
        setContentView(mainBinding.getRoot());
    }

    private void selectTrack(int requestCode) {
        if (isHaveAccessToStorage()) {
            Intent selectFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            selectFileIntent.setType("audio/*");
            startActivityForResult(selectFileIntent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Consts.FileRequestType.FIRST_TRACK || requestCode == Consts.FileRequestType.SECOND_TRACK) {
                bindTrack(requestCode, data.getData());
            }
        }

    }

    private void bindTrack(int requestCode, Uri data) {
        switch (requestCode) {
            case Consts.FileRequestType.FIRST_TRACK: {
                myPlayer.setFirstTrack(data);
                bindTrackName(requestCode, data);
                break;
            }
            case Consts.FileRequestType.SECOND_TRACK: {
                myPlayer.setSecondTrack(data);
                bindTrackName(requestCode, data);
                break;
            }
        }

    }

    private void bindTrackName(int requestCode, Uri data) {
        String path = getRealPathFromURI(data);
        String file = path.substring(path.lastIndexOf("/") + 1);
        switch (requestCode) {
            case Consts.FileRequestType.FIRST_TRACK: {
                model.setFirstTrackName(file);
                break;
            }
            case Consts.FileRequestType.SECOND_TRACK: {
                model.setSecondTrackName(file);
                break;
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { //checking
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myPlayer.stop();
    }

    private boolean isHaveAccessToStorage() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permissionStatus = ContextCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(this),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    makeToast("Необходимо дать разрешение на чтение файлов в настройках устройства!");
                    return false;
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            999);
                }
            } else {
                return true;
            }
        } else {
            int permission = PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permission == PermissionChecker.PERMISSION_GRANTED) {
                return true;
            } else {
                makeToast("Необходимо дать разрешение на чтение файлов в настройках устройства!");
                return false;
            }
        }

        return false;
    }


    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
