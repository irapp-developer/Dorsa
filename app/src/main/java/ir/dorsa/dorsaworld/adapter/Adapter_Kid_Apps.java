package ir.dorsa.dorsaworld.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.dialog.Dialog_edit_app;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

public class Adapter_Kid_Apps extends RecyclerView.Adapter<Adapter_Kid_Apps.ViewHolder> {
    private Activity mContext;
    private TextView hint;
    public Adapter_Kid_Apps(Activity c,TextView mHint) {
        mContext = c;
        hint=mHint;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View convertView;

        ImageView AppImage;
        ImageView wifiIcon;
        ImageView moreIcon;
        TextView AppName;
        CardView cardRow;
        int selectedNumberMode = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            AppImage=(ImageView)itemView.findViewById(R.id.grid_kidd_app_img);
            wifiIcon=(ImageView)itemView.findViewById(R.id.grid_kidd_app_wifi);
            moreIcon=(ImageView)itemView.findViewById(R.id.grid_kidd_app_more);
            AppName=(TextView)itemView.findViewById(R.id.grid_kidd_app_name);
            cardRow=(CardView)itemView.findViewById(R.id.grid_kidd_app_cardView);
            convertView = itemView;

        }
    }

    @Override
    public int getItemCount() {
        if (hint != null) {
            if (G.kids_app.KidAppResolver.size() == 0) {
                hint.setVisibility(View.VISIBLE);
            } else {
                hint.setVisibility(View.GONE);
            }
        }
        return G.kids_app.KidAppResolver.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.row_kids_app, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

    /*holder.AppImage.setImageDrawable(G.kids_app.KidAppResolver.get(position).loadIcon(mContext.getPackageManager()));
        holder.AppName.setText(G.kids_app.KidAppResolver.get(position).loadLabel(mContext.getPackageManager()));
        holder.convertView.setOnClickListener(new cellClick(G.kids_app.KidAppResolver.get(position), position));*/
       

        try {
            
            JSONObject jo=G.kids_app.KidAppResolver.get(position);
            final String packageName=jo.getString("packageName");
            final String className=jo.getString("className");
            final String AccessInternet=jo.getString("AccessInternet");

            ComponentName componentName=new ComponentName(packageName,className);
            PackageManager pkgManager=mContext.getPackageManager();
            
            ActivityInfo app =mContext.getPackageManager().getActivityInfo(componentName,0);
            final Drawable icon = app.loadIcon(pkgManager);
            final String name = app.loadLabel(pkgManager).toString();
            
            holder.AppName.setText(name);
            holder.AppImage.setImageDrawable(icon);
            

            //------------ get Access internet ------------------------
            boolean needAccessIntenet=false;
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
            String internetMode="noPer";
            if(needAccessIntenet){
                if(AccessInternet.equals("true")){//user alow to access intent
                    holder.wifiIcon.setImageResource(R.drawable.icon_wifi_on);
                    internetMode="on";
                }else{//user doesnt alow
                    holder.wifiIcon.setImageResource(R.drawable.icon_wifi_off);
                    internetMode="off";
                }
            }else{
                holder.wifiIcon.setVisibility(View.INVISIBLE);
            }

            Log.d(G.LOG_TAG, "kidd app pkg:" + packageName);
            if("ir.dorsa.dorsagard".equals(packageName)){
             final   PopupMenu popup = new PopupMenu(mContext, holder.moreIcon);
                //Inflating the Popup using xml file  
                popup.getMenuInflater().inflate(R.menu.menu_cell_kid_apps, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menue_remove) {
                            final AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
                            mDialog.setTitle("حذف برنامه");
                            mDialog.setMessage("مایل به حذف برنامه " + name + " می باشید ؟");
                            mDialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    try {
                                        FetchDb.removeKidApp(mContext, G.selectedKid.joKid.getString("ID"), packageName, className);
                                        boolean isDorsaApp = false;
                                        for (JSONObject jo : G.DorsaApps.listDataBackup) {
                                            Log.d(G.LOG_TAG, "PGNAme my app is:" + packageName);
                                            Log.d(G.LOG_TAG, "PGNAme dorsa app is:" + jo.getString("packageName"));

                                            if (jo.getString("packageName").equals(packageName)) {
                                                isDorsaApp = true;
                                                break;
                                            }
                                        }
                                        if (!isDorsaApp) {
                                            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                                                if (ri.activityInfo.applicationInfo.packageName.equals(packageName)) {
                                                    G.kids_app.AllAppResolver.add(0, ri);
                                                    break;
                                                }
                                            }
                                        }
                                        G.kids_app.KidAppResolver.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, G.kids_app.KidAppResolver.size());

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            mDialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });
                            mDialog.show();

                        } else if (item.getItemId() == R.id.menu_settings) {
                            try {
                                Func.dialogPermissions(mContext, holder.convertView, name, G.selectedKid.joKid.getString("ID"), packageName, position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                });
                holder.moreIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popup.show();
                    }
                });
                holder.convertView.setOnClickListener(new dorsaAppClick(holder.convertView,name,packageName,className,position));
                
                
            }else{
                holder.moreIcon.setOnClickListener(new cellClick(icon,name, packageName, internetMode, holder.wifiIcon, className, position));
                holder.convertView.setOnClickListener(new cellClick(icon,name, packageName, internetMode, holder.wifiIcon, className, position));     
            }
           
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        Drawable d = mContext.getPackageManager().getla
    }
    
    class dorsaAppClick implements View.OnClickListener{
        private String appName;
        private String packageName;
        private int position;
        private String mClassName;
        View convertView;
        public dorsaAppClick(View mView,String appName,String packageName,String className, int pos) {
            this.position = pos;
            this.appName = appName;
            this.packageName = packageName;
            this.mClassName=className;
            convertView=mView;
        }
        @Override
        public void onClick(View view) {
            ArrayAdapter<CharSequence> itemsAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.select_dialog_item, new String[]{"حذف برنامه","ویرایش دسترسی ها"});
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setAdapter(itemsAdapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(which==0){
                        final AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
                        mDialog.setTitle("حذف برنامه");
                        mDialog.setMessage("مایل به حذف برنامه " + appName + " می باشید ؟");
                        mDialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                    FetchDb.removeKidApp(mContext, G.selectedKid.joKid.getString("ID"), packageName, mClassName);
                                    boolean isDorsaApp = false;
                                    for (JSONObject jo : G.DorsaApps.listDataBackup) {
                                        Log.d(G.LOG_TAG, "PGNAme my app is:" + packageName);
                                        Log.d(G.LOG_TAG, "PGNAme dorsa app is:" + jo.getString("packageName"));

                                        if (jo.getString("packageName").equals(packageName)) {
                                            isDorsaApp = true;
                                            break;
                                        }
                                    }
                                    if (!isDorsaApp) {
                                        for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                                            if (ri.activityInfo.applicationInfo.packageName.equals(packageName)) {
                                                G.kids_app.AllAppResolver.add(0, ri);
                                                break;
                                            }
                                        }
                                    }
                                    G.kids_app.KidAppResolver.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, G.kids_app.KidAppResolver.size());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        mDialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });
                        mDialog.show();
                    }else if(which==1){
                        try {
                            Func.dialogPermissions(mContext, convertView, appName, G.selectedKid.joKid.getString("ID"), packageName, position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Dialog dialog = builder.create();
            dialog.show();
        }
    }

    class cellClick implements View.OnClickListener {

        private String appName;
        private String packageName;
        private int position;
        private String internetMode;
        private ImageView wifiIcon;
        private String mClassName;
        private Drawable mIcon;


        public cellClick(Drawable icon,String appName,String packageName,String internetMode,ImageView wifiIcon,String className, int pos) {
            this.position = pos;
            this.appName = appName;
            this.packageName = packageName;
            this.internetMode=internetMode;
            this.wifiIcon=wifiIcon;
            this.mClassName=className;
            this.mIcon=icon;
        }

        @Override
        public void onClick(final View v) {

            final Dialog_edit_app dialog_edit_app=new Dialog_edit_app(mContext);
            
            View.OnClickListener clickRemoveApp=new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        FetchDb.removeKidApp(mContext, G.selectedKid.joKid.getString("ID"), packageName,mClassName);
                        boolean isDorsaApp=false;
                        for(JSONObject jo:G.DorsaApps.listDataBackup){
                            Log.d(G.LOG_TAG,"PGNAme my app is:"+packageName);
                            Log.d(G.LOG_TAG,"PGNAme dorsa app is:"+jo.getString("packageName"));

                            if(jo.getString("packageName").equals(packageName)){
                                isDorsaApp=true;
                                break;
                            }
                        }
                        if(!isDorsaApp) {
                            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                                if (ri.activityInfo.applicationInfo.packageName.equals(packageName)) {
                                    G.kids_app.AllAppResolver.add(0, ri);
                                    break;
                                }
                            }
                        }
                        G.kids_app.KidAppResolver.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, G.kids_app.KidAppResolver.size());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog_edit_app.cancel();
                }
            };
            View.OnClickListener clickToggleInternet=null;
            String toggleInternetText="";
            
            if(internetMode.equals("noPer")){//no internet permission
            }else if(internetMode.equals("on")){//was Enable Internet Permision
                clickToggleInternet=new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        try {
                        FetchDb.UpdateInternetAccess(mContext, G.selectedKid.joKid.getString("ID"), packageName, "false");
                        
                        wifiIcon.setImageResource(R.drawable.icon_wifi_off);

                        JSONObject jo=G.kids_app.KidAppResolver.get(position);
                        jo.put("AccessInternet","false");
                        G.kids_app.KidAppResolver.set(position, jo);

                        v.setOnClickListener(new cellClick(mIcon,appName, packageName, "off", wifiIcon, mClassName,position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog_edit_app.cancel();
                    }
                };
                toggleInternetText="قطع دسترسی به اینترنت";
                
            }else{//was disable internet permision
                clickToggleInternet=new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        try {
                            FetchDb.UpdateInternetAccess(mContext, G.selectedKid.joKid.getString("ID"), packageName, "true");
                        wifiIcon.setImageResource(R.drawable.icon_wifi_on);

                        JSONObject jo=G.kids_app.KidAppResolver.get(position);
                        jo.put("AccessInternet", "true");
                        G.kids_app.KidAppResolver.set(position, jo);

                        v.setOnClickListener(new cellClick(mIcon,appName, packageName, "on", wifiIcon,mClassName, position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog_edit_app.cancel();
                    }
                };
                toggleInternetText="تایید دسترسی به اینترنت";
            }

            dialog_edit_app.setClickListners(toggleInternetText,clickToggleInternet,clickRemoveApp);
            dialog_edit_app.setAppDetails(appName,mIcon);
            dialog_edit_app.show();
            
        }
    }


}
