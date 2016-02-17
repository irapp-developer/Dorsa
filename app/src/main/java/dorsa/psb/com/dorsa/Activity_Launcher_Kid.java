package dorsa.psb.com.dorsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import dorsa.psb.com.dorsa.other.FetchDb;

/**
 * Created by mehdi on 1/26/16 AD.
 */

public class Activity_Launcher_Kid extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.launcher_layout);
        
        if(FetchDb.getKidsCount(this)>0){
            startActivity(new Intent(this,launcher.class));
        }else{
            startActivity(new Intent(this,Activity_Parent.class));
        }
        finish();
    }

    
}
