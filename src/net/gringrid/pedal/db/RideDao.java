package net.gringrid.pedal.db;

import java.util.LinkedList;
import java.util.List;

import net.gringrid.pedal.db.vo.RideVO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public class RideDao extends AbstractMasterDao<RideVO>{
	public static final String TABLENAME = "ride";

	private static RideDao instance;
	
	private static final String SQL_CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLENAME +
					"(primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					RideVO.NAME 		+ " TEXT , " +
					RideVO.START_TIME 	+ " TEXT NOT NULL UNIQUE , " +
					RideVO.END_TIME 	+ " TEXT , " +
					RideVO.RIDING_TIME 	+ " TEXT , " +
					RideVO.DISTANCE 	+ " TEXT , " +
					RideVO.AVG_SPEED 	+ " TEXT , " +
					RideVO.MAX_SPEED 	+ " TEXT , " +
					RideVO.ALTITUDE 	+ " TEXT , " + 
					RideVO.STRAVA_ID	+ " TEXT " +
					")";

	private static final String SQL_INSERT =
			String.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES(?,?,?,?,?,?,?,?,?)",
					TABLENAME,
					RideVO.NAME,
					RideVO.START_TIME,
					RideVO.END_TIME,
					RideVO.RIDING_TIME,
					RideVO.DISTANCE,
					RideVO.AVG_SPEED,
					RideVO.MAX_SPEED,
					RideVO.ALTITUDE,
					RideVO.STRAVA_ID
					);

    public static RideDao getInstance(DBHelper helper){
        if(instance == null) {
        	synchronized(RideVO.class) {
	            instance = new RideDao();
        	}

        }
       	instance.setDbHelper(helper);
       	if ( !instance.existsTable() ){
       		instance.createTable();
       	}

        return instance;
    }
    
	@Override
	public long insert(RideVO object) {
		long result = 0L;
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();
		
			stmt = db.compileStatement(SQL_INSERT);
			
			stmt.bindString(1, object.name);
			stmt.bindString(2, String.valueOf(object.startTime));
			stmt.bindString(3, String.valueOf(object.endTime));
			stmt.bindString(4, String.valueOf(object.ridingTime));
			stmt.bindString(5, String.valueOf(object.distance));
			stmt.bindString(6, String.valueOf(object.avgSpeed));
			stmt.bindString(7, String.valueOf(object.maxSpeed));
			stmt.bindString(8, String.valueOf(object.altitude));
			stmt.bindString(9, String.valueOf(object.stravaId));
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
	public long[] insert(List<RideVO> objects) {
		long results[] = new long[objects.size()];
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			stmt = db.compileStatement(SQL_INSERT);

			for(int i=0; i<objects.size(); i++)
			{
				RideVO object = objects.get(i);
				stmt.bindString(1, object.name);
				stmt.bindString(2, String.valueOf(object.startTime));
				stmt.bindString(3, String.valueOf(object.endTime));
				stmt.bindString(4, String.valueOf(object.ridingTime));
				stmt.bindString(5, String.valueOf(object.distance));
				stmt.bindString(6, String.valueOf(object.avgSpeed));
				stmt.bindString(7, String.valueOf(object.maxSpeed));
				stmt.bindString(8, String.valueOf(object.altitude));
				stmt.bindString(9, String.valueOf(object.stravaId));
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

			result = db.delete(TABLENAME, RideVO.PRIMARY_KEY+"="+Integer.toString(id), null);
			Log.d("jiho", "delete result : "+result);

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
	public int update(RideVO object) {
		int result = 0;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			ContentValues values = new ContentValues();
			values.put(RideVO.NAME, object.name);
			values.put(RideVO.START_TIME, object.startTime);
			values.put(RideVO.END_TIME, object.endTime);
			values.put(RideVO.RIDING_TIME, object.ridingTime);
			values.put(RideVO.DISTANCE, object.distance);
			values.put(RideVO.AVG_SPEED, object.avgSpeed);
			values.put(RideVO.MAX_SPEED, object.maxSpeed);
			values.put(RideVO.ALTITUDE, object.altitude);
			values.put(RideVO.STRAVA_ID, object.stravaId);
			
			result = db.update(TABLENAME,
									values,
									RideVO.PRIMARY_KEY+"="+object.primaryKey,
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
	public RideVO find(int id) {
		RideVO object = null;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try{
				cursor = db.query(true, TABLENAME, null,
					RideVO.PRIMARY_KEY+"="+Integer.toString(id), null, null, null, null, null);
				if (cursor != null) {
					if(cursor.moveToFirst()) {
						object = new RideVO();
						object.primaryKey = cursor.getInt(0); 
						object.name = cursor.getString(1);
						object.startTime = Long.parseLong(cursor.getString(2));
						object.endTime = Long.parseLong(cursor.getString(3));
						object.ridingTime = cursor.getString(4);
						object.distance = cursor.getString(5);
						object.avgSpeed = cursor.getString(6);
						object.maxSpeed = cursor.getString(7);
						object.altitude = cursor.getString(8);
						object.stravaId = cursor.getString(9);
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
	public List<RideVO> findAll() {
		List<RideVO> objects = new LinkedList<RideVO>();
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try {
				cursor = db.query(TABLENAME, null, null, null, null, null, RideVO.START_TIME+ " ASC");
				if(cursor!=null) {
					if(cursor.moveToFirst()) {
						do{
							RideVO object = new RideVO();
							object.primaryKey = cursor.getInt(0); 
							object.name = cursor.getString(1);
							object.startTime = Long.parseLong(cursor.getString(2));
							object.endTime = Long.parseLong(cursor.getString(3));
							object.ridingTime = cursor.getString(4);
							object.distance = cursor.getString(5);
							object.avgSpeed = cursor.getString(6);
							object.maxSpeed = cursor.getString(7);
							object.altitude = cursor.getString(8);
							object.stravaId = cursor.getString(9);
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
