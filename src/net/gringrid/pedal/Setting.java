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
	}
}
