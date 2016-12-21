package ir.dorsa.dorsaworld.other;

import android.app.Activity;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.dorsa.dorsaworld.adapter.Adapter_All_Apps;
import ir.dorsa.dorsaworld.adapter.Adapter_Calendar;
import ir.dorsa.dorsaworld.adapter.Adapter_Kid_Apps;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class G {

    public static String LOG_TAG = "DORSA_TAG";

    public static int version=1;
    
    public static ViewPager mViewPager;
    public static String dir = Environment.getExternalStorageDirectory().toString() + "/Android/Dorsa";
    public static FragmentManager frgManager;
    public static AppCompatActivity mActivity;

    public static final int REQUEST_INTRO = 6473;
    public static final int REQUEST_PASSWORD = 1245;
    public static final int REQUEST_ACCESS_TASK = 840;
    public static final int REQUEST_CHILD = 435;
    public static final int REQUEST_SELECT_IMAGE = 238;
    
    public static boolean isServiceLaucerRun=false;
    public static boolean isServiceWatchDog=false;
    public static boolean isServiceRunInBackground=false;

    public static class kid_details {
        public static int sexMode = -1;//0=male,1=fmale
        public static String birthDay = "";
        public static int selectedValueYear = -1;
        public static int selectedValueMonth = -1;
        public static int selectedValueDay = -1;


        public static RelativeLayout MaleRel;
        public static RelativeLayout FMaleRel;

        public static ImageView kidsImg;

        public static ImageView[] girlsIcons = new ImageView[0];
        public static ImageView[] boysIcons = new ImageView[0];

        public static ImageView girls_stroke[] = new ImageView[6];
        public static ImageView boys_stroke[] = new ImageView[6];

    }

    public static class kids_app {

        public static List<ResolveInfo> AllSystemApps = new ArrayList<>();

        public static ArrayList<ResolveInfo> AllAppResolver = new ArrayList<>();
        public static ArrayList<JSONObject> KidAppResolver = new ArrayList<>();

        public static Adapter_Kid_Apps Adapter_kids_app;
        public static Adapter_All_Apps Adapter_all_app;

    }

    public static class Intro {
        public static String password = "";
        public static String passmode = "-1";//0--> char,1-->pattern
        public static boolean isFromApp = false;
    }

    public static class addChild {
        public static String name = "";
        public static String birthday = "";
        public static boolean isEditedMode = false;
        public static String saveID="";
        public static Bitmap avatarBitmap;
        public static ArrayList<JSONObject> kidApps=new ArrayList<>();
        
        
    }

    public static class selectedKid {
        public static JSONObject joKid;
    }

    public static class chooseKid {
        public static ArrayList<JSONObject> kidsDetails = new ArrayList<>();
    }

    public static class selectImage {
        public static int SelectedImageResource;
    }

    public static class Permiss {
        public static SettingsContentObserver lockVolume;
        
        public static boolean DEFAULT_WIFI=false;
        public static boolean DEFAULT_DATA=false;
    }

    public static class ResetPass {
        public static ViewPager mViewPager;
    }

    public static class Watchdog {
        public static Activity waitForBack;
        public static Activity launcher;
        public static int isAppRuned = 0;
    }

    public static class DorsaApps {
        public static ArrayList<JSONObject> listData = new ArrayList<>();
        public static ArrayList<JSONObject> listDataBackup = new ArrayList<>();
    }

    public static class schedual {
        public static String KidId;
        public static String packageName;
        public static String className;
        public static boolean isNothigAdded=true;
        public static boolean[][] timing = new boolean[48][7];
        public static Adapter_Calendar mAdapter;


    }


}
