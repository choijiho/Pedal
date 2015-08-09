package net.gringrid.pedal.db;

import java.util.LinkedList;
import java.util.List;

import net.gringrid.pedal.db.vo.GpsLogVO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public class GpsLogDao extends AbstractMasterDao<GpsLogVO>{
	public static final String TABLENAME = "gps_log";

	private static GpsLogDao instance;

	private static final String SQL_CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLENAME +
					"(primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					GpsLogVO.PARENT_ID	+ " INTEGER NOT NULL 	, " +
					GpsLogVO.LATITUDE	+ " TEXT 				, " +
					GpsLogVO.LONGITUDE	+ " TEXT 				, " +
					GpsLogVO.ELEVATION	+ " TEXT 				, " +
					GpsLogVO.GPS_TIME	+ " TEXT				 )";

	private static final String SQL_INSERT =
			String.format("INSERT INTO %s(%s, %s,%s,%s,%s) VALUES(?,?,?,?,?)",
					TABLENAME,
					GpsLogVO.PARENT_ID,
					GpsLogVO.LATITUDE,
					GpsLogVO.LONGITUDE,
					GpsLogVO.ELEVATION,
					GpsLogVO.GPS_TIME);

    public static GpsLogDao getInstance(DBHelper helper){
        if(instance == null) {
        	synchronized(GpsLogVO.class) {
	            instance = new GpsLogDao();
        	}
        }
        
       	instance.setDbHelper(helper);
       	if ( !instance.existsTable() ){
      		instance.createTable();
       	}

        return instance;
    }
    
	@Override
	public long insert(GpsLogVO object) {
		long result = 0L;
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();
		
			stmt = db.compileStatement(SQL_INSERT);

			stmt.bindString(1, String.valueOf(object.parentId));
			stmt.bindString(2, String.format("%.7f", object.latitude));
			stmt.bindString(3, String.format("%.7f",  object.longitude));
			stmt.bindString(4, String.format("%.1f", object.elevation));
			stmt.bindString(5, String.valueOf(object.gpsTime));

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
	public long[] insert(List<GpsLogVO> objects) {
		long results[] = new long[objects.size()];
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			stmt = db.compileStatement(SQL_INSERT);

			for(int i=0; i<objects.size(); i++)
			{
				GpsLogVO object = objects.get(i);
				stmt.bindString(1, String.valueOf(object.parentId));
				stmt.bindString(2, String.format("%.7f", object.latitude));
				stmt.bindString(3, String.format("%.7f",  object.longitude));
				stmt.bindString(4, String.format("%.1f", object.elevation));
				stmt.bindString(5, String.valueOf(object.gpsTime));;
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

			result = db.delete(TABLENAME, GpsLogVO.PRIMARY_KEY+"="+Integer.toString(id), null);

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
	public int update(GpsLogVO object) {
		int result = 0;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			ContentValues values = new ContentValues();
			values.put(GpsLogVO.LATITUDE, object.latitude);
			values.put(GpsLogVO.LONGITUDE, object.longitude);
			values.put(GpsLogVO.ELEVATION, object.elevation);
			values.put(GpsLogVO.GPS_TIME, object.gpsTime);
			result = db.update(TABLENAME,
									values,
									GpsLogVO.PRIMARY_KEY+"="+object.primaryKey,
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
	public GpsLogVO find(int id) {
		GpsLogVO object = null;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try{
				cursor = db.query(true, TABLENAME, null,
					GpsLogVO.PRIMARY_KEY+"="+Integer.toString(id), null, null, null, null, null);
				if (cursor != null) {
					if(cursor.moveToFirst()) {
						object = new GpsLogVO();
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

	public List<GpsLogVO> findWithParentId(int parentId) {
		List<GpsLogVO> objects = new LinkedList<GpsLogVO>();
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try {
				cursor = db.query(true, TABLENAME, null,
					GpsLogVO.PARENT_ID+"="+Integer.toString(parentId), null, null, null, null, null);
				if(cursor!=null) {
					if(cursor.moveToFirst()) {
						do{
							GpsLogVO object = new GpsLogVO();
							object.primaryKey = cursor.getInt(1); 
							object.latitude = Double.parseDouble(cursor.getString(2));
							object.longitude = Double.parseDouble(cursor.getString(3));
							object.elevation = Double.parseDouble(cursor.getString(4));
							object.gpsTime = Long.parseLong(cursor.getString(5));
							objects.add(object);  

//							Log.d("jiho", "cursor.getString(1) : "+cursor.getString(1));
//							Log.d("jiho", "cursor.getString(2) : "+cursor.getString(2));
//							Log.d("jiho", "cursor.getString(3) : "+cursor.getString(3));
//							Log.d("jiho", "cursor.getString(4) : "+cursor.getString(4));
//							Log.d("jiho", "cursor.getString(5) : "+cursor.getString(5));
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
	public List<GpsLogVO> findAll() {
		List<GpsLogVO> objects = new LinkedList<GpsLogVO>();
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try {
				cursor = db.query(TABLENAME, null, null, null, null, null, GpsLogVO.GPS_TIME+ " ASC");
				if(cursor!=null) {
					if(cursor.moveToFirst()) {
						do{
							GpsLogVO object = new GpsLogVO();
							object.primaryKey = cursor.getInt(1); 
							object.latitude = Double.parseDouble(cursor.getString(2));
							object.longitude = Double.parseDouble(cursor.getString(3));
							object.elevation = Double.parseDouble(cursor.getString(4));
							object.gpsTime = Long.parseLong(cursor.getString(5));
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
