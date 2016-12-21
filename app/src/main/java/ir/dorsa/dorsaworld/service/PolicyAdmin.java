package ir.dorsa.dorsaworld.service;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 2/25/16 AD.
 */
public class PolicyAdmin extends DeviceAdminReceiver {

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.d(G.LOG_TAG,"disable Admin");
        super.onDisabled(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.d(G.LOG_TAG,"enable Admin");
        super.onEnabled(context, intent);
    }
}
