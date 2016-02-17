package dorsa.psb.com.dorsa.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;
import dorsa.psb.com.dorsa.other.Permisions;

public class Adapter_Launcher_Apps extends RecyclerView.Adapter<Adapter_Launcher_Apps.ViewHolder> {
    private Context mContext;

    public Adapter_Launcher_Apps(Context c) {
        mContext = c;
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
        return G.kids_app.KidAppResolver.size();
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

        int radius = Func.dpToPx(mContext, 10);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(radius);
        shape.setColor(Color.parseColor("#3baed9"));
        holder.mainRel.setBackgroundDrawable(shape);

        try {
            String packageName = G.kids_app.KidAppResolver.get(position);
            ApplicationInfo app = mContext.getPackageManager().getApplicationInfo(packageName, 0);

            Drawable icon = mContext.getPackageManager().getApplicationIcon(app);
            String name = mContext.getPackageManager().getApplicationLabel(app).toString();

            holder.AppName.setText(name);
            holder.AppImage.setImageDrawable(icon);
            holder.convertView.setOnClickListener(new cellClick(name, G.kids_app.KidAppResolver.get(position), position));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Animation rotate = AnimationUtils.loadAnimation(mContext, R.anim.rotate_list_item);
        holder.AppImage.startAnimation(rotate);

//        Drawable d = mContext.getPackageManager().getla
    }

    class cellClick implements View.OnClickListener {

        private String packageName;

        public cellClick(String appName, String packageName, int pos) {
       
            this.packageName = packageName;
        }

        @Override
        public void onClick(View v) {
            try {
                Permisions.setPermissionApps(mContext,new JSONObject(G.selectedKid.joKid.getString("settings")));
                PackageManager pm = mContext.getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
                mContext.startActivity(launchIntent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
           
        }
    }


}
