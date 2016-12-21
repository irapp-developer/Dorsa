package ir.dorsa.dorsaworld.service;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.Permisions;

public class Broadcast_Recieve_Call extends BroadcastReceiver {

    private String mode = "";

    public Broadcast_Recieve_Call(String mode) {
        this.mode = mode;
    }

    public Broadcast_Recieve_Call() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.


        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            // This code will execute when the phone has an incoming call
            // get the phone number 
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Log.d(G.LOG_TAG, "Recieve call");
            //--------- start app watcher --------------------
            if (mode.equals("launcher-false")) {
                try {
                    G.selectedKid.joKid = FetchDb.getSelectedKid(context);
                    Intent mIntent = new Intent();
                    mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    mIntent.setAction(Intent.ACTION_MAIN);
                    mIntent.setPackage("com.android.phone");
                    ResolveInfo rInfo = context.getPackageManager().resolveActivity(mIntent, 0);
                    if (rInfo == null) return;
                    String className = rInfo.activityInfo.name;
                    Permisions.setPermissionApps(context, new JSONObject(G.selectedKid.joKid.getString("settings")), "com.android.phone", className, "false");
                   /* PackageManager pm = context.getPackageManager();
                    Intent launchIntent = pm.getLaunchIntentForPackage("com.android.phone");
                    context.startActivity(launchIntent);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (mode.equals("launcher-true")) {
                killCall(context, incomingNumber);
            } else if (mode.equals("WD-false")) {
                killCall(context, incomingNumber);
               /* try {
                    JSONObject jo= FetchDb.getSelectedKid(context);
                    JSONObject kidSettings = new JSONObject(jo.getString("settings"));
                    Permisions.setPermissionLauncher(context,kidSettings);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               
                context.startService(new Intent(App.getContext(), Service_Launcher.class));
                context.stopService(new Intent(App.getContext(), Service_Whatchdog.class));*/
            }


        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_IDLE)
                || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            // This code will execute when the call is disconnected
            Toast.makeText(context, "Detected call hangup event", Toast.LENGTH_LONG).show();
            Log.d(G.LOG_TAG, "Detected call hangup event");

//            context.startService(new Intent(App.getContext(), Service_Launcher.class));
//            context.stopService(new Intent(App.getContext(), Service_Whatchdog.class));
        }

    }


    public void killCall(Context mContext, String phoneNumber) {

        try {


            //String serviceManagerName = "android.os.IServiceManager";
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";

            Class telephonyClass;
            Class telephonyStubClass;
            Class serviceManagerClass;
            Class serviceManagerNativeClass;

            Method telephonyEndCall;

            Object telephonyObject;
            Object serviceManagerObject;

            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);

            Method getService =
                    serviceManagerClass.getMethod("getService", String.class);

            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                    "asInterface", IBinder.class);

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");

            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);

            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);
            Log.v("VoiceCall", "Call End Complete.");

            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
            values.put(CallLog.Calls.TYPE, CallLog.Calls.MISSED_TYPE);
            values.put(CallLog.Calls.DATE, System.currentTimeMillis());
            values.put(CallLog.Calls.DURATION, 50);
            values.put(CallLog.Calls.NUMBER, phoneNumber);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mContext.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VoiceCall", "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("VoiceCall", "Exception object: " + e);
        }


    }
}
