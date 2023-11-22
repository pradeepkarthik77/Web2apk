package com.example.web2apk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBHandler
{
    private Context context;

    private SQLiteDatabase db;
    public DBHandler(Context context)
    {
        this.context = context;
        this.db = this.context.openOrCreateDatabase("appDB.db", Context.MODE_PRIVATE, null);
    }

    public void createDB()
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS Apps " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "appname TEXT, " +
                "apkLink TEXT);");
    }

    public void insertDB(String appname,String apkLink)
    {
        String query = "INSERT INTO Apps (appname,apkLink) VALUES('"+appname+"','"+apkLink+"');";
        db.execSQL(query);
    }

    public Cursor getValues()
    {
        Cursor cursor = db.rawQuery("SELECT id,appname,apkLink from Apps ORDER BY id DESC",null);
        return cursor;
    }

    public void deleteDB(String appname)
    {
        String query = "DELETE FROM Apps WHERE appname = '"+appname+"';";
        db.execSQL(query);
    }
}
