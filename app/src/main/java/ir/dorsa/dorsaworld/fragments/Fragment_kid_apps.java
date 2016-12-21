package ir.dorsa.dorsaworld.fragments;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.adapter.Adapter_All_Apps_Frg;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 1/21/16 AD.
 */
public class Fragment_kid_apps extends Fragment {

    private View pView;

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_add_kid_apps, container, false);

        RecyclerView mainList = (RecyclerView) pView.findViewById(R.id.frg_add_kid_app_mainList);
        ImageView btnFinish = (ImageView) pView.findViewById(R.id.frg_add_kid_app_submit);
        ImageView btnBack = (ImageView) pView.findViewById(R.id.frg_add_kid_app_back);


        Func.getApps(getActivity());
        mainList.setAdapter(new Adapter_All_Apps_Frg(getActivity()));

        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mainList.setLayoutManager(mStaggeredLayoutManager);


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject kidJson = new JSONObject();
                try {
                    kidJson.put("name", G.addChild.name);
                    kidJson.put("birthday", G.addChild.birthday);
                    kidJson.put("sex", "" + G.kid_details.sexMode);

                    if (!G.addChild.isEditedMode) {
                        JSONObject joSettings = new JSONObject();
                        joSettings.put("isSchedualActive", "false");
                        joSettings.put("schedualDay", "-1");
                        joSettings.put("startH", "-1");
                        joSettings.put("startM", "-1");
                        joSettings.put("endH", "-1");
                        joSettings.put("endM", "-1");
                        joSettings.put("backgroundRun", "false");
                        joSettings.put("AppName", "درسا");
                        joSettings.put("maxSound", "100");
                        joSettings.put("call", "false");
                        joSettings.put("lockVolume", "false");
                        joSettings.put("restart", "false");
                        joSettings.put("statusBar", "false");
                        joSettings.put("screen", "false");
                        joSettings.put("bgr", "bgr_kid_drawable_" + R.drawable.bgr_default);
                        kidJson.put("settings", joSettings.toString());
                        G.addChild.saveID = FetchDb.AddKid(getActivity(), kidJson.toString());

                    } else {
                        G.addChild.saveID = G.selectedKid.joKid.getString("ID");
                        FetchDb.editKid(getActivity(), G.addChild.saveID, kidJson.toString());
                    }
                    if (G.addChild.avatarBitmap == null)
                        G.addChild.avatarBitmap = ((BitmapDrawable) G.kid_details.kidsImg.getDrawable()).getBitmap();
                    Func.saveImage(G.addChild.avatarBitmap, "Avatar_" + G.addChild.saveID);

                    saveKidsApps();

                    G.addChild.isEditedMode = false;
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G.mViewPager.setCurrentItem(1);
            }
        });


        return pView;
    }


    private void saveKidsApps() {
        for (JSONObject jo : G.addChild.kidApps) {
            try {
                String packageName = jo.getString("packageName");
                String AccessInternet = jo.getString("AccessInternet");
                String className = jo.getString("className");

                FetchDb.saveKidsApp(getContext(), G.addChild.saveID, packageName, AccessInternet, className);

                G.kids_app.KidAppResolver.add(jo);
                if (G.kids_app.Adapter_kids_app != null)
                    G.kids_app.Adapter_kids_app.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
