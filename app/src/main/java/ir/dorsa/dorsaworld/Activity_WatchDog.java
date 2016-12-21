package ir.dorsa.dorsaworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.service.Service_Launcher;
import ir.dorsa.dorsaworld.service.Service_Whatchdog;

/**
 * Created by mehdi on 2/27/16 AD.
 */
public class Activity_WatchDog extends AppCompatActivity {

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if(G.Watchdog.isAppRuned==1){
            Log.d(G.LOG_TAG,"WD have to Destroy");
            G.Watchdog.isAppRuned=0;
                G.Watchdog.waitForBack=null;
            stopService(new Intent(this,Service_Whatchdog.class));
            startService(new Intent(App.getContext(), Service_Launcher.class));
            finish();
           //stop service watchDog
            //start service launcher
            //finish
        }else if(G.Watchdog.isAppRuned==2){
            finish();
//            G.Watchdog.isAppRuned = true;
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        G.Watchdog.waitForBack=this;
        setContentView(R.layout.activity_wait_for_back);
        getSupportActionBar().hide();
        Log.d(G.LOG_TAG, "WD start waiting");
    }
}
