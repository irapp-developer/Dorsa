package ir.dorsa.dorsaworld.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class FetchDb {

    public static void EditDB(Context mContext) {
        String Result = null;
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        try {
            ContentValues cv=new ContentValues();
            cv.put("name", "lastWrongPass");
            cv.put("dim", "-1");
            db.insert("tbl_settings", null, cv);
            
            cv.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        db.close();
        sql.close();

    }

    public static String getSetting(Context mContext, String name) {
        String Result = null;
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"name", "dim"};
        Cursor c = db.query("tbl_settings", columns, "name=?", new String[]{name}, null, null, null);
        if (c.moveToFirst()) {
            Result = c.getString(1);
        }else{
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("dim", "");
            db.insert("tbl_settings", null,cv);
        }
        db.close();
        sql.close();
        return Result;
    }

    public static void setSetting(Context mContext, String name, String dim) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("dim", dim);
        db.update("tbl_settings", cv, "name=?", new String[]{name});

        db.close();
        sql.close();
    }

    public static int getKidsCount(Context mContext) {
        int result = 0;
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"ID"};
        Cursor c = db.query("tbl_kids", columns, null, null, null, null, null);
        result = c.getCount();
        db.close();
        sql.close();
        return result;
    }

    public static JSONObject getSelectedKid(Context mContext) {
        JSONObject Result = null;
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"ID", "isSelected", "name", "birthday", "sex", "settings"};
        Cursor c = db.query("tbl_kids", columns, "isSelected=?", new String[]{"true"}, null, null, null);
        if (c.moveToFirst()) {
            Result = new JSONObject();
            try {
                Result.put("ID", c.getString(0));
                Result.put("isSelected", c.getString(1));
                Result.put("name", c.getString(2));
                Result.put("birthday", c.getString(3));
                Result.put("sex", c.getString(4));
                Result.put("settings", c.getString(5));
            } catch (JSONException e) {
                e.printStackTrace();
            }
//			Result=c.getString(1);
        }
        db.close();
        sql.close();
        return Result;
    }

    public static String AddKid(Context mContext, String jsonKid) {
        String result = "-1";
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();

        try {
            JSONObject jo = new JSONObject(jsonKid);
            String name = jo.getString("name");
            String birthday = jo.getString("birthday");
            String settings = jo.getString("settings");
            String sex = jo.getString("sex");

            cv.put("isSelected", "false");
            db.update("tbl_kids", cv, "isSelected=?", new String[]{"true"});
            cv.clear();

            cv.put("isSelected", "true");
            cv.put("name", name);
            cv.put("birthday", birthday);
            cv.put("settings", settings);
            cv.put("sex", sex);
            db.insert("tbl_kids", null, cv);

            String[] columns = new String[]{"ID"};
            Cursor c = db.query("tbl_kids", columns, null, null, null, null, "ID ASC");
            if (c.moveToLast()) {
                result = c.getString(0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();
        sql.close();
        return result;
    }

    public static void setSelctedKid(Context mContext, String kidId) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("isSelected", "false");
        db.update("tbl_kids", cv, "isSelected=?", new String[]{"true"});
        cv.clear();

        cv.put("isSelected", "true");
        db.update("tbl_kids", cv, "ID=?", new String[]{kidId});
        db.close();
        sql.close();
    }

    public static JSONArray getAllKids(Context mContext) {
        JSONArray Result = new JSONArray();
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"ID", "isSelected", "name", "birthday", "settings"};
        Cursor c = db.query("tbl_kids", columns, null, null, null, null, null);
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("ID", c.getString(0));
                    jo.put("isSelected", c.getString(1));
                    jo.put("name", c.getString(2));
                    jo.put("birthday", c.getString(3));
                    jo.put("settings", c.getString(4));
                    Result.put(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                c.moveToNext();
            }
        }
        db.close();
        sql.close();
        return Result;
    }

    public static void removeKid(Context mContext, String kidId) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.delete("tbl_kids", "ID=?", new String[]{kidId});
        db.delete("tbl_kid_apps", "kidId=?", new String[]{kidId});
        db.close();
        sql.close();
        removeKidDailyLimit(mContext, kidId);
        removeKidSchedual(mContext, kidId);
    }

    public static void editKid(Context mContext, String kidId, String jsonKid) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();
        JSONObject jo = null;
        try {
            jo = new JSONObject(jsonKid);

            String name = jo.getString("name");
            String birthday = jo.getString("birthday");
            String sex = jo.getString("sex");

            cv.put("name", name);
            cv.put("birthday", birthday);
            cv.put("sex", sex);

            db.update("tbl_kids", cv, "ID=?", new String[]{kidId});

        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();
        sql.close();
    }

    public static void setKidSettings(Context mContext, String Id, String json) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("settings", json);
        db.update("tbl_kids", cv, "ID=?", new String[]{Id});
        db.close();
        sql.close();
    }

    public static void saveKidsApp(Context mContext, String kidId, String packageName, String AccessIntenrt, String className) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("kidId", kidId);
        cv.put("packageName", packageName);
        cv.put("className", className);
        cv.put("AccessInternet", AccessIntenrt);

        db.insert("tbl_kid_apps", null, cv);

        db.close();
        sql.close();

        addKidDailyLimit(mContext, kidId, packageName, className, "false", "5");
    }

    public static void removeKidApp(Context mContext, String kidId, String pakackeName, String className) {
        int result = 0;
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.delete("tbl_kid_apps", "kidId=? AND packageName=? AND className=?", new String[]{kidId, pakackeName, className});

        db.close();
        sql.close();

        removeKidAppSchedual(mContext, kidId, pakackeName, className);
        removeKidAppDailyLimit(mContext, kidId, pakackeName, className);
    }

    public static JSONArray getKidApps(Context mContext, String kidId) {
        JSONArray result = new JSONArray();
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"ID", "kidId", "packageName", "AccessInternet", "className"};
        Cursor c = db.query("tbl_kid_apps", columns, "kidId=?", new String[]{kidId}, null, null, null);
        try {
            if (c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    ///----- check is selected app still installed -----------
                    boolean isAppInstalled = false;
                    for (int j = 0; j < G.kids_app.AllAppResolver.size(); j++) {
                        if (c.getString(2).equals(G.kids_app.AllAppResolver.get(j).activityInfo.applicationInfo.packageName)) {
                            isAppInstalled = true;
                            break;
                        }
                    }
                    if(!isAppInstalled) {
                        for (int j = 0; j < G.kids_app.AllAppResolver.size(); j++) {
                            if (c.getString(2).equals(G.DorsaApps.listData.get(j).getString("packageName"))) {
                                isAppInstalled = true;
                                break;
                            }
                        }
                    }
                    //---------------------------------------------------
                    if (isAppInstalled) {//app still on device
                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("ID", c.getString(0));
                            jo.put("kidId", c.getString(1));
                            jo.put("packageName", c.getString(2));
                            jo.put("AccessInternet", c.getString(3));
                            jo.put("className", c.getString(4));
                            result.put(jo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {//app removed from device
                        db.delete("tbl_kid_apps", "ID=?", new String[]{c.getString(0)});
                    }
                    c.moveToNext();
                }
            }
        } catch (Exception ex) {

        }
        db.close();
        sql.close();
        return result;
    }

    public static void UpdateInternetAccess(Context mContext, String kidId, String packageName, String mode) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("AccessInternet", mode);

        db.update("tbl_kid_apps", cv, "kidId=? AND packageName=?", new String[]{kidId, packageName});

        db.close();
        sql.close();
    }

    //---------------
    public static boolean getSchedual(Context mContext, String kidId, String packageName, String className) {
        boolean result = false;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 48; j++) {
                G.schedual.timing[j][i] = false;
            }
        }

        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"ID", "kidId", "packageName", "timeIndex", "dayIndex", "className"};
        Cursor c = db.query("tbl_schedual", columns, "kidId=? AND packageName=? AND className=?", new String[]{kidId, packageName, className}, null, null, null);
        if (c.moveToFirst()) {
            result = true;
            for (int i = 0; i < c.getCount(); i++) {
                G.schedual.timing[Integer.parseInt(c.getString(3))][Integer.parseInt(c.getString(4))] = true;
                c.moveToNext();
            }

        }
        db.close();
        sql.close();
        return result;
    }

    public static int isSchedualWeekEnable(Context mContext, String kidId, String packageName, String className) {
        int result = -1;//-1-->not allow,0-->no weekly limit,1-->allow
        Calendar cal = Calendar.getInstance();
        int timeIndex = Func.timeIndex(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        int dayIndex = Func.getDayIndex(cal.get(Calendar.DAY_OF_WEEK));
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"ID", "kidId", "packageName", "timeIndex", "dayIndex", "className"};
        Cursor c = db.query("tbl_schedual", columns, "kidId=? AND packageName=? AND className=?", new String[]{kidId, packageName, className}, null, null, null);
        if (c.getCount() > 0) {//there is weekly limited
            columns = new String[]{"ID", "kidId", "packageName", "timeIndex", "dayIndex"};
            c = db.query("tbl_schedual", columns, "kidId=? AND packageName=? AND className=? AND dayIndex=? AND timeIndex=?", new String[]{kidId, packageName, className, "" + dayIndex, "" + timeIndex}, null, null, null);
            if (c.moveToFirst()) {
                result = 1;
            }
        } else {//no weekly limited
            result = 0;
        }
        db.close();
        sql.close();
        return result;
    }

    public static void addSchedual(Context mContext, String kidId, String packageName, String className, String dayIndex, String timeIndex) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("kidId", kidId);
        cv.put("packageName", packageName);
        cv.put("className", className);
        cv.put("timeIndex", timeIndex);
        cv.put("dayIndex", dayIndex);

        db.insert("tbl_schedual", null, cv);

        db.close();
        sql.close();
    }

    public static void removeSchedual(Context mContext, String kidId, String packageName, String className, String dayIndex, String timeIndex) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.delete("tbl_schedual", "kidId=? AND packageName=? AND className=? AND timeIndex=? AND dayIndex=?", new String[]{kidId, packageName, className, timeIndex, dayIndex});
        db.close();
        sql.close();
    }

    public static void removeKidAppSchedual(Context mContext, String kidId, String packageName, String className) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.delete("tbl_schedual", "kidId=? AND packageName=? AND className=?", new String[]{kidId, packageName, className});
        db.close();
        sql.close();
    }

    public static void removeKidSchedual(Context mContext, String kidId) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.delete("tbl_schedual", "kidId=?", new String[]{kidId});
        db.close();
        sql.close();
    }

    //--------------
    public static void addKidDailyLimit(Context mContext, String kidId, String packageName,String className, String enable, String limitPerDay) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("kidId", kidId);
        cv.put("packageName", packageName);
        cv.put("className", className);
        cv.put("enable", enable);
        cv.put("limitPerDay", limitPerDay);
        cv.put("lastDayUsed", "-1");
        cv.put("usedSecound", "0");

        db.insert("tbl_daily_limit", null, cv);

        db.close();
        sql.close();
    }

    public static void removeKidDailyLimit(Context mContext, String kidId) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.delete("tbl_daily_limit", "kidId=?", new String[]{kidId});
        db.close();
        sql.close();
    }

    public static void removeKidAppDailyLimit(Context mContext, String kidId, String packageName,String className) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.delete("tbl_daily_limit", "kidId=? AND packageName=? AND className=?", new String[]{kidId, packageName,className});
        db.close();
        sql.close();
    }

    public static JSONObject getKidDailyLimit(Context mContext, String kidId, String packageName,String className) {
        Log.d(G.LOG_TAG, "Kid daily limit :" + packageName);
        JSONObject result = null;
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"ID", "kidId", "packageName", "limitPerDay", "enable", "lastDayUsed", "usedSecound","className"};
        Cursor c = db.query("tbl_daily_limit", columns, "kidId=? AND packageName=? AND className=?", new String[]{kidId, packageName,className}, null, null, null);
        if (c.moveToFirst()) {
            result = new JSONObject();
            try {
                result.put("limitPerDay", c.getString(3));
                result.put("enable", c.getString(4));
                result.put("lastDayUsed", c.getString(5));
                result.put("usedSecound", c.getString(6));
                result.put("className", c.getString(7));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
        sql.close();
        return result;
    }

    public static void setKidDailyLimit(Context mContext, String kidId, String packageName,String className, String enable, String limitPerDay) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("enable", enable);
        cv.put("limitPerDay", limitPerDay);

        db.update("tbl_daily_limit", cv, "kidId=? AND packageName=? AND className=?", new String[]{kidId, packageName,className});

        db.close();
        sql.close();
    }

    public static void setKidDailyLimitLastUsed(Context mContext, String kidId, String packageName,String className, String lastDayUsed, String usedSecound) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = new ContentValues();

        Log.d(G.LOG_TAG, "DL ch usedSecound=" + usedSecound);

        cv.put("lastDayUsed", lastDayUsed);
        cv.put("usedSecound", usedSecound);

        db.update("tbl_daily_limit", cv, "kidId=? AND packageName=? AND className=?", new String[]{kidId, packageName,className});

        db.close();
        sql.close();
    }
}
