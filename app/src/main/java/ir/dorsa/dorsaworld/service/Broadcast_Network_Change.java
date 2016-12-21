package ir.dorsa.dorsaworld.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

public class Broadcast_Network_Change extends BroadcastReceiver {
    public Broadcast_Network_Change() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(G.LOG_TAG,"Got network change");
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        if (isConnected) {
            Log.d("Network Available ", "YES");
            Func.setInternetEnabled(context, false);
        } else {
            Log.d("Network Available ", "NO");
        }
    }
}
