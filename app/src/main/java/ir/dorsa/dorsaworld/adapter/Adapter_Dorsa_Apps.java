package ir.dorsa.dorsaworld.adapter;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;

public class Adapter_Dorsa_Apps extends RecyclerView.Adapter<Adapter_Dorsa_Apps.ViewHolder> {
    private Context mContext;
    private Dialog mDialog;

    public Adapter_Dorsa_Apps(Context c,Dialog dialog) {
        mContext = c;
        this.mDialog=dialog;
       
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View convertView;

        ImageView AppImage;
        TextView AppName;
        TextView download;
        ImageView wifiIcon;
        ImageView moreIcon;
        int selectedNumberMode = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            AppImage = (ImageView) itemView.findViewById(R.id.grid_kidd_app_img);
            wifiIcon=(ImageView)itemView.findViewById(R.id.grid_kidd_app_wifi);
            moreIcon=(ImageView)itemView.findViewById(R.id.grid_kidd_app_more);
            AppName = (TextView) itemView.findViewById(R.id.grid_kidd_app_name);
            download = (TextView) itemView.findViewById(R.id.grid_kidd_app_download);
            convertView = itemView;

        }
    }

    @Override
    public int getItemCount() {
        return G.DorsaApps.listData.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.grid_kids_dorsaapp, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            JSONObject appJson = G.DorsaApps.listData.get(position);

            if(appJson.getString("exist").equals("true")){//app exist use from package to load data
                ApplicationInfo app = mContext.getPackageManager().getApplicationInfo(appJson.getString("packageName"), 0);
                Drawable icon = mContext.getPackageManager().getApplicationIcon(app);
                String name = mContext.getPackageManager().getApplicationLabel(app).toString();
                holder.AppName.setText(name);
                holder.AppImage.setImageDrawable(icon);


                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setPackage(appJson.getString("packageName"));
                final ResolveInfo rInfo = mContext.getPackageManager().resolveActivity(intent, 0);
                
                String className=rInfo.activityInfo.name;
                
                //set add to kids app list
                if(!"ir.dorsa.dorsagard".equals(appJson.getString("packageName"))){
                    holder.convertView.setOnClickListener(new AddToListcellClick(appJson.getString("packageName"),position,className));
                }else{
                    holder.convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            
                    dialogPermissions(rInfo, position);
                        }
                    });
                }
//                holder.convertView.setOnClickListener(new AddToListcellClick(appJson.getString("packageName"),position,className));
                
            }else{//app not exit set from given data
                holder.download.setVisibility(View.VISIBLE);
                holder.AppName.setText(appJson.getString("AppName"));
                Picasso.with(mContext).load(appJson.getInt("icon")).into(holder.AppImage);
                // set onclick listner download form store
                holder.convertView.setOnClickListener(new OpenStroreClickListner(appJson.getString("url")));
            }
        }catch(JSONException ex){
            
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        
        holder.wifiIcon.setVisibility(View.INVISIBLE);
        holder.moreIcon.setVisibility(View.INVISIBLE);
     
    }
    
    
    class OpenStroreClickListner implements View.OnClickListener{

        private String url;
        public OpenStroreClickListner(String mUrl){
            this.url=mUrl;
        }
        @Override
        public void onClick(View v) {
            mDialog.cancel();
           
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            mContext.startActivity(i);
        }
    }

    class AddToListcellClick implements View.OnClickListener {

        private int position;
        private String packageName;
        private String mClassName;

        public AddToListcellClick(String pkg, int pos,String className) {
            this.position = pos;
            this.packageName=pkg;
            this.mClassName=className;
        }

        @Override
        public void onClick(View v) {
          boolean needAccessIntenet=false;
               try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            if(requestedPermissions!=null) {
                for (String permissionName : requestedPermissions) {
                    if(permissionName.toLowerCase().contains("internet")){
                        needAccessIntenet=true;
                        break;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
            if(needAccessIntenet){
                final  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("دسترسی به اینترنت");
                dialog.setMessage("این برنامه نیاز به دسترسی اینترنت دارد\nآیا اجازه دسترسی به اینترن برای این برنامه را می دهد؟");
                dialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            addKidApp(G.selectedKid.joKid.getString("ID"),packageName,"true",position,mClassName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                dialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            addKidApp(G.selectedKid.joKid.getString("ID"),packageName,"false",position,mClassName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }else{// there is no internet Access
                try {
                    addKidApp(G.selectedKid.joKid.getString("ID"),packageName,"true",position,mClassName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            
           
        }
    }

    private void addKidApp(String kidId,String packageName,String AccessIntent,int position,String className){
       
            FetchDb.saveKidsApp(mContext, kidId, packageName, AccessIntent,className);
            G.DorsaApps.listData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, G.DorsaApps.listData.size());

        JSONObject jo=new JSONObject();
        try {
            jo.put("packageName",packageName);
            jo.put("AccessInternet",AccessIntent);
            jo.put("className",className);

            G.kids_app.KidAppResolver.add(jo);
            G.kids_app.Adapter_kids_app.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
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
                try {
                    addKidApp(G.selectedKid.joKid.getString("ID"),appResove.activityInfo.applicationInfo.packageName,"true",position,appResove.activityInfo.name);

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
}
