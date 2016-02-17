package dorsa.psb.com.dorsa.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dorsa.psb.com.dorsa.Activity_Select_Background;
import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;

/**
 * Created by mehdi on 1/25/16 AD.
 */
public class Fragment_Kid_Setting extends Fragment {
    private int GALLERY_REQUEST_USER_IMAGE = 8364;

    View pView;

    static Activity mActivity;

    static SwitchCompat SC_runBackground;
    static SwitchCompat SC_maxSound;
    static SwitchCompat SC_airPlane;
    static SwitchCompat SC_lockVolume;
    static SwitchCompat SC_runRestart;
    static SwitchCompat SC_statusBar;
    static SwitchCompat SC_lockScreen;

    static RelativeLayout REL_runBackground;
    static RelativeLayout REL_selectImage;
    static RelativeLayout REL_appName;
    static RelativeLayout REL_maxSound;
    static RelativeLayout REL_airPlane;
    static RelativeLayout REL_lockVolume;
    static RelativeLayout REL_runRestart;
    static RelativeLayout REL_statusBar;
    static RelativeLayout REL_lockScreen;

    static ImageView IMG_selectedBgr;
    static TextView TXT_appName;


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
        SC_maxSound = (SwitchCompat) pView.findViewById(R.id.act_parent_child_max_sound_switch);
        SC_airPlane = (SwitchCompat) pView.findViewById(R.id.act_parent_child_airplane_switch);
        SC_lockVolume = (SwitchCompat) pView.findViewById(R.id.act_parent_child_lock_volume_switch);
        SC_runRestart = (SwitchCompat) pView.findViewById(R.id.act_parent_child_restart_switch);
        SC_statusBar = (SwitchCompat) pView.findViewById(R.id.act_parent_child_status_switch);
        SC_lockScreen = (SwitchCompat) pView.findViewById(R.id.act_parent_child_screen_switch);

        REL_runBackground = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_hide);
        REL_selectImage = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_background);
        REL_appName = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_app_name);
        REL_maxSound = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_max_sound);
        REL_airPlane = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_airplane);
        REL_lockVolume = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_lock_volume);
        REL_runRestart = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_restart);
        REL_statusBar = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_status);
        REL_lockScreen = (RelativeLayout) pView.findViewById(R.id.act_parent_child_sett_screen);

        IMG_selectedBgr = (ImageView) pView.findViewById(R.id.act_parent_child_background_img);

        TXT_appName = (TextView) pView.findViewById(R.id.act_parent_child_app_name_title);


        REL_runBackground.setOnClickListener(new switchRowClick(SC_runBackground));
        REL_maxSound.setOnClickListener(new switchRowClick(SC_maxSound));
        REL_airPlane.setOnClickListener(new switchRowClick(SC_airPlane));
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
                                Fragment_Kid_Setting.this.startActivityForResult(Intent.createChooser(
                                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Choose an image"), GALLERY_REQUEST_USER_IMAGE);

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
            JSONObject setting = new JSONObject(G.selectedKid.joKid.getString("settings"));

            SC_runBackground.setChecked(Boolean.parseBoolean(setting.getString("backgroundRun")));
            SC_maxSound.setChecked(Boolean.parseBoolean(setting.getString("maxSound")));
            SC_airPlane.setChecked(Boolean.parseBoolean(setting.getString("airPlane")));
            SC_lockVolume.setChecked(Boolean.parseBoolean(setting.getString("lockVolume")));
            SC_runRestart.setChecked(Boolean.parseBoolean(setting.getString("restart")));
            SC_statusBar.setChecked(Boolean.parseBoolean(setting.getString("statusBar")));
            SC_lockScreen.setChecked(Boolean.parseBoolean(setting.getString("screen")));

            SC_runBackground.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "backgroundRun"));
            SC_maxSound.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "maxSound"));
            SC_airPlane.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "airPlane"));
            SC_lockVolume.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "lockVolume"));
            SC_runRestart.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "restart"));
            SC_statusBar.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "statusBar"));
            SC_lockScreen.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "screen"));

            Bitmap BgrKid = Func.getImage("bgr_kid_thumb_" + G.selectedKid.joKid.getString("ID"));
            if (BgrKid != null) {
                IMG_selectedBgr.setImageBitmap(BgrKid);
            } else {
                IMG_selectedBgr.setImageResource(R.drawable.bgr_default);
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
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    if (bitmap != null) {
                        IMG_selectedBgr.setImageBitmap(bitmap);
                    } else {
                        IMG_selectedBgr.setImageResource(R.drawable.bgr_default);
                    }
                    try {
                        Func.saveImage(bitmap, "bgr_kid_" + G.selectedKid.joKid.getString("ID"));
                        Func.saveImage(Bitmap.createScaledBitmap(bitmap, 120, 120, false), "bgr_kid_thumb_" + G.selectedKid.joKid.getString("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_OK & requestCode == G.REQUEST_SELECT_IMAGE) {

            IMG_selectedBgr.setImageResource(G.selectImage.SelectedImageResource);
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), G.selectImage.SelectedImageResource);
                        Func.saveImage(bitmap, "bgr_kid_" + G.selectedKid.joKid.getString("ID"));
                        Func.saveImage(Bitmap.createScaledBitmap(bitmap, 120, 120, false), "bgr_kid_thumb_" + G.selectedKid.joKid.getString("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
    }
}
