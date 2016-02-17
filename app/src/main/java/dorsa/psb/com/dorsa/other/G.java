package dorsa.psb.com.dorsa.other;

import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dorsa.psb.com.dorsa.adapter.Adapter_All_Apps;
import dorsa.psb.com.dorsa.adapter.Adapter_Kid_Apps;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class G {

    public static String LOG_TAG = "DORSA_TAG";

    public static ViewPager mViewPager;
    public static String dir = Environment.getExternalStorageDirectory().toString() + "/Android/Dorsa";

    public static int REQUEST_INTRO = 6473;
    public static int REQUEST_PASSWORD = 1245;
    public static int REQUEST_CHILD = 435;
    public static int REQUEST_SELECT_IMAGE = 238;

    public static class kid_details {
        public static int sexMode = -1;//0=male,1=fmale
        public static String birthDay = "";
        public static int selectedValueYear = -1;
        public static int selectedValueMonth = -1;


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
        public static ArrayList<String> KidAppResolver = new ArrayList<>();

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
    }

    public static class selectedKid {
        public static JSONObject joKid;
    }


    public static class chooseKid {
        public static ArrayList<JSONObject> kidsDetails = new ArrayList<>();
    }
    
    public static class selectImage{
        public static int SelectedImageResource;
    }
    
    public static class Permiss{
        public static SettingsContentObserver lockVolume;
    }

}
