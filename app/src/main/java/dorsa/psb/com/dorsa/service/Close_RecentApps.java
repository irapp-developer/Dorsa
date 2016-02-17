package dorsa.psb.com.dorsa.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Close_RecentApps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent closeDialog =
                new Intent(Intent.
                        ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeDialog);
       finish();
    }

  
}
