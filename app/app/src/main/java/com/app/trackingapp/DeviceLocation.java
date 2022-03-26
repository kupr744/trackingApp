package com.app.trackingapp;

import android.Manifest;
import android.content.Intent;
import android.os.IBinder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

public class DeviceLocation extends android.app.Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
