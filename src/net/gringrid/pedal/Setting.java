package net.gringrid.pedal;

import java.lang.Character.UnicodeBlock;

import net.gringrid.pedal.db.vo.DisplayVO;
import android.content.Context;
import android.util.Log;
import android.widget.SeekBar;

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

	public static final String SHARED_KEY_DISPLAY_ITEM_NAME = "display_item_name";
	public static final String SHARED_KEY_DISPLAY_MIN_INDEX = "display_min_index";
	public static final String SHARED_KEY_DISPLAY_MAX_INDEX = "display_max_index";
	public static final String SHARED_KEY_DISPLAY_TITLE_FONT_SIZE = "display_title_font_size";
	public static final String SHARED_KEY_DISPLAY_ITEM_FONT_SIZE = "display_item_font_size";
	public static final String SHARED_KEY_DISPLAY_UNIT_FONT_SIZE = "display_unit_font_size";
	

	public static final String SHARED_KEY_GEAR_TIRES_INDEX = "gear_tires_index";
	public static final String SHARED_KEY_GEAR_CHAINRING = "gear_chainring";
	public static final String SHARED_KEY_GEAR_SPROCKET = "gear_sprocket";
	public static final String SHARED_KEY_GEAR_CADENCE = "gear_cadence";
	
	private final int DEFAULT_TITLE_FONT_SIZE = 13;
	private final int DEFAULT_ITEM_FONT_SIZE = 20;
	private final int DEFAULT_UNIT_FONT_SIZE = 11;
	
//	public static final String SHARED_KEY_DISPLAY_CUR_SPEED_MIN		= "display_cur_speed_min";
//	public static final String SHARED_KEY_DISPLAY_CUR_SPEED_MAX 	= "display_cur_speed_max";
//	public static final String SHARED_KEY_DISPLAY_AVG_SPEED_MIN 	= "display_avg_speed_min";
//	public static final String SHARED_KEY_DISPLAY_AVG_SPEED_MAX 	= "display_avg_speed_max";
//	public static final String SHARED_KEY_DISPLAY_MAX_SPEED_MIN 	= "display_max_speed_min";
//	public static final String SHARED_KEY_DISPLAY_MAX_SPEED_MAX 	= "display_max_speed_max";
//	public static final String SHARED_KEY_DISPLAY_ALTITUDE_MIN 		= "display_altitude_min";
//	public static final String SHARED_KEY_DISPLAY_ALTITUDE_MAX 		= "display_altitude_max";
//	public static final String SHARED_KEY_DISPLAY_DISTANCE_MIN 		= "display_distance_min";
//	public static final String SHARED_KEY_DISPLAY_DISTANCE_MAX 		= "display_distance_max";
//	public static final String SHARED_KEY_DISPLAY_TRAVEL_TIME_MIN 	= "display_travel_time_min";
//	public static final String SHARED_KEY_DISPLAY_TRAVEL_TIME_MAX 	= "display_travel_time_max";
//	public static final String SHARED_KEY_DISPLAY_RIDING_TIME_MIN 	= "display_riding_time_min";
//	public static final String SHARED_KEY_DISPLAY_RIDING_TIME_MAX 	= "display_riding_time_max";
//	
//	public static final String SHARED_KEY_DISPLAY_PRESENT_TIME_MIN 	= "display_present_time_min";
//	public static final String SHARED_KEY_DISPLAY_PRESENT_TIME_MAX 	= "display_present_time_max";
//	public static final String SHARED_KEY_DISPLAY_DATE_MIN 			= "display_date_min";
//	public static final String SHARED_KEY_DISPLAY_DATE_MAX 			= "display_date_max";
//	public static final String SHARED_KEY_DISPLAY_BATTERY_MIN 		= "display_battery_min";
//	public static final String SHARED_KEY_DISPLAY_BATTERY_MAX 		= "display_battery_max";
	
	String CUR_SPEED = "cur_speed";
	String AVG_SPEED = "avg_speed";
	String MAX_SPEED = "max_speed";
	String ALTITUDE = "altitude";
	String DISTANCE = "distance";
	String TRAVEL_TIME = "travel_time";
	String RIDING_TIME = "riding_time";
	String PRESENT_TIME = "present_time";
	String DATE = "date";
	String BATTERY = "battery";
	
	
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
		
		String[] displayList = mContext.getResources().getStringArray(R.array.riding_infomation_list);
		
		for ( String item : displayList ){
			DisplayVO vo = new DisplayVO();
			vo.itemName = item;
			
			if ( item.equals(CUR_SPEED) ){
				vo.minIndex = 8;
				vo.maxIndex = 36;
			}else if ( item.equals(AVG_SPEED) ){
				vo.minIndex = 13;
				vo.maxIndex = 23;
			}else if ( item.equals(ALTITUDE) ){
				vo.minIndex = 29;
				vo.maxIndex = 39;
			}else if ( item.equals(DISTANCE) ){
				vo.minIndex = 40;
				vo.maxIndex = 52;
			}else if ( item.equals(TRAVEL_TIME) ){
				vo.minIndex = 45;
				vo.maxIndex = 55;
			}else if ( item.equals(TRAVEL_TIME) ){
				vo.minIndex = 45;
				vo.maxIndex = 55;
			}else if ( item.equals(DATE) ){
				vo.minIndex = 0;
				vo.maxIndex = 1;
			}else if ( item.equals(PRESENT_TIME) ){
				vo.minIndex = 2;
				vo.maxIndex = 5;
			}else if ( item.equals(BATTERY) ){
				vo.minIndex = 6;
				vo.maxIndex = 7;
			}

			// TODO 항목별 default
			vo.titleFontSize = 13;
			vo.itemFontSize = 20;
			vo.unitFontSize = 11;
		
			setDisplayInfo(vo);
			vo.debug();
			
		}
	}
	
	public String displayInfoTostring(){
		String result = "";
//		result += "SHARED_KEY_DISPLAY_CUR_SPEED_MIN : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_CUR_SPEED_MIN);
//		result += "SHARED_KEY_DISPLAY_CUR_SPEED_MAX : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_CUR_SPEED_MAX);
//		result += "SHARED_KEY_DISPLAY_AVG_SPEED_MIN : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_AVG_SPEED_MIN);
//		result += "SHARED_KEY_DISPLAY_AVG_SPEED_MAX : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_AVG_SPEED_MAX);
//		result += "SHARED_KEY_DISPLAY_ALTITUDE_MIN : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_ALTITUDE_MIN);
//		result += "SHARED_KEY_DISPLAY_ALTITUDE_MAX : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_ALTITUDE_MAX);
//		result += "SHARED_KEY_DISPLAY_DISTANCE_MIN : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_DISTANCE_MIN);
//		result += "SHARED_KEY_DISPLAY_DISTANCE_MAX : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_DISTANCE_MAX);
//		result += "SHARED_KEY_DISPLAY_TRAVEL_TIME_MIN : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_TRAVEL_TIME_MIN);
//		result += "SHARED_KEY_DISPLAY_TRAVEL_TIME_MAX : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_TRAVEL_TIME_MAX);
//		result += "SHARED_KEY_DISPLAY_DATE_MIN : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_DATE_MIN);
//		result += "SHARED_KEY_DISPLAY_DATE_MAX : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_DATE_MAX);
//		result += "SHARED_KEY_DISPLAY_PRESENT_TIME_MIN : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_PRESENT_TIME_MIN);
//		result += "SHARED_KEY_DISPLAY_PRESENT_TIME_MAX : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_PRESENT_TIME_MAX);
//		result += "SHARED_KEY_DISPLAY_BATTERY_MIN : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_BATTERY_MIN);
//		result += "SHARED_KEY_DISPLAY_BATTERY_MAX : "+SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_BATTERY_MAX);
		return result;
	}
	
	public void setDisplayInfo(DisplayVO item){
		String suffix = "_"+item.itemName;
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_ITEM_NAME+suffix, item.itemName);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_MIN_INDEX+suffix, item.minIndex);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_MAX_INDEX+suffix, item.maxIndex);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_TITLE_FONT_SIZE+suffix, item.titleFontSize);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_ITEM_FONT_SIZE+suffix, item.itemFontSize);
		SharedData.getInstance(mContext).setGlobalData(SHARED_KEY_DISPLAY_UNIT_FONT_SIZE+suffix, item.unitFontSize);
		SharedData.getInstance(mContext).commit();
	}
	
	public DisplayVO getDisplayInfo(String itemName){
		String suffix = "_"+itemName;
		DisplayVO vo = new DisplayVO();

		int cellWidth = DisplayInfoManager.getInstance(mContext).getCellWidth();
		int cellHeight = DisplayInfoManager.getInstance(mContext).getCellHeight();
		
		vo.itemName = SharedData.getInstance(mContext).getGlobalDataString(SHARED_KEY_DISPLAY_ITEM_NAME+suffix);
		vo.minIndex = SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_MIN_INDEX+suffix);
		vo.maxIndex = SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_MAX_INDEX+suffix);
		vo.titleFontSize = SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_TITLE_FONT_SIZE+suffix);
		vo.itemFontSize = SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_ITEM_FONT_SIZE+suffix);
		vo.unitFontSize = SharedData.getInstance(mContext).getGlobalDataInt(SHARED_KEY_DISPLAY_UNIT_FONT_SIZE+suffix);
		vo.left = vo.minIndex % DisplayInfoManager.CELL_COLS * cellWidth;
		vo.top = vo.minIndex / DisplayInfoManager.CELL_ROWS * cellHeight;
		vo.right = (vo.maxIndex % DisplayInfoManager.CELL_COLS + 1) * cellWidth;
		vo.bottom = (vo.maxIndex / DisplayInfoManager.CELL_ROWS + 1) * cellHeight;
		return vo;
	}
	
	public void debugDisplayInfo(){
		DisplayVO vo = null;
		String[] list = mContext.getResources().getStringArray(R.array.riding_infomation_list);
		for ( String item : list ){
			vo = getDisplayInfo(item);
//			Log.d("jiho", vo.itemName+" : ("+vo.minIndex+", "+vo.maxIndex+")");
//			Log.d("jiho", vo.itemName+" : ("+vo.left+", "+vo.top+", "+vo.right+", "+vo.bottom+")");
//			Log.d("jiho", vo.itemName+" : ("+vo.titleFontSize+", "+vo.itemFontSize+", "+vo.right+", "+vo.unitFontSize+")");
		}
	}
	
	public void clearDisplayInfo(DisplayVO item){
		item.minIndex = 0;
		item.maxIndex = 0;
		item.titleFontSize = DEFAULT_TITLE_FONT_SIZE;
		item.itemFontSize = DEFAULT_ITEM_FONT_SIZE;
		item.unitFontSize = DEFAULT_UNIT_FONT_SIZE;
		
		setDisplayInfo(item);
	}
}




