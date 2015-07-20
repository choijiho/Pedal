package net.gringrid.pedal.db.vo;

public class GpsLogMaster {

	public static final String PRIMARY_KEY	= "primaryKey";
	public static final String LATITUDE 	= "latitude";
	public static final String LONGITUDE 	= "longitude";
	public static final String ELEVATION 	= "elevation";
	public static final String GPS_TIME 	= "gpsTime";
	
	public int primaryKey;
	public double latitude;
	public double longitude;
	public double elevation;
	public long gpsTime;
}
