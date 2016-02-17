package dorsa.psb.com.dorsa.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.G;

public class Adapter_Kid_Apps extends RecyclerView.Adapter<Adapter_Kid_Apps.ViewHolder> {
    private Activity mContext;

    public Adapter_Kid_Apps(Activity c) {
        mContext = c;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        View convertView;

        ImageView AppImage;
        TextView AppName;
        int selectedNumberMode = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            AppImage=(ImageView)itemView.findViewById(R.id.grid_kidd_app_img);
            AppName=(TextView)itemView.findViewById(R.id.grid_kidd_app_name);
            convertView = itemView;

        }
    }

    @Override
    public int getItemCount() {
        return G.kids_app.KidAppResolver.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.grid_kids_app, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

    /*holder.AppImage.setImageDrawable(G.kids_app.KidAppResolver.get(position).loadIcon(mContext.getPackageManager()));
        holder.AppName.setText(G.kids_app.KidAppResolver.get(position).loadLabel(mContext.getPackageManager()));
        holder.convertView.setOnClickListener(new cellClick(G.kids_app.KidAppResolver.get(position), position));*/
       

        try {
            String packageName=G.kids_app.KidAppResolver.get(position);
            ApplicationInfo app = mContext.getPackageManager().getApplicationInfo(packageName, 0);
            Log.d(G.LOG_TAG,"package name is :"+packageName);
            Drawable icon = mContext.getPackageManager().getApplicationIcon(app);
            String name = mContext.getPackageManager().getApplicationLabel(app).toString();
            holder.AppName.setText(name);
            holder.AppImage.setImageDrawable(icon);
            holder.convertView.setOnClickListener(new cellClick(name,G.kids_app.KidAppResolver.get(position), position));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        
        

//        Drawable d = mContext.getPackageManager().getla
    }

    class cellClick implements View.OnClickListener {

        private String appName;
        private String packageName;
        private int position;


        public cellClick(String appName,String packageName, int pos) {
            this.position = pos;
            this.appName = appName;
            this.packageName = packageName;
        }

        @Override
        public void onClick(View v) {
            final  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("حذف برنامه");
            dialog.setMessage("مایل به حذف برنامه "+appName+" می باشید ؟");
            dialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    try {
                        FetchDb.removeKidApp(mContext,G.selectedKid.joKid.getString("ID"),packageName);
                        for (ResolveInfo ri :  G.kids_app.AllSystemApps) {
                            if(ri.activityInfo.applicationInfo.packageName.equals(packageName)){
                                G.kids_app.AllAppResolver.add(0,ri);
                                break;
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
            dialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
            dialog.show();
        }
    }


}
