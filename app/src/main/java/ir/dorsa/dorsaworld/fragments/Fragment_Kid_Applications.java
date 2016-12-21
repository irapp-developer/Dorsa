package ir.dorsa.dorsaworld.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.adapter.Adapter_Kid_Apps;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 1/25/16 AD.
 */
public class Fragment_Kid_Applications extends Fragment {
    static View pView;
    static Activity mActivity;
    TextView hint;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_kid_applications, container, false);
        mActivity = getActivity();
        
//        getApps();
//        setupViews();
        ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("لطفا صبور باشید");
        new ProgressAsyncTaskSchedual(getActivity(),mDialog).execute();

        return pView;
    }
    private  class ProgressAsyncTaskSchedual<Params> extends AsyncTask<Params, Integer, Boolean> {

        protected Activity mActivity;
        protected ProgressDialog progressDialog;


        public ProgressAsyncTaskSchedual(Activity activity, ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
            this.mActivity = activity;
        }

        @Override
        protected Boolean doInBackground(Params... paramses) {
            getApps();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupViews();
                }
            });
            
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... changed) {
            if (progressDialog != null)
                progressDialog.setProgress(changed[0]);

        }

        @Override
        protected void onPreExecute() {
            if (progressDialog != null)
                try {
                    progressDialog.show();
                } catch (Exception ex) {

                }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

        }
    }

    public  void setupViews() {
            RecyclerView mainList = (RecyclerView) pView.findViewById(R.id.frg_kid_app_mailList);
            hint=(TextView) pView.findViewById(R.id.frg_kid_apps_hint);
            
      
            G.kids_app.Adapter_kids_app = new Adapter_Kid_Apps(Fragment_Kid_Applications.this.getActivity(),hint);
            mainList.setAdapter(G.kids_app.Adapter_kids_app);
//        RecyclerView.LayoutManager mLayoutManage=new RecyclerView.LayoutManager(); 
        
            StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mainList.setLayoutManager(mStaggeredLayoutManager);


            
            fab= (FloatingActionButton) pView.findViewById(R.id.frg_kid_app_fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Func.DialogSelectApp(getActivity(),G.selectedKid.joKid.getString("sex"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        
        try {
            if("0".equals(G.selectedKid.joKid.getString("sex"))){
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.Color_blue)));
                hint.setTextColor(ContextCompat.getColor(getContext(), R.color.Color_blue));
            }else{
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.Color_red_girl)));
                hint.setTextColor(ContextCompat.getColor(getContext(), R.color.Color_red_girl));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void refreShApps(){
     
        try {
            if("0".equals(G.selectedKid.joKid.getString("sex"))){
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.Color_blue)));
                hint.setTextColor(ContextCompat.getColor(getContext(), R.color.Color_blue));
            }else{
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.Color_red_girl)));
                hint.setTextColor(ContextCompat.getColor(getContext(), R.color.Color_red_girl));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        try{
            ProgressDialog mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("لطفا صبور باشید");
            new ProgressAsyncTaskSchedual(getActivity(), mDialog).execute();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void getApps(){
        try {
            if (G.kids_app.AllSystemApps.size() == 0) {//if it was not loadded before
                G.kids_app.AllSystemApps = installedApps();
            }

            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                String PkgName=ri.activityInfo.applicationInfo.packageName;
                if(!PkgName.equals(App.getContext().getPackageName())
                        & !PkgName.equals("com.android.dialer")
                        ){
                    G.kids_app.AllAppResolver.add(ri);
                }
                if(PkgName.equals("com.android.dialer") ){
//                    isDialerAdded=true;
                    G.kids_app.AllAppResolver.add(ri);
                }

            }
            JSONArray JArrKidApps = FetchDb.getKidApps(mActivity, G.selectedKid.joKid.getString("ID"));
            if (G.kids_app.KidAppResolver.size() == 0) {// if it was not loaded before
                for (int i = 0; i < JArrKidApps.length(); i++) {
                    G.kids_app.KidAppResolver.add(JArrKidApps.getJSONObject(i));
                }
            }

            Log.d(G.LOG_TAG,"PKG APP :"+App.getContext().getPackageName());
            for (JSONObject jo : G.kids_app.KidAppResolver) {//if it was added before ,remove
                String pkgName=jo.getString("packageName");
                for (int i = 0; i < G.kids_app.AllAppResolver.size(); i++) {
                    String systemPackageName=G.kids_app.AllAppResolver.get(i).activityInfo.applicationInfo.packageName;
                    Log.d(G.LOG_TAG,"PKG system :"+systemPackageName);
                    if (systemPackageName.equals(pkgName)) {
                        G.kids_app.AllAppResolver.remove(i);
                        break;
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private  List<ResolveInfo> installedApps() {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mActivity.getPackageManager().queryIntentActivities(main_intent, 0);
    }
}
