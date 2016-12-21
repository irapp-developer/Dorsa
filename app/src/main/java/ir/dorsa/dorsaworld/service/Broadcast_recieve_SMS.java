package ir.dorsa.dorsaworld.service;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import ir.dorsa.dorsaworld.Activity_GetPattern;
import ir.dorsa.dorsaworld.Activity_Get_Password;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.Permisions;

public class Broadcast_recieve_SMS extends BroadcastReceiver {
    
    
    public Broadcast_recieve_SMS() {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    String emergency_number= FetchDb.getSetting(context,"emergency_number");
                    
                    if(senderNum.length()>11){
                        senderNum=senderNum.substring((senderNum.length()-9),senderNum.length());
                        emergency_number=emergency_number.substring((emergency_number.length()-9),emergency_number.length());
                        
                        if(senderNum.equals(emergency_number) & "off".equals(message.toLowerCase())){
                                Log.i("SmsReceiver", "1.senderNum: "+ senderNum + "; message: " + message);
                                G.Watchdog.isAppRuned=2;
                                Permisions.setDisablePermisions(context);
                                context.stopService(new Intent(context, Service_Launcher.class));
                                Permisions.setDisablePermisions(context);
                            if(Activity_GetPattern.THIS!=null){
                                Activity_GetPattern.THIS.finish();
                            }

                            if(Activity_Get_Password.THIS!=null){
                                Activity_Get_Password.THIS.finish();
                            }
                        }
                        
                    }
                    
                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);


                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}
