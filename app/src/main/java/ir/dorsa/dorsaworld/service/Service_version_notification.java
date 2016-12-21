package ir.dorsa.dorsaworld.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import ir.dorsa.dorsaworld.other.G;

public class Service_version_notification extends IntentService {
   
    public Service_version_notification() {
        super("Service_version_notification");
    }

    @Override
    protected void onHandleIntent(Intent mIntent) {
        
        if (mIntent != null) {
            if(mIntent.hasExtra("extra_1")){
                String extra=mIntent.getStringExtra("extra_1");
                if (extra.contains("http://")){//start download intent
                    String url = extra;
                    Log.d(G.LOG_TAG,"check version url is :"+url);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
            
         
            }else{
        }
        
    }
}
