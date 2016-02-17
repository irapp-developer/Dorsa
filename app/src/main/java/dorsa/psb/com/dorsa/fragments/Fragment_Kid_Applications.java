package dorsa.psb.com.dorsa.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.adapter.Adapter_Kid_Apps;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;

/**
 * Created by mehdi on 1/25/16 AD.
 */
public class Fragment_Kid_Applications extends Fragment {
    static View pView;
    static Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_kid_applications, container, false);
        mActivity = getActivity();
        setupViews();


        return pView;
    }


    public static void setupViews() {

        try {
            if (G.kids_app.AllSystemApps.size() == 0) {//if it was not loadded before
                G.kids_app.AllSystemApps = installedApps();
            }
            
            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                G.kids_app.AllAppResolver.add(ri);
            }
            JSONArray JArrKidApps = FetchDb.getKidApps(mActivity, G.selectedKid.joKid.getString("ID"));
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

            RecyclerView mainList = (RecyclerView) pView.findViewById(R.id.frg_kid_app_mailList);
            G.kids_app.Adapter_kids_app = new Adapter_Kid_Apps(mActivity);
            mainList.setAdapter(G.kids_app.Adapter_kids_app);
            StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
            mainList.setLayoutManager(mStaggeredLayoutManager);

            FloatingActionButton fab = (FloatingActionButton) pView.findViewById(R.id.frg_kid_app_fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Func.DialogSelectApp(mActivity);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static List<ResolveInfo> installedApps() {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mActivity.getPackageManager().queryIntentActivities(main_intent, 0);
    }
}
