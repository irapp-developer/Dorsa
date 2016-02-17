package dorsa.psb.com.dorsa.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchDb {
    public static String getSetting(Context mContext, String name) {
        String Result = null;
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"name", "dim"};
        Cursor c = db.query("tbl_settings", columns, "name=?", new String[]{name}, null, null, null);
        if (c.moveToFirst()) {
            Result = c.getString(1);
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
        String[] columns = new String[]{"ID", "isSelected", "name", "birthday","sex", "settings"};
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
            if (c.moveToLast()) result = c.getString(0);

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
            
            db.update("tbl_kids",cv,"ID=?",new String[]{kidId});

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

    public static void saveKidsApp(Context mContext, String kidId, String packageName) {
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("kidId", kidId);
        cv.put("packageName", packageName);

        db.insert("tbl_kid_apps", null, cv);

        db.close();
        sql.close();
    }

    public static void removeKidApp(Context mContext, String kidId, String pakackeName) {
        int result = 0;
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.delete("tbl_kid_apps", "kidId=? AND packageName=?", new String[]{kidId, pakackeName});

        db.close();
        sql.close();
    }

    public static JSONArray getKidApps(Context mContext, String kidId) {
        JSONArray result = new JSONArray();
        Sql sql = new Sql(mContext);
        SQLiteDatabase db = sql.getReadableDatabase();
        String[] columns = new String[]{"ID", "kidId", "packageName"};
        Cursor c = db.query("tbl_kid_apps", columns, "kidId=?", new String[]{kidId}, null, null, null);
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
                //---------------------------------------------------
                if (isAppInstalled) {//app still on device
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("ID", c.getString(0));
                        jo.put("kidId", c.getString(1));
                        jo.put("packageName", c.getString(2));
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
        db.close();
        sql.close();
        return result;
    }
}
