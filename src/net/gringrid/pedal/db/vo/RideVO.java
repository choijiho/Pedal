package net.gringrid.pedal.db.vo;

import android.text.TextUtils;

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
	public static final String STRAVA_ID	= "stravaId";

	/**
	 * DB field
	 */
	public int primaryKey;
	public String name;
	public long startTime;
	public long endTime;
	public String ridingTime;
	public String distance;
	public String avgSpeed;
	public String maxSpeed;
	public String altitude;
	
	public String stravaId;
	
	/**
	 * Display Field
	 */
	public String stravaStatus;
	
	public boolean isShowDetail = false;

	public boolean isValidData(){
		if ( TextUtils.isEmpty(name) || name.equals("null") ){
			return false;
		}
		if ( TextUtils.isEmpty(ridingTime) || ridingTime.equals("null") ){
			return false;
		}
		if ( TextUtils.isEmpty(distance) || distance.equals("null") ){
			return false;
		}
		if ( TextUtils.isEmpty(avgSpeed) || avgSpeed.equals("null") ){
			return false;
		}
		if ( TextUtils.isEmpty(maxSpeed) || maxSpeed.equals("null") ){
			return false;
		}
		if ( TextUtils.isEmpty(altitude) || altitude.equals("null") ){
			return false;
		}
		if (startTime == 0 || endTime == 0){
			return false;
		}
		return true;
	}
}
