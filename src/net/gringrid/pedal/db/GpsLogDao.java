package net.gringrid.pedal.db;

import java.util.LinkedList;
import java.util.List;

import net.gringrid.pedal.db.vo.GpsLogMaster;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


public class GpsLogDao extends AbstractMasterDao<GpsLogMaster>{
	public static final String TABLENAME = "gps_log";

	private static GpsLogDao instance;

	private static final String SQL_CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLENAME +
					"(primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					GpsLogMaster.LATITUDE+ " TEXT , " +
					GpsLogMaster.LONGITUDE+ " TEXT , " +
					GpsLogMaster.ELEVATION+ " TEXT , " +
					GpsLogMaster.GPS_TIME+ " TEXT)";

	private static final String SQL_INSERT =
			String.format("INSERT INTO %s(%s,%s,%s,%s) VALUES(?,?,?,?)",
					TABLENAME,
					GpsLogMaster.LATITUDE,
					GpsLogMaster.LONGITUDE,
					GpsLogMaster.ELEVATION,
					GpsLogMaster.GPS_TIME);

    public static GpsLogDao getInstance(DBHelper helper){
        if(instance == null)
        {
        	synchronized(GpsLogMaster.class)
        	{
	            instance = new GpsLogDao();
        	}
        }
        instance.setDbHelper(helper);

        return instance;
    }
    
	@Override
	public long insert(GpsLogMaster object) {
		long result = 0L;
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();
		
			stmt = db.compileStatement(SQL_INSERT);
			
			stmt.bindString(1, String.format("%.7f", object.latitude));
			stmt.bindString(2, String.format("%.7f",  object.longitude));
			stmt.bindString(3, String.format("%.1f", object.elevation));
			// TODO
			stmt.bindString(4, String.format("%.7f", object.gpsTime));

			result = stmt.executeInsert();

			db.setTransactionSuccessful();

		} catch (SQLException e) {
		} finally {
			if(stmt!=null) {
				stmt.close();
				stmt = null;
			}
			if(db!=null) {
				db.endTransaction();
				db.close();
				db = null;
			}
		}
		return result;
	}

	@Override
	public long[] insert(List<GpsLogMaster> objects) {
		long results[] = new long[objects.size()];
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			stmt = db.compileStatement(SQL_INSERT);

			for(int i=0; i<objects.size(); i++)
			{
				GpsLogMaster object = objects.get(i);
				stmt.bindString(1, String.format("%.7f", object.latitude));
				stmt.bindString(2, String.format("%.7f",  object.longitude));
				stmt.bindString(3, String.format("%.1f", object.elevation));
				// TODO
				stmt.bindString(4, String.format("%.7f", object.gpsTime));;
				results[i] = stmt.executeInsert();
			}

			db.setTransactionSuccessful();

		} catch (SQLException e) {
		} finally {
			if(stmt!=null) {
				stmt.close();
				stmt = null;
			}
			if(db!=null) {
				db.endTransaction();
				db.close();
				db = null;
			}
		}
		return results;
	}

	@Override
	public int delete(int id) {
		int result = 0;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			result = db.delete(TABLENAME, GpsLogMaster.PRIMARY_KEY+"="+Integer.toString(id), null);

			db.setTransactionSuccessful();
			
		} catch (SQLException e) {
		} finally {
			if(db!=null) {
				db.endTransaction();
				db.close();
				db = null;
			}
		}

		return result;
	}

	@Override
	public int update(GpsLogMaster object) {
		int result = 0;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			ContentValues values = new ContentValues();
			values.put(GpsLogMaster.LATITUDE, object.latitude);
			values.put(GpsLogMaster.LONGITUDE, object.longitude);
			values.put(GpsLogMaster.ELEVATION, object.elevation);
			values.put(GpsLogMaster.GPS_TIME, object.gpsTime);
			result = db.update(TABLENAME,
									values,
									GpsLogMaster.PRIMARY_KEY+"="+object.primaryKey,
									null);

			db.setTransactionSuccessful();

		} catch (SQLException e) {
		} finally {
			if(db!=null) {
				db.endTransaction();
				db.close();
				db = null;
			}
		}

		return result;
	}

	@Override
	public int deleteAll() {
		int result = 0;

		SQLiteDatabase db = null;
		
		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			result = db.delete(TABLENAME, null, null);
			
			db.setTransactionSuccessful();
			
		} catch (SQLException e) {
		} finally {
			if(db!=null) {
				db.endTransaction();
				db.close();
				db = null;
			}
		}
		
		return result;
	}

	@Override
	public GpsLogMaster find(int id) {
		GpsLogMaster object = null;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try{
				cursor = db.query(true, TABLENAME, null,
					GpsLogMaster.PRIMARY_KEY+"="+Integer.toString(id), null, null, null, null, null);
				if (cursor != null) {
					if(cursor.moveToFirst()) {
						object = new GpsLogMaster();
						object.primaryKey = cursor.getInt(0); 
						object.latitude = Double.parseDouble(cursor.getString(1));
						object.longitude = Double.parseDouble(cursor.getString(2));
						object.elevation = Double.parseDouble(cursor.getString(3));
						object.gpsTime = Long.parseLong(cursor.getString(4));
					}
		        }
			} finally {
				if(cursor!=null) cursor.close();
			}
		} catch (SQLException e) {
		} finally {
			if(db!=null) {
				db.close();
				db = null;
			}
		}

        return object;
	}

	@Override
	public List<GpsLogMaster> findAll() {
		List<GpsLogMaster> objects = new LinkedList<GpsLogMaster>();
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try {
				cursor = db.query(TABLENAME, null, null, null, null, null, GpsLogMaster.GPS_TIME+ " ASC");
				if(cursor!=null) {
					if(cursor.moveToFirst()) {
						do{
							GpsLogMaster object = new GpsLogMaster();
							object.primaryKey = cursor.getInt(0); 
							object.latitude = Double.parseDouble(cursor.getString(1));
							object.longitude = Double.parseDouble(cursor.getString(2));
							object.elevation = Double.parseDouble(cursor.getString(3));
							object.gpsTime = Long.parseLong(cursor.getString(4));
							objects.add(object);  
						} while (cursor.moveToNext());
					}
				}
			} finally {
				if(cursor!=null) cursor.close();
			}
		} catch (SQLException e) {
		} finally {
			if(db!=null) {
				db.close();
				db = null;
			}
		}
			
		return objects;
	}

	@Override
	public int getCount() {
		int count = 0;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try{
				cursor = db.rawQuery("select count(0) from "+TABLENAME, null);
				if(cursor!=null) {
					if(cursor.moveToFirst()){
						count = cursor.getInt(0);
					}
				}
			} finally {
				if(cursor!=null) cursor.close();
			}
		} catch (SQLException e) {
		} finally {
			if(db!=null) {
				db.close();
				db = null;
			}
		}

		return count;
	}

	@Override
	protected String getCreateTableQuery() {
		return SQL_CREATE_TABLE;
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

}
