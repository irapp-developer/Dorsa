package ir.dorsa.dorsaworld.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.Permisions;

public class Broadcast_Reset extends BroadcastReceiver {
    public Broadcast_Reset() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int totalKids = FetchDb.getKidsCount(context);
        if (totalKids > 0) {//has a kid
            boolean runInReset=false;
            boolean runInBackground=false;
            try {
                G.selectedKid.joKid = FetchDb.getSelectedKid(context);
                JSONObject jo = new JSONObject(G.selectedKid.joKid.getString("settings"));
              
                if(jo.getString("restart").equals("true")){
                    runInReset=true;
                }
                
                if (jo.getString("backgroundRun").equals("true")) {
                    runInBackground = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!runInReset) {//kid is not activated
                Permisions.setDisablePermisions(context);
                
            } else {//kid is activated
                if(runInBackground){//run app in background
                    App.getContext().startService(new Intent(App.getContext(), Service_RunInBackground.class));
                }else{//run launcher
                    App.getContext().startService(new Intent(App.getContext(), Service_Launcher.class));
                }

             
            }
        }
      
    }
}
