package dorsa.psb.com.dorsa;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dorsa.psb.com.dorsa.adapter.Adapter_Launcher_Apps;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;
import dorsa.psb.com.dorsa.other.Permisions;
import dorsa.psb.com.dorsa.service.Service_Launcher;

public class launcher extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.launcher_layout);
        getSupportActionBar().hide();
        
       


        if(G.Intro.isFromApp){
           G.Intro.isFromApp=false;
           finish();
       }else{
            int totalKids=FetchDb.getKidsCount(this);
            
            if(totalKids>0){//check selected kid
                boolean isAppActive=true;
                try {
                    G.selectedKid.joKid = FetchDb.getSelectedKid(this);
                    JSONObject jo=new JSONObject(G.selectedKid.joKid.getString("settings"));
                    if(jo.getString("isEnable").equals("false")){
                        isAppActive=false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(!isAppActive) {//kid is not activated
                    Permisions.setParrentPermisions(this);
                    finish();
                }else{//kid is activated

                    try {
                        G.selectedKid.joKid = FetchDb.getSelectedKid(this);
                        Permisions.setPermissionLauncher(this,new JSONObject(G.selectedKid.joKid.getString("settings")));
//               getApplicationContext().stopService(new Intent(getApplicationContext(), Service_Launcher.class));
                        startService(new Intent(this, Service_Launcher.class));
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else{//start add kid
                startActivity(new Intent(this,Activity_Parent.class));
                finish();
            }
            
            
        }
        
        
        
     
    }

    void setupViews() {
        G.selectedKid.joKid = FetchDb.getSelectedKid(this);
        //--------- setup launcher design -------------------
        RelativeLayout REL_parent=(RelativeLayout)findViewById(R.id.act_launcher_parentRel);
        ImageView IMG_kidAvatar=(ImageView)findViewById(R.id.act_launcher_kid_name);
        TextView IMG_kidName=(TextView)findViewById(R.id.act_launcher_kid_avatar);


        try {
            Bitmap bgrImage= Func.getImage("bgr_kid_"+G.selectedKid.joKid.getString("ID"));
            Bitmap kidImage=Func.getImage("Avatar_"+G.selectedKid.joKid.getString("ID"));
            if(bgrImage!=null)REL_parent.setBackgroundDrawable(new BitmapDrawable(getResources(), bgrImage));
            if(kidImage!=null)IMG_kidAvatar.setImageBitmap(kidImage);
            IMG_kidName.setText(G.selectedKid.joKid.getString("name"));
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //--------- setup kids apps -------------------------
        try {
          
            if (G.kids_app.AllSystemApps.size() == 0) {//if it was not loadded before
                G.kids_app.AllSystemApps = installedApps();
            }
            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                G.kids_app.AllAppResolver.add(ri);
            }
            JSONArray JArrKidApps = FetchDb.getKidApps(this, G.selectedKid.joKid.getString("ID"));
            if (G.kids_app.KidAppResolver.size() == 0) {// if it was not loaded before
                for (int i = 0; i < JArrKidApps.length(); i++) {
                    G.kids_app.KidAppResolver.add(JArrKidApps.getJSONObject(i).getString("packageName"));


                }
            }
            for (String pkgName : G.kids_app.KidAppResolver) {//if it was added before ,remove
                for (int i = 0; i < G.kids_app.AllAppResolver.size(); i++) {
                    if (G.kids_app.AllAppResolver.get(i).activityInfo.applicationInfo.packageName
                            .equals(pkgName)
                            ) {
                        G.kids_app.AllAppResolver.remove(i);
                        break;
                    }
                }
            }


            RecyclerView mainList = (RecyclerView) findViewById(R.id.act_launcher_mainRel);
            mainList.setAdapter(new Adapter_Launcher_Apps(this));
            StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mainList.setLayoutManager(mStaggeredLayoutManager);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private List<ResolveInfo> installedApps() {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return getPackageManager().queryIntentActivities(main_intent, 0);
    } 
    
    
}
