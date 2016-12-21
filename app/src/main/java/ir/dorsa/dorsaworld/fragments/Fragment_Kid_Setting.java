package ir.dorsa.dorsaworld.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ir.dorsa.dorsaworld.Activity_Select_Background;
import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;
import eu.janmuller.android.simplecropimage.CropImage;

/**
 * Created by mehdi on 1/25/16 AD.
 */
public class Fragment_Kid_Setting extends Fragment {
    private int GALLERY_REQUEST_USER_IMAGE = 8364;
    private int CROP_PIC_REQUEST_CODE = 8574;

    View pView;

    static Activity mActivity;

    static SwitchCompat SC_runBackground;

    static SwitchCompat SC_call;
    static SwitchCompat SC_lockVolume;
    static SwitchCompat SC_runRestart;
    static SwitchCompat SC_statusBar;
    static SwitchCompat SC_lockScreen;

    static RelativeLayout REL_runBackground;
    static RelativeLayout REL_selectImage;
    static RelativeLayout REL_appName;
    static RelativeLayout REL_maxSound;
    static RelativeLayout REL_call;
    static RelativeLayout REL_lockVolume;
    static RelativeLayout REL_runRestart;
    static RelativeLayout REL_statusBar;
    static RelativeLayout REL_lockScreen;

    static ImageView IMG_selectedBgr;
    static TextView TXT_appName;

    static SeekBar SEEKBAR_maxSound;
    static TextView TV_maxSound;
    
    static TextView textViewTitle ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_kid_setting, container, false);
        mActivity = getActivity();
        setupViews();
        setValues();
        return pView;
    }


    private void setupViews() {
        SC_runBackground = null;
        SC_runBackground = (SwitchCompat) pView.findViewById(R.id.act_parent_child_hide_switch);
        SC_call = (SwitchCompat) pView.findViewById(R.id.act_parent_child_call_switch);
        SC_lockVolume = (SwitchCompat) pView.findViewById(R.id.act_parent_child_lock_volume_switch);
        SC_runRestart = (SwitchCompat) pView.findViewById(R.id.act_parent_child_restart_switch);
        SC_statusBar = (SwitchCompat) pView.findViewById(R.id.act_parent_child_status_switch);
        SC_lockScreen = (SwitchCompat) pView.findViewById(R.id.act_parent_child_screen_switch);

        textViewTitle = (TextView) pView.findViewById(R.id.act_parent_child_sett_title);

        REL_runBackground = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_hide);
        REL_selectImage = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_background);
        REL_appName = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_app_name);
        REL_maxSound = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_max_sound);
        REL_call = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_call);
        REL_lockVolume = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_lock_volume);
        REL_runRestart = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_restart);
        REL_statusBar = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_status);
        REL_lockScreen = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_screen);

        SEEKBAR_maxSound = (SeekBar) pView.findViewById(R.id.act_parent_child_max_sound_seekbar);
        TV_maxSound=(TextView) pView.findViewById(R.id.act_parent_child_max_sound_percent);
        
        IMG_selectedBgr = (ImageView) pView.findViewById(R.id.act_parent_child_background_img);

        TXT_appName = (TextView) pView.findViewById(R.id.act_parent_child_app_name_title);


        REL_runBackground.setOnClickListener(new switchRowClick(SC_runBackground));
//        REL_maxSound.setOnClickListener(new switchRowClick(SC_maxSound));
        REL_call.setOnClickListener(new switchRowClick(SC_call));
        REL_lockVolume.setOnClickListener(new switchRowClick(SC_lockVolume));
        REL_runRestart.setOnClickListener(new switchRowClick(SC_runRestart));
        REL_statusBar.setOnClickListener(new switchRowClick(SC_statusBar));
        REL_lockScreen.setOnClickListener(new switchRowClick(SC_lockScreen));


        REL_selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<CharSequence> itemsAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.select_dialog_item, new String[]{"پس زمینه های درسا", "انتخاب از گالری"});
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setAdapter(itemsAdapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Fragment_Kid_Setting.this.startActivityForResult(new Intent(getActivity(), Activity_Select_Background.class), G.REQUEST_SELECT_IMAGE);
                                break;
                            case 1:
//                                Fragment_Kid_Setting.this.startActivityForResult(Intent.createChooser(
//                                        new Intent(Intent.ACTION_GET_CONTENT).setType("image"), "Choose an image"), GALLERY_REQUEST_USER_IMAGE);

                                doSelectImage();
                                break;
                        }
                    }

                });
                Dialog dialog = builder.create();
                dialog.show();

            }
        });

        REL_appName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                mDialog.setContentView(R.layout.dialog_ask_app_name);
                Button ask_yes = (Button) mDialog.findViewById(R.id.dialog_ask_btn_yes);
                Button ask_no = (Button) mDialog.findViewById(R.id.dialog_ask_btn_no);
                final EditText appName = (EditText) mDialog.findViewById(R.id.dialog_ask_text);


                ask_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (appName.getText().toString().length() > 0) {
                            TXT_appName.setText("نام برنامه (" + appName.getText() + ")");
                            Func.changeSettings(getActivity(), "AppName", appName.getText().toString());
                            mDialog.cancel();
                        }
                    }
                });

                ask_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                    }
                });
                mDialog.show();
            }
        });
    }

    public static void setValues() {
        try {
            if("0".equals(G.selectedKid.joKid.getString("sex"))){//boy
                textViewTitle.setTextColor(ContextCompat.getColor(mActivity,R.color.Color_blue));    
            }else{//girl
                textViewTitle.setTextColor(ContextCompat.getColor(mActivity,R.color.Color_red_girl));
            }
            
            
            JSONObject setting = new JSONObject(G.selectedKid.joKid.getString("settings"));

            SC_runBackground.setChecked(Boolean.parseBoolean(setting.getString("backgroundRun")));

            SC_call.setChecked(Boolean.parseBoolean(setting.getString("call")));
            SC_lockVolume.setChecked(Boolean.parseBoolean(setting.getString("lockVolume")));
            SC_runRestart.setChecked(Boolean.parseBoolean(setting.getString("restart")));
            SC_statusBar.setChecked(Boolean.parseBoolean(setting.getString("statusBar")));
            SC_lockScreen.setChecked(Boolean.parseBoolean(setting.getString("screen")));

            SC_runBackground.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "backgroundRun"));

            SC_call.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "call"));
            SC_lockVolume.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "lockVolume"));
            SC_runRestart.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "restart"));
            SC_statusBar.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "statusBar"));
            SC_lockScreen.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "screen"));


            SEEKBAR_maxSound.setProgress(setting.getInt("maxSound"));
            TV_maxSound.setText("" + setting.getInt("maxSound") + "%");
            
            SEEKBAR_maxSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int stepSize = 20;
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                   int progress = (Math.round(seekBar.getProgress()/stepSize))*stepSize;
                    seekBar.setProgress(progress);
                    TV_maxSound.setText("" + progress+ "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Func.changeSettings(App.getContext(), "maxSound", "" + seekBar.getProgress());
                    AudioManager am =(AudioManager) App.getContext().getSystemService(Context.AUDIO_SERVICE);
                    int max_SoundSystem=(int)(am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)*(seekBar.getProgress()/100.0f));
                    am.setStreamVolume(AudioManager.STREAM_SYSTEM, max_SoundSystem, am.FLAG_PLAY_SOUND);
                }
            });

            String bgr=setting.getString("bgr");
            if(bgr.contains("bgr_kid_drawable_")){
                Picasso.with(App.getContext()).load(G.selectImage.SelectedImageResource).fit().into(IMG_selectedBgr);
//                IMG_selectedBgr.setImageResource(Integer.parseInt(bgr.substring("bgr_kid_drawable_".length())));
            }else{
                Bitmap BgrKid = Func.getImage("bgr_kid_thumb_" + G.selectedKid.joKid.getString("ID"));
                if (BgrKid != null) {
                    IMG_selectedBgr.setImageBitmap(BgrKid);
                } else {
                    IMG_selectedBgr.setImageResource(R.drawable.bgr_default);
                }
            }
            

            TXT_appName.setText("نام برنامه (" + setting.getString("AppName") + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class switchRowClick implements View.OnClickListener {
        private SwitchCompat mSwitch;

        public switchRowClick(SwitchCompat switchCompat) {
            this.mSwitch = switchCompat;
        }

        @Override
        public void onClick(View v) {
            mSwitch.setChecked(!mSwitch.isChecked());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == GALLERY_REQUEST_USER_IMAGE && data.getData() != null) {
//                Uri uri = data.getData();
                Uri selectedImage = data.getData();
                String fileRourcePath=null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && "com.android.providers.media.documents".equals(selectedImage.getAuthority()))
                {
                    Log.d(G.LOG_TAG,"start from here");
                     fileRourcePath = getPath(getContext(), selectedImage);
                    if(fileRourcePath==null)
                        try {
                            InputStream is = getActivity().getContentResolver().openInputStream(selectedImage);
                            fileRourcePath=selectedImage.getPath();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                   
                }else {
                   
                     fileRourcePath = getPath(selectedImage);
                }
                Log.d(G.LOG_TAG,"File path is "+fileRourcePath);
                
                if(fileRourcePath==null)return;
                
                File fileFinal=new File(G.dir +"/gallery.PNG");
                if(!fileFinal.exists()) {
                    try {
                        Log.d(G.LOG_TAG, "file not exists:" + fileFinal.createNewFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                fileFinal.deleteOnExit();
                File FileSource=new File(fileRourcePath);
                try {
                    copy(FileSource, fileFinal);
                    //fileFinal.delete();
                    doCrop(getContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == CROP_PIC_REQUEST_CODE) {
                String path = data.getStringExtra(CropImage.IMAGE_PATH);

                if (path == null) {

                    return;
                }
                // cropped bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    IMG_selectedBgr.setImageBitmap(bitmap);
                } else {
                    IMG_selectedBgr.setImageResource(R.drawable.bgr_default);
                }
                try {
                    Func.saveImage(bitmap, "bgr_kid_" + G.selectedKid.joKid.getString("ID"));
                    Func.saveImage(Bitmap.createScaledBitmap(bitmap, 120, 120, false), "bgr_kid_thumb_" + G.selectedKid.joKid.getString("ID"));
                    Func.changeSettings(App.getContext(), "bgr", "bgr_kid_" + 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_OK & requestCode == G.REQUEST_SELECT_IMAGE) {
            Picasso.with(getContext()).load(G.selectImage.SelectedImageResource).fit().into(IMG_selectedBgr);
//            IMG_selectedBgr.setImageResource(G.selectImage.SelectedImageResource);
            Func.changeSettings(App.getContext(), "bgr", "bgr_kid_drawable_" + G.selectImage.SelectedImageResource);


        }
    }

    private static String getPath(Context context, Uri uri)
    {
        if( uri == null ) {
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor;
        if(Build.VERSION.SDK_INT >19)
        {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to             
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, sel, new String[]{ id }, null);
        }
        else
        {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try
        {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        }
        catch(NullPointerException e) {

        }
        return path;
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
//        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        //yourSelectedImage = BitmapFactory.decodeFile(filePath);
        return filePath;
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
   
    private void doSelectImage(){
        Intent intent = new Intent();

        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_USER_IMAGE);
//        Fragment_Kid_Setting.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_USER_IMAGE);


    }

    private void doCrop(Context mContext){
        Intent intent = new Intent(mContext, CropImage.class);

        // tell CropImage activity to look for image to crop 
        File file=new File(G.dir +"/gallery.PNG");
        if(!file.exists()) {
           return;
        }
        file.deleteOnExit();
        String filePath =file.getPath();
        
        intent.putExtra(CropImage.IMAGE_PATH, filePath);

        // allow CropImage activity to rescale image
        intent.putExtra(CropImage.SCALE, true);

        // if the aspect ratio is fixed to ratio 3/2

        int width=0;
        int height=0;
        // getsize() is available from API 13
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width=size.x;
            height=size.y;
        } else {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            // getWidth() & getHeight() are depricated
            width=display.getWidth();
            height=display.getHeight();
        }

        int gcd = GCD(width,height);
        int aspectX = width / gcd;
        int aspectY = height / gcd;
        
        intent.putExtra(CropImage.ASPECT_X, aspectX);
        intent.putExtra(CropImage.ASPECT_Y, aspectY);


//        intent.putExtra(CropImage.OUTPUT_X, 128);
//        intent.putExtra(CropImage.OUTPUT_Y, 128);

        // start activity CropImage with certain request code and listen
        // for result
        startActivityForResult(intent, CROP_PIC_REQUEST_CODE);
        
    }
    
    private void doCrop1() {
        try {
            File file=new File(G.dir +"/gallery.PNG");
            if(!file.exists()) {
                try {
                    Log.d(G.LOG_TAG, "file not exists:" + file.createNewFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file.deleteOnExit();
            Intent cropIntent  = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            int width=0;
            int height=0;
            // getsize() is available from API 13
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                width=size.x;
                height=size.y;
            } else {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                // getWidth() & getHeight() are depricated
                width=display.getWidth();
                height=display.getHeight();
            }

            int gcd = GCD(width,height);
            int aspectX = width / gcd;
            int aspectY = height / gcd;

            int outputX = width - (aspectX * 30);
            int outputY = height - (aspectY * 30);
            
            if(outputX<0)outputX=outputX*-1;
            if(outputY<0)outputY=outputY*-1;
            
            Log.d(G.LOG_TAG,"width :"+width);
            Log.d(G.LOG_TAG,"height :"+height);
            Log.d(G.LOG_TAG,"width ration :"+aspectX);
            Log.d(G.LOG_TAG, "height ration :" + aspectY);
            
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 0);
            cropIntent.putExtra("aspectY", 0);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, file.toURI());
//            cropIntent.putExtra("outputFormat",Bitmap.CompressFormat.PNG.toString());
            
            
            
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            // display an error message
//            String errorMessage = "Whoops - your device doesn't support the crop action!";
        }
    }
    private int GCD(int a, int b) {
        return (b == 0 ? a : GCD(b, a % b));
    }
}
