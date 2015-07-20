package net.gringrid.pedal.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	public static final String DB_FILE_NAME = "pedal.db";
	
	private static int version = 1;

	private static SQLiteDatabase db;
	private static DBHelper instance;

    private DBHelper(Context ctx)
    {
        super(ctx, DB_FILE_NAME, null, version);
    }

    public static DBHelper getInstance(Context context){
        if(instance == null)
        {
        	synchronized(DBHelper.class)
        	{
	            instance = new DBHelper(context);
        	}
        }
        return instance;
    }

    public void close(){
        if(instance != null){
            db.close();
            instance = null;
        }
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
}
