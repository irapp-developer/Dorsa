package ir.dorsa.dorsaworld.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.Permisions;

public class Adapter_Launcher_Fragments_Apps extends RecyclerView.Adapter<Adapter_Launcher_Fragments_Apps.ViewHolder> {
    private Context mContext;
    private int fromIndex;
    private int toIndex;
    private int mHeightIcon;
    

    public Adapter_Launcher_Fragments_Apps(Context c,int heightIcon, int from, int to) {
        mContext = c;
        this.fromIndex=from;
        this.toIndex=to;
        this.mHeightIcon=heightIcon;
        
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        View convertView;

        RelativeLayout mainRel;
        ImageView AppImage;
        TextView AppName;
        int selectedNumberMode = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            AppImage = (ImageView) itemView.findViewById(R.id.row_launcher_app_icon);
            AppName = (TextView) itemView.findViewById(R.id.row_launcher_app_name);
            mainRel = (RelativeLayout) itemView.findViewById(R.id.row_launcher_mainRel);
            convertView = itemView;

        }
    }

    @Override
    public int getItemCount() {
        Log.d(G.LOG_TAG,"FRG total is "+((toIndex-fromIndex)+1));
        return (toIndex-fromIndex)+1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.row_launcher, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

    /*    int radius = Func.dpToPx(mContext, 10);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(radius);
        shape.setColor(Color.parseColor("#3baed9"));
        holder.mainRel.setBackgroundDrawable(shape);*/


      
        try {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height= mHeightIcon;
            holder.itemView.setLayoutParams(params);

            JSONObject jo = G.kids_app.KidAppResolver.get(position+fromIndex);
            String packageName = jo.getString("packageName");
            String className = jo.getString("className");

            ComponentName componentName=new ComponentName(packageName,className);
            PackageManager pkgManager=mContext.getPackageManager();

            ActivityInfo app =mContext.getPackageManager().getActivityInfo(componentName, 0);
            Drawable icon = app.loadIcon(pkgManager);
            String name = app.loadLabel(pkgManager).toString();
            
           
            holder.AppName.setText(name);
            holder.AppImage.setImageDrawable(icon);
            holder.convertView.setOnClickListener(new cellClick(className, packageName, position,jo.getString("AccessInternet")));
            
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*Animation rotate = AnimationUtils.loadAnimation(mContext, R.anim.rotate_list_item);
        holder.AppImage.startAnimation(rotate);*/

//        Drawable d = mContext.getPackageManager().getla
    }

    class cellClick implements View.OnClickListener {

        private String packageName;
        private String AccessInternet;
        private String mClassName;

        public cellClick(String className, String packageName, int pos, String accessIntenet) {
            this.AccessInternet = accessIntenet;
            this.packageName = packageName;
            this.mClassName=className;
        }

        @Override
        public void onClick(View v) {
            try {
                boolean isAllowToUseApp = Func.isDailyForAppEnable(App.getContext(), G.selectedKid.joKid.getString("ID"), packageName,mClassName);
                int isAllowToUseWeeklyApp = FetchDb.isSchedualWeekEnable(App.getContext(), G.selectedKid.joKid.getString("ID"), packageName, mClassName);
                if(isAllowToUseApp & isAllowToUseWeeklyApp != -1) {
                    Permisions.setPermissionApps(mContext, new JSONObject(G.selectedKid.joKid.getString("settings")), packageName,mClassName, AccessInternet);

                    ComponentName componentName=new ComponentName(packageName,mClassName);
                    Intent intent=new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(componentName);
                    mContext.startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
