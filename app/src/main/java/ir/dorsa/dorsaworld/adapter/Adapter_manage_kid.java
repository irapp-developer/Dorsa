package ir.dorsa.dorsaworld.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.Activity_Add_Child;
import ir.dorsa.dorsaworld.Activity_Parent;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

public class Adapter_manage_kid extends RecyclerView.Adapter<Adapter_manage_kid.ViewHolder> {
    private Activity mContext;
    private Dialog mDialod;

    public Adapter_manage_kid(Activity c, Dialog dialog) {
        mContext = c;
        mDialod = dialog;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        View convertView;

        ImageView kidAvetar;
        ImageView kidAvetar_Stroke;
        
        TextView  kidName;
        int selectedNumberMode = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            kidAvetar = (ImageView) itemView.findViewById(R.id.row_kid_avetar);
            kidAvetar_Stroke = (ImageView) itemView.findViewById(R.id.row_kid_avetar_stroke);
            kidName=(TextView)itemView.findViewById(R.id.row_kid_name);
            convertView = itemView;

        }
    }

    @Override
    public int getItemCount() {
        return G.chooseKid.kidsDetails.size() + 1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.row_manage_kid, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == 0) {
            holder.kidAvetar.setImageResource(R.drawable.icon_add_blue);
            holder.kidName.setText("جدید");
            holder.kidAvetar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialod.cancel();
                    mContext.startActivityForResult(new Intent(mContext, Activity_Add_Child.class), G.REQUEST_CHILD);
                }
            });
        } else {
            final JSONObject JoKid=G.chooseKid.kidsDetails.get(position-1);
            try {
                holder.kidName.setText(JoKid.getString("name"));
                holder.kidAvetar.setImageBitmap(Func.getImage("Avatar_" + JoKid.getString("ID")));
                if(JoKid.getString("isSelected").equals("true")){
                    holder.kidAvetar_Stroke.setVisibility(View.VISIBLE);
                    Log.d(G.LOG_TAG,"kid name is :"+JoKid.getString("name"));
                }else{
                    holder.kidAvetar_Stroke.setVisibility(View.INVISIBLE);
                }
                
                holder.convertView.setOnClickListener(new cellClick(position-1));
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    
    
    

    class cellClick implements View.OnClickListener {

        
        private int position;


        public cellClick( int pos) {
            this.position = pos;
        }

        @Override
        public void onClick(View v) {
            ArrayAdapter<CharSequence> itemsAdapter = new ArrayAdapter<CharSequence>(mContext, android.R.layout.select_dialog_item, new String[]{"انتخاب","ویرایش", "حذف"});
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setAdapter(itemsAdapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            try {
                                JSONObject JoKid = G.chooseKid.kidsDetails.get(position);
                                FetchDb.setSelctedKid(mContext, JoKid.getString("ID"));
                                Activity_Parent.notifyParentPage();
                                mDialod.cancel();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            G.addChild.isEditedMode=true;
                            mContext.startActivityForResult(new Intent(mContext, Activity_Add_Child.class), G.REQUEST_CHILD);
                            mDialod.cancel();
                            break;
                        case 2:
                            JSONObject JoKid = G.chooseKid.kidsDetails.get(position);
                            final AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
                            mDialog.setTitle("حذف کودک");
                            try {
                                mDialog.setMessage("برای حذف " + JoKid.getString("name") + " مطمئن هستید ؟");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mDialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (G.chooseKid.kidsDetails.size() > 1) {//remove selected item
                                        JSONObject JoKid = G.chooseKid.kidsDetails.get(position);
                                        try {
                                            if(JoKid.getString("isSelected").equals("true")) {//selected item is default kid
                                                FetchDb.removeKid(mContext, JoKid.getString("ID"));
                                                Func.removeImage("Avatar_" + JoKid.getString("ID"));//remove kid avetar
                                                Func.removeImage("bgr_kid_" + JoKid.getString("ID"));//remove kid bgr
                                                Func.removeImage("bgr_kid_thumb_"+JoKid.getString("ID"));//remove kid thumb bgr
                                                G.chooseKid.kidsDetails.remove(position);//remove selected kid
                                                JoKid = G.chooseKid.kidsDetails.get(0);//set first selected
                                                FetchDb.setSelctedKid(mContext, JoKid.getString("ID"));
                                            }else{
                                                Func.removeImage("Avatar_" + JoKid.getString("ID"));//remove kid avetar
                                                Func.removeImage("bgr_kid_" + JoKid.getString("ID"));//remove kid bgr
                                                Func.removeImage("bgr_kid_thumb_"+JoKid.getString("ID"));//remove kid thumb bgr
                                                FetchDb.removeKid(mContext, JoKid.getString("ID"));
                                                G.chooseKid.kidsDetails.remove(position);//remove selected kid
                                                
                                            }
                                            notifyDataSetChanged();//notify new kid
                                            Activity_Parent.notifyParentPage();//notify page
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {//start add new kid
                                        try {
                                            JSONObject JoKid = G.chooseKid.kidsDetails.get(position);
                                            FetchDb.removeKid(mContext, JoKid.getString("ID"));
                                            G.chooseKid.kidsDetails.remove(position);
                                            mDialod.cancel();
                                            notifyDataSetChanged();//notify new kid
                                            Activity_Parent.notifyParentPage();//notify page
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                }
                            });
                            mDialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });
                            mDialog.show();
                            break;
                    }
                }

            });

            Dialog dialog = builder.create();
            dialog.show();
        }
    }


}
