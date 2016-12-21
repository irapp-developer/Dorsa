package ir.dorsa.dorsaworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ir.dorsa.dorsaworld.other.FetchDb;

/**
 * Created by mehdi on 1/26/16 AD.
 */

public class Activity_Launcher_Kid extends Activity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.trans_layout);
//        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));
        if(FetchDb.getKidsCount(this)>0){
            //check android version
            //if android version is higher than 6
            startActivity(new Intent(this,launcher.class));
        }else{
            startActivity(new Intent(this,Activity_Parent.class));
        }
        finish();
    }

    
}
