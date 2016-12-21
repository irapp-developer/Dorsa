package ir.dorsa.dorsaworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.service.Service_Launcher;
import ir.dorsa.dorsaworld.service.Service_RunInBackground;

public class launcher extends Activity {
    
    private int OVERLAY_PERMISSION_REQ_CODE=4356;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                showDialogOverLat();
            }else{
                switchView();
            }
        }else{
            switchView();    
        }
    }
    
    private void showDialogOverLat(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("نیاز به دسترسی");
        dialog.setMessage("برای اجرای برنامه بایستی گزینه درسا را فعال نمایید \nگزینه زیر را تایید نموده تا از برنامه استفاده نمایید");
        dialog.setPositiveButton("انجام می دهم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        });
        dialog.setNegativeButton("بعدا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
  /*      dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });*/
        dialog.show();

       
    }
    

private void switchView(){
    int totalKids = FetchDb.getKidsCount(this);
    if (totalKids > 0) {//has a kid
        boolean runInBackground=false;
        boolean callAnswer=false;
        try {
            G.selectedKid.joKid = FetchDb.getSelectedKid(this);
            JSONObject jo = new JSONObject(G.selectedKid.joKid.getString("settings"));
            callAnswer=jo.getBoolean("call");
            if (jo.getString("backgroundRun").equals("true")) {
                runInBackground = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //kid is activated
        if(runInBackground){//run app in background
            Intent intent=new Intent(App.getContext(), Service_RunInBackground.class);
            intent.putExtra("call", "" + !callAnswer);
            App.getContext().startService(intent);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("برنامه در پس زمینه");
            dialog.setMessage("برنامه در پس زمینه مراقب کودک شما می باشد برای خروج از این حالت به درسا تنظیمات مراجعه نمایید");
            dialog.setPositiveButton("متوجه شدم", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    finish();
                }
            });
            dialog.show();
//                    finish();
        }else{//run launcher
            G.Watchdog.launcher=this;
            App.getContext().startService(new Intent(App.getContext(), Service_Launcher.class));
        }


    } else {//start add kid
        startActivity(new Intent(this, Activity_Parent.class));
        finish();
    }
}
  
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(G.LOG_TAG,"cad draw :"+Settings.canDrawOverlays(getApplicationContext()));
                if (!Settings.canDrawOverlays(this)) {
                    showDialogOverLat();
                }else{
                    switchView();
                }
            }
        }
    }

}
