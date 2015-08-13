package net.gringrid.pedal.db.vo;

public class RideVO {

	public static final String PRIMARY_KEY 	= "primaryKey";
	public static final String NAME 		= "name";
	public static final String START_TIME 	= "startTime";
	
	public int primaryKey;
	public String name;
	public long startTime;

	public String detailTime;
	public String detailDistance;
	public String detailAvgSpeed;
	public String detailMaxSpeed;
	public String detailAltitude;
	
	public boolean isShowDetail = false;
	
}
