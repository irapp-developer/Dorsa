package ir.dorsa.dorsaworld.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.dialog.Dialog_internet_permission;
import ir.dorsa.dorsaworld.other.G;

public class Adapter_All_Apps_Frg extends RecyclerView.Adapter<Adapter_All_Apps_Frg.ViewHolder> {
    public static Context mContext;
    

    class a implements View.OnClickListener {

        @Override
        public void onClick(View view) {

        }
    }


    public Adapter_All_Apps_Frg(Context c) {
        mContext = c;
        

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View convertView;

        ImageView AppImage;
        TextView AppName;
        int selectedNumberMode = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            AppImage = (ImageView) itemView.findViewById(R.id.grid_kidd_app_img);
            AppName = (TextView) itemView.findViewById(R.id.grid_kidd_app_name);
            convertView = itemView;

        }
    }

    @Override
    public int getItemCount() {
        return G.kids_app.AllAppResolver.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.grid_kid_apps_circle, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ResolveInfo appResolver = G.kids_app.AllAppResolver.get(position);

        String appName = appResolver.loadLabel(mContext.getPackageManager()).toString();
        holder.AppImage.setImageDrawable(appResolver.loadIcon(mContext.getPackageManager()));
        holder.AppName.setText(appName);
        holder.convertView.setOnClickListener(new cellClick(appResolver, position));


    }

    class cellClick implements View.OnClickListener {

        private ResolveInfo appResove;
        private int position;


        public cellClick(ResolveInfo resolveInfo, int pos) {
            this.position = pos;
            this.appResove = resolveInfo;
        }

        @Override
        public void onClick(View v) {
            String pkgName = "-1";
            boolean needAccessIntenet = false;
            try {

                pkgName = appResove.activityInfo.packageName;
                Log.d(G.LOG_TAG, "Package name 1 is :" + appResove.activityInfo.name);

                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(appResove.activityInfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (String permissionName : requestedPermissions) {
                        if (permissionName.toLowerCase().contains("internet")) {
                            needAccessIntenet = true;
                            break;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (needAccessIntenet && !"ir.dorsa.dorsagard".equals(pkgName)) {

                final Dialog_internet_permission mDialog_internet_permission=new Dialog_internet_permission(mContext);
                
                View.OnClickListener clickYes=new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addKidApp(appResove.activityInfo.applicationInfo.packageName, "true", appResove.activityInfo.name, position);
                        mDialog_internet_permission.cancel();
                    }
                };
                
                View.OnClickListener clickNo=new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addKidApp(appResove.activityInfo.applicationInfo.packageName, "false", appResove.activityInfo.name, position);
                        mDialog_internet_permission.cancel();
                    }
                };

                mDialog_internet_permission.setBtnListner(clickYes,clickNo);
                if (!((Activity) mContext).isFinishing()) {
                    mDialog_internet_permission.show();
                } else {
                    Log.d(G.LOG_TAG, "close dialog");
                }

            } else if (!"ir.dorsa.dorsagard".equals(pkgName)) {// there is no internet Access
               
                    addKidApp(appResove.activityInfo.applicationInfo.packageName, "true", appResove.activityInfo.name, position);
              
            } else if ("ir.dorsa.dorsagard".equals(pkgName)) {
                dialogPermissions(appResove, position);
            }
        }
    }

    private void dialogPermissions(final ResolveInfo appResove, final int position) {
        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_permissions);

        TextView title = (TextView) mDialog.findViewById(R.id.dialog_permissions_txt_title);
        final CheckBox chBox_internet = (CheckBox) mDialog.findViewById(R.id.dialog_permissions_chbox_internet);
        final CheckBox chBox_addressBar = (CheckBox) mDialog.findViewById(R.id.dialog_permissions_chbox_addressbar);
        RelativeLayout rel_internet = (RelativeLayout) mDialog.findViewById(R.id.dialog_permissions_rel_internet);
        RelativeLayout rel_addressBar = (RelativeLayout) mDialog.findViewById(R.id.dialog_permissions_rel_addressbar);
        Button btn_save = (Button) mDialog.findViewById(R.id.dialog_ask_btn_yes);
        Button btn_cancel = (Button) mDialog.findViewById(R.id.dialog_ask_btn_no);

        //set shared prefrece if not exist
        Context dorsaAppContext = null;
        boolean isChecked = true;
        JSONObject joShPref = null;
        SharedPreferences AppPrf = null;
        try {
            dorsaAppContext = mContext.getApplicationContext().createPackageContext("ir.dorsa.dorsagard", Context.CONTEXT_IGNORE_SECURITY);
            AppPrf = dorsaAppContext.getSharedPreferences("com_adak_dorsagard_public", Context.MODE_MULTI_PROCESS);
            String pref = AppPrf.getString("public_sp", "-1");
            if ("-1".equals(pref)) {
                Log.d(G.LOG_TAG, "App prefrence not set yet");
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("اولین اجرا");
                dialog.setMessage("این برنامه برای افزوده شدن به لیست حداقل یکبار بایستی اجرا گردد٬مایل به اجرای برنامه می باشید ؟");
                dialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ComponentName componentName = new ComponentName(appResove.activityInfo.applicationInfo.packageName, appResove.activityInfo.name);
                        Intent intent = new Intent(Intent.ACTION_MAIN, null);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setComponent(componentName);
                        App.getContext().startActivity(intent);
                    }
                });
                dialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.show();
                return;
            } else {
                JSONArray ja = new JSONArray(pref);
                joShPref = ja.getJSONObject(0);
                isChecked = Boolean.parseBoolean(joShPref.getString("Default"));
                chBox_addressBar.setChecked(isChecked);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        chBox_internet.setChecked(true);
        btn_save.setText("افزودن");
        btn_cancel.setText("انصراف");

        title.setText("تنظیمات برنامه " + appResove.activityInfo.name);

        rel_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chBox_internet.setChecked(!chBox_internet.isChecked());
            }
        });

        final JSONObject finalJo = joShPref;
        final SharedPreferences finalAppPrf = AppPrf;
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               
                    addKidApp(appResove.activityInfo.applicationInfo.packageName, "" + chBox_internet.isChecked(), appResove.activityInfo.name, position);

                    try {
                        finalJo.put("Default", "" + chBox_addressBar.isChecked());
                        JSONArray ja = new JSONArray();
                        ja.put(finalJo);
                        SharedPreferences.Editor finalSharedPrefereceEditor = finalAppPrf.edit();
                        finalSharedPrefereceEditor.putString("public_sp", ja.toString());
                        finalSharedPrefereceEditor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
               
                mDialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.cancel();
            }
        });


        mDialog.show();
    }


    private void addKidApp(String packageName, String AccessIntent, String className, int position) {

//        FetchDb.saveKidsApp(mContext, kidId, packageName, AccessIntent, className);
        G.kids_app.AllAppResolver.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, G.kids_app.AllAppResolver.size());

        JSONObject jo = new JSONObject();
        try {
            jo.put("packageName", packageName);
            jo.put("AccessInternet", AccessIntent);
            jo.put("className", className);
            G.addChild.kidApps.add(jo);
            Toast.makeText(mContext, "اضافه گردید", Toast.LENGTH_SHORT).show();
            
//            G.kids_app.KidAppResolver.add(jo);
//            G.kids_app.Adapter_kids_app.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
