package net.gringrid.pedal.db.vo;

public class RideVO {

	public static final String PRIMARY_KEY 	= "primaryKey";
	public static final String NAME 		= "name";
	public static final String START_TIME 	= "startTime";
	public static final String END_TIME 	= "endTime";
	public static final String RIDING_TIME	= "ridingTime";
	public static final String DISTANCE		= "distance";
	public static final String AVG_SPEED	= "avgSpeed";
	public static final String MAX_SPEED	= "maxSpeed";
	public static final String ALTITUDE		= "altitude";
	
	public int primaryKey;

	public String name;
	public long startTime;
	public long endTime;

	public String ridingTime;
	public String distance;
	public String avgSpeed;
	public String maxSpeed;
	public String altitude;
	
	public boolean isShowDetail = false;
	
}
