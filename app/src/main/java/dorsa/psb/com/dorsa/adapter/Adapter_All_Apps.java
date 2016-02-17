package dorsa.psb.com.dorsa.adapter;

import android.app.Activity;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.G;

public class Adapter_All_Apps extends RecyclerView.Adapter<Adapter_All_Apps.ViewHolder> {
    private Activity mContext;

    public Adapter_All_Apps(Activity c) {
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
        View contactView = inflater.inflate(R.layout.grid_kids_app, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String appName=G.kids_app.AllAppResolver.get(position).loadLabel(mContext.getPackageManager()).toString();
        holder.AppImage.setImageDrawable(G.kids_app.AllAppResolver.get(position).loadIcon(mContext.getPackageManager()));
        holder.AppName.setText(appName);
        holder.convertView.setOnClickListener(new cellClick(G.kids_app.AllAppResolver.get(position), position));
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
            /*boolean isAddedBefor = false;
            for (String ResInfoKid : G.kids_app.KidAppResolver) {
                if (ResInfoKid.equals(appResove.loadLabel(mContext.getPackageManager()))) {
                    isAddedBefor = true;
                    break;
                }
            }*/
               

            try {
                FetchDb.saveKidsApp(mContext,G.selectedKid.joKid.getString("ID"),appResove.activityInfo.applicationInfo.packageName);

                G.kids_app.AllAppResolver.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, G.kids_app.AllAppResolver.size());

                G.kids_app.KidAppResolver.add(appResove.activityInfo.applicationInfo.packageName);
                G.kids_app.Adapter_kids_app.notifyDataSetChanged();
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
