package com.example.fitbit_api_test.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fitbit_api_test.services.LocationPollingService;

public class BootCompleteReceiver extends BroadcastReceiver {

    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BootCompleteReceiver","Boot Callback Received");
        context.startService(new Intent(context, LocationPollingService.class));
    }
}
