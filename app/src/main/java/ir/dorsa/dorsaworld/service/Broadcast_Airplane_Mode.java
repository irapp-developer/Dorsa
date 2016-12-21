package ir.dorsa.dorsaworld.service;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import ir.dorsa.dorsaworld.other.G;

public class Broadcast_Airplane_Mode extends BroadcastReceiver {
    public Broadcast_Airplane_Mode() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(G.LOG_TAG, "Air plane mode Changed :" + isAirplaneModeOn(context));
        if(!isAirplaneModeOn(context)) {
            Settings.System.putInt(
                    context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0);
            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(
                        context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0);
            } else {
                Settings.Global.putInt(
                        context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0);
            }*/

            Intent mIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            mIntent.putExtra("state", false);
            context.sendBroadcast(mIntent);
        }
    }


    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }
}
