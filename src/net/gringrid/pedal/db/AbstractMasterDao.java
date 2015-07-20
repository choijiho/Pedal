package net.gringrid.pedal.db;

import java.util.List;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractMasterDao<T> implements MasterDao<T> {
	private DBHelper helper;

	protected void setDbHelper(DBHelper helper){
		this.helper = helper;
	}

	protected DBHelper getDbHelper(){
		return helper;
	}

	@Override
	public void createTable() {
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.execSQL(getCreateTableQuery());
		} catch (SQLException e) {
		} finally {
			if(db!=null) {
				db.close();
				db = null;
			}
		}
	}

	@Override
	public void dropTable() {
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + getTableName());
		} catch (Exception e) {
		} finally {
			if(db!=null) {
				db.close();
			}
		}
	}

	@Override
	public boolean existTable() {
        boolean exists = false;

		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			try{
				final String sql = "SELECT COUNT(0) FROM sqlite_master\n" +
								   "WHERE tbl_name='"+getTableName()+"' AND type='table'";
				db = getDbHelper().getReadableDatabase();
				cursor = db.rawQuery(sql, null);
				cursor.moveToFirst();
		        exists = cursor.getInt(0) > 0;
			} finally {
				if(cursor!=null) cursor.close();
			}
		} catch (SQLException e) {
		} finally {
			if(cursor!=null)
				cursor.close();
			if(db!=null)
				db.close();
		}
		
		return exists;
	}

	protected abstract String getCreateTableQuery();
	
	protected abstract String getTableName();

}
