package net.gringrid.pedal.db;

import java.util.LinkedList;
import java.util.List;

import net.gringrid.pedal.db.vo.RideMaster;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


public class RideDao extends AbstractMasterDao<RideMaster>{
	public static final String TABLENAME = "ride";

	private static RideDao instance;
	
	private static final String SQL_CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + TABLENAME +
					"(primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					RideMaster.NAME + " TEXT , " +
					RideMaster.START_TIME + " TEXT NOT NULL UNIQUE)";

	private static final String SQL_INSERT =
			String.format("INSERT INTO %s(%s,%s) VALUES(?,?)",
					TABLENAME,
					RideMaster.NAME,
					RideMaster.START_TIME);

    public static RideDao getInstance(DBHelper helper){
        if(instance == null)
        {
        	synchronized(RideMaster.class)
        	{
	            instance = new RideDao();
        	}
        }
        instance.setDbHelper(helper);

        return instance;
    }
    
	@Override
	public long insert(RideMaster object) {
		long result = 0L;
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();
		
			stmt = db.compileStatement(SQL_INSERT);
			
			stmt.bindString(1, object.name);
			stmt.bindString(2, object.startTime);
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
	public long[] insert(List<RideMaster> objects) {
		long results[] = new long[objects.size()];
		SQLiteDatabase db = null;
		SQLiteStatement stmt = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			stmt = db.compileStatement(SQL_INSERT);

			for(int i=0; i<objects.size(); i++)
			{
				RideMaster object = objects.get(i);
				stmt.bindString(1, object.name);
				stmt.bindString(2, object.startTime);
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

			result = db.delete(TABLENAME, RideMaster.PRIMARY_KEY+"="+Integer.toString(id), null);

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
	public int update(RideMaster object) {
		int result = 0;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getWritableDatabase();
			db.beginTransaction();

			ContentValues values = new ContentValues();
			values.put(RideMaster.NAME, object.name);
			values.put(RideMaster.START_TIME, object.startTime);
			result = db.update(TABLENAME,
									values,
									RideMaster.PRIMARY_KEY+"="+object.primaryKey,
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
	public RideMaster find(int id) {
		RideMaster object = null;
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try{
				cursor = db.query(true, TABLENAME, null,
					RideMaster.PRIMARY_KEY+"="+Integer.toString(id), null, null, null, null, null);
				if (cursor != null) {
					if(cursor.moveToFirst()) {
						object = new RideMaster();
						object.primaryKey = cursor.getInt(0); 
						object.name = cursor.getString(1);
						object.startTime = cursor.getString(2);
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
	public List<RideMaster> findAll() {
		List<RideMaster> objects = new LinkedList<RideMaster>();
		SQLiteDatabase db = null;

		try {
			db = getDbHelper().getReadableDatabase();

			Cursor cursor = null;
			try {
				cursor = db.query(TABLENAME, null, null, null, null, null, RideMaster.START_TIME+ " ASC");
				if(cursor!=null) {
					if(cursor.moveToFirst()) {
						do{
							RideMaster object = new RideMaster();
							object.primaryKey = cursor.getInt(0); 
							object.name = cursor.getString(1);
							object.startTime = cursor.getString(2);
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
