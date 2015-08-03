package net.gringrid.pedal.db.vo;

public class GpsLogVO {

	public static final String PRIMARY_KEY	= "primaryKey";
	public static final String PARENT_ID	= "parentId";
	public static final String LATITUDE 	= "latitude";
	public static final String LONGITUDE 	= "longitude";
	public static final String ELEVATION 	= "elevation";
	public static final String GPS_TIME 	= "gpsTime";
	
	public int primaryKey;
	public long parentId;
	public double latitude;
	public double longitude;
	public double elevation;
	public long gpsTime;
}
