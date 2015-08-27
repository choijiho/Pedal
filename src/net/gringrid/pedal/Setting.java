package net.gringrid.pedal;

import android.content.Context;

public class Setting {

	Context mContext;
	
	public boolean IS_ENABLE_CURRENT_SPEED = true;
	public boolean IS_ENABLE_AVERAGE_SPEED = true;
	public boolean IS_ENABLE_MUSIC = true;
	public boolean IS_ENABLE_SAVE_GPS = true;

	public static final String SHARED_KEY_STATE = "state"; 
	public static final String SHARED_KEY_INITIAL_SETTING = "initial_setting"; 
	public static final String SHARED_KEY_CURRENT_SPEED = "current_speed";
	public static final String SHARED_KEY_AVERAGE_SPEED = "average_speed";
	public static final String SHARED_KEY_MUSIC = "music";
	public static final String SHARED_KEY_SAVE_GPS = "save_gps";
	public static final String SHARED_KEY_STRAVA_USER_ID = "strava_user_id";
	public static final String SHARED_KEY_STRAVA_ACCESS_TOKEN = "strava_access_token";
	public static final String SHARED_KEY_STRAVA_EMAIL = "strava_email";
	public static final String SHARED_KEY_RIDING_ID = "riding_id";

	public static final String SHARED_KEY_DISPLAY_CUR_SPEED_MIN		= "display_cur_speed_min";
	public static final String SHARED_KEY_DISPLAY_CUR_SPEED_MAX 	= "display_cur_speed_max";
	public static final String SHARED_KEY_DISPLAY_AVG_SPEED_MIN 	= "display_avg_speed_min";
	public static final String SHARED_KEY_DISPLAY_AVG_SPEED_MAX 	= "display_avg_speed_max";
	public static final String SHARED_KEY_DISPLAY_MAX_SPEED_MIN 	= "display_max_speed_min";
	public static final String SHARED_KEY_DISPLAY_MAX_SPEED_MAX 	= "display_max_speed_max";
	public static final String SHARED_KEY_DISPLAY_ALTITUDE_MIN 		= "display_altitude_min";
	public static final String SHARED_KEY_DISPLAY_ALTITUDE_MAX 		= "display_altitude_max";
	public static final String SHARED_KEY_DISPLAY_DISTANCE_MIN 		= "display_distance_min";
	public static final String SHARED_KEY_DISPLAY_DISTANCE_MAX 		= "display_distance_max";
	public static final String SHARED_KEY_DISPLAY_TRAVEL_TIME_MIN 	= "display_travel_time_min";
	public static final String SHARED_KEY_DISPLAY_TRAVEL_TIME_MAX 	= "display_travel_time_max";
	public static final String SHARED_KEY_DISPLAY_RIDING_TIME_MIN 	= "display_riding_time_min";
	public static final String SHARED_KEY_DISPLAY_RIDING_TIME_MAX 	= "display_riding_time_max";
	
	public static final String SHARED_KEY_DISPLAY_PRESENT_TIME_MIN 	= "display_present_time_min";
	public static final String SHARED_KEY_DISPLAY_PRESENT_TIME_MAX 	= "display_present_time_max";
	public static final String SHARED_KEY_DISPLAY_DATE_MIN 			= "display_date_min";
	public static final String SHARED_KEY_DISPLAY_DATE_MAX 			= "display_date_max";
	public static final String SHARED_KEY_DISPLAY_BATTERY_MIN 		= "display_battery_min";
	public static final String SHARED_KEY_DISPLAY_BATTERY_MAX 		= "display_battery_max";
	
	public Setting(Context context) {
		mContext = context;
		loadSetting();
	}

	private void loadSetting(){
		IS_ENABLE_CURRENT_SPEED = SharedData.getInstance(mContext).getGlobalDataBoolean(SHARED_KEY_CURRENT_SPEED);
		IS_ENABLE_AVERAGE_SPEED = SharedData.getInstance(mContext).getGlobalDataBoolean(SHARED_KEY_AVERAGE_SPEED);
		IS_ENABLE_MUSIC = SharedData.getInstance(mContext).getGlobalDataBoolean(SHARED_KEY_MUSIC);
		IS_ENABLE_SAVE_GPS = SharedData.getInstance(mContext).getGlobalDataBoolean(SHARED_KEY_SAVE_GPS);
	}

	public void initSetting(){
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_INITIAL_SETTING, true);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_CURRENT_SPEED, true);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_AVERAGE_SPEED, true);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_MUSIC, true);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_SAVE_GPS, true);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_RIDING_ID, Long.MAX_VALUE);
		SharedData.getInstance(mContext).commit();

		displayInitSetting();
	}
	
	public void displayInitSetting(){
		// RIDING INFO DISPLAY DEFAULT
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_CUR_SPEED_MIN, 8);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_CUR_SPEED_MAX, 36);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_AVG_SPEED_MIN, 13);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_AVG_SPEED_MAX, 23);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_ALTITUDE_MIN, 29);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_ALTITUDE_MAX, 39);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_DISTANCE_MIN, 40);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_DISTANCE_MAX, 52);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_TRAVEL_TIME_MIN, 45);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_TRAVEL_TIME_MAX, 55);
		
		// SYSTEM INFO DISPLAY DEFAULT
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_DATE_MIN, 0);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_DATE_MAX, 1);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_PRESENT_TIME_MIN, 2);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_PRESENT_TIME_MAX, 5);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_BATTERY_MIN, 6);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_BATTERY_MAX, 7);
		SharedData.getInstance(mContext).commit();
		
	}
}
