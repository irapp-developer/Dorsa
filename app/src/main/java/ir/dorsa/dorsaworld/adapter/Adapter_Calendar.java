package ir.dorsa.dorsaworld.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by Morteza on 8/30/2015.
 */
public class Adapter_Calendar extends BaseAdapter {

    public Activity mContext;
    private TextView noDateSet;
    
    private TextView clear_text;
    private ImageView clear_img;
    private View clear_btn;

    public Adapter_Calendar(Activity c) {
        this.mContext = c;
        noDateSet=(TextView)mContext.findViewById(R.id.act_calendar_weeek_no_date_set);

          clear_text=(TextView)mContext.findViewById(R.id.act_calendar_weeek_reset_all_text);
          clear_img=(ImageView)mContext.findViewById(R.id.act_calendar_weeek_reset_all_icon);
          clear_btn=mContext.findViewById(R.id.act_calendar_weeek_reset_all);
        
        setNoDate();
    }

    @Override
    public int getCount() {
        return 48;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        setNoDate();
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_calendar, parent, false);
        
        TextView time = (TextView) convertView.findViewById(R.id.row_cal_time);

        ImageView dayIndex_0=(ImageView)convertView.findViewById(R.id.row_cal_dayIndex_0);
        ImageView dayIndex_1=(ImageView)convertView.findViewById(R.id.row_cal_dayIndex_1);
        ImageView dayIndex_2=(ImageView)convertView.findViewById(R.id.row_cal_dayIndex_2);
        ImageView dayIndex_3=(ImageView)convertView.findViewById(R.id.row_cal_dayIndex_3);
        ImageView dayIndex_4=(ImageView)convertView.findViewById(R.id.row_cal_dayIndex_4);
        ImageView dayIndex_5=(ImageView)convertView.findViewById(R.id.row_cal_dayIndex_5);
        ImageView dayIndex_6=(ImageView)convertView.findViewById(R.id.row_cal_dayIndex_6);

        dayIndex_0.setOnClickListener(new cellClick(dayIndex_0,position,0));
        dayIndex_1.setOnClickListener(new cellClick(dayIndex_1,position,1));
        dayIndex_2.setOnClickListener(new cellClick(dayIndex_2,position,2));
        dayIndex_3.setOnClickListener(new cellClick(dayIndex_3,position,3));
        dayIndex_4.setOnClickListener(new cellClick(dayIndex_4,position,4));
        dayIndex_5.setOnClickListener(new cellClick(dayIndex_5,position,5));
        dayIndex_6.setOnClickListener(new cellClick(dayIndex_6,position,6));
        
        
                if(position%2==0){
                    String text=""+(position-(position/2));
                    if(position-(position/2)<10)text="۰"+text;
                    text="از\n"+text+":۰۰\nتا\n"+text+":۲۹";
                    time.setText(text);
                }else{
                    String text=""+(position-((position/2)+1));
                    if (position-((position/2)+1)<10)text="۰"+text;
                    text="از\n"+text+":۳۰\nتا\n"+text+":۵۹";
                    time.setText(text );
                }
        
        
        if(G.schedual.timing[position][0]){
            dayIndex_0.setImageResource(R.drawable.icon_tick_green);
            dayIndex_0.setBackgroundColor(Color.parseColor("#f4f8e7"));
            
        }else{
            dayIndex_0.setImageResource(R.drawable.bgr_transparent);
            dayIndex_0.setBackgroundColor(Color.parseColor("#ffe0e0"));
        }
        if(G.schedual.timing[position][1]){
            dayIndex_1.setImageResource(R.drawable.icon_tick_green);
            dayIndex_1.setBackgroundColor(Color.parseColor("#f4f8e7"));
        }else{
            dayIndex_1.setImageResource(R.drawable.bgr_transparent);
            dayIndex_1.setBackgroundColor(Color.parseColor("#ffe0e0"));
        }
        if(G.schedual.timing[position][2]){
            dayIndex_2.setImageResource(R.drawable.icon_tick_green);
            dayIndex_2.setBackgroundColor(Color.parseColor("#f4f8e7"));
        }else{
            dayIndex_2.setImageResource(R.drawable.bgr_transparent);
            dayIndex_2.setBackgroundColor(Color.parseColor("#ffe0e0"));
        }
        if(G.schedual.timing[position][3]){
            dayIndex_3.setImageResource(R.drawable.icon_tick_green);
            dayIndex_3.setBackgroundColor(Color.parseColor("#f4f8e7"));
        }else{
            dayIndex_3.setImageResource(R.drawable.bgr_transparent);
            dayIndex_3.setBackgroundColor(Color.parseColor("#ffe0e0"));
        }
        if(G.schedual.timing[position][4]){
            dayIndex_4.setImageResource(R.drawable.icon_tick_green);
            dayIndex_4.setBackgroundColor(Color.parseColor("#f4f8e7"));
        }else{
            dayIndex_4.setImageResource(R.drawable.bgr_transparent);
            dayIndex_4.setBackgroundColor(Color.parseColor("#ffe0e0"));
        }
        if(G.schedual.timing[position][5]){
            dayIndex_5.setImageResource(R.drawable.icon_tick_green);
            dayIndex_5.setBackgroundColor(Color.parseColor("#f4f8e7"));
        }else{
            dayIndex_5.setImageResource(R.drawable.bgr_transparent);
            dayIndex_5.setBackgroundColor(Color.parseColor("#ffe0e0"));
        }
        if(G.schedual.timing[position][6]){
            dayIndex_6.setImageResource(R.drawable.icon_tick_green);
            dayIndex_6.setBackgroundColor(Color.parseColor("#f4f8e7"));
        }else{
            dayIndex_6.setImageResource(R.drawable.bgr_transparent);
            dayIndex_6.setBackgroundColor(Color.parseColor("#ffe0e0"));
        }
        
        
        
            
        return convertView;
    }
    
    class cellClick implements View.OnClickListener{
        private int mRow;
        private int mCol;
        private ImageView mCellImage;
        
        public cellClick(ImageView cellText,int row,int col){
            this.mRow=row;
            this.mCol=col;
            mCellImage =cellText;
        }
        
        @Override
        public void onClick(View view) {
            if(G.schedual.timing[mRow][mCol]){//remove timeing
                mCellImage.setImageResource(R.drawable.bgr_transparent);
                mCellImage.setBackgroundColor(Color.parseColor("#ffe0e0"));
                G.schedual.timing[mRow][mCol]=false;
                FetchDb.removeSchedual(mContext,G.schedual.KidId,G.schedual.packageName,G.schedual.className,""+mCol,""+mRow);
//                G.schedual.isNothigAdded=FetchDb.getSchedual(mContext,G.schedual.KidId,G.schedual.packageName);
//                notifyDataSetChanged();
            }else{//add timing
                mCellImage.setImageResource(R.drawable.icon_tick_green);
                mCellImage.setBackgroundColor(Color.parseColor("#f4f8e7"));
                G.schedual.timing[mRow][mCol]=true;
                FetchDb.addSchedual(mContext, G.schedual.KidId, G.schedual.packageName, G.schedual.className, "" + mCol,""+mRow);
//                G.schedual.isNothigAdded=FetchDb.getSchedual(mContext,G.schedual.KidId,G.schedual.packageName);
//                notifyDataSetChanged();
            }
            setNoDate();
        }
    }

    private void setNoDate(){
        boolean isDateSet=false;
        for(int i=0;i<G.schedual.timing.length;i++){
            for(int j=0;j<G.schedual.timing[i].length;j++){
                if(G.schedual.timing[i][j]){
                    isDateSet=true;
                    break;
                }
                if(isDateSet)break;
            }
        }
        if(isDateSet){
            noDateSet.setVisibility(View.GONE);
            
            clear_btn.setVisibility(View.VISIBLE);
            clear_img.setVisibility(View.VISIBLE);
            clear_text.setVisibility(View.VISIBLE);
            
        }else{

            clear_btn.setVisibility(View.GONE);
            clear_img.setVisibility(View.GONE);
            clear_text.setVisibility(View.GONE);
            
            noDateSet.setVisibility(View.VISIBLE);
        }
    }
}

